/**
 * 
 */
package it.libersoft.firmapiu;
import static it.libersoft.firmapiu.consts.FactoryConsts.*;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 *  Questa factory crea gli argomenti generici da passare come parametro di input alle operazioni di firma e verifica
 * 
 * @author dellanna
 *
 */
final class ArgumentFactory extends DefaultFactory {

	// inizializza il resourcebundle per il recupero dei messaggi lanciati dalla
	// classe
	private static final ResourceBundle RB = ResourceBundle.getBundle("it.libersoft.firmapiu.lang.localefactory", Locale.getDefault());

	
	/**
	 * Questa classe non dovrebbe essere inizializzata dal costruttore ma dalla super factory ad essa associata che inizializza
	 * le proprietà di default di questo oggetto.
	 */
	protected ArgumentFactory() {
		//NON carica le proprietà di default di DefaultFactory
	}


	/** 
	 * @see it.libersoft.firmapiu.DefaultFactory#getArgument(java.lang.String)
	 * @throws IllegalArgumentException
	 *             Se l'oggetto richiesto non esiste
	 */
	@Override
	public Argument<?, ?> getArgument(String choice)
			throws IllegalArgumentException {
		if (choice.equals(GENERICARGUMENT))
			return new GenericArgument();
		else
			throw new IllegalArgumentException(RB.getString("factoryerror3")
					+ " : " + choice);
	}

	
	
}
