import java.util.*;

/**
 * A class which represents the transoprt layer for both sender and receiver.
 */

public class NetworkLayer {
    float lossProbability; //probability of losing a packet
    float currProbability; //probability of corrupting a packet
    Timeline tl;
    Random ran; //random number generator for losing packets.

    /**
     * Constructor of TransportLayerFactory
     *
     * @param probability of losing a packet
     * @param probability of corrupting a packet
     * @param timeline
     */
    public NetworkLayer(float lp, float cp, Timeline tl) {
        lossProbability = lp;
        currProbability = cp;
        this.tl = tl;
        ran = new Random();
    }

    /**
     * sending packet if it is not lost, and corrupting it if necessary.
     *
     * @param packet
     * @param timeout
     */
    public void sendPacket(Packet pkt, int to) {
        if (ran.nextDouble() < lossProbability) {
            if (NetworkSimulator.DEBUG > 1)
                System.out.println("Packet seq: " + pkt.getSeqnum() + " ack: " + pkt.getAcknum() + " lost");
            return;
        }
        if (ran.nextDouble() < currProbability) {
            if (NetworkSimulator.DEBUG > 1)
                System.out.println("Packet seq: " + pkt.getSeqnum() + " ack: " + pkt.getAcknum() + " corrupted");
            pkt.corrupt();
        }
        if (NetworkSimulator.DEBUG > 1)
            System.out.println("Packet seq: " + pkt.getSeqnum() + " ack: " + pkt.getAcknum() + " sent");
        tl.createArriveEvent(pkt, to);
        //System.out.println(tl);
    }

}
