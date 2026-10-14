package myspring.user.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import myspring.user.dao.UserDao;
import myspring.user.vo.UserVO;

@Service("userService")
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

	@Autowired
	UserDao userdao;
	
	public void setUserdao(UserDao userdao) {
		this.userdao = userdao;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void insertUser(UserVO user) {
		userdao.insert(user);
	}

	public List<UserVO> getUserList() {
		return userdao.readAll();
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
	public void deleteUser(String id) {
		userdao.delete(id);
		
	}

	@Override
	public UserVO getUser(String id) {
		return userdao.read(id);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateUser(UserVO user) {
		userdao.update(user);
	}
}
