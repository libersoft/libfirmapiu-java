/**
 * 
 */
package it.libersoft.firmapiu;

import it.libersoft.firmapiu.exception.FirmapiuException;

import java.util.Map;

/**
 * Interfaccia di comandi usata per effettuare le operazioni di firma e la verifica di un insieme di dati passati come parametro
 * 
 * @author dellanna
 *
 */
public interface CommandInterface {

	/**
	 * Firma un insieme di dati passati come parametro, restituendo l'esito dell'operazione eseguita
	 * 
	 * @param data I dati da firmare 
	 * @param option argomenti associati all'operazione da eseguire
	 * @return l'esito dell'operazione
	 * @throws FirmapiuException Se l'esecuzione del metodo ha generato un errore applicativo
	 */
	public Map<?,?> sign(Data<?> data, Argument<?,?> option) throws FirmapiuException;
	
	/**
	 * Verifica la firma di un insieme di dati passati come parametro restituendo l'esito dell'operazione eseguita
	 * 
	 * @param data I dati da verificare
	 * @param option Opzioni associate all'operazione da eseguire
	 * @return l'esito dell'operazione
	 * @throws FirmapiuException Se l'esecuzione del metodo ha generato un errore applicativo
	 */
	public Map<?,?> verify(Data<?> data, Argument<?,?> option) throws FirmapiuException;
	
	/**
	 * Restituisce il contenuto di un insieme di dati firmati
	 * 
	 * @param signedData I dati firmati (probabilmente in una busta crittografica tipo CMS, PAdes, CAdes Xades ecc...)
	 * @param option Opzioni associate all'operazione da eseguire
	 * @return Il contenuto originale dei dati firmati
	 * @throws FirmapiuException In caso di errore applicativo
	 */
	public Data<?> getContentSignedData(Data<?> signedData, Argument<?,?> option) throws FirmapiuException;
}
