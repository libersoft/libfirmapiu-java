/**
 * 
 */
package it.libersoft.firmapiu;

import it.libersoft.firmapiu.exception.FirmapiuException;

import java.security.KeyStore;
import java.security.Provider;

/**
 * Questa interfaccia descrive un token crittografico utilizzato dalla libreria
 * <code>firmapiulib</code> per accedere alle credenziali (tipo chiavi private o
 * certificati) necessarie alle operazioni di firma e verifica di un insieme di
 * dati
 * 
 * @author dellanna
 *
 */
public interface CRToken {

	/**
	 * Restituisce il provider associato a questa istanza del token
	 * crittografico
	 * 
	 * @return il provider associato
	 * @throws FirmapiuException
	 *             in caso di errori di carattere applicativo
	 */
	public Provider getProvider() throws FirmapiuException;

	/**
	 * Carica il keystore associato a questa istanza del token
	 * crittografico
	 * 
	 * @param pass Il pin/password utilizzato per accedere al keystore
	 * 
	 * @return il keystore associato
	 * @throws FirmapiuException
	 *             in caso di errori di carattere applicativo
	 */
	public KeyStore loadKeyStore(char[] pass) throws FirmapiuException;	
}
