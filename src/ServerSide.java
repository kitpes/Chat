import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ServerSide 
{
	public static void main(String[] args)
	{
		try 
		{
			ServerSocket serverSocket = new ServerSocket(1234);
			Socket incoming = serverSocket.accept();
			
			try 
			{
				InputStream inStream = incoming.getInputStream();
				OutputStream outStream = incoming.getOutputStream();
				
				Scanner in = new Scanner(inStream);
				PrintWriter out = new PrintWriter(outStream, true);
				
				out.println("Hello! Enter EXIT to exit.");
				
				boolean done = false;
				while (!done && in.hasNextLine())
				{
					String line = in.nextLine();
					out.println("Echo: " + line);
					if (line.trim().equals("EXIT")) done = true;
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
