import java.util.ArrayList;

/**
 * Created by nortondj on 3/30/17.
 */
public class SenderGBNProtocol extends SenderTransport {

    public int getNextSeqNum() {
        return nextSeqNum;
    }

    public int getBase() {
        return base;
    }

    public ArrayList<Packet> getSentPkts() {
        return sentPkts;
    }

    private int nextSeqNum;
    private int base;
    private ArrayList<Packet> sentPkts;
    private static int timeOut = 15;

    public SenderGBNProtocol(NetworkLayer nl, Timeline tl, int n){
        super(nl, tl, n);
    }

    public void initialize(){
        sentPkts = new ArrayList<Packet>();
        sentPkts.add(null); // create an empty buffer at sentPkts[0]
        nextSeqNum = 1;
        base = 1;
    }

    public void sendMessage(Message msg) {
        Packet p = new Packet(msg,sentPkts.size(),-1,-1);
        sentPkts.add(p);
        if (canSendNext()) {
            sendNextPkt();
        } else {
            System.out.println("SENDER GBN BUFFERED: " + msg.getMessage());
        }
    }

    private void sendNextPkt(){
        Packet toSend = new Packet(sentPkts.get(nextSeqNum));
        System.out.println("SENDER GBN SENDING:    " + toSend.toString());
        nl.sendPacket(toSend, to);
        if (base == nextSeqNum) {
            tl.startTimer(timeOut);
        }
        nextSeqNum++;
    }

    public void receiveMessage(Packet pkt) {
        System.out.println("SENDER GBN RECEIVED:   " + pkt.toString());
        if (!verifyPacket(pkt)) {
            //DO NOTHING
        } else {
            int acknum = pkt.getAcknum();
            if(ackNumMakesSense(acknum)) {
                base = acknum + 1;
                if (base == nextSeqNum) {
                    tl.stopTimer();
                } else {
                    tl.startTimer(timeOut);
                }
                sendBufferedPkts();
            }
        }
    }

    public void timerExpired() {
        tl.startTimer(timeOut);
        for (int i = base; i < nextSeqNum; i++) {
            if (sentPkts.get(i) != null) {
                nl.sendPacket(sentPkts.get(i), to);
            }
        }
    }

    public boolean verifyPacket(Packet pkt){
        return true;
    }

    public boolean canSendNext(){
        if(nextSeqNum < base + windowSize && nextSeqNum < sentPkts.size()){
            return true;
        } else {
            return false;
        }
    }

    public void sendBufferedPkts(){
         while(canSendNext()){
            sendNextPkt();
        }
    }

    public boolean ackNumMakesSense(int acknum) {
        if (acknum < base || acknum > nextSeqNum) {
            return false;
        } else {
            return true;
        }
    }
}
