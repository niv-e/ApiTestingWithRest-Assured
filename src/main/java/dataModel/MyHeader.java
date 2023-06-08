package dataModel;

import io.restassured.http.Header;

public class MyHeader{

	public String name;
	public String value;
	
	public MyHeader() {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public Header toHeader() {
		
		return new Header(this.getName(), this.getValue());
	}
	
	
}
