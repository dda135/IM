package mine.fanjh.DO;

import java.util.List;

public class FriendDailyReturnBean implements java.io.Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public int id;

	public String name;
	
	public int senderId;
	
	public String pulishDate;
	
	public String videoFile;//视频名字
	
	public String content;
	
	public String portrait;
	
	public List<String> images;//图片集合
	
	public int dailyType;//朋友圈类型

}
