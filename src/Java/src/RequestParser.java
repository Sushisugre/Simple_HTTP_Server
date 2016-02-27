import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class RequestParser {

    public static Request parse(InputStream clientInput) throws IOException{
        String buffer;
        Request request = new Request();

        BufferedReader inStream = new BufferedReader(new InputStreamReader(
                clientInput, StandardCharsets.UTF_8));

        /* Read the data send by the client */
        buffer = inStream.readLine();

        if (buffer == null) {
            return null;
        }

        System.out.println("Client send: "+buffer);

        String[] reqLine  = buffer.split(" ");
        if(reqLine.length < 3) {
            if (reqLine.length >= 1 && Request.RequestMethod.contains(reqLine[0]))
                request.setMethod(reqLine[0]);
            request.setValid(false);
            return request;
        }

//        System.out.println("test GET---");
//        System.out.println(Request.RequestMethod.contains("GET"));
//        System.out.println("test GET---");
//
//        System.out.println("test reqLine[0]---");
//        System.out.println(Request.RequestMethod.contains(reqLine[0]));
//        System.out.println("test reqLine[0]---");
//
//        System.out.println("GET equals reqLine[0]? " + reqLine[0].trim().equals("GET"));
//
//        String text = reqLine[0];
//        System.out.println("test text---");
//        System.out.println(Request.RequestMethod.contains(new String(reqLine[0])));
//        System.out.println("test text---");

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

        System.out.println("finish parsing");
        return request;
    }
}
