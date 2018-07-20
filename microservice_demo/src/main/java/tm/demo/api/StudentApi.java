package tm.demo.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tm.demo.GID;
import tm.demo.entity.Course;
import tm.demo.entity.Student;
import tm.demo.service.StudentService;

@RestController
@RequestMapping
public class StudentApi {
	@Autowired
	private StudentService studentService;

	@GetMapping("/students")
	public List<Student> list() {
		return studentService.list();
	}

	@PostMapping("/students")
	public Student register(@RequestBody Student s) {
		Long id = GID.gen("student");
		return studentService.save(id, s.getName(), s.getContact());
	}

	@PutMapping("/students/courses/{course_id}/books/{book_id}")
	public Student select(@PathVariable(value = "course_id") String course_id,
			@PathVariable(value = "book_id") String book_id, @CookieValue(value = "uid") String student_id) {
		studentService.select(Long.valueOf(student_id), Long.valueOf(course_id), Long.valueOf(book_id));
		return studentService.get(Long.valueOf(student_id));
	}

	@GetMapping("/students/courses/{course_id}/contact")
	public List<Student> contact(@PathVariable(value = "course_id") String course_id,
			@CookieValue(value = "uid") String student_id) {
		Student s = studentService.get(Long.valueOf(student_id));
		List<Student> list = new ArrayList<Student>();
		for (Course c : s.getCourses()) {
			if (!c.getRequired() && c.getId() == Long.valueOf(course_id)) {
				for (Student ss : c.getStudents()) {
					if (ss.getId() != Long.valueOf(student_id))
						list.add(ss);
				}
			}
		}
		return list;
	}
}
