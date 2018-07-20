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

@Service
public class BookService {
	@PersistenceContext
	private EntityManager em;

	@Autowired
	private CourseService courseService;

	@Transactional
	public Book save(Long id, String name, Integer count) {
		Book b = new Book(id, name, count);
		return em.merge(b);
	}

	@Transactional
	public Book get(Long id) {
		return em.find(Book.class, id);
	}

	@Transactional
	public void assign(Long bookId, Long courseId) {
		Book b = get(bookId);
		Course c = courseService.get(courseId);
		if (c.getBooks() == null)
			c.setBooks(new HashSet<Book>());

		b.setCourse(c); // 所属课程
		c.getBooks().add(b); // 课程的教材集增加
		em.merge(b);
	}

	@Transactional
	public List<Book> list() {
		TypedQuery<Book> typedQuery = em.createNamedQuery("Book.all", Book.class);
		return typedQuery.getResultList();
	}

	@Transactional
	public void deleteAll() {
		em.createQuery("delete from Book").executeUpdate();
	}

}
