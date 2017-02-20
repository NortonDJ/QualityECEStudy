import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Queue;

//This class represents the client application
public class ClientApp {
    private TransportLayer tl;
    private HTTPRequestBuilder requestBuilder;
    private HTTPResponseDecoder responseDecoder;
    private WebPage page;
    private MyMarkUp mmu;

    public static void main(String[] args) throws Exception {
        ClientApp ca = new ClientApp();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line = reader.readLine();
        ca.run(line);
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

    public ClientApp() {
        this.responseDecoder = new HTTPResponseDecoder();
        this.requestBuilder = new HTTPRequestBuilder();
        this.tl = new TransportLayer(false, 0, 0);
        this.page = new WebPage();
        this.mmu = new MyMarkUp();
    }

    public byte[] GETRequest(String file, float httpversion){
        byte[] request = requestBuilder.build("GET", file, httpversion);
        tl.send(request);
        byte[] response = tl.receive();
        responseDecoder.decode(response);
        return response;
    }

    public void run(String startingFile){
        try{
            Queue<String> workQ = new LinkedList<String>();
            workQ.add(startingFile);
            while(workQ.isEmpty() == false){
                String filename = workQ.poll();
                //send a get request for an embedded file
                //*********FROM SERVER *******************
                //File f2 = new File(filename);

                //extract the contents
                //String embedContents = mmu.readFile(f2);
                //*********FROM SERVER *******************
                GETRequest(filename,1.0f);
                String contents = responseDecoder.getBody();
                //add them to the page's information list
                page.addPageContents(filename, contents);

                //look for attachments
                Queue<String> newQ = mmu.findAttachments(filename);

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
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }



}
