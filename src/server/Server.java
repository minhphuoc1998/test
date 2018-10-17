package server;

import java.io.*;
import java.net.*;

import rsa.*;
import structure.*;

public class Server implements Runnable
{

	public ServerSocket listener;
	public static ClientTable ServerClientTable;
	public static int clientNumber;
	public int port;
	
//	public static InitializeClientTable
	
	public Server()
	{
		clientNumber = 0;
		ServerClientTable = new ClientTable();
		ServerClientTable.initial();
		try
		{
			port = 7777;
			System.out.println("Server starting at port " + port);
			listener = new ServerSocket(port);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		try
		{
			while (true)
			{
				clientNumber ++;
				Socket socketOfServer = listener.accept();
				System.out.println("A new client is connected");
				
				IdentifyThread new_thread = new IdentifyThread(socketOfServer, clientNumber);
				Thread thread = new Thread(new_thread);
				thread.start();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				listener.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
	}
	
}
