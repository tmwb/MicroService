package tm.demo.service;

import java.util.HashSet;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tm.demo.entity.Book;
import tm.demo.entity.Course;
import tm.demo.entity.Student;

@Service
public class StudentService {
	@PersistenceContext
	private EntityManager em;

	@Autowired
	private CourseService courseService;
	@Autowired
	private BookService bookService;

	@Transactional
	public Student save(Long id, String name, String contact) {
		Student s = new Student(id, name, contact);
		return em.merge(s);
	}

	@Transactional
	public void select(Long studentId, Long courseId, Long bookId) {
		Student s = get(studentId);
		Course c = courseService.get(courseId);
		Book b = bookService.get(bookId);
		if (s.getCourses() == null)
			s.setCourses(new HashSet<Course>());
		if (s.getBooks() == null)
			s.setBooks(new HashSet<Book>());
		if (c.getStudents() == null)
			c.setStudents(new HashSet<Student>());
		if (b.getStudents() == null)
			b.setStudents(new HashSet<Student>());

		if (b.getCount() == b.getStudents().size()) // 教材被领完的判定
			return;
		if (!c.getRequired() && c.getSeats() == c.getStudents().size()) // 选修课满员判定
			return;
		s.getCourses().add(c);
		s.getBooks().add(b);
		c.getStudents().add(s);
		b.getStudents().add(s);
		em.merge(s);
	}

	@Transactional
	public Student get(Long id) {
		return em.find(Student.class, id);
	}

	@Transactional
	public List<Student> list() {
		TypedQuery<Student> typedQuery = em.createNamedQuery("Student.all", Student.class);
		return typedQuery.getResultList();
	}

	@Transactional
	public void deleteAll() {
		em.createQuery("delete from Student").executeUpdate();
	}

}
