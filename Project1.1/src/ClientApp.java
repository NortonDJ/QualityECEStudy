import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Queue;

//This class represents the client application
public class ClientApp {
    protected TransportLayer tl;
    protected HTTPRequestEncoder requestEncoder;
    protected HTTPResponseDecoder responseDecoder;
    protected WebPage page;
    protected MyMarkUp mmu;
    protected float version;
    private DateFormat format;


    public static void main(String[] args) {
        try {
            float version = Float.parseFloat(args[0]);
            ClientApp ca = new ClientApp(version);
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String line = reader.readLine();
            ca.run(line);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            System.out.println("Bye!");
            System.exit(-1);
        }
        /*float httpversion;

        switch (args.length) {
            case 1:
                try {
                    httpversion = Float.parseFloat(args[0]);
                } catch (Exception e) {
                    httpversion = 1.0f;
                    e.printStackTrace();
                }
                break;
            default:
                httpversion = 1.0f;
        }
        //create a new transport layer for client (hence false) (connect to server), and read in first line from keyboard
        TransportLayer transportLayer = new TransportLayer(false, 0, 0);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line = reader.readLine();

        //while line is not empty
        while (line != null && !line.equals("")) {
            //convert lines into byte array, send to transport layer and wait for response
            byte[] byteArray = line.getBytes();
            transportLayer.send(byteArray);
            byteArray = transportLayer.receive();
            String str = new String(byteArray);
            System.out.println(str);
            //read next line
            line = reader.readLine();
        }*/

    }

    public ClientApp(float version) {
        this.format = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
        this.responseDecoder = new HTTPResponseDecoder();
        this.requestEncoder = new HTTPRequestEncoder();
        this.tl = new TransportLayer(false, 0, 0);
        this.page = new WebPage();
        this.mmu = new MyMarkUp();
        this.version = version;
    }

    public HTTPResponse GETRequest(String file, float httpversion){
        HTTPRequest req = new HTTPRequest("GET", file, httpversion);
        byte[] request = requestEncoder.encode(req);
        tl.send(request);
        byte[] response = tl.receive();
        if(response == null){
            System.out.println("RESPONSE IS NULL");
        }
        HTTPResponse resp = responseDecoder.decode(response);
        if(httpversion == 1.0f){
            tl.disconnect();
        }
        return resp;
    }

    public long run(String startingFile){
        try{
            long start = System.currentTimeMillis();
            System.out.println("TIME START");
            Queue<String> workQ = new LinkedList<String>();
            workQ.add(startingFile);
            while(workQ.isEmpty() == false){
                String filename = workQ.poll();

                //send a get request for an embedded file
                HTTPResponse response = GETRequest(filename,version);
                //get the response information
                String contents = response.getBody();
                int statusCode = response.getStatusCode();
                String phrase = response.getPhrase();
                //print them
                System.out.println("Status Code: " + statusCode);
                System.out.println("Phrase: " + phrase);
                if(statusCode != 200){
                    //Webpage could not be constructed
                    System.out.println("The webpage could not be constructed");
                    return 0;
                }
                //add them to the page's information list
                page.addPageContents(filename, contents);

                //look for attachments
                Queue<String> newQ = mmu.findAttachments(contents);
                //copy those attachments to the workQ if worthy
                while(newQ.isEmpty() == false) {
                    String srcName = newQ.poll();
                    //if the work q or the page already have the information
                    if(workQ.contains(srcName) || page.containsSrc(srcName)) {
                        //dont add it again
                    }
                    //otherwise add the attachment to the work q
                    else{
                        workQ.add(srcName);
                    }
                }
            }
            System.out.println(page.constructPage());
            if(version==1.1f){
                tl.disconnect();
            }
            long stop = System.currentTimeMillis();
            System.out.println("TIME STOP");
            System.out.println("TIME ELAPSED(ms): " + (stop-start));
            page.clear();
            return(stop-start);
        }
        catch(Exception e){
            e.printStackTrace();
            return(0);
        }
    }

    public String getCurTime(){
        Calendar cal = Calendar.getInstance();
        return (format.format(cal.getTime()));
    }


}
