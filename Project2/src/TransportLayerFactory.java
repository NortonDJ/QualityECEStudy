/**
 * This class creates receiver and sender
 */
public class TransportLayerFactory {

    private NetworkLayer nl;
    private Timeline tl;

    /**
     * Constructor of TransportLayerFactory
     *
     * @param network  layer
     * @param timeline
     */
    public TransportLayerFactory(NetworkLayer nl, Timeline tl) {
        this.nl = nl;
        this.tl = tl;
    }

    /**
     * create sender
     *
     * @param protocol type
     * @param size     of window
     * @param timeout
     * @return SenderTransport
     */
    public SenderTransport makeSender(int protocol, int windowSize, int timeOut) {
        switch (protocol) {
            case (0):
                System.out.println("Setting Sender Transport protocol to GBN.");
                return new SenderGBNProtocol(this.nl, this.tl, windowSize, timeOut);

            case (1): //continue down

            default:
                System.out.println("Setting Sender Transport protocol to TCP.");
                return new SenderTCPProtocol(this.nl, this.tl, windowSize, timeOut);
        }
    }

    /**
     * create receiver
     *
     * @param protocol type
     * @param size     of window
     * @param receiver application
     * @param timeout
     * @return ReceiverTransport
     */
    public ReceiverTransport makeReceiver(int protocol, int windowSize, ReceiverApplication ra, int timeOut) {
        switch (protocol) {
            case (0):
                System.out.println("Setting Receiver Transport protocol to GBN.");
                return new ReceiverGBNProtocol(this.nl, ra, windowSize);
            case (1): //continue down
            default:
                System.out.println("Setting Receiver Transport protocol to TCP.");
                return new ReceiverTCPProtocol(this.nl, ra, windowSize, timeOut);
        }
    }

}
