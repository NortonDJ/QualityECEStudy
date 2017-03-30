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
            case (0) : return new SenderGBNProtocol(this.nl, this.tl, windowSize);

            case (1) : //continue down

            default : return new SenderTCPProtocol(this.nl, this.tl, windowSize);
        }
    }

}
