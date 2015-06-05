/**
 * 
 */
package it.libersoft.firmapiu.data;

import it.libersoft.firmapiu.cades.P7SData;

/**
 * Supporta dati P7S rappresentati come array di bytes
 * 
 * @author dellanna
 * 
 * @param <byte[]> Rappesentazione in bytes di un p7s/Cades-bes detached<br>
 *        Dovrebbero essere delle chiavi per non firmare/verificare gli stessi
 *        dati due volte
 * @param <byte[]> Rappresentazione in bytes dei dati utilizzati per generare
 *        p7s/Cades-bes detached
 * 
 * @see it.libersoft.firmapiu.cades.P7SData
 */
public interface P7SDataByteArray extends P7SData<byte[], byte[]> {

}
