
/**
 * A class which represents the receiver transport layer
 */
public abstract class ReceiverTransport implements Protocol
{
    protected ReceiverApplication ra;
    protected NetworkLayer nl;
    protected int windowSize;
    protected static int to = 2;

    public ReceiverTransport(NetworkLayer nl, ReceiverApplication ra, int windowSize){
        this.ra = ra;
        this.nl=nl;
        this.windowSize = windowSize;
        initialize();
    }

}
