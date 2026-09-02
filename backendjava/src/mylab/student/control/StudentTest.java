package mylab.student.control;

import mylab.student.entity.Student;
import mylab.student.exception.InvalidGradeException;

/**
 * [실습1] Student 클래스 테스트.
 *
 * Sample Run:
 *   김민수 / 컴퓨터공학 / 3학년
 *   5학년으로 변경
 *   학년은 1~4 사이의 값이어야 합니다.
 */
public class StudentTest {

    public static void main(String[] args) {
        Student student = new Student("2021001", "김민수", "컴퓨터공학", 3);

        System.out.println(student.getName() + " / " + student.getMajor() + " / " + student.getGrade() + "학년");

        System.out.println("5학년으로 변경");
        try {
            student.setGrade(5);
        } catch (InvalidGradeException e) {
            System.out.println(e.getMessage());
        }
    }
}
