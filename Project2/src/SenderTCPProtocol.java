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
     *
     * @param network  layer
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
        dupACKNum = 0;
        nextSeqNum = 0;
        base = 0;
        packetArrayList = new ArrayList<Packet>();
        numTransmissions = 0;
    }

    /**
     * Send message in TCP protocol
     *
     * @param message content
     */
    public void sendMessage(Message msg) {
        // add packet to sender's buffer/storage
        int seqNum = packetArrayList.size();
        int ackNum = -1;
        Packet p = new Packet(msg, seqNum, ackNum, generateCheckSum(msg, seqNum, ackNum));
        packetArrayList.add(p);
        // try to send the next packet
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
        numTransmissions++;
    }

    /**
     * Send next packet in TCP protocol
     */
    public void receiveMessage(Packet pkt) {
        System.out.println("SENDER TCP RECEIVED:    " + pkt.toString());
        if (verifyPacket(pkt)) { //if packet is not corrupt
            int ackNum = pkt.getAcknum();
            if (ackNum == tl.getTotalMessagesToSend()) {
                System.out.println("Sender has received the final ACK. Simulation OVER.");
                throw new UnsupportedOperationException("We're DONE!");
            }
            if (ackNumMakesSense(ackNum)) { //if ack is in window
                if (ackIsDuplicate(ackNum)) { //check if its duplicate
                    dupACKCount++;
                    if (dupACKCount == 3) {
                        fastRetransmit();
                    }
                } else {
                    trackAck(ackNum); //track the ack
                    base = ackNum;
                    if (base == nextSeqNum) {
                        tl.stopTimer(me);
                    } else {
                        tl.startTimer(timeOut, me);
                    }
                    //the sender has received an ack, send the next packet(s)
                    sendBufferedPkts();
                }
            }
        } else {
            System.out.println("SENDER TCP RECOGNIZED CORRUPT PACKET");
            // DO NOTHING
        }
    }

    /**
     * when time out expired, call resending routine
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
        numTransmissions++;
    }

    /**
     * check if the received packet correct
     *
     * @param packet
     * @return true/false
     */
    public boolean verifyPacket(Packet pkt) {
        if (corruptionAllowed) {
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
        System.out.println("SENDER TCP FAST RETRANSMIT:   " + toSend.toString());
        nl.sendPacket(toSend, to);
        dupACKCount = 0;
        tl.stopTimer(me);
        tl.startTimer(timeOut, me);
        numTransmissions++;
    }

    /**
     * track ack sets the current acknum to be followed
     *
     * @param ack number
     */
    public void trackAck(int ackNum) {
        dupACKNum = ackNum;
        dupACKCount = 0;
    }

    /**
     * check if ack number has three duplicates
     *
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
     * check if ack is in window
     *
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
     * send the packets stored in buffer (previously could not be sent
     * because the window was full)
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
