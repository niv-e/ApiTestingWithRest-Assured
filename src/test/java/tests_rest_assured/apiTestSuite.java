package tests_rest_assured;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.*;

import dataModel.ApiBookingRequest;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import dataModel.MyHeader;

import io.restassured.response.Response;
import org.junit.rules.TestName;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import rest_assured.JsonReader;
import rest_assured.RestHttpClient;

import static org.junit.Assert.*;

public class apiTestSuite {
	
	private static Logger log;
	static JsonReader jsonReader = new JsonReader();

	@Rule
	public TestWatcher testWatcher = new TestWatcher() {
		protected void failed(Throwable e, Description description) {
			log.error("" + description.getDisplayName() + " failed: " + e.getMessage());
			super.failed(e, description);
		}
	};

	@Rule
	public TestName testName = new TestName();

    @BeforeClass
    public static void setUp() throws IOException {
		log = LogManager.getLogger(apiTestSuite.class);
		log.debug("Tests set up was done successfully.\n");
    }

	@Test
	public void createValidBooking()
	{
		log.info(testName.getMethodName() + " - start.");

		String inputJsonFile = "src/test/resources/validBookingInput.json";
		String urlCreateBooking = "https://restful-booker.herokuapp.com/booking";	
		String urlGetBookings = "https://restful-booker.herokuapp.com/booking";

		ApiBookingRequest apiBookingrequest = jsonReader.parseObjectFromJson(inputJsonFile, ApiBookingRequest.class);
		Headers headers = converTempHeadersToRequestHeaders(apiBookingrequest.getHeaders());

		log.debug("Create new booking using HTTP Post.");
		Response createBookingResponse = RestHttpClient.sendPostWithBody(
				apiBookingrequest.getBookingRequestBody(),
				headers,
				urlCreateBooking);

		assertEquals(HttpStatus.SC_OK, createBookingResponse.getStatusCode());

		urlGetBookings = urlGetBookings + "/" + createBookingResponse.jsonPath().getString("bookingid");

		log.debug("Getting the created booking using HTTP Get.");

		Response getBookingByID = RestHttpClient.sendGet(headers,urlGetBookings);

		assertEquals(HttpStatus.SC_OK, getBookingByID.getStatusCode());
		var actualFirstName = getBookingByID.jsonPath().getString("firstname");
		var expectedFirstName = apiBookingrequest.getBookingRequestBody().getFirstname();
		assertEquals(expectedFirstName, actualFirstName);
		log.info(testName.getMethodName() + " - end.");
	}

	@Test
	public void createInvalidBooking_CheckOutIsEarlierThenCheckIn_ReturnSc400()
	{
		log.info(testName.getMethodName() + " - start.");

		String inputJsonFile = "src/test/resources/InvalidBooking_BookingDatesError_Input.json";
		String urlCreateBooking = "https://restful-booker.herokuapp.com/booking";

		ApiBookingRequest apiBookingrequest = jsonReader.parseObjectFromJson(inputJsonFile, ApiBookingRequest.class);
		Headers headers = converTempHeadersToRequestHeaders(apiBookingrequest.getHeaders());

		log.debug("Create new booking using HTTP Post.");
		Response createBookingResponse = RestHttpClient.sendPostWithBody(
				apiBookingrequest.getBookingRequestBody(),
				headers,
				urlCreateBooking);

		assertNotEquals(HttpStatus.SC_OK, createBookingResponse.getStatusCode());
		log.info(testName.getMethodName() + " - end.");
	}

	// invalid input - should fail
	@Test
	public void createNonValidBooking_NegativeBookingPrice_ReturnSc400()
	{
		log.info(testName.getMethodName() + " - start.");

		String inputJsonFile = "src/test/resources/InvalidBooking_NegativePrice_Input.json";
		String urlCreateBooking = "https://restful-booker.herokuapp.com/booking";	

		ApiBookingRequest apiBookingrequest = jsonReader.parseObjectFromJson(inputJsonFile, ApiBookingRequest.class);
		Headers headers = converTempHeadersToRequestHeaders(apiBookingrequest.getHeaders());

		log.debug("Create new booking using HTTP Post.");
		Response createBookingResponse = RestHttpClient.sendPostWithBody(
				apiBookingrequest.getBookingRequestBody(),
				headers,
				urlCreateBooking);

		assertNotEquals(HttpStatus.SC_OK, createBookingResponse.getStatusCode());
		log.info(testName.getMethodName() + " - end.");
	}

	@Test
	public void changeExistingBookingWithoutAuthorization_ReturnSc403()
	{
		log.info(testName.getMethodName() + " - start.");

		log.info("Running create valid booking test for creating a new booking...");
		createValidBooking();

		log.debug("Getting all the existing bookings using HTTP Get.");

		String urlGetBookings = "https://restful-booker.herokuapp.com/booking";
		Response getAllBookings = RestHttpClient.sendGet(urlGetBookings);
		assertEquals(HttpStatus.SC_OK, getAllBookings.getStatusCode());

		String firstBookingId = getAllBookings.jsonPath().getString("bookingid").substring(1,4);
		String urlUpdateBooking = urlGetBookings + "/" + firstBookingId;

		String inputJsonFile = "src/test/resources/validBookingInput.json";
		ApiBookingRequest apiBookingrequest = jsonReader.parseObjectFromJson(inputJsonFile, ApiBookingRequest.class);
		Headers headers = converTempHeadersToRequestHeaders(apiBookingrequest.getHeaders());

		log.debug("Try to update the first booking in the list using HTTP Put.");
		Response updateBookingResponse = RestHttpClient.sendPutWithBody(
				apiBookingrequest.getBookingRequestBody(),
				headers,
				urlUpdateBooking);

		assertEquals(HttpStatus.SC_FORBIDDEN, updateBookingResponse.getStatusCode());
		log.info(testName.getMethodName() + " - end.");
	}

	@Test
	public void changeExistingBookingWithAuthorization()
	{
		log.info(testName.getMethodName() + " - start.");

		String inputJsonFile = "src/test/resources/validBookingInput.json";
		String urlCreateBooking = "https://restful-booker.herokuapp.com/booking";
		String urlGetBookings = "https://restful-booker.herokuapp.com/booking";

		log.info("Creating Auth Token");
		var token = createAuthToken();
		log.info("Token created successfully");

		log.debug("Create booking with the auth token");
		ApiBookingRequest apiBookingrequest = jsonReader.parseObjectFromJson(inputJsonFile, ApiBookingRequest.class);
		Headers headers = converTempHeadersToRequestHeaders(apiBookingrequest.getHeaders());

		List<Header> headersAsList = new ArrayList(headers.asList());
		headersAsList.add(new Header("Cookie","token="+token));
		Headers headerWithAuthCookie = new Headers(headersAsList);

		log.debug("Create new booking using HTTP Post.");
		Response createBookingResponse = RestHttpClient.sendPostWithBody(
				apiBookingrequest.getBookingRequestBody(),
				headerWithAuthCookie,
				urlCreateBooking);

		assertEquals(HttpStatus.SC_OK, createBookingResponse.getStatusCode());
		String bookingId = createBookingResponse.jsonPath().getString("bookingid").substring(1,4);
		String urlUpdateBooking = urlGetBookings + "/" + bookingId;

		apiBookingrequest.getBookingRequestBody().setFirstname("New Name");

		log.debug("Try to update the first booking in the list using HTTP Put.");
		Response updateBookingResponse = RestHttpClient.sendPutWithBody(
				apiBookingrequest.getBookingRequestBody(),
				headerWithAuthCookie,
				urlUpdateBooking);

		assertEquals(HttpStatus.SC_OK, updateBookingResponse.getStatusCode());

		var actualFirstName = updateBookingResponse.jsonPath().getString("firstname");
		var expectedFirstName = apiBookingrequest.getBookingRequestBody().getFirstname();
		assertEquals(expectedFirstName, actualFirstName);

		log.info(testName.getMethodName() + " - end.");
	}

	private static String createAuthToken() {
		String urlAuth = "https://restful-booker.herokuapp.com/auth";
		var headers = new Headers(new Header("Content-Type","application/json"));

		String inputJsonFile = "src/test/resources/AuthDetails.json";
		var authDetails = jsonReader.parseObjectFromJson(inputJsonFile, Object.class);
		Response getTokenResponse = RestHttpClient.sendPostWithBody(
				authDetails,
				headers,
				urlAuth);

		assertEquals(HttpStatus.SC_OK, getTokenResponse.getStatusCode());
		String token = getTokenResponse.jsonPath().getString("token");
		return token;
	}


	private Headers converTempHeadersToRequestHeaders(MyHeader [] headers)
	{
		var tempHeaders = Arrays.stream(headers)
		.map( x -> new Header(x.getName(),x.getValue()))
		.collect(Collectors.toList());		
		return new Headers(tempHeaders);
	}

}
