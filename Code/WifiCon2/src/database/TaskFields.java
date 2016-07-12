package database;

public enum TaskFields {
	fakeId("FakeId"), id("Id"), name("Name"), password("Password"), address(
			"Address"), keyword("Keyword"), website("Website"), status("Status"), longi(
			"Longi"), lati("Lati"), rowId("RowId");
	// TODO:
	
	private String text;
	
	TaskFields(String field) {
		this.text = field;
	}
	
	public String getText() {
		return this.text;
	}
}