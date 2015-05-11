/**
 * 
 */
package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.MessageDigest;

import it.libersoft.firmapiu.ResultInterface;
import it.libersoft.firmapiu.cades.CadesBESFactory;
import it.libersoft.firmapiu.cades.P7ByteCommandInterface;
import it.libersoft.firmapiu.crtoken.DefaultTokenFactory;
import it.libersoft.firmapiu.crtoken.PKCS11Token;
import it.libersoft.firmapiu.crtoken.TokenFactoryBuilder;
import it.libersoft.firmapiu.data.DataByteArray;
import it.libersoft.firmapiu.data.DataFactoryBuilder;
import static it.libersoft.firmapiu.consts.FactoryConsts.*;
import static it.libersoft.firmapiu.consts.FactoryPropConsts.*;
import static it.libersoft.firmapiu.consts.ArgumentConsts.*;

/**
 * Test NON-junit che testa le funzionalità basilari di firma messe a disposizione dalla libreria firmapiu
 * 
 * @author dellanna
 *
 */
public class FirmapiuLibSignTest {

	/**
	 * 
	 */
	public FirmapiuLibSignTest() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		//simula il calcolo e la ricezione di un digest da un server
		
		//========================== SERVER ============================
		
		//calcola il digest del file da firmare
		System.out.println("File da firmare: "+args[0]);
		String digestAlgorithm = "SHA-256";
		MessageDigest sha = null;

		sha = MessageDigest.getInstance(digestAlgorithm);

		byte[] digestedFile = null;

		FileInputStream in = new FileInputStream(new File(args[0]));
		byte[] b=new byte[in.available()];
		in.read(b);	
		digestedFile = sha.digest(b);
		System.out.println("Digest: "+sha.toString());
		for(int i=0;i<digestedFile.length;i++){
			String byteStr=Integer.toHexString((int)digestedFile[i]);
			if(byteStr.length()>2)
				byteStr=byteStr.substring(byteStr.length()-2);
			byteStr=byteStr.toUpperCase();
			System.out.print(byteStr+":");
		}
		
		System.out.println();
		
		//-------------------------------> Invia digestedFile al client
		
		
		
		//===================================== CLIENT ====================================
		
		//Il client riceve il digest del file da firmare <-----------------------------------
		
		//Inizializza la factory per la creazione di una busta crittografica in formato Cades-bes
		CadesBESFactory cBesFactory = CadesBESFactory.getFactory();
		
		//setta le proprietà della Cades-Bes Factory: bisogna dire di usare it.libersoft.firmapiu.util.NoSHA256DigestCalculatorProvider
		//come digest calculator provider in modo tale da far capire alle API di Bouncy Castle che devono generare 
		//la busta crittografica da un digest pre-computato
		cBesFactory.setProperty(DIGEST_CALCULATOR_PROVIDER, "it.libersoft.firmapiu.util.NoSHA256DigestCalculatorProvider");
		
		//carica il pkcs11 token
		DefaultTokenFactory defaultTokenFactory=TokenFactoryBuilder.getFactory(PKCS11TOKENFACTORY);
		//setta esplicitamente il file di proprietà (contenente la lista dei driver delle smartcards) che deve essere caricato
		//il codice consente di caricare il file di proprietà anche se si trova all'interno di un jar.
		//se il file si trova ad esempio all'interno di un jar, il suo "namespace java" 
		//deve essere settato nella proprietà CRT_TOKEN_PKCS11_LIBRARYPATH come:
		// pkg1.pkg2.pkg3.file.properties ---> "pkg1/pkg2/pkg3/file.properties"
		defaultTokenFactory.setProperty(CRT_TOKEN_PKCS11_LIBRARYPATH, "pkg/pkcs11driver.properties");
		PKCS11Token pkcs11Token= defaultTokenFactory.getPKCS11Token(CRTSMARTCARD);
		
		//inizializza l'interfaccia di comando utilizzata per firmare il digest-precomputato
		P7ByteCommandInterface p7byteInterface = cBesFactory.getP7ByteCommandInterface(pkcs11Token, null);
		
		//inizializza la struttura dati utilizzata per passare i dati da firmare all'interfaccia di comand come array di byte
		DataByteArray data=DataFactoryBuilder.getFactory(DATABYTEARRAYFACTORY).getDataByteArray();
		data.setData(digestedFile);
		//setta gli argomenti associati ai dati da passare al comando, in particolare bisogna dire che la busta generata deve essere
		//"detached" ossia il contenuto (il digest del file) non deve essere contenuto nella busta cades-bes ossia deve generare una 
		//rappresentazione in array di byte di un p7s
		data.setArgument(DETACHED, "true");
		
		//si logga nel token crittografico utilizzato
		pkcs11Token.login(args[1].toCharArray());
		//firma
		ResultInterface<byte[], byte[]> result=p7byteInterface.sign(data);
		//fa il logout del token
		pkcs11Token.logout();
		
		//recupera il risultato associato al dato che è stato firmato
		
		byte[] p7sStream=result.getResult(digestedFile);
		
		//----------------------------------> Invia la rappresentazione del p7s in byte al server
		
		//========================== SERVER ============================
		
		//Il server riceve il p7s
		
		File fileout = new File("/home/andy/prova.p7s");
		FileOutputStream fileOutStream= new FileOutputStream(fileout);
		fileOutStream.write(p7sStream);
		fileOutStream.flush();
		fileOutStream.close();
	}	
}
