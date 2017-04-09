import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

/**
 * Created by nortondj on 4/9/17.
 */
public class ExperimentController {

    public static void main(String[] args){
        ExperimentController ec = new ExperimentController();
        String test = "t2.txt";
//        ec.testTimeFuncOfPCorr(test);
//        ec.testTimeFuncOfPLoss(test);
        ec.testTimeFuncOfWinSize(test);
        ec.testTransFuncOfWinSize(test);
//        ec.testTimeFuncOfMessages(test);
//        ec.testTimeFuncTimeBtwn(test);
//        ec.testTimeFuncTimeOut(test);
    }

    public void testTimeFuncOfPCorr(String writeFile){
        try {
            FileWriter ftcp = new FileWriter(new File("tcp_time_pCorr_" + writeFile));
            FileWriter fgbn = new FileWriter(new File("gbn_time_pCorr_" + writeFile));
            ArrayList<Float> vec = new ArrayList<Float>();
            for(int i = 0; i < 20; i ++){
                float f = (i * 0.05f);
                vec.add(f);
            }
            ftcp.write("pCorr, aveTime\n");
            fgbn.write("pCorr, aveTime\n");
            for (Float pCorr : vec) {
                ArrayList<Integer> tcpVec = new ArrayList<Integer>();
                ArrayList<Integer> gbnVec = new ArrayList<Integer>();
                for(int i = 0 ; i < 5; i ++) {
                    NetworkSimulator gbnns = new NetworkSimulator();
                    Results gbn = gbnns.run("test.txt", 10, 0, pCorr, 5, 0, 3, 50, 50);
                    NetworkSimulator tcpns = new NetworkSimulator();
                    Results tcp = tcpns.run("test.txt", 10, 0, pCorr, 5, 1, 3, 50, 50);
                    tcpVec.add(tcp.getTimeTaken());
                    gbnVec.add(gbn.getTimeTaken());
                }
                ftcp.write(pCorr + ", " + average(tcpVec) + "\n");
                fgbn.write(pCorr + ", " + average(gbnVec) + "\n");
                ftcp.flush();
                fgbn.flush();
            }
            ftcp.close();
            fgbn.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public void testTimeFuncOfPLoss(String writeFile){
        try {
            FileWriter ftcp = new FileWriter(new File("tcp_time_pLoss_" + writeFile));
            FileWriter fgbn = new FileWriter(new File("gbn_time_pLoss_" + writeFile));
            ArrayList<Float> vec = new ArrayList<Float>();
            for(int i = 0; i < 20; i ++){
                float f = (i * 0.05f);
                vec.add(f);
            }
            ftcp.write("pLoss, aveTime\n");
            fgbn.write("pLoss, aveTime\n");
            for (Float pLoss : vec) {
                ArrayList<Integer> tcpVec = new ArrayList<Integer>();
                ArrayList<Integer> gbnVec = new ArrayList<Integer>();
                for(int i = 0 ; i < 5; i ++) {
                    NetworkSimulator gbnns = new NetworkSimulator();
                    Results gbn = gbnns.run("test.txt", 10, pLoss, 0, 5, 0, 3, 50, 50);
                    NetworkSimulator tcpns = new NetworkSimulator();
                    Results tcp = tcpns.run("test.txt", 10, pLoss, 0, 5, 1, 3, 50, 50);
                    tcpVec.add(tcp.getTimeTaken());
                    gbnVec.add(gbn.getTimeTaken());
                }
                ftcp.write(pLoss + ", " + average(tcpVec) + "\n");
                fgbn.write(pLoss + ", " + average(gbnVec) + "\n");
                ftcp.flush();
                fgbn.flush();
            }
            ftcp.close();
            fgbn.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void testTimeFuncOfWinSize(String writeFile){
        try {
            FileWriter ftcp = new FileWriter(new File("tcp_time_winSize_" + writeFile));
            FileWriter fgbn = new FileWriter(new File("gbn_time_winSize_" + writeFile));
            ArrayList<Integer> vec = new ArrayList<Integer>();
            for(int i = 3; i < 40; i ++){
                vec.add(i);
            }
            ftcp.write("winSize, aveTime\n");
            fgbn.write("winSize, aveTime\n");
            for (Integer winSize : vec) {
                ArrayList<Integer> tcpVec = new ArrayList<Integer>();
                ArrayList<Integer> gbnVec = new ArrayList<Integer>();
                for(int i = 0 ; i < 5; i ++) {
                    NetworkSimulator gbnns = new NetworkSimulator();
                    Results gbn = gbnns.run("test.txt", 10, .05f, .05f, winSize, 0, 3, 50, 50);
                    NetworkSimulator tcpns = new NetworkSimulator();
                    Results tcp = tcpns.run("test.txt", 10, .05f, .05f, winSize, 1, 3, 50, 50);
                    tcpVec.add(tcp.getTimeTaken());
                    gbnVec.add(gbn.getTimeTaken());
                }
                ftcp.write(winSize + ", " + average(tcpVec) + "\n");
                fgbn.write(winSize + ", " + average(gbnVec) + "\n");
                ftcp.flush();
                fgbn.flush();
            }
            ftcp.close();
            fgbn.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void testTransFuncOfWinSize(String writeFile){
        try {
            FileWriter ftcp = new FileWriter(new File("tcp_trans_winSize_" + writeFile));
            FileWriter fgbn = new FileWriter(new File("gbn_trans_winSize_" + writeFile));
            ArrayList<Integer> vec = new ArrayList<Integer>();
            for(int i = 3; i < 40; i ++){
                vec.add(i);
            }
            ftcp.write("winSize, trans\n");
            fgbn.write("winSize, trans\n");
            for (Integer winSize : vec) {
                ArrayList<Integer> tcpVec = new ArrayList<Integer>();
                ArrayList<Integer> gbnVec = new ArrayList<Integer>();
                for(int i = 0 ; i < 5; i ++) {
                    NetworkSimulator gbnns = new NetworkSimulator();
                    Results gbn = gbnns.run("test.txt", 10, .05f, .05f, winSize, 0, 3, 50, 50);
                    NetworkSimulator tcpns = new NetworkSimulator();
                    Results tcp = tcpns.run("test.txt", 10, .05f, .05f, winSize, 1, 3, 50, 50);
                    tcpVec.add(tcp.getNumTransmissions());
                    gbnVec.add(gbn.getNumTransmissions());
                }
                ftcp.write(winSize + ", " + average(tcpVec) + "\n");
                fgbn.write(winSize + ", " + average(gbnVec) + "\n");
                ftcp.flush();
                fgbn.flush();
            }
            ftcp.close();
            fgbn.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void testTimeFuncOfMessages(String writeFile){
        try {
            FileWriter ftcp = new FileWriter(new File("tcp_time_messages_" + writeFile));
            FileWriter fgbn = new FileWriter(new File("gbn_time_messages_" + writeFile));
            ArrayList<String> vec = new ArrayList<String>();
            vec.add("test.txt");
            vec.add("notAnEndorsement.txt");
            vec.add("notAnotherEpic.txt");
            vec.add("notOurProjectReport.txt");
            vec.add("notRedditPost.txt");
            vec.add("notRedditPost2.txt");
            vec.add("notSongLyrics.txt");
            vec.add("notTheEpicOfAllTimes.txt");
            ftcp.write("filename, messages, aveTime\n");
            fgbn.write("filename, messages, aveTime\n");
            for (String fn : vec) {
                ArrayList<Integer> tcpVec = new ArrayList<Integer>();
                ArrayList<Integer> gbnVec = new ArrayList<Integer>();
                int messagesSent = 0;
                for(int i = 0; i < 5; i++) {
                    NetworkSimulator gbnns = new NetworkSimulator();
                    Results gbn = gbnns.run(fn, 10, .05f, .05f, 5, 0, 3, 50, 50);
                    NetworkSimulator tcpns = new NetworkSimulator();
                    Results tcp = tcpns.run(fn, 10, .05f, .05f, 5, 1, 3, 50, 50);
                    tcpVec.add(tcp.getTimeTaken());
                    gbnVec.add(gbn.getTimeTaken());
                    messagesSent = tcp.getMessagesSent();
                }
                ftcp.write(fn + ", " + messagesSent + ", " + average(tcpVec) + "\n");
                fgbn.write(fn + ", " + messagesSent + ", " + average(gbnVec) + "\n");
                ftcp.flush();
                fgbn.flush();
            }
            ftcp.close();
            fgbn.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void testTimeFuncTimeBtwn(String writeFile){
        try {
            FileWriter ftcp = new FileWriter(new File("tcp_time_timeBtwn_" + writeFile));
            FileWriter fgbn = new FileWriter(new File("gbn_time_timeBtwn_" + writeFile));
            ArrayList<Integer> vec = new ArrayList<Integer>();
            for(int i = 10; i < 100; i+= 5){
                vec.add(i);
            }
            ftcp.write("timeBtwn, aveTime\n");
            fgbn.write("timeBtwn, aveTime\n");
            for (Integer timeBtwn : vec) {
                ArrayList<Integer> tcpVec = new ArrayList<Integer>();
                ArrayList<Integer> gbnVec = new ArrayList<Integer>();
                for(int i = 0 ; i < 5; i ++) {
                    NetworkSimulator gbnns = new NetworkSimulator();
                    Results gbn = gbnns.run("test.txt", timeBtwn, .05f, .05f, 5, 0, 3, 50, 50);
                    NetworkSimulator tcpns = new NetworkSimulator();
                    Results tcp = tcpns.run("test.txt", timeBtwn, .05f, .05f, 5, 1, 3, 50, 50);
                    tcpVec.add(tcp.getTimeTaken());
                    gbnVec.add(gbn.getTimeTaken());
                }
                ftcp.write(timeBtwn + ", " + average(tcpVec) + "\n");
                fgbn.write(timeBtwn + ", " + average(gbnVec) + "\n");
                ftcp.flush();
                fgbn.flush();
            }
            ftcp.close();
            fgbn.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void testTimeFuncTimeOut(String writeFile){
        try {
            FileWriter ftcp = new FileWriter(new File("tcp_time_timeout_" + writeFile));
            FileWriter fgbn = new FileWriter(new File("gbn_time_timeout_" + writeFile));
            ArrayList<Integer> vec = new ArrayList<Integer>();
            for(int i = 20; i < 100; i += 5){
                vec.add(i);
            }
            ftcp.write("timeout, aveTime\n");
            fgbn.write("timeout, aveTime\n");
            for (Integer timeout : vec) {
                ArrayList<Integer> tcpVec = new ArrayList<Integer>();
                ArrayList<Integer> gbnVec = new ArrayList<Integer>();
                for(int i = 0 ; i < 5; i ++) {
                    NetworkSimulator gbnns = new NetworkSimulator();
                    Results gbn = gbnns.run("test.txt", 10, .05f, .05f, 5, 0, 3, timeout, 50);
                    NetworkSimulator tcpns = new NetworkSimulator();
                    Results tcp = tcpns.run("test.txt", 10, .05f, .05f, 5, 1, 3, timeout, 50);
                    tcpVec.add(tcp.getTimeTaken());
                    gbnVec.add(gbn.getTimeTaken());
                }
                ftcp.write(timeout + ", " + average(tcpVec) + "\n");
                fgbn.write(timeout + ", " + average(gbnVec) + "\n");
                ftcp.flush();
                fgbn.flush();
            }
            ftcp.close();
            fgbn.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public int average(ArrayList<Integer> intVec){
        if(intVec.isEmpty()){
            return 0;
        }
        int sum = 0;
        int size = intVec.size();
        for(Integer i : intVec){
            sum += i;
        }
        return sum/size;
    }

}
