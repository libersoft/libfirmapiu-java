/**
 * 
 */
package it.libersoft.firmapiu.cades;

import it.libersoft.firmapiu.CommandInterface;
import it.libersoft.firmapiu.Data;
import it.libersoft.firmapiu.ResultInterface;
import it.libersoft.firmapiu.exception.FirmapiuException;

/**
 * Interfaccia di comandi utilizzata per effettuare la firma e la verifica di un
 * insieme di dati passati come parametro in un formato Cades-BES.<br>
 * 
 * L'interfaccia opera su array di byte che rappresentato i dati che devono essere firmati e verificati
 * 
 * <a href="http://en.wikipedia.org/wiki/CAdES_(computing)">http://en.wikipedia.
 * org/wiki/CAdES</a>
 * 
 * @author dellanna
 * 
 * @param <byte> Array di byte: rappresentano i dati che devono essere firmati verificati.<br>
 * Dovrebbero essere delle chiavi per non firmare/verificare gli stessi dati due volte
 * @param <byte> Array di byte: rappresentano i valori associati alle chiavi. Sono i dati firmati o di cui è stato
 * restituito il contenuto originale (Se i dati erano inbustati "attached" in una busta Cades-Bes)
 */
public interface P7ByteCommandInterface extends CadesBESCommandInterface<byte[], byte[],byte[]> {
		
}
