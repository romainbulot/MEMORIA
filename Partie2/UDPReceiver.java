
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Cette classe permet la reception d'un paquet UDP sur le port de reception
 * UDP/DNS. Elle analyse le paquet et extrait le hostname
 * 
 * Il s'agit d'un Thread qui ecoute en permanance pour ne pas affecter le
 * deroulement du programme
 * 
 * @author Max
 *
 */

public class UDPReceiver extends Thread {
	/**
	 * Les champs d'un Packet UDP 
	 * --------------------------
	 * En-tete (12 octects) 
	 * Question : l'adresse demande 
	 * Reponse : l'adresse IP
	 * Autorite :
	 * info sur le serveur d'autorite 
	 * Additionnel : information supplementaire
	 */

	/**
	 * Definition de l'En-tete d'un Packet UDP
	 * --------------------------------------- 
	 * Identifiant Parametres 
	 * QDcount
	 * Ancount
	 * NScount 
	 * ARcount
	 * 
	 * L'identifiant est un entier permettant d'identifier la requete. 
	 * parametres contient les champs suivant : 
	 * 		QR (1 bit) : indique si le message est une question (0) ou une reponse (1). 
	 * 		OPCODE (4 bits) : type de la requete (0000 pour une requete simple). 
	 * 		AA (1 bit) : le serveur qui a fourni la reponse a-t-il autorite sur le domaine? 
	 * 		TC (1 bit) : indique si le message est tronque.
	 *		RD (1 bit) : demande d'une requete recursive. 
	 * 		RA (1 bit) : indique que le serveur peut faire une demande recursive. 
	 *		UNUSED, AD, CD (1 bit chacun) : non utilises. 
	 * 		RCODE (4 bits) : code de retour.
	 *                       0 : OK, 1 : erreur sur le format de la requete,
	 *                       2: probleme du serveur, 3 : nom de domaine non trouve (valide seulement si AA), 
	 *                       4 : requete non supportee, 5 : le serveur refuse de repondre (raisons de sï¿½ecurite ou autres).
	 * QDCount : nombre de questions. 
	 * ANCount, NSCount, ARCount : nombre dï¿½entrees dans les champs ï¿½Reponseï¿½, Autorite,  Additionnel.
	 */

	protected final static int BUF_SIZE = 1024;
	protected String SERVER_DNS = null;//serveur de redirection (ip)
	protected int portRedirect = 53; // port  de redirection (par defaut)
	protected int port; // port de rï¿½ception
	private String adrIP = null; //bind ip d'ecoute
	private String DomainName = "none";
	private String DNSFile = null;
	private boolean RedirectionSeulement = false;

	private class ClientInfo { //quick container
		public String client_ip = null;
		public int client_port = 0;
	};
	private HashMap<Integer, ClientInfo> Clients = new HashMap<>();

	private boolean stop = false;


	public UDPReceiver() {
	}

	public UDPReceiver(String SERVER_DNS, int Port) {
		this.SERVER_DNS = SERVER_DNS;
		this.port = Port;
	}


	public void setport(int p) {
		this.port = p;
	}

	public void setRedirectionSeulement(boolean b) {
		this.RedirectionSeulement = b;
	}

	public String gethostNameFromPacket() {
		return DomainName;
	}

	public String getAdrIP() {
		return adrIP;
	}

	private void setAdrIP(String ip) {
		adrIP = ip;
	}

	public String getSERVER_DNS() {
		return SERVER_DNS;
	}

	public void setSERVER_DNS(String server_dns) {
		this.SERVER_DNS = server_dns;
	}

	public void setDNSFile(String filename) {
		DNSFile = filename;
	}

	public void run() {
		try {
			DatagramSocket serveur = new DatagramSocket(this.port); // *Creation d'un socket UDP
			UDPSender sender = new UDPSender(SERVER_DNS, portRedirect, serveur);
			
			// *Boucle infinie de reception
			while (!this.stop) {
				byte[] buff = new byte[0xFF];
				DatagramPacket paquetRecu = new DatagramPacket(buff,buff.length);
				System.out.println("Serveur DNS  " + serveur.getLocalAddress() + "  en attente sur le port: "+ serveur.getLocalPort());

				// *Reception d'un paquet UDP via le socket
				serveur.receive(paquetRecu);

				System.out.println("paquet recu du  " + paquetRecu.getAddress() + "  du port: " + paquetRecu.getPort());

				// *Creation d'un DataInputStream ou ByteArrayInputStream pour
				// manipuler les bytes du paquet

				ByteArrayInputStream TabInputStream = new ByteArrayInputStream (paquetRecu.getData());

				System.out.println(buff.toString());

				byte temp = 0;
				DomainName = "";

//				int identifiant = TabInputStream.read() * 256;
//				identifiant = identifiant + TabInputStream.read();
//				
//				System.out.println("Identifiant : " + identifiant);
				
				TabInputStream.skip(2);
				
				// Pour avoir la valeur QR
				temp = (byte) TabInputStream.read();
				
				TabInputStream.skip(1);
				
				// Skip le nombre de question
				//TabInputStream.skip(2);
				
				// skip le nombre de réponse
				// TabInputStream.skip(2);
				
				int q, r;
				q = TabInputStream.read() * 256;
				q = q + TabInputStream.read();
				
				System.out.println("Question : " + q);
				
				r = TabInputStream.read() * 256;
				r = r + TabInputStream.read();
				
				System.out.println("Réponse : " + r);
				
				// Skip le nombre d'autorité et d'autre
				TabInputStream.skip(4);
				
				// ****** Dans le cas d'un paquet requete *****
				
				if(temp > 0)
				{
					// *Lecture du Query Domain name, a partir du 13 byte

					//System.out.println("Question reçu");

					do
					{
						temp = (byte) TabInputStream.read();
						DomainName = DomainName + '.';

						for(int i = temp; i > 0; i--)
						{
							temp = (byte) TabInputStream.read();
							DomainName = DomainName + ((char) temp);
						}
					}while(temp != 0);
					
					//System.out.println(DomainName);
					
					// *Sauvegarde du Query Domain name

					// *Sauvegarde de l'adresse, du port et de l'identifiant de la requete
					
					port = paquetRecu.getPort();
					adrIP = paquetRecu.getAddress().toString();
					
//					ClientInfo te = new ClientInfo();
//					
//					te.client_ip = adrIP;
//					te.client_port = port;
//					
//					Clients.put(identifiant, te);

					if(RedirectionSeulement) // *Si le mode est redirection seulement
					{
						// *Rediriger le paquet vers le serveur DNS
						System.out.println("redir");
						sender = new UDPSender(SERVER_DNS, portRedirect, serveur);
						sender.SendPacketNow(paquetRecu);
						
					}
					// *Sinon
					else
					{
						// *Rechercher l'adresse IP associe au Query Domain name
						// dans le fichier de correspondance de ce serveur
						List<String> queryResult = new QueryFinder (DNSFile, DomainName).StartResearch(DomainName);
						// *Si la correspondance n'est pas trouvee
						// *Rediriger le paquet vers le serveur DNS
						
						//System.out.println(queryResult);
						
						if(queryResult.isEmpty())
						{
							sender = new UDPSender(SERVER_DNS, portRedirect, serveur);
							sender.SendPacketNow(paquetRecu);
						}
						// *Sinon
						else
						{
						// *Creer le paquet de reponse a l'aide du UDPAnswerPaquetCreator
							buff = UDPAnswerPacketCreator.getInstance().CreateAnswerPacket(buff, queryResult);
						// *Placer ce paquet dans le socket
							sender = new UDPSender("127.0.0.1", port, serveur);
						// *Envoyer le paquet
							
							//System.out.println(buff);
							
							paquetRecu = new DatagramPacket(buff, buff.length);
							sender.SendPacketNow(paquetRecu);
						}
					}
				}
				else
				{
					//System.out.println("Réponse reçu");
					
					// ****** Dans le cas d'un paquet reponse *****
					// *Lecture du Query Domain name, a partir du 13 byte

					do
					{
						temp = (byte) TabInputStream.read();
						DomainName = DomainName + '.';

						for(int i = temp; i > 0; i--)
						{
							temp = (byte) TabInputStream.read();
							DomainName = DomainName + ((char) temp);
						}
					}while(temp != 0);
					
					//System.out.println(DomainName);
					
					// *Passe par dessus Type et Class
					
					// *Passe par dessus les premiers champs du ressource record
					// pour arriver au ressource data qui contient l'adresse IP associe
					//  au hostname (dans le fond saut de 16 bytes)
					
					TabInputStream.skip(16);
					
					// *Capture de ou des adresse(s) IP (ANCOUNT est le nombre
					// de rï¿½ponses retournï¿½es)
					String[] adrs = new String[r];
					
					for(int i = 0; i < r; i++)
					{
						adrs[i] = TabInputStream.read() + "." + TabInputStream.read() + "." + TabInputStream.read() + "." + TabInputStream.read();
						System.out.println(adrs[i]);
						TabInputStream.skip(12);
					}
					
					//String adresseIP = TabInputStream.read() + "." + TabInputStream.read() + "." + TabInputStream.read() + "." + TabInputStream.read();
					
					//System.out.println("Addresse IP : " + adresseIP);		

					// *Ajouter la ou les correspondance(s) dans le fichier DNS
					// si elles ne y sont pas deja
					
					List<String> queryResult = new QueryFinder (DNSFile, DomainName).StartResearch(DomainName);
					
					if(queryResult.isEmpty())
					{
						for(String e: adrs)
						{
							new AnswerRecorder(DNSFile).StartRecord(DomainName, e);
						}
					}
					
					// *Faire parvenir le paquet reponse au demandeur original,
					// ayant emis une requete avec cet identifiant
					// *Placer ce paquet dans le socket
					sender = new UDPSender("127.0.0.1", port, serveur);
					// *Envoyer le paquet
					sender.SendPacketNow(paquetRecu);
				}
			}
			//			serveur.close(); //closing server
		} catch (Exception e) {
			System.err.println("Problème à l'exécution :");
			e.printStackTrace(System.err);
		}
	}
}
