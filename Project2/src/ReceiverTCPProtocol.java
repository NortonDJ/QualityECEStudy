/**
 * Created by nortondj on 3/30/17.
 */
public class ReceiverTCPProtocol extends ReceiverTransport {
    public int getExpectedSeqNum() {
        return expectedSeqNum;
    }
    private int expectedSeqNum;

    public ReceiverTCPProtocol(NetworkLayer nl, ReceiverApplication ra, int windowSize) {
        super(nl, ra, windowSize);
    }

    public void initialize() {
        expectedSeqNum = 0;
    }

    public void sendMessage(Message msg) {
        // You're the receiver... you can't do that (;_;)
    }

    public void receiveMessage(Packet pkt) {
        System.out.println("RECEIVER TCP RECEIVED:  " + pkt.toString());
        if(verifyPacket(pkt) && pkt.getSeqnum() == expectedSeqNum){
            Message msg = pkt.getMessage();
            ra.receiveMessage(msg);
            expectedSeqNum++;
            sendAck(expectedSeqNum);
        } else {
            sendAck(expectedSeqNum - 1);
        }
    }

    public void sendAck(int acknum){
        Packet ack = new Packet(new Message("I'm an ACK"), -1, acknum, -1);
        System.out.println("RECEIVER TCP SENDING:   " + ack.toString());
        nl.sendPacket(ack, to);
    }


    public void timerExpired() {

    }

    public boolean verifyPacket(Packet pkt){
        return true;
    }

}
