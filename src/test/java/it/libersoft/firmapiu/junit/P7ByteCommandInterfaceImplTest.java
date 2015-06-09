/**
 * 
 */
package it.libersoft.firmapiu.junit;

import static org.junit.Assert.*;
import it.libersoft.firmapiu.ResultInterface;
import it.libersoft.firmapiu.cades.CMSReport;
import it.libersoft.firmapiu.cades.CadesBESFactory;
import it.libersoft.firmapiu.cades.P7ByteCommandInterface;
import it.libersoft.firmapiu.crtoken.KeyStoreToken;
import it.libersoft.firmapiu.crtoken.TokenFactoryBuilder;
import it.libersoft.firmapiu.data.DataByteArray;
import it.libersoft.firmapiu.data.DataFactoryBuilder;
import it.libersoft.firmapiu.data.P7SDataByteArray;
import it.libersoft.firmapiu.exception.FirmapiuException;
import static it.libersoft.firmapiu.consts.FactoryConsts.*;
import static it.libersoft.firmapiu.consts.FirmapiuRecordConstants.CERTCHAIN;
import static it.libersoft.firmapiu.consts.FirmapiuRecordConstants.LEGALLYSIGNED;
import static it.libersoft.firmapiu.consts.FirmapiuRecordConstants.OKSIGNED;
import static it.libersoft.firmapiu.consts.FirmapiuRecordConstants.SIGNERCERT;
import static it.libersoft.firmapiu.consts.FirmapiuRecordConstants.SIGNERCERTREVOKED;
import static it.libersoft.firmapiu.consts.FirmapiuRecordConstants.SIGNERCERTSTATUS;
import static it.libersoft.firmapiu.consts.FirmapiuRecordConstants.TRUSTANCHOR;
import static it.libersoft.firmapiu.consts.FirmapiuRecordConstants.TRUSTEDSIGNER;

import java.io.File;
import java.io.FileInputStream;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.bouncycastle.cms.SignerInformation;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.Log4jLoggerAdapter;

/**
 * Classe di test per verificare le funzionalità di P7ByteCommandInterface
 * 
 * @author dellanna
 *
 */
public class P7ByteCommandInterfaceImplTest {

	//livello di logging del logger
	private static Log4jLoggerAdapter LOG;

	//classe sotto test
	private static P7ByteCommandInterface p7mByteInterface;

	//parametri di test: file di cui effettuare la verifica
	private final static File FILETEST= new File("/home/andy/Scrivania/p7mfiles2/README.txt.p7m");
	private final static File P7SFILETEST = new File("/home/andy/probbblemi.txt.p7s");
	private final static File P7SFILECONTENTTEST = new File("/home/andy/probbblemi.txt");
	
	
	
	public P7ByteCommandInterfaceImplTest() {
	}



	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		LOG = (Log4jLoggerAdapter)LoggerFactory.getLogger(P7ByteCommandInterfaceImplTest.class);
		KeyStoreToken verifyToken= TokenFactoryBuilder.getFactory(KEYSTORETOKENFACTORY).getKeyStoreToken(TSLXMLKEYSTORE);
		p7mByteInterface= CadesBESFactory.getFactory().getP7ByteCommandInterface(null, verifyToken);
		LOG.info("Oggetto da testare creato: inizio batteria di test su: "+p7mByteInterface.getClass().getCanonicalName()+"\n\n");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		LOG.info("\nInizio Test: ---------------------------->\n");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		LOG.info("\n-------------------------------> Fine Test\n");
	}

	/**
	 * Test method for {@link it.libersoft.firmapiu.cades.P7ByteCommandInterfaceImpl#verifyP7S(it.libersoft.firmapiu.cades.P7SData)}.
	 */
	@Test
	public final void testVerifyP7S() throws Exception{
		LOG.info("Testa metodo verifyP7S su: {} e {}\nPreparo i parametri per effettuare il test",P7SFILETEST,P7SFILECONTENTTEST);
		FileInputStream in = new FileInputStream(P7SFILETEST);
		byte[] p7sDataTest=new byte[in.available()];
		in.read(p7sDataTest);
		
		FileInputStream in2 = new FileInputStream(P7SFILECONTENTTEST);
		byte[] p7sDataContentTest=new byte[in2.available()];
		in2.read(p7sDataContentTest);
		
		//inizializza la struttura dati utilizzata per passare i dati da verificate a P7ByteArrayCommandInterface
		P7SDataByteArray p7sData = DataFactoryBuilder.getFactory(P7SDATABYTEARRAYFACTORY).getP7SDataByteArray();
		p7sData.putP7SData(p7sDataTest, p7sDataContentTest);
		ResultInterface<byte[], CMSReport>result=p7mByteInterface.verifyP7S(p7sData);
		logVerify(result);
	}

	/**
	 * Test method for {@link it.libersoft.firmapiu.cades.AbstractCadesBESCommandInterface#sign(it.libersoft.firmapiu.Data)}.
	 */
	//@Test
	public final void testSign() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link it.libersoft.firmapiu.cades.AbstractCadesBESCommandInterface#verify(it.libersoft.firmapiu.Data)}.
	 */
	@Test
	public final void testVerify() throws Exception{
		LOG.info("Testa metodo verify su: {}\nPreparo i parametri per effettuare il test",FILETEST);
		FileInputStream in = new FileInputStream(FILETEST);
		byte[] dataTest=new byte[in.available()];
		in.read(dataTest);
		//inizializza la struttura dati utilizzata per passare i dati da verificate a P7ByteArrayCommandInterface
		DataByteArray data=DataFactoryBuilder.getFactory(DATABYTEARRAYFACTORY).getDataByteArray();
		data.setData(dataTest);
		ResultInterface<byte[], CMSReport>result=p7mByteInterface.verify(data);
		logVerify(result);
	}

	/**
	 * Test method for {@link it.libersoft.firmapiu.cades.AbstractCadesBESCommandInterface#getContentSignedData(it.libersoft.firmapiu.Data)}.
	 */
	//@Test
	public final void testGetContentSignedData() {
		fail("Not yet implemented"); // TODO
	}

	//stampa l'esito della verifica
	private static void logVerify(ResultInterface<byte[], CMSReport> result)throws Exception{
		System.out.println("Esito della verifica:");
		System.out.println();
		//controlla la verifica per ogni file
		Iterator<byte[]> itr=result.getResultDataSet().iterator();
		while(itr.hasNext()){
			byte[] dataPath = itr.next();

			System.out.println();
			System.out.println("ID ->"+dataPath);
			System.out.println("************************************");			
			//verifica la firma di tutti i firmatari per ogni file
			CMSReport signers =result.getResult(dataPath);
			Iterator<SignerInformation> sigItr = signers.getSigners().iterator();
			while(sigItr.hasNext()){
				System.out.println("Firmatario:");
				System.out.println("-------------------------------------------------------------");
				SignerInformation signerRecord = sigItr.next();
				printSignerRecord(signerRecord,signers);
				System.out.println("-------------------------------------------------------------");
			}
			System.out.println("************************************");
			System.out.println();
		}	
	}

	//stampa a video i records contenenti le informazioni di verifica per ogni firmatario
	private static void printSignerRecord(SignerInformation signerRecord,CMSReport report) throws FirmapiuException{
		Set<String> record=report.getSignerRecordFields(signerRecord);
		X509Certificate cert1 =null;
		//certificato del firmatario
		if(record.contains(SIGNERCERT))
		{
			System.out.println("***Certificato del firmatario");
			try {
				Object obj=report.getSignerField(signerRecord, SIGNERCERT);
				if(obj instanceof X509Certificate)
				{
					cert1 = (X509Certificate)obj;
					System.out.println("Issuer-->"+cert1.getIssuerDN().toString());
					System.out.println("Subject-->"+cert1.getSubjectDN().toString());
				}
			} catch (FirmapiuException e) {
				e.printStackTrace();
			}
		}
		else
			System.out.println("***Non ho trovato il certificato del firmatario");
		//verifica la firma del firmatario
		if(record.contains(OKSIGNED)){
			System.out.println("***Verifica della firma per il firmatario");
			try {
				Object obj = report.getSignerField(signerRecord, OKSIGNED);
				if(obj instanceof Boolean){
					System.out.println("Esito:"+obj);
				}
			} catch (FirmapiuException e) {
				e.printStackTrace();
			}
		}
		else
			System.out.println("***Non ho trovato l'esito della verifica della firma per il firmatario");
		//verifica che la firma del firmatario sia legale
		if(record.contains(LEGALLYSIGNED)){
			System.out.println("***Verifica che la firma sia conforme alla DELIBERAZIONE ministeriale del N . 45 DEL 21 MAGGIO 2009");
			try {
				Object obj = report.getSignerField(signerRecord, LEGALLYSIGNED);
				if(obj instanceof Boolean){
					System.out.println("Esito:"+obj);
				}
			} catch (FirmapiuException e) {
				e.printStackTrace();
			}
		}
		else
			System.out.println("***Non ho trovato l'esito della verifica della legalità della firma per il firmatario");
		//verifica che il certificato del firmatario sia affidabile
		if(record.contains(TRUSTEDSIGNER)){
			System.out.println("***Verifica che il certificato del firmatario sia affidabile");
			try {
				Object obj = report.getSignerField(signerRecord, TRUSTEDSIGNER);
				if(obj instanceof Boolean){
					System.out.println("Esito:"+obj);
				}
			} catch (FirmapiuException e) {
				e.printStackTrace();
			}
		}
		else
			System.out.println("***Non ho trovato l'esito della verifica dell'affidabilità del certificato del firmatario");
		//catena dei certificati del firmatario
		if(record.contains(CERTCHAIN))
		{
			System.out.println("***Catena dei certificati associata al certificato del firmatario");
			try {
				Object obj=report.getSignerField(signerRecord, CERTCHAIN);
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
			} catch (FirmapiuException e) {
				e.printStackTrace();
			}
		}
		else
			System.out.println("***Non ho trovato la catena dei certificati associata al certificato del firmatario");
		//"trust anchor" del certificato del firmatario
		if(record.contains(TRUSTANCHOR))
		{
			System.out.println("***Trust Anchor: Certificato della CA associata al certificato del firmatario");
			try {
				Object obj=report.getSignerField(signerRecord, TRUSTANCHOR);
				if(obj instanceof X509Certificate)
				{
					X509Certificate s1 = (X509Certificate)obj;
					System.out.println("Issuer-->"+s1.getIssuerDN().toString());
					System.out.println("Subject-->"+s1.getSubjectDN().toString());
				}
			} catch (FirmapiuException e) {
				e.printStackTrace();
			}
		}
		else
			System.out.println("***Trust Anchor: Non ho trovato il certificato della CA associata al certificato del firmatario");

		//controlla se il certificato è stato revocato
		if(record.contains(SIGNERCERTSTATUS))
		{
			System.out.println("***Signer Certificate Status: Stato di revoca del certificato del firmatario");
			try {
				Object obj=report.getSignerField(signerRecord, SIGNERCERTSTATUS);
				if(obj instanceof String)
				{
					System.out.println("\tStato del certificato del firmatario: "+(String)obj);
				}
			} catch (FirmapiuException e) {
				e.printStackTrace();
			}
		}
		else
			System.out.println("***Non ho trovato lo status di revoca associato al certificato del firmatario");

		//controlla se il certificato era valido al tempo della firma del firmatario
		if(record.contains(SIGNERCERTREVOKED))
		{
			System.out.println("***Signer Certificate Revoked: Stato di revoca del certificato del firmatario nel momento in cui ha firmato il file");
			try {
				Object obj=report.getSignerField(signerRecord, SIGNERCERTREVOKED);
				if(obj instanceof Boolean)
				{
					System.out.println("\tStato del certificato del firmatario al momento della firma: ");
					Boolean revoked = (Boolean)obj;
					if(revoked){
						System.out.println("\tIl certificato era revocato al momento della firma: ");
					}
					else {
						System.out.println("\tIl certificato NON era revocato al momento della firma: ");
					}
				}
			} catch (FirmapiuException e) {
				e.printStackTrace();
			}
		}
		else
			System.out.println("***Non ho trovato lo status di revoca associato al certificato del firmatario");
	}//fine procedura
	
	//classe interna per costruire un messaggio di log incrementale
	private final static class IncrementalLog{
		
		private Object[] arguments;
		private String msg;
		
		
		
		protected IncrementalLog(Object[] arguments, String msg) {
			super();
			this.arguments = arguments;
			this.msg = msg;
		}

		private void updateLog(String msgUpdate,Object... argumentsUpdate){
			msg+=msgUpdate;
			int newArrayLength=this.arguments.length+argumentsUpdate.length;
			//arguments={argumentsUpdate};
		}
		
		private void info(){}
	}
}
