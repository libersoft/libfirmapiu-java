/**
 * 
 */
package it.libersoft.firmapiu.cades;

import java.security.ProviderException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSTypedData;

import it.libersoft.firmapiu.CRToken;
import it.libersoft.firmapiu.CommandInterface;
import it.libersoft.firmapiu.Data;
import it.libersoft.firmapiu.ResultInterface;
import it.libersoft.firmapiu.exception.FirmapiuException;
import static it.libersoft.firmapiu.consts.ArgumentConsts.*;
import static it.libersoft.firmapiu.exception.FirmapiuException.CRT_TOKEN_NOTFOUND;
import static it.libersoft.firmapiu.exception.FirmapiuException.SIGNER_CADESBES_ERROR;
import static it.libersoft.firmapiu.exception.FirmapiuException.SIGNER_TOKEN_REMOVED;

/**
 * 
 * @author dellanna
 *
 */
abstract class AbstractCadesBESCommandInterface<K,V> implements
		CommandInterface<K, V> {
	
	//tipo di token utilizzato per le operazioni di firma
	private final CRToken signToken;
	//tipo di token utilizzato per le operazioni di verifica
	private final CRToken verifyToken;

	//digestCalculator provider utilizzato per le operazioni di firma
	private final String digestCalculatorProviderStr;

	/**
	 * 
	 */
	AbstractCadesBESCommandInterface(CRToken signToken,CRToken verifyToken,String digestCalculatorProviderStr) {
		this.signToken=signToken;
		this.verifyToken=verifyToken;
		this.digestCalculatorProviderStr=digestCalculatorProviderStr;
	}

	/* (non-Javadoc)
	 * @see it.libersoft.firmapiu.CommandInterface#sign(it.libersoft.firmapiu.Data)
	 */
	@Override
	public ResultInterface<K, V> sign(Data<K> data) throws FirmapiuException {
		//se non è stato definito il token per la firma lancia un eccezione
		if(this.signToken==null)
			throw new FirmapiuException(CRT_TOKEN_NOTFOUND, new NullPointerException("signToken=null"));
		
		//controlla gli argomenti di Data<K> data
		Map<String,String> commandArgs = data.getArgumentMap();
		
		//contenuto attached/detached dei file
		boolean detached=false;
		if(commandArgs.containsKey(DETACHED))
			detached=Boolean.parseBoolean(commandArgs.get(DETACHED));
		
		//Crea il resultSet da restituire in risposta
		CMSSignedDataResultInterface<K, V> resultInterface = this.getCMSSIgnedDataResultInterface();
		
		//itera sul set dei dati da firmare e salva i dati firmati nel resultInterface
		Set<K> dataSet=data.getDataSet();
		Iterator<K> itr= dataSet.iterator();
		//TODO vedere se signer può essere inizializzato dentro il while per ottimizzare 
		CadesBESSigner signer = new CadesBESSigner(this.signToken,this.digestCalculatorProviderStr);
		while(itr.hasNext()){
			K dataKey=itr.next();
			try {
				CMSTypedData cmsData=new CMSProcessableByteArray(data.getArrayData(dataKey));
				CMSSignedData signedData=signer.sign(cmsData, !detached);
				resultInterface.put(dataKey, signedData);
			} catch (FirmapiuException e) {
				resultInterface.putFirmapiuException(dataKey, e);
			} catch (CMSException e) {
				FirmapiuException fe1 =new FirmapiuException(SIGNER_CADESBES_ERROR, e);
				resultInterface.putFirmapiuException(dataKey, fe1);
			}catch (ProviderException e){
				//questa eccezione potrebbe essere lanciata se si rimuove il token pkcs11 durante il processo di firma
				//se il token è PKCS11token si slogga e rilancia l'eccezione firmapiuexception al chiamante
				//TODO si prova a gestire il token pkcs11 da un altra parte
				//				if((token!=null)&&(token instanceof PKCS11Token)){
//					((PKCS11Token)token).logout();
//				}
				FirmapiuException fe1 =new FirmapiuException(SIGNER_TOKEN_REMOVED, e);
				throw fe1;
			}
		}//fine while
		
		return resultInterface;
	}

	/* (non-Javadoc)
	 * @see it.libersoft.firmapiu.CommandInterface#verify(it.libersoft.firmapiu.Data)
	 */
	@Override
	public ResultInterface<K, CMSReport> verify(Data<K> data)
			throws FirmapiuException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see it.libersoft.firmapiu.CommandInterface#getContentSignedData(it.libersoft.firmapiu.Data)
	 */
	@Override
	public ResultInterface<K, V> getContentSignedData(Data<K> signedData)
			throws FirmapiuException {
		// TODO Auto-generated method stub
		return null;
	}

	//crea una result interface da CMSSignedData
	abstract CMSSignedDataResultInterface<K, V> getCMSSIgnedDataResultInterface();
	
	//crea una result Interface da una CMSTypedData
	abstract CMSTypedDataResultInterface<K, V> getCMSTypedDataResultInterface();
}
