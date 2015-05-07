/**
 * 
 */
package it.libersoft.firmapiu.cades;

import static it.libersoft.firmapiu.consts.FactoryConsts.*;
import static it.libersoft.firmapiu.consts.FactoryPropConsts.*;
import it.libersoft.firmapiu.CRToken;
import it.libersoft.firmapiu.DefaultFactory;
import it.libersoft.firmapiu.crtoken.PKCS11TokenFactory;
import it.libersoft.firmapiu.crtoken.TokenFactoryBuilder;
import it.libersoft.firmapiu.exception.FirmapiuException;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TreeMap;

import sun.security.ssl.SSLContextImpl.TLS10Context;

/**
 * Costruisce l'interfaccia di comandi utilizzata per la firma e verifica di
 * dati in formato Cades-BES secondo la DELIBERAZIONE ministeriale del N . 45
 * DEL 21 MAGGIO 2009 tramite utilizzo di token crittografico.
 * 
 * @author dellanna
 *
 */
public class CadesBESFactory extends DefaultFactory{
	
	/**
	 * 
	 */
	protected CadesBESFactory() {
		super();
	}

	/**
	 * Inizializza la Cades-BES factory
	 * 
	 * @return la factory creata
	 */
	public static CadesBESFactory getFactory(){
		return new CadesBESFactory();
	}
	
	/**
	 * Crea una interfaccia di comandi specializzata per la creazione e la verifica e la gestione di file .p7m .p7s
	 * 
	 * @param filetype Il tipo di file considerato .p7m .p7s
	 * @param signToken il token utilizzato per le operazioni di firma
	 * @param verifyToken il token utilizzato per le operazioni di verifica
	 * @return un interfaccia P7FileCommandInterface di comandi specializzata per la gestione di file .p7m .p7s
	 * imbustati secondo la busta crittografica Cades-BES
	 * 
	 * @see it.libersoft.firmapiu.consts.FactoryConsts
	 */
	public P7FileCommandInterface getP7FileCommandInterface(CRToken signToken,CRToken verifyToken){
		return new P7FileCommandInterfaceImpl(signToken, verifyToken);
	}
	
	
	/**
	 * Crea una interfaccia di comandi specializzata per la creazione e la verifica e la gestione di file .p7m .p7s
	 * Tenta di inizializzare i Token di firma e verifica usando le proprietà definite nella CadesBESFactory
	 * 
	 * @param filetype Il tipo di file considerato .p7m .p7s
	 * @return un interfaccia P7FileCommandInterface di comandi specializzata per la gestione di file .p7m .p7s
	 * imbustati secondo la busta crittografica Cades-BES
	 * 
	 * @see it.libersoft.firmapiu.consts.FactoryConsts
	 */
	public P7FileCommandInterface getP7FileCommandInterface() throws FirmapiuException{
		//recupera le proprietà
		//token per la firma
		String signTokenType = (String) this.getProperty(CRT_SIGN_TOKEN);
		CRToken signToken=null;
		if (signTokenType!=null)
		{
			try {
				//cerca di caricare il token usando un classloader
				Class<?> signClass = ClassLoader.getSystemClassLoader().loadClass(signTokenType);
				signToken =(CRToken)signClass.newInstance();
			} catch (Exception e) {
				signToken = TokenFactoryBuilder.getFactory(PKCS11TOKENFACTORY).getPKCS11Token(CRTSMARTCARD);
			}
		}//se la proprietà non è definita, crea il token con la factory tokenfactory
		else
			signToken = TokenFactoryBuilder.getFactory(PKCS11TOKENFACTORY).getPKCS11Token(CRTSMARTCARD);
		//token per la firma
		String verifyTokenType = (String) this.getProperty(CRT_VERIFY_TOKEN);
		CRToken verifyToken=null;
		if (verifyTokenType!=null)
		{
			try {
				//cerca di caricare il token usando un classloader
				Class<?> verifyClass = ClassLoader.getSystemClassLoader().loadClass(verifyTokenType);
				verifyToken =(CRToken)verifyClass.newInstance();
			} catch (Exception e) {
				//TODO ??
				verifyToken = TokenFactoryBuilder.getFactory(KEYSTORETOKENFACTORY).getPKCS11Token(CRTSMARTCARD);
			}
		}//se la proprietà non è definita, crea il token con la factory tokenfactory
		else
			verifyToken = TokenFactoryBuilder.getFactory(KEYSTORETOKENFACTORY).getKeyStoreToken(TSLXMLKEYSTORE);
		
		return new P7FileCommandInterfaceImpl(signToken, verifyToken);
	}
	
	/**
	 * Crea una interfaccia di comandi specializzata per la creazione e la verifica e la gestione di file .p7m .p7s
	 * rappresentati come array di bytes
	 * 
	 * @param filetype Il tipo di file considerato .p7m .p7s
	 * @param signToken il token utilizzato per le operazioni di firma
	 * @param verifyToken il token utilizzato per le operazioni di verifica
	 * @return un interfaccia P7ByteCommandInterface di comandi specializzata per la gestione di file .p7m .p7s
	 * imbustati secondo la busta crittografica Cades-BES e rappresentati come array di bytes
	 * 
	 * @see it.libersoft.firmapiu.consts.FactoryConsts
	 */
	public P7ByteCommandInterface getP7ByteCommandInterface(CRToken signToken,CRToken verifyToken){
		//TODO
		return null;
	}
}
