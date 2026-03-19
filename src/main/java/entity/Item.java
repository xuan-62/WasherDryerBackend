package entity;

import org.json.JSONObject;

public record Item(
		String itemId,
		String type,
		String address,
		String userId,
		String condition,
		String model,
		String brand,
		String endTime) {

	public JSONObject toJSONObject() {
		return new JSONObject()
				.put("item_id", itemId)
				.put("type", type)
				.put("address", address)
				.put("user_id", userId)
				.put("condition", condition)
				.put("model", model)
				.put("brand", brand)
				.put("end_time", endTime);
	}
}
