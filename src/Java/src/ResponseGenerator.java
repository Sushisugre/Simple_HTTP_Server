/**
 * Created by s4keng on 2/26/16.
 */
public class ResponseGenerator {
    private static final String PROTOCOL = "HTTP/1.0";

    public static String getErr(String statue) {
        StringBuffer sb = new StringBuffer();
        sb.append(getErrHeader(statue));
        sb.append("<html>");
        sb.append("<head><title>"+statue+"</title></head>");
        sb.append("<body bgcolor=\"white\">");
        sb.append("<h1>"+statue+"</h1>");
        sb.append("<hr><center>stupid-server</center>");
        sb.append("</body>");
        sb.append("</html>");
        return sb.toString();
    }

    public static String getErrHeader(String statue) {
        StringBuffer sb = new StringBuffer();
        sb.append(PROTOCOL+" "+ statue +"\r\n");
        sb.append("Server: Simple/1.0\r\n");
        sb.append("Content-Type: text/html\r\n");
        sb.append("\r\n");
        return sb.toString();
    }

    public static String getSuccessHeader(String mime, long length) {
        StringBuffer sb = new StringBuffer();
        sb.append(PROTOCOL+" 200 OK\r\n");
        sb.append("Server: Simple/1.0\r\n");
        sb.append("Content-Type: "+mime+"\r\n");
        sb.append("Content-Length: "+length+"\r\n");
        sb.append("\r\n");
        return sb.toString();
    }
}
