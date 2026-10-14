package com.rookies6.myspringboot4project.auth.userinfo;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserInfoUserDetails implements UserDetails {

    private String email;
    private String password;
    private List<GrantedAuthority> authorities;
    private UserInfo userInfo;

    public UserInfoUserDetails(UserInfo userInfo) {
        this.userInfo = userInfo;
        this.email=userInfo.getEmail();
        this.password=userInfo.getPassword();
        //roles : ROLE_ADMIN,ROLE_USER
        this.authorities= Arrays.stream(userInfo.getRoles().split(","))
                .map(roleName -> new SimpleGrantedAuthority(roleName))
                //.map(SimpleGrantedAuthority::new)
                //Stream<SimpleGrantedAuthority> => List<SimpleGrantedAuthority>
                .collect(Collectors.toList());
    }

    /*
        ROLE_ADMIN,ROLE_USER 문자열  콤마를 기준으로 Split 하면 Array가 생성됨
        Array로 Stream 객체를 생성함
        Stream<String> => Stream<SimpleGrantedAuthority> => List<GrantedAuthority>
     */

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
    
    /*
        getUsername과 getPassword 메서드는 
        AuthenticationManager가 인증처리를 할때 호출된다.
     */
    @Override
    public String getPassword() {
        return password;
    }
    
    @Override
    public String getUsername() {
        return email;
    }
    
    public UserInfo getUserInfo() {
        return userInfo;
    }    

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}