import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by nortondj on 4/5/17.
 */
class ReceiverGBNProtocolTest {
    private ArrayList<String> messageArray;
    private Timeline tl;
    private NetworkLayer nl;
    private ReceiverGBNProtocol rt;

    @BeforeEach
    void setUp() {
        String filename = "./src/test.txt";
        int timeBtwnMsgs = 10;
        float pLoss = 0;
        float pCorr = 0;
        int winSize = 3;
        int protocol = 0;
        int debug = 0;

        //reading in file line by line. Each line will be one message
        messageArray = NetworkSimulator.readFile(filename);
        //creating a new timeline with an average time between packets.
        tl = new Timeline(timeBtwnMsgs, messageArray.size());
        //creating a new network layer with specific loss and corruption probability.
        nl = new NetworkLayer(pLoss, pCorr, tl);
        ReceiverApplication ra = new ReceiverApplication();
        rt = new ReceiverGBNProtocol(nl, ra, winSize);

    }

    @Test
    public void receiveExpectedSeqNumMovesExpectSeqNum(){
        rt.receiveMessage(new Packet(new Message("Hello"),0,-1,-1));
        assertEquals(1,rt.getExpectedSeqNum());
    }

    @Test
    public void receiveExpectedSeqNumSendsCorrectACK(){
        rt.receiveMessage(new Packet(new Message("Hello"),0,-1,-1));
        ArrayList<Integer> ackNumCounts = new ArrayList<Integer>();
        for(int i = 0; i < 100; i++){
            ackNumCounts.add(0);
        }
        while(tl.sizeOfQueue() != 0){
            Event e = tl.returnNextEvent();
            if(e.getType() == Event.MESSAGEARRIVE) {
                int acknum = e.getPacket().getAcknum();
                ackNumCounts.set(acknum, ackNumCounts.get(acknum) + 1);
            }
        }
        assertEquals(new Integer(1), ackNumCounts.get(0));
    }

    @Test
    public void receiveExpectedSeqNumSendsOnlyCorrectACK(){
        int sizeOfQueue = tl.sizeOfQueue();
        rt.receiveMessage(new Packet(new Message("Hello"),0,-1,-1));
        assertEquals(sizeOfQueue + 1, tl.sizeOfQueue());
    }

    @Test
    public void receiveUnExpectedSeqNumFirstPacketSendsACK0(){
        rt.receiveMessage(new Packet(new Message("Hello"),4,-1,-1));
        ArrayList<Integer> ackNumCounts = new ArrayList<Integer>();
        for(int i = 0; i < 100; i++){
            ackNumCounts.add(0);
        }
        while(tl.sizeOfQueue() != 0){
            Event e = tl.returnNextEvent();
            if(e.getType() == Event.MESSAGEARRIVE) {
                int acknum = e.getPacket().getAcknum();
                ackNumCounts.set(acknum+1, ackNumCounts.get(acknum+1) + 1);
            }
        }
        assertEquals(new Integer(1), ackNumCounts.get(0));
    }

    @Test
    public void receiveGreaterThanExpectedSeqNumSendsMostRecentlyAcked(){
        for(int i = 0; i < 3; i++){
            rt.receiveMessage(new Packet(new Message("Hello"),i,-1,-1));
        }
        rt.receiveMessage(new Packet(new Message("Hello"),15,-1,-1));;
        ArrayList<Integer> ackNumCounts = new ArrayList<Integer>();
        for(int i = 0; i < 100; i++){
            ackNumCounts.add(0);
        }
        while(tl.sizeOfQueue() != 0){
            Event e = tl.returnNextEvent();
            if(e.getType() == Event.MESSAGEARRIVE) {
                int acknum = e.getPacket().getAcknum();
                ackNumCounts.set(acknum+1, ackNumCounts.get(acknum+1) + 1);
            }
        }
        assertEquals(new Integer(1), ackNumCounts.get(1));
        assertEquals(new Integer(1), ackNumCounts.get(2));
        assertEquals(new Integer(2), ackNumCounts.get(3));
        assertEquals(new Integer(0), ackNumCounts.get(15));
    }

    @Test
    public void receiveLessThanExpectedSeqNumSendsMostRecentlyAcked(){
        for(int i = 0; i < 3; i++){
            rt.receiveMessage(new Packet(new Message("Hello"),i,-1,-1));
        }
        rt.receiveMessage(new Packet(new Message("Hello"),2,-1,-1));
        ArrayList<Integer> ackNumCounts = new ArrayList<Integer>();
        for(int i = 0; i < 100; i++){
            ackNumCounts.add(0);
        }
        while(tl.sizeOfQueue() != 0){
            Event e = tl.returnNextEvent();
            if(e.getType() == Event.MESSAGEARRIVE) {
                int acknum = e.getPacket().getAcknum();
                ackNumCounts.set(acknum+1, ackNumCounts.get(acknum+1) + 1);
            }
        }
        assertEquals(new Integer(1), ackNumCounts.get(1));
        assertEquals(new Integer(1), ackNumCounts.get(2));
        assertEquals(new Integer(2), ackNumCounts.get(3));
        assertEquals(new Integer(0), ackNumCounts.get(0));
    }
}