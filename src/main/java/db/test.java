package db;

public class test {
	public static void main(String[] args) {
		MySQLConnection_machine connection = new MySQLConnection_machine();
		//connection.addMachine("test2", "typadasdaeB", "USsaA", "1112", "empadaty", "Bada");
		//connection.setReservation("1112", "abd");
		//connection.removeReservation("1112", "abd");
		
		System.out.println(connection.getReservationIDs("1112").toString());
	}
}
