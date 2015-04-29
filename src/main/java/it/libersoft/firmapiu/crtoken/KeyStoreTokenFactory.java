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
 * Questa factory mette a disposizione un token crittografico "software" per la
 * creazione e la gestione di un keystore che può essere usato dalle operazioni
 * (firma/verifica) messe a disposizione dalla libreria <code>firmapiulib</code>
 * 
 * @author dellanna
 *
 */
public final class KeyStoreTokenFactory extends DefaultTokenFactory {

	// inizializza il resourcebundle per il recupero dei messaggi lanciati dalla
	// classe
	private static final ResourceBundle RB = ResourceBundle.getBundle(
			"it.libersoft.firmapiu.lang.localefactory", Locale.getDefault());

	/**
	 * Questa classe non dovrebbe essere inizializzata dal costruttore ma dalla
	 * super factory ad essa associata che inizializza le proprietà di default
	 * di questo oggetto.
	 */
	protected KeyStoreTokenFactory() {
	}

	/**
	 * 
	 * @see it.libersoft.firmapiu.DefaultTokenFactory#getKeyStoreToken(java.lang.String)
	 */
	@Override
	public KeyStoreToken getKeyStoreToken(String choice)
			throws IllegalArgumentException, FirmapiuException {
		if (choice.equals(TSLXMLKEYSTORE)) {
			// crea un token TSLXmlKeyStoreToken prendendo un file di
			// configurazione come parametro
			// cerca il file contenente i riferimenti al file di configurazione
			// utilizzato per inizializzare un keystore da Trust Service status
			// List
			Map<String, Object> properties = this.getProperties();

			String tslConfigFileLocation;
			if (properties.containsKey(CRT_TOKEN_TSLXMLKEYSTORE_CONFIGFILEPATH))
				tslConfigFileLocation = (String) properties
						.get(CRT_TOKEN_TSLXMLKEYSTORE_CONFIGFILEPATH);
			else {
				ResourceBundle rb1 = ResourceBundle
						.getBundle("it.libersoft.firmapiu.properties.tslkeystoreconfigfilelocation");
				tslConfigFileLocation = rb1.getString("linux.debian.path");
			}
			return new TSLXmlKeyStoreToken(tslConfigFileLocation);
		} else
			throw new IllegalArgumentException(RB.getString("factoryerror3")
					+ " : " + choice);
	}
}
