/**
 * 
 */
package it.libersoft.firmapiu.cades;

import static it.libersoft.firmapiu.consts.FactoryConsts.*;
import it.libersoft.firmapiu.DefaultFactory;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Costruisce l'interfaccia di comandi utilizzata per la firma e verifica di
 * dati in formato Cades-BES secondo la DELIBERAZIONE ministeriale del N . 45
 * DEL 21 MAGGIO 2009 tramite utilizzo di token crittografico.
 * 
 * @author dellanna
 *
 */
public final class CadesBESFactory extends DefaultFactory {

	// inizializza il resourcebundle per il recupero dei messaggi lanciati dalla
	// classe
	private static final ResourceBundle RB = ResourceBundle.getBundle(
			"it.libersoft.firmapiu.lang.localefactory", Locale.getDefault());

	
	/**
	 * Questa classe non dovrebbe essere inizializzata dal costruttore ma dalla super factory ad essa associata che inizializza
	 * le proprietà di default di questo oggetto.
	 */
	public CadesBESFactory() {
		super();
	}

	/**
	 * 
	 * @see it.libersoft.firmapiu.DefaultFactory#getCadesBESCommandInterface(String choice)
	 * @throws IllegalArgumentException
	 *             Se l'oggetto richiesto non esiste
	 */
	@Override
	public CadesBESCommandInterface getCadesBESCommandInterface(String choice) throws IllegalArgumentException{
		//recupera il token crittografico da utilizzare per effettuare le operazioni di firma
		//se è pkcs#11 cerca il driver della smartcard crittografica utilizzata
		
		if (choice.equals(P7MFILE))
			return new P7MCommandInterface();
		else if (choice.equals(P7SFILE))
			// TODO possibile estensione della libreria con supporto ai .p7s
			throw new IllegalArgumentException(RB.getString("factoryerror3")
					+ " : " + choice);
		else
			throw new IllegalArgumentException(RB.getString("factoryerror3")
					+ " : " + choice);
	}

}