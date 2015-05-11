/**
 * 
 */
package it.libersoft.firmapiu.crtoken;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import it.libersoft.firmapiu.exception.FirmapiuException;
import static it.libersoft.firmapiu.consts.FactoryConsts.*;
import static it.libersoft.firmapiu.consts.FactoryPropConsts.*;

/**
 * Costruisce il token crittografico pkcs#11 (smartcard crittografica, penna usb
 * ecc ecc) utilizzato per accedere alle credenziali (certificato utente, chiave
 * privata etc...) necessarie alle operazioni messe a disposizione dalla
 * libreria <code>firmapiulib</code>
 * 
 * @author dellanna
 *
 */
public class PKCS11TokenFactory extends DefaultTokenFactory {

	// inizializza il resourcebundle per il recupero dei messaggi lanciati dalla
	// classe
	private static final ResourceBundle RB = ResourceBundle.getBundle(
			"it.libersoft.firmapiu.lang.localefactory", Locale.getDefault());

	/**
	 * Questa classe non dovrebbe essere inizializzata dal costruttore ma dalla
	 * super factory ad essa associata che inizializza le proprietà di default
	 * di questo oggetto.
	 */
	protected PKCS11TokenFactory() {
	}

	/**
	 * @see it.libersoft.firmapiu.crtoken.DefaultTokenFactory#getPKCS11Token(java.lang.String)
	 */
	@Override
	public PKCS11Token getPKCS11Token(String choice)
			throws IllegalArgumentException, FirmapiuException {
		if (choice.equals(CRTSMARTCARD)) {
			Map<String, Object> properties = this.getProperties();

			// cerca il file contenente i riferimenti ai driver utilizzati per
			// caricare il provider pkcs#11
			String pkcs11driverlocation;
			if (properties.containsKey(CRT_TOKEN_PKCS11_LIBRARYPATH))
				pkcs11driverlocation = (String) properties
						.get(CRT_TOKEN_PKCS11_LIBRARYPATH);
			else {
				ResourceBundle rb1 = ResourceBundle
						.getBundle("it.libersoft.firmapiu.properties.pkcs11driverlocation");
				pkcs11driverlocation = rb1
						.getString("linux.debian.librarypath");
			}

			// cerca la proprietà per impostare che il PIN/PUK siano formati
			// soltanto da numeri o meno
			boolean onlyNumbers = false;
			if (properties.containsKey(CRT_TOKEN_PIN_ONLYNUMBER))
				onlyNumbers = (Boolean) properties
						.get(CRT_TOKEN_PIN_ONLYNUMBER);

			// cerca le proprietà contenenti il numero massimo e minimo di
			// caratteri di cui pin/puk possono essere formati
			int minlength = 1;
			if (properties.containsKey(CRT_TOKEN_PIN_MINLENGTH))
				minlength = (Integer) properties.get(CRT_TOKEN_PIN_MINLENGTH);
			int maxlength = 8;
			if (properties.containsKey(CRT_TOKEN_PIN_MAXLENGTH))
				maxlength = (Integer) properties.get(CRT_TOKEN_PIN_MAXLENGTH);

			return new CRTSmartCardToken(pkcs11driverlocation, onlyNumbers,
					minlength, maxlength);
		} else
			throw new IllegalArgumentException(RB.getString("factoryerror3")
					+ " : " + choice);
	}
}