/**
 * Created by nortondj on 2/19/17.
 */
public class HTTP{
    private HTTPRequestBuilder requestBuilder;
    private HTTPResponseBuilder responseBuilder;
    private HTTPRequestDecoder requestDecoder;
    private HTTPResponseDecoder responseDecoder;


    public HTTP(){
        requestBuilder = new HTTPRequestBuilder();
        responseBuilder = new HTTPResponseBuilder();
        requestDecoder = new HTTPRequestDecoder();
        responseDecoder = new HTTPResponseDecoder();
    }

    public byte[] buildResponse(){
        return responseBuilder.build();
    }

    public byte[] buildRequest(String filename, float httpversion){
        return requestBuilder.build();
    }

    public byte[] decodeResponse(byte[] response){
        return responseDecoder.decode();
    }

    public byte[] decodeRequest(byte[] request){
        return requestDecoder.decode();
    }


}
