package model;

import java.time.LocalDate;

/*
 * This class and its fields were generated using the database schema we were assigned. See package README.
 */
public class Course {
	String title;
	String shortDescription;
	String longDescription;
	String courseLink;
	String videoLink;
	LocalDate startDate;
	int courseLength;
	String courseImage;
	String categorey;
	String site;
	int courseFee;
	String language;
	String university;
	Certificate certficate;
	LocalDate timeScraped;
	CourseDetails details;

	/*
	 * If using the empty constructor, use the setters to fill in the relevant
	 * fields. Everything is initialized to null or 0;
	 */
	public Course() {
		this.title = null;
		this.shortDescription = null;
		this.longDescription = null;
		this.courseLink = null;
		this.videoLink = null;
		this.startDate = null;
		this.courseLength = 0;
		this.courseImage = null;
		this.categorey = null;
		this.site = null;
		this.courseFee = 0;
		this.language = null;
		this.university = null;
		this.certficate = null;
		this.timeScraped = null;
		this.details = null;
	}

	public Course(String title, String shortDescription, String longDescription, String courseLink, String videoLink,
			LocalDate startDate, int courseLength, String courseImage, String categorey, String site, int courseFee,
			String language, String university, Certificate certficate, LocalDate timeScraped, CourseDetails details) {
		this.title = title;
		this.shortDescription = shortDescription;
		this.longDescription = longDescription;
		this.courseLink = courseLink;
		this.videoLink = videoLink;
		this.startDate = startDate;
		this.courseLength = courseLength;
		this.courseImage = courseImage;
		this.categorey = categorey;
		this.site = site;
		this.courseFee = courseFee;
		this.language = language;
		this.university = university;
		this.certficate = certficate;
		this.timeScraped = timeScraped;
		this.details = details;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public String getLongDescription() {
		return longDescription;
	}

	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}

	public String getCourseLink() {
		return courseLink;
	}

	public void setCourseLink(String courseLink) {
		this.courseLink = courseLink;
	}

	public String getVideoLink() {
		return videoLink;
	}

	public void setVideoLink(String videoLink) {
		this.videoLink = videoLink;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public int getCourseLength() {
		return courseLength;
	}

	public void setCourseLength(int courseLength) {
		this.courseLength = courseLength;
	}

	public String getCourseImage() {
		return courseImage;
	}

	public void setCourseImage(String courseImage) {
		this.courseImage = courseImage;
	}

	public String getCategorey() {
		return categorey;
	}

	public void setCategorey(String categorey) {
		this.categorey = categorey;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public int getCourseFee() {
		return courseFee;
	}

	public void setCourseFee(int courseFee) {
		this.courseFee = courseFee;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getUniversity() {
		return university;
	}

	public void setUniversity(String university) {
		this.university = university;
	}

	public Certificate getCertficate() {
		return certficate;
	}

	public void setCertficate(Certificate certficate) {
		this.certficate = certficate;
	}

	public LocalDate getTimeScraped() {
		return timeScraped;
	}

	public void setTimeScraped(LocalDate timeScraped) {
		this.timeScraped = timeScraped;
	}

	public CourseDetails getDetails() {
		return details;
	}

	public void setDetails(CourseDetails details) {
		this.details = details;
	}

	@Override
	public String toString() {
		return "Course [title=" + title + ", shortDescription=" + shortDescription + ", longDescription="
				+ longDescription + ", courseLink=" + courseLink + ", videoLink=" + videoLink + ", startDate="
				+ startDate + ", courseLength=" + courseLength + ", courseImage=" + courseImage + ", categorey="
				+ categorey + ", site=" + site + ", courseFee=" + courseFee + ", language=" + language + ", university="
				+ university + ", certficate=" + certficate + ", timeScraped=" + timeScraped + ", details=" + details
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((categorey == null) ? 0 : categorey.hashCode());
		result = prime * result + ((certficate == null) ? 0 : certficate.hashCode());
		result = prime * result + courseFee;
		result = prime * result + ((courseImage == null) ? 0 : courseImage.hashCode());
		result = prime * result + courseLength;
		result = prime * result + ((courseLink == null) ? 0 : courseLink.hashCode());
		result = prime * result + ((details == null) ? 0 : details.hashCode());
		result = prime * result + ((language == null) ? 0 : language.hashCode());
		result = prime * result + ((longDescription == null) ? 0 : longDescription.hashCode());
		result = prime * result + ((shortDescription == null) ? 0 : shortDescription.hashCode());
		result = prime * result + ((site == null) ? 0 : site.hashCode());
		result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
		result = prime * result + ((timeScraped == null) ? 0 : timeScraped.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((university == null) ? 0 : university.hashCode());
		result = prime * result + ((videoLink == null) ? 0 : videoLink.hashCode());
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
		if (categorey == null) {
			if (other.categorey != null)
				return false;
		} else if (!categorey.equals(other.categorey))
			return false;
		if (certficate != other.certficate)
			return false;
		if (courseFee != other.courseFee)
			return false;
		if (courseImage == null) {
			if (other.courseImage != null)
				return false;
		} else if (!courseImage.equals(other.courseImage))
			return false;
		if (courseLength != other.courseLength)
			return false;
		if (courseLink == null) {
			if (other.courseLink != null)
				return false;
		} else if (!courseLink.equals(other.courseLink))
			return false;
		if (details == null) {
			if (other.details != null)
				return false;
		} else if (!details.equals(other.details))
			return false;
		if (language == null) {
			if (other.language != null)
				return false;
		} else if (!language.equals(other.language))
			return false;
		if (longDescription == null) {
			if (other.longDescription != null)
				return false;
		} else if (!longDescription.equals(other.longDescription))
			return false;
		if (shortDescription == null) {
			if (other.shortDescription != null)
				return false;
		} else if (!shortDescription.equals(other.shortDescription))
			return false;
		if (site == null) {
			if (other.site != null)
				return false;
		} else if (!site.equals(other.site))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		if (timeScraped == null) {
			if (other.timeScraped != null)
				return false;
		} else if (!timeScraped.equals(other.timeScraped))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (university == null) {
			if (other.university != null)
				return false;
		} else if (!university.equals(other.university))
			return false;
		if (videoLink == null) {
			if (other.videoLink != null)
				return false;
		} else if (!videoLink.equals(other.videoLink))
			return false;
		return true;
	}

}
