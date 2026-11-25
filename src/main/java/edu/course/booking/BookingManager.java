package edu.course.booking;

public class BookingManager {
    private final IEventRepository eventRepository;
    private final IPaymentGateway paymentGateway;
    private final INotificationService notificationService;
    private final BookingFactory bookingFactory;

    public BookingManager(IEventRepository eventRepository, IPaymentGateway paymentGateway, INotificationService notificationService) {
        this.eventRepository = eventRepository;
        this.paymentGateway = paymentGateway;
        this.notificationService = notificationService;
        this.bookingFactory = new BookingFactory();
    }

    
public BookingResult processBooking(BookingRequest request) {
    validateRequest(request);

    String eventId = request.getEventId();
    String customerId = request.getCustomerId();

    if (eventRepository.isSoldOut(eventId)) {
        return BookingResult.soldOut(eventId);
    }

    int totalAmount = request.getTicketQuantity()
            * request.getPricePerTicketInCents();

    String transactionId;

    try {
        transactionId = paymentGateway.processPayment(totalAmount);
    } catch (PaymentFailedException e) {
        return BookingResult.paymentFailed(e.getMessage());
    }

    Booking booking = bookingFactory.createBooking(
        customerId,
        eventId,
        request.getTicketQuantity(),
        totalAmount,
        transactionId
);

    eventRepository.saveBooking(booking);

    notificationService.sendConfirmation(
            customerId,
            eventId,
            transactionId
    );

    return BookingResult.success(transactionId);
}


    private void validateRequest(BookingRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Booking request must not be null");
        }
        if (request.getCustomerId() == null || request.getCustomerId().isBlank()) {
            throw new IllegalArgumentException("Customer ID must not be null or blank");
        }
        if (request.getEventId() == null || request.getEventId().isBlank()) {
            throw new IllegalArgumentException("Event ID must not be null or blank");
        }
        if (request.getTicketQuantity() < 1 || request.getTicketQuantity() > 5) {
            throw new IllegalArgumentException("Ticket quantity must be between 1 and 5 inclusive");
        }
        if (request.getPricePerTicketInCents() <= 0) {
            throw new IllegalArgumentException("Price per ticket in cents must be greater than zero");
        }
    }
}
