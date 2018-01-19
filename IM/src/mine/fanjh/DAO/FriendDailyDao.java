package mine.fanjh.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import mine.fanjh.DO.FriendDaily;
import mine.fanjh.DO.FriendDailyReturnBean;
import mine.fanjh.DO.FriendRelationship;
import mine.fanjh.DO.User;
import mine.fanjh.utils.Const;
import mine.fanjh.utils.TextUtils;

public class FriendDailyDao {
	
	
	
public boolean insertFriendDailyImage(Connection connection,FriendDaily friendDaily) throws Exception{
	
		String sql = "insert into friendDaily(name,senderId,publishDate,videoFile,content,portrait,image,dailyType) values(?,?,?,?,?,?,?,?)";
		PreparedStatement prepareStatement = connection.prepareStatement(sql);
		prepareStatement.setString(1, friendDaily.name);
		prepareStatement.setInt(2, friendDaily.senderId);
		prepareStatement.setString(3, friendDaily.pulishDate);
		prepareStatement.setString(4, "");
		prepareStatement.setString(5, friendDaily.content);
		prepareStatement.setString(6, friendDaily.portrait);
		prepareStatement.setString(7, friendDaily.image);
		prepareStatement.setInt(8, FriendDaily.DAILY_IMAGE);
		return prepareStatement.executeUpdate() > 0;
	}
	
	/** 
	 * 插入朋友圈数据
	 * @return
	 */
	public boolean insertFriendDailyVideo(Connection connection,FriendDaily friendDaily) throws Exception{
		
		String sql = "insert into friendDaily(name,senderId,publishDate,videoFile,content,portrait,imag,dailyTypee) values(?,?,?,?,?,?,?,?)";
		PreparedStatement prepareStatement = connection.prepareStatement(sql);
		prepareStatement.setString(1, friendDaily.name);
		prepareStatement.setInt(2, friendDaily.senderId);
		prepareStatement.setString(3, friendDaily.pulishDate);
		prepareStatement.setString(4, friendDaily.videoFile);
		prepareStatement.setString(5, friendDaily.content);
		prepareStatement.setString(6, friendDaily.portrait);
		prepareStatement.setString(7, "");
		prepareStatement.setInt(8, FriendDaily.DAILY_VIDEO);
		return prepareStatement.executeUpdate() > 0;
	}
	
	/** 
	 * 插入朋友圈数据
	 * @return
	 */
	public boolean insertFriendDailyText(Connection connection,FriendDaily friendDaily) throws Exception{
		
		String sql = "insert into friendDaily(name,senderId,publishDate,videoFile,content,portrait,image,dailyType) values(?,?,?,?,?,?,?,?)";
		PreparedStatement prepareStatement = connection.prepareStatement(sql);
		prepareStatement.setString(1, friendDaily.name);
		prepareStatement.setInt(2, friendDaily.senderId);
		prepareStatement.setString(3, friendDaily.pulishDate);
		prepareStatement.setString(4, "");
		prepareStatement.setString(5, friendDaily.content);
		prepareStatement.setString(6, friendDaily.portrait);
		prepareStatement.setString(7, "");
		prepareStatement.setInt(8, FriendDaily.DAILY_TEXT);
		return prepareStatement.executeUpdate() > 0;
	}
	
	/** 
	 * 获取朋友数据
	 * @param connection
	 * @param mineId
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public List<FriendDailyReturnBean> getMineFriendDailyData(Connection connection,int mineId,int id) throws Exception{
				
		String queryIdString = "select * from friend_relationship where confirm_id = ? or apply_id = ? order by create_time desc";

		PreparedStatement preparedStatement;
		preparedStatement = connection.prepareStatement(queryIdString);
		preparedStatement.setInt(1, mineId);
		preparedStatement.setInt(2, mineId);

		ResultSet resultSet = preparedStatement.executeQuery();

		List<FriendRelationship> friendList = new ArrayList<>();
		while (null != resultSet && resultSet.next()) {
			friendList.add(parseFriendRelationship(resultSet));
		}
		List<FriendDailyReturnBean> dailyDatas = new ArrayList<>();	
	
			List<Integer> friendIdList = getFriendListId(friendList,mineId);
			StringBuilder stringBuilder = new StringBuilder("select * from friendDaily where senderId in (");
			int length = friendList.size();
			for(int i = 0;i < length;++i) {
				if(i != length - 1) {
					stringBuilder.append("?,");
				}else {
					stringBuilder.append("?)");
				}
			}
			stringBuilder.append(" and id < ?");
			stringBuilder.append(" order by id desc");
			stringBuilder.append(" limit 15");
			System.out.println(stringBuilder.toString());
			
			PreparedStatement statement = connection.prepareStatement(stringBuilder.toString());
			for(int i = 0, j=1;i < length;++i,++j) {
				System.out.println(friendIdList.get(i));
				statement.setInt(j, friendIdList.get(i));
			}
			length++;
			statement.setInt(length, id);
			ResultSet result = statement.executeQuery();
		
			while(null != result && result.next()) {
				dailyDatas.add(concatFriendDaily(result));
			}			
		return dailyDatas;	
	}
	
	private FriendRelationship parseFriendRelationship(ResultSet resultSet) throws Exception {
		FriendRelationship friendRelationship = new FriendRelationship();
		friendRelationship.id = resultSet.getInt("id");
		friendRelationship.applyID = resultSet.getInt("apply_id");
		friendRelationship.confirmID = resultSet.getInt("confirm_id");
		friendRelationship.status = resultSet.getInt("status");
		friendRelationship.createTime = resultSet.getTimestamp("create_time").getTime();
		return friendRelationship;
	}
	
	private FriendDailyReturnBean  concatFriendDaily(ResultSet result) throws Exception{
		FriendDailyReturnBean  friendDaily = new FriendDailyReturnBean ();
		friendDaily.id = result.getInt("id");
		friendDaily.name = result.getString("name");
		friendDaily.senderId = result.getInt("senderId");
		friendDaily.pulishDate = result.getString("publishDate");
		friendDaily.portrait = result.getString("portrait");
		friendDaily.videoFile = result.getString("videoFile");
		friendDaily.content = result.getString("content");
		handleImg(friendDaily,result.getString("image"));
		friendDaily.dailyType = result.getInt("dailyType");
		return friendDaily;
	}
	
	
	private List<Integer> getFriendListId(List<FriendRelationship> friendList,int mineId){
		List<Integer> idList = new ArrayList<>();
		idList.add(mineId);
		for(int i=0;i < friendList.size();i++) {
			if(friendList.get(i).applyID == mineId){
				idList.add(friendList.get(i).confirmID);				
			}else {				
				idList.add(friendList.get(i).applyID);
			}
		}
		
		return idList;
	}
	
	private void handleImg(FriendDailyReturnBean bean,String image) {
		List<String> mList = new ArrayList<String>();
		if(null != image && !"".equals(image)) {
			String[] images = image.split("\\|");
			System.out.println("imagesSize==" + images.length);
			if(images.length > 0) {
				for(int i = 0;i < images.length ;i++) {
					mList.add(Const.IMAGE_PREFIX + images[i]);	
				}
			}
		}
		bean.images = mList;
	}
	

}
