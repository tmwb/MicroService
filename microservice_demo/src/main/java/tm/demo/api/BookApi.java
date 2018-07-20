package tm.demo.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tm.demo.GID;
import tm.demo.entity.Book;
import tm.demo.service.BookService;

@RestController
@RequestMapping
public class BookApi {
	@Autowired
	private BookService bookService;

	@GetMapping("/books")
	public List<Book> list() {
		return bookService.list();
	}

	@PostMapping("/books")
	public Book register(@RequestBody Book b) {
		Long id = GID.gen("book");
		return bookService.save(id, b.getName(), b.getCount());
	}

	@PutMapping("/books/{book_id}/courses/{course_id}")
	public Book assign(@PathVariable(value = "book_id") String book_id,
			@PathVariable(value = "course_id") String course_id) {
		bookService.assign(Long.valueOf(book_id), Long.valueOf(course_id));
		return bookService.get(Long.valueOf(book_id));
	}
}
