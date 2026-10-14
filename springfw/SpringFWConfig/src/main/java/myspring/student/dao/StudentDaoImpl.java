package myspring.student.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import myspring.student.dao.mapper.StudentMapper;
import myspring.student.vo.CourseStatusVO;
import myspring.student.vo.CourseVO;
import myspring.student.vo.StudentVO;

@Repository("studentDao")
public class StudentDaoImpl implements StudentDao {
	@Autowired
	StudentMapper mapper;
	
	@Override
	public StudentVO selectStudentByCode(int code) {
		return mapper.selectStudentByCode(code);
	}
	
	@Override
	public List<StudentVO> selectStudentDept() {
		return mapper.selectStudentDept();
	}
	
	@Override
	public List<StudentVO> selectStudentCourseStatus() {
		return mapper.selectStudentCourseStatus();
	}
	
	@Override
	public int insertStudent(StudentVO studentVO) {
		return mapper.insertStudent(studentVO);
	}
	
	@Override
	public int insertCourseStatus(CourseStatusVO courseStatusVO) {
		return mapper.insertCourseStatus(courseStatusVO);
	}
	
	@Override
	public List<CourseVO> selectCourse() {
		// TODO Auto-generated method stub
		return mapper.selectCourse();
	}
	
	@Override
	public int insertCourse(CourseVO courseVO) {
		// TODO Auto-generated method stub
		return mapper.insertCourse(courseVO);
	}
	
	@Override
	public CourseVO selectCourseByCode(int code) {
		// TODO Auto-generated method stub
		return mapper.selectCourseByCode(code);
	}
	
	public List<StudentVO> selectStudentByName(String name) {
		return mapper.selectStudentByName(name);
	}
	
	@Override
	public List<StudentVO> selectStudentByGradeOrDay(StudentVO student) {
		// TODO Auto-generated method stub
		return mapper.selectStudentByGradeOrDay(student);
	}
}
