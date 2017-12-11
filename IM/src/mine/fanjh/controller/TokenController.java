package mine.fanjh.controller;

import java.sql.Connection;
import java.util.Date;

import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import mine.fanjh.DAO.TokenDAO;
import mine.fanjh.DO.ResultEntity;
import mine.fanjh.encryption.EncryptionWorker;
import mine.fanjh.utils.JDBC;
import mine.fanjh.utils.TextUtils;

@Controller
@RequestMapping("/token")
public class TokenController{
	
	@RequestMapping(value = "/checkAndExtendToken", method = RequestMethod.POST, produces="text/html;charset=UTF-8")
	@ResponseBody
	public ResultEntity checkAndExtendToken(String content) throws Exception {
		
		String[] result = EncryptionWorker.deciphering(content);
		String desKey = result[0];
		String data = result[1];

		
		JSONObject jsonObject = new JSONObject(data);
		int userID = jsonObject.getInt("user_id");
		String tokenValue = jsonObject.getString("token");
		
	
		if (userID <= 0 || TextUtils.isTextEmpty(tokenValue)) {
			return TextUtils.getNormalErrorCode(desKey,"用户未登录！");
		}
		
		Connection connection = null;
		try {
			connection = JDBC.openConnection();
			TokenDAO tokenDAO = new TokenDAO();
			int tokenID = tokenDAO.checkTokenExpireID(connection, tokenValue, userID);
			if(tokenID <= 0) {
				return TextUtils.getTokenErrorCode(desKey,"当前token无效，请重新登录！");
			}else {
				Date date = TokenDAO.getNextMonthDate();
				boolean isSucceed = tokenDAO.extendTokenExpire(connection, date, tokenID);
				connection.commit();
				if(isSucceed) {
					return TextUtils.getSuccess(desKey, "token续期成功！", new Gson().toJson(date));
				}else {
					return TextUtils.getTokenErrorCode(desKey, "token续期失败！");
				}
			}
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
