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
        st.enableCorruption(false);

    }

    @Test
    public void sendsFirst(){
        st.sendMessage(new Message(messageArray.get(0)));
        assertEquals(0,st.getBase());
        assertEquals(1, st.getNextSeqNum());
    }

    @Test
    public void sendsOnlyWindowSize(){
        for(int i = 0 ; i < 5; i++) {
            st.sendMessage(new Message(messageArray.get(i)));
        }
        assertEquals(0,st.getBase());


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
        assertEquals(new Integer(1), seqNumCounts.get(0));
        assertEquals(new Integer(1), seqNumCounts.get(1));
        assertEquals(new Integer(1), seqNumCounts.get(2));
        assertEquals(new Integer(0), seqNumCounts.get(3));
        assertEquals(new Integer(0), seqNumCounts.get(4));
        assertEquals(new Integer(0), seqNumCounts.get(5));
    }

    @Test
    public void BadACKOnFirstPacketTracksACK(){
        for(int i = 0 ; i < 5; i++) {
            st.sendMessage(new Message(messageArray.get(i)));
        }
        Packet ack = new Packet(new Message("I'm an ACK"), -1, 0, -1);
        st.receiveMessage(ack);
        assertEquals(0,st.getDupACKNum());
        assertEquals(0,st.getDupACKCount());
    }

    @Test
    public void DuplicateACKOnFirstPacketIdentified(){
        for(int i = 0 ; i < 5; i++) {
            st.sendMessage(new Message(messageArray.get(i)));
        }
        for(int i = 0 ; i < 2; i++) {
            Packet ack = new Packet(new Message("I'm an ACK"), -1, 0, -1);
            st.receiveMessage(ack);
        }
        assertTrue(st.ackIsDuplicate(0));
    }

    @Test
    public void DuplicateACKOnFirstPacketIncrementsCount(){
        for(int i = 0 ; i < 5; i++) {
            st.sendMessage(new Message(messageArray.get(i)));
        }
        for(int i = 0 ; i < 2; i++) {
            Packet ack = new Packet(new Message("I'm an ACK"), -1, 0, -1);
            st.receiveMessage(ack);
        }
        assertEquals(0,st.getDupACKNum());
        assertEquals(1,st.getDupACKCount());
    }

    @Test
    public void ThreeDuplicateACKOnFirstPacketResendsFirst(){
        for(int i = 0 ; i < 5; i++) {
            st.sendMessage(new Message(messageArray.get(i)));
        }
        for(int i = 0; i < 4; i++){
            Packet ack = new Packet(new Message("I'm an ACK"), -1, 0, -1);
            st.receiveMessage(ack);
        }

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
        assertEquals(new Integer(2), seqNumCounts.get(0));
        assertEquals(new Integer(1), seqNumCounts.get(1));
        assertEquals(new Integer(1), seqNumCounts.get(2));
        assertEquals(new Integer(0), seqNumCounts.get(3));
        assertEquals(new Integer(0), seqNumCounts.get(4));
        assertEquals(new Integer(0), seqNumCounts.get(5));
    }

    @Test
    public void NonsensibleACKOnNotFirstPacketNotTracksACK(){
        for(int i = 0 ; i < 5; i++) {
            st.sendMessage(new Message(messageArray.get(i)));
        }
        Packet goodAck = new Packet(new Message("I'm an ACK"), -1, 1, -1);
        st.receiveMessage(goodAck);
        Packet ack = new Packet(new Message("I'm an ACK"), -1, 0, -1);
        st.receiveMessage(ack);
        assertEquals(1,st.getDupACKNum());
        assertEquals(0,st.getDupACKCount());
    }

    @Test
    public void BadACKOnNotFirstPacketTracksACK(){
        for(int i = 0 ; i < 5; i++) {
            st.sendMessage(new Message(messageArray.get(i)));
        }
        Packet goodAck = new Packet(new Message("I'm an ACK"), -1, 1, -1);
        st.receiveMessage(goodAck);
        Packet ack = new Packet(new Message("I'm an ACK"), -1, 1, -1);
        st.receiveMessage(ack);
        assertEquals(1,st.getDupACKNum());
        assertEquals(1,st.getDupACKCount());
    }

    @Test
    public void DuplicateACKOnNotFirstPacketIdentified(){
        for(int i = 0 ; i < 5; i++) {
            st.sendMessage(new Message(messageArray.get(i)));
        }
        Packet goodAck = new Packet(new Message("I'm an ACK"), -1, 1, -1);
        st.receiveMessage(goodAck);
        for(int i = 0 ; i < 2; i++) {
            Packet ack = new Packet(new Message("I'm an ACK"), -1, 1, -1);
            st.receiveMessage(ack);
        }
        assertTrue(st.ackIsDuplicate(1));
    }
    @Test
    public void DuplicateACKOnNotFirstPacketIncrementsCount(){
        for(int i = 0 ; i < 5; i++) {
            st.sendMessage(new Message(messageArray.get(i)));
        }
        for(int i = 0 ; i < 2; i++) {
            Packet ack = new Packet(new Message("I'm an ACK"), -1, 1, -1);
            st.receiveMessage(ack);
        }
        assertEquals(1,st.getDupACKNum());
        assertEquals(1,st.getDupACKCount());
    }

    @Test
    public void ThreeDuplicateACKOnNotFirstPacketResendsFirst(){
        for(int i = 0 ; i < 5; i++) {
            st.sendMessage(new Message(messageArray.get(i)));
        }
        for(int i = 0; i < 4; i++){
            Packet ack = new Packet(new Message("I'm an ACK"), -1, 1, -1);
            st.receiveMessage(ack);
        }

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
        assertEquals(new Integer(1), seqNumCounts.get(0));
        assertEquals(new Integer(2), seqNumCounts.get(1));
        assertEquals(new Integer(1), seqNumCounts.get(2));
        assertEquals(new Integer(1), seqNumCounts.get(3));
        assertEquals(new Integer(0), seqNumCounts.get(4));
        assertEquals(new Integer(0), seqNumCounts.get(5));
    }

    @Test
    public void receiveCUMAckMakesSense(){
        for(int i = 0; i < 8; i++){
            st.sendMessage(new Message(messageArray.get(i)));
        }
        assertTrue(st.ackNumMakesSense(3));
    }

    @Test
    public void receiveCUMAckMovesBase(){
        for(int i = 0; i < 8; i++){
            st.sendMessage(new Message(messageArray.get(i)));
        }
        Packet ack = new Packet(new Message("I'm an ACK"), -1, 3, -1);
        st.receiveMessage(ack);
        assertEquals(3, st.getBase());
    }

    @Test
    public void receiveCUMAckSends3() {
        for(int i = 0; i < 8; i++){
            st.sendMessage(new Message(messageArray.get(i)));
        }
        Packet ack = new Packet(new Message("I'm an ACK"), -1, 3, -1);
        st.receiveMessage(ack);
        //when the sender receives the ack requesting for 3, we expect that it send 3,4,5
        assertEquals(6, st.getNextSeqNum());
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
        assertEquals(new Integer(1), seqNumCounts.get(0));
        assertEquals(new Integer(1), seqNumCounts.get(1));
        assertEquals(new Integer(1), seqNumCounts.get(2));
        assertEquals(new Integer(1), seqNumCounts.get(3));
        assertEquals(new Integer(1), seqNumCounts.get(4));
        assertEquals(new Integer(1), seqNumCounts.get(5));
        assertEquals(new Integer(0), seqNumCounts.get(6));

    }

    @Test
    public void timerExpiredResendsOnlyFirst(){
        for(int i = 0; i < 8; i++){
            st.sendMessage(new Message(messageArray.get(i)));
        }
        st.timerExpired();
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
        assertEquals(new Integer(2), seqNumCounts.get(0));
        assertEquals(new Integer(1), seqNumCounts.get(1));
        assertEquals(new Integer(1), seqNumCounts.get(2));
        assertEquals(new Integer(0), seqNumCounts.get(3));
        assertEquals(new Integer(0), seqNumCounts.get(4));
        assertEquals(new Integer(0), seqNumCounts.get(5));
        assertEquals(new Integer(0), seqNumCounts.get(6));
    }

    @Test
    public void timerExpiredTwiceSamePacket(){
        for(int i = 0; i < 8; i++){
            st.sendMessage(new Message(messageArray.get(i)));
        }
        st.timerExpired();
        st.timerExpired();
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
        assertEquals(new Integer(3), seqNumCounts.get(0));
        assertEquals(new Integer(1), seqNumCounts.get(1));
        assertEquals(new Integer(1), seqNumCounts.get(2));
        assertEquals(new Integer(0), seqNumCounts.get(3));
        assertEquals(new Integer(0), seqNumCounts.get(4));
        assertEquals(new Integer(0), seqNumCounts.get(5));
        assertEquals(new Integer(0), seqNumCounts.get(6));
    }

    @Test
    public void timerExpiredTwiceDiffPacket(){
        for(int i = 0; i < 8; i++){
            st.sendMessage(new Message(messageArray.get(i)));
        }
        st.timerExpired();
        Packet ack = new Packet(new Message("I'm an ACK"), -1, 1, -1);
        st.receiveMessage(ack);
        st.timerExpired();
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
        assertEquals(new Integer(2), seqNumCounts.get(0));
        assertEquals(new Integer(2), seqNumCounts.get(1));
        assertEquals(new Integer(1), seqNumCounts.get(2));
        assertEquals(new Integer(1), seqNumCounts.get(3));
        assertEquals(new Integer(0), seqNumCounts.get(4));
        assertEquals(new Integer(0), seqNumCounts.get(5));
        assertEquals(new Integer(0), seqNumCounts.get(6));
    }



}