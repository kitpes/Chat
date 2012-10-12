import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class ServerSide 
{
    private ServerSocket ss;
    private int port;
    private Thread serverThread;
    private BlockingDeque<SocketProcessor> q = new LinkedBlockingDeque<SocketProcessor>();

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
            new ServerSide(1234).run();
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
        public void run()
        {
            while(!s.isClosed())
            {
                String line = null;
                try
                {
                    line = br.readLine();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    close();
                }

                if(line == null)
                {
                    close();
                }
                else if ("shutdown ".equals(line))
                {
                    serverThread.interrupt();
                    try
                    {
                        new Socket("localhost", port);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    finally
                    {
                        shutDownServer();
                    }
                }
                else
                {
                    for(SocketProcessor sp : q)
                    {
                        sp.send(line);
                    }
                }
            }
        }

        public synchronized void send(String line)
        {
            try {
                bw.write(line);
                bw.write("\n");
                bw.flush();
            }
            catch (IOException e)
            {
                e.printStackTrace();
                close();
            }
        }

        public synchronized void close()
        {
            q.remove(this);
            if (!s.isClosed())
            {
                try
                {
                    s.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        protected void finalize() throws Throwable
        {
            super.finalize();
            close();
        }
    }
}
