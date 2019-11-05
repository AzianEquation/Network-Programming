// John Esco 2019
/* A Java program for a Client */
import java.net.*; 
import java.io.*; 
  
public class Client 
{ 
/* initialize socket and input output streams */
private Socket socket = null; 
private BufferedReader input = null; 
private DataOutputStream out = null;
//instance variables for Server -> Client (read)
private ServerSocket server = null;
private DataInputStream in = null;

	/* constructor to put ip address and port */
	// private port range : 49152 - 65535. (use 50000)
public Client(String address, int port) 
{ 
	/* establish a connection */
	// takes user input and sends to server
	try {
		socket = new Socket(address, port); 
	} catch(Exception i) {
		System.out.println("Error in IP or port");
		System.exit(0);
    	}
	System.out.println("Connected");
	System.out.println("Welcome to basic IM! The Client begins by sending the first message \n" +
			"Type 'Over' to switch sides or 'Close' to end the connection");

	try { 
		/* takes input from terminal */
		input = new BufferedReader(new InputStreamReader(System.in)); 

		/* sends output to the socket */
		out = new DataOutputStream(socket.getOutputStream()); 

	} catch(IOException i) { 
		System.out.println(i); 
	}

	/* string to read message from input */
	String cString = "";
	String sString = "";
	/* keep reading until "Over" is input */
	while (!cString.equals("Close") || !sString.equals("Close")) {
		try {
			// takes client input
			cString = input.readLine();
			if (cString.equals("Close"))
				break;
			// writes to server
			out.writeUTF(cString);
		} catch(Exception i) { 
			System.out.println(i); 
		}
		if (cString.equals("Over"))
		{
			try {
				in = new DataInputStream(
						new BufferedInputStream(socket.getInputStream()));
				// clear sString value
				sString = "";
				/* reads message from server until "Over" is sent */
				while (!sString.equals("Over")) {
					sString = in.readUTF();
					if (!sString.equals("")) {
						System.out.println("Got input from Server ...");
						System.out.println("Printing input: " + sString);
					}
				}
			} catch(EOFException i) {
				System.out.println(i);
				System.out.println("Closing connection");
				System.exit(0);
			}
			catch(Exception i) {
				System.out.println(i);
			}
		}
	}
	/* close the connection */
	// end reading client output
	System.out.println("Closing connection");
	try { 
		input.close(); 
		out.close(); 
		socket.close(); 
	} catch(Exception i) {
		System.out.println(i);  
	} 
}

public static void main(String args[]) 
{ 
	if (args.length < 2) {
		System.out.println("Client usage: java Client #IP_address #port_number");
	}
	else {
		Client client = new Client(args[0], Integer.parseInt(args[1])); 
	}
} 

}
