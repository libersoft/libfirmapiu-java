/**
 * 
 */
package it.libersoft.firmapiu.data;

import it.libersoft.firmapiu.exception.FirmapiuException;

import java.io.File;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Implementazione concreta dell'interfaccia DataByteArray: rappresenta il
 * contenuto di un file, una busta crittografica o di dati di vario tipo
 * rappresentati come array di byte
 * 
 * @author dellanna
 *
 */
final class DataByteArrayImpl implements DataByteArray {

	// insieme contenete i dati rappresentati come array di bytes su cui bisogna fare le operazioni
	// di firma/verifica
	private final HashSet<byte[]> byteArraySet;
	// insieme contenente gli argomenti opzionali da passare al comando di
	// firma/verifica
	private final TreeMap<String, String> commandArgs;

	
	/**
	 * 
	 */
	protected DataByteArrayImpl() {
//		// TODO vedere se ce bisogno di sincronizzare o meno
//		//inizializza un comparatore da passare al treeset
//		Comparator comparator=new Comparator<Byte[]>() {
//			public int compare(byte[] a, byte[] b)
//			{
//				//TODO bisognerebbe fare il comparatore ammodo: un buon metodo potrebbe essere quello
//				//di fare il digest e comparare il digest, ma se è il digest del digest?
//				//TODO mettere proprietà per gestire il digest
//				//TODO al momento l'implementazione controlla tutti i byte. E' molto pesante! da rifare con digest
//				
//				
//				
//				return Long.compare(a[0], b[0]);
//			}
//		}
		this.byteArraySet = new HashSet<byte[]>();
		this.commandArgs = new TreeMap<String, String>();
	}

	/**
	 * Aggiunge l'array di bytes rappresentante i dati che devono essere
	 * firmati o di cui bisogna verificare la firma
	 * 
	 * @param data
	 *            array di bytes da firmare
	 * @see it.libersoft.firmapiu.Data#setData(java.lang.Object)
	 */
	@Override
	public void setData(byte[] data) throws FirmapiuException {
		// TODO ci sarebbe da implementare un controllo per vedere che effettivamente i dati passati sono diversi,
		this.byteArraySet.add(data);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.libersoft.firmapiu.Data#getDataSet()
	 */
	@Override
	public Set<byte[]> getDataSet() throws FirmapiuException {
		return (Set<byte[]>) this.byteArraySet.clone();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.libersoft.firmapiu.Data#getDataId(java.lang.Object)
	 */
	@Override
	public String getDataId(byte[] data) throws FirmapiuException {
		// TODO stabilire un metodo per restituire un id: per il momento si fa new String(byte[])
		return new String(data);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.libersoft.firmapiu.Data#getArrayData(java.lang.Object)
	 */
	@Override
	public byte[] getArrayData(byte[] data) throws FirmapiuException {
		//si restituisce la medesima rappresentazione di byte
		//TODO E' il caso di fare il clone dell'oggetto? Per il momento no
		return data;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.libersoft.firmapiu.Data#setArgument(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void setArgument(String key, String value) throws FirmapiuException {
		this.commandArgs.put(key, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.libersoft.firmapiu.Data#getArgumentMap()
	 */
	@Override
	public Map<String, String> getArgumentMap() throws FirmapiuException {
		return (Map<String, String>) this.commandArgs.clone();
	}

}
