/**
 * 
 */
package it.libersoft.firmapiu.data;
import static it.libersoft.firmapiu.consts.FactoryConsts.*;
import static it.libersoft.firmapiu.consts.FactoryPropConsts.*;
import it.libersoft.firmapiu.Data;


import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Questa factory crea un insieme di dati da passare come parametro di input alle operazioni di firma e verifica<p>
 * 
 * I dati richiesti sono un insieme di file
 * 
 * @author dellanna
 *
 */
final class DataFileFactory extends DefaultDataFactory {
	
	// inizializza il resourcebundle per il recupero dei messaggi lanciati dalla
	// classe
	private static final ResourceBundle RB = ResourceBundle.getBundle("it.libersoft.firmapiu.lang.localefactory", Locale.getDefault());

	/**
	 * Questa classe non dovrebbe essere inizializzata dal costruttore ma dalla super factory ad essa associata che inizializza
	 * le proprietà di default di questo oggetto.
	 */
	protected DataFileFactory() {
		super();
		//carica le proprietà di default di DataFileFactory
		this.setProperty(NORMALIZE_DATAPATH, new Boolean(true));
	}

	/**
	 * @see it.libersoft.firmapiu.data.DefaultDataFactory#getDataFile()
	 */
	@Override
	public DataFile getDataFile() throws IllegalArgumentException {
		return new DataFileImpl();
	}
}