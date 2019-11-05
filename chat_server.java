// Coded by John Esco && Luke Whittle - Copyright 2019 
/* A Java program for a Server */
import java.net.*;
import java.util.Scanner;
import java.io.*;

public class chat_server {

    public static void main(String[] args) {
		// instantiate server instance
        server svr = new server();
		// thread executes server runnable 
        Thread thread = new Thread(svr);
		// start thread
        thread.start();
		// kb input
        Scanner input = new Scanner(System.in);
        String line = "";
		// reads until quit is input
        while (!(line = input.nextLine()).equalsIgnoreCase("quit"))
			; // loops
        // on 'quit'
		System.out.println("Stopping server");
        svr.active = false;
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            System.out.println("Shutdown timeout - shutting down now");
        }
        System.out.println("Shutting down");
        System.exit(0);
    }
}