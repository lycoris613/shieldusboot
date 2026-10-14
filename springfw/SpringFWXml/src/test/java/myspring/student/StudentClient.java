package myspring.student;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import myspring.student.dao.StudentDao;
import myspring.student.vo.CourseStatusVO;
import myspring.student.vo.CourseVO;
import myspring.student.vo.DeptVO;
import myspring.student.vo.StudentVO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:beans.xml")
public class StudentClient {
	
	@Autowired
	SqlSession session;
	
	@Autowired
	StudentDao studentDao;
	
	@Test @Ignore
	public void insertCourseStatus() {
		StudentVO stu = new StudentVO();
		stu.setCode(1003);
		CourseVO course = new CourseVO();
		course.setCode(2000);
		CourseStatusVO cs = new CourseStatusVO(stu, course, 85);
		int cnt = studentDao.insertCourseStatus(cs);
		System.out.println("등록된 건수 :" + cnt);
	}
	
	@Test @Ignore
	public void insertStudent() {
		StudentVO stu = new StudentVO(1003, "박소율",21, "2학년","야간",new DeptVO(40));
		int cnt = studentDao.insertStudent(stu);
		System.out.println("등록된 건수 :" + cnt);
	}
	
	
	@Test @Ignore
	public void selectStudentCourseStatusById() {
		List<StudentVO> list = studentDao.selectStudentCourseStatus();
		for (StudentVO studentVO : list) {
			System.out.println(studentVO);
		}
	}
	
	@Test //@Ignore
	public void selectStudentDeptById() {
		List<StudentVO> list = studentDao.selectStudentDept();
		for (StudentVO studentVO : list) {
			System.out.println(studentVO);
		}
	}
	
	@Test @Ignore
	public void studentById() {
		StudentVO student = studentDao.selectStudentByCode(1003);
		System.out.println(student);
	}

	@Test @Ignore
	public void insert() {
//		StudentVO stu = new StudentVO(1005, "백명숙2",21, "2학년","야간",new DeptVO(20));
//		StudentVO stu2 = new StudentVO(1006, "박소율2",22, "2학년","주간2",new DeptVO(40));
//		
//		studentDao.insertStudent(stu);
//		studentDao.insertStudent(stu2);
		
		CourseVO course = new CourseVO(3000, "스프링부트", "김부트");
		studentDao.insertCourse(course);
		
	}
	
	
	@Test @Ignore
	public void student2() {
		
		List<StudentVO> stuDeptList = studentDao.selectStudentDept();
		for (StudentVO studentVO : stuDeptList) {
			System.out.println(studentVO.stu_dept());
		}
				
		List<CourseVO> courseList = studentDao.selectCourse();
		for (CourseVO courseVO : courseList) {
			System.out.println(courseVO);
		}
		
		StudentVO student = studentDao.selectStudentByCode(1005);
		CourseVO course = studentDao.selectCourseByCode(3000);
		
//		CourseStatusVO status = new CourseStatusVO(student, course, 100);
//		studentDao.insertCourseStatus(status);
		
		List<StudentVO> statusList = studentDao.selectStudentCourseStatus();
		for (StudentVO studentVO : statusList) {
			System.out.println(studentVO);
		}
		
	}
	
	@Test
	public void dynamic() {
		List<StudentVO> stu = studentDao.selectStudentByName("홍");
		
		StudentVO student = new StudentVO();
		student.setGrade("2학년");
		//student.setDaynight("야간");
		List<StudentVO> stuList = studentDao.selectStudentByGradeOrDay(student);
		for (StudentVO studentVO : stuList) {
			System.out.println(studentVO);
		}
	}
	
	
	
}
