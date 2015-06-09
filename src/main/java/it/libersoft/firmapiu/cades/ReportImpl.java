/**
 * 
 */
package it.libersoft.firmapiu.cades;

import it.libersoft.firmapiu.exception.FirmapiuException;

import java.util.List;
import java.util.Set;

import org.bouncycastle.cms.SignerInformation;

/**
 * Implementazione di package di CMSReport
 * 
 * @author dellanna
 *
 */
class ReportImpl implements CMSReport{
	private final CadesBESVerifier verifier;
	
	protected ReportImpl(CadesBESVerifier verifier) {
		this.verifier = verifier;
	}

	@Override
	public List<SignerInformation> getSigners() throws FirmapiuException {
		return verifier.getAllSigners();
	}

	@Override
	public Set<String> getSignerRecordFields(SignerInformation signer)
			throws FirmapiuException {
		return this.verifier.verifySigner(signer).keySet();
	}

	@Override
	public Object getSignerField(SignerInformation signer, String field)
			throws FirmapiuException {
		Object fieldValue=this.verifier.verifySignerField(signer, field);
		if(fieldValue == null)
			return null;
		else if(fieldValue instanceof FirmapiuException)
			throw (FirmapiuException)fieldValue;
		else 
			return fieldValue;
	}
}//fine reportImpl
