package test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertStore;
import java.security.cert.CertStoreParameters;
import java.security.cert.CertificateFactory;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;


public class CertPathVer {

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		Provider p = new BouncyCastleProvider();
		String bouncyCastleProvider2 = p.getName();
		if ((Security.getProvider(bouncyCastleProvider2)) == null)
			Security.addProvider(p);
		//verifica l'affidabilità del firmatario controllando la catena dei certificati relativa
		//verifica che il certificato relativo il firmatario non sia stato revocato tramite CRL

		//valida il certificato X509 del firmatario usando il built-in PKIX support messo a disposizione da java
		//carica il keystore contenente i certificati degli enti certificatori autorizzati dallo stato italiano
		//TODO ricordati che devi gestire a modo il database delle CA in particolare per quanto riguarda la sicurezza del keystore
		InputStream trustStoreInput = new FileInputStream(new File("/home/andy/keystore.jks"));
		char[] password = "default".toCharArray();
		//genera la lista di certificati da controllare 
		
		//CASO 1: Certificato farlocco.
		
		
		List<X509Certificate> chain = new LinkedList<X509Certificate>();
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		FileInputStream fis = new FileInputStream(new File("/home/andy/catena/cert.cer"));
		BufferedInputStream bis = new BufferedInputStream(fis);
		 
		X509Certificate x509cert = (X509Certificate)cf.generateCertificate(bis);
		chain.add(x509cert);
		
		//CASO 2: lista di certificati che dovrebbe essere corretta
		/*List<X509Certificate> chain = new LinkedList<X509Certificate>();
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		FileInputStream fis = new FileInputStream(new File("/home/andy/catena/applicazioni"));
		BufferedInputStream bis = new BufferedInputStream(fis);
		X509Certificate x509cert = (X509Certificate)cf.generateCertificate(bis);
		chain.add(x509cert);
		
		
		
		fis = new FileInputStream(new File("/home/andy/catena/BaltimoreCyberTrustRoot"));
		bis = new BufferedInputStream(fis);
		x509cert = (X509Certificate)cf.generateCertificate(bis);
		chain.add(x509cert);
		
		
		fis = new FileInputStream(new File("/home/andy/catena/DigitPACA1"));
		bis = new BufferedInputStream(fis);
		x509cert = (X509Certificate)cf.generateCertificate(bis);
		chain.add(x509cert);
			
		fis = new FileInputStream(new File("/home/andy/catena/GTECyberTrustGlobalRoot"));
		bis = new BufferedInputStream(fis);
		x509cert = (X509Certificate)cf.generateCertificate(bis);
		chain.add(x509cert);
		System.out.println(chain.size());
		/*Iterator<X509Certificate> itr =chain.iterator();
		
		while(itr.hasNext())
			System.out.println(itr.next().toString());*/
		
		/*JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter().setProvider(bouncyCastleProvider2);
		X509Certificate x509cert = certConverter.getCertificate(cert);
		chain.add(x509cert);*/
		/*while (certIt.hasNext()){
			x509cert = certConverter.getCertificate((X509CertificateHolder)certIt.next());
			chain.add(x509cert);
		}*/
		//Collection<X509CRL> crls =

		/* Construct a valid path. */
		KeyStore anchors = KeyStore.getInstance(KeyStore.getDefaultType());
		anchors.load(trustStoreInput, password);
		
		/*fis = new FileInputStream(new File("/home/andy/catena/GTECyberTrustGlobalRoot"));
		bis = new BufferedInputStream(fis);
		X509Certificate cert = (X509Certificate)cf.generateCertificate(bis);
		anchors.setEntry("puppaflex", new KeyStore.TrustedCertificateEntry(cert), null);*/
		
		X509CertSelector target = new X509CertSelector();
		target.setCertificate(chain.get(0));
		PKIXBuilderParameters params = new PKIXBuilderParameters(anchors, target);
		//DISABILITO IL CONTROLLO DELLE CRL
		params.setRevocationEnabled(true);
		CertStoreParameters intermediates = new CollectionCertStoreParameters(chain);
		params.addCertStore(CertStore.getInstance("Collection", intermediates));
		params.setSigProvider(bouncyCastleProvider2);
		//CertStoreParameters revoked = new CollectionCertStoreParameters(crls);
		//params.addCertStore(CertStore.getInstance("Collection", revoked));
		System.out.println("Certstore size: "+params.getCertStores().size());
		System.out.println("Truststore size"+params.getTrustAnchors().size());
		//System.out.println("Lista dei parametri di certificazione\n\n"+params.toString()+"\n\n");
		CertPathBuilder builder = CertPathBuilder.getInstance("PKIX",bouncyCastleProvider2);
		//CertPathBuilder builder = CertPathBuilder.getInstance("PKIX");
		/* 
		 * If build() returns successfully, the certificate is valid. More details 
		 * about the valid path can be obtained through the PKIXBuilderResult.
		 * If no valid path can be found, a CertPathBuilderException is thrown.
		 */
		PKIXCertPathBuilderResult r = (PKIXCertPathBuilderResult) builder.build(params);
		System.out.println("Sono arrivato qua ---------------->"+r.getCertPath().toString());
	}

}
