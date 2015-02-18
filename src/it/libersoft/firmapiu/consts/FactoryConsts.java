/**
 * 
 */
package it.libersoft.firmapiu.consts;

/**
 * Questa classe contiene una serie di costanti utilizzabili dalle Factories per costruire le classi concrete
 * 
 * @author dellanna
 *
 */
public final class FactoryConsts {
	
	
	/**
	 * Factory per la firma e verifica di dati in formato Cades-BES
	 */
	public final static String CADESBESFACTORY="cadesbesFactory";
	
	/**
	 * Factory per la creazione di un insieme di dati da passare come parametro di input alle operazioni di firma e verifica
	 * 
	 * @see it.libersoft.firmapiu.Data
	 */
	public final static String DATAFACTORY="dataFactory";
	
	/**
	 * Factory per la creazione di argomenti da passare come parametro di input alle operazioni di firma e verifica
	 * 
	 * @see it.libersoft.firmapiu.Argument
	 */
	public final static String ARGUMENTFACTORY="argumentFactory";
	
	/**
	 * Factory per la creazione di token pkcs#11 utilizzati dalle operazioni della libreria
	 * per accedere alle credenziali messe a disposizione e per la gestione del token stesso
	 * 
	 * @see it.libersoft.firmapiu.crtoken.CRTSmartCardToken
	 */
	public final static String PKCS11TOKENFACTORY="pkcs11tokenfactory";
	
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
	 * La factory DataFactory crea un insieme di file da passare come parametro di input alle operazioni di firma e verifica
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
	 * @see it.libersoft.firmapiu.crtoken.CRTSmartCardToken
	 * */
	public final static String CRTSMARTCARD="crtsmartcard";
	
}
