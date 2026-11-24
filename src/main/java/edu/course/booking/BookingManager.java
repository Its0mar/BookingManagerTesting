package edu.course.booking;

public class BookingManager {
    private final IEventRepository eventRepository;
    private final IPaymentGateway paymentGateway;
    private final INotificationService notificationService;

    public BookingManager(IEventRepository eventRepository, IPaymentGateway paymentGateway, INotificationService notificationService) {
        this.eventRepository = eventRepository;
        this.paymentGateway = paymentGateway;
        this.notificationService = notificationService;
    }

    public BookingResult processBooking(BookingRequest request) {
        if (request == null || !request.IsValid()) {
            throw new IllegalArgumentException("Invalid booking request");
        }
        return null;
    }
}
