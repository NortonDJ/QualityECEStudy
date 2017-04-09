/**
 * A class which represents the protocol of GBN receiver transport layer
 */
public class ReceiverGBNProtocol extends ReceiverTransport {
    private int expectedSeqNum;
    /**
     * Constructor of Go-Back-n receiver protocol
     * @param network layer
     * @param receiver application
     * @param timeout 
     */
    public ReceiverGBNProtocol(NetworkLayer nl, ReceiverApplication ra, int windowSize) {
        super(nl, ra, windowSize);
    }

    /**
     * Constructor of Go-Back-n receiver protocol
     * @param network layer
     * @param receiver application
     * @param timeout 
     */
    public void initialize() {
        expectedSeqNum = 0;
    }

    public void sendMessage(Message msg) {
        // You're the receiver... you can't do that (;_;)
    }

    /**
     * receive messages in Go-back-n protocol
     * @param packet
     */
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
    
    /**
         * send ack information when receiving packet
         * @param packet number
         */
    public void sendAck(int ackNum){
        Message msg = new Message("I'm an ACK");
        int seqNum = -1;
        Packet ack = new Packet(msg, seqNum, ackNum, generateCheckSum(msg,seqNum,ackNum));
        System.out.println("RECEIVER GBN SENDING:   " + ack.toString());
        nl.sendPacket(ack, to);
    }

    public void timerExpired() {

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
     * ge† expec†´ sequence number
     */
    public int getExpectedSeqNum() {
        return expectedSeqNum;
    }
}
