import javax.sound.midi.Receiver;
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
        socketReader = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"));
        socketWriter = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), "UTF-8"));
        userInput = new BufferedReader(new InputStreamReader(System.in));
        new Thread(new Receiver()).start();
    }

    public void run()
    {
        System.out.println("Type phrase(s) (hit Enter to exit):");
        while (true)
        {
            String userString = null;
            try{
                userString = userInput.readLine();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            if (userString == null || userString.length() == 0 || s.isClosed())
            {
                close();
                break;
            }
            else
            {
                try
                {
                    socketWriter.write("\n");
                    socketWriter.flush();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    close();
                }
            }

        }
    }

    public synchronized void close()
    {
        if(!s.isClosed())
        {
            try
            {
                s.close();
                System.exit(0);
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
            new ClientSide("127.0.0.1", 1234).run();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println("Unable to connect. Server is not running?");
        }
    }

    private class Receiver implements Runnable
    {
        public void run()
        {
            while (!s.isClosed())
            {
                String line = null;
                try
                {
                    line = socketReader.readLine();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    if ("Socket closed".equals(e.getMessage()))
                    {
                        break;
                    }
                    System.out.println("Connection lost");
                    close();
                }

                if (line == null)
                {
                    System.out.println("Server has closed connection.");
                    close();
                }
                else
                {
                    System.out.println("Server: " + line);
                }
            }
        }
    }
}
