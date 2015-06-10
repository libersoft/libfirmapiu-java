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
 * Questa factory crea un oggetto P7SDataFile, che rappresenta un insieme di dati cades-bes
 * detached rappresentati come files .p7s e files originali associati utilizzati per le
 * operazioni di verifica
 * <p>
 * 
 * I dati richiesti sono un insieme di file
 * 
 * @author dellanna
 *
 */
final class P7SDataFileFactory extends DefaultDataFactory {

	// inizializza il resourcebundle per il recupero dei messaggi lanciati dalla
	// classe
	private static final ResourceBundle RB = ResourceBundle.getBundle(
			"it.libersoft.firmapiu.lang.localefactory", Locale.getDefault());

	/**
	 * Questa classe non dovrebbe essere inizializzata dal costruttore ma dalla
	 * super factory ad essa associata che inizializza le proprietà di default
	 * di questo oggetto.
	 */
	protected P7SDataFileFactory() {
		super();
		// carica le proprietà di default di DataFileFactory
		this.setProperty(NORMALIZE_DATAPATH, new Boolean(true));
	}

	/**
	 * @see it.libersoft.firmapiu.data.DefaultDataFactory#getDataByteArray()
	 */
	@Override
	public P7SDataFile getP7SDataFile() throws IllegalArgumentException {
		return new CommonDataFileImpl();
	}
}