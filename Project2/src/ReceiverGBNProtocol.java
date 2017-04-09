/**
 * 
 */
public class ReceiverGBNProtocol extends ReceiverTransport {
    private int expectedSeqNum;
    public ReceiverGBNProtocol(NetworkLayer nl, ReceiverApplication ra, int windowSize) {
        super(nl, ra, windowSize);
    }

    public void initialize() {
        expectedSeqNum = 0;
    }

    public void sendMessage(Message msg) {
        // You're the receiver... you can't do that (;_;)
    }

    public void receiveMessage(Packet pkt) {
        System.out.println("RECEIVER GBN RECEIVED:  " + pkt.toString());
        if(verifyPacket(pkt) && pkt.getSeqnum() == expectedSeqNum){
            Message msg = pkt.getMessage();
            ra.receiveMessage(msg);
            sendAck(expectedSeqNum);
            expectedSeqNum++;
        } else {
            sendAck(expectedSeqNum - 1);
        }
    }

    public void sendAck(int ackNum){
        Message msg = new Message("I'm an ACK");
        int seqNum = -1;
        Packet ack = new Packet(msg, seqNum, ackNum, generateCheckSum(msg,seqNum,ackNum));
        System.out.println("RECEIVER GBN SENDING:   " + ack.toString());
        nl.sendPacket(ack, to);
    }

    public void timerExpired() {

    }

    public boolean verifyPacket(Packet pkt){
        if(corruptionAllowed) {
            return !pkt.isCorrupt();
        } else {
            return true;
        }
    }
    
    public int getExpectedSeqNum() {
        return expectedSeqNum;
    }
}
