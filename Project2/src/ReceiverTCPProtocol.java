import java.util.ArrayList;

/**
 * Created by nortondj on 3/30/17.
 */
public class ReceiverTCPProtocol extends ReceiverTransport {
    public int getExpectedSeqNum() {
        return expectedSeqNum;
    }

    private int expectedSeqNum;
    private int timeOut;
    private ArrayList<Packet> packetArrayList;

    public ReceiverTCPProtocol(NetworkLayer nl, ReceiverApplication ra, int windowSize, int timeOut) {
        super(nl, ra, windowSize);
        this.timeOut = timeOut;
    }

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

    public void receiveMessage(Packet pkt) {
        System.out.println("RECEIVER TCP RECEIVED:  " + pkt.toString());
        if (verifyPacket(pkt)) {
            int seqNum = pkt.getSeqnum();
            if (seqNum == expectedSeqNum) {
                packetArrayList.set(seqNum, pkt);
                Message msg = pkt.getMessage();
                expectedSeqNum += deliverBuffered();
                sendAck(expectedSeqNum);
            } else if (seqNumInWindow(seqNum)) {
                packetArrayList.set(seqNum, pkt);
                sendAck(expectedSeqNum);
            } else {
                sendAck(expectedSeqNum);
            }
        } else {
            sendAck(expectedSeqNum);
        }
    }

    public void sendAck(int ackNum) {
        Packet ack = new Packet(new Message("I'm an ACK"), -1, ackNum, -1);
        System.out.println("RECEIVER TCP SENDING:   " + ack.toString());
        nl.sendPacket(ack, to);
    }


    public void timerExpired() {

    }

    public boolean verifyPacket(Packet pkt) {
        if(corruptionAllowed) {
            return !pkt.isCorrupt();
        } else {
            return true;
        }
    }

    public boolean seqNumInWindow(int seqNum) {
        if (seqNum > expectedSeqNum && seqNum < expectedSeqNum + windowSize) {
            return true;
        } else {
            return false;
        }
    }

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
