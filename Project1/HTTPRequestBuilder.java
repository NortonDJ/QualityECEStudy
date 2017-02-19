import java.util.ArrayList;
import java.util.HashMap;

public class HTTPRequestBuilder
{
    private HashMap<String, String> headerLines;
    private ArrayList<Byte> fullMessage;
    /**
     * Constructor for objects of class HTTP
     */
    public HTTPRequestBuilder(){
        this.headerLines = new HashMap<String, String>();
        this.fullMessage = new ArrayList<Byte>();
    }

    public byte[] build(String method, String url, float version){

        //Add the request line
        putRequestLine(method, url, version);

        //Add all the header lines from the map
        putAllHeaderLines();

        //Add a blank line to show that we are finished
        putBlankLine();

        //Convert the full message into a primitive final message
        byte[] finalMessage = convertMessage();

        //Clear our instance variables for the next building of a message
        clear();

        return finalMessage;
    }

    public void mapHeader(String key, String value){
        headerLines.put(key,value);
    }

    public void putRequestLine(String method, String url, float version) {
        //Add the method bytes
        byte[] methodBytes = method.getBytes();
        for(byte b : methodBytes){
            fullMessage.add(b);
        }

        fullMessage.add(ByteArrayHelper.SP);

        //Add the url bytes
        byte[] urlBytes = url.getBytes();
        for(byte b: urlBytes){
            fullMessage.add(b);
        }

        fullMessage.add(ByteArrayHelper.SP);

        //Add the version bytes
        byte[] versionBytes = ByteArrayHelper.toByteArray(version);
        for(byte b: versionBytes){
            fullMessage.add(b);
        }
        fullMessage.add(ByteArrayHelper.CR);
        fullMessage.add(ByteArrayHelper.LF);
    }

    public void putHeaderLine(String header, String value) {

        // Add the header bytes
        byte[] headerBytes = ByteArrayHelper.toByteArray(header);
        for(byte b : headerBytes){
            fullMessage.add(Byte.valueOf(b));
        }

        fullMessage.add(ByteArrayHelper.SP);

        // Add the value bytes
        byte[] valueBytes = value.getBytes();
        for(byte b: valueBytes) {
            fullMessage.add(Byte.valueOf(b));
        }

        // Terminate the line
        fullMessage.add(ByteArrayHelper.CR);
        fullMessage.add(ByteArrayHelper.LF);
    }

    public void putAllHeaderLines(){
        for(String header : headerLines.keySet()){
            String value = headerLines.get(header);
            putHeaderLine(header,value);
        }
    }

    public void putBlankLine(){
        fullMessage.add(ByteArrayHelper.CR);
        fullMessage.add(ByteArrayHelper.LF);
    }

    public void clear(){
        this.fullMessage = new ArrayList<Byte>();
        this.headerLines = new HashMap<String, String>();
    }

    public byte[] convertMessage(){
        byte[] finalMessage = new byte[fullMessage.size()];
        for(int i = 0; i < fullMessage.size(); i++){
            finalMessage[i] = fullMessage.get(i);
        }

        return finalMessage;
    }

}
