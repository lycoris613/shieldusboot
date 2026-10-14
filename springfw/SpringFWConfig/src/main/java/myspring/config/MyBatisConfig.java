package myspring.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@MapperScan(
        basePackages= {"myspring.user.dao.mapper","myspring.student.dao.mapper"},
        sqlSessionFactoryRef="sqlSessionFactoryBean")
public class MyBatisConfig {
	 /**
     * myBatis의 {@link org.apache.ibatis.session.SqlSessionFactory}을 생성하는 팩토리빈을 등록한다.
     */
    @Bean
    public SqlSessionFactory sqlSessionFactoryBean(
            DataSource dataSource, ApplicationContext applicationContext) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        // 마이바티스가 사용한 DataSource를 등록
        factoryBean.setDataSource(dataSource);
        // 마이바티스 설정파일 위치 설정
        factoryBean.setConfigLocation(applicationContext.getResource("classpath:SqlMapConfig.xml"));
        // com.lge.apip.mgmt.ocpo.*.model 패키지 이하의 model 클래스 이름을 짧은 별칭으로 등록
        factoryBean.setTypeAliasesPackage("myspring.user.vo, myspring.student.vo");
        // META-INF/mybatis/mappers 패키지 이하의 모든 XML을 매퍼로 등록
        factoryBean.setMapperLocations(applicationContext.getResources("classpath:mapper/**/*Mapper.xml"));
        return factoryBean.getObject();
    }

    /**
     * MyBatis {@link org.apache.ibatis.session.SqlSession} 빈을 등록한다.
     *
     * SqlSessionTemplate은 SqlSession을 구현하고 코드에서 SqlSession를 대체하는 역할을 한다.
     * 쓰레드에 안전하게 작성되었기 때문에 여러 DAO나 매퍼에서 공유 할 수 있다.
     */
    @Bean
    public SqlSessionTemplate sqlSession(SqlSessionFactory sqlSessionFactory) throws Exception {
    	final SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory);
        return sqlSessionTemplate;
    }
    
	
}
