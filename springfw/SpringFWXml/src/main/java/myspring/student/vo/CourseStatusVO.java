package myspring.student.vo;

public class CourseStatusVO {
	private Long id;
	private StudentVO student;
	private CourseVO course;
	private Integer score;

	public CourseStatusVO() {

	}

	public CourseStatusVO(StudentVO student, CourseVO course, Integer score) {
		this.student = student;
		this.course = course;
		this.score = score;
	}

	public CourseStatusVO(Long id, StudentVO student, CourseVO course, Integer score) {
		this(student,course,score);
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public StudentVO getStudent() {
		return student;
	}

	public void setStudent(StudentVO student) {
		this.student = student;
	}

	public CourseVO getCourse() {
		return course;
	}

	public void setCourse(CourseVO course) {
		this.course = course;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	@Override
	public String toString() {
		return "CourseStatusVO [id=" + id + ", student=" + student + ", course=" + course + ", score=" + score + "]";
	}
		
}
