/**
 * 
 */
package test;

import it.libersoft.firmapiu.MasterFactoryBuilder;
import it.libersoft.firmapiu.exception.FirmapiuException;
import static it.libersoft.firmapiu.consts.FactoryConsts.*;
import it.libersoft.firmapiu.crtoken.*;

/**
 * Classe di Test per verificare le funzionalità della libreria riguardo il cambio del pin
 * 
 * @author andy
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
		
		pkcs11Token.setPin(oldPin, newPin);
	}

}
