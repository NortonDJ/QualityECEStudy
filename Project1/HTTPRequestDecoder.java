import java.util.*;
/**
 * Created by nortondj on 2/19/17.
 */
public class HTTPRequestDecoder {
    HashMap<String, String> requestMap;
    ByteArrayHelper byteArrayHelper = new ByteArrayHelper();
    public HTTPRequestDecoder(){
        requestMap = new HashMap<String, String>();
    }
    
    public HashMap<String, String> decode(byte[] requestBytes){
        //store Method
        int i = 0;
        char check_sp = 16;
        char check_cr = 15;
        while(!(byteArrayHelper.toChar(Arrays.copyOfRange(requestBytes,i,i))==check_sp)){
            i++;
        }
        byte[] method = Arrays.copyOfRange(requestBytes,0,i-1);
        requestMap.put("method", byteArrayHelper.tostring(method));
        
        //store URL
        int j = i + 1;
        while(!(byteArrayHelper.toChar(Arrays.copyOfRange(requestBytes,j,j))==check_sp)){
            j++;
        }
        byte[] url = Arrays.copyOfRange(requestBytes,i+1,j-1);
        requestMap.put("url", byteArrayHelper.tostring(url));
        
        //store Version
        int k = j + 1;
        while(!(byteArrayHelper.toChar(Arrays.copyOfRange(requestBytes,k,k))==check_cr)){
            k++;
        }
        byte[] version = Arrays.copyOfRange(requestBytes,j+1,k-1);
        requestMap.put("version", new Float(byteArrayHelper.toFloat(version)).toString());
      
        int n = k + 2;
        while(!((byteArrayHelper.toChar(Arrays.copyOfRange(requestBytes,n,n))) == (check_cr))){
            //store header
            int m = n;
            while(!(byteArrayHelper.toChar(Arrays.copyOfRange(requestBytes,m,m))==check_sp)){
                m++;
            }
            byte[] header =  Arrays.copyOfRange(requestBytes, n ,m-1);
            requestMap.put("header", byteArrayHelper.tostring(header));
            
            
            //store value
            int x = m + 1;
            while(!(byteArrayHelper.toChar(Arrays.copyOfRange(requestBytes,x,x))==check_cr)){
                x++;
            }
            byte[] value = Arrays.copyOfRange(requestBytes, m + 1, x - 1);
            requestMap.put("value", byteArrayHelper.tostring(value));
            
            n = x + 2;
        }
        
        byte[] body = Arrays.copyOfRange(requestBytes, n+2, requestBytes.length);
        requestMap.put("body", byteArrayHelper.tostring(body));
        return requestMap;
    }
    
    public String getMethod(){
        try{
            String s = requestMap.get("method");
            return s;
        }
        catch(Exception ex){
            return "";
        }
    }
    
    public String getURL(){
        try{
            String s = requestMap.get("url");
            return s;
        }
        catch(Exception ex){
            return "";
        }
    }
    
    public Float getVersion(){
        try{
            String s = requestMap.get("version");
            float f = Float.parseFloat(s);
            return f;
        }
        catch(Exception ex){
            return 0.0f;
        }
    }
    
    public String getHeader(String header){
        try{
            String s = requestMap.get(header);
            return s;
        }
        catch(Exception ex){
            return "";
        }
    }
    
    public String getBody(){
        try{
            String s = requestMap.get("body");
            return s;
        }
        catch(Exception ex){
            return "";
        }
    }
}
