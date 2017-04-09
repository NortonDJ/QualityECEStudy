import java.util.*;
import java.io.FileReader;

public class NetworkSimulator
{
    public static int DEBUG;
    /**
     * Main method with following variables
     * @param args[0] file with messages
     * @param args[1] time between messages (int)
     * @param args[2] loss probability (float)
     * @param args[3] corruption probability (float)
     * @param args[4] window size (int)
     * @param args[5] protocol type (int)
     * @param args[6] debugging trace (int)
     */
    public static void main(String[] args)
    {
        //checking to see if enough arguements have been sent
        if(args.length<7)
        {
            System.out.println("need at least 7 arguements");
            System.exit(1);
        }
        System.out.println("Working Directory = " + System.getProperty("user.dir"));

        String filename = args[0];
        int timeBtwnMsg = Integer.parseInt(args[1]);
        float pLoss = Float.parseFloat(args[2]);
        float pCorr = Float.parseFloat(args[3]);
        int winSize = Integer.parseInt(args[4]);
        int protocol = Integer.parseInt(args[5]);
        int debug = Integer.parseInt(args[6]);

        System.out.println("INPUT PARAMETERS:" +
                "\nFILENAME: " + filename +
                "\nTIME BETWEEN MESSAGES: " + timeBtwnMsg +
                "\nPROBABILITY OF LOSS: " + pLoss +
                "\nPROBABILITY OF CORRUPTION: " + pCorr +
                "\nWINDOW SIZE: " + winSize +
                "\nPROTOCOL: " + protocol +
                "\nDEBUG: " + debug);

        NetworkSimulator.run(filename, timeBtwnMsg, pLoss, pCorr, winSize,
                protocol, debug, 15, 15);
    }

    public static Results run(String filename, int timeBtwnMsgs, float pLoss,
                           float pCorr, int winSize, int protocol, int debug,
                           int senderTimeOut, int receiverTimeOut){
        //receiverTimeOut not used, could be used at a later point for delayed ACK
        DEBUG = debug;

        //reading in file line by line. Each line will be one message
        ArrayList<String> messageArray = readFile(filename);
        //creating a new timeline with an average time between packets.
        Timeline tl = new Timeline(timeBtwnMsgs, messageArray.size());
        //creating a new network layer with specific loss and corruption probability.
        NetworkLayer nl = new NetworkLayer(pLoss, pCorr, tl);


        //create a factory to create transport layers
        TransportLayerFactory factory = new TransportLayerFactory(nl,tl);
        //create the sender transport from the factory
        SenderTransport st = factory.makeSender(protocol,winSize, senderTimeOut);
        //create the application with the transport layer
        SenderApplication sa = new SenderApplication(messageArray, st);


        //create the receiver application to send to the factory
        ReceiverApplication ra = new ReceiverApplication();
        //create the receiver transport from the factory
        ReceiverTransport rt = factory.makeReceiver(protocol, winSize, ra, receiverTimeOut);

        st.enableCorruption(true);
        rt.enableCorruption(true);


        //current event to process
        Event currentEvent = null;
        //this loop will run while there are events in the priority queue
        int count = 0;
        try {
            while (true) {
                //get next event
                currentEvent = tl.returnNextEvent();
                //if no event present, break out
                if (currentEvent == null) {
                    break;
                }
                //if event is time to send a message, call the send message function of the sender application.
                if (currentEvent.getType() == Event.MESSAGESEND) {
                    sa.sendMessage();
                    if (DEBUG > 0) {
                        System.out.println("Message sent from sender to receiver at time " + currentEvent.getTime());
                    }
                }
                //if event is a message arrival
                else if (currentEvent.getType() == Event.MESSAGEARRIVE) {
                    //if it arrives at the sender, call the get packet from the sender
                    if (currentEvent.getHost() == Event.SENDER) {
                        if (DEBUG > 0)
                            System.out.println("Message arriving from receiver to sender at time " + currentEvent.getTime());
                        st.receiveMessage(currentEvent.getPacket());
                    }
                    //if it arrives at the receiver, call the get packet from the receiver
                    else {
                        if (DEBUG > 0)
                            System.out.println("Message arriving from sender to receiver at time " + currentEvent.getTime());
                        rt.receiveMessage(currentEvent.getPacket());
                    }
                }
                //If event is an expired timer, call the timerExpired method in the sender transport.
                else if (currentEvent.getType() == Event.TIMER) {
                    int host = currentEvent.getHost();
                    if (DEBUG > 0) {
                        String hostString;
                        if (host == Event.SENDER) {
                            hostString = "Sender";
                        } else {
                            hostString = "Receiver";
                        }
                        System.out.println(hostString + " Timer expired at time " + currentEvent.getTime());
                    }

                    tl.stopTimer(host);
                    if (host == Event.SENDER) {
                        st.timerExpired();
                    } else {
                        rt.timerExpired();
                    }
                } else if (currentEvent.getType() == Event.KILLEDTIMER) {
                    //do nothing if it is just a turned off timer.
                }
                //this should not happen.
                else {
                    System.out.println("Unidentified event type!");
                    System.exit(1);
                }
                count++;
            }
            Results r = new Results(tl.getTotalMessagesToSend(), currentEvent.getTime());
        } catch (UnsupportedOperationException e){
            System.out.println("Network Simulator recognized simulation is over.");
            String protocolString = "";
            if(protocol == 0){
                protocolString = "GBN";
            } else {
                protocolString = "TCP";
            }
            System.out.println("Time to send: " + tl.getTotalMessagesToSend() +
                    " messages using " +protocolString +" = " + currentEvent.getTime());
        } finally {
            Results r = new Results(tl.getTotalMessagesToSend(), currentEvent.getTime());
            return r;
        }
    }

    //reading in file line by line.
    public static ArrayList<String> readFile(String fileName) {
        ArrayList<String> messageArray = new ArrayList<String>();
        Scanner sc=null;
        try{
            sc = new Scanner(new FileReader(fileName));
        }catch(Exception e)
        {System.out.println("Could not open file " + e);}

        while(sc.hasNextLine())
            messageArray.add(sc.nextLine());
        return messageArray;
    }

}
