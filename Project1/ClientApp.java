
import com.sun.security.ntlm.Client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

//This class represents the client application
public class ClientApp {
    private float httpversion;
    private boolean connected;
    private TransportLayer tl;
    public static void main(String[] args) throws Exception {
        float httpversion;

        switch (args.length) {
            case 1:
                try {
                    httpversion = Float.parseFloat(args[0]);
                } catch (Exception e) {
                    httpversion = 1.0f;
                    e.printStackTrace();
                }
                break;
            default:
                httpversion = 1.0f;
        }
        //create a new transport layer for client (hence false) (connect to server), and read in first line from keyboard
        TransportLayer transportLayer = new TransportLayer(false, 0, 0);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line = reader.readLine();

        //while line is not empty
        while (line != null && !line.equals("")) {
            //convert lines into byte array, send to transport layer and wait for response
            byte[] byteArray = line.getBytes();
            transportLayer.send(byteArray);
            byteArray = transportLayer.receive();
            String str = new String(byteArray);
            System.out.println(str);
            //read next line
            line = reader.readLine();
        }
    }

    public ClientApp(float httpversion) {
        this.httpversion = httpversion;
        this.tl = new TransportLayer(false, 0, 0);
    }

    public void send(byte[] payload) {
        tl.send(payload);
    }

}
