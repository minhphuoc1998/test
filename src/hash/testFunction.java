package hash;

import java.math.*;

public class testFunction 
{
	public static void main(String[] args) throws Exception
	{
		Hashs a = new Hashs();
		String c = "b24fe425ca66288b87a0c7d0f909f879477b2c37941c0bbd095fb3e9dd68d72a";
		BigInteger d = a.checksum(c);
		System.out.println(d.toString());
		
	}
}
