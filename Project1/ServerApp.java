import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
//This class represents the server application
public class ServerApp
{
    private DateFormat format;
    private TransportLayer transportLayer;
    private int dprop;  // ms
    private int dtrans; // ms per byte

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
    }

    public String getCurTime(){
        Calendar cal = Calendar.getInstance();
        return (format.format(cal.getTime()));
    }

    public void sendMessage(String message){
        byte[] byteArray = message.getBytes();
        transportLayer.send( byteArray );
    }

    public byte[] receiveMessage(){
        //receive message from client, and send the "received" message back.
        byte[] byteArray = transportLayer.receive();
        return byteArray;
    }

    public void run(){
        while( true )
        {
            byte[] bytes = receiveMessage();
            if(bytes==null)
                break;
            String str = new String ( bytes );
            System.out.println( str );
            String line = getCurTime() + "received";
            sendMessage(line);
        }
    }
}
