import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by nortondj on 2/20/17.
 */
public class DUMPClientApp extends ClientApp{

    public DUMPClientApp(float version){
        super(version);
    }

    public static void main(String[] args){
        try {
            float version = Float.parseFloat(args[0]);
            DUMPClientApp dca = new DUMPClientApp(version);
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String line = reader.readLine();
            dca.run(line);
        }
        catch(Exception e){
            System.exit(-1);
        }
    }

    public byte[] DUMRequest(String file, float httpversion){
        byte[] request = requestBuilder.build("DUM", file, httpversion);
        tl.send(request);
        byte[] response = tl.receive();
        if(response == null){
            System.out.println("RESPONSE IS NULL");
        }
        responseDecoder.decode(response);
        if(httpversion == 1.0f){
            tl.disconnect();
        }
        return response;
    }

    public long run(String startingFile){
        try{
            long start = System.currentTimeMillis();
            System.out.println("TIME START");
            String filename = startingFile;

            //send a DUMP request for an embedded file
            DUMRequest(filename,version);
            //get the response information
            String contents = responseDecoder.getBody();
            int statusCode = responseDecoder.getStatus();
            String phrase = responseDecoder.getPhrase();
            //print them
            System.out.println("Status Code: " + statusCode);
            System.out.println("Phrase: " + phrase);
            System.out.println("Body: " + contents);
            if(statusCode != 666){
                //Webpage could not be constructed
                System.out.println("The webpage could not be constructed");
                return 0;
            }
            //add them to the page's information list
            page.addPageContents(filename, contents);
            System.out.println(page.constructPage());
            tl.disconnect();
            long stop = System.currentTimeMillis();
            System.out.println("TIME STOP");
            System.out.println("TIME ELAPSED(ms): " + (stop-start));
            page.clear();
            return stop-start;
        }
        catch(Exception e){
            e.printStackTrace();
            return 0;
        }
    }

}
