/**
 * 
 */
package it.libersoft.firmapiu.consts;

/**
 * Questa classe contiene una serie di chiavi usate come campi di un record
 * contenente informazioni specifiche riguardanti la validità di una firma
 * elettronica emessa da uno specifico firmatario
 * 
 * @author dellanna
 *
 */
public final class FirmapiuRecordConstants {

	// chiavi usate nel report di risposta generato in fase
	// di verifica della validità della firma di un firmatario
	/**
	 * <b>Chiave:</b> Indica se un firmatario abbia firmato correttamente o meno
	 * i dati a lui associati <br>
	 * <b>Valore associato: Boolean/FirmapiuException -</b> TRUE Se firmatario
	 * ha firmato correttamente i dati associati<br>
	 * eccezione se è stato riscontrato un errore di carattere applicativo
	 */
	public static final String OKSIGNED = "oksigned";

	/**
	 * <b>Chiave:</b> Indica se la firma del firmatario ha valore legale secondo
	 * la DELIBERAZIONE ministeriale del N . 45 DEL 21 MAGGIO 2009<br>
	 * <b>Valore associato: Boolean/FirmapiuException -</b> TRUE Se la firma è
	 * legale<br>
	 * eccezione se è stato riscontrato un errore di carattere applicativo
	 */
	public static final String LEGALLYSIGNED = "legallysigned";

	/**
	 * <b>Chiave:</b> Indica se il controllo della catena dei certificati legati
	 * al firmatario dimostra che è attendibile <br>
	 * <b>Valore associato: Boolean/FirmapiuException -</b> TRUE Se il
	 * firmatario è affidabile<br>
	 * eccezione se è stato riscontrato un errore di carattere applicativo
	 */
	public static final String TRUSTEDSIGNER = "trustedsigner";

	/**
	 * <b>Chiave:</b> Indica lo stato del certificato del firmatario (se è stato revocato o
	 * sospeso ecc...)<br>
	 * TODO cambiare il valore associato in maniera adeguata
	 * <b>Valore associato: String/FirmapiuException -</b> String con lo stato del certificato<br>
	 * eccezione se è stato riscontrato un errore di carattere applicativo
	 * @see it.libersoft.firmapiu.consts.FirmapiuRecordConstants.CertStatus
	 * */
	public static final String SIGNERCERTSTATUS = "signercertstatus";

	/**
	 * <b>Chiave:</b> Indica se il certificato del firmatario era revocato o
	 * sospeso al momento in cui i dati sono stati firmati<br>
	 * <b>Valore associato: Boolean/FirmapiuException -</b> TRUE Se il
	 * certificato era revocato o sospeso al momento in cui i dati sono
	 * stati firmati<br>
	 * eccezione se è stato riscontrato un errore di carattere applicativo
	 * */
	public static final String SIGNERCERTREVOKED = "signercertrevoked";

	// chiavi e valori di utilità

	/**
	 * <b>Chiave:</b> Il campo identifica il firmatario <br>
	 * <b>Valore associato: org.bouncycastle.cms.SignerInformation-</b>
	 * */
	public static final String SIGNERINFO = "signerinfo";

	/**
	 * <b>Chiave:</b> Il campo indentifica la catena di certificati associata al
	 * certificato di un firmatario <br>
	 * <b>valore associato: List&lt;Certificate&gt;</b>
	 * */
	public static final String CERTCHAIN = "certchain";

	/**
	 * <b>Chiave:</b> Il campo indentifica il "trust anchor" associato al
	 * certificato di un firmatario: ossia il certificato della CA in cima alla
	 * catena di certificati che ha emesso il certificato del firmatario<br>
	 * <b>Valore associato: java.security.Certificate</b>
	 * */
	public static final String TRUSTANCHOR = "trustanchor";

	/**
	 * <b>Chiave:</b> Il campo indentifica il certificato utente del firmatario<br>
	 * <b>valore associato: java.seurity.Certificate</b>
	 * */
	public static final String SIGNERCERT = "signercert";

	// /**
	// * Contenuto dei dati firmati
	// * Valore associato alla chiave : ??
	// * */
	// public static final String CONTENTDATA="contentdata";
	
	//TODO commenti ammodo!!
	//INNER CLASS contenente le risposte degli status dei certificati se sono revocati o meno
	public static class CertStatus{
		
		/**
		 * Il certificato non è stato revocato
		 * */
		public static final String GOOD="GOOD";
		
		/**
		 * Il certificato è stato revocato
		 * */
		public static final String REVOKED="REVOKED";
		
		/**
		 * Lo stato del certificato è sconosciuto (probabilmente perchè è stato interrogato il server sbagliato)
		 * */
		public static final String UNKNOWN="UNKNOWN";
	}
}
