package it.libersoft.firmapiu;

import java.util.Set;

import it.libersoft.firmapiu.exception.FirmapiuException;

/**
 * Interfaccia utilizzata per gestire le risposte restituite dalle operazioni
 * definite in it.libersoft.firmapiu.CommandInterface
 * 
 * @author dellanna
 *
 * @param <K>
 *            Parametro utilizzato per parametrizzare le chiavi corrispondenti
 *            alle risposte restituite
 * @param <V>
 *            Parametro utilizzato per parametrizzare i valori effettivi
 *            corrispondenti alle chiavi
 */
public interface ResultInterface<K, V> {

	/**
	 * Restituisce una Set contenente le chiavi delle risposte restituite dalle
	 * operazioni corrispondenti. A meno di normalizzazioni questo set di chiavi
	 * dovrebbe corrispondere a quello definito da Data<K>
	 * 
	 * @return il set di chiavi delle risposte restituite
	 * @throws FirmapiuException
	 *             in caso di errore applicativo
	 */
	public Set<K> getResultDataSet() throws FirmapiuException;

	/**
	 * Restituisce il valore della risposta corrispondente alla chiave richiesta
	 * 
	 * @param key
	 *            la chiave richiesta
	 * @return Il valore corrispondente alla chiave
	 * @throws FirmapiuException
	 *             in caso di errore applicativo
	 */
	public V getResult(K key) throws FirmapiuException;

	/**
	 * Inserisce un eccezione per una determinata chiave: quando verrà fatta la
	 * getResult per quella determinata chiave, l'implementazione concreta dovrà
	 * lanciare la firmapiuException passata come paramentro in questo metodo
	 * 
	 * @param key
	 *            la chiave richiesta
	 * @param e
	 *            L'eccezione da "iniettare"
	 */
	public void putFirmapiuException(K key, FirmapiuException e);
}
