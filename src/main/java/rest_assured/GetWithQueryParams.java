package rest_assured;

import io.restassured.RestAssured;
import io.restassured.response.Response;


import static io.restassured.RestAssured.given;

public class GetWithQueryParams {
	
	public static Response send_get_params() {
		
		RestAssured.baseURI ="http://jsonplaceholder.typicode.com";
		
		Response response = (Response) given().queryParam("id", "2")
	            .header("Content-type", "application/json")
	            .when()
	            .get("/users")
	            .then()
	            .extract();
		
		return (response);
		
	
		
	}
	
	

}
