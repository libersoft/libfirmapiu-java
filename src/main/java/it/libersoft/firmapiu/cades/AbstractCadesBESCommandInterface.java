/**
 * 
 */
package it.libersoft.firmapiu.cades;

import java.security.ProviderException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.SignerInformation;

import it.libersoft.firmapiu.CRToken;
import it.libersoft.firmapiu.CommandInterface;
import it.libersoft.firmapiu.Data;
import it.libersoft.firmapiu.ResultInterface;
import it.libersoft.firmapiu.exception.FirmapiuException;
import static it.libersoft.firmapiu.consts.ArgumentConsts.*;
import static it.libersoft.firmapiu.exception.FirmapiuException.*;

/**
 * 
 * @author dellanna
 *
 */
abstract class AbstractCadesBESCommandInterface<K,V,W> implements
		CadesBESCommandInterface<K,V,W> {
	
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

	/**
	 * @see it.libersoft.firmapiu.CommandInterface#getSignToken()
	 */
	@Override
	public CRToken getSignToken() throws FirmapiuException {
		return this.signToken;
	}

	/**
	 * @see it.libersoft.firmapiu.CommandInterface#getVerifyCrToken()
	 */
	@Override
	public CRToken getVerifyCrToken() throws FirmapiuException {
		return this.verifyToken;
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
		//se non è stato definito il token per la verifica lancia un eccezione
		if(this.verifyToken==null)
			throw new FirmapiuException(CRT_TOKEN_NOTFOUND, new NullPointerException("verifyToken=null"));
		
		//Carica il keystore contenenti i certificati di root delle CA ritenuti affidabili per le operazioni di verifica
		this.verifyToken.loadKeyStore(null);
		
		//crea il resultset da restituire in risposta
		CMSReportResultInterface<K> resultInterface = this.getCMSReportResultInterface();
		
		//per ogni dato contenuto in data verifica che la firma sia corretta (l'implmementazione concreta di data 
		//deve essere una busta cades-bes attached)
		Iterator<K> dataItr=data.getDataSet().iterator();
		while(dataItr.hasNext()){
			//crea la busta crittografica dai dati di input
			K dataKey=dataItr.next();
			byte[] b=data.getArrayData(dataKey);
			try {
				CMSSignedData cmsSignedData = new CMSSignedData(b);
				//crea il verificatore per verificare la signedData ed effettua tutte le verifiche su tutti i firmatari
				CadesBESVerifier verifier = new CadesBESVerifier(cmsSignedData, this.verifyToken);
				resultInterface.put(dataKey, verifier);
			} catch (CMSException e) {
				FirmapiuException fe1 =new FirmapiuException(CONTENT_CADESBES_ENCODINGERROR_ATTACHED, e);
				resultInterface.putFirmapiuException(dataKey, fe1);
			}
		}

		return resultInterface;
	}
	
	/* (non-Javadoc)
	 * @see it.libersoft.firmapiu.cades.CadesBESCommandInterface#verifyP7S(it.libersoft.firmapiu.cades.P7SData)
	 */
	@Override
	public ResultInterface<K, CMSReport> verifyP7S(P7SData<K, W> data)
			throws FirmapiuException {		
		//se non è stato definito il token per la verifica lancia un eccezione
		if(this.verifyToken==null)
			throw new FirmapiuException(CRT_TOKEN_NOTFOUND, new NullPointerException("verifyToken=null"));
		
		//Carica il keystore contenenti i certificati di root delle CA ritenuti affidabili per le operazioni di verifica
		this.verifyToken.loadKeyStore(null);
		
		//crea il resultset da restituire in risposta
		CMSReportResultInterface<K> resultInterface = this.getCMSReportResultInterface();
		
		//per ogni dato contenuto in data verifica che la firma sia corretta (l'implmementazione concreta di data 
		//deve essere una busta cades-bes attached)
		Iterator<K> dataItr=data.getDataSet().iterator();
		while(dataItr.hasNext()){
			//crea la busta crittografica dai dati di input
			K dataKey=dataItr.next();
			byte[] b=data.getArrayData(dataKey);
			try {
				byte [] bValue=data.getContentArrayData(dataKey);
				CMSProcessable processable = new CMSProcessableByteArray(bValue);
				CMSSignedData cmsSignedData = new CMSSignedData(processable,b);
				//crea il verificatore per verificare la signedData ed effettua tutte le verifiche su tutti i firmatari
				CadesBESVerifier verifier = new CadesBESVerifier(cmsSignedData, this.verifyToken);
				resultInterface.put(dataKey, verifier);
			} catch (CMSException e) {
				FirmapiuException fe1 =new FirmapiuException(CONTENT_CADESBES_ENCODINGERROR_ATTACHED, e);
				resultInterface.putFirmapiuException(dataKey, fe1);
			}
		}
		return resultInterface;
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

	//crea una resultinterface per gestire i report dell'operazione di verifica
	abstract CMSReportResultInterface<K> getCMSReportResultInterface();
	
	//crea una result Interface da una CMSTypedData
	abstract CMSTypedDataResultInterface<K, V> getCMSTypedDataResultInterface();
}
