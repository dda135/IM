package mine.fanjh.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import mine.fanjh.DO.Token;
import mine.fanjh.DO.User;
import mine.fanjh.encryption.EncryptionWorker;


public class UserDAO {

	
	public List<User> searchUser(Connection connection,String keyword,int userID) throws Exception{
		
		String sql = "select * from user where mobile like ? and id <> ?";
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setString(1, "%" + keyword + "%");
		preparedStatement.setInt(2, userID);
		ResultSet resultSet = preparedStatement.executeQuery();
		
		List<User> users = new ArrayList<>();
		
		if(null == resultSet) {
			return users;
		}
		
		while(resultSet.next()) {
			users.add(concatUser(resultSet));
		}
		
		return users;
	}

	public User getUserMessage(Connection connection,int userID) throws Exception{
		
		String sql = "select * from user where id = ?";

		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setInt(1, userID);

		ResultSet result = preparedStatement.executeQuery();

		if(null != result && result.next()) {
			return concatUser(result);
		}
		
		return null;
	}
	
	public List<User> getUserMessages(Connection connection,int[] userIDs) throws Exception{
		
		StringBuilder stringBuilder = new StringBuilder("select * from user where id in (");
		int length = userIDs.length;
		for(int i = 0;i < length;++i) {
			if(i != length - 1) {
				stringBuilder.append("?,");
			}else {
				stringBuilder.append("?)");
			}
		}
		stringBuilder.append(" order by id asc");
		System.out.println(stringBuilder.toString());
		PreparedStatement preparedStatement = connection.prepareStatement(stringBuilder.toString());
		for(int i = 1;i <= length;++i) {
			System.out.println(userIDs[i - 1]);
			preparedStatement.setInt(i, userIDs[i-1]);
		}

		ResultSet result = preparedStatement.executeQuery();
		List<User> users = new ArrayList<>();	
		while(null != result && result.next()) {
			users.add(concatUser(result));
		}
		
		return users;
	}

	public boolean updateMessage(Connection connection,int userID,String avator,String nickname,String birth,String sex,String address) throws Exception{
		
		String sql = "update user set portrait = ?, nickname = ?, birth = ?, sex = ?, address = ? where id = ?";

		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setString(1, avator);
		preparedStatement.setString(2, nickname);
		preparedStatement.setString(3, birth);
		preparedStatement.setString(4, sex);
		preparedStatement.setString(5, address);
		preparedStatement.setInt(6, userID);

		return preparedStatement.executeUpdate() > 0;
	}
	
public boolean updateMessage(Connection connection,int userID,String nickname,String birth,String sex,String address) throws Exception{
		
		String sql = "update user set nickname = ?, nickname = ?, birth = ?, sex = ?, address = ? where id = ?";

		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setString(2, nickname);
		preparedStatement.setString(3, birth);
		preparedStatement.setString(4, sex);
		preparedStatement.setString(5, address);
		preparedStatement.setInt(6, userID);

		return preparedStatement.executeUpdate() > 0;
	}

	public User registerUser(Connection conn, Token token, String mobile, String password) throws Exception {

		String sql = "insert into user(token_id,nickname,password,mobile,create_time,last_login_time) values(?,?,?,?,?,?)";

		PreparedStatement preparedStatement = conn.prepareStatement(sql,PreparedStatement.RETURN_GENERATED_KEYS);
		preparedStatement.setInt(1, token.id);
		preparedStatement.setString(2, mobile);
		preparedStatement.setString(3, EncryptionWorker.MD5(password));
		preparedStatement.setString(4, mobile);
		preparedStatement.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
		preparedStatement.setTimestamp(6, new Timestamp(System.currentTimeMillis()));

		int result = preparedStatement.executeUpdate();

		if (result != -1) {// executeUpdate语句会返回一个受影响的行数，如果返回-1就没有成功
			ResultSet resultSet = preparedStatement.getGeneratedKeys();
			if(resultSet.next()) {
				int id = resultSet.getInt(1);
				User user = new User();
				user.id = id;
				user.createTime = System.currentTimeMillis();
				user.lastLoginTime = System.currentTimeMillis();
				user.nickname = mobile;
				user.tokenID = token.id;
				user.mobile = mobile;
				user.password = EncryptionWorker.MD5(password);
				return user;
			}
		}

		return null;
	}

	public boolean userExists(Connection conn, String mobile) throws Exception {

		String sql = "select id from user where mobile = ?";

		PreparedStatement preparedStatement = conn.prepareStatement(sql);
		preparedStatement.setString(1, mobile);

		ResultSet result = preparedStatement.executeQuery();

		return null != result && result.next();

	}
	
	public boolean userExists(Connection conn,String mobile, String password) throws Exception {
		
		String sql = "select id from user where mobile = ? and password = ?";

		PreparedStatement preparedStatement = conn.prepareStatement(sql);
		preparedStatement.setString(1, mobile);
		preparedStatement.setString(2, EncryptionWorker.MD5(password));

		ResultSet result = preparedStatement.executeQuery();

		return null != result && result.next();

	}

	public User queryUser(Connection conn, String mobile) throws Exception {

		String sql = "select * from user where mobile = ?";

		PreparedStatement preparedStatement = conn.prepareStatement(sql);
		preparedStatement.setString(1, mobile);

		ResultSet result = preparedStatement.executeQuery();
		
		if(result.next()) {
			return concatUser(result);
		}

		return null;
	}
	
	public boolean login(Connection connection,int tokenID,int userID) throws Exception{
		
		String sql = "update user set token_id = ?, last_login_time = ? where id = ?";

		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setInt(1, tokenID);
		preparedStatement.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
		preparedStatement.setInt(3, userID);

		int result = preparedStatement.executeUpdate();
		
		return result > 0;
	}
	
	private User concatUser(ResultSet result) throws Exception{
		User user = new User();
		user.id = result.getInt("id");
		user.tokenID = result.getInt("token_id");
		user.nickname = result.getString("nickname");
		user.createTime = result.getTimestamp("create_time").getTime();
		user.lastLoginTime = result.getTimestamp("last_login_time").getTime();
		user.portrait = result.getString("portrait");
		user.sex = result.getString("sex");
		user.birth = result.getString("birth");
		user.address = result.getString("address");
		user.password = result.getString("password");
		user.mobile = result.getString("mobile");
		return user;
	}
	
}
