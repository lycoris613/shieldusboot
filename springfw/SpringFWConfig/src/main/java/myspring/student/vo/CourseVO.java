package myspring.student.vo;

public class CourseVO {
	private Long id;
	private Integer code;
	private String name;
	private String instructor;

	public CourseVO() {

	}

	public CourseVO(Integer code, String name, String instructor) {
		this.code = code;
		this.name = name;
		this.instructor = instructor;
	}

	public CourseVO(Long id, Integer code, String name, String instructor) {
		this(code,name,instructor);
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

	public String getInstructor() {
		return instructor;
	}

	public void setInstructor(String instructor) {
		this.instructor = instructor;
	}

	@Override
	public String toString() {
		return "CourseVO [id=" + id + ", code=" + code + ", name=" + name + ", instructor=" + instructor + "]";
	}
		
}
