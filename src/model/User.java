package model;

import java.util.ArrayList;
import java.io.*;

/**
 * Used to store information that is relevant to Users.
 * 
 * @author Naveenan Yogeswaran
 * @author Savan Patel
 *
 */

public class User implements Serializable{

	/**
	 * Default serial version UID for serialization.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Username of the User.
	 */
	private String username;
	/**
	 * List of all Album objects that belong to this User.
	 */
	private ArrayList<Album> albums;
	/**
	 * List of all tag types that the User created or have available.
	 */
	private ArrayList<String> tagTypes;
	
	/**
	 * String that holds name of directory that the serialized files are stored in.
	 */
	public static final String storeDir = "session";
	/**
	 * String that holds name of .dat file that the serialized information is stored in.
	 */
	public String storeFile;
	
	/**
	 * Constructor for User.
	 * @param username	The username of the User.
	 */
	public User(String username) {
		this.username = username;
		albums = new ArrayList<Album>();
		tagTypes = new ArrayList<String>();
		tagTypes.add("person");
		tagTypes.add("location");
		
		storeFile = username + ".dat";
	}
	
	/**
	 * Returns reference username.
	 * @return	Username of the User.
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * Returns reference to albums.
	 * @return	List of all Album objects that belong to this User.
	 */
	public ArrayList<Album> getAlbums() {
		return albums;
	}
	
	/**
	 * Returns reference to tagTypes.
	 * @return	List of all tag types that the User created or have available.
	 */
	public ArrayList<String> getTagTypes() {
		return tagTypes;
	}
	
	/**
	 * Returns the User's username.
	 */
	public String toString() {
		return username;
	}
	
	/**
	 * Used to serialize User object for persistence.
	 * @throws IOException	Used to throw IOException relating to streams.
	 */
	public void writeUser() throws IOException{
		ObjectOutputStream oos= new ObjectOutputStream(new FileOutputStream(storeDir+ File.separator+ storeFile));
		oos.writeObject(this);
		oos.close();
	}
	
	/**
	 * Used to deserialize .dat file to retrieve saved User object.
	 * @param username	The name of the user that is to be serialized.
	 * @return	Reference to the saved User object.
	 * @throws IOException	Used to throw IOException relating to streams.
	 * @throws ClassNotFoundException	Used to throw ClassNotFoundException.
	 */
	public static User readUser(String username) throws IOException, ClassNotFoundException{
		ObjectInputStream ois= new ObjectInputStream(new FileInputStream(storeDir+ File.separator+ username + ".dat"));
		User user= (User)ois.readObject();
		ois.close();
		return user;
	}
}
