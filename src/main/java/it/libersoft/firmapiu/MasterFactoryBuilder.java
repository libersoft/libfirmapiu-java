/**
 * 
 */
package it.libersoft.firmapiu;

import static it.libersoft.firmapiu.consts.FactoryConsts.*;
import it.libersoft.firmapiu.cades.CadesBESFactory;
import it.libersoft.firmapiu.crtoken.KeyStoreTokenFactory;
import it.libersoft.firmapiu.crtoken.PKCS11TokenFactory;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Master Factory che costruisce tutte le factory concrete utilizzare dalla
 * libreria firmapiu secondo il design pattern Abstract Factory
 * <p>
 * 
 * @see <a
 *      href="http://www.tutorialspoint.com/design_pattern/abstract_factory_pattern.htm">http://www.tutorialspoint.com/design_pattern/abstract_factory_pattern.htm</a>
 * 
 * @author dellanna
 *
 */
public final class MasterFactoryBuilder {
	// inizializza il resourcebundle per il recupero dei messaggi lanciati dalla
	// classe
	private final static ResourceBundle RB = ResourceBundle.getBundle(
			"it.libersoft.firmapiu.lang.localefactory", Locale.getDefault());

	/**
	 *  Seleziona la factory da creare
	 * 
	 * @param choice 
	 * @return 
	 * @see it.libersoft.firmapiu.consts.FactoryConsts
	 * @throws IllegalArgumentException Se la factory richiesta non esiste
	 */
	public static DefaultFactory getFactory(String choice) {
		if (choice.equals(CADESBESFACTORY)) {
			return new CadesBESFactory();
		}else if (choice.equals(DATAFACTORY)) {
			return new DataFactory();
		}else if (choice.equals(ARGUMENTFACTORY)) {
			return new ArgumentFactory(); 
		}else if (choice.equals(PKCS11TOKENFACTORY)) {
			return new PKCS11TokenFactory(); 
		}else if(choice.equals(KEYSTORETOKENFACTORY)){
			return new KeyStoreTokenFactory();
		}
			throw new IllegalArgumentException(RB.getString("factoryerror0")
					+ " : " + choice);
	}
}
