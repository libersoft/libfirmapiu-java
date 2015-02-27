/**
 * 
 */
package it.libersoft.firmapiu.crtoken;

import java.util.Locale;
import java.util.ResourceBundle;

import it.libersoft.firmapiu.DefaultFactory;
import static it.libersoft.firmapiu.consts.FactoryConsts.*;

/**
 * Costruisce il token crittografico pkcs#11 (smartcard crittografica, penna usb ecc ecc)
 * utilizzato per accedere alle credenziali (certificato utente, chiave privata
 * etc...) necessarie alle operazioni messe a disposizione dalla libreria
 * <code>firmapiulib</code>
 * 
 * @author dellanna
 *
 */
public class PKCS11TokenFactory extends DefaultFactory {

	// inizializza il resourcebundle per il recupero dei messaggi lanciati dalla
	// classe
	private static final ResourceBundle RB = ResourceBundle.getBundle(
			"it.libersoft.firmapiu.lang.localefactory", Locale.getDefault());
	
	/**
	 * Questa classe non dovrebbe essere inizializzata dal costruttore ma dalla
	 * super factory ad essa associata che inizializza le proprietà di default
	 * di questo oggetto.
	 */
	public PKCS11TokenFactory() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see it.libersoft.firmapiu.DefaultFactory#getPKCS11Token(java.lang.String)
	 */
	@Override
	public PKCS11Token getPKCS11Token(String choice)
			throws IllegalArgumentException {		
		if (choice.equals(CRTSMARTCARD))
			return new CRTSmartCardToken();
		else
			throw new IllegalArgumentException(RB.getString("factoryerror3")
					+ " : " + choice);
	}
}
