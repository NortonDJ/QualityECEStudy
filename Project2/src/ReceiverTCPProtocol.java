import java.util.ArrayList;

/**
 * A class which represents the protocol of TCP receiver transport layer
 */
public class ReceiverTCPProtocol extends ReceiverTransport {
    public int getExpectedSeqNum() {
        return expectedSeqNum;
    }

    private int expectedSeqNum;
    private int timeOut;
    private ArrayList<Packet> packetArrayList;

    /**
     * Constructor of TCP receiver protocol
     *
     * @param network  layer
     * @param receiver application
     * @param window   size
     * @param timeout
     */
    public ReceiverTCPProtocol(NetworkLayer nl, ReceiverApplication ra, int windowSize, int timeOut) {
        super(nl, ra, windowSize);
        this.timeOut = timeOut;
    }

    /**
     * initialize the TCP receiver's transport layer
     */
    public void initialize() {
        expectedSeqNum = 0;
        //create the "infinite" packetArrayList
        this.packetArrayList = new ArrayList<Packet>();
        for (int i = 0; i < nl.tl.getTotalMessagesToSend(); i++) {
            packetArrayList.add(null);
        }
    }

    public void sendMessage(Message msg) {
        // You're the receiver... you can't do that (;_;)
    }

    /**
     * receive message in TCP protocol
     *
     * @param packet
     */
    public void receiveMessage(Packet pkt) {
        System.out.println("RECEIVER TCP RECEIVED:  " + pkt.toString());
        if (verifyPacket(pkt)) {
            int seqNum = pkt.getSeqnum();
            if (seqNum == expectedSeqNum) {
                packetArrayList.set(seqNum, pkt);
                expectedSeqNum += deliverBuffered();
                sendAck(expectedSeqNum);
            } else if (seqNumInWindow(seqNum)) {
                packetArrayList.set(seqNum, pkt);
                sendAck(expectedSeqNum);
            } else {
                sendAck(expectedSeqNum);
            }
        } else {
            System.out.println("RECEIVER TCP RECOGNIZED CORRUPT PACKET");
            sendAck(expectedSeqNum);
        }
    }

    /**
     * Send acknowledgement in TCP protocol
     *
     * @param acknowledgement number
     */
    public void sendAck(int ackNum) {
        Message msg = new Message("I'm an ACK");
        int seqNum = -1;
        Packet ack = new Packet(msg, seqNum, ackNum, generateCheckSum(msg, seqNum, ackNum));
        System.out.println("RECEIVER TCP SENDING:   " + ack.toString());
        nl.sendPacket(ack, to);
    }

    public void timerExpired() {

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
     * check if the sequence number in window
     *
     * @param sequence number
     * @return true/false
     */
    public boolean seqNumInWindow(int seqNum) {
        if (seqNum > expectedSeqNum && seqNum < expectedSeqNum + windowSize) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * get the delivered sequence number
     *
     * @return Delivered sequence number
     */
    public int deliverBuffered() {
        int numDelivered = 0;
        for (int i = expectedSeqNum; i < expectedSeqNum + windowSize && i < packetArrayList.size(); i++) {
            Packet toDeliver = packetArrayList.get(i);
            if (toDeliver == null) {
                break;
            } else {
                ra.receiveMessage(packetArrayList.get(i).getMessage());
                numDelivered++;
            }
        }
        return numDelivered;
    }

}
