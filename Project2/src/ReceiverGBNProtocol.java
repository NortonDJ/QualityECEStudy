/**
 * Created by nortondj on 3/30/17.
 */
public class ReceiverGBNProtocol extends ReceiverTransport {

    private int expectedSeqNum;

    public ReceiverGBNProtocol(NetworkLayer nl, ReceiverApplication ra, int windowSize) {
        super(nl, ra, windowSize);
    }

    public void initialize() {
        expectedSeqNum = 1;
    }

    public void sendMessage(Message msg) {
        // You're the receiver... you can't do that (;_;)
    }

    public void receiveMessage(Packet pkt) {
        if(verifyPacket(pkt) || pkt.getSeqnum() == expectedSeqNum){
            Message msg = pkt.getMessage();
            ra.receiveMessage(msg);
            Packet ack = new Packet(new Message("I'm an ACK"), -1, expectedSeqNum, -1);
            nl.sendPacket(ack, to);
            expectedSeqNum++;
        }
        else{
            Packet ack = new Packet(new Message("I'm an ACK"), -1, expectedSeqNum, -1);
            nl.sendPacket(ack, to);
        }
    }

    public void timerExpired() {

    }

    public boolean verifyPacket(Packet pkt){
        return true;
    }
}
