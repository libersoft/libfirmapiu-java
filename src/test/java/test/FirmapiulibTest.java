/**
 * 
 */
package test;

import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import it.libersoft.firmapiu.CRToken;
import it.libersoft.firmapiu.DataFilePath;
import it.libersoft.firmapiu.GenericArgument;
import it.libersoft.firmapiu.MasterFactoryBuilder;
import it.libersoft.firmapiu.cades.CadesBESCommandInterface;
import it.libersoft.firmapiu.crtoken.KeyStoreToken;
import it.libersoft.firmapiu.exception.FirmapiuException;
import static it.libersoft.firmapiu.consts.FactoryConsts.*;
import static it.libersoft.firmapiu.consts.FirmapiuRecordConstants.*;

/**
 * Test NON-junit che testa le funzionalità basilari messe a disposizione dalla libreria firmapiu
 *
 * @author dellanna
 *
 */
final class FirmapiulibTest {

	/**
	 * @param args
	 * @throws FirmapiuException 
	 */
	public static void main(String[] args) throws FirmapiuException {
		// TODO Auto-generated method stub
		
		//crea il keystore contenente le trust anchor delle CA utilizzate per verificare l'affidabilità del certificato del firmatario
		KeyStoreToken token=(KeyStoreToken)MasterFactoryBuilder.getFactory(KEYSTORETOKENFACTORY).getToken(TSLXMLKEYSTORE);
		
		try {
			token.createKeyStore();
			System.out.println("Il keystore è stato creato");
		} catch (Exception e) {
			System.err.println("Il keystore non è stato creato, probabilmente esiste già");
			e.printStackTrace();
		}
		
		//Crea una cadesbescommandinterface per creare o verificare file P7M
		CadesBESCommandInterface commandInterface=MasterFactoryBuilder.getFactory(CADESBESFACTORY).getCadesBESCommandInterface(P7MFILE);
		
		//Testa le funzionalità di verifica messe a disposizione dall'interfaccia dei comandi
		//crea la struttura dati per passare i dati da verificare come parametro
		DataFilePath data = (DataFilePath)MasterFactoryBuilder.getFactory(DATAFACTORY).getData(DATAFILEPATH);
		GenericArgument option = (GenericArgument)MasterFactoryBuilder.getFactory(ARGUMENTFACTORY).getArgument(GENERICARGUMENT);
		//inizializza la struttura dati con i percorsi dei file p7m da verificare
		System.out.println("File da verificare:");
		for(String arg: args){
			data.setData(arg);
			System.out.println(arg);
		}

		System.out.println();
		System.out.println();
		
		//esegue la verifica
		Map<?,?> result=commandInterface.verify(data, option);
		
		System.out.println("Esito della verifica:");
		System.out.println();
		//controlla la verifica per ogni file
		Iterator<String> itr=data.getDataSet().iterator();
		while(itr.hasNext()){
			String dataPath = itr.next();
			System.out.println();
			System.out.println("File ->"+dataPath);
			System.out.println("************************************");			
			//verifica la firma di tutti i firmatari per ogni file
			Object signers =result.get(dataPath);
			List<?> signersList= (List<?>)signers;
			Iterator<?> sigItr = signersList.iterator();
			while(sigItr.hasNext()){
				System.out.println("Firmatario:");
				System.out.println("-------------------------------------------------------------");
				Object signerRecord = sigItr.next();
				printSignerRecord(signerRecord);
				System.out.println("-------------------------------------------------------------");
			}
			System.out.println("************************************");
			System.out.println();
		}	
	}

	//stampa a video i records contenenti le informazioni di verifica per ogni firmatario
	private static void printSignerRecord(Object signerRecord){
		Map<String,Object> record = (Map<String,Object>)signerRecord;
		X509Certificate cert1 =null;
		//certificato del firmatario
		if(record.containsKey(SIGNERCERT))
		{
			System.out.println("***Certificato del firmatario");
			Object obj=record.get(SIGNERCERT);
			if(obj instanceof X509Certificate)
			{
				cert1 = (X509Certificate)obj;
				System.out.println("Issuer-->"+cert1.getIssuerDN().toString());
				System.out.println("Subject-->"+cert1.getSubjectDN().toString());
			}
			else if (obj instanceof FirmapiuException){
				FirmapiuException e = (FirmapiuException)obj;
				e.printStackTrace();
			}
			else
				System.out.println("???");
		}
		else
			System.out.println("***Non ho trovato il certificato del firmatario");
		//verifica la firma del firmatario
		if(record.containsKey(OKSIGNED)){
			System.out.println("***Verifica della firma per il firmatario");
			Object obj = record.get(OKSIGNED);
			if(obj instanceof Boolean){
				System.out.println("Esito:"+obj);
			}
			else if (obj instanceof FirmapiuException){
				FirmapiuException e = (FirmapiuException)obj;
				e.printStackTrace();
			}
			else
				System.out.println("???");
		}
		else
			System.out.println("***Non ho trovato l'esito della verifica della firma per il firmatario");
		//verifica che la firma del firmatario sia legale
		if(record.containsKey(LEGALLYSIGNED)){
			System.out.println("***Verifica che la firma sia conforme alla DELIBERAZIONE ministeriale del N . 45 DEL 21 MAGGIO 2009");
			Object obj = record.get(LEGALLYSIGNED);
			if(obj instanceof Boolean){
				System.out.println("Esito:"+obj);
			}
			else if (obj instanceof FirmapiuException){
				FirmapiuException e = (FirmapiuException)obj;
				e.printStackTrace();
			}
			else
				System.out.println("???");
		}
		else
			System.out.println("***Non ho trovato l'esito della verifica della legalità della firma per il firmatario");
		//verifica che il certificato del firmatario sia affidabile
		if(record.containsKey(TRUSTEDSIGNER)){
			System.out.println("***Verifica che il certificato del firmatario sia affidabile");
			Object obj = record.get(TRUSTEDSIGNER);
			if(obj instanceof Boolean){
				System.out.println("Esito:"+obj);
			}
			else if (obj instanceof FirmapiuException){
				FirmapiuException e = (FirmapiuException)obj;
				e.printStackTrace();
			}
			else
				System.out.println("???");
		}
		else
			System.out.println("***Non ho trovato l'esito della verifica dell'affidabilità del certificato del firmatario");
		//catena dei certificati del firmatario
		if(record.containsKey(CERTCHAIN))
		{
			System.out.println("***Catena dei certificati associata al certificato del firmatario");
			Object obj=record.get(CERTCHAIN);
			if(obj instanceof List<?>)
			{
				List<?> l1 = (List<?>)obj;
				Iterator<?> it2= l1.iterator();
				int i=0;
				while(it2.hasNext()){
					X509Certificate xcert=(X509Certificate)it2.next();
					System.out.println("Certificato ["+i+"]");
					System.out.println("Issuer-->"+xcert.getIssuerDN().toString());
					System.out.println("Subject-->"+xcert.getSubjectDN().toString());
					i++;
				}
			}
			else if (obj instanceof FirmapiuException){
				FirmapiuException e = (FirmapiuException)obj;
				e.printStackTrace();
			}
			else
				System.out.println("???");
		}
		else
			System.out.println("***Non ho trovato la catena dei certificati associata al certificato del firmatario");
		//"trust anchor" del certificato del firmatario
		if(record.containsKey(TRUSTANCHOR))
		{
			System.out.println("***Trust Anchor: Certificato della CA associata al certificato del firmatario");
			Object obj=record.get(TRUSTANCHOR);
			if(obj instanceof X509Certificate)
			{
				X509Certificate s1 = (X509Certificate)obj;
				System.out.println("Issuer-->"+s1.getIssuerDN().toString());
				System.out.println("Subject-->"+s1.getSubjectDN().toString());
			}
			else if (obj instanceof FirmapiuException){
				FirmapiuException e = (FirmapiuException)obj;
				e.printStackTrace();
			}
			else
				System.out.println("???");
		}
		else
			System.out.println("***Trust Anchor: Non ho trovato il certificato della CA associata al certificato del firmatario");
		
		//controlla se il certificato è stato revocato
		if(record.containsKey(SIGNERCERTSTATUS))
		{
			System.out.println("***Signer Certificate Status: Stato di revoca del certificato del firmatario");
			Object obj=record.get(SIGNERCERTSTATUS);
			if(obj instanceof String)
			{
				System.out.println("\tStato del certificato del firmatario: "+(String)obj);
			}
			else if (obj instanceof FirmapiuException){
				FirmapiuException e = (FirmapiuException)obj;
				e.printStackTrace();
			}
			else
				System.out.println("???");
		}
		else
			System.out.println("***Non ho trovato lo status di revoca associato al certificato del firmatario");

		//controlla se il certificato era valido al tempo della firma del firmatario
		if(record.containsKey(SIGNERCERTREVOKED))
		{
			System.out.println("***Signer Certificate Revoked: Stato di revoca del certificato del firmatario nel momento in cui ha firmato il file");
			Object obj=record.get(SIGNERCERTREVOKED);
			if(obj instanceof Boolean)
			{
				System.out.println("\tStato del certificato del firmatario al momento della firma: ");
				Boolean revoked = (Boolean)obj;
				if(revoked){
					System.out.println("\tIl certificato era revocato al momento della firma: ");
				}
				else {
					System.out.println("\tIl certificato NON era revocato al momento della firma: ");
				}
			}
			else if (obj instanceof FirmapiuException){
				FirmapiuException e = (FirmapiuException)obj;
				e.printStackTrace();
			}
			else
				System.out.println("???");
		}
		else
			System.out.println("***Non ho trovato lo status di revoca associato al certificato del firmatario");
	}
}
