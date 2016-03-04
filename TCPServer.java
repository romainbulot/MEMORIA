import java.io.*; 
import java.net.*; 
import java.util.Scanner;

class TCPServer {    
	public static void main(String argv[]) throws Exception       { 
		
		/*Couleur terminal */
		String ANSI_BLUE = "\u001B[34m";
		String ANSI_GREEN = "\u001B[35m";
		String ANSI_RESET = "\u001B[0m";


		/* Déclaration de la variable de texte reçu du client */
		String texteDuClient;   

		/* Déclaration de la variable d'adresse */
		InetAddress  monAdresse = InetAddress.getLocalHost();  

		/* Décalaration de la variable de texte renvoyé au client */
		String texteDuClientEnMajuscules;     

		/* Déclaration de la variable de saisie du port */
		int monPort;  

		/* Adresse du client */
		String adresseClient;

		/* Demande de la saisie du port */
		System.out.println("\n----------------------------------------------------");
		System.out.print("\n Veuillez renseigner le port souhaité: ");



		/* Enregistrement du port saisi */
		Scanner in = new Scanner(System.in);
		monPort = in.nextInt();

		/* Demande de la saisie du port à utilisé */ 
		ServerSocket socketServeur = new ServerSocket(monPort);

		System.out.print("\n ... Serveur en attente de requête ... \n");
		while(true)          {   

			Socket connexionSocket = socketServeur.accept(); 
			
			System.out.print("\n ... Serveur connecté ... \n \n ");

			/* Récupération du texte du client */ 
			BufferedReader recuDuClient = new BufferedReader(new InputStreamReader(connexionSocket.getInputStream()));     

			/* Renvoi du texte du client */ 
			DataOutputStream envoyeAuClient = new DataOutputStream(connexionSocket.getOutputStream());

			/* Lecture du texte du client */    
			texteDuClient = recuDuClient.readLine(); 

			/* Affichage du texte du client */              
			System.out.println( ANSI_GREEN  +  "\n***********************************\nMessage reçu du client : " + texteDuClient + "\n***********************************\n "+ ANSI_RESET) ; 

			System.out.print("\n ... Traitement ... \n \n ");
			/* Transformation du texte du client en majuscules */    
			texteDuClientEnMajuscules = texteDuClient.toUpperCase() + '\n';  

			/* Affichage du texte renvoyé du client */         
			System.out.println(ANSI_BLUE + "\n***********************************\nMessage envoyé au client: " + texteDuClientEnMajuscules +"***********************************" + ANSI_RESET ); 
			System.out.println("\nDepuis l'adresse : " + monAdresse.getHostAddress());
			System.out.println("Sur le port : " + monPort);
			System.out.println("\n----------------------------------------------------");

			/* Renvoi du texte en majuscules */    
			envoyeAuClient.writeBytes(texteDuClientEnMajuscules);   
		}   
	}
} 