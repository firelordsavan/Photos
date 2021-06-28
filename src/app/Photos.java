package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.File;

import controller.LoginController;
import model.Admin;
import model.User;

/**
 * This class contains the main method and is used to start the program.
 * 
 * @author	Naveenan Yogeswaran
 * @author	Savan Patel
 *
 */

public class Photos extends Application {
	
	/**
	 * Admin object that holds references to all Users.
	 * Static to allow access for all Controllers.
	 */
	static Admin admin;
	
	/**
	 * User object that references the current active user.
	 */
	static User activeUser;
	
	/**
	 * Returns Admin Object
	 * @return	Admin object that holds references to all Users.
	 */
	public static Admin getAdmin() {
		return admin;
	}
	
	/**
	 * Sets the active User currently.
	 * @param user	User who is currently active.
	 */
	public static void setActiveUser(User user) {
		activeUser = user;
	}

	/**
	 * Used to start the program.
	 * This will launch the Login View.
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		
		// Make directory for session if it doesn't exist already
		File sessionDir = new File("session");
		if(!sessionDir.exists()) {
			sessionDir.mkdir();
		}
		
		// Read previously saved Admin or create new one
		File file = new File("session/Admin.dat");
		if(!file.exists()){
			file.createNewFile();
			admin = new Admin();
		}
		else {
			admin = Admin.readAdmin();
		}
		
		// Load the Login page
		FXMLLoader loader = new FXMLLoader();   
		loader.setLocation( getClass().getResource("/view/Login.fxml"));
		AnchorPane root = (AnchorPane)loader.load();
		
		LoginController loginController = loader.getController();
		loginController.start(primaryStage);
		
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	/**
	 * Main method used to launch program.
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}
	
	/**
	 * Will be called when user exits program.
	 * This will save any changes made to the program.
	 */
	@Override
	public void stop() throws Exception{
		Admin.writeAdmin(admin);
		if(activeUser != null) {
			activeUser.writeUser();
		}
	}

}
