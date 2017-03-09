import java.util.*;
/**
* This class contains method for HTTP to decompose the requesst message it received, and stored all information in a
* hashmap. Then, we can use the hash map to get all kinds of information we need.
* 
* @author  Darren Norton, Yizhong Chen
* @since   Feb-19th-2017 
*/
public class HTTPRequestDecoder {
    public HTTPRequestDecoder() {
    }

    /**
     * This method decode the request message(byte array) it received according to the following format
     * the  format of byte array received: method|sp|URL|sp|version|cr|lf|
     * header|sp|value|cr|lf|
     * ...
     * header|sp|value|cr|lf|
     * cr|lf|
     * body
     *
     * @param responseBytes
     */
    public HTTPRequest decode(byte[] requestBytes) {
        //store Method
        int i = 0;
        while (requestBytes[i] != ByteArrayHelper.SP) {
            i++;
        }
        byte[] method = Arrays.copyOfRange(requestBytes, 0, i);
        String methodStr = ByteArrayHelper.tostring(method);

        //store URL
        int j = i + 1;
        while (requestBytes[j] != ByteArrayHelper.SP) {
            j++;
        }
        byte[] url = Arrays.copyOfRange(requestBytes, i + 1, j);
        String urlStr = ByteArrayHelper.tostring(url);

        //store Version
        int k = j + 1;
        while (requestBytes[k] != ByteArrayHelper.CR) {
            k++;
        }
        byte[] version = Arrays.copyOfRange(requestBytes, j + 1, k);
        float fversion = ByteArrayHelper.toFloat(version);

        HTTPRequest req = new HTTPRequest(methodStr, urlStr, fversion);
        int n = k + 2;
        while (requestBytes[n] != ByteArrayHelper.CR) {
            //store header
            int m = n;
            while (requestBytes[m] != ByteArrayHelper.SP) {
                m++;
            }
            byte[] header = Arrays.copyOfRange(requestBytes, n, m);
            String headerStr = ByteArrayHelper.tostring(header);

            //store value
            int x = m + 1;
            while (requestBytes[x] != ByteArrayHelper.CR) {
                x++;
            }
            byte[] value = Arrays.copyOfRange(requestBytes, m + 1, x);
            String valueStr = ByteArrayHelper.tostring(value);
            req.mapHeader(headerStr, valueStr);
            n = x + 2;
        }

        byte[] body = Arrays.copyOfRange(requestBytes, n + 2, requestBytes.length);
        req.setBody(ByteArrayHelper.tostring(body));

        return req;
    }

}

