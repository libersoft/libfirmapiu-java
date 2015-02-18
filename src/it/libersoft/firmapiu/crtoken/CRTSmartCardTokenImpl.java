/**
 * 
 */
package it.libersoft.firmapiu.crtoken;

import it.libersoft.firmapiu.exception.FirmapiuException;

import java.security.KeyStore;
import java.security.Provider;

/**
 * La classe gestisce una smartcard crittografica.<br> 
 * Mette a disposizione funzionalità per gestire la carta stessa quali pin, puk e sblocco.<br>
 * Permette l'accesso alle credenziali messe a disposizione della carta (quali certificato utente, chiave privata)
 * alle operazioni/comandi messi a disposizione della libreria <code>firmapiulib</code>
 * 
 * @author dellanna
 *
 */
class CRTSmartCardTokenImpl implements CRTSmartCardToken {

	/**
	 *La classe non dovrebbe essere inizializzata se non attraverso la factory 
	 */
	protected CRTSmartCardTokenImpl() {}

	/* (non-Javadoc)
	 * @see it.libersoft.firmapiu.CRToken#getProvider()
	 */
	@Override
	public Provider getProvider() throws FirmapiuException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see it.libersoft.firmapiu.CRToken#getKeyStore()
	 */
	@Override
	public KeyStore getKeyStore() throws FirmapiuException {
		// TODO Auto-generated method stub
		return null;
	}
	
	/* (non-Javadoc)
	 * @see it.libersoft.firmapiu.crtoken.CRTSmartCardToken#getATR()
	 */
	@Override
	public byte[] getATR() throws FirmapiuException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see it.libersoft.firmapiu.crtoken.CRTSmartCardToken#setPin(char[], char[])
	 */
	@Override
	public void setPin(char[] oldPin, char[] newPin) throws FirmapiuException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see it.libersoft.firmapiu.crtoken.CRTSmartCardToken#setPuk(char[], char[], char[])
	 */
	@Override
	public void setPuk(char[] pin, char[] oldPuk, char[] newPuk)
			throws FirmapiuException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see it.libersoft.firmapiu.crtoken.CRTSmartCardToken#unlockPKCS11Token(char[], char[])
	 */
	@Override
	public void unlockPKCS11Token(char[] pin, char[] puk)
			throws FirmapiuException {
		// TODO Auto-generated method stub

	}

}
