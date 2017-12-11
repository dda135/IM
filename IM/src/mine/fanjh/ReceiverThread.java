package mine.fanjh;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.sql.Connection;
import java.util.Iterator;
import java.util.LinkedList;

import com.google.gson.Gson;

import fanjh.mine.proto.MessageProtocol;
import fanjh.mine.proto.MessageProtocol.MessageProto;
import mine.fanjh.DAO.FriendDAO;
import mine.fanjh.DO.FriendApply;
import mine.fanjh.DO.message.ApplyAgreeMessage;
import mine.fanjh.DO.message.ApplyAgreeSuccessMessage;
import mine.fanjh.DO.message.ApplyMessage;
import mine.fanjh.DO.message.ApplySuccessMessage;
import mine.fanjh.DO.message.BaseMessage;
import mine.fanjh.DO.message.CommonMessage;
import mine.fanjh.utils.JDBC;
import mine.fanjh.utils.TextUtils;

public class ReceiverThread extends Thread {
	private Socket socket;
	private SenderThread senderThread;
	private String clientID;

	public ReceiverThread(Socket socket, SenderThread senderThread) {
		super();
		this.socket = socket;
		this.senderThread = senderThread;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();

		try {
			InputStream iStream = socket.getInputStream();
			DataInputStream dataInputStream = new DataInputStream(iStream);
			while (true) {

				int len = dataInputStream.readInt();
				byte[] data = new byte[len];
				dataInputStream.readFully(data, 0, len);

				MessageProtocol.MessageProto messageProto = MessageProtocol.MessageProto.parseFrom(data);
				System.out.println(messageProto.getType() + "-->" + messageProto.getContent());

				int type = messageProto.getType();
				switch (type) {
				case ProtoType.CONNECT_REQ:
					handleConnectReq(messageProto);
					break;
				case ProtoType.SEND_REQ:
					handleSendReq(messageProto);
					break;
				case ProtoType.PING_REQ:
					System.out.println("pingReq-->"+messageProto.getContent());
					senderThread.sendMessage(sendMessage(ProtoType.PING_ACK, messageProto.getId(), null));
					break;
				case ProtoType.MESSAGE_ACK:
					handleMessageAck(messageProto);
					break;
				default:
					break;
				}

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Utils.closeSocket(clientID, socket);
		}
		clientID = null;
		socket = null;
		senderThread = null;

	}

	public MessageProto sendMessage(int type, long msgID, String content) {

		MessageProtocol.MessageProto.Builder builder = MessageProtocol.MessageProto.newBuilder();
		if (null != content) {
			builder.setContent(content);
		}
		if (0 != msgID) {
			builder.setId(msgID);
		}
		builder.setType(type);

		return builder.build();

	}

	private void handleConnectReq(MessageProto messageProto) {
		System.out.println("MessageProto-->" + messageProto.toString());
		clientID = messageProto.getContent();
		OnlineClientID.put(clientID, senderThread);
		senderThread.setClientID(clientID);
		senderThread.sendMessage(sendMessage(ProtoType.CONNECT_ACK, 0, ""));
		System.out.println("clientID-->"+clientID);
		int userID = Integer.parseInt(Utils.getIdAndToken(clientID)[0]);
		MessageProto offlineMessage = null;
		LinkedList<MessageProto> protos = OfflineMessage.offlineMessages.get(userID);
		if (null != protos) {
			Iterator<MessageProto> iterator = protos.iterator();
			while (iterator.hasNext()) {
				offlineMessage = iterator.next();
				System.out.println("发送离线消息-->" + offlineMessage.getContent());
				senderThread.sendMessage(
						sendMessage(ProtoType.MESSAGE, offlineMessage.getId(), offlineMessage.getContent()));
			}
		}
	}

	private void handleSendReq(MessageProto messageProto) {
		Gson gson = new Gson();
		CommonMessage arrivedMessage = gson.fromJson(messageProto.getContent(), CommonMessage.class);
		int businessType = arrivedMessage.getMessage().type;
		
		int serverID;
		
		switch (businessType) {
		case BaseMessage.TYPE_APPLY:
			ApplyMessage applyMessage = (ApplyMessage) arrivedMessage.getMessage();
			serverID = handleApplyMessageBusiness(arrivedMessage,applyMessage);
			System.out.println("serverID---" + serverID);
			if(serverID > 0) {
				applyMessage.serverID = serverID;
				arrivedMessage.content = gson.toJson(applyMessage);
				transmitMessage(arrivedMessage.receiver_id, getMessageProto(messageProto.getId(),messageProto.getType(),gson.toJson(arrivedMessage)));
				senderThread.sendMessage(sendMessage(ProtoType.SEND_ACK, messageProto.getId(), ""));
		
				ApplySuccessMessage applySuccessMessage = new ApplySuccessMessage();
				applySuccessMessage.serverID = serverID;
				applySuccessMessage.applyID = arrivedMessage.sender_id;
				applySuccessMessage.confirmID = arrivedMessage.receiver_id;
				applySuccessMessage.text = applyMessage.text;
				CommonMessage commonMessage = new CommonMessage();
				commonMessage.content = gson.toJson(applySuccessMessage);
				commonMessage.receiver_id = arrivedMessage.sender_id;
				commonMessage.sender_name = arrivedMessage.sender_name;
				commonMessage.sender_id = arrivedMessage.receiver_id;
				MessageProto proto = sendMessage(ProtoType.MESSAGE, 1, gson.toJson(commonMessage));
				OfflineMessage.saveOfflineMessage(arrivedMessage.sender_id, proto);
				senderThread.sendMessage(proto);
			}
			break;
		case BaseMessage.TYPE_APPLY_AGREE:
			serverID = handleApplyConfirmMessageBusiness(arrivedMessage);
			if(serverID > 0) {
				ApplyAgreeMessage applyAgreeMessage = (ApplyAgreeMessage) arrivedMessage.getMessage();
				applyAgreeMessage.serverID = serverID;
				arrivedMessage.content = gson.toJson(applyAgreeMessage);
				transmitMessage(arrivedMessage.receiver_id, getMessageProto(messageProto.getId(), messageProto.getType(), gson.toJson(arrivedMessage)));
				senderThread.sendMessage(sendMessage(ProtoType.SEND_ACK, messageProto.getId(), ""));
				
				ApplyAgreeSuccessMessage applyAgreeSuccessMessage = new ApplyAgreeSuccessMessage();
				applyAgreeSuccessMessage.serverID = serverID;
				applyAgreeSuccessMessage.applyID = arrivedMessage.receiver_id;
				applyAgreeSuccessMessage.confirmID = arrivedMessage.sender_id;
				applyAgreeSuccessMessage.applyName = applyAgreeMessage.applyName;
				applyAgreeSuccessMessage.applyContent = applyAgreeMessage.applyContent;
				CommonMessage commonMessage = new CommonMessage();
				commonMessage.content = gson.toJson(applyAgreeSuccessMessage);
				commonMessage.receiver_id = arrivedMessage.sender_id;
				commonMessage.sender_name = arrivedMessage.sender_name;
				MessageProto proto = sendMessage(ProtoType.MESSAGE, 1, gson.toJson(commonMessage));
				OfflineMessage.saveOfflineMessage(arrivedMessage.sender_id, proto);
				senderThread.sendMessage(proto);
			}
			break;
		case BaseMessage.TYPE_APPLY_REJECT:
			boolean result = handleApplyRejectMessageBusiness(arrivedMessage);
			if(result) {
				transmitMessage(arrivedMessage.receiver_id, messageProto);
				senderThread.sendMessage(sendMessage(ProtoType.SEND_ACK, messageProto.getId(), ""));
			}
			break;
		default:
			break;
		}
		//失败的话应该导致客户端发送超时
		
	}
	
	private MessageProto getMessageProto(long id,int type,String content) {
		return MessageProtocol.MessageProto.newBuilder().
				setId(id).
				setType(type).
				setContent(content).
				build();
	}
	
	private void transmitMessage(int receiverID,MessageProto messageProto) {
		SenderThread transmit = OnlineClientID.getSender(receiverID);
		OfflineMessage.saveOfflineMessage(receiverID, messageProto);
		if (null == transmit) {
			System.out.println("transmit-->" + receiverID + "-->offline");
		} else {
			System.out.println("transmit-->" + receiverID + "-->" + messageProto.getContent());
			transmit.sendMessage(sendMessage(ProtoType.MESSAGE, messageProto.getId(), messageProto.getContent()));
		}
	}

	private void handleMessageAck(MessageProto messageProto) {
		String clientID = messageProto.getContent();
		int userID = Integer.parseInt(Utils.getIdAndToken(clientID)[0]);
		OfflineMessage.removeOfflineMessage(userID, messageProto.getId());
	}

	private int handleApplyMessageBusiness(CommonMessage message,ApplyMessage applyMessage) {
		int result = -1;
		Connection connection = null;
		try {
			connection = JDBC.openConnection();
			FriendDAO friendDAO = new FriendDAO();
			int applyID = message.sender_id;
			int confirmID = message.receiver_id;
			if(!friendDAO.isFriendApplyExist(connection, applyID, confirmID)) {
				result = friendDAO.addFriendApply(connection, applyID, confirmID,TextUtils.isTextEmpty(applyMessage.text)?"未填写":applyMessage.text);
				connection.commit();
				System.out.println("isFriendApplyExist-->false" );
			}else {
				System.out.println("isFriendApplyExist-->true" );
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("e-->" + e.toString());
			JDBC.rollback(connection);
		}finally {
			JDBC.close(connection);
		}
		return result;
	}
	
	private boolean handleApplyRejectMessageBusiness(CommonMessage message) {
		Connection connection = null;
		try {
			connection = JDBC.openConnection();
			FriendDAO friendDAO = new FriendDAO();
			boolean result = friendDAO.updateFriendApplyStatus(connection, message.receiver_id, message.sender_id, FriendApply.STATUS_REJECT);
			connection.commit();
			return result;
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			JDBC.rollback(connection);
		}finally {
			JDBC.close(connection);
		}
		return false;
	}
	
	private int handleApplyConfirmMessageBusiness(CommonMessage message) {
		Connection connection = null;
		int result = -1;
		try {
			connection = JDBC.openConnection();
			FriendDAO friendDAO = new FriendDAO();
			int applyID = message.receiver_id;
			int confirmID = message.sender_id;
			if(friendDAO.isFriendExist(connection, applyID, confirmID)) {
				return result;
			}
			boolean isUpdateSucceed = friendDAO.updateFriendApplyStatus(connection, applyID, confirmID, FriendApply.STATUS_CONFIRM);
			if(isUpdateSucceed) {
				result = friendDAO.addFriend(connection, applyID, confirmID);
			}
			connection.commit();
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			JDBC.rollback(connection);
		}finally {
			JDBC.close(connection);
		}
		return result;
	}
	
}
