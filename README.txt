L'applicazione Firma Più CLI è un applicazione a linea di comando utilizzata per la firma e la verifica di firma di 
uno o più file passati come argomento del comando.
I file sono firmati tramite chiavi private e certificati presenti su una smart card con microchip crittografico.




Contenuto dell'applicazione Firma Più:
	
	bin/ 			- Contiene i file "binari" dell'applicazione
	config/ 		- Contiene il file di configurazione utilizzato dal Provider Java per accedere alla smart card.
	lib/			- Contiene una copia locale delle librerie Bouncy Castles
	README.txt		- 
	firmapiu-cli 	- Script di esecuzione dell'applicazione Firma Più.
	



Configurazione dello script firmapiu-cli:

	Lo script contiene la variabile $BCPATH che definisce il path utilizzato da java per 
	caricare le librerie Bouncy Castles.
	Se la variabile non è definita, oppure le librerie non vengono trovate presso il path 
	passato come parametro, lo script carica le librerie presenti nella directory lib/




Uso del comando firmapiu-cli:

	firmapiu-cli COMANDO FILE... [[OPZIONE valore_opzione]...]
	
	dove:

	COMANDO:
		Argomento obbligatorio. 
		Comandi messi a dispozione da firmapiu-cli.
	
		--sign FILE...
			Firma una lista distinta di file che seguono l'argomento "--sign"
		
		--verify FILE...
			Verifica la validità dei firmatari di una lista di file .p7m che seguono l'argomento "--verify". 
			I file, per essere correttamente validati, devono essere espressi nel formato 
			definito da Cryptographic Message Syntax (pkcs#7)
	

	FILE:
		Argomento obbligatorio per "--sign" e "--verify".
		Rappresenta una lista contenente i path di uno o più file che devono essere firmati o verificati da firmapiu-cli
		Se i path dei file contengono degli spazi all'interno, l'intero path deve essere compreso tra " "
		esempio:
			firmapiu-cli --sign "/home/user/Primo esempio di file.txt" /home/user/Scrivania/file.pdf
			firmapiu-cli --verify /home/user/Documenti/first.pdf.p7m /home/user/Documenti/second.pdf "/tmp/File Temporanei/tmp.txt.p7m"
		
		
	OPZIONE:
		Argomento opzionale.
		Opzioni messe a disposizione dei comandi di firmapiu-cli.
		Se le opzioni non sono definite da linea di comando, vengono richieste in fase di esecuzione.
	
		-pin valore_opzione
			pin della carta utilizzata per firmare i file
		
		-alias valore_opzione
			Uno degli alias utente presenti nel keystore della carta utilizzata per firmare i file