package mine.fanjh.controller;

import java.sql.Connection;

import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import mine.fanjh.DAO.TokenDAO;
import mine.fanjh.DAO.UserDAO;
import mine.fanjh.DO.ResultEntity;
import mine.fanjh.encryption.EncryptionWorker;
import mine.fanjh.utils.JDBC;
import mine.fanjh.utils.TextUtils;

@Controller
@RequestMapping("/search")
public class SearchController{

	@RequestMapping(value = "/searchUser", method = RequestMethod.POST, produces="text/html;charset=UTF-8")
	@ResponseBody
	public ResultEntity login(String content) throws Exception {
		
		String[] result = EncryptionWorker.deciphering(content);
		String desKey = result[0];
		String data = result[1];

		
		JSONObject jsonObject = new JSONObject(data);
		int user_id = jsonObject.getInt("user_id");
		String token = jsonObject.getString("token");
		String keyword = jsonObject.getString("keyword");
		
	
		if (user_id <= 0 || TextUtils.isTextEmpty(token)) {
			return TextUtils.getNormalErrorCode(desKey,"用户未登录！");
		}
		
		if (TextUtils.isTextEmpty(keyword)) {
			return TextUtils.getNormalErrorCode(desKey,"关键词不能为空！");
		}
		
		Connection connection = null;
		try {
			TokenDAO tokenDAO = new TokenDAO();
			UserDAO userDAO = new UserDAO();
			connection = JDBC.openConnection();
			boolean tokenAvailable = tokenDAO.checkTokenExpire(connection,token, user_id);
			if(!tokenAvailable) {
				TextUtils.getTokenErrorCode(desKey,"token已过期，请重新登录！");
			}

			String json = new Gson().toJson(userDAO.searchUser(connection,keyword,user_id));	
			connection.commit();
			
			return TextUtils.getSuccess(desKey, "搜索完成！", json);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			JDBC.rollback(connection);
			return TextUtils.getServiceErrorCode(desKey, "代码错误！");
		}finally {
			JDBC.close(connection);
		}
	
		
	}

}
