/**
 * 
 */
package it.libersoft.firmapiu.crtoken;

import it.libersoft.firmapiu.exception.FirmapiuException;
import static it.libersoft.firmapiu.exception.FirmapiuException.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Crea e gestisce un keystore che è stato generato parsando un TSL (Trust
 * Service status List) in formato xml passato come parametro in fase di
 * creazione
 * 
 * @author dellanna
 *
 */
final class TSLXmlKeyStoreToken implements KeyStoreToken {

	// file di configurazione usato per creare e caricare il keystore
	private final File configFile;
	// il keystore gestito da questo token
	private final KeyStore keystore;
	// il path contenente la lista delle CA affidabili nel formato TSL xml
	private final String tslPath;
	// il path presso cui salvare il keystore
	private final String keystorePath;
	// il path presso cui salvare i metadati riguardanti il keystore
	private String keystoreMetaDataPath;
	// ogni quanto il keystore deve essere aggiornato
	private final int updateTime;
	// password del keystore
	private char[] password;

	/**
	 * Inizializza il token utilizzando le configurazioni file passate come
	 * parametro
	 *
	 * @param configFile
	 *            Il file di proprietà che contiene le configurazioni che devono
	 *            essere utilizzate per inizializzare correttamente il
	 *            TSLXmlKeyStoreToken.
	 * @throws FirmapiuException
	 *             In caso di errore applicativo
	 */
	TSLXmlKeyStoreToken(String configFilePath) throws FirmapiuException {
		//TODO da controllare se cè bisogno di sincronizzazione per accesso concorrente
		this.configFile = new File(configFilePath);
		// TODO gestione delle proprietà null
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(this.configFile));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			throw new FirmapiuException(IO_DEFAULT_ERROR, e);
		}
		this.tslPath = prop.getProperty("tslPath");
		this.updateTime = Integer.parseInt(prop.getProperty("updateTime"));
		// se il percorso del file non è un percorso assoluto o non è un file,
		// lancia un eccezione
		this.keystorePath = prop.getProperty("keystorePath");
		File keystoreFile = new File(keystorePath);
		if (!keystoreFile.isAbsolute()) {
			String msg = FirmapiuException
					.getDefaultErrorCodeMessage(IS_NOT_ABS_PATH);
			msg += " : KeyStorePath = " + keystorePath;
			throw new FirmapiuException(IS_NOT_ABS_PATH, msg);
		} else if (keystoreFile.isDirectory()) {
			String msg = FirmapiuException
					.getDefaultErrorCodeMessage(IS_NOT_FILE);
			msg += " : KeyStorePath = " + keystorePath;
			throw new FirmapiuException(IS_NOT_FILE, msg);
		}
		if (prop.getProperty("password") != null) {
			this.password = prop.getProperty("password").toCharArray();
			if (this.password == null)
				throw new FirmapiuException(CRT_TOKENPINPUK_VERIFY_ERROR);
		} else
			// TODO gestire con un errore più specifico
			throw new FirmapiuException(DEFAULT_ERROR);
		try {
			this.keystore = KeyStore.getInstance(KeyStore.getDefaultType());

		} catch (KeyStoreException e) {
			throw new FirmapiuException(CERT_KEYSTORE_DEFAULT_ERROR, e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.libersoft.firmapiu.CRToken#getProvider()
	 */
	@Override
	public Provider getProvider() throws FirmapiuException {
		// TODO che succede se il keystore non è stato creato inizializzato?
		return this.keystore.getProvider();
	}

	/**
	 * @param pass
	 *            Valore ignorato: il keystore viene caricato staticamente
	 *            secondo file di configurazione
	 * 
	 * @see it.libersoft.firmapiu.CRToken#loadKeyStore(char[])
	 */
	@Override
	public KeyStore loadKeyStore(char[] pass) throws FirmapiuException {
		// carica staticamente il keystore usando il path specificato nel file
		// di configurazione
		// se non esiste deve essere creato con createKeyStore e lancia un
		// eccezione
		// TODO da fare caricamento dinamico keystore anche secondo pass passata
		// come parametro
		File keystoreFile = new File(this.keystorePath);
		try {
			this.keystore
					.load(new FileInputStream(keystoreFile), this.password);
		} catch (NoSuchAlgorithmException e) {
			throw new FirmapiuException(CERT_KEYSTORE_DEFAULT_ERROR, e);
		} catch (CertificateException e) {
			throw new FirmapiuException(CERT_DEFAULT_ERROR, e);
		} catch (FileNotFoundException e) {
			String msg = FirmapiuException
					.getDefaultErrorCodeMessage(FILE_NOTFOUND);
			msg += " : " + this.keystorePath;
			throw new FirmapiuException(FILE_NOTFOUND, msg, e);
		} catch (IOException e) {
			throw new FirmapiuException(CRT_TOKENPINPUK_VERIFY_ERROR, e);
		}
		return this.keystore;
	}

	/**
	 * Crea il nuovo token inizializzandolo con i parametri presenti nel file di
	 * configurazione
	 * 
	 * @see it.libersoft.firmapiu.crtoken.KeyStoreToken#createKeyStore()
	 */
	@Override
	public void createKeyStore() throws FirmapiuException {
		Document doc = null;
		String certificateBase64;
		String certificateFinal;
		X509Certificate certificateLast;

		// se il file esiste già lancia un errore
		File keystoreFile = new File(this.keystorePath);
		if (keystoreFile.exists()) {
			String msg = FirmapiuException
					.getDefaultErrorCodeMessage(CERT_KEYSTOSTORE_FORBIDDEN);
			msg += " : " + this.keystorePath;
			throw new FirmapiuException(CERT_KEYSTOSTORE_FORBIDDEN, msg);
		}

		// carica la lista TSL: altrimenti la scarica da un server
		// TODO aggiungere supporto per caricare lista in locale da un file
		String uriScheme = null;
		try {
			URI uriTslPath = new URI(this.tslPath);
			uriScheme = uriTslPath.getScheme();
		} catch (URISyntaxException e1) {
			// TODO aggiungere supporto per caricare keystore da file locale
			String msg = FirmapiuException
					.getDefaultErrorCodeMessage(PROTOCOL_DEFAULT_ERROR);
			msg += " : " + this.tslPath;
			throw new FirmapiuException(PROTOCOL_DEFAULT_ERROR, msg, e1);
		}
		if (uriScheme != null) {
			// se lo schema non è http o https lancia un errore
			if (!(uriScheme.equals("http") || uriScheme.equals("https"))) {
				String msg = FirmapiuException
						.getDefaultErrorCodeMessage(PROTOCOL_DEFAULT_ERROR);
				msg += " : " + this.tslPath;
				throw new FirmapiuException(PROTOCOL_DEFAULT_ERROR, msg);
			}
			// carica lo schema in memoria con JSoup
			try {
				doc = Jsoup.connect(this.tslPath).get();
			} catch (IOException e2) {
				String msg = FirmapiuException
						.getDefaultErrorCodeMessage(IO_DEFAULT_ERROR);
				msg += " : " + this.tslPath;
				throw new FirmapiuException(IO_DEFAULT_ERROR, msg, e2);
			}
			Elements newsHeadlines = doc.select("X509Certificate");
			try {
				this.keystore.load(null, this.password);
			} catch (NoSuchAlgorithmException | CertificateException
					| IOException e1) {
				throw new FirmapiuException(CERT_KEYSTORE_DEFAULT_ERROR, e1);
			}
			ListIterator<Element> iterElements = newsHeadlines.listIterator();
			int alias = 0;
			while (iterElements.hasNext()) {
				certificateBase64 = iterElements.next().ownText();
				certificateFinal = "-----BEGIN CERTIFICATE-----\r\n"
						+ certificateBase64 + "\r\n-----END CERTIFICATE-----";
				certificateLast = generateX509Certificate(certificateFinal);
				// aggiunge il certificato nel keystore
				try {
					this.keystore.setEntry(Integer.toString(alias),
							new KeyStore.TrustedCertificateEntry(
									certificateLast), null);
				} catch (KeyStoreException e) {
					throw new FirmapiuException(CERT_DEFAULT_ERROR, e);
				}
				alias++;
			}
			// salva il keystore su un file
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(this.keystorePath);
				this.keystore.store(fos, this.password);
				fos.close();
			} catch (FileNotFoundException | SecurityException e) {
				// il file non può essere creato
				String msg = FirmapiuException
						.getDefaultErrorCodeMessage(FILE_FORBIDDEN);
				msg += " : " + this.keystorePath;
				throw new FirmapiuException(FILE_FORBIDDEN, msg);
			} catch (KeyStoreException e) {
				throw new FirmapiuException(CERT_KEYSTORE_DEFAULT_ERROR, e);
			} catch (NoSuchAlgorithmException | CertificateException e) {
				throw new FirmapiuException(CERT_DEFAULT_ERROR, e);
			} catch (IOException e) {
				throw new FirmapiuException(IO_DEFAULT_ERROR, e);
			}// fine try-catch
		}// fine if
	}// fine createKeystore

	/**
	 * Aggiorna il keystore scaricando la lista Trust Service status List
	 * 
	 * @see it.libersoft.firmapiu.crtoken.KeyStoreToken#updateKeyStore()
	 */
	@Override
	public void updateKeyStore() throws FirmapiuException {
		// cancella e ricrea un keystore esistente.
		// se il keystore non esisteva (e andava quindi prima creato) lancia un
		// errore
		this.deleteKeystore();
		// ricrea il keystore
		this.createKeyStore();
	}

	/**
	 * Cancella il keystore cancellando fisicamente il file, il keystore può
	 * essere ricreato secondo i file di configurazione
	 * 
	 * @see it.libersoft.firmapiu.crtoken.KeyStoreToken#deleteKeystore()
	 */
	@Override
	public void deleteKeystore() throws FirmapiuException {
		// se il keystore non esiste lancia un errore poichè non può essere
		// cancellato
		File keystoreFile = new File(this.keystorePath);
		if (!keystoreFile.exists()) {
			String msg = FirmapiuException
					.getDefaultErrorCodeMessage(FILE_NOTFOUND);
			msg += " : " + this.keystorePath;
			throw new FirmapiuException(FILE_NOTFOUND, msg);
		}

		try {
			keystoreFile.delete();
		} catch (SecurityException e) {
			// il file non può essere cancellato
			String msg = FirmapiuException
					.getDefaultErrorCodeMessage(FILE_FORBIDDEN);
			msg += " : " + this.keystorePath;
			throw new FirmapiuException(FILE_FORBIDDEN, msg);
		}
	}

	// PROCEDURE PRIVATE
	/**
	 * Trasforma una stringa che rappresenta un certificato in Base 64 in un
	 * certificato di tipo X509
	 * 
	 * @param certificateString
	 *            certificato in Base64 in formato stringa: {@code String}
	 * 
	 * @return certificato di tipo X509: {@code X509Certificate}
	 * */
	private X509Certificate generateX509Certificate(String certificateString)
			throws FirmapiuException {

		InputStream in = null;
		X509Certificate certificateX509 = null;
		try {
			byte[] certEntryBytes = certificateString.getBytes();
			in = new ByteArrayInputStream(certEntryBytes);
			CertificateFactory certFactory = CertificateFactory
					.getInstance("X.509");

			certificateX509 = (X509Certificate) certFactory
					.generateCertificate(in);
		} catch (CertificateException ex) {
			throw new FirmapiuException(CERT_DEFAULT_ERROR, ex);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					throw new FirmapiuException(IO_DEFAULT_ERROR, e);
				}
			}
		}
		return certificateX509;
	}
}
