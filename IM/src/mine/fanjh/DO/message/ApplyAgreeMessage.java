package mine.fanjh.DO.message;

public class ApplyAgreeMessage extends BaseMessage{
	public int serverID;
	public String applyName;
	public String applyContent;

	public ApplyAgreeMessage(int type) {
		super(BaseMessage.TYPE_APPLY_AGREE);
	}

}
