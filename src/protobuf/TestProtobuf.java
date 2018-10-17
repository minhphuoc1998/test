package protobuf;

import protobuf.MessagePB.Message;
import protobuf.MessagePB.Message.Builder;
import protobuf.MessagePB.Message.MessageType;

import com.google.protobuf.TextFormat;
import com.google.protobuf.TextFormat.ParseException;

public class TestProtobuf {

	public static void main(String[] args) throws ParseException {
		Builder mess = Message.newBuilder();
		
		mess.setType(MessageType.CHAT);
		//mess.setMess("Hello world");
		
		Message m = mess.build();
		
		System.out.println(m.toString());
		String c = m.toString();
		
		Builder d = Message.newBuilder();
		
		TextFormat.getParser().merge(c, d);
		
		Message t = d.build();
		
		System.out.println((t.getMess().equals("")));
		
	}

}
