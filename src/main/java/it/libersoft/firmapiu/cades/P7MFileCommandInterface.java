/**
 * 
 */
package it.libersoft.firmapiu.cades;

import it.libersoft.firmapiu.CRToken;
import it.libersoft.firmapiu.Data;
import it.libersoft.firmapiu.DataFilePath;
import it.libersoft.firmapiu.Argument;
import it.libersoft.firmapiu.GenericArgument;
import it.libersoft.firmapiu.MasterFactoryBuilder;
import it.libersoft.firmapiu.crtoken.PKCS11Token;
import it.libersoft.firmapiu.exception.FirmapiuException;
import static it.libersoft.firmapiu.consts.ArgumentConsts.*;
import static it.libersoft.firmapiu.exception.FirmapiuException.*;
import static it.libersoft.firmapiu.consts.FactoryPropConsts.*;
import static it.libersoft.firmapiu.consts.FactoryConsts.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;

import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableFile;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSTypedData;


/**
 * La classe offre operazioni per firmare e la verificare  un insieme di files firmati elettronicamente (.p7m) 
 * secondo la DELIBERAZIONE ministeriale del N . 45 DEL 21 MAGGIO 2009.
 * 
 * @author dellanna
 *
 */
final class P7MFileCommandInterface implements CadesBESCommandInterface {

	// inizializza i resourcebundle per il recupero dei messaggi lanciati dalla
	// classe
	private static final ResourceBundle RB = ResourceBundle.getBundle(
				"it.libersoft.firmapiu.lang.localefactory", Locale.getDefault());
	private static final ResourceBundle RB1 = ResourceBundle.getBundle(
			"it.libersoft.firmapiu.lang.locale", Locale.getDefault());
	
	//tipo di token utilizzato per le operazioni di firma
	private final String signTokenType;
	//tipo di token utilizzato per le operazioni di verifica
	private final String verifyTokenType;
	
	/**
	 * la classe non dovrebbe essere inizializzata se non attraverso la factory
	 * 
	 * @param signTokenType Il tipo di token utilizzato per le operazioni di firma dei files
	 * @param verifyTokenType Il tipo di token utilizzato per le operazioni di verifica  tecnica e legale 
	 * di files firmati elettronicamente in formato CADES-bes (attacched)  
	 * @see it.libersoft.firmapiu.consts.FactoryPropConsts
	 */
	protected P7MFileCommandInterface(String signTokenType,String verifyTokenType) {
		this.signTokenType=signTokenType;
		this.verifyTokenType=verifyTokenType;
	}

	/** 
	 * Firma un batch di file usando le credenziali del token crittografico associato
	 * 
	 * @param data contenete il riferimento ai percorsi dei file da firmare. I file presi in considerazioni devono essere delle "chiavi"
	 * in modo tale che il sistema non firmi lo stesso file più di una volta<br>
	 * I percorsi dei file da firmare devono essere percorsi assoluti.
	 * @param option Gli argomenti presi in considerazione in fase di esecuzione del comando<br>
	 * Un argomento obbligatorio è "tokenpin" in quanto per accedere alle credenziali presenti sul token per firmare il file bisogna passargli il pin
	 * @return Una map contenente l'esito dell'operazione di firma per ogni file passato come parametro
	 * 
	 * @see it.libersoft.firmapiu.CommandInterface#sign(it.libersoft.firmapiu.Data, it.libersoft.firmapiu.Argument)
	 * @see it.libersoft.firmapiu.consts.ArgumentConsts
	 */
	@Override
	public Map<String, ?> sign(Data<?> data, Argument<?, ?> option) throws IllegalArgumentException,FirmapiuException{
		//controllo di coerenza iniziale sugli argomenti
		DataFilePath dataFilePath = checkData(data);
		GenericArgument commandArgs = checkArgument(option);
	
		//controlla gli argomenti
		//TODO bisogna documentare con attenzione tutti gli argomenti che il comando può accettare se l'arg è obbligatorio e che valore può accettare

		//directory di output
		//recupera la directory di output. presso cui salvare i p7m, se non esiste la crea
		//Default: se l'argomento non è presente il p7m viene salvato nella stessa dir del file da firmare
		//DefauLt: se la directory non esiste e non è presente l'opzione CREATEOUTDIR 
		//O L'OPZIONE È FALSE NON CREA LA DIRECTORY e lancia un eccezione 
		File outDir=null;
		if(commandArgs.isArgument(OUTDIR)){
			outDir=getOutDir(commandArgs);
		}//fine if controllo argomento OUTDIR

		//pin
		//recupera il pin del token utilizzato
		//Obbligatorio: L'argomento non può essere omesso. Se l'argomento viene omesso, il token crittografico
		//non è in grado di recuperare le credenziali utilizzare per firmare l'insieme di file passati come parametro
		char[] tokenpin=null;
		if(commandArgs.isArgument(TOKENPIN)){
			tokenpin=(char[])commandArgs.getArgument(TOKENPIN);
		} else
			throw new FirmapiuException(CRT_TOKENPIN_ERROR);


		//prepara Map<String,Object> con i risultati delle operazioni effettuate sui file passati come parametro.
		Map<String,Object> result = new TreeMap<String,Object>();
		//recupera il dataset contenente i percorsi dei file da firmare
		Set<String> dataFilePathSet=dataFilePath.getDataSet();
		
		Iterator<String> dataPathItr=dataFilePathSet.iterator();
		CadesBESSigner signer=null;
		CRToken token=null;
		while(dataPathItr.hasNext()){
			File dataFileIn=new File(dataPathItr.next());
			FileOutputStream fileOutStream=null;
			try {
				//controlla che il file di input esiste
				if (!dataFileIn.exists())
					throw new FileNotFoundException(dataFileIn.getAbsolutePath()+" "+RB1.getString("filerror0"));
				
				//si prepara a creare il file di output: se sovrascrive un file esistente lancia un errore
				File dataFileOut;
				if(outDir==null)
					dataFileOut = new File(dataFileIn.getAbsolutePath()+".p7m");
				else
					dataFileOut = new File(outDir.getAbsolutePath()+"/"+dataFileIn.getName()+".p7m");
				if(dataFileOut.exists())
					throw new IOException("Cannot override file! : "+dataFileOut.getAbsolutePath());

				CMSTypedData cmsDataIn = new CMSProcessableFile(dataFileIn);
				
				//se non è già stata inizializzata, inizializza una sessione di login se il token usato per firmare è pkcs#11
				if(signer==null){
					//crea il token crittografico a seconda del tipo passato come parametro alla P7MFileCommandInterface
					token=MasterFactoryBuilder.getFactory(this.signTokenType).getToken(CRTSMARTCARD);
					//se il token è di tipo PKCS11Token, inizializza la sessione
					if(token instanceof PKCS11Token)
						((PKCS11Token)token).login(tokenpin);
					signer = new CadesBESSigner(token);
				}
				CMSSignedData signedData=signer.sign(cmsDataIn);				
				fileOutStream =new FileOutputStream(dataFileOut);
				fileOutStream.write(signedData.getEncoded());
				
				//se l'operazione è andata bene, genera il percorso del .p7m risultante del file passato come parametro
				result.put(dataFileIn.getAbsolutePath(),dataFileOut.getAbsolutePath());
			} catch (FileNotFoundException e) {
				String msg= FirmapiuException.getDefaultErrorCodeMessage(FILE_NOTFOUND);
				msg+=" : "+dataFileIn.getAbsolutePath();
				FirmapiuException fe1 =new FirmapiuException(FILE_NOTFOUND, msg, e);
				result.put(dataFileIn.getAbsolutePath(), fe1);
			} catch (IOException e) {
				FirmapiuException fe1 = null;
				if(e.getMessage().equals("Cannot override file!"))
					fe1 =new FirmapiuException(FILE_OVERRIDE_ERROR, e);
				else
					fe1 =new FirmapiuException(IO_DEFAULT_ERROR, e);
				result.put(dataFileIn.getAbsolutePath(), fe1);
			} catch (CMSException e){
				FirmapiuException fe1 =new FirmapiuException(SIGNER_CADESBES_ERROR, e);
				result.put(dataFileIn.getAbsolutePath(), fe1);
			}catch (FirmapiuException e) {
				//se il token è PKCS11token si slogga e rilancia l'eccezione firmapiuexception al chiamante
				if((token!=null)&&(token instanceof PKCS11Token)){
					((PKCS11Token)token).logout();
				}
				throw e;
			}finally{
				try {
					//cerca di chiudere le risorse utilizzate
					fileOutStream.flush();
					fileOutStream.close();
				} catch (Exception e) {}
			}//fine try-catch-finally
			
		}//fine while
		//se il token è PKCS11token si slogga e rilancia l'eccezione firmapiuexception al chiamante
		if((token!=null)&&(token instanceof PKCS11Token)){
			((PKCS11Token)token).logout();
		}
		
		//resetta il pin e restituisce i risulti delle operazioni effettuate sui file passati come parametro
		//java.util.Arrays.fill(pin, ' ');
		return result;
	}
	
	
	@Override
	public Map<?, ?> verify(Data<?> data, Argument<?, ?> option) throws FirmapiuException{
		//inizializza il token contenente i certificati di ROOT delle CA utilizzati 
		//per controllare l'affidabilità del certificato del firmatario
		
		// TODO Auto-generated method stub
		return null;
	}

	/** 
	 * Restituisce il contenuto originale di una serie di File passati come parametri
	 * 
	 * @param signedDataPath Il percorso dei file firmati nel formato p7m di cui si vuole recuperare il contenuto originale del file
	 * @param option Argomenti opzionali generici passati al comando
	 * @return Una map contenente l'esito dell'operazione di getContentSignedData per ogni file passato come parametro
	 * 
	 * @see it.libersoft.firmapiu.CommandInterface#getContentSignedData(it.libersoft.firmapiu.Data, it.libersoft.firmapiu.Argument)
	 */
	@Override
	public Map<String, ?> getContentSignedData(Data<?> signedDataPath,
			Argument<?, ?> option) throws FirmapiuException {
		//controllo di coerenza iniziale sugli argomenti
		DataFilePath signedDataFilePath = checkData(signedDataPath);
		GenericArgument commandArgs = checkArgument(option);

		//directory di output
		//recupera la directory di output. presso cui salvare il contenuto originale dei file contenuti nel p7m. Se non esiste la crea
		//Default: se l'argomento non è presente il contenuto originale del file viene salvato nella stessa dir del file da verificare
		//DefauLt: se la directory non esiste e non è presente l'opzione CREATEOUTDIR 
		//O L'OPZIONE È FALSE NON CREA LA DIRECTORY e lancia un eccezione 
		File outDir=null;
		if(commandArgs.isArgument(OUTDIR)){
			outDir=getOutDir(commandArgs);
		}//fine if controllo argomento OUTDIR

		//prepara Map<String,Object> con i risultati delle operazioni effettuate sui file passati come parametro.
		Map<String,Object> result = new TreeMap<String,Object>();
		//recupera il dataset contenente i percorsi dei file da firmare
		Set<String> dataFilePathSet=signedDataFilePath.getDataSet();
		
		Iterator<String> dataPathItr=dataFilePathSet.iterator();
		//per ogni file presente in signedDataFilePath controlla che sia un p7m che sia nel formato valido (attacched)
		//restituisce il contenuto originale del file
		while(dataPathItr.hasNext()){
			File dataFileIn=new File(dataPathItr.next());
			FileOutputStream fileOutStream=null;
			try {
				//controlla che il file termini con .p7m altrimenti lancia un errore
				String fileOutName=dataFileIn.getName();
				if(!fileOutName.endsWith(".p7m"))
					throw new FirmapiuException(CONTENT_CADESBES_NOTP7MFILE);
				
				//controlla che il file esista altrimenti lancia un errore
				if (!dataFileIn.exists())
					throw new FileNotFoundException(dataFileIn.getAbsolutePath()+" "+RB1.getString("filerror0"));
				
				//crea la busta crittografica CMSSignedData. Se non è attacched lancia un errore
				FileInputStream in = new FileInputStream(dataFileIn);
				byte[] b=new byte[in.available()];
				in.read(b);
				CMSSignedData signedData;
				try {
					signedData = new CMSSignedData(b);
				} catch (CMSException e) {
					throw new FirmapiuException(CONTENT_CADESBES_ENCODINGERROR_ATTACHED, e);
				}
				
				//recupera il contenuto originale del file p7m
				File dataFileOut;
				fileOutName=fileOutName.substring(0, fileOutName.length()-4);
				if(outDir==null)
					dataFileOut = new File(dataFileIn.getParent()+"/"+fileOutName);
				else
					dataFileOut = new File(outDir.getAbsolutePath()+"/"+fileOutName);
				//non si può sovrascrivere un file esistente
				if(dataFileOut.exists())
					throw new IOException("Cannot override file! : "+dataFileOut.getAbsolutePath());
				fileOutStream=new FileOutputStream(dataFileOut);
				try {
					signedData.getSignedContent().write(fileOutStream);
				} catch (CMSException e) {
					throw new FirmapiuException(CONTENT_CADESBES_DEFAULT_ERROR, e);
				}
				
				//se l'operazione è andata bene, genera il percorso del file risultante del file passato come parametro
				result.put(dataFileIn.getAbsolutePath(),dataFileOut.getAbsolutePath());
			} catch (FileNotFoundException e) {
				String msg= FirmapiuException.getDefaultErrorCodeMessage(FILE_NOTFOUND);
				msg+=" : "+dataFileIn.getAbsolutePath();
				FirmapiuException fe1 =new FirmapiuException(FILE_NOTFOUND, msg, e);
				result.put(dataFileIn.getAbsolutePath(), fe1);
			} catch (IOException e) {
				FirmapiuException fe1 = null;
				if(e.getMessage().equals("Cannot override file!"))
					fe1 =new FirmapiuException(FILE_OVERRIDE_ERROR, e);
				else
					fe1 =new FirmapiuException(IO_DEFAULT_ERROR, e);
				result.put(dataFileIn.getAbsolutePath(), fe1);
			} catch (SecurityException e){
				FirmapiuException fe1 =new FirmapiuException(FILE_FORBIDDEN, e);
				result.put(dataFileIn.getAbsolutePath(), fe1);
			} catch (FirmapiuException e) {
				result.put(dataFileIn.getAbsolutePath(), e);
			}finally{
				try {
					//cerca di chiudere le risorse utilizzate
					fileOutStream.flush();
					fileOutStream.close();
				} catch (Exception e) {}
			}//fine try-catch-finally
		}//fine while
		return result;
	}//fine metodo	
	
	//PROCEDURE PRIVATE
	//controllo coerenza data
	private static DataFilePath checkData(Data<?> data){
		DataFilePath dataFilePath;
		if( data==null || !(data instanceof DataFilePath))
			throw new IllegalArgumentException(RB.getString("factoryerror4")
					+ " : " + data.getClass().getCanonicalName());
		else 
			dataFilePath=(DataFilePath)data;
		return dataFilePath;
	}
	//controllo coerenza argomenti
	private static GenericArgument checkArgument(Argument<?, ?> option){
		GenericArgument commandArgs;
		if( option==null || !(option instanceof GenericArgument))
			throw new IllegalArgumentException(RB.getString("factoryerror4")
					+ " : " + option.getClass().getCanonicalName());
		else 
			commandArgs=(GenericArgument)option;
		return commandArgs;
	}
			
	//genera directory di output
	private static File getOutDir(GenericArgument commandArgs) throws FirmapiuException{
		//controlla che il percorso della directory sia assoluto altrimenti lancia un errore
		String outDirPath=(String)commandArgs.getArgument(OUTDIR);
		File outDir=new File(outDirPath);
		if(!outDir.isAbsolute()){
			String msg=FirmapiuException.getDefaultErrorCodeMessage(IS_NOT_ABS_PATH);
			msg+=" : "+outDirPath;
			throw new FirmapiuException(IS_NOT_ABS_PATH,msg);
		}
		//se la directory non esiste controlla CREATEOUTDIR se l'opzione non esiste o è false non la crea e lancia un eccezione
		if(!outDir.exists())
		{
			if(commandArgs.isArgument(CREATEOUTDIR) && (Boolean)commandArgs.getArgument(CREATEOUTDIR))
				try {
					outDir.mkdir();
				} catch (SecurityException e) {
					String msg=FirmapiuException.getDefaultErrorCodeMessage(DIR_FORBIDDEN);
					msg+=" : "+outDirPath;
					throw new FirmapiuException(DIR_FORBIDDEN, msg, e);
				}
			else
			{
				String msgError=FirmapiuException.getDefaultErrorCodeMessage(DIR_FORBIDDEN)+" : "+outDirPath;
				throw new FirmapiuException(DIR_FORBIDDEN,msgError);
			}
		}
		//altrimenti se è un file lancia un eccezione
		else if(outDir.isFile()){
			String msgError=FirmapiuException.getDefaultErrorCodeMessage(IS_NOT_DIR)+" : "+outDirPath;
			throw new FirmapiuException(IS_NOT_DIR,msgError);
		}
		return outDir;
	}
	
	//procedura privata per trovare il percorso canonico di un file da un path generico
	/*private static File fileFromPath(String filepath){
		if(filepath.startsWith("~"))
		{
			String[] user=filepath.split("/",2);
			try {
				String command = "ls -d " + user[0];
				Process shellExec = Runtime.getRuntime().exec(
						new String[]{"bash", "-c", command});

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(shellExec.getInputStream()));
				String expandedPath = reader.readLine();

				// Only return a new value if expansion worked.
				// We're reading from stdin. If there was a problem, it was written
				// to stderr and our result will be null.
				if (expandedPath != null) {
					filepath = expandedPath+"/"+user[1];
				}
			} catch (java.io.IOException ex) {
				// Just consider it unexpandable and return original path.
			}
		}
		return new File(filepath).getCanonicalFile();
	}//fine filefrompath*/
}
