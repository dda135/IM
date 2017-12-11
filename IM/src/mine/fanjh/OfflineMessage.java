package mine.fanjh;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import fanjh.mine.proto.MessageProtocol.MessageProto;

public class OfflineMessage {
	public static Map<Integer, LinkedList<MessageProto>> offlineMessages = new HashMap<>();
	
	public synchronized static void saveOfflineMessage(int userID,MessageProto messageProto) {
		if(0 == userID || null == messageProto) {
			return;
		}
		LinkedList<MessageProto> messageProtos = offlineMessages.get(userID);
		if(null == messageProtos) {
			messageProtos = new LinkedList<>();
		}
		messageProtos.offer(messageProto);
		offlineMessages.put(userID, messageProtos);
	}
	
	public synchronized static void removeOfflineMessage(int userID,long protoID) {
		if(userID == 0 || protoID == 0) {
			return;
		}
		LinkedList<MessageProto> protos = offlineMessages.get(userID);
		if(null != protos) {
			Iterator<MessageProto> iterator = protos.iterator();
			while(iterator.hasNext()) {
				MessageProto proto = iterator.next();
				if(proto.getId() == protoID) {
					iterator.remove();
					return;
				}
			}
		}
	}
	
}
