package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.File;

import app.Photos;
import model.Admin;
import model.User;

/**
 * Login Controller used to connect the Login.fxml View to Models.
 * 
 * @author	Naveenan Yogeswaran
 * @author	Savan Patel
 *
 */

public class LoginController {
	
	/**
	 * Holds user input for username.
	 */
	@FXML TextField username;
	
	/**
	 * Reference to primaryStage used to display all our scenes.
	 */
	private static Stage primaryStage;
	/**
	 * Reference to Admin who has reference to all Users.
	 */
	Admin admin = Photos.getAdmin();
	
	/**
	 * Returns reference to primaryStage.
	 * @return	Reference to primaryStage used to display all our scenes.
	 */
	public static Stage getPrimaryStage() {
		return primaryStage;
	}
	/**
	 * Sets our reference to primaryStage in order to allow all methods to have access.
	 * @param mainStage	Reference to primaryStage used to display all our scenes.
	 */
	public static void setPrimaryStage(Stage mainStage) {
		primaryStage = mainStage;
	}
	
	/**
	 * Used to start the Login.fxml View.
	 * @param mainStage	Reference to primaryStage used to display all our scenes.
	 */
	public void start(Stage mainStage) {  
		setPrimaryStage(mainStage);
	}
	
	/**
	 * This method will check the username input to see if there is a matching user.
	 * If "admin", then user will be navigated to the Admin.fxml View.
	 * If a valid User, then user will be navigated to the Album.fxml View of that specified User.
	 * If not a valid user, then an Alert will notify the user of an incorrect username.
	 * @param e	Reference to ActionEvent
	 * @throws Exception	Used to throw Exception from FXMLLoader.
	 */
	public void login(ActionEvent e) throws Exception {
		String inputUser = username.getText();
		FXMLLoader loader = new FXMLLoader(); 
		
		if(inputUser.equals("admin")) {
			loader.setLocation( getClass().getResource("/view/Admin.fxml"));
			AnchorPane root = (AnchorPane)loader.load();
			
			AdminController adminController = loader.getController();
			adminController.start(primaryStage);
			
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.show();
		}
		else {
			String selectedUser = null;
			for(String username : admin.getUsers()) {
				if(inputUser.equals(username)) {
					selectedUser = username;
					break;
				}
			}
			if(selectedUser == null) {
				errorNotice("No User Error", "This user does not exist.");
			}
			else {
				// Read previously saved User or create new one
				User user = null;
				File file = new File("session/" + selectedUser + ".dat");
				if(!file.exists()){
					file.createNewFile();
					user = new User(selectedUser);
					user.writeUser();
				}
				user = User.readUser(selectedUser);
				
				Photos.setActiveUser(user);
				
				loader.setLocation( getClass().getResource("/view/Album.fxml"));
				AnchorPane root = (AnchorPane)loader.load();
				
				AlbumController albumController = loader.getController();
				albumController.start(primaryStage, user);
				
				Scene scene = new Scene(root);
				primaryStage.setScene(scene);
				primaryStage.show();
			}
		}
		
	}
	
	
	/**
	 * Template for error dialogs
	 * @param errorTitle : Error Title
	 * @param errorDesc : Description of the error
	 */
	public void errorNotice(String errorTitle, String errorDesc) {
		// display custom error message
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error Dialog");
		alert.setHeaderText(errorTitle);
		alert.setContentText(errorDesc);

		alert.showAndWait();
	}
	
}
