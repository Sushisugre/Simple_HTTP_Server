/**
 * @file: RequestHandler
 *
 * Handle client connections in separate threads
 *
 * @author: Shi Su <shis@andrew.cmu.edu>
 *
 * @date: Fri Feb 26 18:00:28 EST 2016
 */

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class RequestHandler implements Runnable {
    private static final int BUFFER_SIZE = 1024 * 250;
    private String serverPath;
    private Socket clientSock;

    RequestHandler(Socket clientSock, String serverPath) {
        this.clientSock = clientSock;
        this.serverPath = serverPath;
    }

    @Override
    public void run() {
        try {

            System.out.println("Read from client "
                    + clientSock.getInetAddress() + ":"
                    + clientSock.getPort());

            String buffer;
            /**
             * For handling DOS attack: set client socket timeout after 5000s
             */
            clientSock.setSoTimeout(10000);

            // parse client request
            Request request = RequestParser.parse(clientSock.getInputStream());
            DataOutputStream outStream = new DataOutputStream(clientSock.getOutputStream());

            if (request == null) {
                buffer = ResponseGenerator.getErr(Response.INTERNAL_SERVER_ERROR);

                outStream.writeBytes(buffer);
                outStream.flush();
                clientSock.close();
                return;
            }

            if (!request.isValid()) {
                System.out.println("request is not valid");

                if (Request.RequestMethod.HEAD.equals(request.getMethod()))
                    buffer = ResponseGenerator.getErrHeader(Response.BAD_REQUEST);
                else
                    buffer = ResponseGenerator.getErr(Response.BAD_REQUEST);

                outStream.writeBytes(buffer);
                outStream.flush();
                clientSock.close();
                return;
            }

            // HTTP 1.0 only contains get/head/post, and post is unimplemented
            if (Request.RequestMethod.POST.equals(request.getMethod())) {
                buffer = ResponseGenerator.getErr(Response.METHOD_UNIMPLEMENTED);
                outStream.writeBytes(buffer);
                outStream.flush();
                clientSock.close();
                return;
            }

            // file not found, return 404
            String filePath = serverPath + request.getUri();
            File file = new File(filePath);
            if (!file.exists()) {
                if (Request.RequestMethod.HEAD.equals(request.getMethod()))
                    buffer = ResponseGenerator.getErrHeader(Response.NOT_FOUND);
                else
                    buffer = ResponseGenerator.getErr(Response.NOT_FOUND);

                outStream.writeBytes(buffer);
                outStream.flush();
                clientSock.close();
                return;
            }

            // Not able to open/read it, return 403
            if (!file.canRead()) {
                if (Request.RequestMethod.HEAD.equals(request.getMethod()))
                    buffer = ResponseGenerator.getErrHeader(Response.FORBIDDEN);
                else
                    buffer = ResponseGenerator.getErr(Response.FORBIDDEN);
                outStream.writeBytes(buffer);
                outStream.flush();
                clientSock.close();
                return;
            }

            String mime = GetMime.getMimeType(filePath);
            long length = file.length();
            buffer = ResponseGenerator.getSuccessHeader(mime, length);
            outStream.writeBytes(buffer);

            // only sent header
            if (Request.RequestMethod.HEAD.equals(request.getMethod())) {
                System.out.println(request.getMethod());
                outStream.flush();
                clientSock.close();
                return;
            }

            // GET .. return entire file
            // Use InputStream instead of reader, so image will be handle properly
            BufferedInputStream fileStream = new BufferedInputStream(new FileInputStream(new File(filePath)));
            byte[] fileBuf = new byte[BUFFER_SIZE];
            long remain = length;
            while (remain > 0) {
                int retVal = fileStream.read(fileBuf, 0, (remain > BUFFER_SIZE ? BUFFER_SIZE : (int) remain));
                outStream.write(fileBuf);
                remain -= (retVal == -1 ? 0 : retVal);
            }

            /*
			 * flush the stream to make sure that the
			 * data is sent immediately
			 */
            outStream.flush();
            /* Interaction with this client complete, close() the socket */
            clientSock.close();
        } catch (SocketTimeoutException e) {
            /**
             * For handling DOS attack: set client socket timeout after 5000s
             * fail to get any data client socket within timeout, terminate it
             */
            System.err.println("SocketTimeOut for:" + clientSock.getInetAddress()+":"+clientSock.getPort());
            try{
                clientSock.close();
            }catch (Exception excetpion){}

        } catch (IOException e) {
            clientSock = null;
        }
    }
}
