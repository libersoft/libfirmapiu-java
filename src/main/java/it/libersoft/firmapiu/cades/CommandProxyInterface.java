/**
 * 
 */
package it.libersoft.firmapiu.cades;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableFile;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSTypedData;

/**
 * Questa classe offre un interfaccia di comandi per accedere alle funzionalità messe a disposizione dall'applicazione Firma_più.
 * 
 * @author dellanna
 *
 */
public class CommandProxyInterface {
	
	//Logger associato alla classe
	final static Logger LOGGER=Logger.getLogger(CommandProxyInterface.class.getCanonicalName());
		
	
	//Opzioni utilizzabili dai comandi	
	
	//pin associato alla chiave utilizzata
	public static final String PIN="pin";
	//directory di output su cui salvare i p7m generati
	public static final String OUTDIR="outdir";
	//alias dell'utente utilizzato per firmare un file tramite la chiave privata
	public static final String ALIAS="alias";
	//carica a runtime il path assoluto del file di configurazione del driver utilizzato per accedere alla smartcard
	public static final String DRIVERPATH="driverpath";
	
	//resource bundle utilizzato per la traduzione dei messaggi testuali restituiti da CommandProxyInterface
	private final ResourceBundle rb;
	
	public CommandProxyInterface(ResourceBundle rb) {
		super();
		this.rb = rb;
	}

	//invoca il comando di sign per firmare una lista di file i cui percorsi sono passati come argomenti
	//restituisce Map<String,?> con il risultato dell'operazione sui file passati come parametro
	public Map<String,?> sign(Set<String> args,Map<String,?> options) throws Exception{
		LOGGER.setLevel(Level.OFF);
		//inizializza e gestisce le opzioni
		//crea un options vuoto se il parametro passato era null (vuol dire che non ci sono opzioni)
		if(options==null)
			options=new TreeMap<String,Object>();
		Set<String> optionKeySet=options.keySet();

		//directory di output
		//recupera la directory di output presso cui salvare i p7m, se non esiste la crea
		File outDir=null;
		if(optionKeySet.contains(OUTDIR)){
			outDir=fileFromPath((String)options.get(OUTDIR));
			//se la directory non esiste la crea
			if(!outDir.exists())
				outDir.mkdir();
			//altrimenti se è un file lancia un eccezione
			else if(outDir.isFile()){
				String outDirStr =outDir.getCanonicalPath();
				throw new IOException(rb.getString("warning0")+": "+outDirStr+" "+rb.getString("foldererror0"));
			}
		}
	
		//driver del provider pkcs#11 
		//se è definita l'opzione DRIVERPATH carica il driver dal driverpath passato come parametro
		//altrimenti inizializza il CadesBesSigner passandogli come parametro il driver della carta utilizzato
		//ricavandolo dal path corrente di esecuzione
		String pkcs11driverpath;
		if(optionKeySet.contains(DRIVERPATH)){
			pkcs11driverpath=(String)options.get(DRIVERPATH)+"/pkcs11driver.config";
		}else{
			String path = CommandProxyInterface.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			String parentString=driverPathDecode(path);
			pkcs11driverpath=parentString+"/pkcs11driver.config";
		}
		
		//normalizza le chiavi ricevute in ingresso per generare una lista univoca di file da firmare
		//recuperando il path canonico per ogni file.
		Set<File> normalizedArgs =new TreeSet<File>();
		Iterator<String> itr=args.iterator();
		while(itr.hasNext()){
			//genera il path canonico e inserisce la chiave in nomalizedArgs
			File canonicalFile=fileFromPath(itr.next());
			normalizedArgs.add(canonicalFile);
		}
		
		//firma tutti i file passati come argomento uno ad uno e genera i p7m
		
		//recupera il pin della carta e crea il signer
		if(!optionKeySet.contains(PIN))
			throw new NullPointerException(rb.getString("warning0")+": "+rb.getString("pinerror0"));	
		char[] pin=(char[])options.get(PIN);
		CMSSigner signer = new CMSSigner(pkcs11driverpath,pin,rb);
		//resetta il pin e restituisce i risulti delle operazioni effettuate sui file passati come parametro
		java.util.Arrays.fill(pin, ' ');

		//prepara Map<String,Object> con i risultati delle operazioni effettuate sui file passati come parametro.
		Map<String,Object> result = new TreeMap<String,Object>();

		Iterator<File> itr2=normalizedArgs.iterator();
		while(itr2.hasNext()){
			File filein=itr2.next();
			try {
				if (!filein.exists())
					throw new FileNotFoundException(filein.getCanonicalPath()+" "+rb.getString("filerror0"));
				CMSTypedData data = new CMSProcessableFile(filein);
				CMSSignedData signedData=signer.sign(data);
				signer.close();
				File fileout;
				if(outDir==null)
					fileout = new File(filein.getCanonicalPath()+".p7m");
				else
					fileout = new File(outDir.getCanonicalPath()+"/"+filein.getName()+".p7m");
				FileOutputStream fileoutStream =new FileOutputStream(fileout);
				fileoutStream.write(signedData.getEncoded());
				fileoutStream.flush();
				fileoutStream.close();
				//se l'operazione è andata bene, genera il percorso del .p7m risultante del file passato come parametro
				result.put(filein.getCanonicalPath(),fileout.getCanonicalPath());
			} catch (FileNotFoundException e){
				//associa un errore di file non found per il file passato come parametro
				String errString=rb.getString("warning0")+": "+rb.getString("filenotfound0")+"\n"+e.getMessage();
				LOGGER.severe(errString);
				result.put(filein.getCanonicalPath(), e);
			} catch (IOException e) {
				// logga eccezione
				//LOGGER.throwing(CommandInterface.class.getCanonicalName(), "sign", e);
				//associa un errore generico di I/O per il file passato come parametro
				String errString=rb.getString("warning0")+": "+rb.getString("ioerror0")+"\n"+e.getMessage();
				LOGGER.severe(errString);
				result.put(filein.getCanonicalPath(), e);
			} catch (Exception e) {
				//resetta ilpin e rilancia eccezione
				java.util.Arrays.fill(pin, ' ');
				throw e;
			}//fine try-catch
		}//fine while

		//resetta il pin e restituisce i risulti delle operazioni effettuate sui file passati come parametro
		//java.util.Arrays.fill(pin, ' ');
		return result;
	}
	
	//verifica che la lista di file p7m i cui percorsi sono passati tramite argomento siano firmati correttamente
	//restituisce Map<String,?> con il risultato dell'operazione sui file passati come parametro
	public Map<String,?> verify(Set<String> args,Map<String,?> options) {
		LOGGER.setLevel(Level.OFF);
		//Set<String> optionKeySet=options.keySet();


	

		//verifica la firma tutti i file passati come argomento uno ad uno e
		//restituisce Map<String,Boolean> dove le chiavi sono i percorsi dei file passati come argomento al comando e i valori associati sono
		//un booleano per determinare se il file è stato correttamente firmato o meno

		//prepara Map<String,Object> con i risultati delle operazioni effettuate sui file passati come parametro.
		TreeMap<String,Object> result=new TreeMap<String,Object>();

		Iterator<String> itr=args.iterator();
		while(itr.hasNext()){
			String fileinPathStr=itr.next();
			try {
				//File filein=new File(fileinPathStr);
				File filein=fileFromPath(fileinPathStr);
				//if (!filein.exists())
				//	throw new FileNotFoundException(rb.getString("warning0")+" il file: "+filein.getCanonicalPath()+" non esiste");
				String key=filein.getCanonicalPath();
				CMSSignedData signedData = new CMSSignedData(new FileInputStream(filein));
				//se tutto va bene, restituisce Map<String,Boolean> dove le chiavi sono i percorsi 
				//dei file passati come argomento al comando e i valori associati sono
				//un booleano per determinare se il file è stato firmato correttamente o meno
				result.put(key, new Boolean(CMSSigner.verify(signedData,rb)));
			} catch (FileNotFoundException e) {
				//associa un errore di file non found per il file passato come parametro
				String errString=rb.getString("warning0")+": "+rb.getString("filenotfound0")+"\n"+e.getMessage();
				LOGGER.severe(errString);
				result.put(fileinPathStr, e);
			} catch (IOException e) {
				//associa un errore generico di I/O per il file passato come parametro
				String errString=rb.getString("warning0")+": "+rb.getString("ioerror0")+"\n"+e.getMessage();
				LOGGER.severe(errString);
				result.put(fileinPathStr, e);
			} catch (CMSException e) {
				//associa un errore di validazione per il file passato come parametro
				String errString=rb.getString("warning0")+": "+rb.getString("verifyerror0")+"\n"+e.getMessage();
				LOGGER.severe(errString);
				result.put(fileinPathStr, e);
			} //fine try-catch
		} //fine while
		return result;
	}
	
	
	//PROCEDURE PRIVATE
	//procedura privata per trovare il percorso canonico di un file da un path generico
	private static File fileFromPath(String filepath) throws IOException{
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
	}
	
	//procedura privata per decodificare il path della libreria utilizzata per caricare la carta
	private static String driverPathDecode(String path) throws Exception{
		String decodedPath = URLDecoder.decode(path, "UTF-8");
		File file =new File(decodedPath);
		//System.out.println("--------->"+file.getCanonicalPath());
		String parentString=file.getParent()+"/config";
		if(!(new File(parentString).exists())){
			parentString=driverPathDecode(file.getParent());
		}
		return parentString;
	}
}
