import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;

import org.bouncycastle.cms.CMSException;

import firmapiu.CommandProxyInterface;


class P3 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ResourceBundle rb = ResourceBundle.getBundle("firmapiu.lang.locale",Locale.getDefault());
		//Inizializza il CommandProxyInterface per interfacciarsi ai comandi
		CommandProxyInterface cmdPInterface =new CommandProxyInterface(rb);
		//verifica i file
		Set<String> commandArgs= new TreeSet<String>();
		commandArgs.add(args[0]);
		Map<String,?> result= cmdPInterface.verify(commandArgs, null);
		//stampa a video l'esito della verifica dei file passati come paramentro
		System.out.println();
		System.out.println("Esito dell'operazione richiesta:");
		System.out.println();
		Iterator<String> itr= result.keySet().iterator();
		while(itr.hasNext()){
			String key=itr.next();
			System.out.println(key+" :");
			Object value=result.get(key);
			if(value instanceof Boolean){
				if(((Boolean)value).booleanValue())
					System.out.println("\tTRUE! Il file è stato firmato correttamente dal firmatario!");
				else
					System.out.println("\tFALSE! Il file e il valore della firma del firmatario non corrispondono! ");
			}else if(value instanceof FileNotFoundException){
				System.err.println("\tErrore: File non trovato!");
				System.err.println("\t"+((FileNotFoundException)value).getMessage());
			}else if(value instanceof IOException){
				System.err.println("\tErrore: Errore di I/O!");
				System.err.println("\t"+((IOException)value).getMessage());
			}else if(value instanceof CMSException){
				System.err.println("\tErrore: Errore in fase di validazione!");
				System.err.println("\t"+((CMSException)value).getMessage());
			}
		}//fine while
	}

}
