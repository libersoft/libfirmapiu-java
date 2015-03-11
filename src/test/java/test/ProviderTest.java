/**
 * 
 */
package test;

import java.security.Provider;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Semplice classe di test per vedere cosa succede se si tenta di aggiungere due istanze diverse dello stesso
 * provider alla lista dei providers
 * 
 * @author dellanna
 *
 */
public class ProviderTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Provider bcProv = new BouncyCastleProvider();
		Security.addProvider(bcProv);
		
		//stampa a video i provider
		System.out.println("Lista dei providers primo tentativo:****");
		
		Provider[] providers = Security.getProviders();

		for (int i = 0; i != providers.length; i++) {
			System.out.println("Name: " + providers[i].getName() + " "
					+ providers[i].getInfo() + " Version: "
					+ providers[i].getVersion());
		}
		
		Provider bcProv2 = new BouncyCastleProvider();
		Security.addProvider(bcProv2);
		
		providers = Security.getProviders();

		System.out.println();
		System.out.println("Lista dei providers secondo tentativo:****");
		for (int i = 0; i != providers.length; i++) {
			System.out.println("Name: " + providers[i].getName() + " "
					+ providers[i].getInfo() + " Version: "
					+ providers[i].getVersion());
		}

		System.out.println();
		System.out.println("Contronto providers che non sono stati aggiunti alla lista dei providers: "+bcProv.equals(bcProv2));
		
		System.out.println();
		Provider p1=Security.getProvider(bcProv.getName());
		Provider p2=Security.getProvider(bcProv2.getName());
		System.out.println("Contronto providers che sono stati aggiunti alla lista dei providers: "+p1.equals(p2));
		
	}

}
