import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by nortondj on 4/5/17.
 */
class SenderGBNProtocolNoCorruptionTest {
    private ArrayList<String> messageArray;
    private Timeline tl;
    private NetworkLayer nl;
    private SenderGBNProtocol st;

    @BeforeEach
    void setUp() {
        String filename = "./src/test.txt";
        int timeBtwnMsgs = 10;
        float pLoss = 0;
        float pCorr = 0;
        int winSize = 3;
        int protocol = 0;
        int debug = 0;
        int timeOut = 100;

        //reading in file line by line. Each line will be one message
        messageArray = NetworkSimulator.readFile(filename);
        //creating a new timeline with an average time between packets.
        tl = new Timeline(timeBtwnMsgs, messageArray.size());
        //creating a new network layer with specific loss and corruption probability.
        nl = new NetworkLayer(pLoss, pCorr, tl);
        //create the sender transport from the factory
        st = new SenderGBNProtocol(nl,tl,winSize,100);
        st.enableCorruption(false);
    }

    @Test
    void sendMessage1() {
        System.out.println("Test sendMessage1");
        for(int i = 0; i < 4; i++){
            st.sendMessage(new Message(messageArray.get(i)));
        }

        assertEquals(0, st.getBase());
        assertEquals(3, st.getNextSeqNum());
    }

    @Test
    void sendMessage2() {
        System.out.println("Test sendMessage2");
        for(int i = 0; i < 5; i++){
            st.sendMessage(new Message(messageArray.get(i)));
        }

        assertEquals(0, st.getBase());
        assertEquals(3, st.getNextSeqNum());
    }

    @Test
    void receiveACK0() {
        System.out.println("Test receiveACK0");
        for(int i = 0; i < 5; i++){
            st.sendMessage(new Message(messageArray.get(i)));
        }
        Packet ack = new Packet(new Message("I'm an ACK"), -1, 0, -1);
        st.receiveMessage(ack);
        assertEquals(1, st.getBase());
        assertEquals(4, st.getNextSeqNum());
    }

    @Test
    void receiveACK1() {
        System.out.println("Test receiveACK1");
        for(int i = 0; i < 5; i++){
            st.sendMessage(new Message(messageArray.get(i)));
        }
        Packet ack = new Packet(new Message("I'm an ACK"), -1, 1, -1);
        st.receiveMessage(ack);
        assertEquals(2, st.getBase());
        assertEquals(5, st.getNextSeqNum());
    }

    @Test
    void receiveCUMAckMovesBase() {
        System.out.println("Test receiveCUMAckMovesBase");
        for(int i = 0; i < 5; i++){
            st.sendMessage(new Message(messageArray.get(i)));
        }
        Packet ack = new Packet(new Message("I'm an ACK"), -1, 3, -1);
        st.receiveMessage(ack);
        assertEquals(4, st.getBase());
    }

    @Test
    void receiveCUMAckSends3() {
        System.out.println("Test receiveCUMAckSends3");
        for(int i = 0; i < 8; i++){
            st.sendMessage(new Message(messageArray.get(i)));
        }
        Packet ack = new Packet(new Message("I'm an ACK"), -1, 3, -1);
        st.receiveMessage(ack);
        //when the sender receives the ack for 3, we expect that it send 4,5,6
        assertEquals(7, st.getNextSeqNum());
    }

    @Test
    void receiveCUMAckSendsLessThanWindowSize() {
        System.out.println("Test receiveCUMAckSendsLessThanWindowSize");
        for(int i = 0; i < 8; i++){
            st.sendMessage(new Message(messageArray.get(i)));
        }
        Packet ack = new Packet(new Message("I'm an ACK"), -1, 2, -1);
        st.receiveMessage(ack);
        assertEquals(6, st.getNextSeqNum());
    }

    @Test
    void receiveACKLessThanBaseDoesNothing(){
        System.out.println("Test receiveAckSendsLessThanBaseDoesNothing");
        for(int i = 0; i < 8; i++){
            st.sendMessage(new Message(messageArray.get(i)));
        }
        Packet ack = new Packet(new Message("I'm an ACK"), -1, 3, -1);
        st.receiveMessage(ack);
        Packet ack2 = new Packet(new Message("I'm an ACK"), -1, 1, -1);
        st.receiveMessage(ack2);
        assertEquals(4, st.getBase());
        assertEquals(7, st.getNextSeqNum());
    }

    @Test
    void receiveACKGreaterThanNextSeqNumDoesNothing(){
        System.out.println("Test receiveAckGreaterThanNextSeqNumDoesNothing");
        for(int i = 0; i < 8; i++){
            st.sendMessage(new Message(messageArray.get(i)));
        }
        Packet ack = new Packet(new Message("I'm an ACK"), -1, 10, -1);
        st.receiveMessage(ack);
        assertEquals(0, st.getBase());
        assertEquals(3, st.getNextSeqNum());
    }

    @Test
    void receiveACKDoesntResendPacketsBetweenBaseAndNextSeqNum(){
        System.out.println("Test receiveACKDoesntResendPacketsBetweenBaseAndNextSeqNum");
        for(int i = 0; i < 8; i++){
            st.sendMessage(new Message(messageArray.get(i)));
        }
        Packet ack = new Packet(new Message("I'm an ACK"), -1, 0, -1);
        //packets 0,1,2 should be out
        st.receiveMessage(ack);
        //by receiving ack for 0, it should send 3, and not resend 1,2
        ArrayList<Integer> seqNumCounts = new ArrayList<Integer>();
        for(int i = 0; i < 100; i++){
            seqNumCounts.add(0);
        }
        while(tl.sizeOfQueue() != 0){
            Event e = tl.returnNextEvent();
            if(e.getType() == Event.MESSAGEARRIVE) {
                int seqNum = e.getPacket().getSeqnum();
                seqNumCounts.set(seqNum, seqNumCounts.get(seqNum) + 1);
            }
        }
        assertEquals(new Integer(1), seqNumCounts.get(1));
        assertEquals(new Integer(1), seqNumCounts.get(2));
        assertEquals(new Integer(1), seqNumCounts.get(3));

    }

}