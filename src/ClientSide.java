import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientSide
{
    public static void main(String[] args)
    {
        try
        {
            if (args.length != 2)
                System.out.println("Wrong number of arguments.");
            String host = args[0];
            int port = Integer.parseInt(args[1]);

            Socket connecting = new Socket(host, port);

            InputStream inStream = connecting.getInputStream();
            OutputStream outStream = connecting.getOutputStream();

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        /*try
        {
            Socket connecting = new Socket(host, port);
            InputStream inStream = connecting.getInputStream();
            OutputStream outStream = connecting.getOutputStream();

            Scanner inFromServer = new Scanner(inStream);
            PrintWriter outToServer = new PrintWriter(outStream, true);

            System.out.println("Connection to the server " + host + ":" + port + " is established.");

            String mes = "trololo";
            outToServer.print(mes);
            String line = inFromServer.nextLine();
            System.out.println(line);


        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {

        }*/
    }
}
