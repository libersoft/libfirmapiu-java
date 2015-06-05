/**
 * 
 */
package it.libersoft.firmapiu.data;

import java.io.File;

import it.libersoft.firmapiu.cades.P7SData;

/**
 * Supporta dati P7S rappresentati come Files
 * 
 * @author dellanna
 * 
 * @param <File> Rappesenta il file .p7s.<br>
 * Dovrebbero essere delle chiavi per non firmare/verificare gli stessi dati due volte
 * @param <File> Rappresenta il file da cui è stato generato il p7s
 * 
 * @see it.libersoft.firmapiu.cades.P7SData
 */
public interface P7SDataFile extends P7SData<File, File> {

}
