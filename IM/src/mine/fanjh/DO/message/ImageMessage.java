package mine.fanjh.DO.message;

public class ImageMessage extends BaseMessage{
	public String imageUrl;
	public int width;
	public int height;
	public String fileName;
	
	public ImageMessage() {
		// TODO Auto-generated constructor stub
		super(TYPE_IMAGE);
	}
}
