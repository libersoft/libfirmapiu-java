/**
 * 
 */
package it.libersoft.firmapiu;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.ess.ESSCertIDv2;
import org.bouncycastle.asn1.ess.SigningCertificateV2;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.util.ASN1Dump;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSAttributeTableGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.DefaultSignedAttributeTableGenerator;
import org.bouncycastle.cms.SignerId;
import org.bouncycastle.cms.SignerInfoGenerator;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.cms.SignerInformationVerifier;
import org.bouncycastle.cms.SignerInformationVerifierProvider;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Store;

/**
 * Questa classe firma e verifica dei dati nel formato CMS definito dallo standard pkcs#7
 * La classe usa lo standard pkcs#11 per firmare i dati tramite l'utilizzo di smart cards
 * 
 * @author dellanna
 *
 */
final class CMSSigner {

	/**
	 * Provider utilizzato per caricare il driver della carta 
	 */
	private final String pkcs11Provider;
	
	/**
	 * Provider utilizzato per caricare le Bouncy Castle 
	 */
	private final String bouncyCastleProvider;
	
	private final CMSSignedDataGenerator cmsGenerator;
	
		
	CMSSigner(String pkcs11DriverPath,char[] pin,ResourceBundle rb) throws Exception{
		//inizializza i providers di Bouncy Castle e PKCS#11 (per usare la carta)
		//controlla che i providers siano già stati caricati se no li inizializza
		//il costruttore è thread safe. Lock sulla classe
		Provider p1 = new BouncyCastleProvider();
		this.bouncyCastleProvider = p1.getName();
		synchronized (CMSSigner.class) {
			if ((Security.getProvider(bouncyCastleProvider)) == null)
				Security.addProvider(p1);
		}
	
		//FIXME da integrare probabilmente con il codice di Fabio riguardante il caricamento delle librerie dalla smartcard
		//per ogni invocazione diversa dell'oggetto CMSigner crea la sua istanza del provider pkcs11
		//FIXME l'istanza del provider pkcs11 dovrebbe essere "distrutta" dopo ogni invocazione di sign.
		//FIXME questo perchè se cambia lo "stato" della carta (togli/inserisci lettore/carta) il provider lancia un errore di token
		//FIXME Per questo motivo il codice va integrato con quello di Fabio e probabilmente bisogna fare una gestione dei thread riguardanti la carta
		//Carica la libreria passata come proprietà dal file pkcs11DriverPath
		Properties prop = new Properties();
		prop.load(new FileInputStream(new File(pkcs11DriverPath)));
		String libdriver = prop.getProperty("library");
		//crea un nome di provider diverso per ogni invocazione di oggetto
		String provPkcs11 = "name=pkcs11-"+((long)(Math.random()*Long.MAX_VALUE))+"\nlibrary="+libdriver;
		Provider p2 = new sun.security.pkcs11.SunPKCS11(new ByteArrayInputStream(provPkcs11.getBytes()));
		this.pkcs11Provider = p2.getName();
		Security.addProvider(p2);

		
		//firma i dati ricevuti in ingresso secondo lo standard CMS (pkcs#7). 
		//I dati e la firma devono incapsulati (attached) nel CMS risultante.

		//crea il generatore utilizzato per firmare i dati ricevuti in ingresso
		this.cmsGenerator = new CMSSignedDataGenerator();

		//recupera e carica il keystore del provider SunPKCS11-pcsc (la carta)
		KeyStore pkcs11keystore = KeyStore.getInstance("pkcs11", pkcs11Provider);
		pkcs11keystore.load(null, pin);
		
		//scorre gli alias del keystore fino a trovare quello che ha keyusage nonrepudiation=true e tutti gli flag=false
		//secondo la DELIBERAZIONE ministeriale del N . 45 DEL 21 MAGGIO 2009 art.12 comma 5 par a)
		//se non ne trova nessuno lancia errore, se ne trova più d'uno lancia errore
		Enumeration<String> aliases = pkcs11keystore.aliases();
		String alias= null;
		while(aliases.hasMoreElements()){
			String tmp=aliases.nextElement();
			X509Certificate cert=(X509Certificate) pkcs11keystore.getCertificate(tmp);
	      	boolean[] keyUsage=cert.getKeyUsage();
        	if (keyUsage!=null && keyUsage.length==9) {
				if (checkKeyUsage(keyUsage))
					if (alias == null)
						alias = tmp;
					else
						throw new RuntimeException(rb.getString("warning0")
								+ ": " + rb.getString("aliaserror1"));
			}
           
		}
		if (alias==null)
			throw new NullPointerException(rb.getString("warning0")+": "+rb.getString("aliaserror0"));
			
		//recupera la chiave privata dell'alias passato come parametro
		Key privatekey=pkcs11keystore.getKey(alias, pin);
		//resetta la memoria su cui è salvato il valore del pin
		java.util.Arrays.fill(pin, ' ');
		if(privatekey==null)
			throw new NullPointerException(rb.getString("warning0")+": "+rb.getString("aliaserror3"));
		//recupera la catena di certificati da utilizzare per verificare in seguito la firma di un file
		X509Certificate[] x509certList=(X509Certificate[])pkcs11keystore.getCertificateChain(alias);
		//prepara la catena dei certificati da usare per generare il p7m 
		List<X509Certificate> certList = new ArrayList<X509Certificate>();
		for (int i=0 ;i<x509certList.length;i++)
			certList.add(x509certList[i]);
		Store  certs = new JcaCertStore(certList);
		cmsGenerator.addCertificates(certs);
	
		//crea il firmatario e lo aggiunge al p7m generator
		ContentSigner shaSigner = new JcaContentSignerBuilder("SHA256withRSA").setProvider(pkcs11Provider).build((PrivateKey)privatekey);
		//recupera il certificato del firmatario
		X509Certificate signCert=x509certList[0];
		
		//TODO se il codice viene "internazionalizzato" probabilmente ci vuole una versione "locale" di quest'oggetto
		//che può avere o meno bisogno di questo codice. (O probabilmente no dipende se anche gli altri vogliono CADES-bes o meno)
		
		//aggiunta degli attributi obbligatori per rendere i dati firmati conformi al formato Cades-Bes definito in  ETSI TS 101 733
		//e richiesto dalla  DELIBERAZIONE ministeriale del N . 45 DEL 21 MAGGIO 2009 art.21 comma 1
		
		//aggiungere hash del certificato di sottoscrizione
		String digestAlgorithm = "SHA-256";
		MessageDigest sha = null;
		//TODO vedere se e il caso di fare try catch
		sha = MessageDigest.getInstance(digestAlgorithm);
		byte[] digestedCert = null;
		digestedCert = sha.digest(signCert.getEncoded());
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
	    SignerInfoGenerator original = new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().setProvider(bouncyCastleProvider).build()).build(shaSigner, signCert);
	    cmsGenerator.addSignerInfoGenerator(new SignerInfoGenerator(original,attrGen,null));
		
	    
	    //cmsGenerator.addAttributeCertificate(new X509AttributeCertificateHolder(certHAttribute.getEncoded()));
	}
	
	CMSSignedData sign(CMSTypedData data) throws CMSException {
		
		//TODO gestione del treadsafe?
		//TODO per legge i file devono essere p7m. secondo la DELIBERAZIONE ministeriale del N . 45 DEL 21 MAGGIO 2009 art.21 comma 5,6
        
        //genera i dati codificati in p7m (attached) secondo lo standard pkcs7 e li restituisce al chiamante
		return this.cmsGenerator.generate(data, true);
	}
	
	
	void close(){
		//rimuove i providers utilizzati dall'applicazione
		//Security.removeProvider(bouncyCastleProvider);
		//FIXME da integrare con il codice di Fabio probabilmente
		Security.removeProvider(pkcs11Provider);
	}

	/**
	 * Finalizzatore dell'oggetto CMSSigner. Casomai il chiamante si dimenticasse di fare una close
	 * 
	 * @see java.lang.Object#finalize()
	 * 
	 */
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		this.close();
	}
	
	static boolean verify(CMSSignedData signedData,ResourceBundle rb) throws CMSException{
		//Controlla che il provider delle Bouncy Castle sia stato caricato, se no lo carica. Il metodo è thread safe
		//lock sulla classe
		//FIXME da integrare probabilmente con il codice di Fabio riguardante il caricamento delle librerie dalla smartcard
		Provider p = new BouncyCastleProvider();
		String bouncyCastleProvider2 = p.getName();
		synchronized(CMSSigner.class){
			if ((Security.getProvider(bouncyCastleProvider2)) == null)
				Security.addProvider(p);
		}
		//FIXME pare pulizia dei commenti sottostanti quando non servono più
		/*
        try {
        	System.out.println("Dump del cms generato:------------------------------------------------");
			System.out.println(ASN1Dump.dumpAsString(ASN1Primitive.fromByteArray(signedData.getEncoded())));
			System.out.println("fine Dump del cms generato:------------------------------------------------");
			Iterator<?> itr=signedData.getSignerInfos().getSigners().iterator();
			SignerInformation sigInf=(SignerInformation)itr.next();
			AttributeTable signAttr=sigInf.getSignedAttributes();
			System.out.println("dimensione della tabella degli attributi firmati del primo firmatario"+signAttr.size());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
		//controlla che i dati ricevuti in ingresso siano stati firmati correttamente
		//TODO controllare che dati e firma siano incapsulati
		return signedData.verifySignatures(new PrivateSignerInformationVerifierProvider(bouncyCastleProvider2,signedData,rb));
		//controlla che dati e firma siano incapsulati
		//signedData.getSignedContent()
		
		
		//per ogni firmatario effettua la verifica
		/*Store certStore = signedData.getCertificates();
		SignerInformationStore  signers = signedData.getSignerInfos();
		Collection<?>  c = signers.getSigners();
		Iterator<?>  it = c.iterator();
		while (it.hasNext())
		{
			//verifica che il signedData passato come parametro sia stato firmato correttamente
			SignerInformation   signer = (SignerInformation)it.next();
			Collection<?>          certCollection = certStore.getMatches(signer.getSID());
			Iterator<?>  certIt = certCollection.iterator();
			X509CertificateHolder cert = (X509CertificateHolder)certIt.next();
			if (signer.verify(new JcaSimpleSignerInfoVerifierBuilder().setProvider(bouncyCastleProvider2).build(cert)))
			{
				verified++;
			}   
		}*/
		//verifica che il signedData passato come parametro sia stato firmato correttamente
		
		//controlla che il signedData passato come parametro rispetti la  DELIBERAZIONE ministeriale del N . 45 DEL 21 MAGGIO 2009
		//in fase di verifica
		
		//verifica l'affidabilità del firmatario controllando la catena dei certificati relativa
		
		//verifica che il certificato relativo il firmatario non sia stato revocato tramite CRL
		
		//genera il report della verifica per il firmatario e lo aggiunge alla lista
		
		//restituisce la lista contenente i report di verifica per ogni firmatario presente nella signedData
	
	}
	
	//PROCEDURE PRIVATE
	//controlla che tutti i bit del keyusage del certificato siano false tranne quello
	//di non repudiation che deve essere uguale a true
	private boolean checkKeyUsage(boolean[] keyusage){
		//return !keyusage[0] && keyusage[1];
		return !keyusage[0]&&keyusage[1]&&!keyusage[2]&&!keyusage[3]&&!keyusage[4]&&!keyusage[5]&&!keyusage[6]&&!keyusage[7]&&!keyusage[8];
	}
	
	
	private static final class PrivateSignerInformationVerifierProvider implements SignerInformationVerifierProvider{

		//inizializza la classe passando il provider utilizzato come parametro
		private final String provider;
		private final CMSSignedData sigData;
		private final ResourceBundle rb;
		
		private PrivateSignerInformationVerifierProvider(String provider,
				CMSSignedData sigData,ResourceBundle rb) {
			super();
			this.provider = provider;
			this.sigData = sigData;
			this.rb= rb;
		}

		@Override
		public SignerInformationVerifier get(SignerId sid)
				throws OperatorCreationException {
			Collection<?>  certCollection = sigData.getCertificates().getMatches(sid);
			Iterator<?> certIt = certCollection.iterator();
			X509CertificateHolder cert = (X509CertificateHolder)certIt.next();
			try {
				/*//recupera gli attributi riguarganti il digest del certificato 
				//e gli attributi di ESSCertID per verificare la validità della firma secondo la legge italiana
				AttributeTable signAttr=sigData.getSignerInfos().get(sid).getSignedAttributes();
				//TODO da cambiare con eccezione customizzata se necessario
				if(signAttr==null)
					throw new OperatorCreationException(rb.getString("error1"));
				//deve controllare che ci sia un attributo di tipo ESSCertID
				Attribute attr=signAttr.get(PKCSObjectIdentifiers.id_aa_signingCertificateV2);
				if(attr==null)
					throw new OperatorCreationException(rb.getString("error1"));
				ASN1Sequence sequence = ASN1Sequence.getInstance(attr.getAttrValues().getObjectAt(0));
				SigningCertificateV2 scv2 = SigningCertificateV2.getInstance(sequence);
				ESSCertIDv2[] essCert =scv2.getCerts();
				if(essCert == null || essCert.length < 1)
					throw new OperatorCreationException(rb.getString("error1"));
				
				System.out.println(scv2.getCerts().length);
				
				
				/*ASN1Set attrSet=attr.getAttrValues();
				for(int i=0;i<attrSet.size();i++){
					//try {
						//byte[] econded=attrSet.getObjectAt(i)PKCS#9: 1.2.840.113549.1.9.16.6.2.47
					
						System.out.println(PKCSObjectIdentifiers.id_aa_signingCertificateV2+"\n"+ASN1Dump.dumpAsString(attr.toASN1Primitive()));
					/*} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();PKCS#9: 1.2.840.113549.1.9.16.6.2.47
					}	
				}*/
					//System.out.println(attrSet.getObjectAt(i));
				//attr.getAttrValues().*/
				//System.out.println("----------------------->Sono arrivato qua!"+attr);
				
				return new JcaSimpleSignerInfoVerifierBuilder().setProvider(provider).build(cert);
			} catch (CertificateException e) {
				e.printStackTrace();
				OperatorCreationException e2 = new OperatorCreationException (rb.getString("error0"));
				e2.initCause(e);
				throw e2;
			} 
		}//fine get
	}//fine classe privata
}