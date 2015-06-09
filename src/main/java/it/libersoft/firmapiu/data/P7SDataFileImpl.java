/**
 * 
 */
package it.libersoft.firmapiu.data;

import it.libersoft.firmapiu.exception.FirmapiuException;

import java.io.File;
import java.util.Map;
import java.util.Set;

/**
 * Implementazione concreta di P7DataFile
 * 
 * @author dellanna
 * @see it.libersoft.firmapiu.cades.P7SData
 */
final class P7SDataFileImpl implements P7SDataFile {

	/**
	 * 
	 */
	public P7SDataFileImpl() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see it.libersoft.firmapiu.cades.P7SData#putP7SData(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void putP7SData(File key, File content) throws FirmapiuException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see it.libersoft.firmapiu.cades.P7SData#getP7SContent(java.lang.Object)
	 */
	@Override
	public File getP7SContent(File key) throws FirmapiuException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see it.libersoft.firmapiu.Data#setData(java.lang.Object)
	 */
	@Override
	public void setData(File data) throws FirmapiuException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see it.libersoft.firmapiu.Data#getDataSet()
	 */
	@Override
	public Set<File> getDataSet() throws FirmapiuException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see it.libersoft.firmapiu.Data#getDataId(java.lang.Object)
	 */
	@Override
	public String getDataId(File data) throws FirmapiuException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see it.libersoft.firmapiu.Data#getArrayData(java.lang.Object)
	 */
	@Override
	public byte[] getArrayData(File data) throws FirmapiuException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] getContentArrayData(File key) throws FirmapiuException {
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
