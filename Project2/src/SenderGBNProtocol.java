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
    public ArrayList<Packet> getPacketArrayList() {
        return packetArrayList;
    }
    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    private int nextSeqNum;
    private int base;
    private ArrayList<Packet> packetArrayList;
    private int timeOut;

    public SenderGBNProtocol(NetworkLayer nl, Timeline tl, int n, int timeOut){
        super(nl, tl, n);
        this.timeOut = timeOut;
    }

    public void initialize(){
        packetArrayList = new ArrayList<Packet>();
        nextSeqNum = 0;
        base = 0;
    }

    public void sendMessage(Message msg) {
        int seqNum = packetArrayList.size();
        int ackNum = -1;
        Packet p = new Packet(msg, seqNum, ackNum, generateCheckSum(msg,seqNum,ackNum));
        packetArrayList.add(p);
        if (canSendNext()) {
            sendNextPacket();
        } else {
            System.out.println("SENDER GBN BUFFERED:  " + msg.getMessage());
        }
    }

    private void sendNextPacket(){
        Packet toSend = new Packet(packetArrayList.get(nextSeqNum));
        System.out.println("SENDER GBN SENDING:     " + toSend.toString());
        nl.sendPacket(toSend, to);
        if (base == nextSeqNum) {
            tl.startTimer(timeOut, me);
        }
        nextSeqNum++;
    }

    public void receiveMessage(Packet pkt) {
        System.out.println("SENDER GBN RECEIVED:    " + pkt.toString());
        if (verifyPacket(pkt)) {
            int ackNum = pkt.getAcknum();
            if(ackNumMakesSense(ackNum)) {
                base = ackNum + 1;
                if (base == nextSeqNum) {
                    tl.stopTimer(me);
                } else {
                    tl.startTimer(timeOut, me);
                }
                sendBufferedPkts();
            }
        } else {
            // DO NOTHING
        }
    }

    public void timerExpired() {
        System.out.println("TIMER EXPIRED");
        tl.startTimer(timeOut, me);
        resendDueToTimeout();
    }

    public void resendDueToTimeout(){
        for(int i = base; i < nextSeqNum; i++){
            Packet toSend = new Packet(packetArrayList.get(i));
            System.out.println("SENDER GBN RESENDING:   " + toSend.toString());
            nl.sendPacket(toSend, to);
        }
    }

    public boolean verifyPacket(Packet pkt){
        if(corruptionAllowed) {
            return !pkt.isCorrupt();
        } else {
            return true;
        }
    }

    public boolean canSendNext(){
        if(nextSeqNum < base + windowSize && nextSeqNum < packetArrayList.size()){
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

    public boolean ackNumMakesSense(int ackNum) {
        if (ackNum < base || ackNum > nextSeqNum) {
            return false;
        } else {
            return true;
        }
    }
}
