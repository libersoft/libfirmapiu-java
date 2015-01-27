import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.security.Provider;
import java.security.Security;
import java.util.Locale;
import java.util.ResourceBundle;


public class P2 {

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		/* Provider[]	providers = Security.getProviders();
	        
	        for (int i = 0; i != providers.length; i++)
	        {
	            System.out.println("Name: " + providers[i].getName() +" "+providers[i].getInfo() +  " Version: " + providers[i].getVersion());
	        }*/
		//se percorso inizia con tilde parsa la stringa fino a trovare /
		System.setProperty("java.library.path", System.getProperty("java.library.path")+":/usr/lib/jni");
		
		System.out.println(System.getProperty("java.library.path"));
		
		System.out.println((long)(Math.random()*Long.MAX_VALUE));
		
		ResourceBundle rb = ResourceBundle.getBundle("locale",Locale.US);
		
		System.out.println(rb.getString("try"));
		
		
		System.getProperties().list(System.out);
		
		System.out.println(Locale.getDefault().toString());
		
		String filepath=args[0];
		if(filepath.startsWith("~"))
		{
			String[] user=filepath.split("/",2);
			System.out.println(user[0]);
			System.out.println(user[1]);
		
		    try {
		        String command = "ls -d " + user[0];
		        Process shellExec = Runtime.getRuntime().exec(
		            new String[]{"bash", "-c", command});

		        BufferedReader reader = new BufferedReader(
		            new InputStreamReader(shellExec.getInputStream()));
		        String expandedPath = reader.readLine();

		        // Only return a new value if expansion worked.
		        // We're reading from stdin. If there was a problem, it was written
		        // to stderr and our result will be null.
		        if (expandedPath != null) {
		            filepath = expandedPath+"/"+user[1];
		        }
		    } catch (java.io.IOException ex) {
		        // Just consider it unexpandable and return original path.
		    }
		}
		System.out.println(filepath);
		File file=new File(filepath).getCanonicalFile();
		System.out.println("\t"+file.getCanonicalPath());
		
		System.out.println("Lista di file:");
		File[] filelist= file.getParentFile().listFiles();
		for(int i=0;i<filelist.length;i++)
			System.out.println(filelist[i].getCanonicalPath());
		
		//System.getProperties().list(System.out);
		//~/firmapiu/../firmapiuOLD/../Documenti/Wrox - Beginning Cryptography with Java
	}

}
