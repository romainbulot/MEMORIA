import java.io.*; 
import java.net.*; 
import java.util.Scanner;

class TCPClient {  
	public static void main(String argv[]) throws Exception  { 
		
		/*Couleur terminal */
		String ANSI_BLUE = "\u001B[34m";
		String ANSI_GREEN = "\u001B[35m";
		String ANSI_RESET = "\u001B[0m";

		/* Déclaration de la variable de saisie du texte par le Client */
		String monTexte;  

		/* Déclaration de la variable d'adresse */
		InetAddress  monAdresse = InetAddress.getLocalHost();  

		/* Déclaration de la variable de saisie du port */
		int monPort;  

		/* Déclaration de la variable du texte retourné par le Serveur*/
		String texteRetourneParServeur; 

		
		/* Demande de la saisie du port */
		System.out.println("\n----------------------------------------------------");
		System.out.print("\nVeuillez renseigner le port souhaité : ");

		/* Enregistrement du port saisi */
		Scanner in = new Scanner(System.in);
		monPort = in.nextInt();
		while(true)          { 
			/* Demande de la saisie du mot */
			System.out.print("\nVeuillez renseigner le texte à capitaliser : ");

			/* Enregistrement du texte saisi */
			BufferedReader saisieTexte = new BufferedReader( new InputStreamReader(System.in));  
			monTexte = saisieTexte.readLine();   


			/* Déclaration et lancement d'un Socket Client */
			Socket clientSocket = new Socket(monAdresse, monPort); 
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());  
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));   

			outToServer.writeBytes(monTexte + '\n'); 
			System.out.println(ANSI_BLUE +"\n***********************************\nMessage envoyé au serveur :" + monTexte + "\n***********************************\n" + ANSI_RESET );
			System.out.println("Depuis l'adresse : "  + monAdresse.getHostAddress() );
			System.out.println("Sur le port : " + monPort);

			texteRetourneParServeur = inFromServer.readLine(); 

			System.out.println( ANSI_GREEN  + "\n***********************************\nMessage reçu du serveur : " + texteRetourneParServeur +"\n***********************************\n" + ANSI_RESET ) ; 
			
			clientSocket.close(); 
		}
	} 
}


