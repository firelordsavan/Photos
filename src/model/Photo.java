package model;

import java.util.ArrayList;
import java.util.Calendar;
import java.io.*;

/**
 * Used to store information that is relevant to Photos.
 * 
 * @author Naveenan Yogeswaran
 * @author Savan Patel
 *
 */

public class Photo implements Serializable{

	/**
	 * Default serial version UID for serialization.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Location of the Photo on the disk.
	 */
	private String location;
	/**
	 * Caption of the Photo.
	 */
	private String caption;
	/**
	 * Last modified date of the Photo.
	 */
	private Calendar date;
	/**
	 * List of Tag objects associated with this Photo.
	 */
	private ArrayList<Tag> tags;
	
	/**
	 * Constructor for Photo.
	 * @param location	Location of the Photo on the disk.
	 */
	public Photo(String location) {
		this.location = location;
		this.caption = "No Caption";
		
		File file = new File(location);
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(file.lastModified());
		this.date = cal;
		cal.set(Calendar.MILLISECOND, 0);
		
		tags = new ArrayList<Tag>();
	}

	/**
	 * Returns reference to location.
	 * @return	Location of the Photo on the disk.
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * Sets the location of the Photo.
	 * @param location	Location of the Photo on the disk.
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * Returns reference to caption.
	 * @return	Caption of the Photo.
	 */
	public String getCaption() {
		return caption;
	}

	/**
	 * Sets the caption of the Photo.
	 * @param caption	Caption of the Photo.
	 */
	public void setCaption(String caption) {
		this.caption = caption;
	}

	/**
	 * Returns reference to date.
	 * @return	Last modified date of the Photo.
	 */
	public Calendar getDate() {
		return date;
	}

	/**
	 * Sets the date of the Photo.
	 * @param date	Last modified date of the Photo.
	 */
	public void setDate(Calendar date) {
		this.date = date;
	}
	
	/**
	 * Returns reference to tags.
	 * @return	List of Tag objects associated with this Photo.
	 */
	public ArrayList<Tag> getTags() {
		return tags;
	}

	/**
	 * Sets the tags of the Photo.
	 * @param tags	List of Tag objects associated with this Photo.
	 */
	public void setTags(ArrayList<Tag> tags) {
		this.tags = tags;
	}
	
	/**
	 * Returns the caption of the Photo.
	 */
	public String toString() {
		return caption;
	}
	
	
}
