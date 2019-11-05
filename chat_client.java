// Coded by John Esco && Luke Whittle - Copyright 2019 
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class chat_client {

	// state is available or busy
	public static void main(String[] args) throws IOException {
		// flags
		boolean firstReadFlag = true;
		boolean first = true;
		try {
			Scanner kb = new Scanner(System.in);
			Socket socket = null;
			BufferedReader input = null;
			DataOutputStream outputStream = null;
			DataInputStream inputStream = null;
			if (args.length == 2)
				try {
					socket = new Socket(args[0], Integer.parseInt(args[1]));
				} catch (Exception i) {
					System.out.println("Error in IP or port");
					System.exit(0);
				}
			else {
				System.out.println("Invalid arguments -> chat_client usage: java chat_client IP port");
				System.exit(0);
			}
			System.out.println("Enter client name:");
			outputStream = new DataOutputStream(socket.getOutputStream());
			inputStream = new DataInputStream(socket.getInputStream());
			input = new BufferedReader(new InputStreamReader(System.in));
			String tmpSend = "";
			String tmpRead = "";
			while (!tmpSend.equalsIgnoreCase("quit")) { 
				// This blocks client until input is sent. activated by a flag
				if (firstReadFlag) {
					tmpSend = input.readLine();
					outputStream.writeUTF(tmpSend);
					outputStream.flush();
					// set flag to true so Input does not block
					firstReadFlag = false;
				}
				// not first block
				else {
					// print what ClientConnection sends
					if (inputStream.available() > 0) {
						// read what clientConnection sent
						tmpRead = inputStream.readUTF();
						// print
						System.out.println(tmpRead);
					}
					if (input.ready())
					{
						tmpSend = input.readLine();
						outputStream.writeUTF(tmpSend);
					}
				}
			
			}// end client while
			inputStream.close();
			outputStream.close();
			input.close();
		} // end try
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}