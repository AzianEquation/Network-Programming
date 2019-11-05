// John Esco 2019
/* A Java program for a Server */
import java.net.*; 
import java.io.*; 
  
public class Server 
{ 
/* initialize socket and input stream */
private Socket socket = null; 
private ServerSocket server = null;
private DataInputStream in = null;
// instance variables for Server -> Client (Write)
private BufferedReader input = null;
private DataOutputStream out = null;

	/* constructor with port */
public Server(int port) 
{ 
	/* starts server and waits for a connection */
	try { 
		server = new ServerSocket(port); 
	} catch(Exception i) {
		System.out.println("Error in port");
		System.exit(0);
	}
	System.out.println("Server started"); 

	System.out.println("Waiting for a client ..."); 
	try {
		socket = server.accept(); 
		System.out.println("Client accepted"); 
		
		/* reads output from the client socket */
		in = new DataInputStream( 
		    new BufferedInputStream(socket.getInputStream())); 
		
		String cString = "";
		String sString = "";
		/* reads message from client until "Over" is sent */

		// label
		outerloop:
		while (!cString.equals("Close") || !sString.equals("Close"))
		{
		        cString = in.readUTF();
		        // if recieve message then print..
			    if (!cString.equals("")) {
					System.out.println("Got input from Client ...");
					System.out.println("Printing input: " + cString);
				}
			    // client sends over message
		        if (cString.equals("Over"))
				{
					//servers turn to write
					input = new BufferedReader(new InputStreamReader(System.in));
					/* sends output to the socket */
					out = new DataOutputStream(socket.getOutputStream());

					sString = "";
					//reads until "Over" is sent
					while (!sString.equals("Over")) {
						try {
							sString = input.readLine();
							// writes to client
							out.writeUTF(sString);
						} catch(Exception i) {
							System.out.println(i);
						}if (sString.equals("Close"))
							break outerloop;
					}
				}
		}
		// end reading client output
		System.out.println("Closing connection");
		/* close connection */
		socket.close(); 
		in.close(); 

	} catch(EOFException i) { 
	    System.out.println(i);
		System.out.println("Closing connection");
		System.exit(0);
	} 
	catch(Exception i) { 
	    System.out.println(i); 
	} 
}

public static void main(String args[]) 
{ 
	if (args.length < 1) {
		System.out.println("Server usage: java Server #port_number");
	}
	else {
		try {
			Server server = new Server(Integer.parseInt(args[0])); 
		} catch(Exception i) {
			System.out.println("Error in port");	
		}
	}
} 

} 
