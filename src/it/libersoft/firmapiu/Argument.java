/**
 * 
 */
package it.libersoft.firmapiu;

import java.util.Map;

/**
 * Contiene gli argomenti "opzionali" associate alle operazioni da eseguire.<br>
 * (Ad esempio il pin della carta crittografica utilizzata per firmare un file, la directory in cui salvare i dati firmati etc. etc.)  
 * 
 * @author dellanna
 *
 */
public interface Argument<K,V> {
	
	/**
	 * Aggiunge un argomento opzionale al comando/operazione da eseguire
	 * 
	 * @param key nome/"chiave" dell'argomento da aggiungere. Due argomenti con la stessa chiave vengono sovrascritti
	 * @param value valore associato all'argomento
	 */
	public void setArgument(K key, V value);
	
	/**
	 * Recupera il valore di un argomento associato al comando/operazione da eseguire
	 * 
	 * @param key
	 * @return restituisce il valore associato alla chiave passata come parametro
	 */
	public V getArgument(K key);
	
	/**
	 * Controlla che l'argomento sia presente o meno
	 * 
	 * @param key l'argomento da controllare
	 * @return Un booleano che dice se l'argomento è presente o meno
	 */
	public boolean isArgument(K key);
	
	/**
	 * @return l'insieme di tutti gli argomenti e valori associati al comando da eseguire
	 */
	public Map<K,V> getArgumentMap();
}
