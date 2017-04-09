/**
 * interface of both Go-back-n and TCP protocol
 */
public interface Protocol {

    public void initialize();

    public void sendMessage(Message msg);

    public void receiveMessage(Packet pkt);

    public void timerExpired();

    public boolean verifyPacket(Packet pkt);

    public void enableCorruption(boolean corruptionAllowed);

    public int generateCheckSum(Message m, int seqNum, int ackNum);

}
