/**
 * 
 */
package it.libersoft.firmapiu.data;

import java.io.File;

import it.libersoft.firmapiu.Data;

/**
 * Questa interfaccia rappresenta un insieme di file da passare come parametro
 * di input alle operazioni di firma/verifica
 * <p>
 * 
 * I file presi in considerazioni devono essere delle "chiavi" in modo tale che
 * il sistema non firmi lo stesso file più di una volta<br>
 * I percorsi dei file da firmare devono essere percorsi assoluti.
 * 
 * @author dellanna
 * @see it.libersoft.firmapiu.Data
 * @see it.libersoft.firmapiu.consts.ArgumentConsts
 */
public interface DataFile extends Data<File> {

}
