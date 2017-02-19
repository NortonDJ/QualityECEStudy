import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by nortondj on 2/19/17.
 */
public class HTTPResponseBuilder {
    private HashMap<String, String> headerLines;
    private ArrayList<Byte> fullMessage;

    public HTTPResponseBuilder(){
        this.headerLines = new HashMap<String, String>();
        this.fullMessage = new ArrayList<Byte>();
    }

    public byte[] build(float version, int statusCode, String phrase){

        //Add the status line
        putStatusLine(version, statusCode, phrase);

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

    public void putHeaderLine(String header, String value) {

        // Add the header bytes
        byte[] headerBytes = ByteArrayHelper.toByteArray(header);
        for(byte b : headerBytes){
            fullMessage.add(Byte.valueOf(b));
        }

        fullMessage.add(ByteArrayHelper.SP);

        // Add the value bytes
        byte[] valueBytes = ByteArrayHelper.toByteArray(value);
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
