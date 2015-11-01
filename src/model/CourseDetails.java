package model;

public class CourseDetails {
	String profName;
	String profImage;

	/*
	 * If using the empty constructor, use the setters to fill in the relevant
	 * fields. Everything is initialized to null;
	 */
	public CourseDetails() {
		this.profName = null;
		this.profImage = null;
	}

	public CourseDetails(String profName, String profImage) {
		this.profName = profName;
		this.profImage = profImage;
	}

	public String getProfName() {
		return profName;
	}

	public void setProfName(String profName) {
		this.profName = profName;
	}

	public String getProfImage() {
		return profImage;
	}

	public void setProfImage(String profImage) {
		this.profImage = profImage;
	}

	@Override
	public String toString() {
		return "CourseDetails [profName=" + profName + ", profImage=" + profImage + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((profImage == null) ? 0 : profImage.hashCode());
		result = prime * result + ((profName == null) ? 0 : profName.hashCode());
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
		CourseDetails other = (CourseDetails) obj;
		if (profImage == null) {
			if (other.profImage != null)
				return false;
		} else if (!profImage.equals(other.profImage))
			return false;
		if (profName == null) {
			if (other.profName != null)
				return false;
		} else if (!profName.equals(other.profName))
			return false;
		return true;
	}
	
	

}
