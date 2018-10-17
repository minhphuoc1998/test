package client;

public class testClient 
{
	public static void main(String[] args) throws Exception
	{
		String ip = "127.0.0.1";
		Client client = new Client(ip, 7777);
		
		System.out.println(client.connect());
		client.run();
	}
}
