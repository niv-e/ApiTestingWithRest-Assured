package dataModel;


public class ApiBookingRequest {

	public MyHeader [] headers;
	public BookingRequestBody bookingRequestBody;
	
	public ApiBookingRequest()
	{
		
	}

	public MyHeader[] getHeaders() {
		return headers;
	}

	public void setHeaders(MyHeader[] headers) {
		this.headers = headers;
	}

	public BookingRequestBody getBookingRequestBody() {
		return bookingRequestBody;
	}

	public void setBookingRequestBody(BookingRequestBody bookingRequestBody) {
		this.bookingRequestBody = bookingRequestBody;
	}
	
}
