/**
 * 
 */
package it.libersoft.firmapiu;

import it.libersoft.firmapiu.exception.FirmapiuException;

import java.util.Map;
import java.util.Set;

/**
 *TODO commenti ammodo
 * 	 * @see it.libersoft.firmapiu.consts.ArgumentConsts
 * 
 * Insieme di "dati" che devono essere firmati o verificati dalle factory concrete che realizzano firmapiu.<br>
 * Le classi concrete che realizzano questa interfaccia devono definire la tipologia specifica dei dati da utilizzare
 * 
 * @author dellanna
 *
 */
public interface Data<T> {
	
	/**
	 * aggiunge dei "dati" all'insieme dei dati da firmare/verificare.<br>
	 * I dati dovrebbero essere delle "chiavi" in modo da non effettuare più volte l'operazione di firma/verifica sullo stesso dato
	 * 
	 * @param data i dati da firmare/verificare
	 * 
	 * @throws FirmapiuException In caso di errori di carattere applicativo
	 */
	public void setData(T data) throws FirmapiuException;
	
	/**
	 * @return un insieme contenente i dati che devono essere firmati/verificati dall'operazione eseguita
	 * 
	 * @throws FirmapiuException In caso di errori di carattere applicativo
	 */
	public Set<T> getDataSet() throws FirmapiuException;
	
	/**
	 * restituisce un identificatore univoco dei tipi di dato passati come parametro
	 * 
	 * @param data il dato passato come parametro
	 * @return una stringa che identifica l'identificatore univoco
	 * @throws FirmapiuException In caso di errori di carattere applicativo
	 */
	public String getDataId(T data) throws FirmapiuException;
	
	/**
	 * Restituisce una rappresentazione in array di byte dei dati passati come parametro
	 * @param data il dato passato come parametro
	 * @return Una rappresentazione del dato in array di byte
	 * @throws FirmapiuException In caso di errori di carattere applicativo
	 */
	public byte[] getArrayData(T data) throws FirmapiuException;
	
	/**
	 * Setta gli argomenti opzionali associati ai dati da passare come argomento ai comandi
	 * 
	 * @param key Il nome dell'argomento da settare
	 * @param value
	 * @throws FirmapiuException in caso di errori applicativi
	 * 
	 * @see it.libersoft.firmapiu.consts.ArgumentConsts
	 */
	public void setArgument(String key,String value) throws FirmapiuException;
	
	/**
	 * Recupera gli argomenti associati ai dati
	 * 
	 * @return una Map contenente gli argomenti settati da passare al comando
	 * @throws FirmapiuException
	 * 
	 * @see it.libersoft.firmapiu.consts.ArgumentConsts
	 */
	public Map<String,String> getArgumentMap() throws FirmapiuException;
}
