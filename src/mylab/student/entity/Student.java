package mylab.student.entity;

import mylab.student.exception.InvalidGradeException;

/**
 * 학생 정보를 표현하는 클래스.
 * 모든 필드는 private 으로 선언하고 getter/setter 로만 접근한다(캡슐화).
 */
public class Student {

    private String studentId; // 학번
    private String name;      // 이름
    private String major;     // 전공
    private int grade;        // 학년 (1~4)

    public Student() {
    }

    public Student(String studentId, String name, String major, int grade) {
        this.studentId = studentId;
        this.name = name;
        this.major = major;
        this.grade = grade;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public int getGrade() {
        return grade;
    }

    /**
     * 학년은 1~4 까지만 허용한다.
     * 범위를 벗어나면 InvalidGradeException 을 발생시킨다.
     */
    public void setGrade(int grade) throws InvalidGradeException {
        if (grade < 1 || grade > 4) {
            throw new InvalidGradeException("학년은 1~4 사이의 값이어야 합니다.");
        }
        this.grade = grade;
    }
}
