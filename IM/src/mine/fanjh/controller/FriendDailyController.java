package mine.fanjh.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.google.gson.Gson;

import mine.fanjh.DAO.FriendDailyDao;
import mine.fanjh.DAO.TokenDAO;
import mine.fanjh.DAO.UserDAO;
import mine.fanjh.DO.FriendDaily;
import mine.fanjh.DO.FriendDailyReturnBean;
import mine.fanjh.DO.ResultEntity;
import mine.fanjh.DO.User;
import mine.fanjh.encryption.EncryptionWorker;
import mine.fanjh.utils.Const;
import mine.fanjh.utils.JDBC;
import mine.fanjh.utils.TextUtils;

@Controller
@RequestMapping("/friendDaily")
public class FriendDailyController {
	
	
	@RequestMapping(value = "/publishImageDaily", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public ResultEntity publishDailyImageLife(HttpServletRequest req) throws Exception {
		
		MultipartHttpServletRequest mreq = (MultipartHttpServletRequest) req;
		Map<String,MultipartFile>  imageFiles = mreq.getFileMap();
		String newImageFileName = null;
		String upLoadImageFile = "";
		int i = 0;
		
		//图片地址
		if(null != imageFiles && imageFiles.size() > 0) {
			 for (Map.Entry<String, MultipartFile> entry : imageFiles.entrySet()) {
				   System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
				   MultipartFile imageFile = entry.getValue();
				   if(null != imageFile) {
						String originFileName = imageFile.getOriginalFilename();
						newImageFileName = UUID.randomUUID().toString() + originFileName.substring(originFileName.indexOf("."));
						String realPath = System.getProperty("catalina.home") + "/webapps/IM/dailyImage/";
						String videoPath = "dailyImage/";
						File file = new File(realPath,newImageFileName);
						if(!file.getParentFile().exists()) {
							file.getParentFile().mkdirs();
						}
						
						if(!file.exists()) {
							file.createNewFile();
						}
						
						FileOutputStream fileStream = new FileOutputStream(file);
						System.out.println("file_bytes===" + imageFile.getBytes().length);
						fileStream.write(imageFile.getBytes());
						fileStream.flush();
						fileStream.close();
						if(i != imageFiles.size() - 1) {
							   upLoadImageFile = upLoadImageFile + videoPath + newImageFileName + "|";
						   }else {
							   upLoadImageFile = upLoadImageFile + videoPath + newImageFileName;
						   }
						i++;
				}
		    }
		}
			
		String content = mreq.getParameter("content");
		String[] result = EncryptionWorker.deciphering(content);
		String desKey = result[0];
		String data = result[1];
		
		Gson gson = new Gson();
		FriendDaily friendDaily = gson.fromJson(data, FriendDaily.class);
		System.out.println(friendDaily.name+"-->"+friendDaily.pulishDate);
		
		Connection connection = null;
		
		try {
			connection = JDBC.openConnection();
			FriendDailyDao friendDailyDao = new FriendDailyDao();
			
			boolean isSuccess = false;
			
			if(null != imageFiles && imageFiles.size() > 0) {
				friendDaily.image = upLoadImageFile;
				isSuccess = friendDailyDao.insertFriendDailyImage(connection, friendDaily);
			}else {
				isSuccess = friendDailyDao.insertFriendDailyText(connection, friendDaily);
			}
			connection.commit();
			if(isSuccess) {
				return TextUtils.getSuccess(desKey, "发布成功！！", "");
			}else {
				return TextUtils.getNormalErrorCode(desKey, "发布失败");
			}
		}catch(Exception e) {
			e.printStackTrace();
			JDBC.rollback(connection);
		    return TextUtils.getServiceErrorCode(desKey, "代码错误！！");
		}finally {
			JDBC.close(connection);
		}
		
	}
	
	
	@RequestMapping(value = "/publishVideoDaily", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public ResultEntity publishDailyVideoLife(HttpServletRequest req) throws Exception {
		
		MultipartHttpServletRequest mreq = (MultipartHttpServletRequest) req;
		Map<String,MultipartFile>  imageFile = mreq.getFileMap();
		MultipartFile videoFile = mreq.getFile("video");
		String newVideoFileName = null;
		
		//视频地址
		if(null != videoFile) {
			String originFileName = videoFile.getOriginalFilename();
			newVideoFileName = UUID.randomUUID().toString() + originFileName.substring(originFileName.indexOf("."));
			String videoPath = "dailyVideo/";
			String realPath = System.getProperty("catalina.home") + "/webapps/IM/dailyVideo/";
			File file = new File(realPath,newVideoFileName);
			if(!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			
			if(!file.exists()) {
				file.createNewFile();
			}
			
			FileOutputStream fileStream = new FileOutputStream(file);
			fileStream.write(videoFile.getBytes());
			fileStream.flush();
			fileStream.close();
		}
		
		String content = mreq.getParameter("content");
		String[] result = EncryptionWorker.deciphering(content);
		String desKey = result[0];
		String data = result[1];
		
		Gson gson = new Gson();
		FriendDaily friendDaily = gson.fromJson(data, FriendDaily.class);
		System.out.println(friendDaily.name+"-->"+friendDaily.pulishDate);
		
		Connection connection = null;
		
		try {
			connection = JDBC.openConnection();
			FriendDailyDao friendDailyDao = new FriendDailyDao();
			
			boolean isSuccess = false;
				if(null != newVideoFileName) {
					newVideoFileName = "dailyVideo/" + newVideoFileName;
					friendDaily.videoFile = newVideoFileName;
					isSuccess = friendDailyDao.insertFriendDailyVideo(connection, friendDaily);
				}else {
					isSuccess = friendDailyDao.insertFriendDailyText(connection, friendDaily);
				}
			
			connection.commit();
			if(isSuccess) {
				return TextUtils.getSuccess(desKey, "发布成功！！", "");
			}else {
				return TextUtils.getNormalErrorCode(desKey, "发布失败");
			}
		}catch(Exception e) {
			e.printStackTrace();
			JDBC.rollback(connection);
		    return TextUtils.getServiceErrorCode(desKey, "代码错误！！");
		}finally {
			JDBC.close(connection);
		}
		
	}
	
	@RequestMapping(value = "/getFriendDaily", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public ResultEntity ResultEntity(String content) throws Exception {
		
		String[] result = EncryptionWorker.deciphering(content);
		String desKey = result[0];
		String data = result[1];

		JSONObject jsonObject = new JSONObject(data);
		int userID = jsonObject.getInt("user_id");
		int id = jsonObject.getInt("id");
		String token = jsonObject.getString("token");

		if (userID <= 0 || TextUtils.isTextEmpty(token)) {
			return TextUtils.getNormalErrorCode(desKey, "用户未登录！");
		}
		
		Connection connection = null;
		try {
			connection = JDBC.openConnection();
			FriendDailyDao friendDailyDao = new FriendDailyDao();
			TokenDAO tokenDAO = new TokenDAO();
			
			boolean isSuccess = tokenDAO.checkTokenExpire(connection, token, userID);
			if (!isSuccess) {
				return TextUtils.getTokenErrorCode(desKey, "当前token无效，请重新登录！");
			} else {                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            
				List<FriendDailyReturnBean> mList = friendDailyDao.getMineFriendDailyData(connection, userID, id);
				handleUserAvator(mList);
				return TextUtils.getSuccess(desKey, "获取信息成功！", new Gson().toJson(mList));
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return TextUtils.getServiceErrorCode(desKey, "代码错误！");
		}finally {
			JDBC.close(connection);
		}
	}
	
	private void handleUserAvator(List<FriendDailyReturnBean> mList) {
		if(null != mList && mList.size() > 0) {
			for(int i=0;i < mList.size();i++) {
				String avator = mList.get(i).portrait;
				if (!TextUtils.isTextEmpty(avator)) {
					 mList.get(i).portrait = (Const.IMAGE_PREFIX + avator);
				}
			}
		}
	}

}
