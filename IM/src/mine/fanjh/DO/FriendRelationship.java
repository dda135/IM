package mine.fanjh.DO;

public class FriendRelationship implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5864983329018872765L;
	public static final int STATUS_HIDDEN = 0;
	public static final int STATUS_FRIEND = 1;

	public int id;

	public int applyID;

	public int confirmID;

	public long createTime;
	
	public int status;
	
	public User friend;

}
