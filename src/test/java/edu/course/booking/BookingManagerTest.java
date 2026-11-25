package edu.course.booking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingManagerTest {
    @Mock
    private IEventRepository eventRepository;

    @Mock
    private INotificationService notificationService;

    @Mock
    private IPaymentGateway paymentGateway;

    private  BookingManager bookingManager;

    @BeforeEach
    private  void setUp() {
        bookingManager = new BookingManager(eventRepository, paymentGateway, notificationService);
    }


@Test
@DisplayName("M1-US1: Successful booking")
void shouldProcessSuccessfulBooking() {

    
    BookingRequest request =
    new BookingRequest("CUST-1", "EVT-101", 3, 2500);

    when(eventRepository.isSoldOut("EVT-101"))
    .thenReturn(false);

    when(paymentGateway.processPayment(7500))
    .thenReturn("TXN-123");
    
    BookingResult result = bookingManager.processBooking(request);

    
    assertNotNull(result);
    assertEquals(BookingStatus.SUCCESS, result.getStatus());
    assertEquals("TXN-123", result.getTransactionId());

    verify(eventRepository, times(1))
    .isSoldOut("EVT-101");

    verify(paymentGateway, times(1))
    .processPayment(7500);


        ArgumentCaptor<Booking> bookingCaptor =ArgumentCaptor.forClass(Booking.class);

        verify(eventRepository, times(1))
        .saveBooking(bookingCaptor.capture());

        Booking savedBooking = bookingCaptor.getValue();

        assertNotNull(savedBooking);
        assertNotNull(savedBooking.getBookingId());
        assertFalse(savedBooking.getBookingId().isBlank());

        assertEquals("CUST-1", savedBooking.getCustomerId());
        assertEquals("EVT-101", savedBooking.getEventId());
        assertEquals(3, savedBooking.getTicketQuantity());
        assertEquals(7500, savedBooking.getTotalAmountInCents());
        assertEquals("TXN-123", savedBooking.getTransactionId());

    verify(notificationService, times(1))
    .sendConfirmation("CUST-1", "EVT-101", "TXN-123");
}


    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("invalidBookingRequests")
    @DisplayName("M1-US2: Invalid request")
    void shouldRejectInvalidRequestsWithoutInvokingDependencies(String description, BookingRequest request) {

        assertThrows(IllegalArgumentException.class, () -> bookingManager.processBooking(request));

        verifyNoInteractions(eventRepository, notificationService, paymentGateway);
    }

    private static Stream<Arguments> invalidBookingRequests() {
        return Stream.of(
                Arguments.of("Null request object", null),
                Arguments.of("Null customer ID", new BookingRequest(null, "EVT-101", 2, 2500)),
                Arguments.of("Empty customer ID", new BookingRequest("", "EVT-101", 2, 2500)),
                Arguments.of("Blank customer ID", new BookingRequest("   ", "EVT-101", 2, 2500)),
                Arguments.of("Null event ID", new BookingRequest("CUST-1", null, 2, 2500)),
                Arguments.of("Empty event ID", new BookingRequest("CUST-1", "", 2, 2500)),
                Arguments.of("Blank event ID", new BookingRequest("CUST-1", "   ", 2, 2500)),
                Arguments.of("Ticket quantity zero (< 1)", new BookingRequest("CUST-1", "EVT-101", 0, 2500)),
                Arguments.of("Ticket quantity negative (< 1)", new BookingRequest("CUST-1", "EVT-101", -2, 2500)),
                Arguments.of("Ticket quantity exceeds max (> 5)", new BookingRequest("CUST-1", "EVT-101", 6, 2500)),
                Arguments.of("Price per ticket is zero (<= 0)", new BookingRequest("CUST-1", "EVT-101", 2, 0)),
                Arguments.of("Price per ticket is negative (<= 0)", new BookingRequest("CUST-1", "EVT-101", 2, -500))
        );
    }
}
