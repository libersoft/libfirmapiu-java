/**
 * 
 */
package it.libersoft.firmapiu.data;

import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.cms.SignerInformation;

/**
 * Questa classe contiene un insieme di record contenenti il report dell'esito dell'intera operazione di verifica 
 * di una firma digitale su un firmatario SignerInfo definito in una busta crittografica CMS 
 * 
 * @author dellanna
 *
 */
public class SignerInfoReportData {
	
	//private final List<VerifyRecord> verifyRecordList;

	//informazioni di report legate alla verifica della firma di un sigolo firmatario
	//restituisce un Boolean se le info legate al report sono vere o meno oppure una FirmapiuException 
	//con un codice di errore in caso di errore
	
	
	//verifica che la firma sia corretta
	private final Object isCorrect;
	//verifica che la firma sia legale secondo la DELIBERAZIONE ministeriale del N . 45 DEL 21 MAGGIO 2009
	private final Object isLegal;
	//verifica l'affidabilità del firmatario tramite la catena di certificati a lui associata
	private final Object isTrustful;
	//verifica che il certificato legato al firmatario non sia stato revocato tramite CRL
	private final Object isNotCRLRevoked;
	

	
	//informazioni ausiliari utili per il report
	//firmatario SignerInfo cui si riferisce il report dell'operazione di verifica
	private final SignerInformation sigInfo;
	//catena di certificati legati al firmatario


	public SignerInfoReportData(Object isCorrect, Object isLegal,
			Object isTrustful, Object isNotCRLRevoked, SignerInformation sigInfo) {
		super();
		this.isCorrect = isCorrect;
		this.isLegal = isLegal;
		this.isTrustful = isTrustful;
		this.isNotCRLRevoked = isNotCRLRevoked;
		this.sigInfo = sigInfo;
	}


	/**
	 * @return the isCorrect
	 */
	public Object getIsCorrect() {
		return isCorrect;
	}


	/**
	 * @return the isLegal
	 */
	public Object getIsLegal() {
		return isLegal;
	}


	/**
	 * @return the isTrustful
	 */
	public Object getIsTrustful() {
		return isTrustful;
	}


	/**
	 * @return the isNotCRLRevoked
	 */
	public Object getIsNotCRLRevoked() {
		return isNotCRLRevoked;
	}
}
