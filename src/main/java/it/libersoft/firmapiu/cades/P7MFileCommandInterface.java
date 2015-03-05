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
	private final String tokenType;
	
	/**
	 * la classe non dovrebbe essere inizializzata se non attraverso la factory
	 * 
	 * @param tokenType Il tipo di token utilizzato per l'operazione di firma dei files
	 * @see it.libersoft.firmapiu.consts.FactoryPropConsts
	 */
	protected P7MFileCommandInterface(String tokenType) {
		this.tokenType=tokenType;
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
		DataFilePath dataFilePath;
		if( data==null || !(data instanceof DataFilePath))
			throw new IllegalArgumentException(RB.getString("factoryerror4")
					+ " : " + data.getClass().getCanonicalName());
		else 
			dataFilePath=(DataFilePath)data;
		GenericArgument commandArgs;
		if( option==null || !(option instanceof GenericArgument))
			throw new IllegalArgumentException(RB.getString("factoryerror4")
					+ " : " + option.getClass().getCanonicalName());
		else 
			commandArgs=(GenericArgument)option;

		//controlla gli argomenti
		//TODO bisogna documentare con attenzione tutti gli argomenti che il comando può accettare se l'arg è obbligatorio e che valore può accettare

		//directory di output
		//recupera la directory di output. presso cui salvare i p7m, se non esiste la crea
		//Default: se l'argomento non è presente il p7m viene salvato nella stessa dir del file da firmare
		//DefauLt: se la directory non esiste e non è presente l'opzione CREATESIGNOUTDIR 
		//O L'OPZIONE È FALSE NON CREA LA DIRECTORY e lancia un eccezione 
		File outDir=null;
		if(commandArgs.isArgument(SIGNOUTDIR)){
			//controlla che il percorso della directory sia assoluto altrimenti lancia un errore
			String outDirPath=(String)commandArgs.getArgument(SIGNOUTDIR);
			outDir=new File(outDirPath);
			if(!outDir.isAbsolute()){
				String msg=FirmapiuException.getDefaultErrorCodeMessage(IS_NOT_ABS_PATH);
				msg+=" : "+outDirPath;
				throw new FirmapiuException(IS_NOT_ABS_PATH,msg);
			}
			//se la directory non esiste controlla CREATESIGNOUTDIR se l'opzione non esiste o è false non la crea e lancia un eccezione
			if(!outDir.exists())
			{
				if(commandArgs.isArgument(CREATESIGNOUTDIR) && (Boolean)commandArgs.getArgument(CREATESIGNOUTDIR))
					try {
						outDir.mkdir();
					} catch (SecurityException e) {
						String msg=FirmapiuException.getDefaultErrorCodeMessage(MKDIR_FORBIDDEN);
						msg+=" : "+outDirPath;
						throw new FirmapiuException(MKDIR_FORBIDDEN, msg, e);
					}
				else
				{
					String msgError=FirmapiuException.getDefaultErrorCodeMessage(MKDIR_FORBIDDEN)+" : "+outDirPath;
					throw new FirmapiuException(MKDIR_FORBIDDEN,msgError);
				}
			}
			//altrimenti se è un file lancia un eccezione
			else if(outDir.isFile()){
				String msgError=FirmapiuException.getDefaultErrorCodeMessage(IS_NOT_DIR)+" : "+outDirPath;
				throw new FirmapiuException(IS_NOT_DIR,msgError);
			}
		}//fine if controllo argomento SIGNOUTDIR

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
		CadesBesSigner signer=null;
		CRToken token=null;
		while(dataPathItr.hasNext()){
			File dataFileIn=new File(dataPathItr.next());
			try {
				if (!dataFileIn.exists())
					throw new FileNotFoundException(dataFileIn.getAbsolutePath()+" "+RB1.getString("filerror0"));
				CMSTypedData cmsDataIn = new CMSProcessableFile(dataFileIn);
				//se il file esiste inizializza una sessione, altrimenti usa quella che è già stata inizializzata
				if(signer==null){
					//crea il token crittografico a seconda del tipo passato come parametro alla P7MFileCommandInterface
					token=MasterFactoryBuilder.getFactory(this.tokenType).getToken(CRTSMARTCARD);
					//se il token è di tipo PKCS11Token, inizializza la sessione
					if(token instanceof PKCS11Token)
						((PKCS11Token)token).login(tokenpin);
					signer = new CadesBesSigner(token);
				}
				CMSSignedData signedData=signer.sign(cmsDataIn);
				File dataFileOut;
				if(outDir==null)
					dataFileOut = new File(dataFileIn.getAbsolutePath()+".p7m");
				else
					dataFileOut = new File(outDir.getAbsolutePath()+"/"+dataFileIn.getName()+".p7m");
				FileOutputStream fileoutStream =new FileOutputStream(dataFileOut);
				fileoutStream.write(signedData.getEncoded());
				fileoutStream.flush();
				fileoutStream.close();
				//se l'operazione è andata bene, genera il percorso del .p7m risultante del file passato come parametro
				result.put(dataFileIn.getAbsolutePath(),dataFileOut.getAbsolutePath());
			} catch (FileNotFoundException e) {
				String msg= FirmapiuException.getDefaultErrorCodeMessage(FILE_NOTFOUND);
				msg+=" : "+dataFileIn.getAbsolutePath();
				FirmapiuException fe1 =new FirmapiuException(FILE_NOTFOUND, msg, e);
				result.put(dataFileIn.getAbsolutePath(), fe1);
			} catch (IOException e) {
				FirmapiuException fe1 =new FirmapiuException(IO_DEFAULT_ERROR, e);
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
			}//fine try-catch
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
		// TODO Auto-generated method stub
		return null;
	}	
	
	//PROCEDURE PRIVATE
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
