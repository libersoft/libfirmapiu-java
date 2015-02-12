/**
 * 
 */
package firmapiu.exception;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Wrapper utilizzato per rilanciare le eccezioni sottostanti di runtime 
 * 
 * @author dellanna
 *
 */
public final class FirmapiuRuntimeException extends RuntimeException {

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
	public FirmapiuRuntimeException(int code, Throwable cause) {
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
	public FirmapiuRuntimeException(Throwable cause) {
		super(cause);
		if(cause==null)
		{	ResourceBundle rb=ResourceBundle.getBundle("firmapiu.lang.locale",Locale.getDefault());
			throw new IllegalArgumentException(rb.getString("fimapiuerror0")+" "+this.getClass().getName());
		}
		
		errorCode=DEFAULTERROR;
	}

}
