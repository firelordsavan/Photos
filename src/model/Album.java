package model;

import java.util.ArrayList;
import java.io.*;
import java.util.Calendar;

/**
 * Used to store information that is relevant to Albums.
 * 
 * @author Naveenan Yogeswaran
 * @author Savan Patel
 *
 */

public class Album implements Serializable{

	/**
	 * Default serial version UID for serialization.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Name of the Album.
	 */
	private String name;
	/**
	 * The number of Photo objects belonging to this Album.
	 */
	private int numPhotos;
	/**
	 * Stores the Calendar object of the earliest date of all Photos in this album.
	 */
	private Calendar earliestDate;
	/**
	 * Stores the Calendar object of the latest date of all Photos in this album.
	 */
	private Calendar latestDate;
	/**
	 * List of all Photo objects that belong to this album.
	 */
	private ArrayList<Photo> photos;
	
	/**
	 * Constructor for Album.
	 * @param name	Name of the Album.
	 */
	public Album(String name) {
		this.name = name;
		this.numPhotos = 0;
		this.earliestDate = null;
		this.latestDate = null;
		photos = new ArrayList<Photo>();
	}
	
	/**
	 * Returns reference to name.
	 * @return	Name of the Album.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of Album.
	 * @param name	Name of the Album.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns reference to numPhotos.
	 * @return	The number of Photo objects belonging to this Album.
	 */
	public int getNumPhotos() {
		return numPhotos;
	}

	/**
	 * Sets the numPhotos of Album.
	 * @param numPhotos	The number of Photo objects belonging to this Album.
	 */
	public void setNumPhotos(int numPhotos) {
		this.numPhotos = numPhotos;
	}

	/**
	 * Returns reference to earliestDate.
	 * @return	Stores the Calendar object of the earliest date of all Photos in this album.
	 */
	public Calendar getEarliestDate() {
		return earliestDate;
	}

	/**
	 * Sets the earliestDate of Album.
	 * @param earliestDate	Stores the Calendar object of the earliest date of all Photos in this album.
	 */
	public void setEarliestDate(Calendar earliestDate) {
		this.earliestDate = earliestDate;
	}

	/**
	 * Returns reference to latestDate.
	 * @return	Stores the Calendar object of the latest date of all Photos in this album.
	 */
	public Calendar getLatestDate() {
		return latestDate;
	}

	/**
	 * Sets the latestDate of Album.
	 * @param latestDate	Stores the Calendar object of the latest date of all Photos in this album.
	 */
	public void setLatestDate(Calendar latestDate) {
		this.latestDate = latestDate;
	}
	
	/**
	 * Returns reference to photos.
	 * @return	List of all Photo objects that belong to this album.
	 */
	public ArrayList<Photo> getPhotos() {
		return photos;
	}

	/**
	 * Adds new Photo object to photos and increments numPhotos.
	 * If new photo has date earlier than earliestDate, earliestDate is updated.
	 * If new photo has date later than latestDate, latestDate is updated.
	 * @param newPhoto	New Photo object that is to be added to photos.
	 */
	public void addPhoto(Photo newPhoto) {
		photos.add(newPhoto);
		numPhotos += 1;
		if(earliestDate == null || newPhoto.getDate().compareTo(earliestDate) < 0) {
			earliestDate = newPhoto.getDate();
		}
		if(latestDate == null || newPhoto.getDate().compareTo(latestDate) > 0) {
			latestDate = newPhoto.getDate();
		}
	}
	
	/**
	 * Removes the Photo object at the given index and decrements numPhotos.
	 * If removed Photo had the earliestDate, earliestDate is updated to the next earliestDate.
	 * If removed Photo had the latestDate, latestDate is updated to the next latestDate.
	 * If photos is empty after removal, earliestDate and latestDate are set to null.
	 * @param index	Index of the Photo to be removed.
	 */
	public void removePhoto(int index) {
		Photo deletedPhoto = photos.remove(index);
		numPhotos -= 1;
		if(numPhotos == 0) {
			earliestDate = null;
			latestDate = null;
			return;
		}
		if(earliestDate == deletedPhoto.getDate()) {
			Calendar newEarlyDate = photos.get(0).getDate();
			for(int i = 1; i < photos.size(); i++) {
				if(newEarlyDate.compareTo(photos.get(i).getDate()) > 0) {
					newEarlyDate = photos.get(i).getDate();
				}
			}
			earliestDate = newEarlyDate;
		}
		if(latestDate == deletedPhoto.getDate()) {
			Calendar newLateDate = photos.get(0).getDate();
			for(int i = 1; i < photos.size(); i++) {
				if(newLateDate.compareTo(photos.get(i).getDate()) < 0) {
					newLateDate = photos.get(i).getDate();
				}
			}
			latestDate = newLateDate;
		}
	}

	/**
	 * Returns the name, numPhotos, and the date range of the Album.
	 */
	public String toString() {
		String result = name + "\n" + numPhotos + " Photos\n";
		if(earliestDate == null) {
			result += "No Date Range";
		}
		else {
			result += earliestDate.getTime().toString() + " to " + latestDate.getTime().toString();
		}
		return result;
	}
	
}
