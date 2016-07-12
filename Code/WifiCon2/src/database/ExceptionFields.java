package database;

public enum ExceptionFields {
	fromClassName("FromClassName"), classErrorName("ClassErrorName"), exceptionStack(
			"ExceptionStack"), timeOfError("TimeOfError");
	// TODO:
	private String text;
	
	ExceptionFields(String field) {
		this.text = field;
	}
	
	public String getText() {
		return this.text;
	}
}
