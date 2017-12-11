package mine.fanjh.DO.message;

public class ApplySuccessMessage extends BaseMessage{
	public int applyID;
	public int confirmID;
	public int serverID;
	public String text;
	
	public ApplySuccessMessage() {
		super(BaseMessage.TYPE_APPLY_SUCCESS);
		// TODO Auto-generated constructor stub
	}

}
