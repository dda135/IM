package mine.fanjh;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.sun.org.apache.xalan.internal.xsltc.compiler.Template;

public class OnlineClientID {
	public static Map<String, SenderThread> tokenToSenderSets = new HashMap<>();
	public static Map<Integer, String> idToTokenSets = new HashMap<>();

	public synchronized static void put(String clientID,SenderThread senderThread) {
		String[] temp = Utils.getIdAndToken(clientID);
		if (null == temp) {
			return;
		}
		int id = Integer.parseInt(temp[0]);
		String token = temp[1];
		idToTokenSets.put(id, token);
		tokenToSenderSets.put(token, senderThread);
	}
	
	public synchronized static void remove(String clientID) {
		String[] temp = Utils.getIdAndToken(clientID);
		if (null == temp) {
			return;
		}
		int id = Integer.parseInt(temp[0]);
		String token = temp[1];
		String key = idToTokenSets.remove(id);
		tokenToSenderSets.remove(token);
		if (null != key) {
			tokenToSenderSets.remove(key);
		}
	}

	public synchronized static SenderThread getSender(int receiver_id) {
		String token = idToTokenSets.get(receiver_id);
		  for (Map.Entry<Integer, String> entry : idToTokenSets.entrySet()) {
			    String key = entry.getKey().toString();  
			    String value = entry.getValue().toString();
			    System.out.println("key=" + key + " value=" + value);
			   }
		if (null != token) {
			SenderThread senderThread = tokenToSenderSets.get(token);
			return senderThread;
		}
		return null;
	}

	public synchronized static SenderThread getSender(String clientID) {
		String[] temp = Utils.getIdAndToken(clientID);
		int id = Integer.parseInt(temp[0]);
		String token = idToTokenSets.get(id);
		if (null != token) {
			SenderThread senderThread = tokenToSenderSets.get(token);
			return senderThread;
		}
		return null;
	}

}
