import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketPermission;
import java.util.ArrayList;
import java.util.Scanner;

public class ServerSide 
{
    private ServerSocket ss;
    private int port;
    private Thread serverThread;

    public  ServerSide(int port) throws IOException
    {
        ss = new ServerSocket(port);
        this.port = port;
    }

    void run()
    {
        serverThread = Thread.currentThread();
        while (true)
        {
            Socket s = getNewConnection();
            if(serverThread.isInterrupted())
            {
                break;
            }
            else if (s != null)
            {
                try
                {
                    final SocketProcessor processor = new SocketProcessor(s);
                    final Thread thread = new Thread(processor);
                    thread.setDaemon(true);
                    thread.start();
                    q.offer(processor);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private Socket getNewConnection()
    {
        Socket s = null;
        try
        {
            s = ss.accept();
        }
        catch (IOException e)
        {
            shutDownServer();
        }
        return s;
    }

    private synchronized void shutDownServer()
    {
        for (SocketProcessor s : q)
        {
            s.close();
        }
        if (!ss.isClosed())
        {
            try
            {
                ss.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args)
	{
        try
        {
            new ServerSide(1234);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private class SocketProcessor implements Runnable
    {
        Socket s;
        BufferedReader br;
        BufferedWriter bw;

        SocketProcessor(Socket socketParam) throws IOException
        {
            s = socketParam;
            br = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"));
            bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), "UTF-8"));
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