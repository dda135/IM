package mine.fanjh.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import mine.fanjh.DO.Token;

public class TokenDAO {

	public static Date getNextMonthDate() {
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.add(Calendar.MONTH, 1);
		return calendar.getTime();
	}

	public boolean extendTokenExpire(Connection connection,Date newExpireTime, int tokenID) throws Exception{

		String sql = "update token set expire_time = ? where id = ?";
		
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setLong(1, newExpireTime.getTime());
		preparedStatement.setInt(2, tokenID);
		
		int result = preparedStatement.executeUpdate();
		
		return result > 0;

	}
	
	public boolean checkTokenExpire(Connection connection,String tokenValue, int userID) throws Exception{

		String sql = "select id from token where token_value = ? and id = (select token_id from user where id = ?)";
		
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setString(1, tokenValue);
		preparedStatement.setInt(2, userID);
		
		ResultSet resultSet = preparedStatement.executeQuery();
		
		return null != resultSet && resultSet.next();

	}
	
	public int checkTokenExpireID(Connection connection,String tokenValue, int userID) throws Exception{

		String sql = "select id from token where token_value = ? and id = (select token_id from user where id = ?)";
		
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setString(1, tokenValue);
		preparedStatement.setInt(2, userID);
		
		ResultSet resultSet = preparedStatement.executeQuery();
		
		if(null != resultSet && resultSet.next()) {
			return resultSet.getInt("id");
		}
		
		return 0;

	}

	
	public Token findTokenWithID(Connection connection, int tokenID) throws Exception {

		String sql = "select * from token where id = ?";

		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setInt(1, tokenID);

		ResultSet result = preparedStatement.executeQuery();
		
		if(null != result && result.next()) {
			Token token = new Token();
			token.id = result.getInt("id");
			token.tokenValue = result.getString("token_value");
			token.createTime = result.getTimestamp("create_time").getTime();
			token.expireTime = result.getTimestamp("expire_time").getTime();
			return token;
		}

		return null;

	}

	public Token getNewToken(Connection connection) throws Exception {

		String sql = "insert into token(token_value,create_time,expire_time) values(?,?,?)";

		PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		Date createDate = new Date();
		String tokenValue = UUID.randomUUID().toString();
		Date expireDate = getNextMonthDate();
		preparedStatement.setString(1, tokenValue);
		preparedStatement.setTimestamp(2, new Timestamp(createDate.getTime()));
		preparedStatement.setTimestamp(3, new Timestamp(expireDate.getTime()));
		int result = preparedStatement.executeUpdate();

		if (result > 0) {
			ResultSet resultSet = preparedStatement.getGeneratedKeys();
			if (resultSet.next()) {
				int id = resultSet.getInt(1);
				Token token = new mine.fanjh.DO.Token();
				token.createTime = createDate.getTime();
				token.expireTime = expireDate.getTime();
				token.tokenValue = tokenValue;
				token.id = id;
				return token;
			}
		}

		return null;

	}

}
