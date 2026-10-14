package myspring.student.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import myspring.student.dao.StudentDao;
import myspring.student.vo.CourseStatusVO;
import myspring.student.vo.StudentVO;

@RestController
public class RestfulStudentController {
	@Autowired
	private StudentDao studentDao;

	@RequestMapping(value = "/studentdept", method = RequestMethod.GET)
	public List<StudentVO> getStudentDeptByIdList() {
		List<StudentVO> stuList = studentDao.selectStudentDept();
		return stuList;
	}

	@RequestMapping(value = "/studentcourse", method = RequestMethod.GET)
	public List<StudentVO> getStudentCourseStatusByIdList() {
		List<StudentVO> stuList = studentDao.selectStudentCourseStatus();
		return stuList;
	}

	@RequestMapping(value = "/students/{code}", method = RequestMethod.GET)
	public StudentVO getStudent(@PathVariable int code) {
		StudentVO stu = studentDao.selectStudentByCode(code);
		return stu;
	}

	@RequestMapping(value = "/students", method = RequestMethod.POST, headers = { "Content-type=application/json" })
	public int insertStudent(@RequestBody StudentVO student) {
			return studentDao.insertStudent(student);
	}

	@PostMapping("/coursestatus")
	public int insertCourseStatus(@RequestBody CourseStatusVO courseStatusVO)  {
		System.out.println(">>>>courseStatusVO " + courseStatusVO);
		int cnt = studentDao.insertCourseStatus(courseStatusVO);
		return cnt;
	}
	
	/*
	@PostMapping("/coursestatus")
	public int insertCourseStatus(@RequestParam int student, @RequestParam int course, @RequestParam int score)  {
		StudentVO studentVO = studentDao.selectStudentByCode(student);
		CourseVO courseVO = studentDao.selectCourseByCode(course);
		CourseStatusVO courseStatusVO = new CourseStatusVO(studentVO, courseVO, score);
		
		int cnt = studentDao.insertCourseStatus(courseStatusVO);
		return cnt;
	}
	*/

}
