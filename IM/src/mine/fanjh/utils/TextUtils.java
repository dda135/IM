package mine.fanjh.utils;

import mine.fanjh.DO.ResultEntity;
import mine.fanjh.encryption.EncryptionWorker;

public class TextUtils {
	
	public static boolean isTextEmpty(String text) {
		return text == null || text.length() == 0;
	}
	
	public static ResultEntity getNormalErrorCode(String B,String hint) {
		ResultEntity resultEntity = new ResultEntity();
		resultEntity.setCacheControl("{}");
		resultEntity.setHint(hint);
		resultEntity.setStatus(Codes.ERROR);
		resultEntity.setData(EncryptionWorker.encrypt(B, "{}"));
		return resultEntity;
	}
	
	public static ResultEntity getTokenErrorCode(String B,String hint) {
		ResultEntity resultEntity = new ResultEntity();
		resultEntity.setCacheControl("{}");
		resultEntity.setHint(hint);
		resultEntity.setStatus(Codes.TOKEN_ERROR);
		resultEntity.setData(EncryptionWorker.encrypt(B, "{}"));
		return resultEntity;
	}
	
	public static ResultEntity getMoreoverLoginCode(String hint) {
		ResultEntity resultEntity = new ResultEntity();
		resultEntity.setHint(hint);
		resultEntity.setStatus(Codes.MOREOVER_LOGIN);
		return resultEntity;
	}

	public static ResultEntity getServiceErrorCode(String B,String hint) {
		ResultEntity resultEntity = new ResultEntity();
		resultEntity.setCacheControl("{}");
		resultEntity.setHint(hint);
		resultEntity.setStatus(Codes.SERVER_CODE_ERROR);
		resultEntity.setData(EncryptionWorker.encrypt(B, "{}"));
		return resultEntity;
	}

	public static ResultEntity getSuccess(String B,String hint,String data) {
		ResultEntity resultEntity = new ResultEntity();
		resultEntity.setCacheControl("{}");
		resultEntity.setHint(hint);
		resultEntity.setStatus(1);
		System.out.println(data);
		resultEntity.setData(EncryptionWorker.encrypt(B, data));
		return resultEntity;
	}
	

}
