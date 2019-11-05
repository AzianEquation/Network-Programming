// Coded by John Esco && Luke Whittle - Copyright 2019 
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class server implements Runnable {

    int port = 4321;
    ServerSocket socket = null;
    public volatile boolean active = true;
	static DataInputStream inputStream;
	static DataOutputStream outputStream;

    // need client state here
    static CopyOnWriteArrayList<ClientConnection> clientConnections = new CopyOnWriteArrayList<>();

    public void run() {
        System.out.println("Waiting for a client...");
        try {
            // try to connect socket to port
            this.socket = new ServerSocket(this.port);
            // timeout of 10 seconds
            socket.setSoTimeout(10000);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port" + port);
        }
        while (this.active) {
            Socket clientSocket = null;
            try {
                clientSocket = this.socket.accept();
                System.out.println("Client accepted");
                inputStream = new DataInputStream(clientSocket.getInputStream());
                outputStream = new DataOutputStream(clientSocket.getOutputStream());
				// create ClientConnection per client
                ClientConnection currConnect = new ClientConnection(clientSocket, inputStream, outputStream);
				// add to CopyOnWriteArrayList
                clientConnections.add(currConnect);
				// start each thread
				new Thread(currConnect).start();
            } catch (SocketTimeoutException e) {
                //System.out.println("Timeout socket (server.java)" + e);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        } // end while
    } // end run
	// print helper method: iterates through CopyOnWriteArrayList. if size>=2&&available, prompts connection request
	public static void printArrayList() {
		System.out.println("List of clients and states");
        Iterator itr = server.clientConnections.iterator();
		String ret = arrayString();
		String question ="";
        while (itr.hasNext()) {
			try {
				// retrieve current ClientConnection
				ClientConnection c = (ClientConnection)itr.next();
				// Print to main chat_server
				System.out.print(c.clientName + "\t");
				System.out.println(c.state);
				// asks available clients for name to connect to 
				if ((server.clientConnections.size() >= 2) && c.state.equals("available")) {
				question = "Connect to which client";
				}
				// write to each ClientConnection
				c.outputStreamtC.writeUTF("List of clients and states \n" + ret + question);
				// clear buffer
				c.outputStreamtC.flush();
			}
			catch(Exception e) {
				System.out.println("printArrayList exception");
				e.printStackTrace();
			}
		}
    }
	// helper method to create string of current arraylist's {name,state}
	public static String arrayString() {
	Iterator itr = server.clientConnections.iterator();
	String ret = "";
	while (itr.hasNext()) {
		try {
				// retrieve current ClientConnection
				ClientConnection c = (ClientConnection)itr.next();
				ret += (c.clientName + "\t" + c.state + "\n");
			}
			catch(Exception e) {
				System.out.println("printArrayList exception");
				e.printStackTrace();
			}
	}
	return ret;
	}
}