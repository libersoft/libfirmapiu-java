package it.libersoft.firmapiu.cades;

import java.io.File;
import java.util.Map;

import it.libersoft.firmapiu.CommandInterface;
import it.libersoft.firmapiu.Data;
import it.libersoft.firmapiu.DataFilePath;
import it.libersoft.firmapiu.P7SDataFilePath;
import it.libersoft.firmapiu.Report;
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
public interface P7FileCommandInterface extends CommandInterface<String,File> {

	/**
	 * Salva i dati da firmare nella busta crittografica Cades-BES in un file
	 * .p7m, p7s
	 * 
	 * @see it.libersoft.firmapiu.CommandInterface#sign(it.libersoft.firmapiu.Data)
	 */
	public ResultInterface<String, File> sign(DataFilePath data) throws FirmapiuException;

	
	/**
	 * Verifica i file p7m nel formato Cades-BES per controllarne la correttezza e l'affidabilità
	 * 
	 *  @see it.libersoft.firmapiu.CommandInterface#sign(it.libersoft.firmapiu.Data)
	 */
	public ResultInterface<String,Report> verify(DataFilePath data) throws FirmapiuException;
	
	
	/**
	 * Verifica i file p7s nel formato Cades-BES per controllarne la correttezza e l'affidabilità
	 * 
	 * @param data I dati da controllare (devono contenere un mapping p7s, contenuto originale di un file)
	 * @return Un report contentente L'esito della verifica per ogni p7s passato comeparametro 
	 * @throws FirmapiuException in caso di errore applicativo
	 */
	public ResultInterface<String,Report> verifyP7S(P7SDataFilePath data) throws FirmapiuException;
	
	/**
	 * Salva il contenuto originale dei dati da verificare in un file<br>
	 * (Nota il nome del file deve essere identificato univocamente.
	 * Corrisponderà quindi al valore restituito da String getDataId(T data))
	 * 
	 * @see it.libersoft.firmapiu.CommandInterface#getContentSignedData(it.libersoft.firmapiu.Data)
	 */
	public ResultInterface<String, File> getContentSignedData(DataFilePath signedData)
			throws FirmapiuException;
}
