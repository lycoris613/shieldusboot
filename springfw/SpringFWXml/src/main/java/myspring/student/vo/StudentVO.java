package myspring.student.vo;

import java.util.List;

public class StudentVO {
	private Long id;
	private Integer code;
	private String name;
	private Integer age;
	private String grade;
	private String daynight;

	private DeptVO dept;

	private List<CourseStatusVO> courseStatus;

	public StudentVO() {

	}
	
	public StudentVO(Integer code, String name, Integer age, String grade, String daynight, DeptVO dept) {
		super();
		this.code = code;
		this.name = name;
		this.age = age;
		this.grade = grade;
		this.daynight = daynight;
		this.dept = dept;
	}

	public StudentVO(Long id, Integer code, String name, Integer age, String grade, String daynight, DeptVO dept) {
		this(code,name,age,grade,daynight,dept);
		this.id = id;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getDaynight() {
		return daynight;
	}

	public void setDaynight(String daynight) {
		this.daynight = daynight;
	}

	public DeptVO getDept() {
		return dept;
	}

	public void setDept(DeptVO dept) {
		this.dept = dept;
	}

	public List<CourseStatusVO> getCourseStatus() {
		return courseStatus;
	}

	public void setCourseStatus(List<CourseStatusVO> courseStatus) {
		this.courseStatus = courseStatus;
	}

	public String stu_dept() {
		return "StudentVO [id=" + id + ", name=" + name + ", age=" + age + ", grade=" + grade + ", daynight=" + daynight
				+ ", dept=" + dept + "]";
	}


	@Override
	public String toString() {
		return "StudentVO [id=" + id + ", code=" + code + ", name=" + name + ", age=" + age + ", grade=" + grade
				+ ", daynight=" + daynight + ", dept=" + dept + ", courseStatus=" + courseStatus + "]";
	}
	

}