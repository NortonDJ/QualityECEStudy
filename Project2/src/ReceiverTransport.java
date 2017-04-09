
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
    protected boolean corruptionAllowed = true;

    /**
     * Constructor of Go-Back-n receiver protocol
     * @param network layer
     * @param receiver application
     * @param timeout 
     */
    public ReceiverTransport(NetworkLayer nl, ReceiverApplication ra, int windowSize){
        this.ra = ra;
        this.nl=nl;
        this.windowSize = windowSize;
        initialize();
    }

    /**
     * Set if corruption is allowed in this layer
     * @param boolean corruptionAllowed
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
