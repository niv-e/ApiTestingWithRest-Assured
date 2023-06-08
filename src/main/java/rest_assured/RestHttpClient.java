package rest_assured;

import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.response.Response;


import static io.restassured.RestAssured.given;



public class RestHttpClient {
	
	public static Response send_pst() {

	String requestBody = "{\n" +
		"  \"name\": \"formystudents\",\n" +
		"  \"job\": \"qaProffessional\"\n}" ;

	RestAssured.baseURI ="https://reqres.in";

	Response response = (Response) given()
			.header("Content-type", "application/json")
			.and()
			.body(requestBody)
			.when()
			.post("/api/users")
			.then()
			.extract();
    return (response);
    }

	public static Response sendPostWithBody(Object requestBody, Headers headers, String url) {       
		Response response = (Response) given()
            .headers(headers)
            .and()
            .body(requestBody)
            .when()
            .post(url)
            .then()
            .extract();    
		return (response);
	}
	
	public static Response sendGet(Headers headers, String url) {       
		Response response = (Response) given()
            .headers(headers)
            .and()
            .when()
            .get(url)
            .then()
            .extract();    
		return (response);
	}
	
	public static Response sendGet( String url) {       
		Response response = (Response) given()
            .and()
            .when()
            .get(url)
            .then()
            .extract();    
		return (response);
	}
	public static Response sendPutWithBody(Object requestBody, String url) {
		Response response = (Response) given()
				.and()
				.body(requestBody)
				.when()
				.put(url)
				.then()
				.extract();
		return (response);
	}
	public static Response sendPutWithBody(Object requestBody, Headers headers, String url) {       
		Response response = (Response) given()
            .headers(headers)
            .and()
            .body(requestBody)
            .when()
            .put(url)
            .then()
            .extract();    
		return (response);
	}
}
	
	



	
