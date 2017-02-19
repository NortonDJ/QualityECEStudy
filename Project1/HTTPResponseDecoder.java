import java.util.*;
import java.nio.ByteBuffer;
/**
 * Created by nortondj on 2/19/17.
 */
public class HTTPResponseDecoder {
    float version;
    int sp, cr, lf;
    int status_code;
    String phrase;
    String header;
    String value;
    String body;
    
    public byte[] decode(byte[] response){
        //the byte array received: Version|sp|status|sp|phrase|cr|lf|
        //                         header|sp|value|cr|lf|
        //                         header|sp|value|cr|lf|
        //                         cr|lf|
        //                         Entire body
        
        //byte array -> float
        //return ByteBuffer.wrap(response).getFloat();
        return new byte[0];
    }
<<<<<<< HEAD
    
    public byte[] toByteArray(float value) {    
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putFloat(value);
        return buffer.array();
    }
    
=======

>>>>>>> aae0c7b77bcbf7258556e13b9f594788bb919b0e
    public static void main(String[] args){
        float version1 = 1.0f;
        int sp
        HTTPResponseDecoder h1 = new HTTPResponseDecoder();
        byte[] array1 = ByteArrayHelper.toByteArray(version1);
        //System.out.println(array1[0]);
        //System.out.println(array1[1]);
        //System.out.println(array1[2]);
        //System.out.println(array1[3]);
        System.out.println(h1.decode(array1));
    }
}
