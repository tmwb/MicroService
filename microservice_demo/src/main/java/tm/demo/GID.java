package tm.demo;

public class GID {
	public static Long courseCount = 0L, bookCount = 0L, studentCount = 0L;

	public static Long gen(String type) {
		switch (type) {
		case "course":
			return ++courseCount;
		case "book":
			return ++bookCount;
		case "student":
			return ++studentCount;
		default:
			return null;
		}
	}

}
