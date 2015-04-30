/**
 * 
 */
package it.libersoft.firmapiu.consts;

import static it.libersoft.firmapiu.consts.FactoryConsts.PKCS11TOKENFACTORY;

/**
 * Questa classe contiene una serie di costanti che descrivono le factories che possono essere create dalla MasterBuilderFactory<br>
 * e gli oggetti che possono essere creati dalle factories stesse.
 * 
 * @author dellanna
 *
 */
public final class FactoryConsts {
	
	//descrive le factories che possono essere create dalla MsterFactoryBuilder
	
	/**
	 * Factory per la firma e verifica di dati in formato Cades-BES
	 */
	public final static String CADESBESFACTORY="cadesbesFactory";
	
	/**
	 * Factory per la creazione di un insieme di file da passare come parametro di input alle operazioni di firma e verifica
	 * 
	 * @see it.libersoft.firmapiu.Data
	 */
	public final static String DATAFILEFACTORY="dataFileFactory";
	
	/**
	 * Factory per la creazione di un insieme di busta crittografiche o contenuti di file, rappresentati come array di byte, 
	 * da passare come parametro di input alle operazioni di firma e verifica
	 * 
	 * @see it.libersoft.firmapiu.Data
	 */
	public final static String DATABYTEARRAYFACTORY="dataByteArrayFactory";
	
	/**
	 * Factory per la creazione di token pkcs#11 utilizzati dalle operazioni della libreria
	 * per accedere alle credenziali messe a disposizione e per la gestione del token stesso
	 * 
	 * @see it.libersoft.firmapiu.crtoken.PKCS11Token
	 */
	public final static String PKCS11TOKENFACTORY="pkcs11tokenfactory";
	
	/**
	 * Factory per la creazione di un token crittografico "software" per la gestione di un keystore   
	 * che può essere usato dalle operazioni messe a disposizione dalla libreria 
	 * 
	 * @see it.libersoft.firmapiu.crtoken.KeyStoreToken
	 */
	public final static String KEYSTORETOKENFACTORY="keystoretokenfactory";
	
	//descrive i tipi di oggetto che le factories possono creare
	
	
	/**
	 * La factory Cades-BES genera un oggetto che firma e verifica dei files imbustati in una busta crittografica Cades-BES.<br>
	 * Contenuto del file originale "attached" nella busta crittografica
	 */
	public final static String P7MFILE="p7mfile";
	/**
	 * La factory Cades-BES genera un oggetto che firma e verifica dei files imbustati in una busta crittografica Cades-BES.<br>
	 * Contenuto del file originale "detached": il file originale non è presente nella busta crittografica 
	 */
	public final static String P7SFILE="p7sfile";
	
	/**
	 * La factory DataFileFactory crea un insieme di file da passare come parametro di input alle operazioni di firma e verifica
	 * 
	 * @see it.libersoft.firmapiu.Data
	 */
	public final static String DATAFILEPATH="dataFilePath";
	
	/**
	 * La factory ArgumentFactory crea gli argomenti generici da passare come parametro di input alle operazioni di firma e verifica
	 * 
	 * @see it.libersoft.firmapiu.Argument
	 */
	public final static String GENERICARGUMENT="genericArgument";
	
	/**
	 * La Factory PKCS11TokenFactory crea un token per gestire una smartcard crittografica
	 * 
	 * @see it.libersoft.firmapiu.crtoken.PKCS11Token
	 * */
	public final static String CRTSMARTCARD="crtsmartcard";

	/**
	 * Crea un keystore parsando i certificati contenuti in un TSL (Trust Service status List) in formato xml
	 * 
	 * @see it.libersoft.firmapiu.crtoken.KeyStoreToken
	 */
	public final static String TSLXMLKEYSTORE="tslxmlkeystore";
	
}
