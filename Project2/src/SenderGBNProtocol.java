import java.util.ArrayList;

/**
 * A class which represents the protocol of Go-back-n of sender's transport layer
 */
public class SenderGBNProtocol extends SenderTransport {
    private int nextSeqNum;
    private int base;
    private ArrayList<Packet> packetArrayList;
    private int timeOut;
    /**
     * Constructor of Go-Back-n sender protocol
     * @param network layer
     * @param timeline
     * @param size of window
     * @param timeout 
     */
    public SenderGBNProtocol(NetworkLayer nl, Timeline tl, int n, int timeOut){
        super(nl, tl, n);
        this.timeOut = timeOut;
    }

    /**
     * initialize the Go-back-n of sender's transport layer
     */
    public void initialize(){
        packetArrayList = new ArrayList<Packet>();
        nextSeqNum = 0;
        base = 0;
    }

    /**
     * Send message in Go-Back-n protocol
     * @param message content
     */
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
    
    /**
     * Send next packet in Go-Back-n protocol
     */
    private void sendNextPacket(){
        Packet toSend = new Packet(packetArrayList.get(nextSeqNum));
        System.out.println("SENDER GBN SENDING:     " + toSend.toString());
        nl.sendPacket(toSend, to);
        if (base == nextSeqNum) {
            tl.startTimer(timeOut, me);
        }
        nextSeqNum++;
    }

    /**
     * sender receive message in Go-Back-n protocol
     * @param packet received
     */
    public void receiveMessage(Packet pkt) {
        System.out.println("SENDER GBN RECEIVED:    " + pkt.toString());
        if (verifyPacket(pkt)) { // if packet is not corrupt
            int ackNum = pkt.getAcknum();
            if(ackNumMakesSense(ackNum)) { //if ack is in window
                base = ackNum + 1;
                if (base == nextSeqNum) {
                    tl.stopTimer(me);
                } else {
                    tl.startTimer(timeOut, me);
                }
                // try to send the next packet(s)
                sendBufferedPkts();
            }
        } else {
            // DO NOTHING
        }
    }

    /**
     * when time out expired, call resending packet 
     */
    public void timerExpired() {
        System.out.println("TIMER EXPIRED");
        tl.startTimer(timeOut, me);
        resendDueToTimeout();
    }

    /**
     * method of resending packet 
     */
    public void resendDueToTimeout(){
        for(int i = base; i < nextSeqNum; i++){
            Packet toSend = new Packet(packetArrayList.get(i));
            System.out.println("SENDER GBN RESENDING:   " + toSend.toString());
            nl.sendPacket(toSend, to);
        }
    }

    /**
     * check if the received packet correct 
     * @param packet
     */
    public boolean verifyPacket(Packet pkt){
        if(corruptionAllowed) {
            return !pkt.isCorrupt();
        } else {
            return true;
        }
    }

    /**
     * check if next packet can be sent
     */
    public boolean canSendNext(){
        if(nextSeqNum < base + windowSize && nextSeqNum < packetArrayList.size()){
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * send next packet
     */
    public void sendBufferedPkts(){
         while(canSendNext()){
            sendNextPacket();
        }
    }

    /**
     * check if the ack number correct
     */
    public boolean ackNumMakesSense(int ackNum) {
        if (ackNum < base || ackNum > nextSeqNum) {
            return false;
        } else {
            return true;
        }
    }
    
    /**
     * get seqence number of next packet
     */
    public int getNextSeqNum() {
        return nextSeqNum;
    }
    
    /**
     * the highest packet number that has not been received
     */
    public int getBase() {
        return base;
    }
    
    /**
     * get the list of packets
     */
    public ArrayList<Packet> getPacketArrayList() {
        return packetArrayList;
    }
    
    /**
     * set time out
     * @param time out
     */
    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }
}
