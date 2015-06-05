/**
 * 
 */
package it.libersoft.firmapiu.data;

import static it.libersoft.firmapiu.consts.FactoryConsts.*;
import it.libersoft.firmapiu.cades.CadesBESFactory;
import it.libersoft.firmapiu.crtoken.KeyStoreTokenFactory;
import it.libersoft.firmapiu.crtoken.PKCS11TokenFactory;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Data Factory che costruisce tutte le factory concrete utilizzare dalla
 * libreria firmapiu per la creazione delle strutture dati utilizzate 
 * dalle operazioni di firma e verifica dei dati
 * <p>
 * 
 * @see <a
 *      href="http://www.tutorialspoint.com/design_pattern/abstract_factory_pattern.htm">http://www.tutorialspoint.com/design_pattern/abstract_factory_pattern.htm</a>
 * 
 * @author dellanna
 *
 */
public final class DataFactoryBuilder {
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
	public static DefaultDataFactory getFactory(String choice) {
		if (choice.equals(DATAFILEFACTORY)) {
			return new DataFileFactory();
		}else if (choice.equals(DATABYTEARRAYFACTORY)) {
			return new DataByteArrayFactory(); 
		}else if (choice.equals(P7SDATAFILEFACTORY)) {
			return new P7SDataFileFactory(); 
		}else if (choice.equals(P7SDATABYTEARRAYFACTORY)) {
			return new P7SDataByteArrayFactory(); 
		}
			throw new IllegalArgumentException(RB.getString("factoryerror0")
					+ " : " + choice);
	}
}