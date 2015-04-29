/**
 * 
 */
package it.libersoft.firmapiu.crtoken;

import it.libersoft.firmapiu.crtoken.PKCS11Token;
import it.libersoft.firmapiu.exception.FirmapiuException;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

/**
 * Factory di default ereditata da tutte le factory concrete utilizzate dalla
 * libreria.<br>
 * Le factory sono implementate concretamente dalle classi che ereditano la
 * DefaultTokenFactory
 * 
 * @author dellanna
 *
 */
public class DefaultTokenFactory {

	// inizializza il resourcebundle per il recupero dei messaggi lanciati dalla
	// classe
	private static final ResourceBundle RB = ResourceBundle.getBundle(
			"it.libersoft.firmapiu.lang.localefactory", Locale.getDefault());

	// mappa delle proprietà caricate dalla factory
	private final TreeMap<String, Object> propMap;

	/**
	 * Crea la Factory settando le proprietà di default degli oggetti che
	 * saranno creati
	 */
	protected DefaultTokenFactory() {
		// crea la factory settando le proprietà di default
		this.propMap = new TreeMap<String, Object>();
	}

	/**
	 * Crea un PKCS11Token per gestire le operazioni messe a disposizione da un
	 * token pkcs11. (tipo smartcard, penne usb ecc ecc...)
	 * 
	 * @param choice
	 *            il tipo di dati concreto da creare
	 * @return
	 * @throws IllegalArgumentException
	 *             Se la factory utilizzata non implementa questo metodo
	 * @see it.libersoft.firmapiu.consts.FactoryConsts
	 */
	public PKCS11Token getPKCS11Token(String choice)
			throws IllegalArgumentException, FirmapiuException {
		throw new IllegalArgumentException(RB.getString("factoryerror1")
				+ " : " + this.getClass().getCanonicalName());
	}

	/**
	 * Crea un KeyStoreToken per gestire le operazioni messe a disposizione da
	 * un token "software" che gestisce un KeyStore contenente dati
	 * crittografici. (tipo certificati, chiavi pubbliche, private ecc...)
	 * 
	 * @param choice
	 *            il tipo di dati concreto da creare
	 * @return
	 * @throws IllegalArgumentException
	 *             Se la factory utilizzata non implementa questo metodo
	 * @see it.libersoft.firmapiu.consts.FactoryConsts
	 */
	public KeyStoreToken getKeyStoreToken(String choice)
			throws IllegalArgumentException, FirmapiuException {
		throw new IllegalArgumentException(RB.getString("factoryerror1")
				+ " : " + this.getClass().getCanonicalName());
	}

	/**
	 * Setta o sovrascrive una proprietà della factory. Questo comporta che gli
	 * oggetti saranno creati dalla factory secondo questa proprietà<br>
	 * Per una lista delle proprietà e dei valori supportati da questa factory
	 * vedi i constant fields di questa classe
	 * 
	 * @param key
	 * @param value
	 */
	public void setProperty(String key, Object value) {
		this.propMap.put(key, value);
	}

	/**
	 * Restituisce il valore di una proprietà di questa factory
	 * 
	 * @param key
	 *            chiave della proprietà di cui si vuole ottenere il valore:
	 *            vedi i constant fields di questa classe
	 * @return
	 */
	public Object getProperty(String key) {
		return this.propMap.get(key);
	}

	/**
	 * Restituisce tutte le proprietà settate da questa Factory: vedi i constant
	 * fields di questa classe
	 * 
	 * @return Una map contenete tutte le associazioni &lt;chiave,valore&gt;
	 *         delle proprietà settate da questa classe
	 */
	public Map<String, Object> getProperties() {
		return (TreeMap<String, Object>) propMap.clone();
	}
}
