#测试驱动开发过程
###----基于一个简单的课程管理服务

###相关知识点：
	1. 业务描述及分析
	2. SpringBoot Project创建
	3. Maven的POM文件配置
	4. Junit测试过程及断言的使用
	5. 测试驱动开发的逆向思维训练
	6. 领域建模及领域模型关系
	7. JPA领域模型注解的用法及配置文件
	8. JPA Query API
	9. Spring IoC注解的用法：@Service和@Autowired
	10.Java重构技巧
	11.接口暴露和Spring Restful相关注解的用法
	12.Spring RestController实体序列化与反序列化定制

##1. 讲故事
	
做一个简单的课程管理服务，使得:

* 教务人员可以管理课程、教材；学生可以选课、选教材。
* 每门课程都有名称，分为必修课和选修课两种，选修课有人数限制，满员的选修课就不能再选。
* 每门课程提供一种或以上的教材供学生选择。
* 教材都有名称，并且有数量限制，没了就不能选该教材，
* 如果选修课程的所有教材都没了，那这门选修课也不能选择，必修课没有这个限制。
* 学生必须登记所有必修课，选修课至少选择一门。
* 教务人员可以多方位的查看数据：学生表、课程登记表、教材领取表。
* 学生可以通过课程登记表来选择课程及教材。
* 学生还可以查看选修课同学的通讯录。

##2. 找出参与者

* 教务人员manager
* 学生student
	
从业务描述中可以看出manager的变化对系统没有任何影响，其只需要一个id，用来记录课程和教材的创建人即可。
	
##3. 场景描述
就是解析上述故事，让故事逻辑化，主要是理顺表达顺序和表达方式。
先讲表达方式，主要是每一句话要说清楚这么几件事：

* 谁who
* 在什么时间when
* 在什么地方where
* 做什么事what
* 产生什么结果result

表达顺序，自然形成的逻辑顺序，有前置条件事情必须等前置任务做完才能做。
比如：教务人员在系统中可以为课程指定一本或多本教材。很显然，在指定教材之前，必须先把教材添加到系统中。

我们对上述故事进行分析后得出。

####需求用例：

	a. 教务人员在系统中登记课程course；课程都有名称，并且分为必选课和选修课两种类型；
		选修课有人数上限，而必修课没有。
	b. 教务人员在系统中为课程指定教材book，可以指定一本或多本教材；
		教材都有名称和数量上限。
	c. 学生首先要在在系统中注册，注册时提供姓名和联系方式。
	d. 学生根据课程登记表的情况选择课程及相应教材；其中：
		. 必修课必选（假定教材无限）
		. 当满员或没有教材时不能选择该选修课。
	e. 学生可以查看已选选修课同学的联系方式，没选的不能看。
	f. 教务人员可以查看学生表、课程登记表、教材领取表。

这样，逻辑表述结束之后，我们可以发现，一般情况下：

* 名词有可能是对象或者对象属性：
	* 教务人员、学生，课程、教材；这些是对象；
	* 名称、类型、座位数等；这些是属性。
* 动词是方法，登记、指定、注册、选择等。
* 状语、定语是限定，在系统中、在课程登记中等；
* 还有一些对象属性也会有限定作用，比如满员的课程学生就不能选修，教材被领完就不能再领等等。
	当然还有更复杂一点儿的，比如所有教材被领完的选修课，学生就不能再选修该课程了。
* 量词是关联关系，比如每门课程可以指定一本或多本教材，而每本教材必然属于某一门课程，这很明显是一对多的关系
	其中还有一些隐含的关联关系，比如学生必须选择所有必选课，而每门必选课肯定有多名学生，这明显是多对对的关系

##4. 创建工程
	
* Eclipse, 在Eclipse Marketplace中安装一下Spring Tools,以前叫Spring Tool Suite

我们打开eclipse

* 创建一个Spring Boot工程，为什么要选Spring Boot工程，就是因为它能搞的都搞了，省的咱们自己再去配置各种参数。
* Spring Boot工程已经配置好了JUnit，咱们在后面直接用。
* 创建是要选择『Spring Starter Project』
* 工程配置中注意一下Java的版本跟你本机版本一致即可，package要按项目要求定义好
* 在项目依赖列表中，选择web、jpa、mysql三项

然后就创建吧。

##5. 开始写代码
在src/test/java目录下，新建一个Junit Test Case文件，叫DemoTest吧。<br/>
然后我们把上面的场景描述翻译成代码......<br/>
<br/>
首先我们先假想一些测试数据：

	两门课程：一门必修课，一门选修课，人数上限是3
	四本教材：前两本是必修课教材，数量分别是3，10；后两本是选修课教材，数量分别是1，5
	四名学生

从需求用例a开始写，

test方法上有个@Test注解，你只要知道它用来表明下面的方法是个测试单元就行了 <br/>
我们把test方法先改为test1，然后在其中写代码。<br/>

**注意：有报错的地方，等故事讲完再改，eclipse有非常完善的修改建议。**
	
####a. 教务人员在系统中登记课程course；课程都有名称，并且分为必选课和选修课两种类型； 选修课有人数上限，而必修课没有。

登记课程，就是实例化一个课程类，参数分别是名称、类型、人数上限，有必要记录一下创建人<br/>
课程中对业务有影响的属性时类型和人数上限，此处因为我们只有两类，所有用逻辑类型表达。<br/>
先登记两门课，其中一门必修课，另外一门选修课，人数上限是3。<br/>

使用Junit的断言，别用spring的
	
	Assert.assertNotNull()
	Assert.assertTrue()
	Assert.assertEquals()

使用断言时要注意： **新建实体类后的断言一定要检查ID非空和关键属性的正确性**
	
创建实体类时要注意：

* 所有实体类都要有长整型主键id,主键id不得在构造函数中更新或采用数据库相关功能生成。<br/>
	此例中Id用计数法生成，即我们定义三个静态变量，courseCount\bookCount\studentCount, 对应的id通过累加获得
* 所有实体类必须有一个空参数的构造函数，当然如果没有其他构造函数的情况下，空参构造函数可以忽略
* 一般情况下实体类要记录创建时间，所以会增加两个属性creatorId和created，但要注意的是：<br/>
	在构造函数中，这个创建人和创建时间只有在创建时间为空时才能更新。
* 对于一对多或者多对多关系要用到Collection时，请使用Set，不要用List。
* getter和setter都用eclipse source搞定
* 每个实体类都要继承序列化接口Serializable
* 每个实体类都要重载hashCode和equals方法

需求用例b，我们新建一个方法test2，注意写上@Test注解<br/>
	
####b. 教务人员在系统中为课程指定教材book，可以指定一本或多本教材；教材都有名称和数量上限。
教务人员为课程指定教材分两步：先得有教材，然后才能指定。<br/>
所以先要实例化教材，然后才能指定教材。<br/>
此例中，我们实例化4本教材，教材属性有名称、数量和创建人，前两本分配给必修课、后两本分配给选修课<br/>
他们的数量分别是：3，10，1，3<br/>
实例化后用断言判定ID非空和数量是否正确。<br/>
<br/>
然后运行这两个测试用例，保证测试通过<br/>
接下来指定教材。。。。<br/>
调用函数assignBook2Course, 这个函数的参数是教材和课程。<br/>
我们知道，这个业务执行的结果，也就是对系统的影响是：课程指定教材列表中包含这本教材，教材所属课程是这个课程。根据这句话写断言。<br/>
这时候我们会看到，在这个函数中，只有刚刚实例化的教材，没有课程，因为我们上个测试用例中实例化了两门课程，这里就不能再实例化了，要想利用前一个测试单元的变量，我们只能将要用到的变量存在一个地方，可选择的存储方式有：<br/>

	内存、缓存、数据库

因为缓存、数据库涉及到中间件，太麻烦，我们后面再说，先放到内存里。<br/>
我们在这个测试类的头部新建三个静态的Map变量,用静态变量的原因是每个测试单元的运行都是重新实例化测试类运行的。<br/>
分别存放课程、教材和学生，并把在测试函数中实例化的对象存在对应的Map中。<br/>
然后我们将两门课程从map中取出来，断言其非空，大家可以试试非静态Map和静态Map运行上的区别。<br/>
然后我们完善assignBook2Course函数。<br/>
运行测试，使得测试通过。<br/>
<br/>
这里要注意的是：<br/>
**随着测试方法的增多，这些方法执行的顺序是随机的，如果有顺序要求，请在测试类前加上注解，强制其按函数声明顺序执行。** 

	@FixMethodOrder(MethodSorters.JVM) 
	
接下来是需求用例c，新建一个方法test3<br/>
####c. 学生首先要在在系统中注册，注册时提供姓名和联系方式。
学生注册同样也是实例化学生类而已，注意id生成和id非空检查即可,记得把实例化的对象保存到Map<br/>
<br/>

接下来是需求用例d，新建一个方法test4	
####d. 学生根据课程登记表的情况选择课程及相应教材；其中：

* 必修课必选（假定教材无限）
* 当满员或没有教材时不能选择该选修课。

要选课了，我们得把实例化好的课程、教材都取出来，供学生选择。<br/>
我们先让学生选必修课，取出必修课、两本教材和四名学生，注意断言非空。<br/>
然后写业务方法selectCourse，其参数是：课程、教材和学生<br/>
先让这四位学生都选必修课，教材都用第一本。<br/>
前三个学生做出选择后，其断言包括：<br/>

* 学生选课列表中包含该课程
* 学生选书列表中包含该教材
* 选择该课程的学生列表中包含该学生
* 选择该教材的学生列表中包含该学生

而第四个学生做出同样的选择后，其断言应该是上面四个断言的非，原因很简单因为第一本书只有三本被前三位领走了，第四位学生只能选择第二本教材。所以再让第四位学生选一次，课程不变，教材用第二本，断言跟前三位学生的一样。<br/>

开始执行测试。根据报错，完善selectCourse函数，直到绿条出现。<br/>
接下来让这四位同学选择选修课, 这里课程人数上限为3，第一本教材只有1本，第二本教材有3本<br/>
第一位学生选了第一本教材后，其断言跟上面相同<br/>
第二位学生选了第一本教材后，其断言跟上面相反，因为教材没了。<br/>
第二位学生选了第二本教材后，其断言跟上面相同<br/>
第三位学生选了第二本教材后，其断言跟上面相同<br/>
第四位学生选了第二本教材后，其断言跟上面相反，因为满员了。<br/>
	
执行测试，直到绿了。<br/>
	
##6. 此时，我们的业务功能开发已经完成了。 接下来要进行一些简单的重构。

我们首先找出代码中跟业务无关的事物，把它摘出来，变成接口调用的形式。<br/>
最容易看出来的有两个：id生成机制和数据存储<br/>
	
我们写一个id生成接口：Long gen(String type); 类型分为：course\book\student<br/>
相应的修改每个setId的地方<br/>
运行测试。<br/>
然后在新建一个GID类，将此方法和三个静态变量一并移至GID类，并改为静态公开方法。<br/>
相应的修改每个setId的地方的gen调用方式。<br/>
运行测试<br/>

**将来在微服务或云计算架构下，所有实体类id的生成都是全局式的接口调用，不再采用本地生成或数据库自增量的方式。**
	
我们写一个类叫DB，然后将三个静态Map移过去,并改为私有静态变量。<br/>
此时代码报错，可以看出，报错的地方涉及三类操作：保存、读取和列表<br/>
那么我们为DB类建立三个方法：save、get和list<br/>
先写Course的三个方法:<br/>

	public static Course save(Course course)
	public static Course get(Long id)
	public static List<Course> list()

对于Book、Student也是同样的三个方法，我们没办法在同一个类中实现，目前只能笨一点写成三个类。<br/>
然后在测试类中，直接使用这三个类的静态方法，修改相应的报错位置<br/>
运行测试。<br/>
	
接着我们做一些函数抽取，就是找出执行内容一样的多行代码，抽取到函数中。<br/>
比如实体的实例化和保存代码：<br/>

	Course course1 = new Course("计算机概论", true, 0, managerId);
	course1.setId(GID.gen("course"));
	CourseDB.save(course1);

我们把它抽取出来，放到函数saveCourse中<br/>

	private Course saveCourse(String name, Boolean required, Integer seats, Long creatorId)

教材、学生的同样处理<br/>
运行测试<br/>
	
根据单一职责原则，测试类是用来测试的，不能做其他的事儿，所以类中这些函数都需要把它们放到合适的类中去。<br/>
我们看看目前有的函数：<br/>
	
	saveCourse、assignBook2Course、selectCourse这些都是课程相关的业务
	saveBook是教材相关的业务
	saveStudent是学生相关的业务

那么我们建立三个服务类，把这些方法移过去。CourseService\BookService\StudentService<br/>
把方法移过去后都改为public<br/>
在测试类中，实例化这三个服务类<br/>
修改测试类中的接口调用方式，然后执行测试<br/>
	
接下来，我们继续观察会发现，在测试类中大多接口调用都是调用服务类来完成对实体类的操作，进而完成业务。<br/>
那么其中出现CourseDB、BookDB、StudentDB这种直接访问数据的接口就有写丑陋了。<br/>
正确的做法事全部放入服务类中去，让测试类仅仅调用服务类接口即可。<br/>
那么现在有两种做法：

* 第一种在服务类中新写三个方法，并调用DB中的三个对应的方法获得其结果
* 第二种是直接把DB类中的三个方法移到服务类中去，我倾向于第二种，第一种有脱了裤子放屁的赶脚。

修改时注意两点：

* 第一，把Map和方法的静态声明都去掉；
* 第二，save方法和saveCourse方法合并一下

这时，三个DB类中只剩下了静态Map声明，我们再建一个DB类，把三个静态声明移过去。<br/>
然后修改三个service类中的DB调用。<br/>
执行测试。<br/>
 
接着再把服务类中的方法名改改。<br/>

	saveCourse -> save
	assignBook2Course -> assignBook
	selectCourse -> select
	saveBook -> save
	saveStudent -> save

就是把方法名中的实体名词去掉。<br/>
<br/>
在src/main/java下的包里，建tm.demo.entity包，把三个实体类移过去<br/>
再建tm.demo.service包，把三个服务类移过去<br/>
再把DB、GID移到tm.demo下<br/>
 	
然后，用Spring注解，把三个服务类注解为@Service，并在测试类中用@Autowired注解自动装载服务类，<br/>
去掉等号及后面的实例化代码。先不要执行测试，还需要把：<br/>

	@RunWith(SpringRunner.class)
	@SpringBootTest

这两个注解放在测试类头部，使得容器自动扫描类，自动装载服务类。<br/>
然后把pom.xml中<br/>

	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-data-jpa</artifactId>
	</dependency>

注释起来。 然后运行测试。这时候的测试过程已经是带容器运行了。从日志中可以看到<br/>
<br/>
至此，重构完成。我们得到了层次清晰、接口一致的业务服务设计。
 
##7. 搞定JPA，即数据持久化和查询

先把刚刚注释起来的pom.xml中的代码打开。<br/>
然后搞配置文件，在src/main/resources下面有个application.properties的空文件，<br/>
先改个名字，改为application.yml,这是springboot支持的另外一种配置格式，缩进形式简明易懂<br/>
把下面这段代码复制进去，这是配置mysql数据连接用的<br/>
其中：demo是数据库名，ip地址、端口按你的改，其他没啥可改的。<br/>

	spring:
	  datasource:
	    url: jdbc:mysql://localhost:3306/demo?characterEncoding=UTF-8
	    username: root
	    password: 12345678
	    driver-class-name: com.mysql.jdbc.Driver
	  jpa:
	    database: MYSQL
	    show-sql: true
	    hibernate:
	      ddl-auto: update
	      naming-strategy: org.hibernate.cfg.ImprovedNamingStrategy
	    properties:
	      hibernate:
	        dialect: org.hibernate.dialect.MySQL5Dialect
	  jackson:
	    date-format: yyyy-MM-dd HH:mm:ss
	
	logging.level.org.springframework.web: DEBUG
	logging.level.org.hibernate: ERROR  


还有在tm.demo包新建一个JpaConfiguration类<br/>
好多注解！只要关注两个：<br/>

* @EnableJpaRepositories 这是激活实体类扫描的命令，参数是包的根路径
* @EntityScan 这是实体类扫描的包声明，这个要按实际的来
	
然后登录mysql用下面的命令建个数据库。<br/>

	create database demo default charset="utf8mb4";

不用管建表的事，表在后面会自动建。<br/>
<br/>
然后我们开始进行实体类注解过程：<br/>
按顺序说：<br/>
    
打开Book类，在头部写：<br/>

	@Entity //声明这是个实体
	@Table(name = "books")  //数据表名是books

在属性声明上面写：
	
	@Id //id注解，写在id声明语句上面
	@Column //非关联类属性注解，所有非关联类属性的属性都用它，包括Date类型
	
	//用于多对一方向的关联属性，比如Book类中的course属性，要这样写：
	@ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
	@JoinColumn(name = "course_id")
	
其中@JoinColumn指明了在books表中，用course_id字段关联Course实体<br/>
cascade级联方式不用管怎么回事就这么用<br/>
fetch读取方式，这里的用了EAGER还有一种叫LAZY，就是你用不用懒加载，这里不用。<br/>
使用懒加载可以提高实体数据读取效率。<br/>
这个注解用于多对一这一方，对应方向上的Course实体中必然是个Set，要用OneToMany<br/>
	
	//多对多注解，用在Book和学生的关联上，这样写
	@ManyToMany(mappedBy = "books", fetch = FetchType.EAGER)

这里的mappedBy指明在对应方向上的属性是books，在Student实体类中可以见到。<br/>
这里是非主导方的写法，主导方是Student<br/>
	
打开Course类，在头部写：

	@Entity
	@Table(name = "courses")
	
	@Id @Column照上面说的用
	
	//用于一对多方向的关联属性
	@OneToMany(mappedBy = "course", fetch = FetchType.EAGER)
	private Set<Book> books;

mappedBy明确指出在另外一端的属性是course<br/>
	
	@ManyToMany(mappedBy = "courses", fetch = FetchType.EAGER)
	private Set<Student> students;

同上，这里也是非主导方，主导方是Student<br/>

打开Student类，在头部写：<br/>

	@Entity
	@Table(name = "students")

	@Id @Column照上面说的用
	
	//这里是多对多的主导方了
	@ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
	@JoinTable(name = "student_courses", joinColumns = { @JoinColumn(name = "student_id") }, inverseJoinColumns = {
		@JoinColumn(name = "course_id") })
	private Set<Course> courses;

我们知道，多对多关联必定要建立一个关联表，或者叫中间表，表中只有两个关联实体的id，以表明关系<br/>
在JPA中，多对多关联表是隐含的，使用这个@JoinTable注解，写明<br/>

* name: 关联表表名
* joinColumns：两个实体的id要用的字段名。 主导方id写在@JoinColumn中，非主导方写在inverseJoinColumns中。
	
books属性也一样处理<br/>

	@ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
	@JoinTable(name = "student_books", joinColumns = { @JoinColumn(name = "student_id") }, inverseJoinColumns = {
		@JoinColumn(name = "book_id") })
	private Set<Book> books;
	
运行测试，有可能会报java.lang.ClassNotFoundException: javax.xml.bind.JAXBException这个异常，这是因为java版本不一致造成的，
在maven中加个jaxb依赖就行了。<br/>
	
接下来，要把DB类删掉，然后修改Service类<br/>
在每个Service类加声明，这是JPA上下文环境变量。<br/>

	@PersistenceContext
	private EntityManager em;
	
把原来的保存动作：<br/>
	
	DB.bookMap.put(book.getId(), book);
	改为：
	em.merge(book);
	
	DB.bookMap.get(id);
	改为：
	em.find(Book.class,id);
	
list函数稍微复杂一点儿：<br/>
首先在Book实体类头部加这么一句：<br/>

	@NamedQuery(name="Book.all",query="select b from Book b")

这是个命名查询声明<br/>
然后把list函数中的内容替换成：<br/>

	TypedQuery<Book> typedQuery = em.createNamedQuery("Book.all", Book.class);
	return typedQuery.getResultList();
	
然后在三个函数的上面写上@Transactional，声明这个方法要事务中执行。<br/>
其余两个service照上面的改，只是类名不同而已。<br/>
	
要注意的是在CourseService中，有两个独特的方法，select和assignBook<br/>
它们都修改了Course\Book\Student的实体中的关联属性，<br/>
在select函数开始加上：<br/>

	course = em.find(Course.class, course.getId());
	book = em.find(Book.class, book.getId());
	student = em.find(Student.class, student.getId());

这是为了保证参数中的三个实体都是联机状态。<br/>
	
在select方法的最后，加上：<br/>

	em.merge(student);

在assignBook的最后加上：<br/>

	em.merge(book);
	
修改测试代码，在每一步业务调用之后，都用重新使用get方法取出相关实体对象，用此实体对象进行测试。<br/>

执行测试，修改报错，直到绿条出现。<br/><br/>

至此，服务已与数据库建立的关联，你可以在demo数据库查到测试过程添加的数据了。
	
##8. 接下来，我们要暴露http接口给前端

这要用到Spring Restful的相关注解了。并且，不再使用测试驱动对http接口进行测试，只能用postman这样的工具。<br/>
http api要按照《天码WebAPI设计规范》来设计。<br/>
	
首先在src/main/java下，建立tm.demo.api包<br/>
在包中新建CourseApi\BookApi\StudentApi三个类<br/>
	
分别在每个类头部加上：<br/>
	@RestController
	@RequestMapping
	
	@RestController
	@RequestMapping("/books")
	
	@RestController
	@RequestMapping("/students")
	
* @RestController //声明这是个rest服务
* @RequestMapping //声明这个rest服务uri路径
	
然后开始写api方法，对照测试类中service接口的使用来写：<br/>

###CourseApi:

	登记课程 POST /courses
	课程登记表 GET /courses
	为课程指定教材 PUT /courses/{course_id}/books/{book_id}
	学生选课及教材	PUT /courses/{course_id}/books/{book_id}/selection
						cookie uid={student_id}
###BookApi:

	登记教材 POST /books
	教材领取表 GET /books
	
###StudentApi:
	
	学生注册 POST /students
	学生表 GET /students
	选修课通讯录 GET /students/courses/{course_id}/contact
			  		cookie uid={student_id}
			  
在DemoApplication类的头部加上注解 @ComponentScan<br/>
在application.yml中加上：指明服务端口、编码和api前缀<br/>
	
	server:
		port: 10000
		tomcat:
			uri-encoding: UTF-8  
		servlet:
		context-path: /api/v1
  
然后运行DemoApplication, 在日志中可以看到URL映射信息，你的服务就正常启动了。<br/>

下面我们用先用curl简单测试一下<br/>

	curl http://localhost:10000/api/v1/students
	curl http://localhost:10000/api/v1/students
	curl http://localhost:10000/api/v1/students

服务报错，原因是api中spring在序列化成json时形成了死循环<br/>
因为Student中有Course，而Course中有Student，Book也类似<br/>
有人提出的解决方案是在实体类的多对一、多对多关联属性上加注解： @JsonBackReference， 禁止spring对其进行序列化<br/>
多对一关联属性上不必写。这种方案可用，但是不够灵活。<br/>
	
还有一种自定义实体序列化和反序列化的方案如下：<br/>
在实体类头部加注解：<br/>

	@JsonSerialize(using = Course.CourseSerializer.class)
	@JsonDeserialize(builder = Course.CourseBuilder.class)

然后在实体类中实现序列化工具类和反序列化工具类
	
	public static class CourseBuilder {
		private String name;
		private Boolean required;
		private Integer seats;
	
		public CourseBuilder(@JsonProperty("name") String name, @JsonProperty("required") Boolean required,
				@JsonProperty("seats") Integer seats) {
			this.name = name;
			this.required = required;
			this.seats = seats;
		}
	
		public Course build() {
			return new Course(this);
		}
	}
	
	private Course(CourseBuilder builder) {
		this.name = builder.name;
		this.required = builder.required;
		this.seats = builder.seats;
	}
	
	public static class CourseSerializer extends JsonSerializer<Course> {
		private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
		@Override
		public void serialize(Course value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
			gen.writeStartObject();
			gen.writeNumberField("id", value.getId());
			gen.writeStringField("name", value.getName());
			gen.writeNumberField("seats", value.getSeats());
			gen.writeNumberField("creatorId", value.getCreatorId());
			gen.writeStringField("created", df.format(value.getCreated()));
			if (value.getStudents() != null)
				gen.writeNumberField("students_count", value.getStudents().size());
			if (value.getBooks() != null)
				gen.writeNumberField("books_count", value.getBooks().size());
			gen.writeEndObject();
		}
	}	
	
其他两个实体类的做法类似，改掉实体类名即可。<br/>
	
启动服务。 <br/>
	
##9. 打开postman

创建下列测试命令：

**注意：以下命令的header中都有Content-Type=application/json**
	
	学生表 GET http://localhost:10000/api/v1/students
	教材表 GET http://localhost:10000/api/v1/books
	课程表 GET http://localhost:10000/api/v1/courses
	登记课程 POST http://localhost:10000/api/v1/courses
		body: {
				"name":"数据结构",
				"required": true,
				"seats": 3
				}
	登记教材 POST http://localhost:10000/api/v1/books
		body: {
			"name":"《数据结构》",
			"count": 3
		}
	指定教材 PUT http://localhost:10000/api/v1/courses/1/books/1
	学生注册 POST http://localhost:10000/api/v1/students
		body: {
				"name":"马云",
				"contact": 3
			}
	选课 PUT http://localhost:10000/api/v1/courses/1/books/1/selection
			cookie uid=1
	选修课通讯录 GET http://localhost:10000/api/v1/students/courses/2/contact
				cookie uid=1
	
	
##10. 完成。

    
    
 