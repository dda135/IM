package mine.fanjh.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.google.gson.Gson;

import mine.fanjh.DAO.TokenDAO;
import mine.fanjh.DAO.UserDAO;
import mine.fanjh.DO.ResultEntity;
import mine.fanjh.DO.Token;
import mine.fanjh.DO.User;
import mine.fanjh.encryption.EncryptionWorker;
import mine.fanjh.utils.Const;
import mine.fanjh.utils.JDBC;
import mine.fanjh.utils.RegexUtils;
import mine.fanjh.utils.TextUtils;

@Controller
@RequestMapping("/user")
public class UserController{


	@RequestMapping("/hello")
	public String hello(String name, String title) {
		Logger logger = Logger.getLogger("aa");


		return "index";
	}

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public String upload(HttpServletRequest req) throws Exception {
		MultipartHttpServletRequest mreq = (MultipartHttpServletRequest) req;
		MultipartFile file = mreq.getFile("file");
		String fileName = file.getOriginalFilename();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		File temp = new File(req.getSession().getServletContext().getRealPath("/") + sdf.format(new Date())
				+ fileName.substring(fileName.lastIndexOf('.')));

		temp.createNewFile();
		FileOutputStream fos = new FileOutputStream(temp);
		fos.write(file.getBytes());
		fos.flush();
		fos.close();

		String title = mreq.getParameter("title");
		Logger logger = Logger.getLogger("aa");
		return "hello";
	}

	@RequestMapping("/jsonText")
	@ResponseBody
	public User jsonText() {
		User user = new User();
		return user;
	}

	@RequestMapping(value = "/testEncryption", method = RequestMethod.POST)
	@ResponseBody
	public String testEncryption(HttpServletRequest req) throws Exception {
		MultipartHttpServletRequest mreq = (MultipartHttpServletRequest) req;

		String content = mreq.getParameter("content");

		EncryptionWorker.deciphering(content);

		return "hello";
	}

	@RequestMapping(value = "/regist", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public ResultEntity regist(String content) throws Exception {

		String[] result = EncryptionWorker.deciphering(content);
		String desKey = result[0];
		String data = result[1];

		Gson gson = new Gson();
		User user = gson.fromJson(data, User.class);

		if (TextUtils.isTextEmpty(user.mobile)) {
			return TextUtils.getNormalErrorCode(desKey, "手机号不能为空！");
		}

		if (TextUtils.isTextEmpty(user.password)) {
			return TextUtils.getNormalErrorCode(desKey, "密码不能为空！");
		}

		if (user.password.length() < 8 || user.password.length() > 16) {
			return TextUtils.getNormalErrorCode(desKey, "密码必须大于等于8位且小于等于16位！");
		}

		if (!RegexUtils.checkNumberAndChar(user.password)) {
			return TextUtils.getNormalErrorCode(desKey, "密码必须是数字加字母的组合！");
		}

		Connection connection = null;
		try {

			connection = JDBC.openConnection();

			UserDAO userDAO = new UserDAO();
			TokenDAO tokenDAO = new TokenDAO();
			
			boolean isExists = userDAO.userExists(connection, user.mobile);

			if (isExists) {
				return TextUtils.getNormalErrorCode(desKey, "当前手机号已被占用！");
			}

			Token token = tokenDAO.getNewToken(connection);

			User newUser = userDAO.registerUser(connection, token, user.mobile, user.password);
			newUser.token = token;
			
			connection.commit();

			if (null != newUser) {
				handleUserAvator(newUser);
				return TextUtils.getSuccess(desKey, "注册成功！", gson.toJson(newUser));
			} else {
				return TextUtils.getNormalErrorCode(desKey, "注册失败！");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			JDBC.rollback(connection);
			return TextUtils.getServiceErrorCode(desKey, "代码错误！");
		} finally {
			JDBC.close(connection);
		}

	}

	@RequestMapping(value = "/login", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public ResultEntity login(String content) throws Exception {

		String[] result = EncryptionWorker.deciphering(content);
		String desKey = result[0];
		String data = result[1];

		Gson gson = new Gson();
		User user = gson.fromJson(data, User.class);

		if (TextUtils.isTextEmpty(user.mobile)) {
			return TextUtils.getNormalErrorCode(desKey, "手机号不能为空！");
		}

		if (TextUtils.isTextEmpty(user.password)) {
			return TextUtils.getNormalErrorCode(desKey, "密码不能为空！");
		}

		if (user.password.length() < 8 || user.password.length() > 16) {
			return TextUtils.getNormalErrorCode(desKey, "密码必须大于等于8位且小于等于16位！");
		}

		if (!RegexUtils.checkNumberAndChar(user.password)) {
			return TextUtils.getNormalErrorCode(desKey, "密码必须是数字加字母的组合！");
		}

		Connection connection = null;

		try {
			connection = JDBC.openConnection();
			
			UserDAO userDAO = new UserDAO();
			TokenDAO tokenDAO = new TokenDAO();

			User oldUser = userDAO.queryUser(connection, user.mobile);

			if (null == oldUser) {
				return TextUtils.getNormalErrorCode(desKey, "该账号未注册！");
			}

			if (!oldUser.password.equals(EncryptionWorker.MD5(user.password))) {
				return TextUtils.getNormalErrorCode(desKey, "密码错误！");
			}

			// TopicSender.sendToTopic("mine.PTP", oldUser.getId()+"", new
			// Gson().toJson(TextUtils.getMoreoverLoginCode("发现您当前在其他地方已经登录，当前账号需要下线！")));

			Token token = tokenDAO.getNewToken(connection);

			boolean loginResult = userDAO.login(connection, token.id, oldUser.id);
			
			connection.commit();
			
			oldUser.token = token;
			oldUser.tokenID = token.id;

			if (loginResult) {
				handleUserAvator(oldUser);
				return TextUtils.getSuccess(desKey, "登录成功！", new Gson().toJson(oldUser));
			} else {
				return TextUtils.getNormalErrorCode(desKey, "登录失败！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			JDBC.rollback(connection);
			// TODO: handle exception
			return TextUtils.getServiceErrorCode(desKey, "代码错误！");
		} finally {
			JDBC.close(connection);
		}

	}

	@RequestMapping(value = "/getUserMessage", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public ResultEntity getMessage(String content) throws Exception {

		String[] result = EncryptionWorker.deciphering(content);
		String desKey = result[0];
		String data = result[1];

		JSONObject jsonObject = new JSONObject(data);
		int userID = jsonObject.getInt("user_id");
		String token = jsonObject.getString("token");

		if (userID <= 0 || TextUtils.isTextEmpty(token)) {
			return TextUtils.getNormalErrorCode(desKey, "用户未登录！");
		}
		
		Connection connection = null;
		try {
			connection = JDBC.openConnection();
			
			UserDAO userDAO = new UserDAO();
			TokenDAO tokenDAO = new TokenDAO();
			
			boolean isSuccess = tokenDAO.checkTokenExpire(connection, token, userID);
			if (!isSuccess) {
				return TextUtils.getTokenErrorCode(desKey, "当前token无效，请重新登录！");
			} else {
				User user = userDAO.getUserMessage(connection, userID);
				handleUserAvator(user);
				return TextUtils.getSuccess(desKey, "获取信息成功！", new Gson().toJson(user));
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return TextUtils.getServiceErrorCode(desKey, "代码错误！");
		}finally {
			JDBC.close(connection);
		}

		
	}

	@RequestMapping(value = "/updateUserMessage", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public ResultEntity updateUserMessage(HttpServletRequest req) throws Exception {
		MultipartHttpServletRequest mreq = (MultipartHttpServletRequest) req;
		MultipartFile file = mreq.getFile("avator");
		String newFilename = null;
		if (null != file) {
			String fileName = file.getOriginalFilename();

			newFilename = UUID.randomUUID().toString() + fileName.substring(fileName.lastIndexOf('.'));
			String realPath = "userAvator/";

			File temp = new File(realPath, newFilename);
			if (!temp.getParentFile().exists()) {
				temp.getParentFile().mkdir();
			}
			if (!temp.exists()) {
				temp.createNewFile();
			}

			FileOutputStream fos = new FileOutputStream(temp);
			fos.write(file.getBytes());
			fos.flush();
			fos.close();
		}

		String content = mreq.getParameter("content");

		String[] result = EncryptionWorker.deciphering(content);
		String desKey = result[0];
		String data = result[1];


		Gson gson = new Gson();
		User user = gson.fromJson(data, User.class);
		System.out.println(user.nickname+"-->"+user.address);
		
		
		Connection connection = null;
		
		try {
			connection = JDBC.openConnection();
			
			UserDAO userDAO = new UserDAO();
			
			boolean isSuccess = false;
			if(null != newFilename) {
				newFilename = "userAvator/" + newFilename;
				isSuccess = userDAO.updateMessage(connection, user.id, newFilename, user.nickname, user.birth, user.sex, user.address);
			}else {
				isSuccess = userDAO.updateMessage(connection, user.id, user.nickname, user.birth, user.sex, user.address);
			}
			connection.commit();
			if(isSuccess) {
				User newUser = new User();
				newUser.id = user.id;
				newUser.nickname = user.nickname;
				newUser.birth = user.birth;
				newUser.sex = user.sex;
				newUser.portrait = newFilename;
				newUser.address = user.address;
				if(null != newUser.portrait) {
					handleUserAvator(newUser);
				}
				return TextUtils.getSuccess(desKey, "获取信息成功！", new Gson().toJson(newUser));
			}else {
				return TextUtils.getNormalErrorCode(desKey, "信息更新失败！！");
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

	private void handleUserAvator(User user) {
		String avator = user.portrait;
		if (!TextUtils.isTextEmpty(avator)) {
			user.portrait = (Const.IMAGE_PREFIX + avator);
		}
	}

}
