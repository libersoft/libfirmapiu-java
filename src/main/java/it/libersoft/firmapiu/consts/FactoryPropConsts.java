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

	private FactoryPropConsts() {
	}

	/**
	 * <b>Chiave:</b> Token crittografico utilizzato per firmare dei dati<br>
	 * <b>Valore associato: String -</b> Viene specificata la factory utilizzata
	 * per creare il token crittografico <br>
	 * <b>Default: <i>PKCS11TOKENFACTORY</i> -</b> di Default viene utilizzata
	 * la factory per creare un token crittografico pkcs#11 (ad esempio
	 * smartcard, penna USB etc etc...)
	 */
	public static final String CRT_SIGN_TOKEN = "crtToken";

	/**
	 * <b>Chiave:</b> Token crittografico utilizzato per la verifica di dati
	 * firmati elettronicamente<br>
	 * <b>Valore associato: String -</b> Viene specificata la factory utilizzata
	 * per creare il token crittografico <br>
	 * */
	public static final String CRT_VERIFY_TOKEN = "crtVerifyToken";

	// /**
	// * Token crittografico pkcs#11. Il token crittografico utilizzato per
	// * firmare i dati è una smartcard
	// */
	// public static final String CRT_TOKEN_PKCS11 = "pkcs11";

	/**
	 * <b>Chiave:</b> file contenente i riferimenti ai driver delle smartcard
	 * che possono essere utilizzate per caricare il provider pkcs11
	 * corrispondente<br>
	 * <b>Valore associato: String -</b> Viene specificato il percorso del file
	 * <b>Default: <i> se la proprietà non è definita viene utilizzato il
	 * percorso di default di sistema per i file di configurazione (In questo
	 * caso <code>/etc/firmapiulib/pkcs11driver.properties</code>)
	 */
	public static final String CRT_TOKEN_PKCS11_LIBRARYPATH = "pkcs11librarypath";

	/**
	 * <b>Chiave:</b> file di configurazione utilizzato per inizializzare e
	 * caricare un keystore da una Trust Service status List<br>
	 * <b>Valore associato: String -</b> Viene specificato il percorso del file
	 * <b>Default: <i> se la proprietà non è definita viene utilizzato il
	 * percorso di default di sistema per i file di configurazione (In questo
	 * caso <code>/etc/firmapiulib/tslkeystoreconfig.properties</code>)
	 * */
	public static final String CRT_TOKEN_TSLXMLKEYSTORE_CONFIGFILEPATH = "tslxmlkeystoreconfigfilepath";

	/**
	 * <b>Chiave:</b> (Proprietà di DataFactory) I percorsi dei file/url vengono
	 * normalizzati nei confronti del loro path relativo e dei loro link
	 * simbolici, in modo da garantire che i percorsi siano delle chiavi e che
	 * non venga effettuata più volte l'operazione di firma/verifica sulla
	 * stessa risorsa <br>
	 * <b>Valore associato: Boolean -</b> Se i percorsi devono essere
	 * normalizzati o meno <br>
	 * <b>Default: <i>true</i> -</b> I percorsi vengono normalizzati
	 * */
	public static final String NORMALIZE_DATAPATH = "normalizeDatapath";

}
