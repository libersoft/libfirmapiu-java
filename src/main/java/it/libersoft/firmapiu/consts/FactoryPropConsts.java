/**
 * 
 */
package it.libersoft.firmapiu.consts;

/**
 * Questa classe contiene una serie di costanti utilizzabili dai metodi
 * setProperty delle Factories per impostarne le proprietà
 * 
 * @author dellanna
 *
 */
public final class FactoryPropConsts {

	private FactoryPropConsts() {
	}

	/**
	 * <b>Chiave:</b> Token crittografico utilizzato per firmare dei dati<br>
	 * <b>Valore associato: String -</b> 
	 * TODO semantica cambiata nella versione della libreria con le firme nuove, togliere questo commento se tutto va bene
	 * Cerca di creare il token usando il fully qualified name della classe o della factory definita nel valore di questa proprietà. 
	 * La classe deve avere un cotruttore senza parametri e deve implementare l'interfaccia CRTToken oppure deve essere una delle 
	 * factories definite dalla libreria per la creazione di token 
	 */
	public static final String CRT_SIGN_TOKEN = "crtToken";

	/**
	 * <b>Chiave:</b> Token crittografico utilizzato per la verifica di dati
	 * firmati elettronicamente<br>
	 * <b>Valore associato: String -</b> 
	 * TODO semantica cambiata nella versione della libreria con le firme nuove, togliere questo commento se tutto va bene
	 * Cerca di creare il token usando il fully qualified name della classe o della factory definita nel valore di questa proprietà. 
	 * La classe deve avere un cotruttore senza parametri e deve implementare l'interfaccia CRTToken oppure deve essere una delle 
	 * factories definite dalla libreria per la creazione di token
	 * <br>
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
	 * <b>Chiave:</b> Proprietà utilizzata per impostare il fatto che il pin del
	 * token può essere formato solo da numeri<br>
	 * <b>Valore associato: java.lang.Boolean -</b> true - Il pin del token può
	 * essere formato solo da numeri. false - Il pin può essere formato anche da
	 * altri caratteri oltre che numeri. <b>Default: Se la proprietà non viene
	 * definita il pin del token può essere formato da qualsiasi carattere
	 * */
	public static final String CRT_TOKEN_PIN_ONLYNUMBER = "crtTokenPINonlyNumber";

	/**
	 * <b>Chiave:</b> Proprietà utilizzata per impostare il numero minimo di
	 * caratteri che pin del token deve contenere<br>
	 * <b>Valore associato: java.lang.Integer -</b> <b>Default: Se la proprietà
	 * non viene definita il valore è =1. Se il valore esiste ed è <1 o >8 di
	 * default il valore viene impostato a 1.
	 * */
	public static final String CRT_TOKEN_PIN_MINLENGTH = "crtTokenPINMinLength";

	/**
	 * <b>Chiave:</b> Proprietà utilizzata per impostare il numero massimo di
	 * caratteri che pin del token deve contenere<br>
	 * <b>Valore associato: java.lang.Integer -</b> <b>Default: Se la proprietà
	 * non viene definita il valore è =8. Se il valore esiste ed è <1 o >8 di
	 * default il valore viene impostato a 8.
	 * */
	public static final String CRT_TOKEN_PIN_MAXLENGTH = "crtTokenPINMaxLength";

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

	/**
	 * <b>Chiave:</b> (Proprietà di CadesBESFactory) DigestCalculatorProvider
	 * utilizzato dalle API BOuncy Castles nell'operazione di firma. Questa
	 * proprietà serve se si vuole imporre a BC di usare un DigestCalculator
	 * diverso da quello solitamente utilizzato. (Ad esempio se si vuole passare
	 * a BC un digest già precomputato) <b>Valore associato: String -</b> il
	 * fully quilified name della classe utilizzata come
	 * DigestCalculatorProvider (Ad esempio
	 * it.libersoft.firmapiu.util.NoSHA256DigestCalculatorProvider se si vuole
	 * passare alle Bouncy Castle un digest SHA-256 già precomputato)
	 * <b>Default: <i> Se la proprietà non è definita BC usa il digest calulator
	 * standard
	 * 
	 * */
	public static final String DIGEST_CALCULATOR_PROVIDER = "digestCalculatorProvider";

}
