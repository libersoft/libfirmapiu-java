/**
 * 
 */
package it.libersoft.firmapiu.data;

import it.libersoft.firmapiu.exception.FirmapiuException;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Implementazione concreta comune dell'interfaccia DataByteArray e
 * P7SDataByteArray: rappresenta il contenuto di un file, una busta
 * crittografica o di dati di vario tipo rappresentati come array di byte
 * 
 * @author dellanna
 *
 */
final class CommonDataByteArrayImpl implements DataByteArray, P7SDataByteArray {

	// insieme contenete i dati rappresentati come array di bytes su cui bisogna
	// fare le operazioni
	// di firma/verifica
	private final HashMap<ArrayWrapper, byte[]> wrapperByteArrayMap;
	// insieme contenente gli argomenti opzionali da passare al comando di
	// firma/verifica
	private final TreeMap<String, String> commandArgs;

	/**
	 * 
	 */
	protected CommonDataByteArrayImpl() {
		// TODO vedere se ce bisogno di sincronizzare o meno se si considera
		// l'opzione di usare hashtable
		// TODO mettere proprietà per gestire il digest? se bisogna fare i
		// digest di un digest o magari anche no?

		this.wrapperByteArrayMap = new HashMap<ArrayWrapper, byte[]>();
		this.commandArgs = new TreeMap<String, String>();
	}

	@Override
	public void putP7SData(byte[] key, byte[] content) throws FirmapiuException {
		this.wrapperByteArrayMap.put(new ArrayWrapper(key), content);
	}

	@Override
	public byte[] getP7SContent(byte[] key) throws FirmapiuException {
		return this.wrapperByteArrayMap.get(new ArrayWrapper(key));
	}

	/**
	 * Aggiunge l'array di bytes rappresentante i dati che devono essere firmati
	 * o di cui bisogna verificare la firma
	 * 
	 * @param data
	 *            array di bytes da firmare
	 * @see it.libersoft.firmapiu.Data#setData(java.lang.Object)
	 */
	@Override
	public void setData(byte[] data) throws FirmapiuException {
		this.wrapperByteArrayMap.put(new ArrayWrapper(data), null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.libersoft.firmapiu.Data#getDataSet()
	 */
	@Override
	public Set<byte[]> getDataSet() throws FirmapiuException {
		Set<byte[]> byteDataSet = new HashSet<byte[]>();

		Iterator<ArrayWrapper> itr = this.wrapperByteArrayMap.keySet()
				.iterator();
		while (itr.hasNext()) {
			byteDataSet.add(itr.next().byteArray);
		}
		return byteDataSet;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.libersoft.firmapiu.Data#getDataId(java.lang.Object)
	 */
	@Override
	public String getDataId(byte[] data) throws FirmapiuException {
		// TODO stabilire un metodo per restituire un id: al momento fa
		// l'hashcode del arraywrapper
		ArrayWrapper wrappedData = new ArrayWrapper(data);
		if (this.wrapperByteArrayMap.containsKey(wrappedData))
			return wrappedData.toString();
		else
			return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.libersoft.firmapiu.Data#getArrayData(java.lang.Object)
	 */
	@Override
	public byte[] getArrayData(byte[] data) throws FirmapiuException {
		// si restituisce la medesima rappresentazione di byte o null se data
		// non è una chiave
		// TODO E' il caso di fare il clone dell'oggetto? Per il momento non lo
		// fa
		ArrayWrapper wrappedData = new ArrayWrapper(data);
		if (this.wrapperByteArrayMap.containsKey(wrappedData))
			return data;
		else
			return null;
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

	// oggetto privato wrapper utilizzato per fare l'override del metodo
	// Object.hashCode
	private final static class ArrayWrapper {

		private final byte[] byteArray;

		private final int hashCode;

		private ArrayWrapper(byte[] byteArray) {
			this.byteArray = byteArray;
			this.hashCode = this.hashCodeProcedure();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			return hashCode;
		}// fine hashCode

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return Integer.toString(hashCode);
		}

		/**
		 * nell'implementazione di questo oggetto, due oggetti sono considerati
		 * essere uguali se hanno lo stesso numero di bytes e lo stesso hashcode
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			ArrayWrapper toCheckObj = (ArrayWrapper) obj;
			if (this.byteArray.length == toCheckObj.byteArray.length) {
				if (this.hashCode == toCheckObj.hashCode)
					return true;
			}
			return false;
		}

		// procedura privata che fa l'hashCode
		private int hashCodeProcedure() {
			try {
				// calcola l'hashcode facendo il digest del contenuto passato
				MessageDigest md5digest = MessageDigest.getInstance("MD5");
				byte[] digest = md5digest.digest(byteArray);
				int result = 0;
				for (int digit : digest)
					result += Math.abs(digit);
				return result;
			} catch (NoSuchAlgorithmException e) {
				return this.byteArray.hashCode();
			}
		}

	}// fine ArrayWrapper
}
