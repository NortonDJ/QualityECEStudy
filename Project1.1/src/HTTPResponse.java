import java.util.HashMap;

/**
 * Created by nortondj on 3/8/17.
 */
public class HTTPResponse {

    private float version;
    private int statusCode;
    private String phrase;
    private HashMap<String, String> headerLines;
    private String body;

    public HTTPResponse(float version, int statusCode, String phrase){
        this.version = version;
        this.statusCode = statusCode;
        this.phrase = phrase;
        this.body = "";
        this.headerLines = new HashMap<String, String>();
    }

    /**
     * a method of storing into the hashmap
     *
     * @param key, value
     */
    public void mapHeader(String key, String value){
        headerLines.put(key,value);
    }

    public void setBody(String body){
        this.body = body;
    }

    public HashMap<String,String> getHeaderLines(){
        return this.headerLines;
    }

    public String getPhrase(){
        return this.phrase;
    }

    public int getStatusCode(){
        return this.statusCode;
    }

    public float getVersion(){
        return this.version;
    }

    public String getBody(){
        return this.body;
    }

    public String toString(){
        String s = "Version: " + this.version + "\n";
        s += "Status: " + this.statusCode + "\n";
        s += "Phrase: " + this.phrase + "\n";
        for(String header : headerLines.keySet()){
            s += header + ": " + headerLines.get(header) + "\n";
        }
        s += "Body: " + this.body + "\n";
        return s;
    }
}
