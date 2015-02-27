/**
 * 
 */
package it.libersoft.firmapiu;

import java.util.Map;
import java.util.TreeMap;

/**
 * Questa è una classe generica di argomenti che possono essere passati come parametro delle operazioni di Firma e Verifica 
 * 
 * @author dellanna
 *
 */
public final class GenericArgument implements Argument<String,Object> {

	//mappa contenete tutti gli argomenti utilizzati dal comando
	private TreeMap<String,Object> argumentmap;
	
	protected GenericArgument() {
		// TODO vedere se ce bisogno di sincronizzare o meno
		this.argumentmap=new TreeMap<String,Object>();
	}

	/** 
	 * @param key L'argomento è identificato tramite una stringa
	 * @param value All'argomento può essere associato un oggetto generico
	 * 
	 * @see it.libersoft.firmapiu.Argument#setArgument(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setArgument(String key, Object value) {
		// TODO Auto-generated method stub
		this.argumentmap.put(key, value);
	}

	/**
	 * 
	 * @see it.libersoft.firmapiu.Argument#getArgument(java.lang.Object)
	 */
	@Override
	public Object getArgument(String key) {
		return this.argumentmap.get(key);
	}
	
	/**
	 * @see it.libersoft.firmapiu.Argument#isArgument(java.lang.Object)
	 */
	@Override
	public boolean isArgument(String key) {
		return this.argumentmap.containsKey(key);
	}

	/**
	 * @see it.libersoft.firmapiu.Argument#getArgumentMap()
	 */
	@Override
	public Map<String,Object> getArgumentMap() {
		return (TreeMap<String,Object>) this.argumentmap.clone();
	}

}
