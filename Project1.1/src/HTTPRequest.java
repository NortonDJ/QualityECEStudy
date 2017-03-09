import java.util.HashMap;

/**
 * Created by nortondj on 3/8/17.
 */
public class HTTPRequest {

    private HashMap<String, String> headerLines;
    private float version = 0;
    private String url = "";
    private String method = "";
    private String body = "";

    public HTTPRequest(){
        this.headerLines = new HashMap<String,String>();
    }

    public HTTPRequest(String method, String url, float version){
        this.method = method;
        this.url = url;
        this.version = version;
        this.headerLines = new HashMap<String, String>();
        this.body = "";
    }

    /**
     * a method of storing into the hashmap
     *
     * @param key, value
     */
    public void mapHeader(String key, String value){
        headerLines.put(key,value);
    }

    public HashMap<String,String> getHeaderLines(){
        return this.headerLines;
    }

    public String getMethod(){
        return this.method;
    }

    public String getUrl(){
        return this.url;
    }

    public float getVersion(){
        return this.version;
    }

    public void setBody(String body){
        this.body = body;
    }

    public String getBody(){
        return this.body;
    }

    public String getHeader(String header){
        if(this.headerLines.containsKey(header)){
            return headerLines.get(header);
        }
        else{
            return "";
        }
    }

    public String toString(){
        String s = "Method: " + this.method + "\n";
        s += "Url: " + this.url + "\n";
        s += "Version: " + this.version + "\n";
        for(String header : headerLines.keySet()){
            s +=header + ": " + headerLines.get(header) + "\n";
        }
        s += "Body: " + this.body + "\n";
        return s;
    }





}
