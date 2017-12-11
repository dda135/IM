package mine.fanjh.controller;

import java.util.List;

import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import mine.fanjh.DAO.NodeDAO;
import mine.fanjh.DO.Node;
import mine.fanjh.DO.ResultEntity;
import mine.fanjh.encryption.EncryptionWorker;
import mine.fanjh.utils.TextUtils;

@Controller
@RequestMapping("/node")
public class NodeController{
	

	@RequestMapping(value = "/getnode", method = RequestMethod.POST, produces="text/html;charset=UTF-8")
	@ResponseBody
	public ResultEntity login(String content) throws Exception {
		
		String[] result = EncryptionWorker.deciphering(content);
		String desKey = result[0];
		String data = result[1];

		
		JSONObject jsonObject = new JSONObject(data);
		int id = jsonObject.getInt("user_id");
		String token = jsonObject.getString("token");
		
	
		if (id <= 0 || TextUtils.isTextEmpty(token)) {
			return TextUtils.getNormalErrorCode(desKey,"用户未登录！");
		}
		
		NodeDAO nodeDAO = new NodeDAO();
		List<Node> nodeList = nodeDAO.getNodes(token);
		if(null == nodeList) {
			return TextUtils.getNormalErrorCode(desKey,"token已过期！");
		}else {
			return TextUtils.getSuccess(desKey, "获取节点成功！", new Gson().toJson(nodeList));
		}
		
	}

}
