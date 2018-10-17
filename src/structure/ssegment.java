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
import rsa.Rsa;
import hash.Hashs;
import protobuf.MessagePB.Message;
import protobuf.MessagePB.Segment;

public class ssegment 
{
	public static Segment newSegment(String sender, String receiver, String data, String signature) throws Exception
	{
		Segment.Builder _segment = Segment.newBuilder();
		
		_segment.setSender(sender);
		_segment.setReceiver(receiver);
		_segment.setData(data);
		_segment.setSignature(signature);
		_segment.setDchecksum(Hashs.checksum(data).toString());
		_segment.setSchecksum(Hashs.checksum(signature).toString());
		_segment.setType(Segment.SegmentType.NONE);
		Segment segment = _segment.build();
		
		return segment;
	}
	
	public static Segment newSegmentConnect() throws Exception
	{
		Segment.Builder _segment = Segment.newBuilder();
		
		_segment.setSender("0");
		_segment.setReceiver("1");
		_segment.setData("0");
		_segment.setSignature("0");
		_segment.setDchecksum("0");
		_segment.setSchecksum("0");
		_segment.setType(Segment.SegmentType.REQUEST_CONNECT);
		Segment segment = _segment.build();
		
		return segment;
	}
	
	public static Segment newSegmentDisconnect() throws Exception
	{
		Segment.Builder _segment = Segment.newBuilder();
		
		_segment.setSender("0");
		_segment.setReceiver("1");
		_segment.setData("0");
		_segment.setSignature("0");
		_segment.setDchecksum("0");
		_segment.setSchecksum("0");
		_segment.setType(Segment.SegmentType.REQUEST_DISCONNECT);
		Segment segment = _segment.build();
		
		return segment;
	}
	
	public static Segment newSegmentRequestKey(String id) throws Exception
	{
		Segment.Builder _segment = Segment.newBuilder();
		
		_segment.setSender("0");
		_segment.setReceiver("1");
		_segment.setData(id);
		_segment.setSignature(Hashs.checksum(id).toString());
		_segment.setDchecksum("0");
		_segment.setSchecksum("0");
		_segment.setType(Segment.SegmentType.REQUEST_KEY);
		Segment segment = _segment.build();
		
		return segment;
	}
	
	public static Segment newSegmentKey(String pubkey, String verkey) throws Exception
	{
		Segment.Builder _segment = Segment.newBuilder();
		
		_segment.setSender("0");
		_segment.setReceiver("1");
		_segment.setData("0");
		_segment.setSignature("0");
		_segment.setDchecksum("0");
		_segment.setSchecksum("0");
		_segment.setType(Segment.SegmentType.SEND_KEY);
		_segment.setPubkey(pubkey);
		_segment.setVerkey(verkey);
		
		Segment segment = _segment.build();
		
		return segment;
	}
	
	public static Segment newSegmentAccept() throws Exception
	{
		Segment.Builder _segment = Segment.newBuilder();
		
		_segment.setSender("0");
		_segment.setReceiver("1");
		_segment.setData("0");
		_segment.setSignature("0");
		_segment.setDchecksum("0");
		_segment.setSchecksum("0");
		_segment.setType(Segment.SegmentType.ACCEPT_CONNECT);
		Segment segment = _segment.build();
		
		return segment;
	}
	
	public static Segment newSegmentAccept(String mess) throws Exception
	{
		Segment.Builder _segment = Segment.newBuilder();
		
		_segment.setSender("0");
		_segment.setReceiver("1");
		_segment.setData(mess);
		_segment.setSignature("0");
		_segment.setDchecksum(Hashs.checksum(mess).toString());
		_segment.setSchecksum("0");
		_segment.setType(Segment.SegmentType.ACCEPT_CONNECT);
		Segment segment = _segment.build();
		
		return segment;
	}
	
	public static Segment newSegmentReject() throws Exception
	{
		Segment.Builder _segment = Segment.newBuilder();
		
		_segment.setSender("0");
		_segment.setReceiver("1");
		_segment.setData("0");
		_segment.setSignature("0");
		_segment.setDchecksum("0");
		_segment.setSchecksum("0");
		_segment.setType(Segment.SegmentType.REJECT_CONNECT);
		Segment segment = _segment.build();
		
		return segment;
	}

	public static Segment newSegmentNext(String sender, String receiver) throws Exception
	{
		Segment.Builder _segment = Segment.newBuilder();
		
		_segment.setSender(sender);
		_segment.setReceiver(receiver);
		_segment.setData("0");
		_segment.setSignature("0");
		_segment.setDchecksum("0");
		_segment.setSchecksum("0");
		_segment.setType(Segment.SegmentType.NEXT);
		Segment segment = _segment.build();
		
		return segment;
	}
	
	public static Segment newSegmentError(String sender, String receiver) throws Exception
	{
		Segment.Builder _segment = Segment.newBuilder();
		
		_segment.setSender(sender);
		_segment.setReceiver(receiver);
		_segment.setData("0");
		_segment.setSignature("0");
		_segment.setDchecksum("0");
		_segment.setSchecksum("0");
		_segment.setType(Segment.SegmentType.ERROR);
		Segment segment = _segment.build();
		
		return segment;
	}
	
	public static Segment newSegmentRequestChat(String sender, String receiver) throws Exception
	{
		Segment.Builder _segment = Segment.newBuilder();
		
		_segment.setSender(sender);
		_segment.setReceiver(receiver);
		_segment.setData("0");
		_segment.setSignature("0");
		_segment.setDchecksum("0");
		_segment.setSchecksum("0");
		_segment.setType(Segment.SegmentType.REQUEST_CHAT);
		Segment segment = _segment.build();
		
		return segment;
	}
	
	public static Segment newSegmentRequestSendFile(String sender, String receiver) throws Exception
	{
		Segment.Builder _segment = Segment.newBuilder();
		
		_segment.setSender(sender);
		_segment.setReceiver(receiver);
		_segment.setData("0");
		_segment.setSignature("0");
		_segment.setDchecksum("0");
		_segment.setSchecksum("0");
		_segment.setType(Segment.SegmentType.REQUEST_SEND_FILE);
		Segment segment = _segment.build();
		
		return segment;
	}
	
	public static Segment newSegmentFromMessage(String sender, String receiver, Message message, PublicKey pubkey, PrivateKey sigkey) throws Exception
	{
		String mess = message.toString();
		
		byte[] encrypted = Rsa.encrypt(pubkey, mess.getBytes());
		String encryptedmess = Rsa.byteToString(encrypted);
		byte[] signed = Rsa.sign(sigkey, encrypted);
		String signedmess = Rsa.byteToString(signed);
		
		Segment.Builder _segment = Segment.newBuilder();
		
		_segment.setSender(sender);
		_segment.setReceiver(receiver);
		_segment.setData(encryptedmess);
		_segment.setSignature(signedmess);
		_segment.setDchecksum(Hashs.checksum(encryptedmess).toString());
		_segment.setSchecksum(Hashs.checksum(signedmess).toString());
		_segment.setType(Segment.SegmentType.NONE);
		Segment segment = _segment.build();
		
		return segment;
	}

	public static boolean checkSegmentSignature(Segment segment, PublicKey verkey) throws Exception
	{
		byte[] data = Rsa.stringToByte(segment.getData());
		byte[] sign = Rsa.stringToByte(segment.getSignature());
		
		System.out.println(data.length);
		System.out.println(sign.length);
		
		byte[] ver = Rsa.verify(verkey, sign);
		
		if (Rsa.checkEquals(data, ver))
			return true;
		return false;
	}

}
