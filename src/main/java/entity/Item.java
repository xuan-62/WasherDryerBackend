package entity;

import org.json.JSONObject;

public class Item {
	private String itemId;
	private String type;
	private String address;
	private String userId;
	private String condition;
	private String model;
	private String brand;
	
	private Item(ItemBuilder builder) {
		this.itemId = builder.itemId;
		this.type = builder.type;
		this.address = builder.address;
		this.userId= builder.userId;
		this.condition = builder.condition;
		this.model = builder.model;
		this.brand = builder.brand;
	}

	
	public String getItemId() {
		return itemId;
	}
	public String getType() {
		return type;
	}

	public String getAddress() {
		return address;
	}
	
	public String getUserId() {
		return userId;
	}

	public String getCondition() {
		return condition;
	}

	public String getModel() {
		return model;
	}
	public String getBrand() {
		return brand;
	}
	public void setUserId(String userId) {
		this.userId = userId;	
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();
		obj.put("item_id", itemId);
		obj.put("type", type);
		obj.put("address", address);
		obj.put("user_id", userId);
		obj.put("condition", condition);
		obj.put("model", model);
		obj.put("brand", brand);
		return obj;
	}

	public static class ItemBuilder {
		private String itemId;
		private String type;
		private String address;
		private String userId;
		private String condition;
		private String model;
		private String brand;
		
		public void setItemId(String itemId) {
			this.itemId = itemId;
		}
		public void setType(String type) {
			this.type = type;
		}
		public void setAddress(String address) {
			this.address = address;
		}
		public void setUserId(String userId) {
			this.userId = userId;
		}
		public void setCondition(String condition) {
			this.condition = condition;
		}
		public void setModel(String model) {
			this.model = model;
		}
		public void setBrand(String brand) {
			this.brand = brand;
		}
		public Item build() {
			return new Item(this);
		}
	}
}
