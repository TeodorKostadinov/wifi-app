package util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class UserException {
	
	private Calendar timeOfError;
	private Exception exception;
	private String fromClassName;
	private String classErrorName;
	
	public String getClassErrorName() {
		return this.classErrorName;
	}
	
	public void setClassErrorName(String classErrorName) {
		this.classErrorName = classErrorName;
	}
	
	public String getFromClassName() {
		return this.fromClassName;
	}
	
	public void setFromClassName(String className) {
		this.fromClassName = className;
	}
	
	public Calendar getTimeOfError() {
		return timeOfError;
	}
	
	public Exception getException() {
		return exception;
	}
	
	public void setTimeOfError(Calendar timeOfError) {
		this.timeOfError = timeOfError;
	}
	
	public void setException(Exception exception) {
		this.exception = exception;
	}
	
	@Override
	public String toString() {
		String output = "";
		output += this.fromClassName + Util.SEPARATOR;
		output += this.classErrorName + Util.SEPARATOR;
		output += this.timeOfError.get( Calendar.YEAR ) + ",  "
				+ this.timeOfError.get( Calendar.MONTH ) + ", "
				+ this.timeOfError.get( Calendar.DAY_OF_MONTH ) + ", "
				+ this.timeOfError.get( Calendar.HOUR ) + ", "
				+ this.timeOfError.get( Calendar.MINUTE ) + ", "
				+ this.timeOfError.get( Calendar.SECOND ) + ", "
				+ Util.SEPARATOR;
		
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter( writer );
		exception.printStackTrace( printWriter );
		String s = writer.toString();
		
		output += s.trim() + Util.SEPARATOR;
		return output;
	}
	
	public UserException(String className, Exception exception) {
		this.fromClassName = className;
		this.timeOfError = new GregorianCalendar();
		
		this.exception = exception;
		this.classErrorName = exception.getClass().toString();
		exception.printStackTrace();
	}
	
	public String getExceptionStack() {
		String output = "";
		
		StackTraceElement[] elm = exception.getStackTrace();
		for (StackTraceElement stackTraceElement : elm) {
			output += stackTraceElement.getClassName().toString() + ".";
			output += stackTraceElement.getFileName().toString();
			output += "(" + stackTraceElement.getMethodName().toString() + ":";
			output += stackTraceElement.getLineNumber() + ") ";
		}
		return output;
	}
	
	public UserException() {
	}
}
