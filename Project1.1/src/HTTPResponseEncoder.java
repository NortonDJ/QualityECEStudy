import java.util.ArrayList;
import java.util.HashMap;
/**
* This class contains method for HTTP to compose the response message it needs to send, and stored all original information in a
* hashmap.
* 
* @author  Darren Norton, Yizhong Chen
* @since   Feb-19th-2017 
*/
public class HTTPResponseEncoder {
    private ArrayList<Byte> fullMessage;

    /**
     * Constructor of HTTPResponseEncoder
     * create two data structure to keep track with
     */
    public HTTPResponseEncoder(){
        this.fullMessage = new ArrayList<Byte>();
    }

    public byte[] encode(HTTPResponse resp)
    {

        //Add the status line
        putStatusLine(resp.getVersion(), resp.getStatusCode(), resp.getPhrase());

        //Add all the header lines from the map
        putAllHeaderLines(resp);

        //Add a blank line to show that we are finished with status and header
        //lines
        putBlankLine();

        //Add our message in this case the web-page
        putBody(resp.getBody());

        //Convert the full message into a primitive final message
        byte[] finalMessage = convertMessage();

        //Clear our instance variables for the next building of a message
        clear();

        return finalMessage;

    }

    
    /**
     * This method build the status line
     * 
     * @param version, statusCode, phrase
     */ 
    public void putStatusLine(float version, int statusCode, String phrase) {
        //Add the version bytes
        byte[] versionBytes = ByteArrayHelper.toByteArray(version);
        for(byte b : versionBytes){
            fullMessage.add(b);
        }

        fullMessage.add(ByteArrayHelper.SP);

        //Add the statusCode bytes
        byte[] statusCodeBytes = ByteArrayHelper.toByteArray(statusCode);
        for(byte b: statusCodeBytes){
            fullMessage.add(b);
        }

        fullMessage.add(ByteArrayHelper.SP);

        //Add the phase bytes
        byte[] phraseBytes = ByteArrayHelper.toByteArray(phrase);
        for(byte b: phraseBytes){
            fullMessage.add(b);
        }

        fullMessage.add(ByteArrayHelper.CR);
        fullMessage.add(ByteArrayHelper.LF);
    }
    
    /**
     * This method build one header line
     * 
     * @param header and value
     */ 
    public void putHeaderLine(String header, String value) {
        // Add the header bytes
        byte[] headerBytes = ByteArrayHelper.toByteArray(header);
        for(byte b : headerBytes){
            fullMessage.add(b);
        }

        fullMessage.add(ByteArrayHelper.SP);

        // Add the value bytes
        byte[] valueBytes = ByteArrayHelper.toByteArray(value);
        for(byte b: valueBytes) {
            fullMessage.add(b);
        }

        // Terminate the line
        fullMessage.add(ByteArrayHelper.CR);
        fullMessage.add(ByteArrayHelper.LF);
    }

    /**
     * This method form all header lines together
     * 
     */ 
    public void putAllHeaderLines(HTTPResponse resp){
        HashMap<String, String> headerLines = resp.getHeaderLines();
        for(String header : headerLines.keySet()){
            String value = headerLines.get(header);
            putHeaderLine(header,value);
        }
    }

    /**
     * This method build a blank line
     * 
     */ 
    public void putBlankLine(){
        fullMessage.add(ByteArrayHelper.CR);
        fullMessage.add(ByteArrayHelper.LF);
    }

    /**
     * convert body of the message into byte array and get stored
     * 
     * @param body
     */ 
    public void putBody(String body){
        byte[] messageBytes = ByteArrayHelper.toByteArray(body);
        for(byte b : messageBytes) {
            fullMessage.add(b);
        }
    }

    /**
     * This method clear everything in hashmap and arraylist
     * 
     */ 
    public void clear(){
        this.fullMessage = new ArrayList<Byte>();
    }

    /**
     * This method build the body
     * 
     * @return byte array of body
     */ 
    public byte[] convertMessage(){
        byte[] finalMessage = new byte[fullMessage.size()];
        for(int i = 0; i < fullMessage.size(); i++){
            finalMessage[i] = fullMessage.get(i);
        }

        return finalMessage;
    }

}
