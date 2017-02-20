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

    public byte[] GETRequest(String file, float httpversion){
        if(cache.containsKey(file)){
            requestBuilder.mapHeader("ifmodified", getCurTime());
        }
        byte[] request = requestBuilder.build("GET", file, httpversion);
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

    public void run(String startingFile){
        try{
            long start = System.currentTimeMillis();
            System.out.println("TIME START");
            Queue<String> workQ = new LinkedList<String>();
            workQ.add(startingFile);
            while(workQ.isEmpty() == false){
                String filename = workQ.poll();

                //send a get request for an embedded file
                GETRequest(filename,version);
                //get the response information
                int statusCode = responseDecoder.getStatus();
                String phrase = responseDecoder.getPhrase();
                //print them
                System.out.println("Status Code: " + statusCode);
                System.out.println("Phrase: " + phrase);
                String contents;
                //if the response says the information is up to date, get it
                //from the cache
                if(responseDecoder.getStatus() == 304) {
                    contents = cache.get(filename);
                }
                else if(responseDecoder.getStatus() == 200){
                //otherwise it would have sent the information
                    contents = responseDecoder.getBody();
                }
                else{
                    System.out.println("Could not create the webpage");
                    return;
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
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

}
