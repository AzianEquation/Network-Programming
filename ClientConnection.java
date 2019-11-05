// Coded by John Esco && Luke Whittle - Copyright 2019 
import java.io.*;
import java.net.*;
import java.util.Iterator;
import java.util.Scanner;

public class ClientConnection implements Runnable {

	// attributes
	public Socket clientSocket;
	// public Socket clientConnectSocket;
	public DataInputStream inputStreamfC; // from client
	public DataOutputStream outputStreamtC;
	public String clientName; // self
	public String state; // {available,busy}
	private String connectName = "";  // client to connect to 
	//private String reverseConnectName ="";
	// parameterized constructor
	public ClientConnection(Socket aClientSocket, DataInputStream anInputStream, DataOutputStream anOutputStream) {
		this.clientSocket = aClientSocket;
		//this.clientConnectSocket = 
		this.inputStreamfC = anInputStream;
		this.outputStreamtC = anOutputStream;
	}
	// mutators
	public void setName(String aName) {
		this.clientName = aName;
	}
	public void setState(String aState) {
		this.state = aState;
	}
	public void setConnectName(String aName)
	{
		this.connectName = aName;
	}
	public void run() {
		// variables to store chat data
		String dataGet, dataSend;
		try {
			inputStreamfC = new DataInputStream(clientSocket.getInputStream()); 
			outputStreamtC = new DataOutputStream(clientSocket.getOutputStream());
			// write to each chat_client's terminal
			// only want to write this initially
			// take user input from chat_client terminal
			dataGet = inputStreamfC.readUTF();
			// first received data is name
			this.setName(dataGet);
			System.out.println("Setting name:"  + this.clientName);
			// initially available
			this.setState("available");
			System.out.println("Setting state:"  + this.state);
			// print welcome message after name given
			outputStreamtC.writeUTF("Welcome to the 416 chat server");
			outputStreamtC.flush();
			// call server's print method. Prints current list to each chat_client and to chat_server on name entry
			server.printArrayList();
			// read chat_client input loop
			while (!dataGet.equalsIgnoreCase("quit")) {
				// at this point expecting chat_client to name another chat_client to connect to
				dataGet = inputStreamfC.readUTF();
				// call helper method returns true if name is in copyonwritearraylist
				if(this.arrayListContainsName(dataGet)) 
				{
					// set's this client's connectName to another client found in list
					this.setConnectName(dataGet);
				}
				// at this point expecting chat_client to respond to "Connect?" with 'y'
				// comes from the receiver
				if (dataGet.equalsIgnoreCase("y")) 
				{
					// establish connection
					//System.out.println("Attempting a connection from " + this.clientName + " to "  + this.connectName); // prints to chat_server
					// call establish method
					this.establish(this.connectName);
				}
				// if client is marked busy write dataGet 
				if (this.state.equalsIgnoreCase("busy"))
				{
					this.establish(this.connectName);
					this.outputStreamtC.writeUTF(dataGet);
				    this.outputStreamtC.flush();
				}
			if (dataGet.equalsIgnoreCase("quit")) {
				// quit
				System.out.println("Quiting"); // prints to chat_server
				// close the streams
				clientSocket.close();
				outputStreamtC.close();
				inputStreamfC.close();
				break;
			}
			} // end while
		} catch (EOFException e) {
			System.out.println("Server closed connection");
		} catch (Exception e) {
			System.out.println("Client (clientconnection.java)" + e.getMessage());
		}
	} // end run
	// checks arraylist for client by name; returns true if contained
	public boolean arrayListContainsName(String aName) {
        Iterator itr = server.clientConnections.iterator();
		boolean ret = false;
        while (itr.hasNext()) {
			try {
				ClientConnection c = (ClientConnection)itr.next();
				// found the client to connect
				if (aName.equalsIgnoreCase(c.clientName)){
					// need to send request to correct chat_client terminal
					c.outputStreamtC.writeUTF("Received request from " + this.clientName + "\nConnect?");
				    c.outputStreamtC.flush();
					ret = true;
					// set the found client to connect to searching client
					c.setConnectName(this.clientName);
				}
			}
			catch(Exception e) {
				System.out.println("printArrayList exception");
				e.printStackTrace();
			}
		}
		return ret;
    }
	// establish connection with client by name
	public void establish(String aName) {
		// receiver -> establish(sender/aName) 
		//System.out.println("On ENTER ESTABLISH      Attempting a connection from " + this.clientName + " to "  + aName);
        Iterator itr = server.clientConnections.iterator();
        String dataGet = "";
		while (itr.hasNext()) {
			try {
				ClientConnection c = (ClientConnection)itr.next();
				// found the client to connect
				//System.out.println("################ BEFORE IF ESTABLISH########   " + this.clientName);
				// if name we are searching for is a connection in list ...
				if (aName.equalsIgnoreCase(c.clientName)){
					// set receiver to busy
					this.setState("busy");
					// set what we connect to ->busy (sender)
					c.setState("busy");
					// redirect datastream to new client
					// connect to sender socket 
					// receiver gets input from sender
					DataInputStream inputStreamfS = new DataInputStream(c.clientSocket.getInputStream());
					// receiver writes to sender
					DataOutputStream outputStreamtS = new DataOutputStream(c.clientSocket.getOutputStream());
					//outputStreamtS.writeUTF("Im sending data from another client! says: " + this.clientName);
					// new loop for connection
					if(this.state.equalsIgnoreCase("busy"))
					{
						while(!dataGet.equalsIgnoreCase("quit"))
						{
						// get input from sender
						dataGet = inputStreamfC.readUTF();
						// redirect to receiver
						outputStreamtS.writeUTF(this.clientName + ": " +dataGet);
						}
						// set other to availbe
						this.setState("available");
						c.setState("available");
						server.clientConnections.remove(this);
						server.printArrayList();


				}
				}
			}
			catch(Exception e) {
				System.out.println("printArrayList exception");
				e.printStackTrace();
			}
		}
    }	
}