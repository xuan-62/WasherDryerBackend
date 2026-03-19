package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.mindrot.jbcrypt.BCrypt;

import entity.Machine;

public class MySQLConnection implements AutoCloseable {
	private Connection conn;

	public MySQLConnection() {
		try {
			conn = DriverManager.getConnection(MySQLDBUtil.URL);
		} catch (SQLException e) {
			throw new RuntimeException("Failed to connect to database", e);
		}
	}

	public String getFullname(String userId) {
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
			throw new RuntimeException(e);
		}
		return name;
	}

	public boolean verifyLogin(String userId, String password) {
		String sql = "SELECT password FROM user WHERE user_id = ?";
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				return BCrypt.checkpw(password, rs.getString("password"));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return false;
	}

	public boolean addUser(String userId, String email, String password) {
		String sql = "SELECT * FROM user WHERE user_id = ? AND email = ?";
		try {
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, userId);
			preparedStatement.setString(2, email);
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.next()) {
				return false;
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		String sql2 = "INSERT IGNORE INTO user (user_id, email, password, role) VALUES (?, ?, ?, 'user')";
		try {
			PreparedStatement statement = conn.prepareStatement(sql2);
			statement.setString(1, userId);
			statement.setString(2, email);
			statement.setString(3, BCrypt.hashpw(password, BCrypt.gensalt()));
			statement.executeUpdate();
			return true;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public String getRole(String userId) {
		String sql = "SELECT role FROM user WHERE user_id = ?";
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				return rs.getString("role");
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return "user";
	}

	public String getEmail(String user_id) {
		String email = "";
		try {
			String sql = "SELECT email FROM user WHERE user_id = ?";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, user_id);
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				email = rs.getString("email");
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return email;
	}

	public Set<Machine> getAllMachine() {
		Set<Machine> items = new HashSet<>();
		String sql = "select * from item left join reservation on item.item_id = reservation.item_id";
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				items.add(new Machine(
						rs.getString("item_id"),
						rs.getString("type"),
						rs.getString("address"),
						rs.getString("user_id"),
						rs.getString("item_condition"),
						rs.getString("model"),
						rs.getString("brand"),
						rs.getString("end_time")));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return items;
	}

	public void addMachine(Machine item) {
		String sql = "INSERT IGNORE INTO item (item_id, type, address, item_condition, model, brand) VALUES (?, ?, ?, ?, ?, ?)";
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, item.itemId());
			statement.setString(2, item.type());
			statement.setString(3, item.address());
			statement.setString(4, item.condition());
			statement.setString(5, item.model());
			statement.setString(6, item.brand());
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public String getCondition(String item_id) {
		String item_condition = "";
		try {
			String sql = "SELECT item_condition FROM item WHERE item_id = ?";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, item_id);
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				item_condition = rs.getString("item_condition");
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return item_condition;
	}

	public void updateCondition(String item_id, String condition) {
		String sql = "UPDATE item SET item_condition=? WHERE item_id = ?";
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, condition);
			statement.setString(2, item_id);
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void addUsertoMachine(String item_id, String user_id) {
		String sql = "UPDATE item SET user_id=? WHERE item_id = ?";
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, user_id);
			statement.setString(2, item_id);
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void removeUserfromMachine(String item_id) {
		String sql = "UPDATE item SET user_id = NULL WHERE item_id = ?";
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, item_id);
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void setReservation(String user_id, String item_id, int time) {
		String sql = "INSERT INTO reservation (user_id, item_id, start_time, end_time) VALUES (?, ?, ?, ?)";
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			LocalDateTime start_time = LocalDateTime.now();
			LocalDateTime end_time = start_time.plusMinutes(time);
			statement.setString(1, user_id);
			statement.setString(2, item_id);
			statement.setTimestamp(3, Timestamp.valueOf(start_time));
			statement.setTimestamp(4, Timestamp.valueOf(end_time));
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void removeReservation(String user_id, String item_id) {
		String sql = "DELETE FROM reservation WHERE user_id = ? AND item_id = ?";
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, user_id);
			statement.setString(2, item_id);
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public Set<String> getReservationIDs(String userId) {
		Set<String> reservations = new HashSet<>();
		try {
			String sql = "SELECT item_id FROM reservation WHERE user_id = ?";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				reservations.add(rs.getString("item_id"));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return reservations;
	}

	public Set<Machine> getReservedMachines(String userId) {
		Set<Machine> reservedMachines = new HashSet<>();
		Set<String> itemIDs = getReservationIDs(userId);
		String sql = "SELECT * FROM item, reservation WHERE reservation.item_id = item.item_id AND item.item_id = ?";
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			for (String itemId : itemIDs) {
				statement.setString(1, itemId);
				ResultSet rs = statement.executeQuery();
				if (rs.next()) {
					reservedMachines.add(new Machine(
							rs.getString("item_id"),
							rs.getString("type"),
							rs.getString("address"),
							rs.getString("user_id"),
							rs.getString("item_condition"),
							rs.getString("model"),
							rs.getString("brand"),
							rs.getString("end_Time")));
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return reservedMachines;
	}

	public String getMachineType(String item_id) {
		String type = "";
		try {
			String sql = "SELECT type FROM item WHERE item_id = ?";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, item_id);
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				type = rs.getString("type");
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return type;
	}

	@Override
	public void close() {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
