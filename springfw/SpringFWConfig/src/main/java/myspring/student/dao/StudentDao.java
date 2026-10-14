package myspring.student.dao;

import java.util.List;

import myspring.student.vo.CourseStatusVO;
import myspring.student.vo.CourseVO;
import myspring.student.vo.StudentVO;

public interface StudentDao {

	StudentVO selectStudentByCode(int code);
	
	List<StudentVO> selectStudentByName(String name);
	
	List<StudentVO> selectStudentByGradeOrDay(StudentVO student);

	List<StudentVO> selectStudentDept();

	List<StudentVO> selectStudentCourseStatus();

	int insertStudent(StudentVO studentVO);

	int insertCourseStatus(CourseStatusVO courseStatusVO);
	
	CourseVO selectCourseByCode(int code);
	
	List<CourseVO> selectCourse();
	
	int insertCourse(CourseVO courseVO);
	
}