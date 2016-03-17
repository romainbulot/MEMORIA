import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*; 
import java.net.*; 
import java.util.Scanner;

public class Server implements Runnable {
 /* Déclaration de la variable de texte reçu du client */
 String texteDuClient;
 /* Décalaration de la variable de texte renvoyé au client */
 String texteDuClientEnMajuscules;  
 /*Couleur terminal - marche uniquement sous noyeau UNIX */
 String ANSI_BLUE = "\u001B[34m";
 String ANSI_GREEN = "\u001B[35m";
 String ANSI_RESET = "\u001B[0m";
 Socket socketCo;
 Server(Socket socketCo) {
  this.socketCo = socketCo;
}

public static void main(String args[]) 
throws Exception {

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

  ServerSocket ConnexionSocket = new ServerSocket(monPort);
  System.out.println("Serveur en attente de requête");
  while (true) {
   Socket sock = ConnexionSocket.accept();
   System.out.println("\n --- Connecté --- \n ");
   new Thread(new Server(sock)).start();
 }
}
public void run() {
  try {
    /* Récupération du texte du client */ 
    BufferedReader recuDuClient = new BufferedReader(new InputStreamReader(socketCo.getInputStream()));    
    /* Renvoi du texte du client */ 
    DataOutputStream envoyeAuClient = new DataOutputStream(socketCo.getOutputStream());
    /* Lecture du texte du client */    
    texteDuClient = recuDuClient.readLine(); 

    /* Affichage du texte du client */              
    System.out.println( ANSI_GREEN  +  "\n***********************************\nMessage reçu du client : " + texteDuClient + "\n***********************************\n "+ ANSI_RESET) ; 

    System.out.print("\n ... Traitement ... \n \n ");
    /* Transformation du texte du client en majuscules */    
    texteDuClientEnMajuscules = texteDuClient.toUpperCase() + '\n';  

    /* Affichage du texte renvoyé du client */         
    System.out.println(ANSI_BLUE + "\n***********************************\nMessage envoyé au client: " + texteDuClientEnMajuscules +"***********************************" + ANSI_RESET ); 
    System.out.println("\nVers l'adresse : " + socketCo.getInetAddress());
     System.out.println("\nSur les ports : " + socketCo.getPort() + "   /   local : " + socketCo.getLocalPort());
    System.out.println("\n----------------------------------------------------");
    /* Renvoi du texte en majuscules */    
    envoyeAuClient.writeBytes(texteDuClientEnMajuscules);   
  }
  catch (IOException e) {
   System.out.println(e);
 }
}
}
