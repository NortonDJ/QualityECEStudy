import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by nortondj on 2/20/17.
 */
public class CacheClientApp extends ClientApp {

    private HashMap<String, String> cache;
    private HashMap<String, String> cacheTimes;

    public CacheClientApp(float httpversion){
        super(httpversion);
        this.cache = new HashMap<String, String>();
        this.cacheTimes = new HashMap<String, String>();
    }

    public static void main(String[] args){
        try{
        float version = Float.parseFloat(args[0]);
        CacheClientApp cca = new CacheClientApp(version);
            String file = "example4.txt";
            cca.run(file);
            System.out.println("EDIT AND SAVE THE FILE NOW");
            Thread.sleep(10000);
            File f = new File(file);
            cca.run(file);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public HTTPResponse GETRequest(String file, float httpversion){
        HTTPRequest req = new HTTPRequest("GET", file, httpversion);
        if(cache.containsKey(file)){
            req.mapHeader("ifmodified", cacheTimes.get(file));
        }
        byte[] request = requestEncoder.encode(req);
        tl.send(request);

        byte[] response = tl.receive();
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
                HTTPResponse resp = GETRequest(filename,version);
                //get the response information
                int statusCode = resp.getStatusCode();
                String phrase = resp.getPhrase();
                
                System.out.println(statusCode);
                System.out.println(phrase);
                //print them
                String contents;
                //if the response says the information is up to date, get it
                //from the cache
                if(statusCode == 304) {
                    contents = cache.get(filename);
                }
                else if(statusCode == 200){
                    //otherwise it would have sent the information
                    contents = resp.getBody();
                    cacheTimes.put(filename, getCurTime());
                }
                else{
                    System.out.println("Could not create the webpage");
                    return 0;
                }
                //add them to the page's information list
                cache.put(filename,contents);
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

}
