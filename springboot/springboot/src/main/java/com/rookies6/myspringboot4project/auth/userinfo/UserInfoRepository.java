package com.rookies6.myspringboot4project.auth.userinfo;

import org.springframework.data.repository.ListCrudRepository;
import java.util.Optional;

public interface UserInfoRepository extends ListCrudRepository<UserInfo,Long> {
    //인증할 때 username은 값은 email 주소로 입력함
    Optional<UserInfo> findByEmail(String email);
}