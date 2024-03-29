/**
 * @file: Client.java
 * 
 * @author: Chinmay Kamat <chinmaykamat@cmu.edu>
 * 
 * @date: Feb 15, 2013 1:14:09 AM EST
 * 
 */

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
	public static void main(String[] args) {
		Socket sock;
		int port = 8080;
		InetAddress addr = null;
		BufferedReader inStream = null;
		DataOutputStream outStream = null;
		String buffer = null;

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		/* Parse parameter and do args checking */
		if (args.length < 2) {
			System.err.println("Usage: java Client <server_ip> <server_port>");
			System.exit(1);
		}

		try {
			port = Integer.parseInt(args[1]);
		} catch (Exception e) {
			System.err.println("Usage: java Client <server_ip> <server_port>");
			System.exit(1);
		}

		if (port > 65535 || port < 1024) {
			System.err.println("Port number must be in between 1024 and 65535");
			System.exit(1);
		}

		try {
			/* Get the server adder in InetAddr format */
			addr = InetAddress.getByName(args[0]);
		} catch (UnknownHostException e) {
			System.err.println("Invalid address provided for server");
			System.exit(1);
		}

		while (true) {
			try {
				/* Read data from the user */
				buffer = br.readLine();
				/*
				 * connect() to the server at addr:port. The server needs to be
				 * listen() in order for this to succeed. This call initiates
				 * the SYN-SYN/ACK-ACK handshake
				 */
				sock = new Socket(addr, port);
			} catch (IOException e) {
				System.err.println("Unable to reach server");
				continue;
			}
			try {
				inStream = new BufferedReader(new InputStreamReader(
						sock.getInputStream()));
				/* Write the date to the server */
				outStream = new DataOutputStream(sock.getOutputStream());
				outStream.writeBytes(buffer);
				outStream.writeChar('\n');
				outStream.flush();
				/* Read the data echoed by the server */
				buffer = inStream.readLine();
				System.out.println("Received : " + buffer);
				/* Close the connection and wait for next input */
				sock.close();
			} catch (IOException e) {
			}
		}
	}
}
