/**
 * 
 */
package it.libersoft.firmapiu.consts;

/**
 * Questa classe contiene una serie di costanti utilizzabili dalle Factories per
 * impostarne le proprietà
 * 
 * @author dellanna
 *
 */
public final class FactoryPropConsts {

	private FactoryPropConsts(){}
	
	/**
	 * <b>Chiave:</b> Token crittografico utilizzato per firmare dei dati<br>
	 * <b>Valore associato: String -</b> Viene specificato il token crittogragrico (ad
	 * esempio smartcard, penna USB etc etc...)<br>
	 * <b>Default: <i>CRT_TOKEN_PKSC11</i> -</b> di Default le factories
	 * utilizzano il token crittografico pkcs#11 per l'operazione di firma,
	 * ossia una smartcard crittografica
	 */
	public static final String CRT_TOKEN_KEY = "crtToken";

	/**
	 * Token crittografico pkcs#11. Il token crittografico utilizzato per
	 * firmare i dati è una smartcard
	 */
	public static final String CRT_TOKEN_PKCS11 = "pkcs11";

	/**
	 * <b>Chiave:</b> (Proprietà di DataFactory) I percorsi dei file/url vengono
	 * normalizzati nei confronti del loro path relativo e dei loro link
	 * simbolici, in modo da garantire che i percorsi siano delle chiavi e che
	 * non venga effettuata più volte l'operazione di firma/verifica sulla
	 * stessa risorsa <br>
	 * <b>Valore associato: Boolean -</b> Se i percorsi devono essere normalizzati o meno <br>
	 * <b>Default: <i>true</i> -</b> I percorsi vengono normalizzati
	 * */
	public static final String NORMALIZE_DATAPATH = "normalizeDatapath";
}
