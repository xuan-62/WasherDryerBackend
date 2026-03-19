package rpc;

import entity.Machine;
import org.json.JSONObject;
import org.junit.Test;
import static org.junit.Assert.*;

public class RpcHelperTest {

	@Test
	public void testBuildItem() {
		JSONObject input = new JSONObject()
				.put("item_id", "W001")
				.put("type", "washer")
				.put("address", "Building A")
				.put("item_condition", "available")
				.put("model", "WF45T6000AW")
				.put("brand", "Samsung");

		Machine m = RpcHelper.buildMachine(input);
		assertEquals("W001", m.itemId());
		assertEquals("washer", m.type());
		assertEquals("Building A", m.address());
		assertEquals("available", m.condition());
		assertEquals("WF45T6000AW", m.model());
		assertEquals("Samsung", m.brand());
		assertNull(m.userId());
		assertNull(m.endTime());
	}
}
