package it.libersoft.firmapiu.cades;

import java.io.File;
import java.util.Map;

import it.libersoft.firmapiu.CommandInterface;
import it.libersoft.firmapiu.Data;
import it.libersoft.firmapiu.ResultInterface;
import it.libersoft.firmapiu.exception.FirmapiuException;

/**
 * 
 */

/**
 * Interfaccia di comandi utilizzata per effettuare la firma e la verifica di un
 * insieme di dati passati come parametro in un formato Cades-BES.<br>
 * L'interfaccia specializzata opera su oggetti di tipo java.io.File come valori
 * parametrizzati associati alle chiavi
 * 
 * <a href="http://en.wikipedia.org/wiki/CAdES_(computing)">http://en.wikipedia.
 * org/wiki/CAdES</a>
 * 
 * @author dellanna
 * @param <File>
 *            Tipo di dato associato ai valori restituiti da
 *            ResultInterface<K,V>
 */
public interface P7FileCommandInterface<K, V> extends CommandInterface<K, File> {

	/**
	 * Salva i dati da firmare nella busta crittografica Cades-BES in un file
	 * .p7m, p7s
	 * 
	 * @see it.libersoft.firmapiu.CommandInterface#sign(it.libersoft.firmapiu.Data)
	 */
	public ResultInterface<K, File> sign(Data<K> data) throws FirmapiuException;

	/**
	 * Salva il contenuto originale dei dati da verificare in un file<br>
	 * (Nota il nome del file deve essere identificato univocamente.
	 * Corrisponderà quindi al valore restituito da String getDataId(T data))
	 * 
	 * @see it.libersoft.firmapiu.CommandInterface#getContentSignedData(it.libersoft.firmapiu.Data)
	 */
	public ResultInterface<K, File> getContentSignedData(Data<K> signedData)
			throws FirmapiuException;
}
