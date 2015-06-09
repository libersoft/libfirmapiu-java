/**
 * 
 */
package it.libersoft.firmapiu.cades;

import it.libersoft.firmapiu.CommandInterface;
import it.libersoft.firmapiu.Data;
import it.libersoft.firmapiu.ResultInterface;
import it.libersoft.firmapiu.exception.FirmapiuException;

/**
 * Interfaccia "astratta" che mette a disposizione l'operazione verifyP7S per
 * verificare una busta Cades-BES (CMS,.p7s,pkcs7) detached
 * <p>
 * 
 * Non è direttamente visibile dagli utilizzatori della libreria che utilizzano
 * invece le implementazioni concrete realizzate per il package
 * it.libersoft.firmapiu.cades
 * 
 * @author dellanna
 *
 */
interface CadesBESCommandInterface<K,V,W> extends CommandInterface<K, V> {
	

	/**
	 * Verifica i dati presenti in una busta crittografica Cades-BES/CMS salvandoli in un CMSReport 
	 * 
	 * @see it.libersoft.firmapiu.CommandInterface#verify(it.libersoft.firmapiu.Data)
	 */
	public ResultInterface<K,CMSReport> verify(Data<K> data) throws FirmapiuException;
	
	/**
	 * Verifica i file p7s nel formato Cades-BES per controllarne la correttezza
	 * e l'affidabilità
	 * 
	 * @param data
	 *            I dati da controllare (devono contenere un mapping p7s,
	 *            contenuto originale di un file)
	 * @return Un report contentente L'esito della verifica per ogni p7s passato
	 *         comeparametro
	 * @throws FirmapiuException
	 *             in caso di errore applicativo
	 */
	public ResultInterface<K, CMSReport> verifyP7S(
			P7SData<K, W> data) throws FirmapiuException;
}
