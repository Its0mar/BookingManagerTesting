package edu.course.booking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("invalidBookingRequests")
    @DisplayName("M1-US2: Invalid request")
    void shouldRejectInvalidRequestsWithoutInvokingDependencies(String description, BookingRequest request) {

        assertThrows(IllegalAccessException.class, () -> bookingManager.processBooking(request));

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
