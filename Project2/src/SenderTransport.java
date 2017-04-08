import java.util.ArrayList;

/**
 * A class which represents the sender transport layer
 */
public abstract class SenderTransport implements Protocol {
    protected NetworkLayer nl;
    protected Timeline tl;
    protected int windowSize;
    protected static int to = Event.RECEIVER;
    protected static int me = Event.SENDER;
    protected boolean corruptionAllowed = true;

    public SenderTransport(NetworkLayer nl, Timeline tl, int n) {
        this.nl = nl;
        this.tl = tl;
        this.windowSize = n;
        initialize();
    }

    public void enableCorruption(boolean corruptionAllowed){
        this.corruptionAllowed = corruptionAllowed;
    }

}
