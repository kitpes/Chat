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

            Scanner inFromServer = new Scanner(connecting.getInputStream());
            PrintWriter outToServer = new PrintWriter(connecting.getOutputStream(), true);
            Scanner fromConsol = new Scanner(System.in);

            String fromUser, fromServer;
            boolean done = false;
            while (!done && fromConsol.hasNextLine())
            {
                fromUser = fromConsol.nextLine();
                outToServer.println(fromUser);
                fromServer = inFromServer.nextLine();
                System.out.println(fromServer);
                if (fromUser.trim().equals("BYE"))
                {
                    done = true;
                }
            }

            connecting.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
