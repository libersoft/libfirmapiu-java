/**
 * 
 */
package test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.AuthProvider;
import java.security.cert.Certificate;
import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.Properties;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

/**
 * Semplice programma che effettua una sessione di login e logout sulla smartcard
 * 
 * @author dellanna
 *
 */
final class SmartCardLoginTest {

	/**
	 * @param args gli argomenti passati al programma
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		if (args.length==0)
		{
			System.err.println("Devi specificare un pin!");
			System.exit(-1);
		}
		
		loginProcedure(args[0].toCharArray());
		System.out.println();
		System.out.println("Cambia la carta");
		System.out.println();
		Thread.sleep(10000);
		System.out.println();
		System.out.println("Non Cambiare più la carta");
		System.out.println();
		loginProcedure(args[1].toCharArray());
		
		
	}//fine main
	
	
	private static void loginProcedure(char[] pin) throws Exception{
		//inizializza il provider pkcs#11
		Properties prop = new Properties();
		prop.load(new FileInputStream(new File("/etc/firmapiu/pkcs11driver.config")));
		String libdriver = prop.getProperty("library");
		String provPkcs11 = "name=pkcs11\nlibrary="+libdriver;
		Provider pkcs11provider = new sun.security.pkcs11.SunPKCS11(new ByteArrayInputStream(provPkcs11.getBytes()));
		Security.addProvider(pkcs11provider);
		AuthProvider aprov = (AuthProvider)Security.getProvider(pkcs11provider.getName());
		//Subject subject =new Subject();
		aprov.login(null, new PrivateCallbackHandler(pin));
		System.out.println("Loggato");
		KeyStore pkcs11keystore = KeyStore.getInstance("pkcs11", pkcs11provider);
		pkcs11keystore.load(null, null);
		
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
		
		aprov.logout();
		Security.removeProvider(pkcs11provider.getName());
	}
	
	
	private static class PrivateCallbackHandler implements CallbackHandler{

		private final char[] pass;
		
		private PrivateCallbackHandler(char[] pass){
			this.pass=pass;
		}
		
		@Override
		public void handle(Callback[] callbacks) throws IOException,
				UnsupportedCallbackException {
			PasswordCallback pc = (PasswordCallback) callbacks[0];
			pc.setPassword(this.pass);
			
		}
		
	}
}
