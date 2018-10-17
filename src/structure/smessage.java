package structure;

import java.nio.charset.Charset;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import com.google.protobuf.TextFormat;

import rsa.Rsa;

import protobuf.MessagePB.Message;
import protobuf.MessagePB.Segment;

public class smessage 
{
	public static Message newMessage(Message.MessageType type, String mess)
	{
		Message.Builder _message = Message.newBuilder();
		_message.setType(type);
		_message.setMess(mess);
		
		Message message = _message.build();
		
		return message;
	}
	
	public static Message newMessageChat(String mess)
	{
		Message.Builder _message = Message.newBuilder();
		_message.setType(Message.MessageType.C);
		_message.setMess(mess);
		
		Message message = _message.build();
		
		return message;
	}
	
	public static Message newMessageRequestViewScreen()
	{
		Message.Builder _message = Message.newBuilder();
		_message.setType(Message.MessageType.RV);
		
		Message message = _message.build();
		
		return message;
	}
	
	public static Message newMessageRequestSendFile()
	{
		Message.Builder _message = Message.newBuilder();
		_message.setType(Message.MessageType.RF);
		
		Message message = _message.build();
		
		return message;
	}
	
	public static Message newMessageRequestChat(String mess)
	{
		Message.Builder _message = Message.newBuilder();
		_message.setType(Message.MessageType.RC);
		_message.setMess(mess);
		Message message = _message.build();
		
		return message;
	}
	
	public static Message newMessageAccept()
	{
		Message.Builder _message = Message.newBuilder();
		_message.setType(Message.MessageType.A);
		
		Message message = _message.build();
		
		return message;
	}
	
	public static Message newMessageReject()
	{
		Message.Builder _message = Message.newBuilder();
		_message.setType(Message.MessageType.R);
		
		Message message = _message.build();
		
		return message;
	}
	
	public static Message newMessageNext()
	{
		Message.Builder _message = Message.newBuilder();
		_message.setType(Message.MessageType.T);
		
		Message message = _message.build();
		
		return message;
	}
	
	public static Message newMessageError()
	{
		Message.Builder _message = Message.newBuilder();
		_message.setType(Message.MessageType.E);
		
		Message message = _message.build();
		
		return message;
	}
	
	public static Message newMessageFromSegment(Segment segment, PrivateKey prikey) throws Exception
	{
		byte[] encrypted = Rsa.stringToByte(segment.getData());
		byte[] decrypted = Rsa.decrypt(prikey, encrypted);
		
		String data = Rsa.byteToString(decrypted);
		Message.Builder _message = Message.newBuilder();
		TextFormat.getParser().merge(data, _message);
		Message message = _message.build();
		
		return message;
	}

}
