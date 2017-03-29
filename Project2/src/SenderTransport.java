import java.util.ArrayList;

/**
 * A class which represents the receiver transport layer
 */
public class SenderTransport {
    private NetworkLayer nl;
    private Timeline tl;
    private int n;
    private boolean usingTCP;
    private int nextSeqNum;
    private int base;
    private ArrayList<Packet> sentPkts;
    private static int timeOut = 10;
    private static int to = 1;

    public SenderTransport(NetworkLayer nl) {
        this.nl = nl;
        initialize();
    }

    public void initialize() {
        sentPkts = new ArrayList<Packet>();
        nextSeqNum = 0;
        base = 0;
    }

    public void sendMessage(Message msg) {
        if (usingTCP) {
            sendMessageViaTCP(msg);
        } else {
            sendMessageViaGBN(msg);
        }
    }

    public void receiveMessage(Packet pkt) {
        if(usingTCP){
            receiveMessageViaTCP(pkt);
        }
        else{
            receiveMessageViaGBN(pkt);
        }
    }

    public void sendMessageViaTCP(Message msg) {

    }

    public void sendMessageViaGBN(Message msg) {
        sendMessageViaGBN(msg);
        if (nextSeqNum < base + n) {
            sentPkts.set(nextSeqNum, new Packet(msg, nextSeqNum, -1, -1));
            nl.sendPacket(sentPkts.get(nextSeqNum), to);
            if (base == nextSeqNum) {
                tl.startTimer(timeOut);
            }
            nextSeqNum++;
        } else {
            return;
        }
    }

    public void receiveMessageViaTCP(Packet pkt){

    }

    public void receiveMessageViaGBN(Packet pkt){
        if(pkt.isCorrupt()){
            //TODO
        }
        else{
            base = pkt.getAcknum() + 1;
            if(base == nextSeqNum){
                tl.stopTimer();
            }
            else{
                tl.startTimer(timeOut);
            }
        }
    }

    public void timerExpired() {
        tl.startTimer(timeOut);
        for(int i = base; i < nextSeqNum; i++){  //TODO maybe base+nextSeqNum?
            if(sentPkts.get(i) != null){
                nl.sendPacket(sentPkts.get(i), to);
            }
        }
    }

    public void setTimeLine(Timeline tl) {
        this.tl = tl;
    }

    public void setWindowSize(int n) {
        this.n = n;
    }

    public void setProtocol(int n) {
        if (n > 0)
            usingTCP = true;
        else
            usingTCP = false;
    }

}
