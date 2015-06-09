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
 * @param <String> Tipo di dato associato alle chiavi utilizzate per associare i file da firmare/verificare al tipo di valore restituito<br>
 * Rappresentano i percorsi assoluti dei file da firmare/verificare  
 * @param <File>
 *            Tipo di dato associato ai valori restituiti da
 *            ResultInterface<K,V>
 */
public interface P7FileCommandInterface extends CadesBESCommandInterface<File,File,File> {

}
