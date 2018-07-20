package tm.demo.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import tm.demo.entity.Course;

@Service
public class CourseService {
	@PersistenceContext
	private EntityManager em;

	@Transactional
	public Course save(Long id, String name, Boolean required, Integer seats) {
		Course c = new Course(id, name, required, seats);
		return em.merge(c);
	}

	@Transactional
	public Course get(Long id) {
		return em.find(Course.class, id);
	}

	@Transactional
	public List<Course> list() {
		TypedQuery<Course> typedQuery = em.createNamedQuery("Course.all", Course.class);
		return typedQuery.getResultList();

	}

	@Transactional
	public void deleteAll() {
		em.createQuery("delete from Course").executeUpdate();
	}

}
