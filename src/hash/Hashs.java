package hash;

import java.io.*;
import java.math.*;
import java.nio.charset.StandardCharsets;
import java.security.*;

public class Hashs 
{	
	public static String sha256(String message) throws Exception
	{
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] hashedmessage = digest.digest(message.getBytes(StandardCharsets.UTF_8));
		String result = "";
		for (byte b : hashedmessage)
		{
			result += String.format("%02x", b);
		}
		return result;
	}
	
	public static BigInteger checksum(Object obj) throws Exception {

	    if (obj == null) {
	      return BigInteger.ZERO;   
	    }

	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    ObjectOutputStream oos = new ObjectOutputStream(baos);
	    oos.writeObject(obj);
	    oos.close();

	    MessageDigest m = MessageDigest.getInstance("SHA1");
	    m.update(baos.toByteArray());

	    return new BigInteger(1, m.digest());
	}
	
}
