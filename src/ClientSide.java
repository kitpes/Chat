import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientSide
{
    final Socket s;
    final BufferedReader socketReader;
    final BufferedWriter socketWriter;
    final BufferedReader userInput;

    public ClientSide(String host, int port) throws IOException
    {
        s = new Socket(host, port);
    }
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
