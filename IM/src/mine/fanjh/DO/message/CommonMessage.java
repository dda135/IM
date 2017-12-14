package mine.fanjh.DO.message;

import com.google.gson.Gson;

/**
* @author fanjh
* @date 2017/11/30 10:42
* @description
* @note
**/
public class CommonMessage {
	public static final int SEND_FAILURE = 3;
	public static final int SEND_SUCCESS = 1;
	public static final int SEND_UPLOADING = 2;
	public int id;
	public String message_id;
	public int sender_id;
	public String content;
	public String sender_avator;
	public String sender_name;
	public int receiver_id;
	public int send_status;
	public long time;

	private transient BaseMessage message;

	public BaseMessage getMessage() {
		if(null == message){
			Gson gson = new Gson();
			BaseMessage baseMessage = gson.fromJson(content,BaseMessage.class);
			System.out.println("baseMessage-->" + baseMessage.toString());
			System.out.println("baseMessage-->" + baseMessage.type);
			switch (baseMessage.type){
				case BaseMessage.TYPE_TEXT:
					message = gson.fromJson(content,TextMessage.class);
					break;
				case BaseMessage.TYPE_APPLY:
					message = gson.fromJson(content,ApplyMessage.class);
					break;
				case BaseMessage.TYPE_APPLY_AGREE:
					message = gson.fromJson(content, ApplyAgreeMessage.class);
					break;
				case BaseMessage.TYPE_IMAGE:
					message = gson.fromJson(content, ImageMessage.class);
					break;
				default:
					message = baseMessage;
					break;
			}
		}
		return message;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof CommonMessage && ((CommonMessage) obj).message_id.equals(message_id);
	}

	@Override
	public int hashCode() {
		return message_id.hashCode();
	}
}
