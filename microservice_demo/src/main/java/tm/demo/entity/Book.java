package tm.demo.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Entity
@Table(name = "books")
@NamedQuery(name = "Book.all", query = "select b from Book b")
@JsonDeserialize(builder = Book.BookBuilder.class)
public class Book implements Serializable {
	private static final long serialVersionUID = -3321085270563746409L;
	@Id
	private Long id;
	@Column
	private String name;
	@Column
	private Integer count;
	@Column
	private Date created;
	@ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
	@JoinColumn(name = "course_id")
	private Course course;
	@JsonBackReference
	@ManyToMany(mappedBy = "books", fetch = FetchType.EAGER)
	private Set<Student> students;

	public Book() {
	}

	public Book(Long id, String name, Integer count) {
		this.id = id;
		this.name = name;
		this.count = count;
		if (this.created == null)
			this.created = new Date();
	}

	public static class BookBuilder {
		private String name;
		private Integer count;

		public BookBuilder(@JsonProperty("name") String name, @JsonProperty("count") Integer count) {
			this.name = name;
			this.count = count;
		}

		public Book build() {
			return new Book(this);
		}
	}

	private Book(BookBuilder builder) {
		this.name = builder.name;
		this.count = builder.count;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Book other = (Book) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public Set<Student> getStudents() {
		return students;
	}

	public void setStudents(Set<Student> students) {
		this.students = students;
	}
}
