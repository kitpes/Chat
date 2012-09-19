import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ServerSide 
{
	public static void main(String[] args)
	{
		try 
		{
			int i = 1;
            ServerSocket serverSocket = new ServerSocket(1234);
            ArrayList<Runnable> connections = new ArrayList<Runnable>();

            while (true)
            {
                Socket incoming = serverSocket.accept();
                System.out.println("Spawning " + i);
                Runnable r = new ThreadedEchoHandler(incoming, i);
                Thread t = new Thread(r);
                connections.add(t);

                t.start();
                i++;
            }


        }
        catch (IOException e)
        {
            e.printStackTrace();
		}
	}
}

class ThreadedEchoHandler implements Runnable
{
    private Socket incoming;
    private int counter;

    public ThreadedEchoHandler(Socket i, int c)
    {
        incoming = i;
        counter = c;
    }

    public void run()
    {
        try
        {
            try
            {
                InputStream inStream = incoming.getInputStream();
                OutputStream outStream = incoming.getOutputStream();

                Scanner in = new Scanner(inStream);
                PrintWriter out = new PrintWriter(outStream, true);

                boolean done = false;
                while (!done &&in.hasNextLine())
                {
                    String line = in.nextLine();
                    out.println("Echo: " + line);
                    if (line.trim().equals("BYE"))
                        done = true;
                }
            }
            finally
            {
                incoming.close();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}