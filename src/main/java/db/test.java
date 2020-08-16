package db;

import java.util.Iterator;
import java.util.Set;

import entity.Item;

public class test {
	public static void main(String[] args) {
		MySQLConnection_machine connection = new MySQLConnection_machine();
		connection.addMachine("W001", "typeA", "USA", "empty", "A");
		connection.addMachine("W002", "typeB", "USB", "empty", "B");
		//connection.reserveUser("W001", "1112");
		//connection.setReservation("1112", "W001");
		
		//connection.removeReservation("1112", "W001");
		//connection.removeUserInItem("W001");
		//System.out.println(connection.getReservationIDs("1112").toString());
		//Set<Item> temp = connection.getAllMachine();
		//Set<Item> temp2 = connection.getReservedItems("1112");
		//System.out.println(temp.toString());
		
		//Iterator<Item> iterator = temp2.iterator();
        //while (iterator.hasNext()){
        //    String str = iterator.next().toJSONObject().toString();
        //    System.out.println(str);
        //}
	}
}
