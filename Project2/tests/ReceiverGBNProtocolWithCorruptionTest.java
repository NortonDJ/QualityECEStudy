import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by nortondj on 4/7/17.
 */
class ReceiverGBNProtocolWithCorruptionTest {
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
        rt.enableCorruption(false);
    }
}