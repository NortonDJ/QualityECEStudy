import java.util.*;
import java.nio.ByteBuffer;
/**
 * Created by nortondj on 2/19/17.
 */
public class HTTPResponseDecoder {
    HashMap<String, String> responseMap;
    ByteArrayHelper byteArrayHelper = new ByteArrayHelper();
    
    public HTTPResponseDecoder(){
        responseMap = new HashMap<String, String>();
    }
    
    /**the  format of byte array received: Version|sp|status|sp|phrase|cr|lf|
      *                                    header|sp|value|cr|lf|
      *                                    ...
      *                                    header|sp|value|cr|lf|
      *                                    cr|lf|
      *                                    body
      */
    public void decode(byte[] responseBytes){                       
        //store version
                if(responseBytes.length == 0){
            System.out.println("HTTPResponseDecoder EMPTY");
        }
        int i = 0;
        char check_sp = 16;
        char check_cr = 15;
        while(responseBytes[i]!=check_sp){
            i++;
        }
        byte[] version = Arrays.copyOfRange(responseBytes,0,i-1);
        responseMap.put("version", byteArrayHelper.tostring(version));
        
        //store status
        int j = i + 1;
        while(responseBytes[j]!=check_sp){
            j++;
        }
        byte[] status = Arrays.copyOfRange(responseBytes, i+1, j-1);
        responseMap.put("status", byteArrayHelper.tostring(status));
        
        //store phrase
        int k = j + 1;
        while(responseBytes[k]!=check_cr){
            k++;
        }
        byte[] phrase = Arrays.copyOfRange(responseBytes,j+1,k-1);
        responseMap.put("phrase", byteArrayHelper.tostring(phrase));
        
        int n = k + 2;
        while(responseBytes[n]!= (check_cr)){
            //store header
            int m = n;
            while(responseBytes[m]!=check_sp){
                m++;
            }
            byte[] header =  Arrays.copyOfRange(responseBytes, n ,m-1);
            responseMap.put("header", byteArrayHelper.tostring(header));
            
            
            //store value
            int x = m + 1;
            while(responseBytes[x]!=check_cr){
                x++;
            }
            byte[] value = Arrays.copyOfRange(responseBytes, m + 1, x - 1);
            responseMap.put("value", byteArrayHelper.tostring(value));
            
            n = x + 2;
        }
        
        byte[] body = Arrays.copyOfRange(responseBytes, n+2, responseBytes.length);
        responseMap.put("body", byteArrayHelper.tostring(body));
    }
    
    public String getMethod(){
        try{
            String s = responseMap.get("method");
            return s;
        }
        catch(Exception ex){
            return "";
        }
    }
    
    public String getURL(){
        try{
            String s = responseMap.get("url");
            return s;
        }
        catch(Exception ex){
            return "";
        }
    }
    
    public String getVersion(){
        try{
            String s = responseMap.get("version");
            return s;
        }
        catch(Exception ex){
            return "";
        }
    }
    
    public String getHeader(){
        try{
            String s = responseMap.get("header");
            return s;
        }
        catch(Exception ex){
            return "";
        }
    }
    
    public String getValue(){
        try{
            String s = responseMap.get("value");
            return s;
        }
        catch(Exception ex){
            return "";
        }
    }
    
    public String getBody(){
        try{
            String s = responseMap.get("body");
            return s;
        }
        catch(Exception ex){
            return "";
        }
    }
}
