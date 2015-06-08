/**
 * 
 */
package it.libersoft.firmapiu.cades;

import it.libersoft.firmapiu.exception.FirmapiuException;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bouncycastle.cms.SignerInformation;

/**
 * Restituisce un report sull'operazione di verifica di un dato firmato elettronicamente in una busta pkcs7(CMS) o Cades-Bes
 * 
 * @author dellanna
 *
 */
public interface CMSReport {
	
	/**
	 * Restituisce la lista di tutti i firmatari presenti nella busta crittografica
	 * 
	 * @return la lista dei firmatari
	 * @throws FirmapiuException in caso di errore applicativo
	 */
	public List<SignerInformation> getSigners() throws FirmapiuException;
	
	/**
	 * Restituisce un set dei campi di un record contentente i dati di verifica di un firmatario associato come parametro<p>
	 * 
	 * Per maggiori informazioni sui campi dei record restituiti in fase di verifica vedi it.libersoft.firmapiu.consts.FirmapiuRecordConstants 
	 * 
	 * @param signer firmatario di cui bisogna verificare la validità 
	 * @return Set contenente i campi verificati dal firmatario
	 * @throws FirmapiuException in caso di errore applicativo
	 * 
	 * @see it.libersoft.firmapiu.consts.FirmapiuRecordConstants
	 */
	public Set<String> getSignerRecordFields(SignerInformation signer) throws FirmapiuException;
	
	/**
	 * Restituisce il valore del un campo di un record contentente i dati di verifica di un firmatario associato come parametro<p>
	 * 
	 * Per maggiori informazioni sui campi dei record restituiti in fase di verifica vedi it.libersoft.firmapiu.consts.FirmapiuRecordConstants 
	 * 
	 * @param signer firmatario di cui bisogna verificare la validità
	 * @param field campo di verifica del firmatario da controllare
	 * @return Il valore associato al campo di verifica da controllare
	 * @throws FirmapiuException in caso di errore applicativo
	 * 
	 * @see it.libersoft.firmapiu.consts.FirmapiuRecordConstants
	 */
	public Object getSignerField(SignerInformation signer,String field) throws FirmapiuException;
}
