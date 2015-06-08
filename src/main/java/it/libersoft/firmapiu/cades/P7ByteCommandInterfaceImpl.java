/**
 * 
 */
package it.libersoft.firmapiu.cades;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSTypedData;

import it.libersoft.firmapiu.CRToken;
import it.libersoft.firmapiu.Data;
import it.libersoft.firmapiu.ResultInterface;
import it.libersoft.firmapiu.exception.FirmapiuException;
import static it.libersoft.firmapiu.exception.FirmapiuException.*;

/**
 * @author dellanna
 *
 */
final class P7ByteCommandInterfaceImpl extends
		AbstractCadesBESCommandInterface<byte[], byte[]> implements
		P7ByteCommandInterface {

	P7ByteCommandInterfaceImpl(CRToken signToken, CRToken verifyToken,
			String digestCalculatorProviderStr) {
		super(signToken, verifyToken, digestCalculatorProviderStr);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.libersoft.firmapiu.CommandInterface#getSignToken()
	 */
	@Override
	public CRToken getSignToken() throws FirmapiuException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.libersoft.firmapiu.CommandInterface#getVerifyCrToken()
	 */
	@Override
	public CRToken getVerifyCrToken() throws FirmapiuException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.libersoft.firmapiu.cades.P7ByteCommandInterface#verifyP7S(it.libersoft
	 * .firmapiu.P7SData)
	 */
	@Override
	public ResultInterface<byte[], CMSReport> verifyP7S(P7SData<byte[],byte[]> data)
			throws FirmapiuException {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Restituisce la CMSTypedDataResultInterface<byte[],byte[]> effettivamente
	 * utilizzata dall'operazione di sign per salvare i risultati ottenuti
	 * 
	 * @see it.libersoft.firmapiu.cades.AbstractCadesBESCommandInterface#getCMSTypedDataResultInterface()
	 */
	@Override
	CMSSignedDataResultInterface<byte[], byte[]> getCMSSIgnedDataResultInterface() {
		// TODO Auto-generated method stub
		return new ResultByteInterfaceImpl();
	}

	@Override
	CMSTypedDataResultInterface<byte[], byte[]> getCMSTypedDataResultInterface() {
		// TODO Auto-generated method stub
		return null;
	}

	// implementazione privata di CMSTypedDataResultInterface
	private class ResultByteInterfaceImpl implements
	CMSSignedDataResultInterface<byte[], byte[]> {

		private final HashMap<byte[], Object> result;

		protected ResultByteInterfaceImpl() {
			this.result = new HashMap<byte[], Object>();
		}

		@Override
		public Set<byte[]> getResultDataSet() throws FirmapiuException {
			return this.result.keySet();
		}

		@Override
		public byte[] getResult(byte[] key) throws FirmapiuException {

			if (this.result.containsKey(key)) {
				Object value = this.result.get(key);
				if (value instanceof FirmapiuException)
					throw (FirmapiuException) value;
				else
					return (byte[]) value;
			} else
				return null;
		}

		@Override
		public void putFirmapiuException(byte[] key, FirmapiuException e) {
			this.result.put(key, e);
		}

		@Override
		public void put(byte[] key, CMSSignedData signedData)
				throws FirmapiuException {
			try {
				this.result.put(key, signedData.getEncoded());
			} catch (IOException e) {
				FirmapiuException fe1 =new FirmapiuException(IO_DEFAULT_ERROR, e);
				throw fe1;
			}
		}

	}
}
