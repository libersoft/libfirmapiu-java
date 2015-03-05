/**
 * 
 */
package it.libersoft.firmapiu.crtoken;

import javax.smartcardio.ATR;

import it.libersoft.firmapiu.CRToken;
import it.libersoft.firmapiu.MasterFactoryBuilder;
import it.libersoft.firmapiu.crtoken.PKCS11Token;
import it.libersoft.firmapiu.exception.FirmapiuException;
import static it.libersoft.firmapiu.consts.FactoryConsts.*;
/**
 * @author andy
 *
 */
public class TestCard {
	/**
	 * @param args
	 * @throws FirmapiuException 
	 */
	public static void main(String[] args) throws FirmapiuException {
		// TODO Auto-generated method stub
		PKCS11Token token = (PKCS11Token)MasterFactoryBuilder.getFactory(PKCS11TOKENFACTORY).getToken(CRTSMARTCARD);
		
		//String atrString = CRTSmartCardToken.getHexString(smartcardToken.getATR());
		ATR atr= new ATR(token.getATR());
		//System.out.println("ATR:"+atrString);
		System.out.println("ATR (toString()): "+atr.toString());
		byte[] historybyte=atr.getHistoricalBytes();
		//System.out.println("Historical Bytes: "+CRTSmartCardToken.getHexString(historybyte));
		System.out.println("Manufacter Vendor code:"+historybyte[0]);
		
		
		//System.out.println("Smartcard driver:"+smartcardToken.findLibraries(atrString, "/home/andy/Scaricati/atr.ini"));
	}
}
