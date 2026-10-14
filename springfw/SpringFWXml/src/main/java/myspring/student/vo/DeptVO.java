package myspring.student.vo;

public class DeptVO {
	private Long id;
	private Integer code;
	private String name;
	
	public DeptVO() {
		
	}

	public DeptVO(Integer code) {
		this.code = code;
	}
	public DeptVO(Integer code, String name) {
		this.code = code;
		this.name = name;
	}

	public DeptVO(Long id, Integer code, String name) {
		this(code,name);
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

	@Override
	public String toString() {
		return "DeptVO [id=" + id + ", code=" + code + ", name=" + name + "]";
	}
	
}
