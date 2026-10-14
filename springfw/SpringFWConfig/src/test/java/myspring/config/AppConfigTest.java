package myspring.config;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSession;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import myspring.student.dao.StudentDao;
import myspring.student.dao.mapper.StudentMapper;
import myspring.student.vo.CourseVO;
import myspring.student.vo.StudentVO;
import myspring.user.service.UserService;
import myspring.user.vo.UserVO;

@RunWith(SpringJUnit4ClassRunner.class)
//ApplicationContext will be loaded from the OrderServiceConfig class
@ContextConfiguration(classes=AppConfig.class, loader=AnnotationConfigContextLoader.class)

public class AppConfigTest {
	@Autowired
	DataSource dataSource;
	
	@Autowired
	SqlSession sqlSession;
	
	@Autowired
	UserService userService;
	
	@Autowired
	StudentMapper studentMapper;
	
	@Autowired
	StudentDao studentDao;
	
	@Test @Ignore
	public void studentDao() {
		List<StudentVO> stuList = studentDao.selectStudentDept();
		System.out.println(stuList);
	}
	
	@Test //@Ignore
	public void student() {
		List<StudentVO> stuList = studentMapper.selectStudentDept();
		System.out.println(stuList);
		
		StudentVO stu = studentMapper.selectStudentByCode(1002);
		System.out.println(stu);
		
		CourseVO cou = studentMapper.selectCourseByCode(1000);
		System.out.println(cou);
		
	}
	
	@Test @Ignore
	public void service() {
		System.out.println(userService.getUser("gildong"));
		for(UserVO user : userService.getUserList()) {
			System.out.println(user);
		}
	}
	
	@Test @Ignore
	public void ds() {
		System.out.println(dataSource);
		try {
			System.out.println(dataSource.getConnection());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		UserVO user = sqlSession.selectOne("userNS.selectUserById","gildong");
		System.out.println(user);
	}
}