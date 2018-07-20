package tm.demo;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import tm.demo.entity.Book;
import tm.demo.entity.Course;
import tm.demo.entity.Student;
import tm.demo.service.BookService;
import tm.demo.service.CourseService;
import tm.demo.service.StudentService;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.JVM)
public class MicroserviceDemoApplicationTests {
	@Autowired
	private CourseService courseService;
	@Autowired
	private BookService bookService;
	@Autowired
	private StudentService studentService;
	
	@Test
	public void contextLoads() {
	}

	@Test
	public void test0() {
		courseService.deleteAll();
		bookService.deleteAll();
		studentService.deleteAll();
	}

	@Test
	public void test1() {
		Course c = courseService.save(GID.gen("course"), "计算机概论", true, 0); // 登记课程
		Assert.assertEquals(1, courseService.list().size()); // 系统中多了一门课程
		Assert.assertNotNull(c); // 这门课程的对象非空
		Assert.assertNotNull(c.getId()); // id非空，实体类必有唯一id
		Assert.assertEquals("计算机概论", c.getName()); // 课程名称是。。。
		Assert.assertTrue(c.getRequired()); // 是必修课
		Assert.assertEquals(Integer.valueOf(0), c.getSeats()); // 人数上限是0

		c = courseService.save(GID.gen("course"), "Basic语言", false, 3); // 登记课程
		Assert.assertEquals(2, courseService.list().size()); // 系统中多了一门课程
		Assert.assertNotNull(c); // 这门课程的对象非空
		Assert.assertNotNull(c.getId()); // id非空，实体类必有唯一id
		Assert.assertEquals("Basic语言", c.getName()); // 课程名称是。。。
		Assert.assertTrue(!c.getRequired()); // 非必修课
		Assert.assertEquals(Integer.valueOf(3), c.getSeats()); // 人数上限是3
	}

	@Test
	public void test2() {
		Book b = bookService.save(GID.gen("book"), "《计算机概论》", 3); // 登记教材
		Assert.assertEquals(1, bookService.list().size()); // 系统中多了一本教材
		Assert.assertNotNull(b); // 对象非空
		Assert.assertNotNull(b.getId()); // id非空
		Assert.assertEquals("《计算机概论》", b.getName()); // 名称
		Assert.assertEquals(Integer.valueOf(3), b.getCount());// 数量

		b = bookService.save(GID.gen("book"), "《计算机基础》", 10); // 登记教材
		Assert.assertEquals(2, bookService.list().size()); // 系统中多了一本教材
		Assert.assertNotNull(b); // 对象非空
		Assert.assertNotNull(b.getId()); // id非空
		Assert.assertEquals("《计算机基础》", b.getName()); // 名称
		Assert.assertEquals(Integer.valueOf(10), b.getCount());// 数量

		b = bookService.save(GID.gen("book"), "《Basic语言》", 1); // 登记教材
		Assert.assertEquals(3, bookService.list().size()); // 系统中多了一本教材
		Assert.assertNotNull(b); // 对象非空
		Assert.assertNotNull(b.getId()); // id非空
		Assert.assertEquals("《Basic语言》", b.getName()); // 名称
		Assert.assertEquals(Integer.valueOf(1), b.getCount());// 数量

		b = bookService.save(GID.gen("book"), "《Quick Basic》", 3); // 登记教材
		Assert.assertEquals(4, bookService.list().size()); // 系统中多了一本教材
		Assert.assertNotNull(b); // 对象非空
		Assert.assertNotNull(b.getId()); // id非空
		Assert.assertEquals("《Quick Basic》", b.getName()); // 名称
		Assert.assertEquals(Integer.valueOf(3), b.getCount());// 数量
	}

	@Test
	public void test3() {
		Course c = courseService.get(1L);
		int count = 0;
		bookService.assign(1L, 1L); // 把id=1L的教材指定给id=1L的课程
		c = courseService.get(1L);
		Book b = bookService.get(1L);
		Assert.assertEquals(count + 1, c.getBooks().size()); // 课程的教材数量为1
		Assert.assertTrue(c.getBooks().contains(b)); // 课程的教材集包含这本教材
		Assert.assertEquals(c, b.getCourse()); // 教材所属课程

		c = courseService.get(1L);
		count = c.getBooks().size();
		bookService.assign(2L, 1L); // 把id=2L的教材指定给id=1L的课程
		c = courseService.get(1L);
		b = bookService.get(2L);
		Assert.assertEquals(count + 1, c.getBooks().size()); // 课程的教材数量为1
		Assert.assertTrue(c.getBooks().contains(b)); // 课程的教材集包含这本教材
		Assert.assertEquals(c, b.getCourse()); // 教材所属课程

		c = courseService.get(2L);
		count = 0;
		bookService.assign(3L, 2L); // 把id=3L的教材指定给id=2L的课程
		c = courseService.get(2L);
		b = bookService.get(3L);
		Assert.assertEquals(count + 1, c.getBooks().size()); // 课程的教材数量为1
		Assert.assertTrue(c.getBooks().contains(b)); // 课程的教材集包含这本教材
		Assert.assertEquals(c, b.getCourse()); // 教材所属课程

		c = courseService.get(2L);
		count = c.getBooks().size();
		bookService.assign(4L, 2L); // 把id=4L的教材指定给id=2L的课程
		c = courseService.get(2L);
		b = bookService.get(4L);
		Assert.assertEquals(count + 1, c.getBooks().size()); // 课程的教材数量为1
		Assert.assertTrue(c.getBooks().contains(b)); // 课程的教材集包含这本教材
		Assert.assertEquals(c, b.getCourse()); // 教材所属课程
	}

	@Test
	public void test4() {
		Student s = studentService.save(GID.gen("student"), "赵毅", "13511111111"); // 学生注册
		Assert.assertEquals(1, studentService.list().size());
		Assert.assertNotNull(s);
		Assert.assertNotNull(s.getId());

		s = studentService.save(GID.gen("student"), "钱儿", "13522222222"); // 学生注册
		Assert.assertEquals(2, studentService.list().size());
		Assert.assertNotNull(s);
		Assert.assertNotNull(s.getId());

		s = studentService.save(GID.gen("student"), "孙山", "1353333333"); // 学生注册
		Assert.assertEquals(3, studentService.list().size());
		Assert.assertNotNull(s);
		Assert.assertNotNull(s.getId());

		s = studentService.save(GID.gen("student"), "李思", "135444444"); // 学生注册
		Assert.assertEquals(4, studentService.list().size());
		Assert.assertNotNull(s);
		Assert.assertNotNull(s.getId());
	}

	@Test
	public void test5() {
		studentService.select(1L, 1L, 1L); // 学生1选择课程1和教材1
		Student s = studentService.get(1L);
		Course c = courseService.get(1L);
		Book b = bookService.get(1L);
		Assert.assertTrue(s.getCourses().contains(c));
		Assert.assertTrue(s.getBooks().contains(b));
		Assert.assertTrue(c.getStudents().contains(s));
		Assert.assertTrue(b.getStudents().contains(s));

		studentService.select(2L, 1L, 1L); // 学生2选择课程1和教材1
		s = studentService.get(2L);
		c = courseService.get(1L);
		b = bookService.get(1L);
		Assert.assertTrue(s.getCourses().contains(c));
		Assert.assertTrue(s.getBooks().contains(b));
		Assert.assertTrue(c.getStudents().contains(s));
		Assert.assertTrue(b.getStudents().contains(s));

		studentService.select(3L, 1L, 1L); // 学生3选择课程1和教材1
		s = studentService.get(3L);
		c = courseService.get(1L);
		b = bookService.get(1L);
		Assert.assertTrue(s.getCourses().contains(c));
		Assert.assertTrue(s.getBooks().contains(b));
		Assert.assertTrue(c.getStudents().contains(s));
		Assert.assertTrue(b.getStudents().contains(s));

		studentService.select(4L, 1L, 1L); // 学生4选择课程1和教材1
		s = studentService.get(4L);
		c = courseService.get(1L);
		b = bookService.get(1L);
		Assert.assertTrue(!s.getCourses().contains(c));
		Assert.assertTrue(!s.getBooks().contains(b));
		Assert.assertTrue(!c.getStudents().contains(s));
		Assert.assertTrue(!b.getStudents().contains(s));

		studentService.select(4L, 1L, 2L); // 学生4选择课程1和教材2
		s = studentService.get(4L);
		c = courseService.get(1L);
		b = bookService.get(2L);
		Assert.assertTrue(s.getCourses().contains(c));
		Assert.assertTrue(s.getBooks().contains(b));
		Assert.assertTrue(c.getStudents().contains(s));
		Assert.assertTrue(b.getStudents().contains(s));

		c = courseService.get(1L);
		Assert.assertEquals(4, c.getStudents().size()); // 必修课学生4人
		b = bookService.get(1L);
		Assert.assertEquals(3, b.getStudents().size()); // 教材1学生3人，因为只有三本
		b = bookService.get(2L);
		Assert.assertEquals(1, b.getStudents().size()); // 教材2学生1人

	}

	@Test
	public void test6() {
		studentService.select(1L, 2L, 3L); // 学生1选择课程2和教材3
		Student s = studentService.get(1L);
		Course c = courseService.get(2L);
		Book b = bookService.get(3L);
		Assert.assertTrue(s.getCourses().contains(c));
		Assert.assertTrue(s.getBooks().contains(b));
		Assert.assertTrue(c.getStudents().contains(s));
		Assert.assertTrue(b.getStudents().contains(s));

		studentService.select(2L, 2L, 3L); // 学生2选择课程2和教材3
		s = studentService.get(2L);
		c = courseService.get(2L);
		b = bookService.get(3L);
		Assert.assertTrue(!s.getCourses().contains(c));
		Assert.assertTrue(!s.getBooks().contains(b));
		Assert.assertTrue(!c.getStudents().contains(s));
		Assert.assertTrue(!b.getStudents().contains(s));

		studentService.select(2L, 2L, 4L); // 学生2选择课程2和教材4
		s = studentService.get(2L);
		c = courseService.get(2L);
		b = bookService.get(4L);
		Assert.assertTrue(s.getCourses().contains(c));
		Assert.assertTrue(s.getBooks().contains(b));
		Assert.assertTrue(c.getStudents().contains(s));
		Assert.assertTrue(b.getStudents().contains(s));

		studentService.select(3L, 2L, 4L); // 学生3选择课程2和教材4
		s = studentService.get(3L);
		c = courseService.get(2L);
		b = bookService.get(4L);
		Assert.assertTrue(s.getCourses().contains(c));
		Assert.assertTrue(s.getBooks().contains(b));
		Assert.assertTrue(c.getStudents().contains(s));
		Assert.assertTrue(b.getStudents().contains(s));

		studentService.select(4L, 2L, 4L); // 学生3选择课程2和教材4
		s = studentService.get(4L);
		c = courseService.get(2L);
		b = bookService.get(4L);
		Assert.assertTrue(!s.getCourses().contains(c));
		Assert.assertTrue(!s.getBooks().contains(b));
		Assert.assertTrue(!c.getStudents().contains(s));
		Assert.assertTrue(!b.getStudents().contains(s));

		c = courseService.get(2L);
		Assert.assertEquals(3, c.getStudents().size()); // 课程学生3人
		b = bookService.get(3L);
		Assert.assertEquals(1, b.getStudents().size()); // 教材3只有1本
		b = bookService.get(4L);
		Assert.assertEquals(2, b.getStudents().size()); // 教材3只有1本
	}
}
