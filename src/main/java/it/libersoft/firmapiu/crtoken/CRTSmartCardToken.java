/**
 * 
 */
package it.libersoft.firmapiu.crtoken;

import it.libersoft.firmapiu.exception.FirmapiuException;
import static it.libersoft.firmapiu.exception.FirmapiuException.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.Provider;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.smartcardio.Card;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardTerminals;
import javax.smartcardio.TerminalFactory;

/**
 * La classe gestisce una smartcard crittografica.<br> 
 * Mette a disposizione funzionalità per gestire la carta stessa quali pin, puk e sblocco.<br>
 * Permette l'accesso alle credenziali messe a disposizione della carta (quali certificato utente, chiave privata)
 * alle operazioni/comandi messi a disposizione della libreria <code>firmapiulib</code>
 * 
 * @author dellanna
 *
 */
final class CRTSmartCardToken implements PKCS11Token {
	
	// inizializza il resourcebundle per il recupero dei messaggi lanciati dalla
	// classe
	private static final ResourceBundle RB = ResourceBundle.getBundle(
				"it.libersoft.firmapiu.lang.locale", Locale.getDefault());
	
	private TerminalFactory factory;
	private CardTerminals terminals;
	private CardTerminal terminal = null;
	private List<CardTerminal> listTerminals = null;
	private Card card = null;

	/**
	 *La classe non dovrebbe essere inizializzata se non attraverso la factory 
	 */
	protected CRTSmartCardToken() {}

	/* (non-Javadoc)
	 * @see it.libersoft.firmapiu.CRToken#getProvider()
	 */
	@Override
	public Provider getProvider() throws FirmapiuException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see it.libersoft.firmapiu.CRToken#getKeyStore()
	 */
	@Override
	public KeyStore getKeyStore() throws FirmapiuException {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Se sono presenti un o più lettori con una o più smartcart presenti, il metodo restituisce l'ATR della prima carta trovata
	 * 
	 * @see it.libersoft.firmapiu.crtoken.PKCS11Token#getATR()
	 */
	@Override
	public byte[] getATR() throws FirmapiuException {
		int numberTerminals;
		boolean cardPresent;

		// lettori collegati
		try {
			checkTerminal();
		} catch (CardException e1) {
			throw new FirmapiuException(CRT_TOKENTERMINAL_NOTFOUND,e1);
		}
		// numero dei lettori connessi
		numberTerminals = listTerminals.size();

		// verifico che la carta sia insertita
		cardPresent = false;
		for (int i = 0; i < numberTerminals; i++){
			terminal = listTerminals.get(i);
			// carta inserita
			try {
				if(terminal.isCardPresent()){
					// effettuo la connessione con la carta
					card = terminal.connect("*");
					cardPresent = true;
					break;
				}
			} catch(SecurityException e1){
				throw new FirmapiuException(CRT_TOKEN_FORBIDDEN,e1);
			}catch (CardException e) {
				throw new FirmapiuException(CRT_TOKEN_DEFAULT_ERROR,e);
			}
		}

		// carta non inserita
		if(!cardPresent)
			throw new FirmapiuException(CRT_TOKEN_NOTFOUND);

		// ATR della carta in array di byte
		return card.getATR().getBytes();
	}

	/* (non-Javadoc)
	 * @see it.libersoft.firmapiu.crtoken.PKCS11Token#setPin(char[], char[])
	 */
	@Override
	public void setPin(char[] oldPin, char[] newPin) throws FirmapiuException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see it.libersoft.firmapiu.crtoken.PKCS11Token#setPuk(char[], char[], char[])
	 */
	@Override
	public void setPuk(char[] pin, char[] oldPuk, char[] newPuk)
			throws FirmapiuException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see it.libersoft.firmapiu.crtoken.PKCS11Token#unlockPKCS11Token(char[], char[])
	 */
	@Override
	public void unlockPKCS11Token(char[] pin, char[] puk)
			throws FirmapiuException {
		// TODO Auto-generated method stub

	}

	//PROCEDURE PRIVATE

	
	//assegna la lista dei lettori connessi alla variabile d'istanza <code>listTerminals</code>
	private void checkTerminal() throws CardException{
		factory = TerminalFactory.getDefault();
		terminals = factory.terminals();

		// lista dei lettori collegati
		listTerminals = terminals.list();
		
	}
	
	//
	//restituisce il path assoluto della libreria per la smart card identificata dal suo atr e il nome della smart card 
	//
	private String findLibraries(String atrString, String nameIni) throws FirmapiuException{
		if((atrString == null) || (nameIni == null))
			throw new IllegalArgumentException(RB.getString("illegalargument0"));

		//cerco l'atr della carta all'interno del file .ini
		boolean trovato = false;
		int j = 0;
		int k = 1;
		char c = "_".charAt(0);
		Properties configurationProperties = null;
		configurationProperties = new Properties();
		for (k = 1; k <= 15; k++){
			try{
				//carico il file di configurazione
				configurationProperties.load(new FileInputStream(nameIni));
			}
			catch(IOException e){
				String errormsg=FirmapiuException.getDefaultErrorCodeMessage(CRT_TOKEN_CONFIGFILE_NOTFOUND);
				errormsg+=nameIni;
				throw new FirmapiuException(CRT_TOKEN_CONFIGFILE_NOTFOUND,errormsg,e);
			}
			//ottengo il primo atr del file di configurazione
			String fileAtr1 = (configurationProperties.getProperty("smartcard." + k + ".atr"));
			char[] fileAtr = fileAtr1.toCharArray();

			if(atrString.length() == fileAtr.length){
				for (j = 0; j < atrString.length(); j++){
					String corj = Character.toString(atrString.charAt(j)).toUpperCase();
					String filej = Character.toString(fileAtr[j]).toUpperCase();
					if(!corj.equals(filej) && fileAtr[j] != c){
						break;
					}
				}
				//corrispondenza trovata
				if(j == atrString.length()){
					trovato = true;
					break;
				}
			}
			j = 0;
		}

		//path della libreria
		String pathPKCS11Lib = configurationProperties.getProperty("smartcard." + k + ".linux.path");
		//libreria della smart card
		String PKCS11Lib = configurationProperties.getProperty("smartcard." + k + ".linux.lib");
		//nome della smart card
		String nameCard = configurationProperties.getProperty("smartcard."+k+".manufacturer");
		
		//TODO da cambiare
		if(trovato == false){
			System.out.println("configurazione non riuscita: libreria mancante!");
			System.exit(1);
		}
		return nameCard+"$"+pathPKCS11Lib+PKCS11Lib;
	}


	//restituisce la rappresentazione esadecimale di un array di byte
	public static String getHexString(byte[] dataInput){

		String mes = "Utils getHexString:  ";
		String result = "";

		// verifica parametro
		if(dataInput == null){
			System.err.println(mes + "parametro null!!");
			System.exit(1);
		}

		for (int i = 0; i < dataInput.length; i++){
			result += Integer.toString((dataInput[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}
}
