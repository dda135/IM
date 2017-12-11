package mine.fanjh;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

import fanjh.mine.proto.MessageProtocol.MessageProto;

public class SenderThread extends Thread {
	private Socket socket;
	private LinkedBlockingQueue<MessageProto> queue = new LinkedBlockingQueue<>();
	private String clientID;

	public SenderThread(Socket socket) {
		super();
		this.socket = socket;
	}
	
	
	
	public String getClientID() {
		return clientID;
	}



	public void setClientID(String clientID) {
		this.clientID = clientID;
	}



	public void sendMessage(MessageProto message) {
		try {
			queue.put(message);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();

		try {
			OutputStream oStream = socket.getOutputStream();
			DataOutputStream dataOutputStream = new DataOutputStream(oStream);
			
			MessageProto proto = null;
			while(null != (proto = queue.take())) {
				
				byte[] protos = proto.toByteArray();
	  
	            System.out.println("向"+clientID+"发送"+(protos.length+4)+"个字节数据！");
				dataOutputStream.writeInt(protos.length);
                dataOutputStream.write(protos);
                dataOutputStream.flush();
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Utils.closeSocket(clientID, socket);
		}
		socket = null;
		clientID = null;

	}
	
	
}
