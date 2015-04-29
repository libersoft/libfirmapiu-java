/**
 * 
 */
package it.libersoft.firmapiu;

import it.libersoft.firmapiu.exception.FirmapiuException;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author dellanna
 *
 */
public class P7SData<K> extends TreeMap<String,String> implements Data<K> {

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	protected P7SData() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setData(K data) throws FirmapiuException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<K> getDataSet() throws FirmapiuException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDataId(K data) throws FirmapiuException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] getArrayData(K data) throws FirmapiuException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setArgument(String key, String value) throws FirmapiuException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, String> getArgumentMap() throws FirmapiuException {
		// TODO Auto-generated method stub
		return null;
	}

}
