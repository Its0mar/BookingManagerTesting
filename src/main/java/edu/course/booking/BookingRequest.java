package edu.course.booking;

public class BookingRequest {
    private final String CustomerId;
    private final String EventId;
    private final int TicketQuantity;
    private final int PricePerTicketInCents;

    public BookingRequest(String customerId, String eventId, int ticketQuantity, int pricePerTicketInCents) {
        CustomerId = customerId;
        EventId = eventId;
        TicketQuantity = ticketQuantity;
        PricePerTicketInCents = pricePerTicketInCents;
    }

    public String getCustomerId() {
        return CustomerId;
    }

    public String getEventId() {
        return EventId;
    }

    public int getTicketQuantity() {
        return TicketQuantity;
    }

    public int getPricePerTicketInCents() {
        return PricePerTicketInCents;
    }

    public boolean IsValid() {
        if (CustomerId == null || CustomerId.trim().isEmpty()) {
            return false;
        }
        if (EventId == null || EventId.trim().isEmpty()) {
            return false;
        }
        if (TicketQuantity < 1 || TicketQuantity > 5) {
            return false;
        }
        return PricePerTicketInCents > 0;
    }
}
