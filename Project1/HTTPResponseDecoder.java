import java.util.*;
import java.nio.ByteBuffer;
/**
 * Created by nortondj on 2/19/17.
 */
public class HTTPResponseDecoder {
    byte[] originalArray;
    ArrayList<byte[]> splitedStatus;
    
    //float version;
    //int sp, cr, lf;
    //int status_code;
    //String phrase;
    //String header;
    //String value;
    //String body;
    public HTTPResponseDecoder(){
        //this.originalArray = array;
        splitedStatus = new ArrayList<byte[]>();
    }
    
    public byte[] decode(byte[] response){                       
        //byte array -> float
        //return ByteBuffer.wrap(response).getFloat();
        this.originalArray = response;
        return new byte[0];
    }

    public static void main(String[] args){
        float version1 = 1.0f;
        int sp1 = 16;
        byte[] array1 = ByteArrayHelper.toByteArray(version1);
        HTTPResponseDecoder h1 = new HTTPResponseDecoder();
        //System.out.println(array1[0]);
        //System.out.println(array1[1]);
        //System.out.println(array1[2]);
        //System.out.println(array1[3]);
        System.out.println(h1.decode(array1));
    }
    
    /**the  format of byte array received: Version|sp|status|sp|phrase|cr|lf|
      *                                    header|sp|value|cr|lf|
      *                                    ...
      *                                    header|sp|value|cr|lf|
      *                                    cr|lf|
      *                                    Entire body
      */  
    public ArrayList<byte[]> split_status(){
        //status line
        byte[] version = Arrays.copyOfRange(originalArray,0,3);
        splitedStatus.add(version);
        byte[] sp1 = Arrays.copyOfRange(originalArray,4,4);
        splitedStatus.add(sp1);
        byte[] status = Arrays.copyOfRange(originalArray,5,8);
        splitedStatus.add(status);
        byte[] sp2 = Arrays.copyOfRange(originalArray, 9,9);
        splitedStatus.add(sp2);
        byte[] phrase = Arrays.copyOfRange(originalArray, 10,11);
        splitedStatus.add(phrase);
        byte[] cr = Arrays.copyOfRange(originalArray,12,12);
        splitedStatus.add(cr);
        byte[] lf = Arrays.copyOfRange(originalArray,13,13);
        splitedStatus.add(lf);
        return splitedStatus;
    }
        //Headerline
}
