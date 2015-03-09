/**
 * 
 */
package it.libersoft.firmapiu.crtoken;

import it.libersoft.firmapiu.exception.FirmapiuException;
import static it.libersoft.firmapiu.exception.FirmapiuException.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.AuthProvider;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;
import java.security.cert.CertificateException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.smartcardio.Card;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardTerminals;
import javax.smartcardio.TerminalFactory;

/**
 * La classe gestisce una smartcard crittografica.<br>
 * Mette a disposizione funzionalità per gestire la carta stessa quali pin, puk
 * e sblocco.<br>
 * Permette l'accesso alle credenziali messe a disposizione della carta (quali
 * certificato utente, chiave privata) alle operazioni/comandi messi a
 * disposizione della libreria <code>firmapiulib</code>
 * 
 * @author dellanna
 *
 */
final class CRTSmartCardToken implements PKCS11Token {

	// inizializza il resourcebundle per il recupero dei messaggi lanciati dalla
	// classe
	private static final ResourceBundle RB = ResourceBundle.getBundle(
			"it.libersoft.firmapiu.lang.locale", Locale.getDefault());

	// variabili private per accedere a lettore e smartcard
	private TerminalFactory factory;
	private CardTerminals terminals;
	private CardTerminal terminal = null;
	private List<CardTerminal> listTerminals = null;
	private Card card = null;

	// provider pkcs#11 per accedere alle operazioni della carta
	private Provider pkcs11Provider;

	// variabili di sessione (apre/chiude sessioni sulla carta)
	private boolean session;
	private AuthProvider aprov;

	/**
	 * @param pkcs11DriverPath
	 *            file contenente i riferimenti ai driver delle smartcard che
	 *            possono essere utilizzate per caricare il provider pkcs11
	 *            corrispondente
	 * 
	 *            La classe non dovrebbe essere inizializzata se non attraverso
	 *            la factory.<br>
	 *            Inizializza il token cercando di caricare il driver corretto.
	 *            Se non ce la fa lancia un errore.
	 */
	protected CRTSmartCardToken(String pkcs11DriverPath)
			throws FirmapiuException {
		// non è stata instaurata ancora una sessione
		this.session = false;

		// controlla che la smartcard sia stata inserita
		this.getATR();

		// inizializza il provider PKCS#11: cerca di inizializzare il provider
		// caricando uno dei driver presenti nel sistema
		Properties pkcs11Prop = new Properties();
		try {
			pkcs11Prop.load(new FileInputStream(new File(pkcs11DriverPath)));
		} catch (IOException e) {
			throw new FirmapiuException(CRT_TOKEN_CONFIGFILE_NOTFOUND, e);
		}
		Iterator<String> pkcs11DriverItr = pkcs11Prop.stringPropertyNames()
				.iterator();
		while (pkcs11DriverItr.hasNext()) {
			String key = pkcs11DriverItr.next();
			String pkcs11Driver = pkcs11Prop.getProperty(key);
			String pkcs11Config = "name=pkcs11" + key + "\n";
			pkcs11Config += "library=" + pkcs11Driver;
			try {
				this.pkcs11Provider = new sun.security.pkcs11.SunPKCS11(
						new ByteArrayInputStream(pkcs11Config.getBytes()));
				break;
			} catch (Exception e) {
				// non gestisce l'eccezione, cerca il prossimo driver con cui
				// caricare il token
			}
		}// fine while

		// se non è stato in grado di caricare nessun driver lancia un errore
		if (this.pkcs11Provider == null)
			throw new FirmapiuException(CRT_TOKEN_LIB_NOTFOUND);
	}

	/**
	 * @see it.libersoft.firmapiu.CRToken#getProvider()
	 */
	@Override
	public Provider getProvider() throws FirmapiuException {
		return this.pkcs11Provider;
	}

	/**
	 * 
	 * @param pass
	 *            Il pin/password utilizzato per accedere al keystore.<br>
	 *            Questo valore è necessario se si accede al keystore presente
	 *            su questo token in maniera stand-alone<br>
	 *            Se invece è stata instaurata una sessione, questo valore viene
	 *            ignorato.
	 * 
	 * @throws FirmapiuException
	 *             Se ci sono errori riguardanti all'accesso del keystore
	 * @see it.libersoft.firmapiu.CRToken#getKeyStore()
	 * @see <a
	 *      href="http://docs.oracle.com/javase/7/docs/technotes/guides/security/p11guide.html#Login">http://docs.oracle.com/javase/7/docs/technotes/guides/security/p11guide.html#Login</a>
	 * 
	 */
	@Override
	public KeyStore loadKeyStore(char[] pin) throws FirmapiuException {
		// TODO da cambiare se si decide che il keystore debba essere gestito in
		// maniera stand-alone o meno a seconda di proprietà da stabilire a
		// monte.
		// TODO Al momento il keystore può essere caricato in maniera "statica"
		// passandogli il pin come parametro di questo metodo.
		// TODO Tuttavia se è stato effettuato un login su questo token, il
		// caricamento statico viene ignorato e il keystore viene caricato
		// considerando il contesto della sessione a cui appartiene
		try {
			KeyStore pkcs11keystore = KeyStore.getInstance("pkcs11",
					pkcs11Provider.getName());
			// se non è stata instaurata una sessione, carica il keystore in
			// maniera stand-alone altrimenti usa la sessione
			if (!this.session)
				pkcs11keystore.load(null, pin);
			else
				pkcs11keystore.load(null, null);
			return pkcs11keystore;
		} catch (KeyStoreException e) {
			throw new FirmapiuException(CERT_KEYSTORE_DEFAULT_ERROR, e);
		} catch (NoSuchAlgorithmException e) {
			throw new FirmapiuException(CERT_DEFAULT_ERROR, e);
		} catch (CertificateException e) {
			throw new FirmapiuException(CERT_DEFAULT_ERROR, e);
		} catch (IOException e) {
			throw new FirmapiuException(CRT_TOKENPIN_ERROR, e);
		} catch (NoSuchProviderException e) {
			String msg = FirmapiuException
					.getDefaultErrorCodeMessage(CRT_TOKEN_DEFAULT_ERROR);
			msg += RB.getString("providererror0") + " : "
					+ pkcs11Provider.getName();
			throw new FirmapiuException(CRT_TOKEN_DEFAULT_ERROR, msg, e);
		}
	}

	/**
	 * Effettua il login sul pkcs#11 token
	 * 
	 * @throws FirmapiuException
	 *             in caso in cui il pin/pass passato come parametro è sbagliato
	 * @see it.libersoft.firmapiu.crtoken.PKCS11Token#login(char[])
	 */
	@Override
	public void login(char[] pass) throws FirmapiuException {
		Security.addProvider(this.pkcs11Provider);
		this.aprov = (AuthProvider) Security.getProvider(this.pkcs11Provider
				.getName());
		// Subject subject =new Subject();
		try {
			this.aprov.login(null, new PrivateCallbackHandler(pass));
			// inizia la sessione
			this.session = true;
		} catch (LoginException e) {
			Security.removeProvider(this.pkcs11Provider.getName());
			throw new FirmapiuException(CRT_TOKENPIN_ERROR, e);
		}

	}

	@Override
	public void logout() throws FirmapiuException {
		try {
			this.aprov.logout();
		} catch (LoginException e) {
			throw new FirmapiuException(CRT_TOKEN_DEFAULT_ERROR, e);
		} finally {
			Security.removeProvider(this.pkcs11Provider.getName());
			// chiude la sessione
			this.session = false;
		}
	}

	/**
	 * Se sono presenti un o più lettori con una o più smartcart presenti, il
	 * metodo restituisce l'ATR della prima carta trovata
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
			throw new FirmapiuException(CRT_TOKENTERMINAL_NOTFOUND, e1);
		}
		// numero dei lettori connessi
		numberTerminals = listTerminals.size();
		// TODO lanciare eccezione se trova più di un lettore/carta collegato?

		// verifico che la carta sia insertita
		cardPresent = false;
		for (int i = 0; i < numberTerminals; i++) {
			terminal = listTerminals.get(i);
			// carta inserita
			try {
				if (terminal.isCardPresent()) {
					// effettuo la connessione con la carta
					card = terminal.connect("*");
					cardPresent = true;
					break;
				}
			} catch (SecurityException e1) {
				throw new FirmapiuException(CRT_TOKEN_FORBIDDEN, e1);
			} catch (CardException e) {
				throw new FirmapiuException(CRT_TOKEN_DEFAULT_ERROR, e);
			}
		}

		// carta non inserita
		if (!cardPresent)
			throw new FirmapiuException(CRT_TOKEN_NOTFOUND);

		// ATR della carta in array di byte
		return card.getATR().getBytes();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.libersoft.firmapiu.crtoken.PKCS11Token#setPin(char[], char[])
	 */
	@Override
	public void setPin(char[] oldPin, char[] newPin) throws FirmapiuException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.libersoft.firmapiu.crtoken.PKCS11Token#setPuk(char[], char[],
	 * char[])
	 */
	@Override
	public void setPuk(char[] pin, char[] oldPuk, char[] newPuk)
			throws FirmapiuException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.libersoft.firmapiu.crtoken.PKCS11Token#unlockPKCS11Token(char[],
	 * char[])
	 */
	@Override
	public void unlockPKCS11Token(char[] pin, char[] puk)
			throws FirmapiuException {
		// TODO Auto-generated method stub

	}

	// PROCEDURE PRIVATE

	// assegna la lista dei lettori connessi alla variabile d'istanza
	// <code>listTerminals</code>
	private void checkTerminal() throws CardException {
		factory = TerminalFactory.getDefault();
		terminals = factory.terminals();

		// lista dei lettori collegati
		listTerminals = terminals.list();

	}

	private static class PrivateCallbackHandler implements CallbackHandler {

		private final char[] pass;

		private PrivateCallbackHandler(char[] pass) {
			this.pass = pass;
		}

		@Override
		public void handle(Callback[] callbacks) throws IOException,
				UnsupportedCallbackException {
			PasswordCallback pc = (PasswordCallback) callbacks[0];
			pc.setPassword(this.pass);

		}

	}

	//
	// restituisce il path assoluto della libreria per la smart card
	// identificata dal suo atr e il nome della smart card
	//
	/*
	 * private String findLibraries(String atrString, String nameIni) throws
	 * FirmapiuException{ if((atrString == null) || (nameIni == null)) throw new
	 * IllegalArgumentException(RB.getString("illegalargument0"));
	 * 
	 * //cerco l'atr della carta all'interno del file .ini boolean trovato =
	 * false; int j = 0; int k = 1; char c = "_".charAt(0); Properties
	 * configurationProperties = null; configurationProperties = new
	 * Properties(); for (k = 1; k <= 15; k++){ try{ //carico il file di
	 * configurazione configurationProperties.load(new
	 * FileInputStream(nameIni)); } catch(IOException e){ String
	 * errormsg=FirmapiuException
	 * .getDefaultErrorCodeMessage(CRT_TOKEN_CONFIGFILE_NOTFOUND);
	 * errormsg+=nameIni; throw new
	 * FirmapiuException(CRT_TOKEN_CONFIGFILE_NOTFOUND,errormsg,e); } //ottengo
	 * il primo atr del file di configurazione String fileAtr1 =
	 * (configurationProperties.getProperty("smartcard." + k + ".atr")); char[]
	 * fileAtr = fileAtr1.toCharArray();
	 * 
	 * if(atrString.length() == fileAtr.length){ for (j = 0; j <
	 * atrString.length(); j++){ String corj =
	 * Character.toString(atrString.charAt(j)).toUpperCase(); String filej =
	 * Character.toString(fileAtr[j]).toUpperCase(); if(!corj.equals(filej) &&
	 * fileAtr[j] != c){ break; } } //corrispondenza trovata if(j ==
	 * atrString.length()){ trovato = true; break; } } j = 0;
	 * 
	 * 
	 * //path della libreria String pathPKCS11Lib =
	 * configurationProperties.getProperty("smartcard." + k + ".linux.path");
	 * //libreria della smart card String PKCS11Lib =
	 * configurationProperties.getProperty("smartcard." + k + ".linux.lib");
	 * //nome della smart card String nameCard =
	 * configurationProperties.getProperty("smartcard."+k+".manufacturer");
	 * 
	 * if(trovato == false){
	 * System.out.println("configurazione non riuscita: libreria mancante!");
	 * System.exit(1); } return nameCard+"$"+pathPKCS11Lib+PKCS11Lib; }
	 * 
	 * //restituisce la rappresentazione esadecimale di un array di byte private
	 * static String getHexString(byte[] dataInput){
	 * 
	 * String mes = "Utils getHexString:  "; String result = "";
	 * 
	 * // verifica parametro if(dataInput == null){ System.err.println(mes +
	 * "parametro null!!"); System.exit(1); }
	 * 
	 * for (int i = 0; i < dataInput.length; i++){ result +=
	 * Integer.toString((dataInput[i] & 0xff) + 0x100, 16).substring(1); }
	 * return result; }
	 */
}
