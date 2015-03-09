/**
 * 
 */
package it.libersoft.firmapiu.crtoken;

import it.libersoft.firmapiu.CRToken;
import it.libersoft.firmapiu.exception.FirmapiuException;

/**
 * Questa interfaccia estende l'interfaccia CRToken per supportare le funzionalità di un token
 * che crea e gestisce un keystore "software" che può essere usato dalle operazioni della libreria <code>firmapiulib</code>
 * 
 * @author dellanna
 *
 */
public interface KeyStoreToken extends CRToken {

	//TODO incubating: le firme possono essere cambiate
	/**
	 * Crea un nuovo keystore inizializzandolo
	 * */
	public void createKeyStore() throws FirmapiuException;
	
	/**
	 * Aggiorna un keystore esistente
	 * */
	public void updateKeyStore() throws FirmapiuException;
	
	/**
	 * Cancella un keystore esistente
	 * */
	public void deleteKeystore() throws FirmapiuException;
	
	
}
