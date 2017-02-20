
/**
 * Created by nortondj on 2/20/17.
 */
public class DUMPClientApp extends ClientApp{

    public DUMPClientApp(float version){
        super(version);
    }

    public byte[] DUMRequest(String file, float httpversion){
        byte[] request = requestBuilder.build("DUM", file, httpversion);
        tl.send(request);
        byte[] response = tl.receive();
        if(response == null){
            System.out.println("RESPONSE IS NULL");
        }
        responseDecoder.decode(response);
        if(httpversion == 1.0f){
            tl.disconnect();
        }
        return response;
    }

}
