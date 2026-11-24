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
        // Red stage: stub implementation that does not perform validation
        return null;
    }
}
