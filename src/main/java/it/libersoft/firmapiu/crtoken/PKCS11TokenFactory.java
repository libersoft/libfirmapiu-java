/**
 * 
 */
package it.libersoft.firmapiu.crtoken;

import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import it.libersoft.firmapiu.DefaultFactory;
import it.libersoft.firmapiu.exception.FirmapiuException;
import static it.libersoft.firmapiu.consts.FactoryConsts.*;
import static it.libersoft.firmapiu.consts.FactoryPropConsts.*;

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
	public PKCS11Token getToken(String choice)
			throws IllegalArgumentException,FirmapiuException {	
		if (choice.equals(CRTSMARTCARD)){
			//cerca il file contenente i riferimenti ai driver utilizzati per caricare il provider pkcs#11
			Map<String,Object> properties = this.getProperties();
		
			String pkcs11driverlocation;
			if(properties.containsKey(CRT_TOKEN_PKCS11_LIBRARYPATH))
				pkcs11driverlocation=(String)properties.get(CRT_TOKEN_PKCS11_LIBRARYPATH);
			else{
				ResourceBundle rb1 = ResourceBundle.getBundle("it.libersoft.firmapiu.properties.pkcs11driverlocation");
				pkcs11driverlocation = rb1.getString("linux.debian.librarypath");
			}
			return new CRTSmartCardToken(pkcs11driverlocation);
		}
		else
			throw new IllegalArgumentException(RB.getString("factoryerror3")
					+ " : " + choice);
	}
}
