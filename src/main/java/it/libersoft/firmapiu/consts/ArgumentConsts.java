/**
 * 
 */
package it.libersoft.firmapiu.consts;

/**
 * Questa classe contiene una serie di costanti che possono essere usate come
 * chiavi per gli argomenti opzionali dei comandi/operazioni di firma/verifica
 * eseguiti su un insieme di dati
 * 
 * @author dellanna
 * @see it.libersoft.firmapiu.Argument
 */
public final class ArgumentConsts {
	/**
	 * Chiave: pin associato al token crittografico utilizzato per
	 * l'operazione/comando di firma<br>
	 * Valore associato all'argomento: char[] - Un array di char contenete il
	 * pin richiesto
	 */
	// FIXME versione debug, da cambiare
	public static final String TOKENPIN = "pin";
	// public static final String TOKENPIN = "tokenpin";
	/**
	 * Chiave: directory in cui salvare l'esito del risultato dell'operazione di
	 * firma<br>
	 * Valore associato all'argomento: String - Il percorso della directory in
	 * cui salvare l'esito dell'operazione
	 * */
	// FIXME versione debug, da cambiare
	public static final String OUTDIR = "outdir";
	// public static final String OUTDIR = "signoutdir";

	/**
	 * Chiave: se l'opzione è presente, la directory in cui salvare l'esito del
	 * risultato dell'operazione di firma, deve essere creata (o meno) nel caso
	 * in cui non esiste<br>
	 * Valore associato all'argomento: Boolean - se la directory deve essere
	 * creata o no cui salvare l'esito dell'operazione
	 * */
	public static final String CREATEOUTDIR = "createoutdir";

	/**
	 * Chiave: Opzione usata durante l'operazione di firma per la generazione di
	 * buste crittografiche Cades-BES<br>
	 * Se l'opzione è presente, il contenuto dei dati da firmare deve essere
	 * detached dalla busta crittografica generata se il valore è impostato a
	 * true<br>
	 * Valore associato all'argomento: Boolean - se il contenuto dei dati da
	 * firmare deve essere detached o meno dalla busta crittografica generata
	 * */
	public static final String DETACHED = "detached";

	/**
	 * Chiave: Opzione usata durante l'operazione di firma di un file o recupero
	 * del contenuto originale di un file.<br>
	 * La libreria non prevede l'override di un file per motividi sicurezza<br>
	 * . Se l'opzione è presente ed è uguale a true la libreria duplica i file con nome uguale
	 * in modo da avere un nome diverso<br>
	 * Valore associato all'argomento: Boolean - Se l'opzione è presente i file
	 * con nome uguale possono essere duplicati con un nome diverso
	 * */
	public static final String COPYFILE = "copyfile";
	
	
	/**
	 * Chiave: Opzione usata durante l'operazione di firma di un file o recupero
	 * del contenuto originale di un file.<br>
	 * La libreria non prevede l'override di un file per motividi sicurezza<br>
	 * . Se l'opzione è presente i file con nome uguale possono essere duplicati
	 * in modo da avere un nome diverso<br>
	 * Valore associato all'argomento: String - Se l'opzione è presente i file
	 * con nome uguale possono essere duplicati con un nome diverso
	 * */
	public static final String RENAMEFILE = "renamefile";
}
