package model;

import java.util.ArrayList;
import java.io.*;

/**
 * Used to store information that is relevant to Admins, such as list of Users.
 * 
 * @author Naveenan Yogeswaran
 * @author Savan Patel
 *
 */

public class Admin implements Serializable {

	/**
	 * Default serial version UID for serialization.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * List containing all Users of the program.
	 */
	private ArrayList<String> currentUsers;
	
	/**
	 * String that holds name of directory that the serialized files are stored in.
	 */
	public static final String storeDir = "session";
	/**
	 * String that holds name of .dat file that the serialized information is stored in.
	 */
	public static final String storeFile = "Admin.dat";
	
	/**
	 * Constructor for Admin.
	 */
	public Admin() {
		currentUsers = new ArrayList<String>();
	}
	
	/**
	 * Returns reference to currentUsers.
	 * @return	List containing all Users of the program.
	 */
	public ArrayList<String> getUsers(){
		return currentUsers;
	}
	
	/**
	 * Used to serialize Admin object for persistence.
	 * @param admin	Admin object to be serialized.
	 * @throws IOException	Used to throw IOException relating to streams.
	 */
	public static void writeAdmin(Admin admin) throws IOException{
		ObjectOutputStream oos= new ObjectOutputStream(new FileOutputStream(storeDir+ File.separator+ storeFile));
		oos.writeObject(admin);
		oos.close();
	}
	
	/**
	 * Used to deserialize .dat file to retrieve saved Admin object.
	 * @return	Reference to the saved Admin object.
	 * @throws IOException	Used to throw IOException relating to streams.
	 * @throws ClassNotFoundException	Used to throw ClassNotFoundException.
	 */
	public static Admin readAdmin() throws IOException, ClassNotFoundException{
		ObjectInputStream ois= new ObjectInputStream(new FileInputStream(storeDir+ File.separator+ storeFile));
		Admin admin= (Admin)ois.readObject();
		ois.close();
		return admin;
	}
	
}
