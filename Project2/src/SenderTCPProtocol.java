import java.util.ArrayList;

/**
 * Created by nortondj on 3/30/17.
 */
public class SenderTCPProtocol extends SenderTransport {

    private int dupACKCount; //counter from 0 to 3, 0 meaning ack received not dup
    private int dupACKNum;  //tracker for dup ack's acknum

    public int getDupACKCount() {
        return dupACKCount;
    }

    public int getDupACKNum() {
        return dupACKNum;
    }

    public int getNextSeqNum() {
        return nextSeqNum;
    }

    public int getBase() {
        return base;
    }

    public int getRound() {
        return round;
    }

    public boolean isStarted() {
        return started;
    }

    private ArrayList<Packet> sentPkts;
    private int timeOut;
    private int nextSeqNum;
    private int base;
    private int round;
    private boolean started;

    public SenderTCPProtocol(NetworkLayer nl, Timeline tl, int n, int timeOut){
        super(nl, tl, n);
        this.timeOut = timeOut;
    }

    public void initialize() {
        dupACKCount = -1;
        dupACKNum = -1;
        nextSeqNum = 0;
        round = 0;
        base = 0;
        started = false;
        sentPkts = new ArrayList<Packet>();
    }

    public void sendMessage(Message msg) {
        Packet p = new Packet(msg,sentPkts.size(),-1,-1);
        sentPkts.add(p);
        if (!started) {
            Packet toSend = new Packet(sentPkts.get(nextSeqNum));
            System.out.println("SENDER TCP SENDING:     " + toSend.toString());
            nl.sendPacket(toSend, to);
            started = true;
            tl.startTimer(timeOut);
            nextSeqNum++;
        } else {
            System.out.println("SENDER TCP BUFFERED:  " + msg.getMessage());
        }
    }


    public void receiveMessage(Packet pkt) {
        System.out.println("SENDER TCP RECEIVED:    " + pkt.toString());
        if (!verifyPacket(pkt)) {
            //DO NOTHING
        } else {
            int acknum = pkt.getAcknum();
            if(ackIsDuplicate(acknum)) {
                dupACKCount++;
                if(dupACKCount == 3){
                    //fastRetransmit will reset duplicate ack count
                    fastRetransmit();
                } else {
                    // DO NOTHING, dupACKCount has already by incremented
                }
            } else { // ACK IS NOT DUPLICATE
                if(ackNumMakesSense(acknum)) {
                    trackAck(acknum);
                    base = acknum + 1;
                    if (base == nextSeqNum) {
                        finishRound();
                        beginNextRound();
                    } else {
                        // DO NOTHING
                    }
                } else {
                    // DO NOTHING, if the ack doesnt make sense, ignore it
                }
            }
        }
    }

    public void timerExpired() {}

    public boolean verifyPacket(Packet pkt){
        return true;
    }

    public void fastRetransmit(){}

    public void finishRound(){
        round++;
    }

    public void beginNextRound(){
        for(int i = 0; i < Math.pow(2,round) && nextSeqNum < sentPkts.size(); i++){
            sendNextPacket();
        }
    }

    public void sendNextPacket(){
        Packet toSend = new Packet(sentPkts.get(nextSeqNum));
        System.out.println("SENDER TCP SENDING:     " + toSend.toString());
        nl.sendPacket(toSend, to);
        if (base == nextSeqNum) {
            tl.startTimer(timeOut);
        }
        nextSeqNum++;
    }

    public void trackAck(int acknum){
        dupACKNum = acknum;
        dupACKCount = 0;
    }

    public boolean ackIsDuplicate(int acknum){
        if(acknum == dupACKNum && dupACKCount >= 0) {
            return true;
        } else {
            return false;
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
