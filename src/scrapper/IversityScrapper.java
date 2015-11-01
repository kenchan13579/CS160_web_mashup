package scrapper;

import java.io.IOException;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import model.Course;

public class IversityScrapper implements CourseScrapper {

	String homepage = "https://iversity.org/en/courses";

	public IversityScrapper() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Course> scrapeCourses() {
		try {
			Document catalogDoc = Jsoup.connect(homepage).get();
		} catch (IOException e) {
			System.out.println("Unable to connect to Iversity homepage at: " + homepage);
			System.out.println("Exception is: " + e.toString());
			e.printStackTrace();
		}
		throw new UnsupportedOperationException("Not yet implemented.");
	}

}
