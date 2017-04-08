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

    /**
     * Constructor of sender transport layer
     * @param network layer
     * @param timeline
     * @param size of window
     */
    public SenderTransport(NetworkLayer nl, Timeline tl, int n) {
        this.nl = nl;
        this.tl = tl;
        this.windowSize = n;
        initialize();
    }

    /**
     * Set if corruption is allowed in this layer
     */
    public void enableCorruption(boolean corruptionAllowed){
        this.corruptionAllowed = corruptionAllowed;
    }

    /**
     * Generate check sum for each message
     * @param message
     * @param sequence number
     * @param acknowledge number
     */
    public int generateCheckSum(Message m, int seqNum, int ackNum){
        int checkSum = seqNum + ackNum;
        String s = m.getMessage();
        for(char c : s.toCharArray()){
            checkSum += c;
        }
        return checkSum;
    }

}
