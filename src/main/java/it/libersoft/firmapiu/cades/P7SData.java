/**
 * 
 */
package it.libersoft.firmapiu.cades;

import it.libersoft.firmapiu.Data;
import it.libersoft.firmapiu.exception.FirmapiuException;

/**
 * Questa interfaccia estende Data per fornire supporto a dati di tipo P7S.
 * <p>
 * Il generic K è una rappresentazione di una busta crittografica Cades-Bes
 * detached<br>
 * K dovrebbero essere delle chiavi per non effettuare la stessa operazione
 * sugli stessi dati più di una volta
 * <p>
 * 
 * Il generic V è una rappresentazione del contenuto originale dei dati
 * imbustati
 * 
 * @author Dellanna
 *
 */
public interface P7SData<K, V> extends Data<K> {

	/**
	 * Associa una rappresentazione di una busta crittografica Cades-Bes
	 * detached al contenuto che rappresenta
	 * 
	 * @param key
	 *            busta crittografica Cades-bes/P7S
	 * @param content
	 *            Contenuto associato al p7s
	 * @throws FirmapiuException
	 *             In caso di errori applicativi
	 */
	public void putP7SData(K key, V content) throws FirmapiuException;

	/**
	 * Restituisce il contenuto associato ad una busta crittografica Cades-bes
	 * detached
	 * 
	 * @param key
	 *            busta crittografica Cades-bes/P7S
	 * @return Contenuto associato al p7s
	 * @throws FirmapiuException
	 *             In caso di errori applicativi
	 */
	public V getP7SContent(K key) throws FirmapiuException;
}
