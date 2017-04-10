import java.util.*;

/**
 * A class which represents the sender's application. the sendMessage will be called at random times.
 */
public class SenderApplication {
    private SenderTransport st; //transport layer used
    private ArrayList<String> messages; //all messages the application will send
    private int index; //how many messages has the application sent so far

    /**
     * Constructor SenderApplication
     *
     * @param messages
     * @param sender   transport
     */
    public SenderApplication(ArrayList<String> messages, SenderTransport st) {
        this.st = st;
        this.messages = messages;
        index = 0;
    }

    /**
     * get transport layer used
     */
    public SenderTransport getSenderTransport() {
        return st;
    }

    /**
     * send message through transport layer
     */
    public void sendMessage() {

        //System.out.println("Sender is sending message: " + messages.get(index));
        st.sendMessage(new Message(messages.get(index++)));

    }
}
