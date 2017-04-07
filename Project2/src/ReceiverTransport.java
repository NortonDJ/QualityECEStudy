
/**
 * A class which represents the receiver transport layer
 */
public abstract class ReceiverTransport implements Protocol
{
    protected ReceiverApplication ra;
    protected NetworkLayer nl;
    protected int windowSize;
    protected static int to = Event.SENDER;
    protected static int me = Event.RECEIVER;

    public ReceiverTransport(NetworkLayer nl, ReceiverApplication ra, int windowSize){
        this.ra = ra;
        this.nl=nl;
        this.windowSize = windowSize;
        initialize();
    }

}
