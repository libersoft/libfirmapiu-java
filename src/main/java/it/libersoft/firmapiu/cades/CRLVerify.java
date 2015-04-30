package it.libersoft.firmapiu.cades;

import it.libersoft.firmapiu.exception.FirmapiuException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.X509Extensions;

/**
 * 
 * Verifica la CRL di un certificato di tipo X509
 * 
 * Classe modificata da Andrea Dell'Anna
 * 
 * @author Fabio Arieta
 * @author dellanna
 * 
 * @version 1.0
 * 
 * */
@SuppressWarnings("deprecation")
final class CRLVerify {

	/** certificato di tipo X509 */
	private final X509Certificate certificate;
	/** CRL del certificato */
	private X509CRL cRLCertificate;
	/** punti di distribuzione delle CRL */
	private List<String> crlDistPoints;

	/**
	 * costruttore della classe CRLVerify
	 * 
	 * @param certificate
	 *            certificato di tipo X509
	 */
	CRLVerify(X509Certificate certificate) {
//		if (this.certificate == null)
//			throw new NullPointerException();
		this.certificate = certificate;

	}

	/**
	 * restituisce il certificato di tipo X509
	 * 
	 * @return certificato di tipo X509: {@code X509Certificate}
	 * */
	X509Certificate getCertificate() {
		return this.certificate;
	}

	/**
	 * restituisce la CRL del certificato
	 * 
	 * @return CRL del certificato: {@code X509CRL}
	 * @throws FirmapiuException
	 */
	X509CRL getCRL() throws FirmapiuException {

		this.verifyCertificateCRLs();

		if (this.cRLCertificate == null) {

			System.err.println("CRLVerify.getCRL() " + "CRL null!");
			System.exit(1);
		}
		return this.cRLCertificate;
	}

	/**
	 * restituisce i punti di distribuzione della CRL
	 * 
	 * @return punti di distribuzione della CRL: {@code List<String>}
	 */
	List<String> getDistrPoint() throws FirmapiuException {

		return this.getCrlDistributionPoints();
	}

	/**
	 * verifica la CRL di un certificato
	 * 
	 * @return <code>true</code> se il certificato non � stato revocato;
	 *         <code>false</code> altrimenti
	 * @throws FirmapiuException
	 * */
	boolean verifyCertificateCRLs() throws FirmapiuException {

		// punti di distibuzione della CRL
		this.crlDistPoints = getCrlDistributionPoints();

		// verifico se il certificato � stato revocato
		for (String crlDP : this.crlDistPoints) {

			this.cRLCertificate = downloadCRL(crlDP);
			// certificato revocato
			if (this.cRLCertificate.isRevoked(this.certificate)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Restituisce la CRLEntry contenente lo status del certificato di cui
	 * bisogna controllare la revoca
	 *
	 * @return La CRLEntry contenente le informazioni sullo stato di revoca del
	 *         certificato se è stato revocato oppure null se il certificato non
	 *         è stato revocato
	 * @throws FirmapiuException
	 */
	X509CRLEntry getX509CRLEntry() throws FirmapiuException {
		// punti di distibuzione della CRL
		this.crlDistPoints = getCrlDistributionPoints();

		// verifico se il certificato � stato revocato
		for (String crlDP : this.crlDistPoints) {

			this.cRLCertificate = downloadCRL(crlDP);
			X509CRLEntry revokedCertEntry = this.cRLCertificate
					.getRevokedCertificate(this.certificate);
			// certificato revocato
			if (revokedCertEntry != null) {
				return revokedCertEntry;
			}
		}
		return null;
	}

	/**
	 * restituisce i punti di distribuzione della CRL del certificato
	 * 
	 * @return punti di distribuzione della CRL del certificato:
	 *         <code>List<String></code>
	 * @throws FirmapiuException
	 * */
	private List<String> getCrlDistributionPoints() throws FirmapiuException {

		// ottengo il DER-encoded octet string dall'OID passato come argomento
		byte[] crldpExt = this.certificate
				.getExtensionValue(X509Extensions.CRLDistributionPoints.getId());

		if (crldpExt == null) {
			List<String> emptyList = new ArrayList<>();
			return emptyList;
		}

		@SuppressWarnings("resource")
		ASN1InputStream oAsnInStream = new ASN1InputStream(
				new ByteArrayInputStream(crldpExt));
		ASN1Primitive derObjCrlDP = null;

		try {
			derObjCrlDP = oAsnInStream.readObject();
		} catch (IOException e) {
			// TODO eccezioni ammodo
			throw new FirmapiuException(e);
		}

		DEROctetString dosCrlDP = (DEROctetString) derObjCrlDP;
		byte[] crldpExtOctets = dosCrlDP.getOctets();

		@SuppressWarnings("resource")
		ASN1InputStream oAsnInStream2 = new ASN1InputStream(
				new ByteArrayInputStream(crldpExtOctets));
		ASN1Primitive derObj2 = null;
		try {
			derObj2 = oAsnInStream2.readObject();
		} catch (IOException e) {
			// System.err.println("CRLVerify.getCrlDistributionPoints() "
			// + "errore nell'I/O");
			// e.printStackTrace();
			// System.exit(0);
			// TODO eccezioni ammodo
			throw new FirmapiuException(e);
		}

		CRLDistPoint distPoint = CRLDistPoint.getInstance(derObj2);

		List<String> crlUrls = new ArrayList<>();

		for (DistributionPoint dp : distPoint.getDistributionPoints()) {

			DistributionPointName dpn = dp.getDistributionPoint();

			if (dpn != null) {

				if (dpn.getType() == DistributionPointName.FULL_NAME) {

					GeneralName[] genNames = GeneralNames.getInstance(
							dpn.getName()).getNames();

					for (int j = 0; j < genNames.length; j++) {

						if (genNames[j].getTagNo() == GeneralName.uniformResourceIdentifier) {

							String url = DERIA5String.getInstance(
									genNames[j].getName()).getString();
							crlUrls.add(url);
						}
					}
				}
			}
		}
		return crlUrls;
	}

	/**
	 * dato un URL scarica la CRL del certificato
	 * 
	 * @param crlURL
	 *            -URL da dove scaricare la CRL: {@code String}
	 * @return CRL del certificato: {@code X509CRL}
	 * @throws FirmapiuException
	 * 
	 * */
	private X509CRL downloadCRL(String crlURL) throws FirmapiuException {

		if (crlURL == null) {
			// System.err
			// .println("CRLVerify.downloadCRL() " + "parametro errato!");
			// System.exit(1);
			throw new NullPointerException();
		}

		X509CRL crl = null;
		if (crlURL.startsWith("http://") || crlURL.startsWith("https://")
				|| crlURL.startsWith("ftp://")) {

			crl = downloadCRLFromWeb(crlURL);
			return crl;

		} else if (crlURL.startsWith("ldap://")) {

			crl = downloadCRLFromLDAP(crlURL);
			return crl;

		} else {
			// System.err.println("CRLVerify.downloadCRL() "
			// + "errore nella ricerca della CRL!");
			// System.out.println("non � possibile scaricare la CRL dall'URL!!!");
			// System.exit(1);
			// TODO eccezioni ammodo
			throw new FirmapiuException();
		}
		// return crl;
	}

	/**
	 * dato un URL di tipo LDAP scarica la CRL del certificato
	 * 
	 * @param ldapURL
	 *            -URL di tipo LDAP: {@code String}
	 * @return CRL del certificato: {@code X509CRL}
	 * @throws FirmapiuException
	 * 
	 * */
	private X509CRL downloadCRLFromLDAP(String ldapURL)
			throws FirmapiuException {

		if (ldapURL == null) {
			// System.err.println("CRLVerify.downloadCRLFromLDAP() "
			// + "parametro null!");
			// System.exit(1);
			throw new NullPointerException();
		}

		X509CRL crl = null;
		Hashtable<String, String> env = new Hashtable<>();
		env.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, ldapURL);

		DirContext ctx = null;
		try {
			ctx = new InitialDirContext(env);
		} catch (NamingException e) {
			// System.err.println("CRLVerify.downloadCRLFromLDAP() "
			// + "errore nella risoluzione del nome!");
			// e.printStackTrace();
			// System.exit(0);
			// TODO eccezioni ammodo
			throw new FirmapiuException(e);
		}
		Attributes avals = null;
		try {
			avals = ctx.getAttributes("");
		} catch (NamingException e) {
			// System.err.println("CRLVerify.downloadCRLFromLDAP() "
			// + "errore nella risoluzione del nome!");
			// e.printStackTrace();
			// System.exit(0);
			// TODO eccezioni ammodo
			throw new FirmapiuException(e);
		}
		Attribute aval = avals.get("certificateRevocationList;binary");

		byte[] val = null;
		try {
			val = (byte[]) aval.get();
		} catch (NamingException e) {
			// System.err.println("CRLVerify.downloadCRLFromLDAP() "
			// + "errore nella risoluzione del nome!");
			// e.printStackTrace();
			// System.exit(0);
			// TODO eccezioni ammodo
			throw new FirmapiuException(e);
		}

		if ((val == null) || (val.length == 0)) {
			// System.out.println("impossibile scaricare da LDAP!!");
			// System.err.println("CRLVerify.downloadCRLFromLDAP() "
			// + "errore nel download della CRL da LDAP!");
			// System.exit(0);
			// TODO eccezioni ammodo
			throw new FirmapiuException();

		} else {

			InputStream inStream = new ByteArrayInputStream(val);
			CertificateFactory cf = null;
			try {
				cf = CertificateFactory.getInstance("X.509");
			} catch (CertificateException e) {
				// System.err.println("CRLVerify.downloadCRLFromLDAP() "
				// + "errore nel certificato!");
				// e.printStackTrace();
				// System.exit(0);
				// TODO eccezioni ammodo
				throw new FirmapiuException(e);
			}
			try {
				crl = (X509CRL) cf.generateCRL(inStream);
			} catch (CRLException e) {
				// System.err.println("CRLVerify.downloadCRLFromLDAP() "
				// + "errore nella CRL!!");
				// e.printStackTrace();
				// System.exit(0);
				// TODO eccezioni ammodo
				throw new FirmapiuException(e);
			}

		}
		return crl;
	}

	/**
	 * dato un URL scarica una CRL dal Web
	 * 
	 * @param crlURL
	 *            URL dove scaricare la CRL: {@code String}
	 * @return CRL del certificato: {@code X509CRL}
	 * @throws FirmapiuException
	 * */
	private X509CRL downloadCRLFromWeb(String crlURL) throws FirmapiuException {

		if (crlURL == null) {
			// System.err.println("CRLVerify.downloadCRLFromWeb() "
			// + "parametro null!");
			// System.exit(1);
			throw new NullPointerException();
		}

		URL url = null;
		try {
			url = new URL(crlURL);
		} catch (MalformedURLException e) {
			// System.err.println("CRLVerify.downloadCRLFromWeb() "
			// + "URL non valido!!");
			// e.printStackTrace();
			// System.exit(0);
			// TODO eccezioni ammodo
			throw new FirmapiuException(e);
		}
		InputStream crlStream = null;
		try {
			crlStream = url.openStream();
		} catch (IOException e) {
			// System.err
			// .println("CRLVerify.downloadCRLFromWeb() " + "errore I/O");
			// e.printStackTrace();
			// System.exit(0);
			// TODO eccezioni ammodo
			throw new FirmapiuException(e);
		}
		try {
			CertificateFactory cf = null;
			try {
				cf = CertificateFactory.getInstance("X.509");
			} catch (CertificateException e) {
				// System.err.println("CRLVerify.downloadCRLFromWeb() "
				// + "errore nella getInstance!");
				// e.printStackTrace();
				// System.exit(0);
				// TODO eccezioni ammodo
				throw new FirmapiuException(e);
			}
			X509CRL crl = null;
			try {
				crl = (X509CRL) cf.generateCRL(crlStream);
			} catch (CRLException e) {
				// System.err.println("CRLVerify.downloadCRLFromWeb() "
				// + "errore nella CRL!");
				// e.printStackTrace();
				// System.exit(0);
				// TODO eccezioni ammodo
				throw new FirmapiuException(e);
			}
			return crl;
		} finally {
			try {
				crlStream.close();
			} catch (IOException e) {
				System.err.println("CRLVerify.downloadCRLFromWeb() "
						+ "stream non chiuso!");
				e.printStackTrace();
			}
		}
	}

}
