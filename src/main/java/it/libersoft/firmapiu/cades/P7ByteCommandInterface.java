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
public interface P7ByteCommandInterface extends CommandInterface<byte[], byte[]> {
	
	
	/**
	 * Verifica i dati presenti in una busta crittografica Cades-BES/CMS salvandoli in un CMSReport 
	 * 
	 * @see it.libersoft.firmapiu.CommandInterface#verify(it.libersoft.firmapiu.Data)
	 */
	public ResultInterface<byte[],CMSReport> verify(Data<byte[]> data) throws FirmapiuException;
	
	/**
	 * Verifica i file p7s nel formato Cades-BES per controllarne la correttezza e l'affidabilità
	 * 
	 * @param data I dati da controllare (devono contenere un mapping p7s, contenuto originale di un file)
	 * @return Un report contentente L'esito della verifica per ogni p7s passato comeparametro 
	 * @throws FirmapiuException in caso di errore applicativo
	 */
	public ResultInterface<byte[],CMSReport> verifyP7S(P7SData<byte[],byte[]> data) throws FirmapiuException;
	
}
