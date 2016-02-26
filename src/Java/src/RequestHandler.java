/**
 *
 *
 * @author: Shi Su <shis@andrew.cmu.edu>
 *
 * @date: Fri Feb 26 18:00:28 EST 2016
 */

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class RequestHandler implements Runnable {
    private String serverPath;
    private Socket clientSock;

    RequestHandler(Socket clientSock, String serverPath) {
        this.clientSock = clientSock;
        this.serverPath = serverPath;
    }

    @Override
    public void run() {
        try {
            BufferedReader inStream = null;
            DataOutputStream outStream = null;
            String buffer = null;

            inStream = new BufferedReader(new InputStreamReader(
                    clientSock.getInputStream()));
            outStream = new DataOutputStream(clientSock.getOutputStream());
				/* Read the data send by the client */
            buffer = inStream.readLine();
            System.out.println("Read from client "
                    + clientSock.getInetAddress() + ":"
                    + clientSock.getPort() + " " + buffer);
				/*
				 * Echo the data back and flush the stream to make sure that the
				 * data is sent immediately
				 */
            outStream.writeBytes(buffer);
            outStream.flush();
				/* Interaction with this client complete, close() the socket */
            clientSock.close();
        } catch (IOException e) {
            clientSock = null;
        }
    }
}
