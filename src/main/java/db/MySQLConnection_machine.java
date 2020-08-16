package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MySQLConnection_machine {
	private Connection conn;

	public MySQLConnection_machine() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(MySQLDBUtil.URL);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// TODO
	/*
	 * public void setcondition() { if (conn == null) {
	 * System.err.println("DB connection failed"); return; }
	 * 
	 * }
	 */

	public List<String> getAllMachine() {
		if (conn == null) {
			System.err.println("DB connection failed");
			return null;
		}
		/*
		 * Set<Item> reservedItems = new HashSet<>(); Set<String> reservationIDs =
		 * getReservationIDs(userId);
		 * 
		 * String sql = "SELECT * FROM items; 
		 * try { 
		 * PreparedStatement statement = conn.prepareStatement(sql); 
		 * ResultSet rs = statement.executeQuery();
		 * ItemBuilder builder = new ItemBuilder(); if (rs.next()) {
		 * builder.setItemId(rs.getString("item_id"));
		 * builder.setName(rs.getString("type"));
		 * builder.setAddress(rs.getString("address"));
		 * builder.setImageUrl(rs.getString("user_id"));
		 * builder.setUrl(rs.getString("item_condition"));
		 * builder.setKeywords(getKeywords(model)); reservedItems.add(builder.build());
		 * } } catch (SQLException e) { e.printStackTrace(); } return Items;
		 */

		return new ArrayList<String>();
	}

	public void addMachine(String item_id, String type, String address, String user_id, String item_condition,
			String model) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return;
		}
		String sql = "INSERT IGNORE INTO item (item_id, type, address, item_condition, model) VALUES (?, ?, ?, ?, ?)";
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, item_id);
			statement.setString(2, type);
			statement.setString(3, address);
			//statement.setString(4, user_id);
			statement.setString(4, item_condition);
			statement.setString(5, model);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

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

	/*
	 * public Set<Item> getReservedItems(String userId) { if (conn == null) {
	 * System.err.println("DB connection failed"); return new HashSet<>(); }
	 * Set<Item> reservedItems = new HashSet<>(); Set<String> reservationIDs =
	 * getReservationIDs(userId);
	 * 
	 * String sql = "SELECT * FROM items WHERE item_id = ?"; try { PreparedStatement
	 * statement = conn.prepareStatement(sql); for (String itemId : reservationIDs)
	 * { statement.setString(1, itemId); ResultSet rs = statement.executeQuery();
	 * 
	 * ItemBuilder builder = new ItemBuilder(); if (rs.next()) {
	 * builder.setItemId(rs.getString("item_id"));
	 * builder.setName(rs.getString("type"));
	 * builder.setAddress(rs.getString("address"));
	 * builder.setImageUrl(rs.getString("user_id"));
	 * builder.setUrl(rs.getString("item_condition"));
	 * builder.setKeywords(getKeywords(model)); reservedItems.add(builder.build());
	 * } } } catch (SQLException e) { e.printStackTrace(); } return reservedItems; }
	 */
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
