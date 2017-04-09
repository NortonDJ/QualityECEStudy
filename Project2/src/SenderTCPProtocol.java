import java.util.ArrayList;

/**
 * A class which represents the protocol of TCP of sender's transport layer
 */
public class SenderTCPProtocol extends SenderTransport {

    private int dupACKCount; //counter from 0 to 3, 0 meaning ack received not dup
    private int dupACKNum;  //tracker for dup ack's ackNum
    private ArrayList<Packet> packetArrayList;
    private int timeOut;
    private int nextSeqNum;
    private int base;

    /**
     * Constructor of TCP sender protocol
     * @param network layer
     * @param timeline
     * @param timeout 
     */
    public SenderTCPProtocol(NetworkLayer nl, Timeline tl, int n, int timeOut) {
        super(nl, tl, n);
        this.timeOut = timeOut;
    }

    /**
     * initialize the TCP sender's transport layer
     */
    public void initialize() {
        dupACKCount = -1;
        dupACKNum = -1;
        nextSeqNum = 0;
        base = 0;
        packetArrayList = new ArrayList<Packet>();
    }

    /**
     * Send message in TCP protocol
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
            System.out.println("SENDER TCP BUFFERED:  " + msg.getMessage());
        }
    }

    private void sendNextPacket() {
        Packet toSend = new Packet(packetArrayList.get(nextSeqNum));
        System.out.println("SENDER TCP SENDING:     " + toSend.toString());
        nl.sendPacket(toSend, to);
        tl.startTimer(timeOut, me);
        nextSeqNum++;
    }
    
    /**
     * Send next packet in TCP protocol
     */
    public void receiveMessage(Packet pkt) {
        System.out.println("SENDER TCP RECEIVED:    " + pkt.toString());
        if (verifyPacket(pkt)) {
            int ackNum = pkt.getAcknum();
            if (ackNumMakesSense(ackNum)) {
                if (ackIsDuplicate(ackNum)) {
                    dupACKCount++;
                    if (dupACKCount == 3) {
                        fastRetransmit();
                    }
                } else {
                    trackAck(ackNum);
                    base = ackNum;
                    if (base == nextSeqNum) {
                        tl.stopTimer(me);
                    } else {
                        tl.startTimer(timeOut, me);
                    }
                    sendBufferedPkts();
                }
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
    public void resendDueToTimeout() {
        Packet toSend = new Packet(packetArrayList.get(base));
        System.out.println("SENDER TCP RESENDING:   " + toSend.toString());
        nl.sendPacket(toSend, to);
        dupACKCount = -1;
        dupACKNum = base;
    }

    /**
     * check if the received packet correct 
     * @param packet
     * @return true/false
     */
    public boolean verifyPacket(Packet pkt) {
        if(corruptionAllowed) {
            return !pkt.isCorrupt();
        } else {
            return true;
        }
    }

    /**
     * resend packets
     */
    public void fastRetransmit() {
        Packet toSend = new Packet(packetArrayList.get(dupACKNum));
        System.out.println("SENDER TCP RESENDING:   " + toSend.toString());
        nl.sendPacket(toSend, to);
        dupACKCount = 0;
        tl.startTimer(timeOut, me);
    }

    /**
     * track ack amount
     * @param ack number
     */
    public void trackAck(int ackNum) {
        dupACKNum = ackNum;
        dupACKCount = 0;
    }

    /**
     * check if ack number has three duplicates
     * @param ack number
     */
    public boolean ackIsDuplicate(int ackNum) {
        if (ackNum == dupACKNum) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * check if ack number has three duplicates
     * @param ack number
     * @return true/false
     */
    public boolean ackNumMakesSense(int ackNum) {
        if (ackNum < base || ackNum > nextSeqNum) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * check if the sender can send next packet
     */
    public boolean canSendNext() {
        if (nextSeqNum < base + windowSize && nextSeqNum < packetArrayList.size()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * send the packets stored in buffer (could not send because the window did not move which stored in buffer)
     */
    public void sendBufferedPkts() {
        while (canSendNext()) {
            sendNextPacket();
        }
    }

    /**
     * count the amount of duplicate acknowledgement
     */
    public int getDupACKCount() {
        return dupACKCount;
    }
    
    /**
     * get the amount of duplicate acknowledgement
     */
    public int getDupACKNum() {
        return dupACKNum;
    }

    /**
     * get next sequence number
     */
    public int getNextSeqNum() {
        return nextSeqNum;
    }

    /**
     * get base number
     */
    public int getBase() {
        return base;
    }


}
