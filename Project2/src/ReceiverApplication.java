import java.util.ArrayList;

/**
 * A class which represents the receiver's application. It simply prints out the message received from the tranport layer.
 */
public class ReceiverApplication
{
    private ArrayList<Message> messagesReceived = new ArrayList<Message>();

    public void receiveMessage(Message msg)
    {
        System.out.println("Receiver received:" + msg.getMessage());
        messagesReceived.add(msg);
    }

    public ArrayList<Message> getMessagesReceived() {
        return messagesReceived;
    }
}
