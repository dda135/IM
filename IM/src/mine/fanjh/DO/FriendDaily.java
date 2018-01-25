package mine.fanjh.DO;

public class FriendDaily  implements java.io.Serializable {
	
	public static final int DAILY_TEXT = 1;//文字朋友圈
	
	public static final int DAILY_IMAGE = 2;//图片朋友圈
	
	public static final int DAILY_VIDEO = 3;//视频朋友圈
	
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
	
	public String image;//图片名字
	
	public int dailyType;//朋友圈类型

}
