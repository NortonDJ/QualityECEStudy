import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
* This class simulates the webpage
* 
* @author  Darren Norton, Yizhong Chen
* @since   Feb-19th-2017 
*/
public class WebPage {
    private HashMap<String, String> embeddedMap;
    private Queue<String> pageOrdering;

    /**
             * This is a constructor for web page
             * Form my data structures to deal with embedded pages and the order of them
             * 
             */
    public WebPage(){
        this.embeddedMap = new HashMap<String,String>();
        this.pageOrdering = new LinkedList();
    }

    /**
             * add contents of a page, get stored
             * 
             * @param file name, contents
             */
    public void addPageContents(String filename, String contents){
        embeddedMap.put(filename,contents);
        pageOrdering.add(filename);
    }

    /**
             * construct the page
             * 
             * @return page
             */
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
                String fse = "<src=" + src + ">";
                page = page.replaceAll(fse, embeddedMap.get(src));
            }

        }
        return page;
    }
    
    /**
             * check if there is embedded filenames
             * 
             * @param file name
             * @return true if yes, false if no
             */
    public boolean containsSrc(String filename){
        if(this.embeddedMap.keySet().contains(filename)){
            return true;
        }
        else{
            return false;
        }
    }


}
