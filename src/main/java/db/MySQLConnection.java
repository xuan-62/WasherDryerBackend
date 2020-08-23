package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import entity.Item;
import entity.Item.ItemBuilder;

public class MySQLConnection {
	private Connection conn;

	public MySQLConnection() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(MySQLDBUtil.URL);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Donghao Feng
	public String getFullname(String userId) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return "";
		}
		String name = "";
		String sql = "select phone_number FROM user WHERE user_id = ? ";
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				name = rs.getString("phone_number");
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return name;
	}

	// Donghao Feng
	public boolean verifyLogin(String userId, String password) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return false;
		}
		String sql = "SELECT user_id FROM user WHERE user_id = ? AND password = ?";
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			statement.setString(2, password);
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return false;
	}

	// Donghao Feng
	public boolean addUser(String userId, String phoneNumber, String password) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return false;
		}

		String sql = "SELECT * FROM user WHERE user_id = ? AND phone_number = ?";
		PreparedStatement preparedStatement;
		try {
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, userId);
			preparedStatement.setString(2, phoneNumber);
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.next()) {
				return false;
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String sql2 = "insert ignore INTO user VALUES (?, ?, ?)";
		try {
			PreparedStatement statement = conn.prepareStatement(sql2);
			statement.setString(1, userId);
			statement.setString(2, phoneNumber);
			statement.setString(3, password);
			statement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	// Xianli Shen
	public Set<Item> getAllMachine() {
		if (conn == null) {
			System.err.println("DB connection failed");
			return null;
		}

		Set<Item> Items = new HashSet<>();
		String sql = "select * from item left join reservation on item.item_id = reservation.item_id";
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			ResultSet rs = statement.executeQuery();
			ItemBuilder builder;
			while (rs.next()) {
				builder = new ItemBuilder();
				builder.setItemId(rs.getString("item_id"));
				builder.setType(rs.getString("type"));
				builder.setAddress(rs.getString("address"));
				builder.setUserId(rs.getString("user_id"));
				builder.setCondition(rs.getString("item_condition"));
				builder.setModel(rs.getString("model"));
				builder.setBrand(rs.getString("brand"));
				builder.setEndtime(rs.getString("end_time"));
				Items.add(builder.build());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return Items;
	}

	// Xianli Shen
	public void addMachine(Item item) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return;
		}
		String sql = "INSERT IGNORE INTO item (item_id, type, address, item_condition, model, brand) VALUES (?, ?, ?, ?, ?, ?)";
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, item.getItemId());
			statement.setString(2, item.getType());
			statement.setString(3, item.getAddress());
			statement.setString(4, item.getCondition());
			statement.setString(5, item.getModel());
			statement.setString(6, item.getBrand());
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void updateCondition(String item_id, String condition) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return;
		}
		String sql = "UPDATE item SET item_condition=? WHERE item_id = ?";
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, condition);
			statement.setString(2, item_id);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Xianli Shen
	public void addUsertoItem(String item_id, String user_id) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return;
		}

		String sql = "UPDATE item SET user_id=? WHERE item_id = ?";
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, user_id);
			statement.setString(2, item_id);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Xianli Shen
	public void removeUserfromItem(String item_id) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return;
		}

		String sql = "UPDATE item SET user_id = NULL WHERE item_id = ?";
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, item_id);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Xianli Shen
	public void setReservation(String user_id, String item_id, int time) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return;
		}
		String sql = "INSERT INTO reservation (user_id, item_id, start_time, end_time) VALUES (?, ?, ?, ?)";
		try {

			PreparedStatement statement = conn.prepareStatement(sql);

			Date start_time = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(start_time);
			cal.add(Calendar.MINUTE, time);
			Date end_time = cal.getTime();

			statement.setString(1, user_id);
			statement.setString(2, item_id);
			statement.setTimestamp(3, new Timestamp(start_time.getTime()));
			statement.setTimestamp(4, new Timestamp(end_time.getTime()));
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Xianli Shen
	public void removeReservation(String user_id, String item_id) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return;
		}
		String sql = "DELETE FROM reservation WHERE user_id = ? AND item_id = ?";
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, user_id);
			statement.setString(2, item_id);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Xianli Shen
	public Set<String> getReservationIDs(String userId) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return new HashSet<>();
		}

		Set<String> Reservations = new HashSet<>();

		try {
			String sql = "SELECT item_id FROM reservation WHERE user_id = ?";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				String itemId = rs.getString("item_id");
				Reservations.add(itemId);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return Reservations;
	}

	// Xianli Shen
	public Set<Item> getReservedItems(String userId) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return new HashSet<>();
		}
		Set<Item> reservedItems = new HashSet<>();
		Set<String> itemIDs = getReservationIDs(userId);
		String sql = "SELECT * FROM item, reservation WHERE reservation.item_id = item.item_id AND item.item_id = ?";
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			for (String itemId : itemIDs) {
				statement.setString(1, itemId);
				ResultSet rs = statement.executeQuery();

				ItemBuilder builder = new ItemBuilder();
				if (rs.next()) {
					builder.setItemId(rs.getString("item_id"));
					builder.setType(rs.getString("type"));
					builder.setAddress(rs.getString("address"));
					builder.setUserId(rs.getString("user_id"));
					builder.setCondition(rs.getString("item_condition"));
					builder.setModel(rs.getString("model"));
					builder.setBrand(rs.getString("brand"));
					builder.setEndtime(rs.getString("end_Time"));
					reservedItems.add(builder.build());
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return reservedItems;
	}
	//Xianli Shen
	public String getMachineType(String item_id) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return null;
		}
		String itemId  = "";
		try {
			String sql = "SELECT type FROM item WHERE item_id = ?";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, item_id);
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				itemId = rs.getString("type");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return itemId;
	}

	public void close() {
		if (conn != null) {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
