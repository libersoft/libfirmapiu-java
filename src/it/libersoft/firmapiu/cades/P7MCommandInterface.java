/**
 * 
 */
package it.libersoft.firmapiu.cades;

import it.libersoft.firmapiu.Data;
import it.libersoft.firmapiu.DataFilePath;
import it.libersoft.firmapiu.Argument;
import it.libersoft.firmapiu.GenericArgument;
import it.libersoft.firmapiu.exception.FirmapiuException;
import static it.libersoft.firmapiu.consts.ArgumentConsts.*;
import static it.libersoft.firmapiu.exception.FirmapiuException.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;


/**
 * La classe offre operazioni per firmare e la verificare  un insieme di files firmati elettronicamente (.p7m) 
 * secondo la DELIBERAZIONE ministeriale del N . 45 DEL 21 MAGGIO 2009.
 * 
 * @author dellanna
 *
 */
class P7MCommandInterface implements CadesBESCommandInterface {

	// inizializza il resourcebundle per il recupero dei messaggi lanciati dalla
	// classe
	private static final ResourceBundle RB = ResourceBundle.getBundle(
				"it.libersoft.firmapiu.lang.localefactory", Locale.getDefault());
	
	/**
	 * la classe non dovrebbe essere inizializzata se non attraverso la factory
	 * */
	protected P7MCommandInterface() {}

	/** 
	 * Firma un batch di file usando le credenziali del token crittografico associato
	 * 
	 * @param data contenete il riferimento ai percorsi dei file da firmare. I file presi in considerazioni devono essere delle "chiavi"
	 * in modo tale che il sistema non firmi lo stesso file più di una volta
	 * @param option Gli argomenti presi in considerazione in fase di esecuzione del comando<br>
	 * Un argomento obbligatorio è "tokenpin" in quanto per accedere alle credenziali presenti sul token per firmare il file bisogna passargli il pin
	 * @return Una map contenente l'esito dell'operazione di firma per ogni file passato come parametro
	 * 
	 * @see it.libersoft.firmapiu.CommandInterface#sign(it.libersoft.firmapiu.Data, it.libersoft.firmapiu.Argument)
	 * @see it.libersoft.firmapiu.consts.ArgumentConsts
	 */
	@Override
	public Map<String, ?> sign(Data<?> data, Argument<?, ?> option) throws IllegalArgumentException,FirmapiuException{
		//FIXME ATTENZIONE TRY CATCH GLOBALE CHE SERVE SOLO PER STUDIARE GLI ERRORI. DA CANCELLARE ASSOLUTAMENTE!!!!!
		try {
			//controllo di coerenza iniziale sugli argomenti
			DataFilePath datafile;
			if( data==null || !(data instanceof DataFilePath))
				throw new IllegalArgumentException(RB.getString("factoryerror4")
						+ " : " + data.getClass().getCanonicalName());
			else 
				datafile=(DataFilePath)data;
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
				outDir=fileFromPath((String)commandArgs.getArgument(SIGNOUTDIR));
				//se la directory non esiste controlla CREATESIGNOUTDIR se l'opzione non esiste o è false non la crea e lancia un eccezione
				if(!outDir.exists())
				{
					if(commandArgs.isArgument(CREATESIGNOUTDIR) && (Boolean)commandArgs.getArgument(CREATESIGNOUTDIR))
						outDir.mkdir();
					else
					{
						String dir= (String)commandArgs.getArgument(SIGNOUTDIR);
						String msgError=FirmapiuException.getDefaultErrorCodeMessage(MKDIR_FORBIDDEN)+" : "+dir;
						throw new FirmapiuException(MKDIR_FORBIDDEN,msgError);
					}
				}
				//altrimenti se è un file lancia un eccezione
				else if(outDir.isFile()){
					String outDirStr =outDir.getCanonicalPath();
					String msgError=FirmapiuException.getDefaultErrorCodeMessage(IS_NOT_DIR)+" : "+outDirStr;
					throw new FirmapiuException(IS_NOT_DIR,msgError);
				}
			}
			
			//pin
			//recupera il pin del token utilizzato
			//Obbligatorio: L'argomento non può essere omesso. Se l'argomento viene omesso, il token crittografico
			//non è in grado di recuperare le credenziali utilizzare per firmare l'insieme di file passati come parametro
			char[] tokenpin=null;
			if(commandArgs.isArgument(TOKENPIN)){
				tokenpin=(char[])commandArgs.getArgument(TOKENPIN);
			} else
				throw new FirmapiuException(CRT_TOKENPIN_NOTFOUND);
			
			
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

			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new FirmapiuException(e);
		}
	}

	@Override
	public Map<?, ?> verify(Data<?> data, Argument<?, ?> option) throws FirmapiuException{
		// TODO Auto-generated method stub
		return null;
	}	
	
	//PROCEDURE PRIVATE
	//procedura privata per trovare il percorso canonico di un file da un path generico
	private static File fileFromPath(String filepath)throws IOException{
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
	}//fine filefrompath
}
