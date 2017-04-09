/**
 * Created by nortondj on 4/9/17.
 */
public class Results {

    private int messagesSent;
    private int timeTaken;
    private int numTransmissions;

    public Results(int messagesSent, int timeTaken, int numTransmissions){
        this.messagesSent = messagesSent;
        this.timeTaken = timeTaken;
        this.numTransmissions = numTransmissions;
    }

    public int getMessagesSent() {
        return messagesSent;
    }

    public int getTimeTaken() {
        return timeTaken;
    }

    public int getNumTransmissions(){
        return numTransmissions;
    }

}
