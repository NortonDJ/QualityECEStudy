import java.util.ArrayList;
import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * The test class SenderTCPProtocolWithCorruptionTest.
 *
 * @author  (your name)
 * @version (a version number or a date)
 */
public class SenderTCPProtocolWithCorruptionTest
{
    private ArrayList<String> messageArray;
    private Timeline tl;
    private NetworkLayer nl;
    private SenderTCPProtocol st;
    /**
     * Default constructor for test class SenderTCPProtocolWithCorruptionTest
     */
    public SenderTCPProtocolWithCorruptionTest()
    {
    }

    /**
     * Sets up the test fixture.
     *
     * Called before every test case method.
     */
    @Before
    public void setUp()
    {
        
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

    /**
     * Tears down the test fixture.
     *
     * Called after every test case method.
     */
    @After
    public void tearDown()
    {
    }
}
