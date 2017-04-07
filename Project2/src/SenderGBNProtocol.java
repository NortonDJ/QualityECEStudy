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
    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    private int nextSeqNum;
    private int base;
    private ArrayList<Packet> sentPkts;
    private int timeOut;

    public SenderGBNProtocol(NetworkLayer nl, Timeline tl, int n, int timeOut){
        super(nl, tl, n);
        this.timeOut = timeOut;
    }

    public void initialize(){
        sentPkts = new ArrayList<Packet>();
        nextSeqNum = 0;
        base = 0;
    }

    public void sendMessage(Message msg) {
        Packet p = new Packet(msg,sentPkts.size(),-1,-1);
        sentPkts.add(p);
        if (canSendNext()) {
            sendNextPacket();
        } else {
            System.out.println("SENDER GBN BUFFERED:  " + msg.getMessage());
        }
    }

    private void sendNextPacket(){
        Packet toSend = new Packet(sentPkts.get(nextSeqNum));
        System.out.println("SENDER GBN SENDING:     " + toSend.toString());
        nl.sendPacket(toSend, to);
        if (base == nextSeqNum) {
            tl.startTimer(timeOut);
        }
        nextSeqNum++;
    }

    public void receiveMessage(Packet pkt) {
        System.out.println("SENDER GBN RECEIVED:    " + pkt.toString());
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
        System.out.println("TIMER EXPIRED");
        tl.startTimer(timeOut);
        resendDueToTimeout();
    }

    public void resendDueToTimeout(){
        for(int i = base; i < nextSeqNum; i++){
            Packet toSend = new Packet(sentPkts.get(i));
            System.out.println("SENDER GBN RESENDING:   " + toSend.toString());
            nl.sendPacket(toSend, to);
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
            sendNextPacket();
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
