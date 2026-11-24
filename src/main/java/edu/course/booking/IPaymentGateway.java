package edu.course.booking;

public interface IPaymentGateway {
    String processPayment() throws PaymentFailedException;
}
