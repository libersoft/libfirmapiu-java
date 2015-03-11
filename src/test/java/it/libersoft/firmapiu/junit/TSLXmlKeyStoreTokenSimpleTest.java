/**
 * 
 */
package it.libersoft.firmapiu.junit;

import static org.junit.Assert.*;

import java.io.File;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.logging.Logger;

import it.libersoft.firmapiu.DefaultFactory;
import it.libersoft.firmapiu.MasterFactoryBuilder;
import it.libersoft.firmapiu.crtoken.KeyStoreToken;
import it.libersoft.firmapiu.exception.FirmapiuException;
import static it.libersoft.firmapiu.consts.FactoryConsts.*;
import static it.libersoft.firmapiu.consts.FactoryPropConsts.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.junit.Test;

/**
 * Semplice test per correttezza classe TSLXmlKeyStoreToken
 * 
 * @author dellanna
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TSLXmlKeyStoreTokenSimpleTest {

	private static Logger LOG;
	private static KeyStoreToken keystoreToken;
	private static String KEYSTORE_PATH;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		LOG = Logger.getLogger(TSLXmlKeyStoreTokenSimpleTest.class.getName());
		LOG.setUseParentHandlers(false);
		LOG.addHandler(new DualConsoleHandler());
		KEYSTORE_PATH="/home/andy/libersoftspace/firmapiulib/src/main/config/keystore.jks";
		DefaultFactory keyfactory = MasterFactoryBuilder.getFactory(KEYSTORETOKENFACTORY);
		String configFilePath=(String)keyfactory.getProperty(CRT_TOKEN_TSLXMLKEYSTORE_CONFIGFILEPATH);
		keystoreToken = (KeyStoreToken)keyfactory.getToken(TSLXMLKEYSTORE);
		LOG.info("Oggetto da testare creato: inizio batteria di test su: "+keystoreToken.getClass().getCanonicalName());
		LOG.info("File di configurazione usato per creare l'oggetto: "+configFilePath);
		System.out.println();
		System.out.println("---------------------------------------------------------");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.out.println();
		LOG.info("BATTERIA DI TEST SU "+keystoreToken.getClass().getCanonicalName()+" TERMINATI: cancello le risorse oggetto di test");
		//cancella il keystore di prova
		File keystoreFile= new File(KEYSTORE_PATH);
		if (keystoreFile.exists()) {
			boolean result = keystoreFile.delete();
			if (result)
				LOG.info(KEYSTORE_PATH + " : file cancellato");
			else
				LOG.info(KEYSTORE_PATH + " : attenzione file non cancellato");
		}else
			LOG.info(KEYSTORE_PATH+" non esiste.");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		System.out.println();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {	
		System.out.println();
	}

	/**
	 * Test method for {@link it.libersoft.firmapiu.crtoken.TSLXmlKeyStoreToken#createKeyStore()}.
	 * @throws FirmapiuException 
	 */
	@Test
	public void a01testCreateKeyStore() throws FirmapiuException {
		LOG.info("Testo che il keystore sia creato in "+KEYSTORE_PATH);
		keystoreToken.createKeyStore();
		File keystoreFile= new File(KEYSTORE_PATH);
		assertTrue("non ho trovato il keystore in: "+KEYSTORE_PATH, keystoreFile.exists());
		LOG.info("Il keystore è stato creato in "+KEYSTORE_PATH);
	}
	
	/**
	 * Test method for {@link it.libersoft.firmapiu.crtoken.TSLXmlKeyStoreToken#loadKeyStore(char[])}.
	 */
	@Test
	public void a02testLoadKeyStore() throws Exception{
		LOG.info("Testo che il keystore sia stato caricato insieme ai certificati scaricati dal percorso specificato in tslpath del file di configurazione");
		KeyStore kstore=keystoreToken.loadKeyStore(null);
		int size =kstore.size();
		assertTrue("Il keystore è stato caricato ma non sembra contenere certificati",size>0);
		LOG.info("Il keystore è stato caricato è contiene "+size+" certificati");
	}

	/**
	 * Test method for {@link it.libersoft.firmapiu.crtoken.TSLXmlKeyStoreToken#updateKeyStore()}.
	 * @throws FirmapiuException 
	 * @throws Exception 
	 */
	@Test
	public void a03testUpdateKeyStore() throws Exception {
		LOG.info("Testo che il keystore sia aggiornato");
		keystoreToken.updateKeyStore();
		KeyStore kstore=keystoreToken.loadKeyStore(null);
		int size =kstore.size();
		assertTrue("Il keystore è stato caricato ma non sembra contenere certificati",size>0);
		LOG.info("Il keystore è stato caricato è contiene "+size+" certificati. Sembra che il keystore sia stato aggiornato");
	}

	/**
	 * Test method for {@link it.libersoft.firmapiu.crtoken.TSLXmlKeyStoreToken#deleteKeystore()}.
	 */
	@Test
	public void a04testDeleteKeystore() throws Exception{
		LOG.info("Testo che il keystore sia stato cancellato");
		keystoreToken.deleteKeystore();
		File keystoreFile= new File(KEYSTORE_PATH);
		assertFalse("Attenzione il keystore esiste ancora",keystoreFile.exists());
		LOG.info("Sembra che il keystore sia stato cancellato correttamente");
	}
}
