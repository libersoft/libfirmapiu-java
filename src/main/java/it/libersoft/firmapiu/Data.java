/**
 * 
 */
package it.libersoft.firmapiu;

import it.libersoft.firmapiu.exception.FirmapiuException;

import java.util.Map;
import java.util.Set;

/**
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
	
	//restituisce l'identificatore univoco dei tipi di dato passati come parametro
	public String getDataId(T data) throws FirmapiuException;
	
	//rappresentazione in array di byte dei dati passati come parametro
	public byte[] getArrayData(T data) throws FirmapiuException;
	
	//setta gli argomenti associati ai dati
	public void setArgument(String key,String value) throws FirmapiuException;
	
	//recupera gli argomenti associati ai dati
	public Map<String,String> getArgumentMap() throws FirmapiuException;
}
