import java.io.File;
import java.io.FileWriter;

/**
 * Created by nortondj on 2/20/17.
 */
public class ExperimentController {

    private FileWriter fout;
    public ExperimentController(){

    }

    public static void main(String[] args){
        ExperimentController ec = new ExperimentController();
        ec.run();
    }

    public void run(){
        simulatePvsNP("p1.txt", "test1.txt", 1.1f);
        simulatePvsNP("p2.txt", "test2a.txt", 1.1f);
        simulatePvsNP("p3.txt", "test3a.txt", 1.1f);
        simulatePvsNP("np1.txt", "test1.txt", 1.0f);
        simulatePvsNP("np2.txt", "test2a.txt", 1.0f);
        simulatePvsNP("np3.txt", "test3a.txt", 1.0f);
        simulateC("c1.txt", "test1.txt", 1.0f);
        simulateC("c2.txt", "test2a.txt", 1.0f);
        simulateC("c3.txt", "test3a.txt", 1.0f);
        simulateNC("nc1.txt", "test1.txt", 1.0f);
        simulateNC("nc2.txt", "test2a.txt", 1.0f);
        simulateNC("nc3.txt", "test3a.txt", 1.0f);
        simulateDUMP("d1.txt", "test1.txt", 1.0f);
        simulateDUMP("d2.txt", "test2a.txt", 1.0f);
        simulateDUMP("d3.txt", "test3a.txt", 1.0f);

    }

    public void simulatePvsNP(String writeFile, String startingFile, float version) {
        try {
            fout = new FileWriter(new File(writeFile));
            int[] testVector = {1, 10, 50, 100, 200};
            fout.write("trial, dprop, dtrans, time");
            for (int i = 0; i < 5; i++) {
                int dprop = 100;
                int dtrans = testVector[i];
                ServerApp s1 = new ServerApp(dprop, dtrans);
                ClientApp c1 = new ClientApp(version);
                long time = c1.run(startingFile);
                fout.write((i+1) + "," + dprop + "," + dtrans + "," + time);
            }
            fout.close();
        }

        catch(Exception e){
            fout.close();
        }
    }

    public void simulateNC(String writeFile, String startingFile, float version){
        try {
            fout = new FileWriter(new File(writeFile));
            int[] testVector = {1, 10, 50, 100, 200};
            fout.write("trial, dprop, dtrans, time");
            for (int i = 0; i < 5; i++) {
                int dprop = 100;
                int dtrans = testVector[i];
                long time = 0l;
                for(int j = 0; j < 3; j++) {
                    ServerApp s1 = new ServerApp(dprop, dtrans);
                    ClientApp c1 = new ClientApp(version);
                    time += c1.run(startingFile);
                }
                fout.write((i + 1) + "," + dprop + "," + dtrans + "," + time);
            }
            fout.close();
        }

        catch(Exception e){
            fout.close();
        }
    }

    public void simulateC(String writeFile, String startingFile, float version){
        try {
            fout = new FileWriter(new File(writeFile));
            int[] testVector = {1, 10, 50, 100, 200};
            fout.write("trial, dprop, dtrans, time");
            for (int i = 0; i < 5; i++) {
                int dprop = 100;
                int dtrans = testVector[i];
                long time = 0l;
                for(int j = 0; j < 3; j++) {
                    ServerApp s1 = new ServerApp(dprop, dtrans);
                    CacheClientApp c1 = new CacheClientApp(version);
                    time += c1.run(startingFile);
                }
                fout.write((i + 1) + "," + dprop + "," + dtrans + "," + time);
            }
            fout.close();
        }

        catch(Exception e){
            fout.close();
        }
    }

    public void simulateDUMP(String writeFile, String startingFile, float version) {
        try {
            fout = new FileWriter(new File(writeFile));
            int[] testVector = {1, 10, 50, 100, 200};
            fout.write("trial, dprop, dtrans, time");
            for (int i = 0; i < 5; i++) {
                int dprop = 100;
                int dtrans = testVector[i];
                ImprovedServerApp s1 = new ImprovedServerApp(dprop, dtrans);
                DUMPClientApp c1 = new DUMPClientApp(version);
                long time = c1.run(startingFile);
                fout.write((i+1) + "," + dprop + "," + dtrans + "," + time);
            }
            fout.close();
        }

        catch(Exception e){
            fout.close();
        }
    }

}
