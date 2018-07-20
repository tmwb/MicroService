package tm.demo.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Entity
@Table(name = "courses")
@NamedQuery(name = "Course.all", query = "select b from Course b")
@JsonDeserialize(builder = Course.CourseBuilder.class)
public class Course implements Serializable {
	private static final long serialVersionUID = -8417802198974836736L;
	@Id
	private Long id;
	@Column
	private String name;
	@Column
	private Boolean required;
	@Column
	private Integer seats;
	@Column
	private Date created;
	@JsonBackReference
	@OneToMany(mappedBy = "course", fetch = FetchType.EAGER)
	private Set<Book> books;
	@JsonBackReference
	@ManyToMany(mappedBy = "courses", fetch = FetchType.EAGER)
	private Set<Student> students;

	public Course() {
	}

	public Course(Long id, String name, boolean required, int seats) {
		this.id = id;
		this.name = name;
		this.required = required;
		this.seats = seats;
		if (this.created == null)
			this.created = new Date();
	}

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

	public Boolean getRequired() {
		return required;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}

	public Integer getSeats() {
		return seats;
	}

	public void setSeats(Integer seats) {
		this.seats = seats;
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
		Course other = (Course) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public Set<Book> getBooks() {
		return books;
	}

	public void setBooks(Set<Book> books) {
		this.books = books;
	}

	public Set<Student> getStudents() {
		return students;
	}

	public void setStudents(Set<Student> students) {
		this.students = students;
	}
}
