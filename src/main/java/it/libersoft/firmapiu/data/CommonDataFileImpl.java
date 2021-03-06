/**
 * 
 */
package it.libersoft.firmapiu.data;

import it.libersoft.firmapiu.Data;
import it.libersoft.firmapiu.exception.FirmapiuException;
import static it.libersoft.firmapiu.exception.FirmapiuException.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Questa classe implementa un "contenitore" che contiene il percorso di un
 * insieme di file da passare come parametro di input
 * alle operazioni di firma/verifica da effettuare sui file stessi
 * <p>
 * 
 * I file presi in considerazioni devono essere delle "chiavi" in modo tale che
 * il sistema non firmi lo stesso file più di una volta<br>
 * I percorsi dei file da firmare devono essere percorsi assoluti.
 * 
 * <b>Attenzione la classe non è sincronizzata.Si deve tenerne in conto in caso
 * di esecuzione concorrente sullo stesso insieme di dati</b>
 * 
 * @author dellanna
 * @see it.libersoft.firmapiu.Data
 * @see it.libersoft.firmapiu.consts.ArgumentConsts
 */
final class CommonDataFileImpl implements DataFile,P7SDataFile {

	// flag per vedere se i percorsi passati come parametro devono essere
	// normalizzati o meno
	// private final boolean normalize;

	// insieme contenete il percorso dei file su cui bisogna fare le operazioni
	// di firma/verifica
	private final TreeMap<File,File> filePathMap;
	// insieme contenente gli argomenti opzionali da passare al comando di
	// firma/verifica
	private final TreeMap<String, String> commandArgs;

	protected CommonDataFileImpl() {
		// TODO vedere se ce bisogno di sincronizzare o meno
		this.filePathMap = new TreeMap<File,File>();
		this.commandArgs = new TreeMap<String, String>();
		// this.normalize=normalize;
	}

	/**
	 * Aggiunge il percorso di un file all'insieme dei files che devono essere
	 * firmati o di cui bisogna verificare la firma
	 * 
	 * @param data
	 *            Un percorso di un file da firmare
	 * @see it.libersoft.firmapiu.Data#setData(java.lang.Object)
	 */
	@Override
	public void setData(File data) throws FirmapiuException {
		// TODO parte da ignorare i percorsi dei file devono già essere forniti
		// normalizzati altrimenti la libreria lancia un errore
		// TODO In un futuro sviluppo della libreria si può decidere se i
		// percorsi debbano essere normalizzati o meno
		// //se il file deve essere normalizzato nei confronti del path relativo
		// e dei link simbolici attiva la procedura
		// //altrimenti lo inserisce direttamente nell'insieme dei file da
		// firmare/verificare
		// if(this.normalize){
		// //genera il path canonico
		// try {
		// data= canonicalPathFromPath(data);
		// } catch (IOException e) {
		// //se non è in grado di generare il path canonico aggiunge il percorso
		// del file originale
		// }
		// }
		if (!data.isAbsolute()) {
			String msg = FirmapiuException
					.getDefaultErrorCodeMessage(IS_NOT_ABS_PATH);
			msg += " : " + data.getAbsolutePath();
			throw new FirmapiuException(IS_NOT_ABS_PATH, msg);
		}
		this.filePathMap.put(data,null);
	}

	/**
	 * @see it.libersoft.firmapiu.Data#getDataSet()
	 */
	@Override
	public Set<File> getDataSet() {
		return this.filePathMap.keySet();
	}

	/**
	 * Restituisce l'identificativo univoco del file passato come parametro<br>
	 * (Il suo percorso assoluto)
	 * 
	 * @return null se il parametro non è stato definito in setData
	 * 
	 * @see it.libersoft.firmapiu.Data#getDataId(java.lang.Object)
	 */
	@Override
	public String getDataId(File data) throws FirmapiuException {
		if (this.filePathMap.containsKey(data))
			return data.getAbsolutePath();
		else
			return null;
	}

	/**
	 * Restituisce una rappresentazione in array di byte del contenuto del file
	 * passato come parametro
	 * 
	 * @return null se il parametro non è stato definito in setData
	 * 
	 * @see it.libersoft.firmapiu.Data#getArrayData(java.lang.Object)
	 */
	@Override
	public byte[] getArrayData(File data) throws FirmapiuException {
		if (!this.filePathMap.containsKey(data))
			return null;

		FileInputStream filein = null;
		try {
			filein = new FileInputStream(data);
			byte[] b = new byte[filein.available()];
			filein.read(b);
			return b;
		} catch (FileNotFoundException e) {
			throw new FirmapiuException(FILE_NOTFOUND, e);
		} catch (IOException e) {
			throw new FirmapiuException(IO_DEFAULT_ERROR, e);
		} finally {
			try {
				filein.close();
			} catch (IOException e) {
			}
		}
	}

	/**
	 * Setta gli argomenti opzionali dei dati che il comando deve eseguire
	 * 
	 * @see it.libersoft.firmapiu.Data#setArgument(java.lang.String,
	 *      java.lang.String)
	 * @see it.libersoft.firmapiu.consts.ArgumentConsts
	 */
	@Override
	public void setArgument(String key, String value) throws FirmapiuException {
		this.commandArgs.put(key, value);
	}

	/**
	 * Recupera gli argomenti associati ai dati
	 * 
	 * @see it.libersoft.firmapiu.Data#getArgumentMap()
	 * @see it.libersoft.firmapiu.consts.ArgumentConsts
	 */
	@Override
	public Map<String, String> getArgumentMap() throws FirmapiuException {
		return (Map<String, String>) this.commandArgs.clone();
	}

	@Override
	public void putP7SData(File data, File content) throws FirmapiuException {
		if (!data.isAbsolute()) {
			String msg = FirmapiuException
					.getDefaultErrorCodeMessage(IS_NOT_ABS_PATH);
			msg += " : " + data.getAbsolutePath();
			throw new FirmapiuException(IS_NOT_ABS_PATH, msg);
		}
		if (!content.isAbsolute()) {
			String msg = FirmapiuException
					.getDefaultErrorCodeMessage(IS_NOT_ABS_PATH);
			msg += " : " + content.getAbsolutePath();
			throw new FirmapiuException(IS_NOT_ABS_PATH, msg);
		}
		this.filePathMap.put(data,content);
	}

	@Override
	public File getP7SContent(File dataKey) throws FirmapiuException {
		File content=this.filePathMap.get(dataKey);
		if(content==null)
			throw new FirmapiuException(FILE_NOTFOUND);
		else if (!content.exists()){
			String msg= FirmapiuException.getDefaultErrorCodeMessage(FILE_NOTFOUND);
			msg+=" : "+content.getAbsolutePath();
			throw new FirmapiuException(FILE_NOTFOUND, msg);
		}
		return content;
	}

	@Override
	public byte[] getContentArrayData(File dataKey) throws FirmapiuException {
		File content=this.filePathMap.get(dataKey);
		if(content==null)
			throw new FirmapiuException(FILE_NOTFOUND);
		else if (!content.exists()){
			String msg= FirmapiuException.getDefaultErrorCodeMessage(FILE_NOTFOUND);
			msg+=" : "+content.getAbsolutePath();
			throw new FirmapiuException(FILE_NOTFOUND, msg);
		}
		FileInputStream fileinStream=null;
		try {
			fileinStream = new FileInputStream(content);
			byte[] contentByte=new byte[fileinStream.available()];
			fileinStream.read(contentByte);
			return contentByte;
		} catch (IOException e) {
			String msg= FirmapiuException.getDefaultErrorCodeMessage(IO_DEFAULT_ERROR);
			msg+=" : "+content.getAbsolutePath();
			throw new FirmapiuException(IO_DEFAULT_ERROR, msg,e);
		} finally{
			try {fileinStream.close();} catch (IOException e) {}
		}
	}

	// PROCEDURE PRIVATE
	// procedura privata per trovare il percorso canonico di un file da un path
	// generico
	/*
	 * private static String canonicalPathFromPath(String filepath) throws
	 * IOException{ if(filepath.startsWith("~")) { String[]
	 * user=filepath.split("/",2); try { String command = "ls -d " + user[0];
	 * Process shellExec = Runtime.getRuntime().exec( new String[]{"bash", "-c",
	 * command});
	 * 
	 * BufferedReader reader = new BufferedReader( new
	 * InputStreamReader(shellExec.getInputStream())); String expandedPath =
	 * reader.readLine();
	 * 
	 * // Only return a new value if expansion worked. // We're reading from
	 * stdin. If there was a problem, it was written // to stderr and our result
	 * will be null. if (expandedPath != null) { filepath =
	 * expandedPath+"/"+user[1]; } } catch (java.io.IOException ex) { // Just
	 * consider it unexpandable and return original path. } } return new
	 * File(filepath).getCanonicalPath(); }
	 */
}
