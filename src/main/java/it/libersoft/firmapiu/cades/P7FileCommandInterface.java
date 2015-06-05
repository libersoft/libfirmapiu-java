package it.libersoft.firmapiu.cades;

import java.io.File;
import java.util.Map;

import it.libersoft.firmapiu.CommandInterface;
import it.libersoft.firmapiu.Data;
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
 * @param <String> Tipo di dato associato alle chiavi utilizzate per associare i file da firmare/verificare al tipo di valore restituito<br>
 * Rappresentano i percorsi assoluti dei file da firmare/verificare  
 * @param <File>
 *            Tipo di dato associato ai valori restituiti da
 *            ResultInterface<K,V>
 */
public interface P7FileCommandInterface extends CommandInterface<File,File> {
	
	/**
	 * Verifica i file p7s nel formato Cades-BES per controllarne la correttezza e l'affidabilità
	 * 
	 * @param data I dati da controllare (devono contenere un mapping p7s, contenuto originale di un file)
	 * @return Un report contentente L'esito della verifica per ogni p7s passato comeparametro 
	 * @throws FirmapiuException in caso di errore applicativo
	 */
	public ResultInterface<File,Report> verifyP7S(P7SData<File,File> data) throws FirmapiuException;
}
