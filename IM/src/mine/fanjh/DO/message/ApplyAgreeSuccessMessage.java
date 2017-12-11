package mine.fanjh.DO.message;

public class ApplyAgreeSuccessMessage extends BaseMessage{
	public int applyID;
	public int confirmID;
	public int serverID;
	public String applyName;
	public String applyContent;
	
	public ApplyAgreeSuccessMessage() {
		super(BaseMessage.TYPE_APPLY_AGREE_SUCCESS);
	}

}
