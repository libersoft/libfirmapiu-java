/**
 * 
 */
package it.libersoft.firmapiu.data;

/**
 * Questa factory crea un insieme di dati da passare come parametro di input alle operazioni di firma e verifica<p>
 * 
 * I dati richiesti sono un insieme di dati rappresentati come array di byte
 * 
 * @author dellanna
 *
 */
final class DataByteArrayFactory extends DefaultDataFactory {

	/**
	 * 
	 */
	public DataByteArrayFactory() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see it.libersoft.firmapiu.data.DefaultDataFactory#getDataByteArray()
	 */
	@Override
	public DataByteArray getDataByteArray() throws IllegalArgumentException {
		return new DataByteArrayImpl();
	}	
}