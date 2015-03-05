/**
 * 
 */
package it.libersoft.firmapiu.exception;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Questa classe incapsula gli errori di carattere applicativo lanciati dalla
 * libreria <code>firmapiulib</code><br>
 * La classe può essere usata per effettuare il wrapping delle eccezioni
 * sottostanti rilanciandole.<br>
 * All'eccezione <code>FirmapiuException</code> è associato un codice di errore
 * a cui corrisponde una semantica precisa definita in una tabella dei codici di
 * errore
 * 
 * @author dellanna
 *
 */
public final class FirmapiuException extends Exception {

	private static final long serialVersionUID = 1L;

	// inizializza il resourcebundle per il recupero dei messaggi lanciati dalla
	// classe
	private static final ResourceBundle RB = ResourceBundle.getBundle(
			"it.libersoft.firmapiu.lang.localerrorcodetable", Locale.getDefault());
	
	
	
	//CODICI DI ERRORE
	//ERRORI SULLE OPERAZIONI DI FIRMA E VERIFICA

	//Errori sulle operazioni di firma: codici (100-199)
	/**
	 * Errore durante l'operazione di firma: Il sistema non è riuscito a imbustare nella busta crittografica CADES-bes i dati richiesti  
	 * */
	public static final int SIGNER_CADESBES_ERROR=100;
	/**
	 * Errore se il token utilizzato non contiene un alias valido per la firma certificata
	 * conforme alla DELIBERAZIONE ministeriale del N . 45 DEL 21 MAGGIO 2009.
	 * */
	public static final int SIGNER_ALIAS_NOTFOUND=110;
	
	/**
	 * Errore se il token utilizzato contiene più di un alias valido per la firma certificata
	 * conforme alla DELIBERAZIONE ministeriale del N . 45 DEL 21 MAGGIO 2009.
	 * */
	public static final int SIGNER_ALIAS_TOOMANY=111;

	/**
	 * Errore di default durante l'operazione di firma
	 * */
	public static final int SIGNER_DEFAULT_ERROR=199;
	
	//Errori sulle operazioni di verifica: codici (200-299)

	//Errori sulle operazioni di gestione del token crittografico (300-399)
	/**
	 * Errore se il lettore per smartcard o un altro strumento utilizzato per accedere al token crittografico non è stato trovato
	 * */
	public static final int CRT_TOKENTERMINAL_NOTFOUND=300;
	
	/**
	 * Errore se la smartcart o il token crittografico richiesto non è presente
	 * */
	public static final int CRT_TOKEN_NOTFOUND=301;
	
	/**
	 * Errore se l'accesso alla smartcard o al token crittografico richiesto non è permesso
	 * */
	public static final int CRT_TOKEN_FORBIDDEN=302;

	/**
	 * Errore se il pin del token crittografico utilizzato non è presente/è stato omesso
	 * */
	public static final int CRT_TOKENPIN_ERROR=303;
	
	/**
	 * Errore se non è stato trovato il file di configurazione contente i driver per accedere al token crittografico utilizzato
	 * */
	public static final int CRT_TOKEN_CONFIGFILE_NOTFOUND=397;
	
	/**
	 * Errore se non è stato trovato il driver/libreria del token crittografico utlizzato
	 * */
	public static final int CRT_TOKEN_LIB_NOTFOUND=398;
	
	/**
	 * Errore di default in caso di un errore generico riguardante un token crittografico
	 * */
	public static final int CRT_TOKEN_DEFAULT_ERROR=399;
	
	//ERRORI GENERICI DI LIBRERIA

	//Errori di (I/O): codici (700-799)
	/**
	 * Errore se non trova il file richiesto
	 * */
	public static final int FILE_NOTFOUND=700;
	/**
	 * Errore se si cerca di creare una nuova directory ma l'operazione non è permessa.
	 * */
	public static final int MKDIR_FORBIDDEN=705;
	
	/**
	 * Il percorso del file/directory cercato non è un percorso assoluto. Non è possibile interpretare correttamente il percorso
	 * */
	public static final int IS_NOT_ABS_PATH=709;
	
	/**
	 * Errore se si cerca di ricavare il percorso di una directory, ma il percorso corrisponde ad un file e non ad una directory
	 * */
	public static final int IS_NOT_DIR=710;
	

	//Errori di certificato: codici (800-899)
	
	/**
	 * Errore nel caso che la chiave specifica richiesta al keystore non sia presente o non sia accessibile
	 * */
	public static final int CERT_KEYSTORE_KEYERROR=800;
	
	/**
	 * Errore nel caso che il certificato specifico richiesto al keystore non sia presente o non sia accessibile
	 * */
	public static final int CERT_KEYSTORE_CERTERROR=801;
	
	/**
	 * Errore nel caso in cui non è stato possibile fare l'encoding/decoding del certificato
	 * */
	public static final int CERT_ENCODING_ERROR=802;
	
	/**
	 * Errore di default in caso di un errore generico su un keystore
	 * */
	public static final int CERT_KEYSTORE_DEFAULT_ERROR=888;
	/**
	 * Errore di default in caso di un errore generico su un certificato
	 * */
	public static final int CERT_DEFAULT_ERROR=899;
	
	//Errori generici: codici (900-999)

	/**
	 * Errore di default
	 * */
	public final static int DEFAULT_ERROR = 999;

	/**
	 * Codice di errore dell'eccezione rilanciata da it.libersoft.firmapiu
	 */
	public final int errorCode;

	/**
	 * Inizializza FirmapiuException con un codice di errore, un messaggio di
	 * errore personalizzato e l'eccezione sottostante
	 *
	 * @param code
	 *            Il codice di errore associato all'eccezione
	 * @param message
	 *            definisce un messaggio di errore personalizzato sovrascrivendo
	 *            il messaggio di default associato al codice di errore
	 * @param cause
	 *            causa iniziale dell'eccezione.
	 */
	public FirmapiuException(int code, String message, Throwable cause) {
		super(message, cause);
		this.errorCode = code;
	}

	/**
	 * Inizializza FirmapiuException con un codice di errore associandogli un
	 * messaggio di default, e l'eccezione sottostante
	 * 
	 * @param code
	 *            Il codice di errore associato all'eccezione
	 * @param cause
	 *            causa iniziale dell'eccezione.
	 */
	public FirmapiuException(int code, Throwable cause) {
		this(code, RB.getString(Integer.toString(code)), cause);
	}

	/**
	 * Inizializza FirmapiuException con l'eccezione sottostante e il codice e
	 * il messaggio di errore di default
	 *
	 * @param cause
	 *            causa iniziale dell'eccezione.
	 */
	public FirmapiuException(Throwable cause) {
		this(DEFAULT_ERROR, RB.getString(Integer.toString(DEFAULT_ERROR)), cause);
	}

	/**
	 * Inizializza FirmapiuException con un codice di errore e un messaggio di
	 * errore personalizzato
	 * 
	 * @param code
	 *            Il codice di errore associato all'eccezione
	 * @param message
	 *            definisce un messaggio di errore personalizzato sovrascrivendo
	 *            il messaggio di default associato al codice di errore
	 */
	public FirmapiuException(int code, String message) {
		super(message);
		errorCode = code;
	}

	/**
	 * Inizializza firmapiuException con un codice di errore e il messaggio di
	 * default associato
	 * 
	 * @param code
	 *            Il codice di errore associato all'eccezione
	 * */
	public FirmapiuException(int code) {
		this(code, RB.getString(Integer.toString(code)));
	}

	/**
	 * Inizializza firmapiuException con il codice di errore e il messaggio di
	 * default
	 * */
	public FirmapiuException() {
		this(DEFAULT_ERROR, RB.getString(Integer.toString(DEFAULT_ERROR)));
	}
	
	/**
	 * Restituisce il messaggio di default associato al codice di errore
	 * 
	 * @param code codice di errore passato come parametro
	 * @return il messaggio di errore predefinito
	 */
	public static String getDefaultErrorCodeMessage(int code){
		return RB.getString(Integer.toString(code));
	}
}