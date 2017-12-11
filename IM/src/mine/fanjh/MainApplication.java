package mine.fanjh;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MainApplication {
	public static final int DEFAULT_PORT = 50001;

	public static void start() {
		try {
			ServerSocket serverSocket = new ServerSocket(DEFAULT_PORT);
			while(true) {
				Socket socket = serverSocket.accept();
				SenderThread senderThread = new SenderThread(socket);
				senderThread.start();
				new ReceiverThread(socket,senderThread).start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
