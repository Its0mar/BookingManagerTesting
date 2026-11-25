package edu.course.booking;

import java.util.UUID;

public class BookingFactory {
    
     public Booking createBooking(
            String customerId,
            String eventId,
            int ticketQuantity,
            int totalAmountInCents,
            String transactionId) {

        String bookingId = UUID.randomUUID().toString();

        return new Booking(
                bookingId,
                customerId,
                eventId,
                ticketQuantity,
                totalAmountInCents,
                transactionId
        );
    }
}
