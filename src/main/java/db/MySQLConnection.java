package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
	
	//Donghao Feng
	public String getFullname(String userId) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return "";
		}
		String name = "";
		String sql = "SELECT first_name, last_name FROM users WHERE user_id = ? ";
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				name = rs.getString("first_name") + " " + rs.getString("last_name");
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return name;
	}
	
	//Donghao Feng
	public boolean verifyLogin(String userId, String password) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return false;
		}
		String sql = "SELECT user_id FROM users WHERE user_id = ? AND password = ?";
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
	//Donghao Feng
	public boolean addUser(String userId, String password, String phoneNumber) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return false;
		}

		String sql = "INSERT INTO user VALUES (?, ?, ?)";
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			statement.setString(2, phoneNumber);
			statement.setString(3, password);
			statement.executeUpdate();

			return statement.executeUpdate() == 1;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	
	//Xianli Shen
	public Set<Item> getAllMachine() {
		if (conn == null) {
			System.err.println("DB connection failed");
			return null;
		}

		Set<Item> Items = new HashSet<>();
		String sql = "SELECT * FROM item";
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			ResultSet rs = statement.executeQuery();
			ItemBuilder builder = new ItemBuilder();
			while (rs.next()) {
				builder.setItemId(rs.getString("item_id"));
				builder.setType(rs.getString("type"));
				builder.setAddress(rs.getString("address"));
				builder.setUserId(rs.getString("user_id"));
				builder.setCondition(rs.getString("item_condition"));
				builder.setModel(rs.getString("model"));
				Items.add(builder.build());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return Items;
	}
	
	//Xianli Shen
	public void addMachine(String item_id, String type, String address, String item_condition,
			String model, String brand) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return;
		}
		String sql = "INSERT IGNORE INTO item (item_id, type, address, item_condition, model, brand) VALUES (?, ?, ?, ?, ?, ?)";
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, item_id);
			statement.setString(2, type);
			statement.setString(3, address);
			statement.setString(4, item_condition);
			statement.setString(5, model);
			statement.setString(6, brand);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	//Xianli Shen
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

	//Xianli Shen
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

	//Xianli Shen
	public void setReservation(String user_id, String item_id) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return;
		}
		String sql = "INSERT INTO reservation (user_id, item_id) VALUES (?, ?)";
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, user_id);
			statement.setString(2, item_id);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	//Xianli Shen
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

	//Xianli Shen
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

	//Xianli Shen
	public Set<Item> getReservedItems(String userId) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return new HashSet<>();
		}
		Set<Item> reservedItems = new HashSet<>();
		Set<String> itemIDs = getReservationIDs(userId);

		String sql = "SELECT * FROM item WHERE item_id = ?";
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
					reservedItems.add(builder.build());
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return reservedItems;
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
