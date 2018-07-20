package tm.demo.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tm.demo.GID;
import tm.demo.entity.Course;
import tm.demo.service.CourseService;

@RestController
@RequestMapping
public class CourseApi {
	@Autowired
	private CourseService courseService;

	@GetMapping("/courses")
	public List<Course> list() {
		return courseService.list();
	}

	@PostMapping("/courses")
	public Course register(@RequestBody Course c) {
		Long id = GID.gen("course");
		return courseService.save(id, c.getName(), c.getRequired(), c.getSeats());
	}
}
