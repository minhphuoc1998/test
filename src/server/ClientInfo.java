package server;

import java.net.Socket;
import java.security.PublicKey;

public class ClientInfo extends Object
{
	PublicKey pubkey;
	PublicKey verkey;
	Socket socket;
	
	public ClientInfo(PublicKey pubkey, PublicKey verkey, Socket socket)
	{
		this.pubkey = pubkey;
		this.verkey = verkey;
		this.socket = socket;
	}
	
	public PublicKey getPublic()
	{
		return this.pubkey;
	}
	
	public PublicKey getVerify()
	{
		return this.verkey;
	}
	
	public Socket getSocket()
	{
		return this.socket;
	}
}
