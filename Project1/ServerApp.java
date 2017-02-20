import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

//This class represents the server application
public class ServerApp
{
    private DateFormat format;
    private TransportLayer transportLayer;
    private int dprop;  // ms
    private int dtrans; // ms per byte
    private HTTPRequestDecoder decoder;
    private HTTPResponseBuilder builder;
    private MyMarkUp language;

    public static void main(String[] args) throws Exception
    {
        int dprop;  //ms
        int dtrans; //ms per byte
        switch(args.length){
            case 1 :
                System.out.println("Invalid arguments: requires specifying a " +
                        "dprop AND a dtrans.");
                dprop = 100; dtrans = 20;
                System.out.println("Setting dprop = " + dprop +
                        " and dtrans = " + dtrans);
                break;
            case 2 :
                try{
                    dprop = Integer.parseInt(args[0]);
                    dtrans = Integer.parseInt(args[1]);
                } catch(Exception e){
                    System.out.println(e.getMessage());
                    dprop = 100; dtrans = 20;
                    System.out.println("Setting dprop = " + dprop +
                            " and dtrans = " + dtrans);
                }
            default: dprop = 100; dtrans = 20;
        }
        ServerApp s = new ServerApp(dprop, dtrans);
        s.run();
    }

    public ServerApp(int dprop, int dtrans){
        this.format = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
        System.out.println(getCurTime());
        this.dprop = dprop;
        this.dtrans = dtrans;
        transportLayer = new TransportLayer(true, dprop, dtrans);
        this.builder = new HTTPResponseBuilder();
        this.decoder = new HTTPRequestDecoder();
    }

    public String getCurTime(){
        Calendar cal = Calendar.getInstance();
        return (format.format(cal.getTime()));
    }

    public void sendResponse(byte[] response){
        transportLayer.send( response );

        //if the http version is 1.0f, then disconnect (force a handshake again)
        if(decoder.getVersion() == 1.0f){
            transportLayer.disconnect();
        }
    }

    public byte[] receiveRequest(){
        //receive message from client, and send the "received" message back.
        byte[] byteArray = transportLayer.receive();
        return byteArray;
    }

    public byte[] formResponse(byte[] request){
        //send the decoder the request
        decoder.decode(request);

        //server supports both versions
        float version = decoder.getVersion();
        String method = decoder.getMethod();
        //if the method was GET
        if(method.equals("GET")){
            try{
                //load the necessary headers
                String ifmodified = decoder.getHeader("ifmodified");
                String url = decoder.getURL();

                //initialize message, phrase, and status code
                String message = "";
                int statusCode = 0;
                String phrase = "";

                //Create a file object from the url
                File f = new File(url);

                if(ifmodified.isEmpty()){
                    //if it doesn't exist, then its just a plain get request
                    message = language.readFile(f);
                    statusCode = 200;
                    phrase = "OK";
                }
                else{
                    Date dClient = format.parse(ifmodified);
                    Date dCurrent = new Date(f.lastModified());
                    if(dCurrent.after(dClient)){
                        //if the current version is newer than the client's
                        //version, send a 200 and the current version
                        message = language.readFile(f);
                        statusCode = 200;
                        phrase = "OK";
                    }
                    else{
                        //the current version is older or the same as the
                        //client's version, send a 304.
                        message = "";
                        statusCode = 304;
                        phrase ="NOT MODIFIED";
                    }
                }
                return builder.build(version, statusCode, phrase, message);
            }
            catch(Exception e){
                //the file could not be opened, thus we don't know the
                //resource
                return builder.build(version, 404, "NOT FOUND",
                        "The requested resource could not be found");
            }
        }
        else{
            return builder.build(version,404, "NOT FOUND",
                    "Unknown request");
        }
    }


    public void run(){
        while( true )
        {
            byte[] request = receiveRequest();
            if(request==null)
                break;

            String str = new String (request);
            System.out.println( str );

            byte[] response = formResponse(request);
            sendResponse(response);
        }
    }
}
