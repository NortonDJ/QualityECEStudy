
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

    public ReceiverTransport(NetworkLayer nl, ReceiverApplication ra, int windowSize){
        this.ra = ra;
        this.nl=nl;
        this.windowSize = windowSize;
        initialize();
    }

    public void enableCorruption(boolean corruptionAllowed){
        this.corruptionAllowed = corruptionAllowed;
    }

    public int generateCheckSum(Message m, int seqNum, int ackNum){
        int checkSum = seqNum + ackNum;
        String s = m.getMessage();
        for(char c : s.toCharArray()){
            checkSum += c;
        }
        return checkSum;
    }

}
