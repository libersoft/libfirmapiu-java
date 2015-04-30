/**
 * 
 */
package it.libersoft.firmapiu.data;

import it.libersoft.firmapiu.Data;

/**
* Questa interfaccia rappresenta una busta crittografica o il contenuto di file, rappresentati come array di byte,
*  da passare come parametro di input alle operazioni di firma/verifica
* <p>
* 
* I dati presi in considerazione dovrebbero essere delle "chiavi" in modo tale che
* il sistema non firmi/verifichi gli stessi dati più di una volta<br>
* 
* @author dellanna
* @see it.libersoft.firmapiu.Data
* @see it.libersoft.firmapiu.consts.ArgumentConsts
*/
public interface DataByteArray extends Data<byte[]> {

}
