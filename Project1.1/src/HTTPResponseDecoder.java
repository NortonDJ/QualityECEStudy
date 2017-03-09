import java.util.*;
import java.nio.ByteBuffer;

/**
 * This class contains method for HTTP to decompose the response message it received, and stored all information in a
 * hashmap. Then, we can use the hash map to get all kinds of information we need.
 *
 * @author Darren Norton, Yizhong Chen
 * @since Feb-19th-2017
 */
public class HTTPResponseDecoder {

    public HTTPResponseDecoder() {

    }

    /**
     * This method decode the response message(byte array) it received according to the following format
     * the  format of byte array received: Version|sp|status|sp|phrase|cr|lf|
     * header|sp|value|cr|lf|
     * ...
     * header|sp|value|cr|lf|
     * cr|lf|
     * body
     *
     * @param responseBytes
     */
    public HTTPResponse decode(byte[] responseBytes) {
        //store version

        int i = 0;
        char check_sp = 16;
        char check_cr = 15;
        while (responseBytes[i] != check_sp) {
            i++;
        }
        byte[] version = Arrays.copyOfRange(responseBytes, 0, i - 1);
        float fversion = ByteArrayHelper.toFloat(version);

        //store status
        int j = i + 1;
        while (responseBytes[j] != check_sp) {
            j++;
        }
        byte[] status = Arrays.copyOfRange(responseBytes, i + 1, j);
        int statusCode = ByteArrayHelper.toInt(status);

        //store phrase
        int k = j + 1;
        while (responseBytes[k] != check_cr) {
            k++;
        }
        byte[] phrase = Arrays.copyOfRange(responseBytes, j + 1, k);
        String strPhrase = ByteArrayHelper.tostring(phrase);

        HTTPResponse resp = new HTTPResponse(fversion, statusCode, strPhrase);
        int n = k + 2;
        while (responseBytes[n] != (check_cr)) {
            //store header
            int m = n;
            while (responseBytes[m] != check_sp) {
                m++;
            }
            byte[] header = Arrays.copyOfRange(responseBytes, n, m - 1);
            String headerStr = ByteArrayHelper.tostring(header);

            //store value
            int x = m + 1;
            while (responseBytes[x] != check_cr) {
                x++;
            }
            byte[] value = Arrays.copyOfRange(responseBytes, m + 1, x - 1);
            String valueStr = ByteArrayHelper.tostring(value);
            resp.mapHeader(headerStr, valueStr);
            n = x + 2;
        }

        byte[] body = Arrays.copyOfRange(responseBytes, n + 2, responseBytes.length);
        resp.setBody(ByteArrayHelper.tostring(body));
        return resp;
    }

}
