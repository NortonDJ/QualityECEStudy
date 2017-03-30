/**
 * Created by nortondj on 3/30/17.
 */
public class SenderTCPProtocol extends SenderTransport {

    public SenderTCPProtocol(NetworkLayer nl, Timeline tl, int n){
        super(nl, tl, n);
    }

    public void initialize() {}

    public void sendMessage(Message msg) {}

    public void receiveMessage(Packet pkt) {}

    public void timerExpired() {}

    public boolean verifyPacket(Packet pkt){
        return true;
    }

}
