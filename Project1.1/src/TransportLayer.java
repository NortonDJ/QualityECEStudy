import java.util.Arrays;

/**
 * This class simulates the Transport layer, which is conneccted with network layer
 * In this layer, it simulates the transmission delay and propagation delay
 * <p>
 * This contains two methods: if the application is a server (should listen) or
 * false if it is a client (should try and connect)
 *
 * @author Darren Norton, Yizhong Chen
 * @since Feb-19th-2017
 */
public class TransportLayer {
    private boolean connected;
    private NetworkLayer networkLayer;
    private int dtrans;
    private int dprop;

    /**
     * This is a constructor for Transport Layer.
     * Server is true if the application is a server (should listen) or
     * false if it is a client (should try and connect);
     * Get the delay time and pass it to transport layer.
     *
     * @param boolean (server/client), transmission delay and propagation delay
     */
    public TransportLayer(boolean server, int dprop, int dtrans) {
        this.connected = false;
        this.dtrans = dtrans;
        this.dprop = dprop;
        networkLayer = new NetworkLayer(server, dprop, dtrans);
    }

    /**
     * if this application is a client that is not connected with server,
     * it will try to connect with server and send request.
     *
     * @return byte message
     */
    public void send(byte[] payload) {
        if (connected == false) {
            connect();
            System.out.println("Sending our original message!\n");
            System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&\n");
        }
        networkLayer.send(payload);
    }

    /**
     * if this application is a server and receive a 'syn' from the client,
     * it will try to read and send back 'syn-ack'.
     *
     * @param byte message
     */
    public byte[] receive() {
        byte[] payload = networkLayer.receive();
        if (Arrays.equals(payload, ByteArrayHelper.syn)) {
            System.out.println("We received a syn! Sending the syn-ack!");
            networkLayer.send(ByteArrayHelper.synack);
            connected = true;
            payload = networkLayer.receive();
        } else {

        }
        return payload;
    }

    /**
     * try to connect client to server, keep sending 'syn' until it received a 'syn-ack'.
     */
    public void connect() {
        System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
        System.out.println("Attempting to form connection with server.");
        byte[] listen;
        do {
            System.out.println("Sending a syn.");
            networkLayer.send(ByteArrayHelper.syn);
            listen = networkLayer.receive();
        } while (Arrays.equals(ByteArrayHelper.synack, listen) == false);
        System.out.println("Received the synack! We're connected!");
        connected = true;
    }

    /**
     * disconnect client from server
     */
    public void disconnect() {
        this.connected = false;
    }

    /**
     * print out the byte message
     *
     * @param byte message
     */
    public void printBytes(byte[] payload) {
        String s = "[";
        for (int i = 0; i < payload.length; i++) {
            s += payload[i] + ",";
        }
        s += "]";
        System.out.println(s);
    }

}
