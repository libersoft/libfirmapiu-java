import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.xml.bind.DatatypeConverter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.util.ASN1Dump;

/**
 * 
 */

/**
 * @author andy
 *
 */
public class TrustAnchor {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		//genera il keystore in cui ci sono i certificati delle CA
		//crea il keystore
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		char[] password = "default".toCharArray();
		ks.load(null, password);
//		KeyStore.ProtectionParameter protParam =
//		        new KeyStore.PasswordProtection(password);
		
		//parsa il file xml
		XMLInputFactory fac = XMLInputFactory.newInstance();
		XMLEventReader eventReader = fac.createXMLEventReader(new FileInputStream("/home/andy/Scaricati/IT_TSL_signed.xml"));
		int alias=0;
		while(eventReader.hasNext()) { 
			XMLEvent event = (XMLEvent) eventReader.next();
			if (event instanceof StartElement && ((StartElement)event).getName().getLocalPart().equals("X509Certificate")) {
				String b64cert=((Characters)eventReader.next()).getData();
				//per ogni certificato x509 trovato:
				//lo salva nel keystore
				b64cert="-----BEGIN CERTIFICATE-----\r\n"+b64cert+"\r\n-----END CERTIFICATE-----";
				CertificateFactory cf = CertificateFactory.getInstance("X.509");
				X509Certificate cert=(X509Certificate)cf.generateCertificate(new ByteArrayInputStream(b64cert.getBytes()));
				
				ks.setEntry(Integer.toString(alias), new KeyStore.TrustedCertificateEntry(cert), null);
				alias++;
			}
		}
		//salva il keystore su un file
	    java.io.FileOutputStream fos = null;
	    try {
	        fos = new java.io.FileOutputStream("/home/andy/keystore.jks");
	        ks.store(fos, password);
	    } finally {
	        if (fos != null) {
	            fos.close();
	        }
	    }
		
		
	}

}
