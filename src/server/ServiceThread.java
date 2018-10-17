package server;

import java.io.*;
import java.net.*;

import java.io.*;
import java.net.*;
import java.security.*;
import java.util.Scanner;

import com.google.protobuf.TextFormat;

import protobuf.MessagePB.Message;
import protobuf.MessagePB.Segment;
import rsa.Rsa;
import structure.ssegment;

public class ServiceThread implements Runnable
{

	public String identifier;
	public Socket socketOfServer;
	public DataInputStream dis;
	public DataOutputStream dos;
	
	public ServiceThread(Socket socketOfServer, String identifier) throws Exception
	{
		this.identifier = identifier;
		this.socketOfServer = socketOfServer;
		this.dis = new DataInputStream(this.socketOfServer.getInputStream());
		this.dos = new DataOutputStream(this.socketOfServer.getOutputStream());
	}
	
	public void disconnect() throws Exception
	{
		socketOfServer.close();
		dis.close();
		dos.close();
	}
	
	public void sendSegment(Segment segment) throws Exception
	{
		dos.writeUTF(segment.toString());
	}
	
	public Segment receiveSegment() throws Exception
	{
		String raw = dis.readUTF();
		Segment.Builder _segment = Segment.newBuilder();
		TextFormat.getParser().merge(raw, _segment);
		Segment segment = _segment.build();
		
		return segment;
	}
	
	public boolean getPublicKeyServer(String id) throws Exception
	{
		if (!Server.ServerClientTable.clientTable.containsKey(id))
		{
			return false;
		}
		
		PublicKey pubKey = Server.ServerClientTable.clientTable.get(id).getPublic();
		PublicKey verKey = Server.ServerClientTable.clientTable.get(id).getVerify();
		
		String pubkey = Rsa.byteToString(pubKey.getEncoded());
		String verkey = Rsa.byteToString(verKey.getEncoded());
		
		Segment sendKey = ssegment.newSegmentKey(pubkey, verkey);
		sendSegment(sendKey);
		return true;
	}
	
	@Override
	public void run() 
	{
		try
		{
			while (true)
			{
				System.out.println("Hello");
				Segment sreceived = receiveSegment();
				
				System.out.println("Received: ");
				System.out.println(sreceived.toString());
				
				if (sreceived.getType().equals(Segment.SegmentType.REQUEST_KEY))
				{
					String id = sreceived.getData();
					
					System.out.println(id);
					System.out.println(Server.ServerClientTable.clientTable.containsKey(id));
					
					getPublicKeyServer(id);
					continue;
				}
				
				String id = sreceived.getReceiver();
				
				System.out.println(id);
				System.out.println(Server.ServerClientTable.clientTable.containsKey(id));
				
				Socket socket = Server.ServerClientTable.clientTable.get(id).getSocket();
				
				DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
				System.out.println("Send: ");
				System.out.println(sreceived.toString());
				dos.writeUTF(sreceived.toString());
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
				disconnect();
			} catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		
	}
	
	
}
