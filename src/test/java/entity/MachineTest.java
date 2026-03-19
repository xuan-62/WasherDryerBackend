package entity;

import org.json.JSONObject;
import org.junit.Test;
import static org.junit.Assert.*;

public class MachineTest {

	@Test
	public void testConstruction() {
		Machine m = new Machine("M001", "washer", "Building A", null, "available", "WF45T6000AW", "Samsung", null);
		assertEquals("M001", m.itemId());
		assertEquals("washer", m.type());
		assertEquals("Building A", m.address());
		assertNull(m.userId());
		assertEquals("available", m.condition());
		assertEquals("WF45T6000AW", m.model());
		assertEquals("Samsung", m.brand());
		assertNull(m.endTime());
	}

	@Test
	public void testToJSONObject() {
		Machine m = new Machine("M001", "washer", "Building A", "user1", "available", "WF45T6000AW", "Samsung", "2026-01-01 12:00:00");
		JSONObject json = m.toJSONObject();
		assertEquals("M001", json.getString("item_id"));
		assertEquals("washer", json.getString("type"));
		assertEquals("Building A", json.getString("address"));
		assertEquals("user1", json.getString("user_id"));
		assertEquals("available", json.getString("condition"));
		assertEquals("WF45T6000AW", json.getString("model"));
		assertEquals("Samsung", json.getString("brand"));
		assertEquals("2026-01-01 12:00:00", json.getString("end_time"));
	}

	@Test
	public void testValueEquality() {
		Machine a = new Machine("M001", "washer", "Building A", null, "available", "ModelX", "Samsung", null);
		Machine b = new Machine("M001", "washer", "Building A", null, "available", "ModelX", "Samsung", null);
		assertEquals(a, b);
		assertEquals(a.hashCode(), b.hashCode());
	}
}
