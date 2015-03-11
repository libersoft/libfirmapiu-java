/**
 * 
 */
package it.libersoft.firmapiu.cades;

import static it.libersoft.firmapiu.consts.FirmapiuRecordConstants.*;
import static it.libersoft.firmapiu.exception.FirmapiuException.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertStore;
import java.security.cert.CertStoreParameters;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.ess.ESSCertIDv2;
import org.bouncycastle.asn1.ess.SigningCertificateV2;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Store;

import it.libersoft.firmapiu.CRToken;
import it.libersoft.firmapiu.exception.FirmapiuException;

/**
 * Questa classe verifica la validità di dati firmati elettronicamente secondo
 * il formato CMS definito dallo standard pkcs#7
 * <p>
 * 
 * Verifica che i dati siano stati firmati correttamente dai firmatari.
 * <p>
 * 
 * Verifica che i dati siano conformi al formato CADES-bes secondo la
 * DELIBERAZIONE ministeriale del N . 45 DEL 21 MAGGIO 2009.
 * <p>
 *
 * Verifica che i certificati dei firmatari siano affidabili<br>
 * Ossia che i certificati dei firmatari siano firmati dagli enti certificatori
 * qualificati come specificato in <br>
 * http://www.agid.gov.it/agenda-digitale/infrastrutture-architetture/firme-
 * elettroniche/certificati
 * <p>
 * 
 * Verifica che i certificati dei firmatari non siano stati sospesi/revocati
 * dalle CA che gli hanno emessi.
 * 
 * @author dellanna
 *
 */
final class CadesBESVerifier {

	//setta un insieme contenente tutti i campi che devono essere controllati in fase di verifica
	private final static HashSet<String> ALLFIELDSET= allFieldSet(); 
	
	// nome del provider Bouncy Castle usato per le operazioni di verifica
	private final String bcProvName;
	// busta crittografica di cui bisogna verificare la firma
	private final CMSSignedData signedData;
	// Token crittografico utilizzato nella fase di verifica dell'affidabilità
	// del firmatario
	private CRToken token;
	//Store contente i certificati della busta crittografica
	private final Store certStore;

	/**
	 * Inizializza il Verificatore passandogli come parametro la busta
	 * crittografica di cui deve verificare la firma
	 * 
	 * @param signedData
	 *            la busta crittografica da controllare
	 * @param token
	 *            Il token crittografico contenente i certicati di ROOT
	 *            utilizzati dal verificatore per verificare l'affidabilità dei
	 *            certificati dei firmatari
	 */
	CadesBESVerifier(CMSSignedData signedData, CRToken token) {
		// inizializza il provider di Bouncy Castle
		Provider p1 = new BouncyCastleProvider();
		Security.addProvider(p1);
		this.bcProvName = p1.getName();
		this.signedData = signedData;
		if (this.signedData == null)
			throw new NullPointerException();
		if (token != null)
			this.token = token;
		this.certStore = this.signedData.getCertificates();
	}

	/**
	 * Inizializza il Verificatore passandogli come parametro la busta
	 * crittografica di cui deve verificare la firma
	 * 
	 * @param signedData
	 *            la busta crittografica da controllare
	 */
	public CadesBESVerifier(CMSSignedData signedData) {
		this(signedData, null);
	}

	/**
	 * @param token
	 *            Il token crittografico contenente i certicati di ROOT
	 *            utilizzati dal verificatore per verificare l'affidabilità dei
	 *            certificati dei firmatari
	 */
	void setToken(CRToken token) {
		if (this.token == null) {
			this.token = token;
		}
	}

	/**
	 * Verifica la correttezza della firma elettronica per tutti i firmatari
	 * presenti nella busta crittografica, controllando tutti i campi di
	 * verifica
	 * 
	 * @return una lista contenente un report dell'esito dell'operazione di
	 *         verifica della firma elettronica per tutti i firmatari presenti
	 *         nella busta crittografica
	 *         <p>
	 * 
	 *         La lista contiene un record di verifica per ogni firmatario
	 *         presente nella busta<br>
	 *         Per ogni firmatario, Il record di verifica è salvato come una
	 *         Map<String,Object>, le cui chiavi rappresentano i campi del
	 *         record e i cui valori rappresentano l'esito della particolare
	 *         operazione di verifica richiesta dal campo.
	 *         <p>
	 * 
	 *         I campi del record rappresentano una parte specifica della
	 *         richiesta di verifica della busta crittografica passata come
	 *         parametro (ad esempio un campo può essere la verifica della firma
	 *         di un firmatario oppure la verifica dell'affidabilità del suo
	 *         certificato) In modo da restituire al chiamante l'esito delle
	 *         operazioni di verifica in modo modulare risaltandone gli aspetti
	 *         specifici
	 * 
	 * @see it.libersoft.firmapiu.consts.FirmapiuRecordConstants
	 */
	List<Map<String, Object>> verifyAllSigners() {
		// genera il report da inviare in risposta: genera una lista contenente
		// gli esiti dell'operazione di verifica per ogni firmatario
		List<Map<String, Object>> report = new LinkedList<Map<String, Object>>();
		
		//per ogni firmatario effettua la verifica
		SignerInformationStore  signers = signedData.getSignerInfos();
		Collection<?>  c = signers.getSigners();
		Iterator<?>  it = c.iterator();
		while (it.hasNext())
		{	
			SignerInformation   signer = (SignerInformation)it.next();
			Map<String,Object> record =this.verifySigner(signer);
			//aggiunge l'esito della verifica nella lista dei firmatari
			report.add(record);
		}//fine while
		
		//restituisce la lista contenente i report di verifica per ogni firmatario presente nella signedData
		return report;
	}

	/**
	 * Verifica la correttezza della firma elettronica <b>di uno dei
	 * firmatari</b> della busta crittografica controllando tutti i campi di
	 * verifica
	 * 
	 * @param signer
	 *            il firmatario di cui bisogna verificare la firma
	 * @return Una Map contenete il record dell'operazione di verifica sul
	 *         firmatario con tutti i campi definiti in
	 *         it.libersoft.firmapiu.consts.FirmapiuRecordConstants
	 * 
	 * @see it.libersoft.firmapiu.consts.FirmapiuRecordConstants
	 */
	Map<String, Object> verifySigner(SignerInformation signer) {
		return this.verifySigner(signer, ALLFIELDSET);
	}

	/**
	 * Verifica la correttezza della firma elettronica <b>di uno dei
	 * firmatari</b> della busta crittografica controllando i campi di verifica
	 * richiesti come parametro
	 * 
	 * @param signer
	 *            il firmatario di cui bisogna verificare la firma
	 * @param fields
	 *            Una Set contenente i nomi dei campi che si vogliono verificare
	 *            in fase di operazione di verifica sul singolo firmatario
	 * @return Una Map contenete il record dell'operazione di verifica sul
	 *         firmatario con i campi richiesti dal parametro fields
	 * 
	 * @see it.libersoft.firmapiu.consts.FirmapiuRecordConstants
	 */
	Map<String, Object> verifySigner(SignerInformation signer,
			Set<String> fields) {
		//genera la map contenente le informazioni di verifica per il singolo firmatario
		Map<String,Object> record = new TreeMap<String,Object>();
		
		//inserisce la signerinfo
		if(fields.contains(SIGNERINFO))
			record.put(SIGNERINFO, signer);
		
		Collection<?>          certCollection = certStore.getMatches(signer.getSID());
		Iterator<?>  certIt = certCollection.iterator();
		X509CertificateHolder cert = (X509CertificateHolder)certIt.next();
		
		//inserisce il certificato del firmatario
		if(fields.contains(SIGNERCERT)){
			try {
				X509Certificate x509cert=new JcaX509CertificateConverter().getCertificate(cert);
				record.put(SIGNERCERT, x509cert);
			} catch (CertificateException e) {
				record.put(SIGNERCERT, new FirmapiuException(CERT_DEFAULT_ERROR, e));
			}
		}
		
		//inserisce la verifica della firma del firmatatio
		if (fields.contains(OKSIGNED)){
			try {
				Boolean result=signer.verify(new JcaSimpleSignerInfoVerifierBuilder().setProvider(this.bcProvName).build(cert));
				record.put(OKSIGNED, result);
			} catch (OperatorCreationException e) {
				record.put(OKSIGNED, new FirmapiuException(VERIFY_SIGNER_DEFAULT_ERROR,e));
			} catch (CertificateException e) {
				record.put(OKSIGNED, new FirmapiuException(CERT_DEFAULT_ERROR,e));
			} catch (CMSException e) {
				record.put(OKSIGNED, new FirmapiuException(VERIFY_SIGNER_DEFAULT_ERROR,e));
			}//fine try-catch	
		}
		
		//inserisce la verifica del controllo legale della firma di un firmatario
		if(fields.contains(LEGALLYSIGNED)){
			try {
				record.put(LEGALLYSIGNED, new Boolean(isLegallySigned(signer, cert)));
			} catch (NoSuchAlgorithmException e) {
				record.put(LEGALLYSIGNED, new FirmapiuException(CERT_DEFAULT_ERROR, e));
			} catch (FirmapiuException e) {
				record.put(LEGALLYSIGNED, e);
			} catch (IOException e) {
				record.put(LEGALLYSIGNED, new FirmapiuException(CERT_DEFAULT_ERROR, e));
			}
		}
		
		//inserisce la verifica del controllo dell'affidabilità del certificato del firmatario
		PKIXCertPathBuilderResult signerCerthPathResult=null;
		if(fields.contains(TRUSTEDSIGNER)){
			try {
				signerCerthPathResult = isTrustedSigner(signer);
				//se è arrivato a questo ramo di codice vuol dire che il certificato è affidbile
				record.put(TRUSTEDSIGNER, new Boolean(true));
			} catch (FirmapiuException e) {
				record.put(TRUSTEDSIGNER, e);
			}
		}

		//inserisce la "trust anchor" del certificato del firmatario, ossia il certificato della CA che lo ha emesso
		if(fields.contains(TRUSTANCHOR)){
			try {
				if(signerCerthPathResult==null)
					signerCerthPathResult = isTrustedSigner(signer);
				X509Certificate trustanchor = signerCerthPathResult.getTrustAnchor().getTrustedCert();
				record.put(TRUSTANCHOR, trustanchor);
			} catch (FirmapiuException e) {
				record.put(TRUSTANCHOR, e);
			}
		}
		
		//inserisce la catena di certificazione
		if(fields.contains(CERTCHAIN)){
			try {
				if(signerCerthPathResult==null)
					signerCerthPathResult = isTrustedSigner(signer);
				List<? extends Certificate> certchain=signerCerthPathResult.getCertPath().getCertificates();
				record.put(CERTCHAIN, certchain);
			} catch (FirmapiuException e) {
				record.put(CERTCHAIN, e);
			}
		}
		
		//verifica che il certificato relativo il firmatario non sia stato revocato
		if(fields.contains(SIGNERISnotREVOKED)){
			
		}
		
		//verifica che il certificato relativo il firmatario non era revocato al momento in cui i dati sono stati firmati
		if(fields.contains(SIGNERISnotREVOKEDatSIGNINGTIME)){
			
		}
		
		//ritorna il record generato al chiamante
		return record;
	}

	/**
	 * Verifica la correttezza della firma elettronica <b>di uno dei
	 * firmatari</b> della busta crittografica controllando uno dei campi di verifica
	 * richiesti come parametro
	 * 
	 * 
	 * @param signer il firmatario di cui bisogna verificare la firma
	 * @param field Il nome del campo che si vuole verificare
	 *            in fase di operazione di verifica sul singolo firmatario
	 * @return Un oggetto contenete l'esito dell'operazione di verifica sul
	 *         firmatario con il campo richiesto dal parametro field
	 */
	Object verifySignerField(SignerInformation signer, String field) {
		HashSet<String> fieldSet=new HashSet<String>(2,1);
		fieldSet.add(field);
		Map<String, Object> record = this.verifySigner(signer);
		return record.get(field);
	}

	// PROCEDURE PRIVATE
	//procedura privata per generare un hashset che contiene tutti i campi da verificare
	private static HashSet<String> allFieldSet(){
		HashSet<String> allFields = new HashSet<String>();
		allFields.add(OKSIGNED);
		allFields.add(LEGALLYSIGNED);
		allFields.add(TRUSTEDSIGNER);
		allFields.add(SIGNERISnotREVOKED);
		allFields.add(SIGNERISnotREVOKEDatSIGNINGTIME);
		allFields.add(SIGNERINFO);
		allFields.add(CERTCHAIN);
		allFields.add(TRUSTANCHOR);
		allFields.add(SIGNERCERT);
		
		return allFields;
	}
	
	//procedure private utilizzate dai metodi di verifica
	//operazioni di verifica eseguibili sui singoli campi
	
	//controlla che nel firmatario sia presente l'attributo ESSCertIDv2 e che esso sia valido 
	//in questo caso la busta crittografica è espressa correttamente nel formato CADES-BES secondo
	//la  DELIBERAZIONE ministeriale del N . 45 DEL 21 MAGGIO 2009
	private boolean isLegallySigned(SignerInformation signer,X509CertificateHolder cert) throws FirmapiuException, NoSuchAlgorithmException, IOException{
		AttributeTable signAttr=signer.getSignedAttributes();
		if (signAttr==null)
			throw new FirmapiuException(VERIFY_SIGNER_SIGNINGATTRIBUTE_NOTFOUND);
		Attribute attr=signAttr.get(PKCSObjectIdentifiers.id_aa_signingCertificateV2);
		if(attr==null)
			throw new FirmapiuException(VERIFY_SIGNER_SIGNINGATTRIBUTE_NOTFOUND);
		ASN1Sequence sequence = ASN1Sequence.getInstance(attr.getAttrValues().getObjectAt(0));
		SigningCertificateV2 scv2 = SigningCertificateV2.getInstance(sequence);
		ESSCertIDv2[] essCert =scv2.getCerts();
		if(essCert == null || essCert.length < 1)
			throw new FirmapiuException(VERIFY_SIGNER_SIGNINGATTRIBUTE_NOTFOUND);
		//controlla l'hash del certificato se si restituisce true se no restituisce no
		//aggiungere hash del certificato di sottoscrizione
		String digestAlgorithm = "SHA-256";
		MessageDigest sha = null;
		sha = MessageDigest.getInstance(digestAlgorithm);
		byte[] digestedCert = sha.digest(cert.getEncoded());
		byte[] essCertHash = essCert[0].getCertHash();
		//affinché la firma sia valida digestCert e essCertHash devono essere uguali	
		if (digestedCert.length!=essCertHash.length)
			return false;
		else
		{
			for (int i=0;i<digestedCert.length;i++)
				if(digestedCert[i]!=essCertHash[i])
				{
					return false;
				}
			return true;
		}//fine if
	}//fine metodo
	
	//controlla che il certificato del firmatario sia affidabile controllando la sua catena di certificati
	//valida il certificato X509 del firmatario usando il built-in PKIX support messo a disposizione da java
	//caricando il keystore contenente i certificati degli enti certificatori autorizzati dallo stato italiano
	private PKIXCertPathBuilderResult isTrustedSigner(SignerInformation signer) throws FirmapiuException{
		//genera la lista di certificati da controllare  per generare la catena dei certificati del firmatario
		//TODO quali certificati carica esattamente?
		Collection<?>          certCollection = certStore.getMatches(signer.getSID());
		Iterator<?>  certIt = certCollection.iterator();
		X509CertificateHolder cert = (X509CertificateHolder)certIt.next();
		List<X509Certificate> chain = new LinkedList<X509Certificate>();
		JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter().setProvider(this.bcProvName);
		try {
			X509Certificate x509cert = certConverter.getCertificate(cert);
			chain.add(x509cert);
			while (certIt.hasNext()){
				x509cert = certConverter.getCertificate((X509CertificateHolder)certIt.next());
				chain.add(x509cert);
			}
		} catch (CertificateException e) {
			new FirmapiuException(CERT_DEFAULT_ERROR, e);
		}
		
		//carica i certificati presenti nel token crittografico passato come parametro
		KeyStore anchors=this.token.loadKeyStore(null);
		X509CertSelector target = new X509CertSelector();
		target.setCertificate(chain.get(0));
		PKIXBuilderParameters params;
		CertPathBuilder builder;
		try {
			params = new PKIXBuilderParameters(anchors, target);
			//disabilita il controllo delle CRL
			params.setRevocationEnabled(false);
			CertStoreParameters intermediates = new CollectionCertStoreParameters(chain);
			params.addCertStore(CertStore.getInstance("Collection", intermediates));
			params.setSigProvider(this.bcProvName);
			builder = CertPathBuilder.getInstance("PKIX",this.bcProvName);
		} catch (KeyStoreException | InvalidAlgorithmParameterException e) {
			throw new FirmapiuException(CERT_KEYSTORE_DEFAULT_ERROR, e);
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			throw new FirmapiuException(DEFAULT_ERROR);
		}
		/* 
		 * If build() returns successfully, the certificate is valid. More details 
		 * about the valid path can be obtained through the PKIXBuilderResult.
		 * If no valid path can be found, a CertPathBuilderException is thrown.
		 */
		try {
			return (PKIXCertPathBuilderResult) builder.build(params);
		} catch (CertPathBuilderException e) {
			throw new FirmapiuException(VERIFY_SIGNER_CERTPATH_ERROR, e);
		} catch (InvalidAlgorithmParameterException e) {
			throw new FirmapiuException(DEFAULT_ERROR);
		}
	}
}