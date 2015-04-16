package test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * 
 */

/**
 * @author dellanna
 *
 */
public class Prova {

	/**
	 * @param args
	 * @throws NoSuchProviderException 
	 * @throws KeyStoreException 
	 * @throws IOException 
	 * @throws CertificateException 
	 * @throws NoSuchAlgorithmException 
	 */
	public static void main(String[] args) throws KeyStoreException, NoSuchProviderException, NoSuchAlgorithmException, CertificateException, IOException {
	
	
	
		Security.addProvider(new BouncyCastleProvider());
		
		//ricava il file di configurazione
		//FileInputStream filein=new FileInputStream(new File(args[0]));
		//String pcsc = "name=pcsc\nlibrary=/usr/local/lib/libsiecap11.so";
		String pcsc = "name=pcsc\nlibrary=/usr/lib/libbit4xpki.so";
		//String pcsc = "name=pcsc\nlibrary=/lib64/libASEP11.so";
		Provider p = new sun.security.pkcs11.SunPKCS11(new ByteArrayInputStream(pcsc.getBytes()));
		//Provider p = new sun.security.pkcs11.SunPKCS11(filein);
		Security.addProvider(p);
		
        //stampa a video i provider
		
		String providerName = "BC";
        
		System.out.println("Lista dei providers****");
		
        if (Security.getProvider(providerName) == null)
        {
            System.out.println(providerName + " provider not installed");
        }
        else
        {
            System.out.println(providerName + " is installed.");
        }
	
        Provider[]	providers = Security.getProviders();
        
        for (int i = 0; i != providers.length; i++)
        {
            System.out.println("Name: " + providers[i].getName() +" "+providers[i].getInfo() +  " Version: " + providers[i].getVersion());
        }
       
        System.out.println();
        System.out.println("Lista degli alias del provider SunPKCS11-pcsc");
        System.out.println();
        
        //recupera il keystore del provider SunPKCS11-pcsc
        KeyStore pkcs11keystore = KeyStore.getInstance("pkcs11", "SunPKCS11-pcsc");
        
        //String pass="12345678";
        //String pass="55732689";
        //String pass="87654321";
        String pass=args[0];
        
        pkcs11keystore.load(null, pass.toCharArray());  
       
        X509Certificate cert = null;
        //stampa a video gli aliases
        Enumeration<String> aliases = pkcs11keystore.aliases();
        while(aliases.hasMoreElements()){
        	String tmp=aliases.nextElement();
        	System.out.println("\tAlias:<"+tmp+">");
        	cert=(X509Certificate) pkcs11keystore.getCertificate(tmp);
        	System.out.println("\t\tuso del certificato:");
        	boolean[] usage=cert.getKeyUsage();
        	/*for(int i=0 ;i<usage.length;i++)
        		System.out.println(usage[i]);*/
        	System.out.println("digitalSignature"+usage[0]);
            System.out.println("nonRepudiation"+usage[1]);
            System.out.println("keyEncipherment"+usage[2]);
            System.out.println("dataEncipherment"+usage[3]);
            System.out.println("keyAgreement"+usage[4]);
            System.out.println("keyCertSign"+usage[5]);
            System.out.println("cRLSign"+usage[6]);
            System.out.println("encipherOnly"+usage[7]);
            System.out.println("decipherOnly"+usage[8]);
            System.out.println("nome certificato: "+cert.getIssuerX500Principal().getName());
            System.out.println("Data di creazione certificato: "+cert.getNotBefore().toString());
            System.out.println("Data di espirazione del certificato: "+cert.getNotAfter().toString());
            System.out.println("Catena di certificati: ");
            Certificate[] certList=pkcs11keystore.getCertificateChain(tmp);
            for(int i=0;i<certList.length;i++){
            	System.out.println("("+i+") "+((X509Certificate)certList[i]).getIssuerX500Principal().getName() );
            	System.out.println("("+i+") "+((X509Certificate)certList[i]).getSubjectDN().getName() );
            }
        }
        
        
       
	}

}
