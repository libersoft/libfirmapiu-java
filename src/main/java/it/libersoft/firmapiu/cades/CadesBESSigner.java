/**
 * 
 */
package it.libersoft.firmapiu.cades;

import it.libersoft.firmapiu.CRToken;
import it.libersoft.firmapiu.exception.FirmapiuException;
import static it.libersoft.firmapiu.exception.FirmapiuException.*;
import static it.libersoft.firmapiu.consts.FactoryConsts.P7MFILE;
import static it.libersoft.firmapiu.consts.FactoryConsts.P7SFILE;

import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.ess.ESSCertIDv2;
import org.bouncycastle.asn1.ess.SigningCertificateV2;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSAttributeTableGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.DefaultSignedAttributeTableGenerator;
import org.bouncycastle.cms.SignerInfoGenerator;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Store;

/**
 * Questa classe firma dei dati nel formato CMS definito dallo standard pkcs#7<p>
 * 
 * Firma elettronicamente i dati nel formato CADES-bes 
 * secondo la DELIBERAZIONE ministeriale del N . 45 DEL 21 MAGGIO 2009. <p>
 * 
 * I dati vengono firmati utilizzando un token crittografico passato come parametro
 * 
 * @author dellanna
 *
 */
final class CadesBESSigner {

	/**
	 * Provider utilizzato per caricare le Bouncy Castle 
	 */
	private final String bcProvName;
	
	private final CMSSignedDataGenerator cmsGenerator;
	
	private final String digestCalculatorProviderStr;
		
	/**
	 * @param token
	 * @throws FirmapiuException
	 */
	CadesBESSigner(CRToken token,String digestCalulatorProviderStr) throws FirmapiuException{
		//digest calulator provider utilizzato per il calcolo del digest
		this.digestCalculatorProviderStr=digestCalulatorProviderStr;
		
		//inizializza il provider di Bouncy Castle
		Provider p1 = new BouncyCastleProvider();
		//TODO vedere se ce da implementare o meno una procedura se si tenta di installare più volte lo stesso providere in maniera concorrente
		Security.addProvider(p1);
		this.bcProvName = p1.getName();
		
		//firma i dati ricevuti in ingresso secondo lo standard CMS (pkcs#7). 
		//I dati e la firma devono incapsulati (attached) nel CMS risultante.

		//crea il generatore utilizzato per firmare i dati ricevuti in ingresso
		//i file devono essere firmati secondo lo standard CMS (pkcs#7) in formato CADES-Bes
		this.cmsGenerator = new CMSSignedDataGenerator();

		//recupera e carica il keystore presente sul token
		KeyStore pkcs11keystore = token.loadKeyStore(null);
		
		//scorre gli alias del keystore fino a trovare quello che ha keyusage nonrepudiation=true e tutti gli flag=false
		//secondo la DELIBERAZIONE ministeriale del N . 45 DEL 21 MAGGIO 2009 art.12 comma 5 par a)
		//se non ne trova nessuno lancia errore, se ne trova più d'uno lancia errore
		Enumeration<String> aliases;
		try {
			aliases = pkcs11keystore.aliases();
		} catch (KeyStoreException e) {
			throw new FirmapiuException(CERT_KEYSTORE_DEFAULT_ERROR, e);
		}
		String alias= null;
		while(aliases.hasMoreElements()){
			String tmp=aliases.nextElement();
			X509Certificate cert;
			try {
				cert = (X509Certificate) pkcs11keystore.getCertificate(tmp);
			} catch (KeyStoreException e) {
				throw new FirmapiuException(CERT_KEYSTORE_CERTERROR, e);
			}
	      	boolean[] keyUsage=cert.getKeyUsage();
        	if (keyUsage!=null && keyUsage.length==9) {
				if (checkKeyUsage(keyUsage))
					if (alias == null)
						alias = tmp;
					else
						throw new FirmapiuException(SIGNER_ALIAS_TOOMANY);
			}
           
		}
		if (alias==null)
			throw new FirmapiuException(SIGNER_ALIAS_NOTFOUND);
		
		//recupera la chiave privata dell'alias passato come parametro
		Key privatekey;
		try {
			privatekey = pkcs11keystore.getKey(alias, null);
		} catch (KeyStoreException
				| NoSuchAlgorithmException e) {
			throw new FirmapiuException(CERT_KEYSTORE_DEFAULT_ERROR, e);
		} catch (UnrecoverableKeyException e ){
			throw new FirmapiuException(CERT_KEYSTORE_KEYERROR, e);
		}
		//resetta la memoria su cui è salvato il valore del pin
		//java.util.Arrays.fill(pin, ' ');
		//if(privatekey==null)
		//	throw new NullPointerException(rb.getString("warning0")+": "+rb.getString("aliaserror3"));
		//recupera la catena di certificati da utilizzare per verificare in seguito la firma di un file
		X509Certificate[] x509certList;
		try {
			x509certList = (X509Certificate[])pkcs11keystore.getCertificateChain(alias);
		} catch (KeyStoreException e) {
			throw new FirmapiuException(CERT_KEYSTORE_DEFAULT_ERROR, e);
		}
		//prepara la catena dei certificati da usare per generare il p7m 
		List<X509Certificate> certList = new ArrayList<X509Certificate>();
		for (int i=0 ;i<x509certList.length;i++)
			certList.add(x509certList[i]);
		try {
			Store  certs = new JcaCertStore(certList);
			cmsGenerator.addCertificates(certs);
		} catch (CertificateEncodingException | CMSException e) {
			throw new FirmapiuException(CERT_ENCODING_ERROR, e);
		}
	
		//crea il firmatario e lo aggiunge al p7m generator
		ContentSigner shaSigner;
		try {
			shaSigner = new JcaContentSignerBuilder("SHA256withRSA").setProvider(token.getProvider()).build((PrivateKey)privatekey);
		} catch (OperatorCreationException e) {
			throw new FirmapiuException(SIGNER_DEFAULT_ERROR, e);
		}
		//recupera il certificato del firmatario se è scaduto lancia un errore
		X509Certificate signCert=x509certList[0];
		try {
			signCert.checkValidity();
		} catch (CertificateExpiredException | CertificateNotYetValidException e1) {
			throw new FirmapiuException(CERT_INVALID_CURRENTDATE, e1);
		}
		
		//aggiunta degli attributi obbligatori per rendere i dati firmati conformi al formato Cades-Bes definito in  ETSI TS 101 733
		//e richiesto dalla  DELIBERAZIONE ministeriale del N . 45 DEL 21 MAGGIO 2009 art.21 comma 1
		
		//aggiungere hash del certificato di sottoscrizione
		String digestAlgorithm = "SHA-256";
		MessageDigest sha = null;
		try {
			sha = MessageDigest.getInstance(digestAlgorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new FirmapiuException(CERT_DEFAULT_ERROR, e);
		}
		byte[] digestedCert = null;
		try {
			digestedCert = sha.digest(signCert.getEncoded());
		} catch (CertificateEncodingException e) {
			throw new FirmapiuException(CERT_ENCODING_ERROR, e);
		}
		/* Viene ora creato l'attributo ESSCertID versione 2 così come richiesto nel nuovo standard: */
	    AlgorithmIdentifier aiSha256 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256);
	    ESSCertIDv2 essCert1 = new ESSCertIDv2(aiSha256, digestedCert);
	    
	    ESSCertIDv2[] essCert1Arr = { essCert1 };
	    SigningCertificateV2 scv2 = new SigningCertificateV2(essCert1Arr);
	    Attribute certHAttribute = new Attribute(PKCSObjectIdentifiers.id_aa_signingCertificateV2, new DERSet(scv2));
	    // Aggiungo l'attributo al vettore degli attributi da firmare:
	    ASN1EncodableVector v = new ASN1EncodableVector();
	    v.add(certHAttribute);
	    AttributeTable at = new AttributeTable(v);
	    CMSAttributeTableGenerator attrGen = new DefaultSignedAttributeTableGenerator(at);
	    //crea il signerInfoGenerator e aggiunge gli attributi richiesti per la legge italiana
	    SignerInfoGenerator original;
		try {
			//cerca di caricare il digest calculator provider passato come parametro. 
			//Se è null usa il digestcalculator provider delle Bouncy Castle
			DigestCalculatorProvider digestCalculatorProvider=null;
			if(this.digestCalculatorProviderStr!=null){
				try {
					Class<?> cls=ClassLoader.getSystemClassLoader().loadClass(digestCalulatorProviderStr);
					digestCalculatorProvider= (DigestCalculatorProvider)cls.newInstance();
				} catch (ClassNotFoundException | InstantiationException
						| IllegalAccessException e) {
					//se non riesce a caricare la classe lancia una firmapiuexception
					String msg= FirmapiuException.getDefaultErrorCodeMessage(DIGESTCALCULATOR_ERROR);
					msg+=" : "+this.digestCalculatorProviderStr;
					throw new FirmapiuException(DIGESTCALCULATOR_ERROR, msg, e);
				}
			}
			else
				digestCalculatorProvider=new JcaDigestCalculatorProviderBuilder().setProvider(bcProvName).build();
			
			original = new JcaSignerInfoGeneratorBuilder(digestCalculatorProvider).build(shaSigner, signCert);
		} catch (CertificateEncodingException e) {
			throw new FirmapiuException(CERT_ENCODING_ERROR, e);
		} catch (OperatorCreationException e) {
			throw new FirmapiuException(SIGNER_DEFAULT_ERROR, e);
		}
	    cmsGenerator.addSignerInfoGenerator(new SignerInfoGenerator(original,attrGen,null));
	    //cmsGenerator.addAttributeCertificate(new X509AttributeCertificateHolder(certHAttribute.getEncoded()));
	}
	
	/**
	 * firma i dati passati come parametro generando la busta crittografica Cades-Bes
	 * 
	 * @param data i dati da firmare
	 * @param attached Se i dati devono essere "attached" o "detached" dalla busta crittografica
	 * @return la busta crittografica generata
	 * @throws CMSException
	 */
	CMSSignedData sign(CMSTypedData data,boolean attached) throws CMSException{
		
		//TODO gestione del treadsafe?
        //genera i dati codificati in p7m (attached) secondo lo standard pkcs7 e 
		//secondo la DELIBERAZIONE ministeriale del N . 45 DEL 21 MAGGIO 2009 art.21 comma 5,6
		return this.cmsGenerator.generate(data, attached);
	}
	
//	void close(){
//		//rimuove i providers utilizzati dall'applicazione
//		//Security.removeProvider(bcProvName);
//		Security.removeProvider(pkcs11Provider);
//	}
//
//	/**
//	 * Finalizzatore dell'oggetto CMSSigner. Casomai il chiamante si dimenticasse di fare una close
//	 * 
//	 * @see java.lang.Object#finalize()
//	 * 
//	 */
//	@Override
//	protected void finalize() throws Throwable {
//		super.finalize();
//		this.close();
//	}
	
	//PROCEDURE PRIVATE
	//controlla che tutti i bit del keyusage del certificato siano false tranne quello
	//di non repudiation che deve essere uguale a true
	private boolean checkKeyUsage(boolean[] keyusage){
		//return !keyusage[0] && keyusage[1];
		return !keyusage[0]&&keyusage[1]&&!keyusage[2]&&!keyusage[3]&&!keyusage[4]&&!keyusage[5]&&!keyusage[6]&&!keyusage[7]&&!keyusage[8];
	}
}