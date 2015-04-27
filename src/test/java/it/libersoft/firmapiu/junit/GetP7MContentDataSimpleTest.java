/**
 * 
 */
package it.libersoft.firmapiu.junit;

import static org.junit.Assert.*;
import it.libersoft.firmapiu.DataFilePath;
import it.libersoft.firmapiu.GenericArgument;
import it.libersoft.firmapiu.MasterFactoryBuilder;
import it.libersoft.firmapiu.cades.P7FileCommandInterface;
import it.libersoft.firmapiu.exception.FirmapiuException;
import static it.libersoft.firmapiu.consts.FactoryConsts.*;
import static it.libersoft.firmapiu.consts.ArgumentConsts.*;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.Log4jLoggerAdapter;

/**
 * Semplice test per recuperare il contenuto originale di una serie di file p7m passati come parametro
 * 
 * @author dellanna
 *
 */
public final class GetP7MContentDataSimpleTest {

	//private static Logger LOG;
	//livello di logging del logger
	private static Log4jLoggerAdapter LOG;
	//private final static Level LOGLEVEL=Level.ALL;
	private static P7FileCommandInterface p7mFileInterface;
	
	//path sul quale tutto dovrebbe andar bene
	private final static String OK_FILEPATH="/home/andy/Scrivania/p7mfiles2/README.txt.p7m";
	//path di un file sul quale deve essere restituito un errore di encoding
	private final static String ENCODINGERRORP7M_FILEPATH="/home/andy/Scrivania/p7mfiles2/262 Art All In One 56-69.pdf.p7m";
	//path sul quale deve essere restituito un errore perchè il file non è un p7m
	private final static String NOTP7M_FILEPATH="/home/andy/overview.html";
	//path sul quale deve essere restituito un errore perché il file non esiste
	private final static String FILENOTFOUND_FILEPATH="/home/truffolo/pippo.txt.p7m";
	//path sul quale deve essere restituito un errore perché non è assoluto
	private final static String NOTABS_FILEPATH="./pippo.txt.p7m";
	
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		//PropertyConfigurator.configure("/home/andy/libersoftspace/firmapiulib/src/test/resources/log4j.properties");
		LOG = (Log4jLoggerAdapter)LoggerFactory.getLogger(GetP7MContentDataSimpleTest.class);
		p7mFileInterface=MasterFactoryBuilder.getFactory(CADESBESFACTORY).getCadesBESCommandInterface(P7MFILE);
		LOG.info("Oggetto da testare creato: inizio batteria di test su: "+p7mFileInterface.getClass().getCanonicalName()+"\n\n");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		LOG.info("Batteria di test terminata: pulisco i file di test");
		String deleteFilePath="/home/andy/Scrivania/p7mfiles2/README.txt";
		deleteFile(deleteFilePath);
		deleteFilePath="/home/andy/Scrivania/README.txt";
		deleteFile(deleteFilePath);
		deleteFilePath="/home/andy/Scrivania/prova/README.txt";
		deleteFile(deleteFilePath);
		deleteFilePath="/home/andy/Scrivania/prova";
		deleteFile(deleteFilePath);
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
	 * 
	 * Test method for {@link it.libersoft.firmapiu.cades.P7FileCommandInterfaceImpl#getContentSignedData(it.libersoft.firmapiu.Data, it.libersoft.firmapiu.Argument)}.<p>
	 *
	 * Testa il metodo su una serie di file senza passare argomenti opzionali
	 */
	@Test
	public final void testGetContentSignedDataWithoutOPT() throws FirmapiuException{
		LOG.info("Testo i file salvando i risultati nella stessa directory in cui i file sono testati");
		GenericArgument option = (GenericArgument)MasterFactoryBuilder.getFactory(ARGUMENTFACTORY).getArgument(GENERICARGUMENT);
		testContentData(option);
	}//fine test

	/**
	 * 
	 * Test method for {@link it.libersoft.firmapiu.cades.P7FileCommandInterfaceImpl#getContentSignedData(it.libersoft.firmapiu.Data, it.libersoft.firmapiu.Argument)}.<p>
	 *
	 * Testa il metodo su una serie di file passandogli la directory nella quale ddevono essere salvati. NON crea la directory
	 */
	@Test
	public final void testGetContentSignedDataWithOPT1() throws FirmapiuException{
		LOG.info("Testo i file salvandoli nella scrivania");
		GenericArgument option = (GenericArgument)MasterFactoryBuilder.getFactory(ARGUMENTFACTORY).getArgument(GENERICARGUMENT);
		option.setArgument(OUTDIR, "/home/andy/Scrivania");
		testContentData(option);
	}//fine test
	
	/**
	 * 
	 * Test method for {@link it.libersoft.firmapiu.cades.P7FileCommandInterfaceImpl#getContentSignedData(it.libersoft.firmapiu.Data, it.libersoft.firmapiu.Argument)}.<p>
	 *
	 * Testa il metodo su una serie di file passandogli la directory nella quale ddevono essere salvati. CREA la directory
	 */
	@Test
	public final void testGetContentSignedDataWithOPT2() throws FirmapiuException{
		LOG.info("Testo i file salvando i risultati creando una nuova directory");
		GenericArgument option = (GenericArgument)MasterFactoryBuilder.getFactory(ARGUMENTFACTORY).getArgument(GENERICARGUMENT);
		option.setArgument(OUTDIR, "/home/andy/Scrivania/prova");
		option.setArgument(CREATEOUTDIR, new Boolean(true));
		testContentData(option);
	}//fine test

	//PROCEDURE PRIVATE
	//esegue il test passandogli gli argomenti come parametro
	private void testContentData(GenericArgument option) throws FirmapiuException{
		DataFilePath signedData = (DataFilePath)MasterFactoryBuilder.getFactory(DATAFACTORY).getData(DATAFILEPATH);
		signedData.setData(OK_FILEPATH);
		signedData.setData(OK_FILEPATH);
		signedData.setData(ENCODINGERRORP7M_FILEPATH);
		signedData.setData(NOTP7M_FILEPATH);
		signedData.setData(FILENOTFOUND_FILEPATH);
		
		//signedData.setData(NOTABS_FILEPATH);	
		Map<?,?> result=p7mFileInterface.getContentSignedData(signedData, option);
		String testResult="Risultato del test:\n";
		
		Iterator<?> itr=result.keySet().iterator();
		while(itr.hasNext()){
			String line="key -> ";
			String key=(String)itr.next();
			line+=key+" ";
			Object value=result.get(key);
			if (value instanceof String){
				File targetFile = new File((String)value);
				assertTrue(targetFile.exists());
				line+="value -> "+value+" OK! il file esiste";
			} else if (value instanceof FirmapiuException){
				FirmapiuException valueException=(FirmapiuException) value;
				LOG.debug("errorCode: {} errorMsg: {}",valueException.errorCode,valueException.getLocalizedMessage());
				if (key.equals(ENCODINGERRORP7M_FILEPATH)){
					assertEquals(Integer.valueOf(FirmapiuException.CONTENT_CADESBES_ENCODINGERROR_ATTACHED), Integer.valueOf(valueException.errorCode));
					line+="value -> "+valueException.errorCode+" : OK! il file non è stato codificato: ";
					line+="errorMessage -> "+valueException.getLocalizedMessage();
				}else if(key.equals(NOTP7M_FILEPATH)){
					//assertSame(FirmapiuException.CONTENT_CADESBES_NOTP7MFILE, valueException.errorCode);
					assertEquals(Integer.valueOf(FirmapiuException.CONTENT_CADESBES_NOTP7MFILE), Integer.valueOf(valueException.errorCode));
					line+="value -> "+valueException.errorCode+" : OK! il file non è un p7m: ";
					line+="errorMessage -> "+valueException.getLocalizedMessage();
				}else if(key.equals(FILENOTFOUND_FILEPATH)){
					//assertSame(FirmapiuException.FILE_NOTFOUND, valueException.errorCode);
					assertEquals(Integer.valueOf(FirmapiuException.FILE_NOTFOUND), Integer.valueOf(valueException.errorCode));
					line+="value -> "+valueException.errorCode+" : OK! il file non esiste: ";
					line+="errorMessage -> "+valueException.getLocalizedMessage();
				}else {
					//eccezioni generiche
					line+="value -> "+valueException.errorCode+" : ";
					line+="errorMessage -> "+valueException.getLocalizedMessage();
				}
			}
			line+="\n";
			testResult+=line;
		}//fine while
		LOG.info(testResult);
	}//fine metodo
	
	private static void deleteFile(String path){
		File del =new File(path);
		if(del.exists()){
			if (del.delete()) {
				LOG.info("Ho cancellato il file {}",del.getAbsolutePath());
			}			
		}
	}//fine metodo
}

