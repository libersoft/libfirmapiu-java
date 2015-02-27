/**
 * 
 */
package it.libersoft.firmapiu;

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
	 */
	public void setData(T data);
	
	/**
	 * @return un insieme contenente i dati che devono essere firmati/verificati dall'operazione eseguita
	 */
	public Set<T> getDataSet();
}
