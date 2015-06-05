/**
 * 
 */
package it.libersoft.firmapiu.data;

import it.libersoft.firmapiu.cades.P7FileCommandInterface;
import it.libersoft.firmapiu.crtoken.PKCS11Token;
import it.libersoft.firmapiu.exception.FirmapiuException;
import static it.libersoft.firmapiu.consts.FactoryPropConsts.*;
import static it.libersoft.firmapiu.consts.FactoryConsts.*;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

/**
 * Factory di default ereditata da tutte le factory concrete utilizzate dalla
 * libreria.<br>
 * Le factory sono implementate concretamente dalle classi che ereditano la
 * DefaultFactory
 * 
 * @author dellanna
 *
 */
public class DefaultDataFactory {

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
	protected DefaultDataFactory() {
		// crea la factory settando le proprietà di default
		this.propMap = new TreeMap<String, Object>();
		// il token crittografico utilizzato di default per la firma è un pkcs11
		// token (una smartcard).
		// Le factories concrete utilizzeranno questo token per implementare le
		// operazioni di firma dei dati
		// propMap.put(CRT_SIGN_TOKEN, PKCS11TOKENFACTORY);
		// // Il token crittografico utilizzato di default per la verifica di
		// dati
		// // firmati elettronicamente è un TSLXmlKeyStoreToken.
		// // Le factories concrete utilizzeranno questo token per implementare
		// le
		// // operazioni di verifica dell firma dei dati
		// propMap.put(CRT_VERIFY_TOKEN, KEYSTORETOKENFACTORY);
	}

	/**
	 * Crea un oggetto che raccoglie un insieme di una buste crittografiche o
	 * contenuti di file, rappresentati come array di byte, da firmare o
	 * verificare tramite le operazioni di firma e verifica messe a disposizione
	 * dalla libreria
	 * 
	 * @param choice
	 *            il tipo di dati concreto da creare
	 * @return
	 * @throws IllegalArgumentException
	 *             Se la factory utilizzata non implementa questo metodo
	 * @see it.libersoft.firmapiu.consts.FactoryConsts
	 */
	public DataByteArray getDataByteArray() throws IllegalArgumentException {
		throw new IllegalArgumentException(RB.getString("factoryerror1")
				+ " : " + this.getClass().getCanonicalName());
	}

	/**
	 * Crea un oggetto che raccoglie un insieme di una buste crittografiche
	 * detached o .p7s, e contenuto originale associato, rappresentati come
	 * array di byte, per le operazioni di verifica messe a disposizione dalla
	 * libreria
	 * 
	 * @param choice
	 *            il tipo di dati concreto da creare
	 * @return
	 * @throws IllegalArgumentException
	 *             Se la factory utilizzata non implementa questo metodo
	 * @see it.libersoft.firmapiu.consts.FactoryConsts
	 */
	public P7SDataByteArray getP7SDataByteArray()
			throws IllegalArgumentException {
		throw new IllegalArgumentException(RB.getString("factoryerror1")
				+ " : " + this.getClass().getCanonicalName());
	}

	/**
	 * Crea un oggetto che raccoglie un insieme di file da firmare o verificare
	 * tramite le operazioni di firma e verifica messe a disposizione dalla
	 * libreria
	 * 
	 * @param choice
	 *            il tipo di dati concreto da creare
	 * @return
	 * @throws IllegalArgumentException
	 *             Se la factory utilizzata non implementa questo metodo
	 * @see it.libersoft.firmapiu.consts.FactoryConsts
	 */
	public DataFile getDataFile() throws IllegalArgumentException {
		throw new IllegalArgumentException(RB.getString("factoryerror1")
				+ " : " + this.getClass().getCanonicalName());
	}

	/**
	 * Crea un oggetto che raccoglie un insieme di file p7s e file originale
	 * associato per le operazioni di verifica messe a disposizione dalla
	 * libreria
	 * 
	 * @param choice
	 *            il tipo di dati concreto da creare
	 * @return
	 * @throws IllegalArgumentException
	 *             Se la factory utilizzata non implementa questo metodo
	 * @see it.libersoft.firmapiu.consts.FactoryConsts
	 */
	public P7SDataFile getP7SDataFile() throws IllegalArgumentException {
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
	 * 
	 * @see it.libersoft.firmapiu.consts.FactoryPropConsts
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
	 * 
	 * @see it.libersoft.firmapiu.consts.FactoryPropConsts
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
	 * 
	 * @see it.libersoft.firmapiu.consts.FactoryPropConsts
	 */
	public Map<String, Object> getProperties() {
		return (TreeMap<String, Object>) propMap.clone();
	}
}
