/**
 * 
 */
package it.libersoft.firmapiu;

import it.libersoft.firmapiu.cades.CMSReport;
import it.libersoft.firmapiu.exception.FirmapiuException;

import java.util.Map;

/**
 * Interfaccia di comandi usata per effettuare le operazioni di firma e la verifica di un insieme di dati passati come parametro
 * 
 * @author dellanna
 *
 * @param <K> Parametro utilizzato per parametrizzare i tipi di chiavi utilizzate da Data<K>
 * @param <V> Parametro utilizzato per parametrizzare i tipi di dati dei valori restituiti da ResultInterface<K,V>
 */
public interface CommandInterface<K,V> {

	/**
	 * Firma un insieme di dati passati come parametro, restituendo l'esito dell'operazione eseguita
	 * 
	 * @param data I dati da firmare<br> 
	 * (I dati da firmare dovrebbero essere delle chiavi univoche, per non firmare più di una volta gli stessi dati)
	 * @return l'esito dell'operazione per ogni dato da firmare
	 * @throws FirmapiuException Se l'esecuzione del metodo ha generato un errore applicativo
	 */
	public ResultInterface<K,V> sign(Data<K> data) throws FirmapiuException;
	
	/**
	 * Verifica la firma di un insieme di dati passati come parametro restituendo l'esito dell'operazione eseguita
	 * 
	 * @param data I dati da verificare
	 * (I dati da verificare dovrebbero essere delle chiavi univoche, per non firmare più di una volta gli stessi dati)
	 * @return ? Dovrebbe essere una classe che rappresenta un report di verifica per ogni dato da verificare
	 * @throws FirmapiuException Se l'esecuzione del metodo ha generato un errore applicativo
	 */
	public ResultInterface<K,?> verify(Data<K> data) throws FirmapiuException;
	
	/**
	 * Restituisce il contenuto di un insieme di dati firmati
	 * 
	 * @param signedData I dati firmati (probabilmente in una busta crittografica tipo CMS, PAdes, CAdes Xades ecc...)
	 * (I dati dovrebbero essere delle chiavi univoche, per non effettuare l'operazione più di una volta gli stessi dati)
	 * @return Il contenuto originale dei dati firmati per ogni chiave passata come paramtero
	 * @throws FirmapiuException In caso di errore applicativo
	 */
	public ResultInterface<K,V> getContentSignedData(Data<K> signedData) throws FirmapiuException;
	
	/**
	 * @return il token crittografico utilizzato nell'operazione di firma
	 * @throws FirmapiuException
	 */
	public CRToken getSignToken() throws FirmapiuException;
	
	/**
	 * @return il token crittografico utilizzato nell'operazione di verifica
	 * @throws FirmapiuException
	 */
	public CRToken getVerifyCrToken() throws FirmapiuException;
}
