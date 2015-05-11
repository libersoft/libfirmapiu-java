/**
 * 
 */
package it.libersoft.firmapiu.cades;

import org.bouncycastle.cms.CMSSignedData;

import it.libersoft.firmapiu.ResultInterface;
import it.libersoft.firmapiu.exception.FirmapiuException;

/**
 * Interfaccia di risposta specializzata per l'operazione di firma
 * 
 * @author dellanna
 *
 */
interface CMSSIgnedDataResultInterface<K,V> extends ResultInterface<K, V> {
	
	/**
	 * Associa dei dati firmati a una chiave
	 * 
	 * @param key chiave 
	 * @param signedData dati firmati
	 * @throws FirmapiuException eccezione in caso di errore applicativo
	 */
	void put(K key, CMSSignedData signedData)throws FirmapiuException;
}
