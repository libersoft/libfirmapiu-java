/**
 * 
 */
package it.libersoft.firmapiu.cades;

import org.bouncycastle.cms.CMSTypedData;

import it.libersoft.firmapiu.ResultInterface;
import it.libersoft.firmapiu.exception.FirmapiuException;

/**
 * Interfaccia di risposta specializzata per l'operazione di getContentSignedData
 * 
 * @author dellanna
 *
 */
interface CMSTypedDataResultInterface<K,V> extends ResultInterface<K, V> {
	/**
	 * Associa il contenuto originale dei dati a una chiave
	 * 
	 * @param key chiave 
	 * @param signedData dati firmati
	 * @throws FirmapiuException eccezione in caso di errore applicativo
	 */
	void put(K key, CMSTypedData data)throws FirmapiuException;
}
