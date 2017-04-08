import java.util.ArrayList;

/**
 * Created by nortondj on 3/30/17.
 */
public class SenderTCPProtocol extends SenderTransport {

    public int getDupACKCount() {
        return dupACKCount;
    }

    public int getDupACKNum() {
        return dupACKNum;
    }

    public int getNextSeqNum() {
        return nextSeqNum;
    }

    public int getBase() {
        return base;
    }

    private int dupACKCount; //counter from 0 to 3, 0 meaning ack received not dup
    private int dupACKNum;  //tracker for dup ack's ackNum
    private ArrayList<Packet> packetArrayList;
    private int timeOut;
    private int nextSeqNum;
    private int base;


    public SenderTCPProtocol(NetworkLayer nl, Timeline tl, int n, int timeOut) {
        super(nl, tl, n);
        this.timeOut = timeOut;
    }

    public void initialize() {
        dupACKCount = -1;
        dupACKNum = -1;
        nextSeqNum = 0;
        base = 0;
        packetArrayList = new ArrayList<Packet>();
    }

    public void sendMessage(Message msg) {
        Packet p = new Packet(msg, packetArrayList.size(), -1, -1);
        packetArrayList.add(p);
        if (canSendNext()) {
            sendNextPacket();
        } else {
            System.out.println("SENDER TCP BUFFERED:  " + msg.getMessage());
        }
    }

    private void sendNextPacket() {
        Packet toSend = new Packet(packetArrayList.get(nextSeqNum));
        System.out.println("SENDER TCP SENDING:     " + toSend.toString());
        nl.sendPacket(toSend, to);
        tl.startTimer(timeOut, me);
        nextSeqNum++;
    }

    public void receiveMessage(Packet pkt) {
        System.out.println("SENDER TCP RECEIVED:    " + pkt.toString());
        if (!verifyPacket(pkt)) {
            //DO NOTHING
        } else {
            int ackNum = pkt.getAcknum();
            if (ackNumMakesSense(ackNum)) {
                if (ackIsDuplicate(ackNum)) {
                    dupACKCount++;
                    if (dupACKCount == 3) {
                        fastRetransmit();
                    }
                } else {
                    trackAck(ackNum);
                    base = ackNum;
                    if (base == nextSeqNum) {
                        tl.stopTimer(me);
                    } else {
                        tl.startTimer(timeOut, me);
                    }
                    sendBufferedPkts();
                }
            }
        }
    }

    public void timerExpired() {
        System.out.println("TIMER EXPIRED");
        tl.startTimer(timeOut, me);
        resendDueToTimeout();
    }

    public void resendDueToTimeout() {
        Packet toSend = new Packet(packetArrayList.get(base));
        System.out.println("SENDER TCP RESENDING:   " + toSend.toString());
        nl.sendPacket(toSend, to);
    }

    public boolean verifyPacket(Packet pkt) {
        return true;
    }

    public void fastRetransmit() {
        Packet toSend = new Packet(packetArrayList.get(dupACKNum));
        System.out.println("SENDER TCP RESENDING:   " + toSend.toString());
        nl.sendPacket(toSend, to);
        tl.startTimer(timeOut, me);
    }

    public void trackAck(int ackNum) {
        dupACKNum = ackNum;
        dupACKCount = 0;
    }

    public boolean ackIsDuplicate(int ackNum) {
        if (ackNum == dupACKNum) {
            return true;
        } else {
            return false;
        }
    }

    public boolean ackNumMakesSense(int ackNum) {
        if (ackNum < base || ackNum > nextSeqNum) {
            return false;
        } else {
            return true;
        }
    }

    public boolean canSendNext() {
        if (nextSeqNum < base + windowSize && nextSeqNum < packetArrayList.size()) {
            return true;
        } else {
            return false;
        }
    }

    public void sendBufferedPkts() {
        while (canSendNext()) {
            sendNextPacket();
        }
    }


}
