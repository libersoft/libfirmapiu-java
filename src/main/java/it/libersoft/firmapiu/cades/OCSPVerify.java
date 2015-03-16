package it.libersoft.firmapiu.cades;

import it.libersoft.firmapiu.exception.FirmapiuException;
import static it.libersoft.firmapiu.exception.FirmapiuException.*;
import static it.libersoft.firmapiu.consts.FirmapiuRecordConstants.*;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Security;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DLSequence;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.CertificateID;
import org.bouncycastle.cert.ocsp.CertificateStatus;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.OCSPReq;
import org.bouncycastle.cert.ocsp.OCSPReqBuilder;
import org.bouncycastle.cert.ocsp.OCSPResp;
import org.bouncycastle.cert.ocsp.RevokedStatus;
import org.bouncycastle.cert.ocsp.SingleResp;
import org.bouncycastle.cert.ocsp.UnknownStatus;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.x509.extension.X509ExtensionUtil;

/**
 * Verifica la validit� di un certificato tramite il protocollo OCSP<br>
 * 
 * 	Classe modificata da Andrea Dell'Anna
 * 
 * @author Fabio Arieta
 * @author dellanna
 * 
 * @version 1.0
 * 
 * 
 * */
@SuppressWarnings("deprecation")
final class OCSPVerify {

	/** certificato della CA */
	private final X509Certificate issuerCertificate;
	/** certificato dell'utente */
	private final X509Certificate userCertificate;
	/** numero seriale del certificato */
	private final BigInteger userSerialNumber;
	/** nome del provider utilizzato */
	private final String providerName;
	
	/** abilito la verifica tramite OCSP */
	static {
		Security.setProperty("ocsp.enable", "true");
	}

	/** costruttore */
	OCSPVerify(X509Certificate issuerCertificate,
			X509Certificate userCertificate, String providerName) {

		this.issuerCertificate = issuerCertificate;
		this.userCertificate = userCertificate;
		this.userSerialNumber = userCertificate.getSerialNumber();
		this.providerName=providerName;
	}

	/**
	 * Genera una request
	 * 
	 * @return una request da inviare al server: {@code OCSPReq}
	 * 
	 * @throws FirmapiuException in caso di errore applicativo
	 * */
	private OCSPReq generateOCSPRequest() throws FirmapiuException{

		X509CertificateHolder certificateHolder = null;
		try {
			certificateHolder = new X509CertificateHolder(
					this.issuerCertificate.getEncoded());
		} catch (CertificateEncodingException | IOException e) {
			throw new FirmapiuException(CERT_DEFAULT_ERROR, e);
		}

		JcaDigestCalculatorProviderBuilder providerBuilder = new JcaDigestCalculatorProviderBuilder()
				.setProvider(this.providerName);
		if (providerBuilder == null) {
			String msg= FirmapiuException.getDefaultErrorCodeMessage(VERIFY_SIGNERCERT_OCSP_DEFAULTERROR);
			msg+=" : providerBuilder null!";
			throw new FirmapiuException(VERIFY_SIGNERCERT_OCSP_DEFAULTERROR,msg);
		}

		DigestCalculatorProvider digestCalculatorProv = null;
		try {
			digestCalculatorProv = providerBuilder.build();
		} catch (OperatorCreationException e) {
			throw new FirmapiuException(VERIFY_SIGNERCERT_OCSP_DEFAULTERROR,e);
		}

		DigestCalculator digestCal = null;
		try {
			digestCal = digestCalculatorProv.get(CertificateID.HASH_SHA1);
		} catch (OperatorCreationException e) {
			throw new FirmapiuException(VERIFY_SIGNERCERT_OCSP_DEFAULTERROR,e);
		}

		CertificateID id = null;
		try {
			id = new CertificateID(digestCal, certificateHolder,
					userSerialNumber);
		} catch (OCSPException e) {
			throw new FirmapiuException(VERIFY_SIGNERCERT_OCSP_DEFAULTERROR,e);
		}

		// request usando il nonce
		OCSPReqBuilder ocspGen = new OCSPReqBuilder();

		// aggiungo l'ID
		ocspGen.addRequest(id);

		// creo il nonce per evitare gli attacchi di tipo replay
		BigInteger nonce = BigInteger.valueOf(System.currentTimeMillis());

		Extension ext = new Extension(OCSPObjectIdentifiers.id_pkix_ocsp_nonce,
				true, new DEROctetString(nonce.toByteArray()));

		// aggiungo il nonce alla request
		ocspGen.setRequestExtensions(new Extensions(new Extension[] { ext }));

		// NB: la request non � firmata
		try {
			return ocspGen.build();
		} catch (OCSPException e) {
			throw new FirmapiuException(VERIFY_SIGNERCERT_OCSP_DEFAULTERROR,e);
		}
	}

	/**
	 * Ottiene una response dal server
	 * 
	 * @param serviceUrl
	 *            indirizzo URL del server da cui avere la response
	 * @param request
	 *            request creata dal client da inviare al server
	 * 
	 * @return response del server: {@code OCSPResp}
	 * */
	private OCSPResp getOCSPResponce(String serviceUrl, OCSPReq request) throws FirmapiuException{

		// verifico i parametri
		if ((request == null) || (serviceUrl == null))
			throw new NullPointerException();

		OCSPResp ocspResponse = null;
		try {

			byte[] array = request.getEncoded();

			if (serviceUrl.startsWith("http")) {

				// effettuo la connessione e imposto le propriet�
				HttpURLConnection con;
				URL url = new URL(serviceUrl);
				con = (HttpURLConnection) url.openConnection();
				con.setRequestProperty("Content-Type",
						"application/ocsp-request");
				con.setRequestProperty("Accept", "application/ocsp-response");
				con.setDoOutput(true);
				OutputStream out = con.getOutputStream();

				DataOutputStream dataOut = new DataOutputStream(
						new BufferedOutputStream(out));
				dataOut.write(array);
				dataOut.flush();
				dataOut.close();

				// ottengo la response
				InputStream in = (InputStream) con.getContent();
				ocspResponse = new OCSPResp(in);

				// verifico la response ottenuta
				if (con.getResponseCode() / 100 != 2) {
					String msg=FirmapiuException.getDefaultErrorCodeMessage(VERIFY_SIGNERCERT_OCSP_HTTPTERROR);
					msg+=" : "+con.getResponseCode();
					throw new FirmapiuException(PROTOCOL_DEFAULT_ERROR,msg);
				}

				return ocspResponse;

			} else {
				String msg=FirmapiuException.getDefaultErrorCodeMessage(PROTOCOL_DEFAULT_ERROR);
				msg+=" : "+serviceUrl;
				throw new FirmapiuException(PROTOCOL_DEFAULT_ERROR,msg);
			}
		} catch (IOException e) {
			throw new FirmapiuException(VERIFY_SIGNERCERT_OCSP_DEFAULTERROR, e);
		}
	}

	/**
	 * Restituisce lo status del certificato ottenuto mediante la verifica OCSP
	 * 
	 * @return Object che rappresenta lo status del certificato: {@code int}
	 * @throws FirmapiuException 
	 * */
	CertificateStatus getStatusCertificate() throws FirmapiuException {

		// request
		OCSPReq request = this.generateOCSPRequest();
		// url ottenuta dal certificato utente
		String serviceUrl = this.getOcspUrl(userCertificate);
		// response
		OCSPResp ocspResponse = this.getOCSPResponce(serviceUrl, request);

		// status del certificato
		return this.getStatusResponse(ocspResponse);

	}

	/**
	 * Restituisce l'URL del server per la verifica tramite OCSP inserito
	 * all'interno del certificato
	 * 
	 * @param certificate
	 *            certificato di tipo X509 da cui ottenere l'URL del server:
	 *            {@code X509Certificate}
	 * 
	 * @return URL del server per la verifica tramite OCSP: {@code String}
	 * */
	//TODO può essere un metodo interessante da esportare all'esterno?
	//TODO lanciare eccezione firmapiu?
	private String getOcspUrl(X509Certificate certificate) {

		byte[] octetBytes = certificate
				.getExtensionValue(X509Extension.authorityInfoAccess.getId());

		DLSequence dlSequence = null;
		ASN1Encodable asn1Encodable = null;

		try {
			ASN1Primitive fromExtensionValue = X509ExtensionUtil
					.fromExtensionValue(octetBytes);
			if (!(fromExtensionValue instanceof DLSequence))
				return null;
			dlSequence = (DLSequence) fromExtensionValue;
			for (int i = 0; i < dlSequence.size(); i++) {
				asn1Encodable = dlSequence.getObjectAt(i);
				if (asn1Encodable instanceof DLSequence)
					break;
			}
			if (!(asn1Encodable instanceof DLSequence))
				return null;
			dlSequence = (DLSequence) asn1Encodable;
			for (int i = 0; i < dlSequence.size(); i++) {
				asn1Encodable = dlSequence.getObjectAt(i);
				if (asn1Encodable instanceof DERTaggedObject)
					break;
			}
			if (!(asn1Encodable instanceof DERTaggedObject))
				return null;
			DERTaggedObject derTaggedObject = (DERTaggedObject) asn1Encodable;
			byte[] encoded = derTaggedObject.getEncoded();
			if (derTaggedObject.getTagNo() == 6) {
				int len = encoded[1];
				return new String(encoded, 2, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Restituisce lo status della response del server
	 * 
	 * @param ocspResponse
	 *            response del server: {@code OCSPResp}
	 * 
	 * @return Object che rappresenta lo status della response inviata dal
	 *         server: {@code Object}
	 * @throws FirmapiuException 
	 * */
	private CertificateStatus getStatusResponse(OCSPResp ocspResponse) throws FirmapiuException {

		// ottengo lo status della response
		final int status = ocspResponse.getStatus();
		//se lo status della risposta ricevuta dal responder OCSP è 0 = "successful"
		//prosegue ad analizzare la risposta riguardo lo stato del certificato richiesto
		//altrimenti lancia un errore.
		//vedi http://tools.ietf.org/html/rfc6960#section-4.2
		if (status==0) {
			BasicOCSPResp basicResponse = null;
			try {
				basicResponse = (BasicOCSPResp) ocspResponse
						.getResponseObject();
			} catch (Exception e) {
				//TODO eccezioni ammodo
				throw new FirmapiuException();
			}
			final SingleResp[] responses = basicResponse.getResponses();
			if (responses.length == 0) {
				//			System.out.println("nessuna risposta dal server!");
				//			System.err.println("OCSPVerify.getStatusResponse() "
				//					+ "response singole vuota");
				//TODO eccezioni ammodo
				throw new FirmapiuException();
			}
			if (responses.length != 1) {
				//			System.out.println("troppe response dal server!");
				//			System.err.println("OCSPVerify.getStatusResponse() "
				//					+ "troppe response singole");
				//TODO eccezioni ammodo
				throw new FirmapiuException();
			}
			final SingleResp resp = responses[0];
			final CertificateStatus status2 = resp.getCertStatus();
			return status2;
//			// certificato valido
//			if (status2 == CertificateStatus.GOOD) {
//				return CertStatus.GOOD;
//			}
//			// certificato revocato
//			if (status2 instanceof RevokedStatus) {
//				return CertStatus.REVOKED;
//			}
//			// stato del certificato sconosciuto
//			if (status2 instanceof UnknownStatus) {
//				//			System.out.println("certificato sconosciuto!");
//				//			System.err.println("OCSPVerify.getStatusResponse() "
//				//					+ "UnknowStatus");
//				return CertStatus.UNKNOWN;
//			}
		}else{
			//TODO eccezioni ammodo
			throw new FirmapiuException();
		}
	}//fine metodo

}