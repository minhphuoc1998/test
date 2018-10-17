package server;

import java.io.*;
import java.net.*;
import java.security.*;

import com.google.protobuf.TextFormat;

import hash.Hashs;
import protobuf.MessagePB.Message;
import protobuf.MessagePB.Segment;

import structure.*;
import rsa.*;

public class IdentifyThread implements Runnable
{
	public int clientNumber;
	String identifier;
	public Socket socketOfServer;
	public DataInputStream dis;
	public DataOutputStream dos;
	
	public IdentifyThread(Socket socketOfServer, int clientNumber) throws Exception
	{
		this.socketOfServer = socketOfServer;
		this.clientNumber = clientNumber;
		this.dis = new DataInputStream(this.socketOfServer.getInputStream());
		this.dos = new DataOutputStream(this.socketOfServer.getOutputStream());
		this.identifier = Hashs.sha256(String.valueOf(clientNumber));
	}
	
	public void disconnect() throws Exception
	{
		socketOfServer.close();
		dis.close();
		dos.close();
	}

	public void acceptConnect() throws Exception
	{
		Segment accept = ssegment.newSegmentAccept(this.identifier);
		sendSegment(accept);
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
	
	@Override
	public void run()
	{
		try
		{
			// Receive request connect
			receiveSegment();
			acceptConnect();
			
			// Receive client information
			Segment clientInfo = receiveSegment();
			
			// Get public key and verificaton key
			PublicKey pubkey = Rsa.getPublicKey(Rsa.stringToByte(clientInfo.getPubkey()));
			PublicKey verkey = Rsa.getPublicKey(Rsa.stringToByte(clientInfo.getPubkey()));
			
			// Insert pub and ver key to table
			ClientInfo clientinfo = new ClientInfo(pubkey, verkey, socketOfServer);
			
			Server.ServerClientTable.clientTable.put(identifier, clientinfo);
			System.out.println("Added: " + identifier);
			
			
			if (Server.clientNumber < 1000)
			{
				acceptConnect();
				ServiceThread servicethread = new ServiceThread(this.socketOfServer, this.identifier);
				Thread clienthandler = new Thread(servicethread);
				clienthandler.start();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			
		}
	}
}
