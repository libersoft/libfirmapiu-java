/**
 * 
 */
package firmapiu.consts;

/**
 * Questa classe contiene una serie di costanti usate dalla libreria firmapiu
 * 
 * @author dellanna
 *
 */
public final class FirmapiuConstants {

	//chiavi usate nel report di risposta generato in fase
	//di verifica della validità della firma di un firmatario
	/**
	 * Se firmatario ha firmato correttamente i dati 
	 * valore associato alla chiave : Boolean
	 */
	public static final String OKSIGNED="oksigned";
	
	/**
	 * Se la firma del firmatario ha valore legale secondo la DELIBERAZIONE ministeriale del N . 45 DEL 21 MAGGIO 2009
	 * valore associato alla chiave : Boolean
	 */
	public static final String LEGALLYSIGNED="legallysigned";
	
	/**
	 * Se il controllo della catena dei certificati legati al firmatario dimostra che è attendibile
	 * valore associato alla chiave : Boolean
	 */
	public static final String TRUSTEDSIGNER="trustedsigner";
	
	/**
	 * Se il certificato del firmatario non è stato revocato
	 * valore associato alla chiave : Boolean
	 * */
	public static final String SIGNERISnotREVOKED="signernotrvkd";
	
	//chiavi e valori di utilità
	
	/**
	 * Identifica il firmatario
	 * valore associato alla chiave : SignerInfo
	 * */
	public static final String SIGNERINFO="signerInfo";
	
	/**
	 * Indentifica la catena di certificati associata al certificato di un firmatario
	 * valore associato: List<Certificate>
	 * */
	public static final String CERTCHAIN="certchain";
	
	/**
	 * Indentifica il "trust anchor" associato al certificato di un firmatario:
	 * ossia il certificato della CA in cima alla catena di certificati 
	 * associata al certificato del firmatario
	 * Valore associato: Certificate
	 * */
	public static final String TRUSTANCHOR="trustanchor";
	
	/**
	 * Certificato del firmatario
	 * valore associato alla chiave : Certificate
	 * */
	public static final String SIGNERCERT="signercert";
	
	/**
	 * Contenuto dei dati firmati
	 * Valore associato alla chiave : ??
	 * */
	public static final String CONTENTDATA="contentdata";
	
}
