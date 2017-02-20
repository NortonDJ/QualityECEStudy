import java.util.*;
/**
* This class contains method for HTTP to decompose the requesst message it received, and stored all information in a
* hashmap. Then, we can use the hash map to get all kinds of information we need.
* 
* @author  Darren Norton, Yizhong Chen
* @since   Feb-19th-2017 
*/
public class HTTPRequestDecoder {
    HashMap<String, String> requestMap;
    ByteArrayHelper byteArrayHelper = new ByteArrayHelper();
    public HTTPRequestDecoder(){
        requestMap = new HashMap<String, String>();
    }
    
    /**
     * This method decode the request message(byte array) it received according to the following format
     * the  format of byte array received: method|sp|URL|sp|version|cr|lf|
     *                                     header|sp|value|cr|lf|
     *                                     ...
     *                                     header|sp|value|cr|lf|
     *                                     cr|lf|
     *                                     body
     * @param responseBytes
     */   
    public HashMap<String, String> decode(byte[] requestBytes){
        //store Method
        if(requestBytes.length == 0){
            System.out.println("HTTPRequestDecoder EMPTY");
        }
        
        int i = 0;
        char check_sp = 16;
        char check_cr = 15;
        
        while(requestBytes[i]!=check_sp){
            i++;
        }
        byte[] method = Arrays.copyOfRange(requestBytes,0,i);
        requestMap.put("method", byteArrayHelper.tostring(method));
        
        //store URL
        int j = i + 1;
        while(requestBytes[j]!=check_sp){
            j++;
        }
        byte[] url = Arrays.copyOfRange(requestBytes,i+1,j);
        requestMap.put("url", byteArrayHelper.tostring(url));
        
        //store Version
        int k = j + 1;
        while(requestBytes[k]!=check_cr){
            k++;
        }
        byte[] version = Arrays.copyOfRange(requestBytes,j+1,k);
        requestMap.put("version", new Float(byteArrayHelper.toFloat(version)).toString());
      
        int n = k + 2;
        while(requestBytes[n]!= check_cr){
            //store header
            int m = n;
            while(requestBytes[m]!=check_sp){
                m++;
            }
            byte[] header =  Arrays.copyOfRange(requestBytes, n ,m-1);
            
            
            //store value
            int x = m + 1;
            while(requestBytes[x]!=check_cr){
                x++;
            }
            byte[] value = Arrays.copyOfRange(requestBytes, m + 1, x - 1);
            requestMap.put(byteArrayHelper.tostring(header), byteArrayHelper.tostring(value));
            
            n = x + 2;
        }
        
        byte[] body = Arrays.copyOfRange(requestBytes, n+2, requestBytes.length);
        requestMap.put("body", byteArrayHelper.tostring(body));
        return requestMap;
    }
    
    /**
             * Get 'method' information from the byte array received
             * 
             * @return method. if there is no such thing, return empty string
             */
    public String getMethod(){
        try{
            String s = requestMap.get("method");
            return s;
        }
        catch(Exception ex){
            return "";
        }
    }
    
    /**
             * Get 'URL' information from the byte array received
             * 
             * @return URL. if there is no such thing, return empty string
             */
    public String getURL(){
        try{
            String s = requestMap.get("url");
            return s;
        }
        catch(Exception ex){
            return "";
        }
    }
    
    /**
             * Get 'version' information from the byte array received
             * 
             * @return version. if there is no such thing, return empty string
             */
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
    
    /**
             * Get 'header' information from the byte array received
             * 
             * @return header. if there is no such thing, return empty string
             */
    public String getHeader(String header){
        try{
            String s = requestMap.get(header);
            if(s == null){
                return "";
            }
            return s;
        }
        catch(Exception ex){
            return "";
        }
    }
    
    /**
             * Get 'value' information from the byte array received
             * 
             * @return value. if there is no such thing, return empty string
             */
    public String getValue(){
        try{
            String s = requestMap.get("value");
            return s;
        }
        catch(Exception ex){
            return "";
        }
    }
    
    /**
             * Get 'value' information from the byte array received
             * 
             * @return value. if there is no such thing, return empty string
             */
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
