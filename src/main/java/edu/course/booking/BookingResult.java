package edu.course.booking;

public class BookingResult {
    private final BookingStatus status;
    private final String transactionId;
    private final String message;

    private BookingResult(BookingStatus status, String transactionId, String message) {
        this.status = status;
        this.transactionId = transactionId;
        this.message = message;
    }

    public static BookingResult success(String transactionId) {
        if (transactionId == null || transactionId.isBlank()) {
            throw new IllegalArgumentException("Transaction ID must be nonblank for successful result");
        }
        return new BookingResult(BookingStatus.SUCCESS, transactionId, "Booking processed successfully");
    }

    public static BookingResult soldOut(String eventId) {
        return new BookingResult(BookingStatus.SOLD_OUT, null, "Event " + eventId + " is sold out");
    }

    public static BookingResult paymentFailed(String reason) {
        return new BookingResult(BookingStatus.PAYMENT_FAILED, null, reason);
    }

    public static BookingResult invalid(String reason) {
        return new BookingResult(BookingStatus.INVALID_REQUEST, null, reason);
    }

    public boolean isSuccess() {
        return status == BookingStatus.SUCCESS;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getMessage() {
        return message;
    }
}
