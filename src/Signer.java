import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;


import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Store;

import static org.bouncycastle.cms.CMSSignedDataGenerator.*;
/**
 * 
 */

/**
 * @author dellanna
 *
 */
public class Signer {
	
	//password della  carta utilizzata per firmare il file
	//public final static char[] PASSWORD = "12345678".toCharArray();
	public final static char[] PASSWORD = "87654321".toCharArray();
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		//inizializza i providers di Bouncy Castle e PKCS#11 (per usare la carta)
		Security.addProvider(new BouncyCastleProvider());
		String pcsc = "name=pcsc\nlibrary=/usr/lib/libbit4xpki.so";
		//String pcsc = "name=pcsc\nlibrary=/usr/lib/libASEP11.so";
		Provider p = new sun.security.pkcs11.SunPKCS11(new ByteArrayInputStream(pcsc.getBytes()));
		Security.addProvider(p);
		//recupera e carica il keystore del provider SunPKCS11-pcsc (la carta)
        KeyStore pkcs11keystore = KeyStore.getInstance("pkcs11", "SunPKCS11-pcsc");
        pkcs11keystore.load(null, PASSWORD);
        //alias del certificato da usare per firmare il file
        //String alias="DS User Certificate1";
        String alias="Firma0";
        //recupera la chiave privata per firmare un file
        System.out.println("***Recupero la chiave privata per firmare il file");
        Key privatekey=pkcs11keystore.getKey(alias, PASSWORD);
        if(privatekey==null){
        	System.err.println("Attenzione non sono riuscito a trovare la chiave privata sulla carta per firmare il file"); System.exit(-1);}
      //  System.out.println("\n\n---------"+privatekey.getClass().toString()+"\n"+privatekey.getAlgorithm()+"\n"+privatekey.getFormat()+"\n"+privatekey.toString()+"-------");
        
        //recupera la catena di certificati utilizzati per verificare la firma di un file 
        System.out.println("***Recupero la catena di certificati per verificare la firma");
        Certificate[] certList=pkcs11keystore.getCertificateChain(alias);
        
        //recupera il file da firmare
        System.out.println("***Recupero il file da firmare");
        File filein = new File(args[0]);
        
        //genera il p7m e lo salva su un file
        System.out.println("***Firmo il file: "+args[0]+"\t\nprocessing...");
        byte[] p7m_encoded = p7mFileGenerator(new FileInputStream(new File(args[0])), privatekey, certList);
        //System.out.println("\n\nrpova\n");
        //System.out.println(new String(((ByteArrayOutputStream)p7m_outstream).toByteArray()));
        System.out.println("***Salvo il file su "+args[0]+".p7m");
        File p7m_file = new File(args[0]+".p7m");
        new FileOutputStream(p7m_file).write(p7m_encoded);
        
        
        
        /*X509Certificate cert = null;
       	String tmp;
        Enumeration<String> aliases = pkcs11keystore.aliases();
        Key privateKey=null;
        while(aliases.hasMoreElements()){
        	tmp=aliases.nextElement();
        	cert=(X509Certificate) pkcs11keystore.getCertificate(tmp);
        	if (cert.getKeyUsage()[1]){
        		privateKey=pkcs11keystore.getKey(tmp, pass.toCharArray());
        		break;
        	}
        	
        }//fine while*/
        //if(privatekey==null){
        //	System.err.println("Attenzione non sono riuscito a trovare la chiave privata sulla carta per firmare il file"); System.exit(-1);}

        //recupera la catena di certificati per controllare la firma
     
        
	}

	//PROCEDURA PRIVATA PER GENERARE IL FILE P7M
	/**
	 * @param input_file file da firmare
	 * @param privatekey chiave privata per firmare
	 * @param certList catena dei certificati utilizzati per verificare firma
	 * @return il file p7m
	 */
	private static byte[] p7mFileGenerator(InputStream instream, Key privatekey , Certificate[] certlist ) throws Exception{
        
	
        //recupera il contenuto del file sotto forma di stream di byte
		byte[] filein_bytedata= new byte[instream.available()];
		instream.read(filein_bytedata);
        CMSTypedData msg = new CMSProcessableByteArray(filein_bytedata);
         
        //aggiunge la catena dei certificati
        X509Certificate[] x509certlist = (X509Certificate[])certlist;
        List<X509Certificate> certList = new ArrayList<X509Certificate>();
        for (int i=0 ;i<x509certlist.length;i++)
        certList.add(x509certlist[i]);
        
        Store  certs = new JcaCertStore(certList);
        
        
    	//crea il p7m generator
		CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
        
        
        
        //crea il firmatario e lo aggiunge al p7m generator
        //TODO attenzione all'algoritmo di firma
        //ContentSigner sha1Signer = new JcaContentSignerBuilder("SHA256withRSA").setProvider("BC").build((PrivateKey)privatekey);
        ContentSigner sha1Signer = new JcaContentSignerBuilder("SHA256withRSA").setProvider("SunPKCS11-pcsc").build((PrivateKey)privatekey);
        X509Certificate signCert=x509certlist[0];
        gen.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().setProvider("BC").build()).build(sha1Signer, signCert));

        gen.addCertificates(certs);
        
        //genera il il file p7m
        CMSSignedData sigData = gen.generate(msg, true);
        /*Collection<?> coll=sigData.getSignerInfos().getSigners();
        Iterator<?> itr=coll.iterator();
        /*System.out.println("Elenco signers");
        while (itr.hasNext()){
        	Object obj=itr.next();
        	System.out.println(obj.getClass());
        	System.out.println(new String(((org.bouncycastle.cms.SignerInformation)obj).getSignature()));
        }
        System.out.println("fine elenco signers");*/
        //System.out.println(new String(sigData.getEncoded()));
        //ByteArrayOutputStream outstream =new ByteArrayOutputStream();
		
		return sigData.getEncoded();
		
	}
}
