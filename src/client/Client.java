package client;

import java.io.*;
import java.net.*;
import java.security.*;
import java.util.Scanner;

import com.google.protobuf.TextFormat;

import protobuf.MessagePB.Message;
import protobuf.MessagePB.Segment;

import structure.*;
import rsa.*;
import hash.*;
import server.ClientTable;
import server.ClientInfo;

public class Client implements Runnable 
{
	public String host;
	public int port;
	public Socket socket;
	public DataInputStream dis;
	public DataOutputStream dos;
	public String identifier;
	public ClientTable clienttable;
	public Scanner scn;
	public PrivateKey prikey;
	public PrivateKey sigkey;
	
	public Client(String host, int port)
	{
		this.host = host;
		this.port = port;
		clienttable = new ClientTable();
		clienttable.initial();
		scn = new Scanner(System.in);
	}
	
	public boolean connect() throws Exception
	{
		// Initialize socket
		socket = new Socket(host, port);
		dis = new DataInputStream(socket.getInputStream());
		dos = new DataOutputStream(socket.getOutputStream());
		
		// Generate Public Key and Private Key
		KeyPair firstKeyPair = Rsa.buildKeyPair();
		prikey = firstKeyPair.getPrivate();
		PublicKey pubKey = firstKeyPair.getPublic();
		String pubkey = Rsa.byteToString(pubKey.getEncoded());
		
		// Generate Signing Key and Verifying Key
		KeyPair secondKeyPair = Rsa.buildKeyPair();
		sigkey = secondKeyPair.getPrivate();
		PublicKey verKey = secondKeyPair.getPublic();
		String verkey = Rsa.byteToString(verKey.getEncoded());
		
		// Send Key
		Segment requestConnect = ssegment.newSegmentConnect();
		sendSegment(requestConnect);
		
		// Receive and Save Identifier
		Segment received = receiveSegment();
		this.identifier = received.getData();
		
		Segment sendKey = ssegment.newSegmentKey(pubkey, verkey);
		sendSegment(sendKey);
		
		// Receive Agreement of Server
		received = receiveSegment();
		
		System.out.println(identifier);
		
		if (received.getType().equals(Segment.SegmentType.ACCEPT_CONNECT))
			return true;
		return false;
	}

	public boolean getPublicKey(String id) throws Exception
	{
		if (this.clienttable.clientTable.containsKey(id))
		{
			return true;
		}
		if (getPublicKeyServer(id))
		{
			return true;
		}
		return false;
	}
	
	public boolean getPublicKeyServer(String id) throws Exception
	{
		// Request key
		Segment request = ssegment.newSegmentRequestKey(id);
		sendSegment(request);
		Segment received = receiveSegment();
		if (received.getType().equals(Segment.SegmentType.SEND_KEY))
		{
			PublicKey clientpubkey = Rsa.getPublicKey(Rsa.stringToByte(received.getPubkey()));
			PublicKey clientverkey = Rsa.getPublicKey(Rsa.stringToByte(received.getPubkey()));
			
			ClientInfo clientinfo = new ClientInfo(clientpubkey, clientverkey, null);
			
			clienttable.clientTable.put(id, clientinfo);
			return true;
		}
		return false;
	}
	
	public boolean sendMessage(String id, String message) throws Exception
	{
		// Initialize messages
		String[] messages = Rsa.splitMessage(message);
		int numMess = messages.length;
		
		int sentMess = 0;

		// Get publickey and verification key of client
		PublicKey pubkey, verkey;
		
		getPublicKey(id);

		if (getPublicKey(id))
		{
			pubkey = clienttable.clientTable.get(id).getPublic();
			verkey = clienttable.clientTable.get(id).getVerify();
		}
		else
		{
			return false;
		}
		
		System.out.println(2);
		// Request to send message
		Message mrequest = smessage.newMessageRequestChat(String.valueOf(numMess));
		System.out.println(mrequest.toString());
		Segment srequest = ssegment.newSegmentFromMessage(identifier, id, mrequest, pubkey, sigkey);
		sendSegment(srequest);


		// Reply
		Segment sreceived = receiveSegment();
		System.out.println(2);
		// Check Signature
		if (!ssegment.checkSegmentSignature(sreceived, verkey))
		{
			return false;
		}
		System.out.println(2);

		// Check if client want to receive
		Message mreceived = smessage.newMessageFromSegment(sreceived, prikey);
		if (!mreceived.getType().equals(Message.MessageType.A))
		{
			return false;
		}
		
		while (sentMess < numMess)
		{
			System.out.println(2);
			// Initialize block
			Message mblock = smessage.newMessageChat(messages[sentMess]);
			System.out.println(2);
			Segment sblock = ssegment.newSegmentFromMessage(identifier, id, mblock, pubkey, sigkey);
			
			System.out.println(1);
			// Send block
			sendSegment(sblock);
			
			System.out.println(1);
			// Check if the other client receives the correct block
			boolean ok = false;
			while (!ok)
			{
				 Segment sreceived2 = receiveSegment();
				 // Check signature
				 if (!ssegment.checkSegmentSignature(sreceived2, verkey))
				 {
					 continue;
				 }
				 
				 Message smessage2 = smessage.newMessageFromSegment(sreceived2, prikey);
				 
				 if (smessage2.getType().equals(Message.MessageType.T))
				 {
					 ok = true;
					 sentMess ++;
				 }
				 else if (smessage2.getType().equals(Message.MessageType.E))
				 {
					 sendSegment(sblock);
				 }
				 else
				 {
					 return false;
				 }
			}
		}
		return true;
	}
	
	public boolean receiveMessage(int length) throws Exception
	{
		
		String result = "";
		
		for (int i = 0; i < length; i ++)
		{
			// Receive Segment
			Segment sreceived = receiveSegment();

			// Get verification key of Sender
			String sender = sreceived.getSender();
			if (!getPublicKey(sender))
			{
				return false;
			}
			
			PublicKey verkey = clienttable.clientTable.get(sender).getVerify();
			PublicKey pubkey = clienttable.clientTable.get(sender).getPublic();
			
			// Checksum and Signature
			if (!ssegment.checkSegmentSignature(sreceived, verkey))
			{
				return false;
			}
			
			// Take message
			Message mreceived = smessage.newMessageFromSegment(sreceived, prikey);
			if (mreceived.getType().equals(Message.MessageType.F))
			{
				return false;
			}
			
			// Add to result
			result += mreceived.getMess();
			
			// Request next message
			Message mrequest = smessage.newMessageNext();
			Segment srequest = ssegment.newSegmentFromMessage(identifier, sender, mrequest, pubkey, sigkey);
			sendSegment(srequest);
		}
		System.out.println(result);
		return true;
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
	public void run() {
		try
		{
			while (true)
			{
				String command = scn.nextLine();
				if (command.equals("send"))
				{
					System.out.println("id:");
					String id = scn.nextLine();
					System.out.println("message:");
					String message = scn.nextLine();
					sendMessage(id, message);
				}
				
				if (dis.available() > 0)
				{
					Segment sreceived = receiveSegment();
					Message mreceived = smessage.newMessageFromSegment(sreceived, prikey);
					
					System.out.println(mreceived);
					if (mreceived.getType().equals(Message.MessageType.RC))
					{
						String mess = mreceived.getMess();
						int len = Integer.parseInt(mess);
						
						receiveMessage(len);
					}
				}
				
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}

class ClientRead implements Runnable
{
	public Socket socket;
	public DataInputStream dis;
	public DataOutputStream dos;
	public String identifier;
	public ClientTable clienttable;
	public Scanner scn;
	public PrivateKey prikey;
	public PrivateKey sigkey;
	
	public ClientRead(Socket socket, DataInputStream dis, DataOutputStream dos,
			String identifier, ClientTable clienttable, Scanner scn, PrivateKey prikey, PublicKey pubkey)
	{
		this.socket = socket;
		this.dis = dis;
		this.dos = dos;
		this.identifier = identifier;
		this.clienttable = clienttable;
		this.scn = scn;
		this.prikey = prikey;
		this.sigkey = sigkey;
	}
	
	public boolean getPublicKey(String id) throws Exception
	{
		if (this.clienttable.clientTable.containsKey(id))
		{
			return true;
		}
		if (getPublicKeyServer(id))
		{
			return true;
		}
		return false;
	}
	
	public boolean getPublicKeyServer(String id) throws Exception
	{
		Segment request = ssegment.newSegmentRequestKey(id);
		sendSegment(request);
		Segment received = receiveSegment();
		if (received.getType().equals(Segment.SegmentType.SEND_KEY))
		{
			PublicKey clientpubkey = Rsa.getPublicKey(Rsa.stringToByte(received.getPubkey()));
			PublicKey clientverkey = Rsa.getPublicKey(Rsa.stringToByte(received.getPubkey()));
			
			ClientInfo clientinfo = new ClientInfo(clientpubkey, clientverkey, null);
			
			clienttable.clientTable.put(id, clientinfo);
			return true;
		}
		return false;
	}
	
	public boolean sendMessage(String id, String message) throws Exception
	{
		
		// Initialize messages
		String[] messages = Rsa.splitMessage(message);
		int numMess = messages.length;
		int sentMess = 0;
				
				
		// Get publickey and verification key of client
		PublicKey pubkey, verkey;
		if (getPublicKey(id))
		{
			pubkey = clienttable.clientTable.get(id).getPublic();
			verkey = clienttable.clientTable.get(id).getVerify();
		}
		else
		{
			return false;
		}
		
		// Request to send message
		Message mrequest = smessage.newMessageRequestChat(String.valueOf(numMess));
		Segment srequest = ssegment.newSegmentFromMessage(identifier, id, mrequest, pubkey, sigkey);
		sendSegment(srequest);
		
		// Reply
		Segment sreceived = receiveSegment();
		
		// Check Signature
		if (!ssegment.checkSegmentSignature(sreceived, verkey))
		{
			return false;
		}
		
		// Check if client want to receive
		Message mreceived = smessage.newMessageFromSegment(sreceived, prikey);
		if (!mreceived.getType().equals(Message.MessageType.A))
		{
			return false;
		}
		
		while (sentMess < numMess)
		{
			// Initialize block
			Message mblock = smessage.newMessageChat(messages[sentMess]);
			Segment sblock = ssegment.newSegmentFromMessage(identifier, id, mblock, pubkey, sigkey);
			
			// Send block
			sendSegment(sblock);
			
			// Check if the other client receives the correct block
			boolean ok = false;
			while (!ok)
			{
				 Segment sreceived2 = receiveSegment();
				 // Check signature
				 if (!ssegment.checkSegmentSignature(sreceived2, verkey))
				 {
					 continue;
				 }
				 
				 Message smessage2 = smessage.newMessageFromSegment(sreceived2, prikey);
				 
				 if (smessage2.getType().equals(Message.MessageType.T))
				 {
					 ok = true;
					 sentMess ++;
				 }
				 else if (smessage2.getType().equals(Message.MessageType.E))
				 {
					 sendSegment(sblock);
				 }
				 else
				 {
					 return false;
				 }
			}
		}
		return true;
	}
	
	public boolean receiveMessage(int length) throws Exception
	{
		
		String result = "";
		
		for (int i = 0; i < length; i ++)
		{
			// Receive Segment
			Segment sreceived = receiveSegment();

			// Get verification key of Sender
			String sender = sreceived.getSender();
			if (!getPublicKey(sender))
			{
				return false;
			}
			
			PublicKey verkey = clienttable.clientTable.get(sender).getVerify();
			PublicKey pubkey = clienttable.clientTable.get(sender).getPublic();
			
			// Checksum and Signature
			if (!ssegment.checkSegmentSignature(sreceived, verkey))
			{
				return false;
			}
			
			// Take message
			Message mreceived = smessage.newMessageFromSegment(sreceived, prikey);
			if (mreceived.getType().equals(Message.MessageType.F))
			{
				return false;
			}
			
			// Add to result
			result += mreceived.getMess();
			
			// Request next message
			Message mrequest = smessage.newMessageNext();
			Segment srequest = ssegment.newSegmentFromMessage(identifier, sender, mrequest, pubkey, sigkey);
			sendSegment(srequest);
		}
		System.out.println(result);
		return true;
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
	public void run() {
		while (true)
		{
			
		}
	}
}
