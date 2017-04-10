import java.util.*;

/**
 * A class which represents a packet
 */
public class Packet {

    private Message msg; //the enclosed message
    private int seqnum; //packets seq. number
    private int acknum; //packet ack. number
    private int checksum; //packet checksum

    Random ran; //random number generator

    public Packet(Packet p) {
        this.msg = new Message(p.msg.getMessage());
        this.seqnum = p.seqnum;
        this.acknum = p.acknum;
        this.checksum = p.checksum;
        this.ran = p.ran;
    }

    public Packet(Message msg, int seqnum, int acknum, int checksum) {
        this.msg = msg;
        this.seqnum = seqnum;
        this.acknum = acknum;
        this.checksum = checksum;
        this.ran = new Random();
    }

    public int getAcknum() {
        return acknum;
    }

    public int getSeqnum() {
        return seqnum;
    }

    public Message getMessage() {
        return msg;
    }

    public void setChecksum() {
    }

    public boolean isCorrupt() {
        int sum = seqnum + acknum;
        String s = msg.getMessage();
        for (char c : s.toCharArray()) {
            sum += c;
        }
        return sum != checksum;
    }

    /**
     * This method curropts the packet the follwing way:
     * curropt the message with a 75% chance
     * curropt the seqnum with 12.5% chance
     * curropt the ackum with 12.5% chance
     */
    public void corrupt() {
        if (ran.nextDouble() < 0.75) {
            this.msg.corruptMessage();
        } else if (ran.nextDouble() < 0.875) {
            this.seqnum = this.seqnum + 1;
        } else {
            this.acknum = this.acknum + 1;
        }

    }

    public String toString() {
        String s = "";
        s += "ACK: " + acknum;
        s += ", SEQNUM: " + seqnum;
        s += ", CHECKSUM: " + checksum;
        s += ", MESSAGE: " + msg.getMessage();
        return s;
    }
}
