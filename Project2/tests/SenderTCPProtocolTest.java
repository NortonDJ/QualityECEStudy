import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by nortondj on 4/6/17.
 */
class SenderTCPProtocolTest {

    private ArrayList<String> messageArray;
    private Timeline tl;
    private NetworkLayer nl;
    private SenderTCPProtocol st;

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
        st = new SenderTCPProtocol(nl,tl,winSize,100);

    }

    @Test
    void startsRoundOnInitialSend(){
        st.sendMessage(new Message(messageArray.get(0)));
        assertEquals(0, st.getRound());
        assertTrue(st.isStarted());
    }

    @Test
    void finishesRound0Sends0IfNone(){
        st.sendMessage(new Message(messageArray.get(0)));
        Packet ack = new Packet(new Message("I'm an ACK"), -1, 0, -1);
        st.receiveMessage(ack);
        assertEquals(1,st.getNextSeqNum());
    }

    @Test
    void finishedRound0Sends2If2(){
        for(int i = 0; i < 10; i++) {
            st.sendMessage(new Message(messageArray.get(i)));
        }
        Packet ack = new Packet(new Message("I'm an ACK"), -1, 0, -1);
        st.receiveMessage(ack);
        assertEquals(3,st.getNextSeqNum());
        assertEquals(1, st.getBase());
    }

    @Test
    void finishedRound0Sends1If1(){
        for(int i = 0; i < 2; i++) {
            st.sendMessage(new Message(messageArray.get(i)));
        }
        Packet ack = new Packet(new Message("I'm an ACK"), -1, 0, -1);
        st.receiveMessage(ack);
        assertEquals(2,st.getNextSeqNum());
        assertEquals(1, st.getBase());
    }

    @Test
    void finishedRound1Sends4If4(){
        for(int i = 0; i < 10; i++) {
            st.sendMessage(new Message(messageArray.get(i)));
        }
        Packet ack = new Packet(new Message("I'm an ACK"), -1, 0, -1);
        st.receiveMessage(ack);
        Packet ack2 = new Packet(new Message("I'm an ACK"), -1, 2, -1);
        st.receiveMessage(ack2);
        assertEquals(7,st.getNextSeqNum());
        assertEquals(3, st.getBase());
    }

}