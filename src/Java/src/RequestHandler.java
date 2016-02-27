/**
 *
 *
 * @author: Shi Su <shis@andrew.cmu.edu>
 *
 * @date: Fri Feb 26 18:00:28 EST 2016
 */

import java.io.*;
import java.net.Socket;

public class RequestHandler implements Runnable {
    private static final int BUFFER_SIZE = 2048;
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

            String buffer = null;
            // parse client request
            Request request = RequestParser.parse(clientSock.getInputStream());
            DataOutputStream outStream = new DataOutputStream(clientSock.getOutputStream());


            if (!request.isValid()) {
                System.out.println("request is not valid");

                if (Request.RequestMethod.HEAD.equals(request.getMethod()))
                    buffer = ResponseGenerator.getErrHeader(Response.BAD_REQUEST);
                else
                    buffer =ResponseGenerator.getErr(Response.BAD_REQUEST);

                outStream.writeBytes(buffer);
                outStream.flush();
                clientSock.close();
                return;
            }

            // HTTP 1.0 only contains get/head/post, and post is unimplemented
            if (Request.RequestMethod.POST.equals(request.getMethod())) {
                buffer = ResponseGenerator.getErr(Response.METHOD_UMIMPLEMENTED);
                outStream.writeBytes(buffer);
                outStream.flush();
                clientSock.close();
                return;
            }

            String filePath = serverPath + request.getUri();
            File file = new File(filePath);
            if(!file.exists()) {
                if (Request.RequestMethod.HEAD.equals(request.getMethod()))
                    buffer = ResponseGenerator.getErrHeader(Response.NOT_FOUND);
                else
                    buffer =ResponseGenerator.getErr(Response.NOT_FOUND);

                outStream.writeBytes(buffer);
                outStream.flush();
                clientSock.close();
                return;
            }

            String mime = GetMime.getMimeType(filePath);
            long length = file.length();
            buffer = ResponseGenerator.getSuccessHeader(mime,length);
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
            while (remain>0) {
                int retVal = fileStream.read(fileBuf, 0, (remain>BUFFER_SIZE? BUFFER_SIZE:(int)remain));
                outStream.write(fileBuf);
                remain -= (retVal==-1?0:retVal);
            }

				/*
				 * flush the stream to make sure that the
				 * data is sent immediately
				 */
            outStream.flush();
				/* Interaction with this client complete, close() the socket */
            clientSock.close();
        } catch (IOException e) {
            clientSock = null;
        }
    }
}
