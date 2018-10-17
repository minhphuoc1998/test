package server;

import java.util.Hashtable;

import rsa.*;

import java.net.Socket;
import java.security.*;

public class ClientTable 
{
	public  Hashtable<String, ClientInfo> clientTable;
	
	public void initial()
	{
		clientTable = new Hashtable<String, ClientInfo>();
	}
	
	public void insert(String id, ClientInfo clientinfo)
	{
		clientTable.put(id, clientinfo);
	}
	
	public void remove(String id)
	{
		clientTable.remove(id);
	}
	
	public boolean contain(String id)
	{
		return clientTable.containsKey(id);
	}
	
	public static void main(String[] args) throws Exception
	{
//		ClientTable a = 
	}
}
