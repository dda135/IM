package mine.fanjh;

import java.net.Socket;

public class Utils {
	
	public static String[] getIdAndToken(String clientID) {
		try {
			String[] temp = clientID.split("_");
			if (temp.length == 2) {
				return temp;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}

	public static void closeSocket(String clientID,Socket socket) {
		try {
			if (null != clientID) {
				OnlineClientID.remove(clientID);
			}
			if(null != socket) {
				socket.close();
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
}
