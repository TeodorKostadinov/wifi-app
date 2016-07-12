package database;

public enum SpotFields {
	fakeId("FakeId"), id("Id"), name("Name"), password("Password"), ip("Ip"), address(
			"Address"), latitude("Latitude"), longitude("Longitude"), isActive(
			"IsActive"), rowId("RowId");
	// TODO:
	
	private String text;
	
	SpotFields(String field) {
		this.text = field;
	}
	
	public String getText() {
		return this.text;
	}
}