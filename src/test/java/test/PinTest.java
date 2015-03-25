/**
 * 
 */
package test;

import it.libersoft.firmapiu.MasterFactoryBuilder;
import it.libersoft.firmapiu.exception.FirmapiuException;
import static it.libersoft.firmapiu.consts.FactoryConsts.*;
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
		
		PKCS11Token pkcs11Token = (PKCS11Token) MasterFactoryBuilder.getFactory(PKCS11TOKENFACTORY).getToken(CRTSMARTCARD);
		
		char[] oldPin=args[0].toCharArray();
		char[] newPin=args[1].toCharArray();
		
		//TODO commentare e "scommentare" le parti di codice richieste per eseguire il test
		//testa il cambio del pin
		pkcs11Token.setPin(oldPin, newPin);
		//testa lo sblocco del PIN
		//pkcs11Token.unlockPKCS11Token(oldPin, newPin);
		//testa il cambio del PUK
		//pkcs11Token.setPuk(null, oldPin, newPin);
	}
}
