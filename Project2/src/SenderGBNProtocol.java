import java.util.ArrayList;

/**
 * Created by nortondj on 3/30/17.
 */
public class SenderGBNProtocol extends SenderTransport {

    private int nextSeqNum;
    private int base;
    private ArrayList<Packet> sentPkts;
    private static int timeOut = 100;

    public SenderGBNProtocol(NetworkLayer nl, Timeline tl, int n){
        super(nl, tl, n);
    }

    public void initialize(){
        sentPkts = new ArrayList<Packet>();
        for(int i = 0; i < tl.getTotalMessagesToSend(); i++){
            sentPkts.add(null);
        }
        nextSeqNum = 0;
        base = 0;
    }

    public void sendMessage(Message msg) {
        if (nextSeqNum < base + windowSize) {
            sentPkts.set(nextSeqNum, new Packet(msg, nextSeqNum, -1, -1));
            nl.sendPacket(sentPkts.get(nextSeqNum), to);
            if (base == nextSeqNum) {
                tl.startTimer(timeOut);
            }
            nextSeqNum++;
        } else {
            return;
        }
    }

    public void receiveMessage(Packet pkt) {
        //TODO make a method that checks if a pkt is corrupt
        System.out.println("SENDER GBN RECEIVED: " + pkt.toString());
        if (!verifyPacket(pkt)) {
            //DO NOTHING
        } else {
            base = pkt.getAcknum() + 1;
            if (base == nextSeqNum) {
                tl.stopTimer();
            } else {
                tl.startTimer(timeOut);
            }
        }
    }

    public void timerExpired() {
        tl.startTimer(timeOut);
        for (int i = base; i < nextSeqNum; i++) {  //TODO maybe base+nextSeqNum?
            if (sentPkts.get(i) != null) {
                nl.sendPacket(sentPkts.get(i), to);
            }
        }
    }

    public boolean verifyPacket(Packet pkt){
        return true;
    }
}
