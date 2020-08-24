# AutoCode

#### 项目介绍
根据数据库表生成model、dao、service、controller各模块java代码,dao暂只支持mybatis

#### 安装教程
mvn install

#### 使用说明

1. 引入依赖
```xml
<dependency>
    <groupId>com.xianyu.open</groupId>
    <artifactId>autocode</artifactId>
    <version>1.0.0-RELEASE</version>
    <scope>test</scope>
</dependency>
```
2. 在spring单元测试中添加执行代码，如下
```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class EurekaServerApplicationTests {
	@Autowired
	private DataSource dataSource;
	@Test
	public void contextLoads() {
		new AutoCode.AutoCodeBuilder()
				// 添加表
				.addTables("user_date")
				// 添加表
				.addTables("item")
				// 设置数据源
				.setDataSource(dataSource)
				// 设置根包名
				.setRootPackage("cn.bainan.eurekaserver")
				// 开始构建
				.build();
	}
}
```
3. 生成文件目录结构
```yaml
cn.bainan.eurekaserver.item:
    controller:
      ItemController
    service:
      ItemService
    dao:
      ItemDao
    model:
      Item
resource:
    mapper:
      item-sql.xml
```
