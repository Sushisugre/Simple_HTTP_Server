import java.util.Map;

/**
 * Created by s4keng on 2/26/16.
 */
public class Request {
    public String protocol;
    public String version;
    public Map<String, String> headers;
    public RequestMethod method;

    public static enum RequestMethod {
        HEAD,
        GET,
        UNSUPPORTED;

        public static RequestMethod fromString(String s) {
            RequestMethod method;
            try {
                method = RequestMethod.valueOf(s.toUpperCase());
            }
            catch (Exception e) {
                method = UNSUPPORTED;
            }
            return method;
        }
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

    public void setMethod(String method) {
        this.method = RequestMethod.fromString(method);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void appendHeader(String key, String value) {
        headers.put(key, value);
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
