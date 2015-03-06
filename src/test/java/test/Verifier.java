package test;


import it.libersoft.firmapiu.exception.FirmapiuException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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

import static it.libersoft.firmapiu.consts.FirmapiuConstants.*;


/**
 * 
 */

/**
 * @author andy
 *
 */
public class Verifier {

	/**
	 * @param args
	 */
	public static void main(String[] args){
		Provider p = new BouncyCastleProvider();
		String bouncyCastleProvider2 = p.getName();
		if ((Security.getProvider(bouncyCastleProvider2)) == null)
			Security.addProvider(p);
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
		//return signedData.verifySignatures(new PrivateSignerInformationVerifierProvider(bouncyCastleProvider2,signedData,rb));
		//controlla che dati e firma siano incapsulati
		CMSSignedData signedData=null;
		try {
			//FileInputStream in = new FileInputStream(new File("/home/andy/Scrivania/Smart Card Handbook.pdf.p7m"));
			//FileInputStream in = new FileInputStream(new File("/home/andy/Scrivania/t.txt.p7m"));
			//FileInputStream in = new FileInputStream(new File("/home/andy/Scrivania/README.txt.p7m"));
			FileInputStream in = new FileInputStream(new File("/home/andy/Scrivania/signFake.pdf.p7m"));
			//FileInputStream in = new FileInputStream(new File("/home/andy/Scrivania/Cose da Fare.txt.p7m"));
			byte[] b=new byte[in.available()];
			in.read(b);
			signedData = new CMSSignedData(b);
			System.out.println("version ->"+signedData.getVersion());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.exit(-1);
		}
		//signedData.getSignedContent().write(new FileOutputStream(new File("/home/andy/Scrivania/262 Art All In One 56-69.pdf")));
		
		//genera il report da inviare in risposta: genera una lista contenente gli esiti dell'operazione 
		//di verifica per ogni firmatario
		
		
		List<Map<String,Object>> report = new LinkedList<Map<String,Object>>();
		
		
		//per ogni firmatario effettua la verifica
		Store certStore = signedData.getCertificates();
		SignerInformationStore  signers = signedData.getSignerInfos();
		Collection<?>  c = signers.getSigners();
		Iterator<?>  it = c.iterator();
		while (it.hasNext())
		{	
			//genera la map contenente tutte le informazioni di verifica per il singolo firmatario
			Map<String,Object> record = new TreeMap<String,Object>();
			
			//verifica che i dati contenuti in signedData siano stati firmati correttamente dal firmatario
			SignerInformation   signer = (SignerInformation)it.next();
			System.out.println("signerinfo version"+signer.getVersion());
			record.put(SIGNERINFO, signer);
			Collection<?>          certCollection = certStore.getMatches(signer.getSID());
			Iterator<?>  certIt = certCollection.iterator();
			X509CertificateHolder cert = (X509CertificateHolder)certIt.next();
			//converte il certificato del firmatario in java.security.cert.X509Certificatee lo aggiunge nel report
			try {
				X509Certificate x509cert=new JcaX509CertificateConverter().getCertificate(cert);
				record.put(SIGNERCERT, x509cert);
			} catch (CertificateException e1) {
				e1.printStackTrace();
				record.put(SIGNERCERT, new FirmapiuException(e1));
			}
			try {
				Boolean result=signer.verify(new JcaSimpleSignerInfoVerifierBuilder().setProvider(bouncyCastleProvider2).build(cert));
				record.put(OKSIGNED, result);
			} catch (OperatorCreationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				record.put(OKSIGNED, new FirmapiuException(e));
			} catch (CertificateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				record.put(OKSIGNED, new FirmapiuException(e));
			} catch (CMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				record.put(OKSIGNED, new FirmapiuException(e));
			}//fine try-catch
			
			//controlla che nel firmatario sia presente l'attributo ESSCertIDv2 e che esso sia valido 
			//in questo caso la busta crittografica è espressa correttamente nel formato CADES-BES secondo
			//la  DELIBERAZIONE ministeriale del N . 45 DEL 21 MAGGIO 2009
			try {
				AttributeTable signAttr=signer.getSignedAttributes();
				Attribute attr=signAttr.get(PKCSObjectIdentifiers.id_aa_signingCertificateV2);
				if(attr==null)
					throw new FirmapiuException("errore attributo signingCertificateV2 non presente");
				ASN1Sequence sequence = ASN1Sequence.getInstance(attr.getAttrValues().getObjectAt(0));
				SigningCertificateV2 scv2 = SigningCertificateV2.getInstance(sequence);
				ESSCertIDv2[] essCert =scv2.getCerts();
				if(essCert == null || essCert.length < 1)
					throw new FirmapiuException("errore attributo ESSCertIDv2 non presente");
				
				//controlla l'hash del certificato se si restituisce true se no restituisce no
				//aggiungere hash del certificato di sottoscrizione
				String digestAlgorithm = "SHA-256";
				MessageDigest sha = null;
				//TODO vedere se e il caso di fare try catch
				sha = MessageDigest.getInstance(digestAlgorithm);
				byte[] digestedCert = sha.digest(cert.getEncoded());
				byte[] essCertHash = essCert[0].getCertHash();
				//affinché la firma sia valida digestCert e essCertHash devono essere uguali	
				if (digestedCert.length!=essCertHash.length)
					record.put(LEGALLYSIGNED, new Boolean(false));
				else
				{
					for (int i=0;i<digestedCert.length;i++)
						if(digestedCert[i]!=essCertHash[i])
						{
							record.put(LEGALLYSIGNED, new Boolean(false));
							break;
						}
					if(!record.containsKey(LEGALLYSIGNED))
						record.put(LEGALLYSIGNED, new Boolean(true));
				}
			} catch (FirmapiuException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				record.put(LEGALLYSIGNED, new FirmapiuException(e));
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				record.put(LEGALLYSIGNED, new FirmapiuException(e));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				record.put(LEGALLYSIGNED, new FirmapiuException(e));
			}//fine try-catch
		
			try {
				//verifica l'affidabilità del firmatario controllando la catena dei certificati relativa
				//valida il certificato X509 del firmatario usando il built-in PKIX support messo a disposizione da java
				//carica il keystore contenente i certificati degli enti certificatori autorizzati dallo stato italiano
				//TODO ricordati che devi gestire a modo il database delle CA in particolare per quanto riguarda la sicurezza del keystore
				InputStream trustStoreInput = new FileInputStream(new File("/home/andy/keystore.jks"));
				char[] password = "default".toCharArray();
				//genera la lista di certificati da controllare 
				List<X509Certificate> chain = new LinkedList<X509Certificate>();
				JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter().setProvider(bouncyCastleProvider2);
				X509Certificate x509cert = certConverter.getCertificate(cert);
				chain.add(x509cert);
				while (certIt.hasNext()){
					x509cert = certConverter.getCertificate((X509CertificateHolder)certIt.next());
					chain.add(x509cert);
				}

				/* Construct a valid path. */
				KeyStore anchors = KeyStore.getInstance(KeyStore.getDefaultType());
				anchors.load(trustStoreInput, password);
				X509CertSelector target = new X509CertSelector();
				target.setCertificate(chain.get(0));
				PKIXBuilderParameters params = new PKIXBuilderParameters(anchors, target);
				//disabilita il controllo delle CRL
				params.setRevocationEnabled(false);
				CertStoreParameters intermediates = new CollectionCertStoreParameters(chain);
				params.addCertStore(CertStore.getInstance("Collection", intermediates));
				params.setSigProvider(bouncyCastleProvider2);
				CertPathBuilder builder = CertPathBuilder.getInstance("PKIX",bouncyCastleProvider2);
				/* 
				 * If build() returns successfully, the certificate is valid. More details 
				 * about the valid path can be obtained through the PKIXBuilderResult.
				 * If no valid path can be found, a CertPathBuilderException is thrown.
				 */
				PKIXCertPathBuilderResult r = (PKIXCertPathBuilderResult) builder.build(params);
				//il certificato del firmatario è affidabile
				record.put(TRUSTEDSIGNER, new Boolean(true));
				//"Trust anchor" del certificato del firmatario
				X509Certificate trustanchor = r.getTrustAnchor().getTrustedCert();
				record.put(TRUSTANCHOR, trustanchor);
				//catena di certificati associata al firmatario
				List<? extends Certificate> certchain=r.getCertPath().getCertificates();
				record.put(CERTCHAIN, certchain);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				record.put(TRUSTEDSIGNER, new FirmapiuException(e));
			} catch (CertificateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				record.put(TRUSTEDSIGNER, new FirmapiuException(e));
			} catch (KeyStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				record.put(TRUSTEDSIGNER, new FirmapiuException(e));
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				record.put(TRUSTEDSIGNER, new FirmapiuException(e));
			} catch (InvalidAlgorithmParameterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				record.put(TRUSTEDSIGNER, new FirmapiuException(e));
			} catch (CertPathBuilderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				record.put(TRUSTEDSIGNER, new FirmapiuException(e));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				record.put(TRUSTEDSIGNER, new FirmapiuException(e));
			}  catch (NoSuchProviderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				record.put(TRUSTEDSIGNER, new FirmapiuException(e));
			}
			
			//verifica che il certificato relativo il firmatario non sia stato revocato tramite CRL
			
			//aggiunge l'esito della verifica nella lista dei firmatari
			report.add(record);
		}//fine while


				
		
		//genera il report della verifica per il firmatario e lo aggiunge alla lista

		//restituisce la lista contenente i report di verifica per ogni firmatario presente nella signedData

		//stampa a schermo l'esito della verifica
		Iterator<Map<String,Object>> repitr=report.iterator();
		while(repitr.hasNext()){
			System.out.println("Firmatario----------->");
			Map<String,Object> record = repitr.next();
			X509Certificate cert1 =null;
			//certificato del firmatario
			if(record.containsKey(SIGNERCERT))
			{
				System.out.println("***Certificato del firmatario");
				Object obj=record.get(SIGNERCERT);
				if(obj instanceof X509Certificate)
				{
					cert1 = (X509Certificate)obj;
					System.out.println("Issuer-->"+cert1.getIssuerDN().toString());
					System.out.println("Subject-->"+cert1.getSubjectDN().toString());
				}
				else if (obj instanceof FirmapiuException){
					FirmapiuException e = (FirmapiuException)obj;
					e.printStackTrace();
				}
				else
					System.out.println("???");
			}
			else
				System.out.println("***Non ho trovato il certificato del firmatario");
			//verifica la firma del firmatario
			if(record.containsKey(OKSIGNED)){
				System.out.println("***Verifica della firma per il firmatario");
				Object obj = record.get(OKSIGNED);
				if(obj instanceof Boolean){
					System.out.println("Esito:"+obj);
				}
				else if (obj instanceof FirmapiuException){
					FirmapiuException e = (FirmapiuException)obj;
					e.printStackTrace();
				}
				else
					System.out.println("???");
			}
			else
				System.out.println("***Non ho trovato l'esito della verifica della firma per il firmatario");
			//verifica che la firma del firmatario sia legale
			if(record.containsKey(LEGALLYSIGNED)){
				System.out.println("***Verifica che la firma sia conforme alla DELIBERAZIONE ministeriale del N . 45 DEL 21 MAGGIO 2009");
				Object obj = record.get(LEGALLYSIGNED);
				if(obj instanceof Boolean){
					System.out.println("Esito:"+obj);
				}
				else if (obj instanceof FirmapiuException){
					FirmapiuException e = (FirmapiuException)obj;
					e.printStackTrace();
				}
				else
					System.out.println("???");
			}
			else
				System.out.println("***Non ho trovato l'esito della verifica della legalità della firma per il firmatario");
			//verifica che il certificato del firmatario sia affidabile
			if(record.containsKey(TRUSTEDSIGNER)){
				System.out.println("***Verifica che il certificato del firmatario sia affidabile");
				Object obj = record.get(TRUSTEDSIGNER);
				if(obj instanceof Boolean){
					System.out.println("Esito:"+obj);
				}
				else if (obj instanceof FirmapiuException){
					FirmapiuException e = (FirmapiuException)obj;
					e.printStackTrace();
				}
				else
					System.out.println("???");
			}
			else
				System.out.println("***Non ho trovato l'esito della verifica dell'affidabilità del certificato del firmatario");
			//catena dei certificati del firmatario
			if(record.containsKey(CERTCHAIN))
			{
				System.out.println("***Catena dei certificati associata al certificato del firmatario");
				Object obj=record.get(CERTCHAIN);
				if(obj instanceof List<?>)
				{
					List<?> l1 = (List<?>)obj;
					Iterator<?> it2= l1.iterator();
					int i=0;
					while(it2.hasNext()){
						X509Certificate xcert=(X509Certificate)it2.next();
						System.out.println("Certificato ["+i+"]");
						System.out.println("Issuer-->"+xcert.getIssuerDN().toString());
						System.out.println("Subject-->"+xcert.getSubjectDN().toString());
						i++;
					}
				}
				else if (obj instanceof FirmapiuException){
					FirmapiuException e = (FirmapiuException)obj;
					e.printStackTrace();
				}
				else
					System.out.println("???");
			}
			else
				System.out.println("***Non ho trovato la catena dei certificati associata al certificato del firmatario");
			//"trust anchor" del certificato del firmatario
			if(record.containsKey(TRUSTANCHOR))
			{
				System.out.println("***Trust Anchor: Certificato della CA associata al certificato del firmatario");
				Object obj=record.get(TRUSTANCHOR);
				if(obj instanceof X509Certificate)
				{
					X509Certificate s1 = (X509Certificate)obj;
					System.out.println("Issuer-->"+s1.getIssuerDN().toString());
					System.out.println("Subject-->"+s1.getSubjectDN().toString());
				}
				else if (obj instanceof FirmapiuException){
					FirmapiuException e = (FirmapiuException)obj;
					e.printStackTrace();
				}
				else
					System.out.println("???");
			}
			else
				System.out.println("***Trust Anchor: Non ho trovato il certificato della CA associata al certificato del firmatario");
			
			if(cert1!=null)
			{
				CRLVerify crlVer =new CRLVerify(cert1);
				System.out.println("***Verifica delle liste di revoca del certificato del firmatario:");
				List<String> distPoint = crlVer.getDistrPoint();
				System.out.println("***Centri di distribuzione delle liste di revoca:");
				Iterator<String> it3= distPoint.iterator();
				while (it3.hasNext())
					System.out.println("\t"+it3.next());
				System.out.println();
				if(crlVer.verifyCertificateCRLs())
					System.out.println("esito: true. Il certificato non è stato revocato");
				else
					System.out.println("esito: false. ATTENZIONE!! Il certificato è stato revocato!");
				
			}
			System.out.println("<---------------------");
		}//fine while
	}//fine main
}
