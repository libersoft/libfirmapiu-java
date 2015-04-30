/**
 * 
 */
package test;

import it.libersoft.firmapiu.DefaultFactory;
//import it.libersoft.firmapiu.MasterFactoryBuilder;
import it.libersoft.firmapiu.exception.FirmapiuException;
import static it.libersoft.firmapiu.consts.FactoryConsts.*;
import static it.libersoft.firmapiu.consts.FactoryPropConsts.*;
import it.libersoft.firmapiu.crtoken.*;

/**
 * Classe di Test NON-standard per verificare le funzionalità della libreria riguardo il cambio del pin/puk/sblocco pin
 * 
 * @author dellanna
 *
 */
final class PinTest {

	/**
	 * @param args array contenente il pin vecchio e nuovo da cambiare
	 * @throws FirmapiuException 
	 * @throws IllegalArgumentException 
	 */
	public static void main(String[] args) throws IllegalArgumentException, FirmapiuException {
		
		//TODO commentare e "scommentare" le parti di codice richieste per eseguire il test
		//imposta le proprietà della factory
		DefaultTokenFactory factory = TokenFactoryBuilder.getFactory(PKCS11TOKENFACTORY);
		factory.setProperty(CRT_TOKEN_PIN_ONLYNUMBER, true);
		factory.setProperty(CRT_TOKEN_PIN_MINLENGTH, 5);
		factory.setProperty(CRT_TOKEN_PIN_MAXLENGTH, 8);
		PKCS11Token pkcs11Token = (PKCS11Token) factory.getPKCS11Token(CRTSMARTCARD);
		
		char[] oldPin=args[0].toCharArray();
		char[] newPin=args[1].toCharArray();
		
		
		//testa il cambio del pin
		//kcs11Token.setPin(oldPin, newPin);
		//testa lo sblocco del PIN
		//pkcs11Token.unlockPKCS11Token(oldPin, newPin);
		//testa il cambio del PUK
		//pkcs11Token.setPuk(null, oldPin, newPin);
		//testa la verifica del pin
		//System.out.println(pkcs11Token.verifyPin(oldPin));
		
		//System.out.println(pkcs11Token.getPinRemainingAttempts());
		
	}
}
