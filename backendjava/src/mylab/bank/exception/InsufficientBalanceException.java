package mylab.bank.exception;

/**
 * 잔액이 부족할 때 발생하는 예외.
 */
public class InsufficientBalanceException extends Exception {

    public InsufficientBalanceException(String message) {
        super(message);
    }
}
