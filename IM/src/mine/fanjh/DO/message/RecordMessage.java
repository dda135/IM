package mine.fanjh.DO.message;

public class RecordMessage extends BaseMessage{

	public String recordUrl;
	public int recordTime;
	public String fileName;
	public RecordMessage() {
		// TODO Auto-generated constructor stub
		super(TYPE_RECORD);
	}
}
