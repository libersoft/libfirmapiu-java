/**
 * 
 */
package it.libersoft.firmapiu.junit;

import static org.junit.Assert.*;

import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;
import java.util.logging.Handler;
import java.util.logging.Logger;

import it.libersoft.firmapiu.MasterFactoryBuilder;
import it.libersoft.firmapiu.crtoken.PKCS11Token;
import static it.libersoft.firmapiu.consts.FactoryConsts.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * Semplice Test per verificare correttezza classe
 * 
 * @author dellanna
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CRTSmartCardTokenSimpleTest {

	private static PKCS11Token smartcard;
	private static Logger LOG;
	
	//info sulla carta specifica da usare
	public static String name;
	public static char[] pass;
	
	
	/*public static void setNamePass(){
		name=null;
		pass=null;
	}*/
	
	/**
	 * @throws java.lang.Exception
	 */
	public static void setUpBeforeClassProcedure() throws Exception {
		LOG = Logger.getLogger(CRTSmartCardTokenSimpleTest.class.getName());
		LOG.setUseParentHandlers(false);
		LOG.addHandler(new DualConsoleHandler());
		System.out.println("INSERIRE SMARTCARD <"+name+"> PER ESEGUIRE IL TEST CORRETTAMENTE!");
		Thread.sleep(10000);
		smartcard = (PKCS11Token)MasterFactoryBuilder.getFactory(PKCS11TOKENFACTORY).getToken(CRTSMARTCARD);
		LOG.info("Oggetto da testare creato: inizio batteria di test su: "+smartcard.getClass().getCanonicalName());
		System.out.println();
		System.out.println("---------------------------------------------------------");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.out.println("---------------------------------------------------------");
		System.out.println();
		LOG.info("BATTERIA DI TEST SU CARTA <"+name+"> TERMINATI.");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		System.out.println();
		System.out.println("*****************************************************************");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		System.out.println("*****************************************************************");
		System.out.println();
	}

	/**
	 * Test method for {@link it.libersoft.firmapiu.crtoken.CRTSmartCardToken#getATR()}.
	 */
	@Test
	public void a01testGetATR() throws Exception{
		LOG.info("Testo metodo getATR():");
		byte[] atr=smartcard.getATR();
		assertNotNull(atr);
		LOG.info("atr:"+getHexString(atr));
	}
	
	/**
	 * Test method for {@link it.libersoft.firmapiu.crtoken.CRTSmartCardToken#getProvider()}.
	 */
	@Test
	public void a02testGetProvider() throws Exception{
		LOG.info("Testo metodo getProvider:");
		Provider pkcs11prov= smartcard.getProvider();
		assertNotNull(pkcs11prov);
		LOG.info("Provider:"+pkcs11prov.getName());
	}

	/**
	 * Test method for {@link it.libersoft.firmapiu.crtoken.CRTSmartCardToken#login(char[])}.
	 */
	@Test
	public void a03testLogin() throws Exception{
		LOG.info("Testo metodo login():");
		smartcard.login(pass);
		LOG.info("Sessione inizializzata:");
	}

	/**
	 * Test method for {@link it.libersoft.firmapiu.crtoken.CRTSmartCardToken#loadKeyStore(char[])}.
	 */
	@Test
	public void a04testGetKeyStore() throws Exception{
		LOG.info("Testo metodo getKeystore():");
		KeyStore pkcs11Keystore = smartcard.loadKeyStore(pass);
		assertNotNull(pkcs11Keystore);
		LOG.info("Keystore not null!");
	}
	
	/**
	 * Test method for {@link it.libersoft.firmapiu.crtoken.CRTSmartCardToken#logout()}.
	 */
	@Test
	public void a05testLogout() throws Exception{
		LOG.info("Testo metodo logout():");
		smartcard.logout();
		LOG.info("Sessione terminata: Testo che il provider sia stato rilasciato");
		Provider prov=Security.getProvider(smartcard.getProvider().getName());
		assertNull(prov);
		LOG.info("il provider è stato rilasciato");
	}
	
	//PRocedure private
	//restituisce la rappresentazione esadecimale di un array di byte
		private static String getHexString(byte[] dataInput){

			String mes = "Utils getHexString:  ";
			String result = "";

			// verifica parametro
			if(dataInput == null){
				System.err.println(mes + "parametro null!!");
				System.exit(1);
			}

			for (int i = 0; i < dataInput.length; i++){
				result += Integer.toString((dataInput[i] & 0xff) + 0x100, 16).substring(1);
			}
			return result;
		}
}
