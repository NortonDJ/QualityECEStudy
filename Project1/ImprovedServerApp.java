import com.sun.corba.se.spi.activation.Server;

import java.io.File;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by nortondj on 2/20/17.
 */
public class ImprovedServerApp extends ServerApp {

    private MyMarkUp mmu;
    private WebPage page;

    public ImprovedServerApp(int dprop, int dtrans){
        super(dprop,dtrans);
        this.mmu = new MyMarkUp();
        this.page = new WebPage();
    }

    public static void main(String[] args){
         try {
            int dprop;  //ms
            int dtrans; //ms per byte
            switch (args.length) {
                case 1:
                    System.out.println("Invalid arguments: requires specifying a " +
                            "dprop AND a dtrans.");
                    dprop = 100;
                    dtrans = 20;
                    System.out.println("Setting dprop = " + dprop +
                            " and dtrans = " + dtrans);
                    break;
                case 2:
                    try {
                        dprop = Integer.parseInt(args[0]);
                        dtrans = Integer.parseInt(args[1]);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        dprop = 100;
                        dtrans = 20;
                        System.out.println("Setting dprop = " + dprop +
                                " and dtrans = " + dtrans);
                    }
                default:
                    dprop = 200;
                    dtrans = 10;
            }
            ImprovedServerApp s = new ImprovedServerApp(dprop, dtrans);
            s.run();
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            System.out.println("Bye!");
            System.exit(-1);
        }
    }
    
    public byte[] formResponse(byte[] request){
        //send the decoder the request
        decoder.decode(request);
        //server supports both versions
        float version = decoder.getVersion();
        String method = decoder.getMethod();
        //if the method was "GET" handle it by GET
        if(method.equals("GET")){
            return (handleGET(version));
        }
        else if(method.equals("DUM")){
            return (handleDUM(version));
        }
        else{
            return builder.build(version,404, "NOT FOUND",
                    "Unknown method of request");
        }
    }

    public byte[] handleDUM(float version){
        try{
            //load the necessary headers
            String url = decoder.getURL();
            
            //initialize message, phrase, and status code
            String message = "";
            int statusCode = 0;
            String phrase = "";

            Queue<String> workQ = new LinkedList<String>();
            workQ.add(url);
            while(workQ.isEmpty() == false){
                String filename = workQ.poll();
                File f = new File(filename);

                String contents = mmu.readFile(f);

                //add them to the page's information list
                page.addPageContents(filename, contents);

                //look for attachments
                Queue<String> newQ = mmu.findAttachments(contents);

                // copy those attachments to the workQ if worthy
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
            message = page.constructPage();
            statusCode = 666;
            phrase = "DUMP SUCCESSFUL";
            return builder.build(version, statusCode, phrase, message);
        }
        catch(Exception e){
            //the file could not be opened, thus we don't know the
            //resource
            e.printStackTrace();
            return builder.build(version, 404, "NOT FOUND",
                    "The requested resource could not be found");
        }
    }
}
