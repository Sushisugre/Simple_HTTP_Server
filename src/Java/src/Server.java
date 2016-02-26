/**
 * @file: Server.java
 * 
 * @author: Chinmay Kamat <chinmaykamat@cmu.edu>
 * 
 * @date: Feb 15, 2013 1:13:37 AM EST
 *
 *
 * @updated: Shi Su <shis@andrew.cmu.edu>
 *
 * @date: Fri Feb 26 17:57:49 EST 2016
 * 
 */
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	private static ServerSocket srvSock;
	private static String path;

	public static void main(String args[]) {
		int port = 8080;

		/* Parse parameter and do args checking */
		if (args.length < 2) {
			System.err.println("Usage: java Server <port_number> <www_path>");
			System.exit(1);
		}

		try {
			port = Integer.parseInt(args[0]);
		} catch (Exception e) {
			System.err.println("Usage: java Server <port_number> <www_path>");
			System.exit(1);
		}

		if (port > 65535 || port < 1024) {
			System.err.println("Port number must be in between 1024 and 65535");
			System.exit(1);
		}

		// working path of server
		path = args[1];

		try {
			/*
			 * Create a socket to accept() client connections. This combines
			 * socket(), bind() and listen() into one call. Any connection
			 * attempts before this are terminated with RST.
			 */
			srvSock = new ServerSocket(port);
		} catch (IOException e) {
			System.err.println("Unable to listen on port " + port);
			System.exit(1);
		}

		while (true) {
			Socket clientSock;
			try {
				/*
				 * Get a sock for further communication with the client. This
				 * socket is sure for this client. Further connections are still
				 * accepted on srvSock
				 */
				clientSock = srvSock.accept();
				System.out.println("Accpeted new connection from "
						+ clientSock.getInetAddress() + ":"
						+ clientSock.getPort());

				// handle the request in handler thread
				RequestHandler handler = new RequestHandler(clientSock, path);
				Thread thread = new Thread(handler);
				thread.start();

			} catch (IOException e) {
				continue;
			}

		}
	}
}
