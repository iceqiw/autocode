import com.xianyu.open.autocode.AutoCode;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.Before;
import org.junit.Test;

public class TestDB {

    BasicDataSource dataSource=new BasicDataSource();
    @Before
    public void init(){
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/test");
        dataSource.setUsername("zhangsan");
        dataSource.setPassword("zhangsan");

    }

    @Test
    public void gen(){
        new AutoCode.AutoCodeBuilder().addTables("user")
                .setDataSource(dataSource)
                .setRootPackage("com.test").build();
    }
}
