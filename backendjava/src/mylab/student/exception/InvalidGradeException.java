package mylab.student.exception;

/**
 * 학년(grade) 값이 허용 범위(1~4)를 벗어났을 때 발생하는 예외.
 */
public class InvalidGradeException extends Exception {

    public InvalidGradeException(String message) {
        super(message);
    }
}
