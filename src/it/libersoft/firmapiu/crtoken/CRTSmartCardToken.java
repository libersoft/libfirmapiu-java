/**
 * 
 */
package it.libersoft.firmapiu.crtoken;

import it.libersoft.firmapiu.CRToken;
import it.libersoft.firmapiu.exception.FirmapiuException;

/**
 * Questa interfaccia estende l'interfaccia CRToken per supportare le
 * funzionalità di un token pkcs#11.<br>
 * Ossia mette a disposizione funzionalità per accedere alle credenziali di una
 * smartcard crittografica (penna usb ecc ecc) (credenziali tipo il certificato utente o la sua
 * chiave privata) utilizzate nelle operazioni della libreria
 * <code>firmapiulib.</code><br>
 * Mette a disposizione anche funzionalità per la gestione della smartcard, tipo
 * il cambio del pin, del puk o dello sblocco della carta
 * 
 * @author dellanna
 *
 */
public interface CRTSmartCardToken extends CRToken {

	// TODO da rivedere bene le firme quando si farà gestione della carta

	public byte[] getATR() throws FirmapiuException;
	
	/**
	 * Cambia il PIN della smartcard
	 * 
	 * @param oldPin
	 * @param newPin
	 * @throws FirmapiuException
	 *             in caso di errori applicativi
	 */
	public void setPin(char[] oldPin, char[] newPin) throws FirmapiuException;

	/**
	 * Cambia il PUK della smartcard
	 * 
	 * @param pin
	 * @param oldPuk
	 * @param newPuk
	 * @throws FirmapiuException
	 *             in caso di errori applicativi
	 */
	public void setPuk(char[] pin, char[] oldPuk, char[] newPuk)
			throws FirmapiuException;

	/**
	 * Sblocca la smartcard nel caso in cui questa dovesse essere bloccata<br>
	 * (Ad esempio perché si è sbagliato ad inserire il pin)
	 * 
	 * @param pin
	 * @param puk
	 *            PUK da usare per sbloccare la carta
	 * @throws FirmapiuException
	 *             in caso di errori applicativi
	 */
	public void unlockPKCS11Token(char[] pin, char[] puk)
			throws FirmapiuException;
}
