/**
 * 
 */
package it.libersoft.firmapiu.cades;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.SignerInformation;

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
		AbstractCadesBESCommandInterface<byte[], byte[],byte[]> implements
		P7ByteCommandInterface {

	P7ByteCommandInterfaceImpl(CRToken signToken, CRToken verifyToken,
			String digestCalculatorProviderStr) {
		super(signToken, verifyToken, digestCalculatorProviderStr);
		// TODO Auto-generated constructor stub
	}



	
	/**
	 * Restituisce la CMSTypedDataResultInterface<byte[],byte[]> effettivamente
	 * utilizzata dall'operazione di sign per salvare i risultati ottenuti
	 * 
	 * @see it.libersoft.firmapiu.cades.AbstractCadesBESCommandInterface#getCMSTypedDataResultInterface()
	 */
	@Override
	CMSSignedDataResultInterface<byte[], byte[]> getCMSSIgnedDataResultInterface() {
		return new ResultByteInterfaceImpl();
	}


	@Override
	CMSReportResultInterface<byte[]> getCMSReportResultInterface() {
		return new CMSReportResultInterfaceImpl();
	}
	
	@Override
	CMSTypedDataResultInterface<byte[], byte[]> getCMSTypedDataResultInterface() {
		return new ResultByteInterfaceImpl();
	}

	// implementazione privata di CMSTypedDataResultInterface
	private final class ResultByteInterfaceImpl implements
	CMSSignedDataResultInterface<byte[], byte[]> ,CMSTypedDataResultInterface<byte[], byte[]>{

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

		@Override
		public void put(byte[] key, CMSTypedData data) throws FirmapiuException {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			try {
				data.write(bout);
				byte[] content=bout.toByteArray();
				this.result.put(key, content);
			} catch (IOException e) {
				FirmapiuException fe1 =new FirmapiuException(IO_DEFAULT_ERROR, e);
				throw fe1;
			} catch (CMSException e) {
				throw new FirmapiuException(CONTENT_CADESBES_DEFAULT_ERROR, e);
			} finally{
				try {bout.close();} catch (IOException e) {}
			}
		}
	}//fine ResultByteInterfaceImpl
	
	//classe privata per gestire i report restituiti in fase di verifica
	private final class CMSReportResultInterfaceImpl implements CMSReportResultInterface<byte[]>{

		private final HashMap<byte[], Object> result;
		
		protected CMSReportResultInterfaceImpl() {
			this.result = new HashMap<byte[], Object>();
		}
		
		@Override
		public Set<byte[]> getResultDataSet() throws FirmapiuException {
			return this.result.keySet();
		}

		@Override
		public CMSReport getResult(byte[] key) throws FirmapiuException {
			if (this.result.containsKey(key)) {
				Object value = this.result.get(key);
				if (value instanceof FirmapiuException)
					throw (FirmapiuException) value;
				else
					return (CMSReport) value;
			} else
				return null;
		}

		@Override
		public void putFirmapiuException(byte[] key, FirmapiuException e) {
			this.result.put(key, e);
		}

		@Override
		public void put(byte[] key, CadesBESVerifier verifier)
				throws FirmapiuException {
			//genera il report dal cadesbesverifier
			ReportImpl report= new ReportImpl(verifier);
			this.result.put(key, report);
		}	
	}//fine CMSReportResultInterfaceImpl
}