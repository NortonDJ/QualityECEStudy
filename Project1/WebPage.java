import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by nortondj on 2/19/17.
 */
public class WebPage {
    private HashMap<String, String> embeddedMap;
    private Queue<String> pageOrdering;

    public WebPage(){
        this.embeddedMap = new HashMap<String,String>();
        this.pageOrdering = new LinkedList();
    }

    public void addPageContents(String filename, String contents){
        embeddedMap.put(filename,contents);
        pageOrdering.add(filename);
    }

    public String constructPage(){
        String page = "";
        while(pageOrdering.isEmpty() == false){
            //srcEmbedded looks like "example1.txt"
            String srcEmbedded = pageOrdering.poll();

            //get the contents for that file
            String contents = embeddedMap.get(srcEmbedded);

            //if the page is empty, put the contents there
            if(page.isEmpty()){
                page = contents;
            }

            //look for all references to any embedded values and replace them
            for(String src : embeddedMap.keySet()) {
                String fse = "<src=\"" + src + "\">";
                page = page.replaceAll(fse, embeddedMap.get(src));
            }

        }
        return page;
    }


}
