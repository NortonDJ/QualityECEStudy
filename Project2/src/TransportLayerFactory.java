/**
 * Created by nortondj on 3/30/17.
 */
public class TransportLayerFactory {

    private NetworkLayer nl;
    private Timeline tl;

    public TransportLayerFactory(NetworkLayer nl, Timeline tl){
        this.nl = nl;
        this.tl = tl;
    }

    public SenderTransport makeSender(int protocol, int windowSize){
        switch(protocol){
            case (0) :
                System.out.println("Setting Sender Transport protocol to GBN.");
                return new SenderGBNProtocol(this.nl, this.tl, windowSize);

            case (1) : //continue down

            default :
                System.out.println("Setting Sender Transport protocol to TCP.");
                return new SenderTCPProtocol(this.nl, this.tl, windowSize);
        }
    }

    public ReceiverTransport makeReceiver(int protocol, int windowSize, ReceiverApplication ra){
        switch(protocol){
            case (0) :
                System.out.println("Setting Receiver Transport protocol to GBN.");
                return new ReceiverGBNProtocol(this.nl, ra, windowSize);
            case (1) : //continue down
            default :
                System.out.println("Setting Receiver Transport protocol to TCP.");
                return new ReceiverTCPProtocol(this.nl, ra, windowSize);
        }
    }

}
