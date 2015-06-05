/**
 * 
 */
package it.libersoft.firmapiu.data;

import it.libersoft.firmapiu.exception.FirmapiuException;

import java.util.Map;
import java.util.Set;

/**
 * Implementazione concreta di P7DataByteArray
 * 
 * @author dellanna
 * @see it.libersoft.firmapiu.cades.P7SData
 *
 */
final class P7SDataByteArrayImpl implements P7SDataByteArray {

	/**
	 * 
	 */
	public P7SDataByteArrayImpl() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see it.libersoft.firmapiu.cades.P7SData#putP7SData(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void putP7SData(byte[] key, byte[] content) throws FirmapiuException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see it.libersoft.firmapiu.cades.P7SData#getP7SContent(java.lang.Object)
	 */
	@Override
	public byte[] getP7SContent(byte[] key) throws FirmapiuException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see it.libersoft.firmapiu.Data#setData(java.lang.Object)
	 */
	@Override
	public void setData(byte[] data) throws FirmapiuException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see it.libersoft.firmapiu.Data#getDataSet()
	 */
	@Override
	public Set<byte[]> getDataSet() throws FirmapiuException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see it.libersoft.firmapiu.Data#getDataId(java.lang.Object)
	 */
	@Override
	public String getDataId(byte[] data) throws FirmapiuException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see it.libersoft.firmapiu.Data#getArrayData(java.lang.Object)
	 */
	@Override
	public byte[] getArrayData(byte[] data) throws FirmapiuException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see it.libersoft.firmapiu.Data#setArgument(java.lang.String, java.lang.String)
	 */
	@Override
	public void setArgument(String key, String value) throws FirmapiuException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see it.libersoft.firmapiu.Data#getArgumentMap()
	 */
	@Override
	public Map<String, String> getArgumentMap() throws FirmapiuException {
		// TODO Auto-generated method stub
		return null;
	}

}
