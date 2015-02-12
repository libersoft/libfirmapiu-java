/**
 * 
 */
package firmapiu.exception;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Wrapper utilizzato per rilanciare le eccezioni sottostanti, utlizzando un codice di errore e un messaggio di errore preciso 
 * 
 * @author dellanna
 *
 */
public final class FirmapiuException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Errore di default
	 * */
	private static int DEFAULTERROR=99;
	
	/**
	 * Codice di errore dell'eccezione rilanciata da firmapiu
	 */
	public final int errorCode;

	
	/**
	 * Inizializza firmapiuException con un codice di errore e l'eccezione sottostante
	 * */
	public FirmapiuException(int code, Throwable cause) {
		super(cause);
		if(cause==null)
		{	ResourceBundle rb=ResourceBundle.getBundle("firmapiu.lang.locale",Locale.getDefault());
			throw new IllegalArgumentException(rb.getString("fimapiuerror0")+" "+this.getClass().getName());
		}
		
		errorCode=code;
	}

	
	/**
	 * Inizializza firmapiuException con un codice di default e l'eccezione sottostante
	 * */
	public FirmapiuException(Throwable cause) {
		super(cause);
		if(cause==null)
		{	ResourceBundle rb=ResourceBundle.getBundle("firmapiu.lang.locale",Locale.getDefault());
			throw new IllegalArgumentException(rb.getString("fimapiuerror0")+" "+this.getClass().getName());
		}
		
		errorCode=DEFAULTERROR;
	}

	/**
	 * Inizializza firmapiuException con un codice di errore
	 * */
	public FirmapiuException(int code) {
		errorCode=code;
	}

	
	/**
	 * Inizializza firmapiuException con un codice di default
	 * */
	public FirmapiuException() {
		errorCode=DEFAULTERROR;
	}


	public FirmapiuException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
		errorCode=DEFAULTERROR;
	}


	public FirmapiuException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
		errorCode=DEFAULTERROR;
	}


	public FirmapiuException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
		errorCode=DEFAULTERROR;
	}
	
	
}
