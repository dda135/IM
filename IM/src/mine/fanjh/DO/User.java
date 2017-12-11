package mine.fanjh.DO;

public class User implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1758307814255476355L;

	public int id;
	
	public transient int tokenID;
	
	public Token token;
	
	public String nickname;
	
	public String password;
	
	public String mobile;
	
	public long createTime;
	
	public long lastLoginTime;
	
	public String portrait;
	
	public String sex;
	
	public String birth;
	
	public String address;

}
