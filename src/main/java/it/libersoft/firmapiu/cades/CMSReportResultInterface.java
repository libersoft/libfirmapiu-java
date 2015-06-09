/**
 * 
 */
package it.libersoft.firmapiu.cades;

import it.libersoft.firmapiu.ResultInterface;
import it.libersoft.firmapiu.exception.FirmapiuException;

/**
 * Interfaccia di risposta specializzata per l'operazione di verifica
 * 
 * @author dellanna
 *
 */
interface CMSReportResultInterface<K> extends ResultInterface<K, CMSReport> {
	
	/**
	 * Associa il report dei dati verificati a una chiave
	 * 
	 * @param key chiave 
	 * @param verifier verifier contenente il report dell'operazione di verifica sulla chiave passata come parametro
	 * @throws FirmapiuException eccezione in caso di errore applicativo
	 */
	void put(K key, CadesBESVerifier verifier)throws FirmapiuException;
}
