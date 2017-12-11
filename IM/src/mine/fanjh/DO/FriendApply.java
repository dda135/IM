package mine.fanjh.DO;

public class FriendApply implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -780281440339323481L;
	public static final int STATUS_HIDDEN = 0;
	public static final int STATUS_CONFIRM = 1;
	public static final int STATUS_REJECT = 2;
	public static final int STATUS_APPLYING = 3;

	public int id;

	public int applyID;

	public int confirmID;

	public long createTime;
	
	public long lastChangeTime;
	
	public int status;
	
	public String content;
	
	public User friend;

}
