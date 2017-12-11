package mine.fanjh.controller;

import java.sql.Connection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.sun.javafx.collections.MappingChange.Map;

import mine.fanjh.DAO.FriendDAO;
import mine.fanjh.DAO.TokenDAO;
import mine.fanjh.DAO.UserDAO;
import mine.fanjh.DO.FriendApply;
import mine.fanjh.DO.FriendRelationship;
import mine.fanjh.DO.ResultEntity;
import mine.fanjh.DO.User;
import mine.fanjh.encryption.EncryptionWorker;
import mine.fanjh.utils.Const;
import mine.fanjh.utils.JDBC;
import mine.fanjh.utils.TextUtils;

@Controller
@RequestMapping("/friend")
public class FriendController{

	@RequestMapping(value = "/getFirendApply", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public ResultEntity getFirendApplyList(String content) throws Exception {

		String[] result = EncryptionWorker.deciphering(content);
		String desKey = result[0];
		String data = result[1];

		JSONObject jsonObject = new JSONObject(data);
		int userID = jsonObject.getInt("user_id");
		String token = jsonObject.getString("token");
		int minID = -1;
		if(jsonObject.has("min_id")) {
			minID = jsonObject.getInt("min_id");
		}
		
		if (userID <= 0 || TextUtils.isTextEmpty(token)) {
			return TextUtils.getNormalErrorCode(desKey,"用户未登录！");
		}


		Connection connection = null;
		try {

			connection = JDBC.openConnection();

			UserDAO userDAO = new UserDAO();
			TokenDAO tokenDAO = new TokenDAO();
			FriendDAO friendDAO = new FriendDAO();
			
			boolean isSucceed = tokenDAO.checkTokenExpire(connection, token, userID);
			
			if(!isSucceed) {
				return TextUtils.getTokenErrorCode(desKey,"token已过期！");
			}
			
			List<FriendApply> list = null;
			
			if(minID <= 0) {
				list = friendDAO.getFriendApplyList(connection, userID);
			}else {
				list = friendDAO.getFriendApplyList(connection, userID, minID);
			}
			
			if(list.size() > 0) {
				HashMap<Integer, FriendApply> cache = new HashMap<Integer,FriendApply>();
				int []userIDs = new int[list.size()];
				for(int i = 0;i < list.size();++i) {
					FriendApply item = list.get(i);
					if(userID == item.applyID) {
						userIDs[i] = item.confirmID;
					}else {
						userIDs[i] = item.applyID;
					}
					cache.put(userIDs[i], item);
				}
				
				List<User> users = userDAO.getUserMessages(connection, userIDs);
				for(int i = 0;i < users.size();++i) {
					User user = users.get(i);
					handleUserAvator(user);
					cache.get(user.id).friend = user;
				}
			}
			
			
			connection.commit();

			return TextUtils.getSuccess(desKey, "获取成功！", new Gson().toJson(list));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			JDBC.rollback(connection);
			return TextUtils.getServiceErrorCode(desKey, "代码错误！");
		} finally {
			JDBC.close(connection);
		}

	}
	
	@RequestMapping(value = "/getFriendRelationship", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public ResultEntity getFriendRelationshipList(String content) throws Exception {

		String[] result = EncryptionWorker.deciphering(content);
		String desKey = result[0];
		String data = result[1];

		JSONObject jsonObject = new JSONObject(data);
		int userID = jsonObject.getInt("user_id");
		String token = jsonObject.getString("token");
		int minID = -1;
		if(jsonObject.has("min_id")) {
			minID = jsonObject.getInt("min_id");
		}
		
		if (userID <= 0 || TextUtils.isTextEmpty(token)) {
			return TextUtils.getNormalErrorCode(desKey,"用户未登录！");
		}


		Connection connection = null;
		try {

			connection = JDBC.openConnection();

			UserDAO userDAO = new UserDAO();
			TokenDAO tokenDAO = new TokenDAO();
			FriendDAO friendDAO = new FriendDAO();
			
			boolean isSucceed = tokenDAO.checkTokenExpire(connection, token, userID);
			
			if(!isSucceed) {
				return TextUtils.getTokenErrorCode(desKey,"token已过期！");
			}
			
			List<FriendRelationship> list = null;
			
			if(minID <= 0) {
				list = friendDAO.getFriendRelationshipList(connection, userID);
			}else {
				list = friendDAO.getFriendRelationshipList(connection, userID, minID);
			}
			
			if(list.size() > 0) {
				HashMap<Integer, FriendRelationship> cache = new HashMap<Integer,FriendRelationship>();
				int []userIDs = new int[list.size()];
				for(int i = 0;i < list.size();++i) {
					FriendRelationship item = list.get(i);
					if(userID == item.applyID) {
						userIDs[i] = item.confirmID;
					}else {
						userIDs[i] = item.applyID;
					}
					cache.put(userIDs[i], item);
				}
				List<User> users = userDAO.getUserMessages(connection, userIDs);
				for(int i = 0;i < list.size();++i) {
					User user = users.get(i);
					handleUserAvator(user);
					cache.get(user.id).friend = user;
				}
			}
			
			
			connection.commit();

			return TextUtils.getSuccess(desKey, "获取成功！", new Gson().toJson(list));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			JDBC.rollback(connection);
			return TextUtils.getServiceErrorCode(desKey, "代码错误！");
		} finally {
			JDBC.close(connection);
		}

	}

	private void handleUserAvator(User user) {
		String avator = user.portrait;
		if (!TextUtils.isTextEmpty(avator)) {
			user.portrait = (Const.IMAGE_PREFIX + avator);
		}
	}

}
