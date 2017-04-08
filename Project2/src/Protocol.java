/**
 * Created by nortondj on 3/30/17.
 */
public interface Protocol {

    public void initialize();

    public void sendMessage(Message msg);

    public void receiveMessage(Packet pkt);

    public void timerExpired();

    public boolean verifyPacket(Packet pkt);

    public void enableCorruption(boolean corruptionAllowed);

}
