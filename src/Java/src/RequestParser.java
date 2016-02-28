/**
 * @file: RequestParser
 *
 * Parser the client input stream and get return an Request object
 *
 * @author: Shi Su <shis@andrew.cmu.edu>
 *
 * @date: Sat Feb 27 16:43:36 EST 2016
 *
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class RequestParser {

    /**
     * Parse client request
     *
     * @param clientInput socket input stream
     * @return Request object
     * @throws IOException
     */
    public static Request parse(InputStream clientInput) throws IOException{
        String buffer;
        Request request = new Request();

        BufferedReader inStream = new BufferedReader(new InputStreamReader(
                clientInput, StandardCharsets.UTF_8));

        /* Read the data send by the client */
        buffer = inStream.readLine();

        if (buffer == null) {
            request.setValid(false);
            return request;
        }


        String[] reqLine  = buffer.split(" ");
        if(reqLine.length < 3) {
            if (reqLine.length >= 1 && Request.RequestMethod.contains(reqLine[0]))
                request.setMethod(reqLine[0]);
            request.setValid(false);
            return request;
        }


        // invalid method
        if(!Request.RequestMethod.contains(reqLine[0])) {
            request.setValid(false);
            return request;
        }
        request.setMethod(reqLine[0]);
        System.out.println(request.getMethod());


        // invalid url
        if (!reqLine[1].startsWith("/")) {
            request.setValid(false);
            return request;
        }

        // if client request /, map to /index.html
        if (reqLine[1].equals("/"))
            request.setUri("/index.html");
        else
            request.setUri(reqLine[1]);
        System.out.println(request.getUri());


        // invalid protocol
        if (!(reqLine[2].trim().equals("HTTP/1.0")
                ||reqLine[2].trim().equals("HTTP/1.1") )) {
            request.setValid(false);
            return request;
        }
        request.setProtocol(reqLine[2]);
        System.out.println(request.getProtocol());


        if(!inStream.ready()) {
            return request;
        }

        // add headers
        while((buffer = inStream.readLine())!=null) {
            System.out.println("line: "+buffer);
            String[] pair = buffer.split(":",2);

            if(buffer.trim().equals("")) {
               break;
            }
            if (pair.length != 2) {

                request.setValid(false);
                break;
            }
            request.appendHeader(pair[0], pair[1]);
        }

        return request;
    }
}
