import java.util.HashMap;
import java.util.Map;

/**
 * @file: Request.java
 *
 * Request Object
 *
 * @author: Shi Su <shis@andrew.cmu.edu>
 *
 * @date: Fri Feb 26 18:00:28 EST 2016
 */
public class Request {
    private String protocol;
    private Map<String, String> headers;
    private RequestMethod method;
    private String uri;
    private boolean isValid;

    public Request() {
        isValid = true;
        headers = new HashMap<>();
    }

    /**
     * method in HTTP 1.0
     */
    public enum RequestMethod {
        HEAD,
        GET,
        POST;

        public static RequestMethod fromString(String s) throws IllegalArgumentException {
            RequestMethod method;
            method = RequestMethod.valueOf(s.toUpperCase());
            return method;
        }

        /**
         * Check if a request method is valid
         * @param test method string
         * @return is valid method
         */
        public static boolean contains(String test) {

            for (RequestMethod m : RequestMethod.values()) {

                if (m.name().trim().equals(test.trim())) {
                    return true;
                }
            }
            return false;
        }
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public RequestMethod getMethod() {
        return method;
    }

    public void setMethod(String method) throws IllegalArgumentException {
        this.method = RequestMethod.fromString(method);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void appendHeader(String key, String value) {
        headers.put(key, value);
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean isValid) {
        this.isValid = isValid;
    }

}
