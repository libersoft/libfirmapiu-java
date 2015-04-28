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
public class P7SDataFilePath extends TreeMap<String,String> implements Data<String> {

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	protected P7SDataFilePath() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setData(String data) throws FirmapiuException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<String> getDataSet() throws FirmapiuException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDataId(String data) throws FirmapiuException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] getArrayData(String data) throws FirmapiuException {
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
