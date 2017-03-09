import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * This class represents the server application
 *
 * @author Darren Norton, Yizhong Chen
 * @since Feb-19th-2017
 */
public class ServerApp {
    protected DateFormat format;
    protected TransportLayer transportLayer;
    protected int dprop;  // ms
    protected int dtrans; // ms per byte
    protected HTTPRequestDecoder requestDecoder;
    protected HTTPResponseEncoder responseEncoder;
    protected MyMarkUp language;

    public static void main(String[] args) throws Exception {
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
            ServerApp s = new ServerApp(dprop, dtrans);
            s.run();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Bye!");
            System.exit(-1);
        }
    }

    /**
     * This is a constructor for ServerApp
     * Form a TransportLayer, get delay time, format, responseEncoder, requestDecoder and language
     */
    public ServerApp(int dprop, int dtrans) {
        this.format = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
        this.dprop = dprop;
        this.dtrans = dtrans;
        transportLayer = new TransportLayer(true, dprop, dtrans);
        this.responseEncoder = new HTTPResponseEncoder();
        this.requestDecoder = new HTTPRequestDecoder();
        this.language = new MyMarkUp();
    }

    public String getCurTime() {
        Calendar cal = Calendar.getInstance();
        return (format.format(cal.getTime()));
    }

    public void sendResponse(byte[] response) {
        transportLayer.send(response);
    }

    public byte[] receiveRequest() {
        //receive message from client, and send the "received" message back.
        byte[] byteArray = transportLayer.receive();
        return byteArray;
    }

    public HTTPResponse formResponse(HTTPRequest req) {
        //server supports both versions
        float version = req.getVersion();
        String method = req.getMethod();

        //if the method was "GET" handle it by GET
        if (method.equals("GET")) {
            HTTPResponse resp = handleGET(req);
            return resp;
        } else {
            HTTPResponse resp = new HTTPResponse(version, 404, "NOT FOUND");
            resp.setBody("Unknown method of request");
            return resp;
        }
    }


    public void run() {
        while (true) {
            byte[] request = receiveRequest();
            if (request == null)
                break;
            HTTPRequest req = requestDecoder.decode(request);
            System.out.println("*************************************************");
            System.out.println("ServerApp received request: \n" + req);
            System.out.println("*************************************************\n");

            HTTPResponse resp = formResponse(req);
            System.out.println("*************************************************");
            System.out.println("ServerApp sent response: \n" + resp);
            System.out.println("*************************************************\n");

            byte[] response = responseEncoder.encode(resp);

            sendResponse(response);

            //if the http version is 1.0f, then disconnect (force a handshake again)
            if (resp.getVersion() == 1.0f) {
                transportLayer.disconnect();
            }
        }
    }

    public HTTPResponse handleGET(HTTPRequest req) {
        float version = req.getVersion();
        try {
            //load the necessary headers
            String ifmodified = req.getHeader("ifmodified");
            String url = req.getUrl();
            //initialize message, phrase, and status code
            String message = "";
            int statusCode = 0;
            String phrase = "";

            //Create a file object from the url
            File f = new File(url);
            if (ifmodified.isEmpty()) {
                //if it doesn't exist, then its just a plain get request
                message = language.readFile(f);
                statusCode = 200;
                phrase = "OK";
            } else {
                System.out.println("Server received ifmodified header!");
                Date dClient = format.parse(ifmodified);
                Date dCurrent = new Date(f.lastModified());
                System.out.println("Client version = " + dClient);
                System.out.println("Server version = " + dCurrent);
                if (dCurrent.after(dClient)) {
                    System.out.println("CLIENT HAS OUTDATED VERSION, send 200\n");
                    //if the current version is newer than the client's
                    //version, send a 200 and the current version
                    message = language.readFile(f);
                    statusCode = 200;
                    phrase = "OK";
                } else {
                    System.out.println("CLIENT HAS CORRECT VERSION, send 304\n");
                    //the current version is older or the same as the
                    //client's version, send a 304.
                    message = "";
                    statusCode = 304;
                    phrase = "NOT MODIFIED";
                }
            }
            HTTPResponse resp = new HTTPResponse(version, statusCode, phrase);
            resp.setBody(message);
            return resp;
        } catch (Exception e) {
            //the file could not be opened, thus we don't know the
            //resource
            HTTPResponse resp = new HTTPResponse(version, 404, "NOT FOUND");
            resp.setBody("The requested resource could not be found.");
            return resp;
        }
    }
}
