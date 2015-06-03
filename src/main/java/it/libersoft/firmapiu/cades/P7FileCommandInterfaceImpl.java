/**
 * 
 */
package it.libersoft.firmapiu.cades;

import it.libersoft.firmapiu.CRToken;
import it.libersoft.firmapiu.Data;
//import it.libersoft.firmapiu.Argument;
//import it.libersoft.firmapiu.GenericArgument;
//import it.libersoft.firmapiu.MasterFactoryBuilder;
import it.libersoft.firmapiu.P7SData;
import it.libersoft.firmapiu.Report;
import it.libersoft.firmapiu.ResultInterface;
import it.libersoft.firmapiu.crtoken.KeyStoreToken;
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
import java.security.ProviderException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;

import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSProcessableFile;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.SignerInformation;

import com.sun.org.apache.xpath.internal.operations.Bool;


/**
 * La classe offre operazioni per firmare e la verificare  un insieme di files firmati elettronicamente (.p7m) 
 * secondo la DELIBERAZIONE ministeriale del N . 45 DEL 21 MAGGIO 2009.
 * 
 * @author dellanna
 *
 */
final class P7FileCommandInterfaceImpl implements P7FileCommandInterface {

	// inizializza i resourcebundle per il recupero dei messaggi lanciati dalla
	// classe
	private static final ResourceBundle RB = ResourceBundle.getBundle(
				"it.libersoft.firmapiu.lang.localefactory", Locale.getDefault());
	private static final ResourceBundle RB1 = ResourceBundle.getBundle(
			"it.libersoft.firmapiu.lang.locale", Locale.getDefault());
	
	//tipo di token utilizzato per le operazioni di firma
	private final CRToken signToken;
	//tipo di token utilizzato per le operazioni di verifica
	private final CRToken verifyToken;
	
	//digestCalculator provider utilizzato per le operazioni di firma
	private final String digestCalculatorProviderStr;
	
	/**
	 * la classe non dovrebbe essere inizializzata se non attraverso la factory
	 * 
	 * @param signToken Il tipo di token utilizzato per le operazioni di firma dei files
	 * @param verifyToken Il tipo di token utilizzato per lee operazioni di verifica  tecnica e legale 
	 * di files firmati elettronicamente in formato CADES-bes (attacched)  
	 * @see it.libersoft.firmapiu.consts.FactoryPropConsts
	 */
	protected P7FileCommandInterfaceImpl(CRToken signToken,CRToken verifyToken,String digestCalculatorProviderStr) {
		//TODO proviamo a passare i token come parametro
		this.signToken=signToken;
		this.verifyToken=verifyToken;
		this.digestCalculatorProviderStr=digestCalculatorProviderStr;
		//TODO gestire p7m p7s
//		if(fileType==null)
//			throw new IllegalArgumentException();
//		else if (fileType.equals(P7MFILE)|| fileType.equals(P7SFILE))
//			this.fileType=fileType;
//		else
//			throw new IllegalArgumentException();
	}

	/** 
	 * Firma un batch di file usando le credenziali del token crittografico associato
	 * 
	 * @param data contenete il riferimento ai percorsi dei file da firmare.
	 * @return Una ResultInterface contenente l'esito dell'operazione di firma per ogni file passato come parametro
	 * 
	 * @see it.libersoft.firmapiu.CommandInterface#sign(it.libersoft.firmapiu.Data, it.libersoft.firmapiu.Argument)
	 * @see it.libersoft.firmapiu.consts.ArgumentConsts
	 */
	@Override
	public ResultInterface<File, File> sign(Data<File> data) throws FirmapiuException{
		//se non è stato definito il token per la firma lancia un eccezione
		if(this.signToken==null)
			throw new FirmapiuException(CRT_TOKEN_NOTFOUND, new NullPointerException("signToken=null"));
		
		//controllo di coerenza iniziale sugli argomenti
		//DataFileImpl dataFilePath = checkData(data);
		//GenericArgument commandArgs = checkArgument(option);
	
		//controlla gli argomenti
		Map<String,String> commandArgs = data.getArgumentMap();
		//TODO bisogna documentare con attenzione tutti gli argomenti che il comando può accettare se l'arg è obbligatorio e che valore può accettare

		//directory di output 
		File outDir=null;
		if(commandArgs.containsKey(OUTDIR)){
			outDir=getOutDir(commandArgs);
		}
		
		//contenuto attached/detached dei file
		boolean detached=false;
		if(commandArgs.containsKey(DETACHED))
			detached=Boolean.parseBoolean(commandArgs.get(DETACHED));

		//copyfile: l'override di un file esistente non è permesso. tuttavia se l'opzione copyfile è settata a true
		//permette di duplicare un file chiamandolo con un nome diverso
		boolean copyfile=false;
		if(commandArgs.containsKey(COPYFILE))
			copyfile=Boolean.parseBoolean(commandArgs.get(COPYFILE));
		
		//pin
		//recupera il pin del token utilizzato
		//Obbligatorio: L'argomento non può essere omesso. Se l'argomento viene omesso, il token crittografico
		//non è in grado di recuperare le credenziali utilizzare per firmare l'insieme di file passati come parametro
		//TODO proviamo a gestire il token pkcs11 da un altra parte
//		char[] tokenpin=null;
//		if(commandArgs.isArgument(TOKENPIN)){
//			tokenpin=(char[])commandArgs.getArgument(TOKENPIN);
//		} else
//			throw new FirmapiuException(CRT_TOKENPINPUK_VERIFY_ERROR);


		//prepara Map<String,Object> con i risultati delle operazioni effettuate sui file passati come parametro.
		TreeMap<File,Object> mapResult = new TreeMap<File,Object>();
		//recupera il dataset contenente i percorsi dei file da firmare
		Set<File> dataFilePathSet=data.getDataSet();
		
		Iterator<File> dataPathItr=dataFilePathSet.iterator();
		CadesBESSigner signer=null;
		//CRToken token=null;
		while(dataPathItr.hasNext()){
			File tmp = dataPathItr.next();
			//recupera l'identificatore univoco associato al dato che si vuole firmare
			String dataId=data.getDataId(tmp);
			//lo passa come percorso del file da firmare
			File dataFileIn=new File(dataId);
			if(!dataFileIn.equals(tmp))
				throw new IllegalArgumentException("DataFile implementation not valid!");
			FileOutputStream fileOutStream=null;
			try {
				//controlla che il file di input esiste
				if (!dataFileIn.exists())
					throw new FileNotFoundException(dataFileIn.getAbsolutePath()+" "+RB1.getString("filerror0"));
				
				//si prepara a creare il file di output: se sovrascrive un file esistente lancia un errore
				
				File dataFileOut;
				String ext;
				if(!detached)
					ext=".p7m";
				else
					ext=".p7s";
				if(outDir==null)
					dataFileOut = new File(dataFileIn.getAbsolutePath()+ext);
				else
					dataFileOut = new File(outDir.getAbsolutePath()+"/"+dataFileIn.getName()+ext);
				if(dataFileOut.exists()){
					if(!copyfile)
						//non si può sovrascrivere un file esistente
						throw new IOException("Cannot override file!");
					else{
						//procedura per rinominare un file esistente
						int i=1;
						while(true){
							dataFileOut = new File(outDir.getAbsolutePath()+"/"+dataFileIn.getName()+i+ext);
							if(!dataFileOut.exists())
								break;
							i++;
						}
					}
				}
				//TODO Passare l'array al posto del file
				//CMSTypedData cmsDataIn = new CMSProcessableFile(dataFileIn);
				CMSTypedData cmsDataIn = new CMSProcessableByteArray(data.getArrayData(tmp));
				
				//se non è già stata inizializzata, inizializza una sessione di login se il token usato per firmare è pkcs#11
				if(signer==null){
					//TODO token pkcs11 gestito da un altra parte
//					//crea il token crittografico a seconda del tipo passato come parametro alla P7FileCommandInterfaceImpl
//					token=TokenFactoryBuilder.getFactory(this.signTokenType).getToken(CRTSMARTCARD);
//					//se il token è di tipo PKCS11Token, inizializza la sessione
//					if(token instanceof PKCS11Token)
//						((PKCS11Token)token).login(tokenpin);
					signer = new CadesBESSigner(this.signToken,this.digestCalculatorProviderStr);
				}
				CMSSignedData signedData;
				if(!detached)
					signedData=signer.sign(cmsDataIn,true);
				else
					signedData=signer.sign(cmsDataIn,false);
				fileOutStream =new FileOutputStream(dataFileOut);
				fileOutStream.write(signedData.getEncoded());
				
				//se l'operazione è andata bene, genera il percorso del .p7m risultante del file passato come parametro
				//mapResult.put(dataFileIn.getAbsolutePath(),dataFileOut.getAbsolutePath());
				mapResult.put(dataFileIn,dataFileOut);
			} catch (FileNotFoundException e) {
				String msg= FirmapiuException.getDefaultErrorCodeMessage(FILE_NOTFOUND);
				msg+=" : "+dataFileIn.getAbsolutePath();
				FirmapiuException fe1 =new FirmapiuException(FILE_NOTFOUND, msg, e);
				mapResult.put(dataFileIn, fe1);
			} catch (IOException e) {
				FirmapiuException fe1 = null;
				if(e.getMessage().equals("Cannot override file!")){
					String msg= FirmapiuException.getDefaultErrorCodeMessage(FILE_OVERRIDE_ERROR);
					msg+=" : "+dataFileIn.getAbsolutePath();
					fe1 =new FirmapiuException(FILE_OVERRIDE_ERROR,msg, e);
				}
				else
					fe1 =new FirmapiuException(IO_DEFAULT_ERROR, e);
				mapResult.put(dataFileIn, fe1);
			} catch (CMSException e){
				FirmapiuException fe1 =new FirmapiuException(SIGNER_CADESBES_ERROR, e);
				mapResult.put(dataFileIn, fe1);
			} catch (ProviderException e){
				//questa eccezione potrebbe essere lanciata se si rimuove il token pkcs11 durante il processo di firma
				//se il token è PKCS11token si slogga e rilancia l'eccezione firmapiuexception al chiamante
				//TODO si prova a gestire il token pkcs11 da un altra parte
				//				if((token!=null)&&(token instanceof PKCS11Token)){
//					((PKCS11Token)token).logout();
//				}
				FirmapiuException fe1 =new FirmapiuException(SIGNER_TOKEN_REMOVED, e);
				throw fe1;
			}
			catch (FirmapiuException e) {
				//se il token è PKCS11token si slogga e rilancia l'eccezione firmapiuexception al chiamante
				//TODO si prova a gestire il token pkcs11 da un altra parte
//				if((token!=null)&&(token instanceof PKCS11Token)){
//					((PKCS11Token)token).logout();
//				}
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
		//TODO si prova a gestire il token da un altra parte
//		if((token!=null)&&(token instanceof PKCS11Token)){
//			((PKCS11Token)token).logout();
//		}
		
		//resetta il pin e restituisce i risulti delle operazioni effettuate sui file passati come parametro
		//java.util.Arrays.fill(pin, ' ');
		return new ResultFileInterfaceImpl<File>(mapResult);
	}
	
	
	/** 
	 * Restituisce l'esito dell'operazione di verifica della firma digitale per una serie di file passati come parametro
	 * 
	 * @param signedData Il percorso dei file firmati nel formato p7m di cui si vuole verificare la correttezza della firma digitale
	 * @param option Argomenti opzionali generici passati al comando
	 * @return Una map contenente l'esito dell'operazione di verifica firma per ogni file passato come parametro
	 * @throws FirmapiuException 
	 * @throws IllegalArgumentException 
	 * 
	 * @see it.libersoft.firmapiu.CommandInterface#verify(it.libersoft.firmapiu.Data, it.libersoft.firmapiu.Argument)
	 */
	@Override
	public ResultInterface<File, Report> verify(Data<File> signedData) throws FirmapiuException{
		//se non è stato definito il token per la verifica lancia un eccezione
		if(this.verifyToken==null)
			throw new FirmapiuException(CRT_TOKEN_NOTFOUND, new NullPointerException("verifyToken=null"));
		
		//inizializza il token contenente i certificati di ROOT delle CA utilizzati 
		//per controllare l'affidabilità del certificato del firmatario
		
		//controllo di coerenza iniziale sugli argomenti
		//DataFileImpl signedDataFilePath = checkData(signedDataPath);
		//GenericArgument commandArgs = checkArgument(option);

//		//directory di output
//		File outDir=null;
//		if(commandArgs.isArgument(OUTDIR)){
//			outDir=getOutDir(commandArgs);
//		}

		//prepara Map<String,Object> con i risultati delle operazioni effettuate sui file passati come parametro.
		TreeMap<File,Object> resultMap = new TreeMap<File,Object>();
		//recupera il dataset contenente i percorsi dei file da verificare
		Set<File> dataFilePathSet=signedData.getDataSet();

		//inizializza e carica il token utilizzato per controllare l'affidabilità della catena dei certificati dei firmatari
		//TODO proviamo ad utilizzare il token da un altra parte
		//CRToken token=TokenFactoryBuilder.getFactory(this.verifyTokenType).getToken(TSLXMLKEYSTORE);
		//token.loadKeyStore(null);
		this.verifyToken.loadKeyStore(null);
		
		
		//per ogni file presente in signedData cerca di verificare la correttezza della firma digitale
		Iterator<File> dataPathItr=dataFilePathSet.iterator();
		while(dataPathItr.hasNext()){
			File tmp = dataPathItr.next();
			//recupera l'identificatore univoco associato al dato che si vuole firmare
			String dataId=signedData.getDataId(tmp);
			//lo passa come percorso del file da verificare
			File dataFileIn=new File(dataId);
			//File dataFileIn=new File(dataPathItr.next());
			if(!dataFileIn.equals(tmp))
				throw new IllegalArgumentException("DataFile implementation not valid!");
			try {	
				//crea la busta crittografica dal file di input
				CMSSignedData cmsSignedData =file2CMSSignedData(dataFileIn);
				
				//crea il verificatore per verificare la signedData ed effettua tutte le verifiche su tutti i firmatari
				CadesBESVerifier verifier = new CadesBESVerifier(cmsSignedData, this.verifyToken);
				//List<Map<String,Object>> report=verifier.verifyAllSigners();
				//TODO da cambiare con un report dei risultati più umano?
				ReportImpl report= new ReportImpl(verifier);
				
				
				//se l'operazione è andata bene, associa il report al percorso del file passato come parametro
				resultMap.put(dataFileIn,report);
			} catch (FirmapiuException e) {
				//associa l'errore al percorso del file passato come parametro
				resultMap.put(dataFileIn,e);
			} catch (IOException e) {
				FirmapiuException fe1 =new FirmapiuException(IO_DEFAULT_ERROR, e);
				resultMap.put(dataFileIn, fe1);
			}
		}
		return new ResultFileInterfaceImpl<Report>(resultMap);
	}

	@Override
	public ResultInterface<String, Report> verifyP7S(P7SData<String> data)
			throws FirmapiuException {
		// TODO Auto-generated method stub
		return null;
	}
	
	/** 
	 * Restituisce il contenuto originale di una serie di File passati come parametri
	 * 
	 * @param signedData Il percorso dei file firmati nel formato p7m di cui si vuole recuperare il contenuto originale del file
	 * @param option Argomenti opzionali generici passati al comando
	 * @return Una map contenente l'esito dell'operazione di getContentSignedData per ogni file passato come parametro
	 * 
	 * @see it.libersoft.firmapiu.CommandInterface#getContentSignedData(it.libersoft.firmapiu.Data, it.libersoft.firmapiu.Argument)
	 */
	@Override
	public ResultInterface<File, File> getContentSignedData(Data<File> signedData) throws FirmapiuException {
		//controllo di coerenza iniziale sugli argomenti
		//DataFileImpl signedDataFilePath = checkData(signedDataPath);
		//GenericArgument commandArgs = checkArgument(option);

		//controlla gli argomenti
		Map<String,String> commandArgs = signedData.getArgumentMap();
		//TODO bisogna documentare con attenzione tutti gli argomenti che il comando può accettare se l'arg è obbligatorio e che valore può accettare

		//directory di output 
		File outDir=null;
		if(commandArgs.containsKey(OUTDIR)){
			outDir=getOutDir(commandArgs);
		}

		//copyfile: l'override di un file esistente non è permesso. tuttavia se l'opzione copyfile è settata a true
		//permette di duplicare un file chiamandolo con un nome diverso
		boolean copyfile=false;
		if(commandArgs.containsKey(COPYFILE))
			copyfile=Boolean.parseBoolean(commandArgs.get(COPYFILE));
		
		//prepara Map<String,Object> con i risultati delle operazioni effettuate sui file passati come parametro.
		TreeMap<File,Object> mapResult = new TreeMap<File,Object>();
		//recupera il dataset contenente i percorsi dei file da firmare
		Set<File> dataFilePathSet=signedData.getDataSet();
		
		//per ogni file presente in signedDataFilePath cerca di restituire il contenuto originale del file
		Iterator<File> dataPathItr=dataFilePathSet.iterator();
		while(dataPathItr.hasNext()){
			File tmp = dataPathItr.next();
			//recupera l'identificatore univoco associato al dato che si vuole firmare
			String dataId=signedData.getDataId(tmp);
			//lo passa come percorso del file da verificare
			File dataFileIn=new File(dataId);
			//File dataFileIn=new File(dataPathItr.next());
			if(!dataFileIn.equals(tmp))
				throw new IllegalArgumentException("DataFile implementation not valid!");
			FileOutputStream fileOutStream=null;	
			try {
				//crea la busta crittografica dal file di input
				CMSSignedData cmsSignedData =file2CMSSignedData(dataFileIn);

				//recupera il contenuto originale del file p7m
				
				//crea il nome del file di output, non si può sovrascrivere un file esistente
				File dataFileOut;
				String fileOutName=dataFileIn.getName();
				fileOutName=fileOutName.substring(0, fileOutName.length()-4);
				if(outDir==null)
					dataFileOut = new File(dataFileIn.getParent()+"/"+fileOutName);
				else
					dataFileOut = new File(outDir.getAbsolutePath()+"/"+fileOutName);
				//non si può sovrascrivere un file esistente
				if(dataFileOut.exists()){
					if(!copyfile)
						//non si può sovrascrivere un file esistente
						throw new IOException("Cannot override file!");
					else{
						//procedura per rinominare un file esistente
						int i=1;
						while(true){
							dataFileOut = new File(outDir.getAbsolutePath()+"/"+fileOutName+"."+i);
							if(!dataFileOut.exists())
								break;
							i++;
						}
					}
				}
				fileOutStream=new FileOutputStream(dataFileOut);
				//scrive il contenuto del file originale sul file di output
				try {
					cmsSignedData.getSignedContent().write(fileOutStream);
				} catch (CMSException e) {
					throw new FirmapiuException(CONTENT_CADESBES_DEFAULT_ERROR, e);
				}
				
				//se l'operazione è andata bene, genera il percorso del file risultante del file passato come parametro
				mapResult.put(dataFileIn,dataFileOut);
			} catch (FileNotFoundException e) {
				String msg= FirmapiuException.getDefaultErrorCodeMessage(FILE_NOTFOUND);
				msg+=" : "+dataFileIn.getAbsolutePath();
				FirmapiuException fe1 =new FirmapiuException(FILE_NOTFOUND, msg, e);
				mapResult.put(dataFileIn, fe1);
			} catch (IOException e) {
				FirmapiuException fe1 = null;
				if(e.getMessage().equals("Cannot override file!")){
					String msg= FirmapiuException.getDefaultErrorCodeMessage(FILE_OVERRIDE_ERROR);
					msg+=" : "+dataFileIn.getAbsolutePath();
					fe1 =new FirmapiuException(FILE_OVERRIDE_ERROR,msg,e);
				}
				else
					fe1 =new FirmapiuException(IO_DEFAULT_ERROR, e);
				mapResult.put(dataFileIn, fe1);
			} catch (SecurityException e){
				FirmapiuException fe1 =new FirmapiuException(FILE_FORBIDDEN, e);
				mapResult.put(dataFileIn, fe1);
			} catch (FirmapiuException e) {
				mapResult.put(dataFileIn, e);
			}finally{
				try {
					//cerca di chiudere le risorse utilizzate
					fileOutStream.flush();
					fileOutStream.close();
				} catch (Exception e) {}
			}//fine try-catch-finally
		}//fine while
		return new ResultFileInterfaceImpl<File>(mapResult);
	}//fine metodo	

	/**
	 * @see it.libersoft.firmapiu.CommandInterface#getSignToken()
	 */
	@Override
	public CRToken getSignToken() throws FirmapiuException {
		return this.signToken;
	}

	/**)
	 * @see it.libersoft.firmapiu.CommandInterface#getVerifyCrToken()
	 */
	@Override
	public CRToken getVerifyCrToken() throws FirmapiuException {
		return this.verifyToken;
	}
	
	//PROCEDURE PRIVATE
	//controllo coerenza data
//	private static DataFileImpl checkData(Data<?> data){
//		DataFileImpl dataFilePath;
//		if( data==null || !(data instanceof DataFileImpl))
//			throw new IllegalArgumentException(RB.getString("factoryerror4")
//					+ " : " + data.getClass().getCanonicalName());
//		else 
//			dataFilePath=(DataFileImpl)data;
//		return dataFilePath;
//	}
//	//controllo coerenza argomenti
//	private static GenericArgument checkArgument(Argument<?, ?> option){
//		GenericArgument commandArgs;
//		if( option==null || !(option instanceof GenericArgument))
//			throw new IllegalArgumentException(RB.getString("factoryerror4")
//					+ " : " + option.getClass().getCanonicalName());
//		else 
//			commandArgs=(GenericArgument)option;
//		return commandArgs;
//	}
			
	//genera directory di output
	//recupera la directory di output. presso cui salvare il contenuto originale dei file contenuti nel p7m. Se non esiste la crea
	//Default: se l'argomento non è presente il contenuto originale del file viene salvato nella stessa dir del file da verificare
	//DefauLt: se la directory non esiste e non è presente l'opzione CREATEOUTDIR 
	//O L'OPZIONE È FALSE NON CREA LA DIRECTORY e lancia un eccezione 
	private static File getOutDir(Map<String,String> commandArgs) throws FirmapiuException{
		//controlla che il percorso della directory sia assoluto altrimenti lancia un errore
		String outDirPath=commandArgs.get(OUTDIR);
		File outDir=new File(outDirPath);
		if(!outDir.isAbsolute()){
			String msg=FirmapiuException.getDefaultErrorCodeMessage(IS_NOT_ABS_PATH);
			msg+=" : "+outDirPath;
			throw new FirmapiuException(IS_NOT_ABS_PATH,msg);
		}
		//se la directory non esiste controlla CREATEOUTDIR se l'opzione non esiste o è false non la crea e lancia un eccezione
		if(!outDir.exists())
		{
			if(commandArgs.containsKey(CREATEOUTDIR) && Boolean.parseBoolean(commandArgs.get(CREATEOUTDIR)))
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
	
	//procedura privata per recuperare una CMSSIgnedData (una busta crittografica) da un FILE
	private static CMSSignedData file2CMSSignedData(File dataFileIn) throws FirmapiuException, IOException
	{
		//controlla che il file di input termini con .p7m altrimenti lancia un errore
		if(!dataFileIn.getName().endsWith(".p7m"))
			throw new FirmapiuException(CONTENT_CADESBES_NOTP7MFILE);
		
		//controlla che il file esista altrimenti lancia un errore
		if (!dataFileIn.exists())
			throw new FileNotFoundException(dataFileIn.getAbsolutePath()+" "+RB1.getString("filerror0"));
		
		//crea la busta crittografica CMSSignedData. Se non è attacched lancia un errore
		FileInputStream fileInStream = new FileInputStream(dataFileIn);
		byte[] b=new byte[fileInStream.available()];
		fileInStream.read(b);
		try {
			return new CMSSignedData(b);
		} catch (CMSException e) {
			throw new FirmapiuException(CONTENT_CADESBES_ENCODINGERROR_ATTACHED, e);
		}finally{
			//cerca di chiude la risorsa esistente
			try {
				fileInStream.close();
			} catch (Exception e) {}
		}
	}
	
	//implementazione privata di ResultInterface
	private class ResultFileInterfaceImpl<K> implements ResultInterface<File, K>{

		private final TreeMap<File, Object> result;
		
		
		
		protected ResultFileInterfaceImpl(TreeMap<File, Object> result) {
			this.result = result;
		}

		@Override
		public Set<File> getResultDataSet() throws FirmapiuException {
			return this.result.keySet();
		}

		@SuppressWarnings("unchecked")
		@Override
		public K getResult(File key) throws FirmapiuException {
			
			if(this.result.containsKey(key)){
				Object value = this.result.get(key);
				if(value instanceof FirmapiuException)
					throw (FirmapiuException)value;
				else
					return (K)value;
			}
			else
				return null;
		}

		@Override
		public void putFirmapiuException(File key, FirmapiuException e) {
			this.result.put(key, e);
		}
	}
	
	//implementazione privata di report
	private class ReportImpl implements Report{

		private final CadesBESVerifier verifier;
		
		protected ReportImpl(CadesBESVerifier verifier) {
			this.verifier = verifier;
		}

		@Override
		public List<SignerInformation> getSigners() throws FirmapiuException {
			return verifier.getAllSigners();
		}

		@Override
		public Set<String> getSignerRecordFields(SignerInformation signer)
				throws FirmapiuException {
			return this.verifier.verifySigner(signer).keySet();
		}

		@Override
		public Object getSignerField(SignerInformation signer, String field)
				throws FirmapiuException {
			Object fieldValue=this.verifier.verifySignerField(signer, field);
			if(fieldValue == null)
				return null;
			else if(fieldValue instanceof FirmapiuException)
				throw (FirmapiuException)fieldValue;
			else 
				return fieldValue;
		}
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
