package mine.fanjh.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import mine.fanjh.DO.FriendApply;
import mine.fanjh.DO.FriendRelationship;

public class FriendDAO {
	
	public boolean isFriendExist(Connection connection, int applyID, int confirmID) throws Exception{
		
		String sql = "select id from friend_relationship where (apply_id = ? and confirm_id = ?) or (apply_id = ? and confirm_id = ?)";
		
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setInt(1, applyID);
		preparedStatement.setInt(2, confirmID);
		preparedStatement.setInt(3, confirmID);
		preparedStatement.setInt(4, applyID);
		
		ResultSet resultSet = preparedStatement.executeQuery();
		
		return null != resultSet && resultSet.next();
	}
	
	public boolean isFriendApplyExist(Connection connection, int applyID, int confirmID) throws Exception{
		
		String sql = "select id from friend_apply where (apply_id = ? and confirm_id = ?) or (apply_id = ? and confirm_id = ?)";
		
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setInt(1, applyID);
		preparedStatement.setInt(2, confirmID);
		preparedStatement.setInt(3, confirmID);
		preparedStatement.setInt(4, applyID);
		
		ResultSet resultSet = preparedStatement.executeQuery();
		
		return null != resultSet && resultSet.next();
	}

	public int addFriendApply(Connection connection, int applyID, int confirmID, String content) throws Exception {

		String sql = "insert into friend_apply(apply_id,confirm_id,status,create_time,last_change_time,content) values(?,?,?,?,?,?)";

		PreparedStatement preparedStatement = connection.prepareStatement(sql,PreparedStatement.RETURN_GENERATED_KEYS);
		preparedStatement.setInt(1, applyID);
		preparedStatement.setInt(2, confirmID);
		preparedStatement.setInt(3, FriendApply.STATUS_APPLYING);
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		preparedStatement.setTimestamp(4, timestamp);
		preparedStatement.setTimestamp(5, timestamp);
		preparedStatement.setString(6, content);

		int result = -1;
		
		if(preparedStatement.executeUpdate() > 0) {
			ResultSet resultSet = preparedStatement.getGeneratedKeys();
			if(null != resultSet && resultSet.next()) {
				result = resultSet.getInt(1);
			}
		}
		
		return result;

	}

	public boolean updateFriendApplyStatus(Connection connection, int applyID, int confirmID, int status)
			throws Exception {

		String sql = "update friend_apply set status = ?, last_change_time = ? where apply_id = ? and confirm_id = ?";

		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setInt(1, status);
		preparedStatement.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
		preparedStatement.setInt(3, applyID);
		preparedStatement.setInt(4, confirmID);

		return preparedStatement.executeUpdate() > 0;

	}

	public int addFriend(Connection connection, int applyID, int confirmID) throws Exception {

		String sql = "insert into friend_relationship(apply_id,confirm_id,status,create_time,last_change_time) values(?,?,?,?,?)";

		PreparedStatement preparedStatement = connection.prepareStatement(sql,PreparedStatement.RETURN_GENERATED_KEYS);
		preparedStatement.setInt(1, applyID);
		preparedStatement.setInt(2, confirmID);
		preparedStatement.setInt(3, FriendRelationship.STATUS_FRIEND);
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		preparedStatement.setTimestamp(4, timestamp);
		preparedStatement.setTimestamp(5, timestamp);

		int result = -1;
		if(preparedStatement.executeUpdate() > 0) {
			ResultSet resultSet = preparedStatement.getGeneratedKeys();
			if(null != resultSet && resultSet.next()) {
				result = resultSet.getInt(1);
			}
		}
		
		return result;
	}

	public List<FriendApply> getFriendApplyList(Connection connection, int userID) throws Exception {

		String sql = "select * from friend_apply where confirm_id = ? or apply_id = ?";

		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setInt(1, userID);
		preparedStatement.setInt(2, userID);

		ResultSet resultSet = preparedStatement.executeQuery();

		List<FriendApply> list = new ArrayList<>();

		while (null != resultSet && resultSet.next()) {
			list.add(parseFriendApply(resultSet));
		}

		return list;
	}
	
	public List<FriendApply> getFriendApplyList(Connection connection, int userID, int minID) throws Exception {

		String sql = "select * from friend_apply where (confirm_id = ? or apply_id = ?) and id > ?";

		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setInt(1, userID);
		preparedStatement.setInt(2, userID);
		preparedStatement.setInt(3, minID);

		ResultSet resultSet = preparedStatement.executeQuery();

		List<FriendApply> list = new ArrayList<>();

		while (null != resultSet && resultSet.next()) {
			list.add(parseFriendApply(resultSet));
		}

		return list;
	}

	public List<FriendRelationship> getFriendRelationshipList(Connection connection, int userID) throws Exception {

		String sql = "select * from friend_relationship where confirm_id = ? or apply_id = ?";

		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setInt(1, userID);
		preparedStatement.setInt(2, userID);

		ResultSet resultSet = preparedStatement.executeQuery();

		List<FriendRelationship> list = new ArrayList<>();

		while (null != resultSet && resultSet.next()) {
			list.add(parseFriendRelationship(resultSet));
		}

		return list;
	}
	
	public List<FriendRelationship> getFriendRelationshipList(Connection connection, int userID, int minID) throws Exception {

		String sql = "select * from friend_relationship where (confirm_id = ? or apply_id = ?) and id > ?";

		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setInt(1, userID);
		preparedStatement.setInt(2, userID);
		preparedStatement.setInt(3, minID);

		ResultSet resultSet = preparedStatement.executeQuery();

		List<FriendRelationship> list = new ArrayList<>();

		while (null != resultSet && resultSet.next()) {
			list.add(parseFriendRelationship(resultSet));
		}

		return list;
	}


	private FriendApply parseFriendApply(ResultSet resultSet) throws Exception {
		FriendApply friendApply = new FriendApply();
		friendApply.id = resultSet.getInt("id");
		friendApply.applyID = resultSet.getInt("apply_id");
		friendApply.confirmID = resultSet.getInt("confirm_id");
		friendApply.status = resultSet.getInt("status");
		friendApply.createTime = resultSet.getTimestamp("create_time").getTime();
		friendApply.content = resultSet.getString("content");
		return friendApply;
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

}
