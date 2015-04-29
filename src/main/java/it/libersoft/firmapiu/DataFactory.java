/**
 * 
 */
package it.libersoft.firmapiu;
import static it.libersoft.firmapiu.consts.FactoryConsts.*;
import static it.libersoft.firmapiu.consts.FactoryPropConsts.*;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Questa factory crea un insieme di dati da passare come parametro di input alle operazioni di firma e verifica
 * 
 * @author dellanna
 *
 */
final class DataFactory extends DefaultFactory {
	
	// inizializza il resourcebundle per il recupero dei messaggi lanciati dalla
	// classe
	private static final ResourceBundle RB = ResourceBundle.getBundle("it.libersoft.firmapiu.lang.localefactory", Locale.getDefault());

	/**
	 * Questa classe non dovrebbe essere inizializzata dal costruttore ma dalla super factory ad essa associata che inizializza
	 * le proprietà di default di questo oggetto.
	 */
	protected DataFactory() {
		super();
		//carica le proprietà di default di DataFactory
		this.setProperty(NORMALIZE_DATAPATH, new Boolean(true));
	}

	/**
	 * @see it.libersoft.firmapiu.DefaultFactory#getData(java.lang.String)
	 * @throws IllegalArgumentException
	 *             Se l'oggetto richiesto non esiste
	 */
	@Override
	public Data<?> getData(String choice) throws IllegalArgumentException {
		if (choice.equals(DATAFILEPATH)){
			boolean normalize=(Boolean)this.getProperty(NORMALIZE_DATAPATH);
			return new DataFilePath();
		}
		else
			throw new IllegalArgumentException(RB.getString("factoryerror3")
					+ " : " + choice);
	}

	
	
}
