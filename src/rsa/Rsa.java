package rsa;

import java.nio.charset.Charset;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class Rsa 
{
	public static int blocksize = 240;
	public static void main(String [] args) throws Exception {
        
		KeyPair s = buildKeyPair();
		PrivateKey sk = s.getPrivate();
		PublicKey pk = s.getPublic();
		
		String[] a = splitMessage("conbocuoiconbocuoiconbocuoiconbocuoiconbocuoiconbocuoiconbocuoiconbocuoiconbocuoiconbocuoiconbocuoiconbocuoiconbocuoiconbocuoiconbocuoiconbocuoiconbocuoiconbocuoiconbocuoiconbocuoiconbocuoiconbocuoiconbocuoiconbocuoiconbocuoiconbocuoiconbocuoiconbocuoiconbocuoiconbocuoiconbocuoiconbocuoiconbocuoiconbocuoi");
		System.out.println(a.length);
		System.out.println(a[0]);
		System.out.println(a[a.length - 1]);
		
		String c = "conbocuoi";
		byte[] d = encrypt(pk, c.getBytes());
		byte[] e = decrypt(sk, d);
		
		System.out.println(new String(e));
		
		byte[] f = sign(sk, c.getBytes());
		byte[] g = verify(pk, f);
		
		System.out.println(new String(g));
		byte[] t = c.getBytes();

    }

    public static KeyPair buildKeyPair() throws NoSuchAlgorithmException {
        final int keySize = 3072;
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keySize);      
        return keyPairGenerator.genKeyPair();
    }

    public static String[] splitMessage(String message)
    {
    	String[] messages = new String[(message.length() + blocksize - 1) / blocksize];
    	
    	int index = 0, i = 0;
    	while (index < message.length())
    	{
    		messages[i] = message.substring(index, Math.min(index + blocksize, message.length()));
    		index += blocksize;
    		i ++;
    	}
		return messages;
    }
    
    public static byte[] sign(PrivateKey signingKey, byte[] message) throws Exception {
    	Cipher cipher = Cipher.getInstance("RSA");
    	cipher.init(Cipher.ENCRYPT_MODE, signingKey);
    	
    	return cipher.doFinal(message);
    }
    
    public static byte[] verify(PublicKey verifyingkey, byte[] message) throws Exception {
    	Cipher cipher = Cipher.getInstance("RSA");
    	cipher.init(Cipher.DECRYPT_MODE, verifyingkey);
    	
    	return cipher.doFinal(message);
    }
    
    public static byte[] encrypt(PublicKey publicKey, byte[] message) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");  
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);  

        return cipher.doFinal(message);  
    }

    public static byte[] decrypt(PrivateKey privateKey, byte [] encrypted) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");  
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        
        return cipher.doFinal(encrypted);
    }
    
    public static PublicKey getPublicKey(byte[] encodedKey) throws Exception
    {
        KeyFactory factory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec encodedKeySpec = new X509EncodedKeySpec(encodedKey);
        return factory.generatePublic(encodedKeySpec);
    }
    
    public static String byteToString(byte[] b)
    {
    	String result = String.valueOf(b[0]);
    	
    	for (int i = 1; i < b.length; i ++)
    	{
    		result += " " + String.valueOf(b[i]);
    	}
    	
    	return result;
    }
   
    public static byte[] stringToByte(String s)
    {
    	String[] _s = s.split(" ");
    	byte[] b = new byte[_s.length];
    	for (int i = 0; i < _s.length; i ++)
    	{
    		b[i] = (byte) Integer.parseInt(_s[i]);
    	}
    	return b;
    	
    }
    
    public static boolean checkEquals(byte[] a, byte[] b)
    {
    	if (a.length != b.length)
    		return false;
    	for (int i = 0; i < a.length; i ++)
    	{
    		if (a[i] != b[i])
    			return false;
    	}
    	return true;
    }
    
}
