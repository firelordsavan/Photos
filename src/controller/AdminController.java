package controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import app.Photos;
import model.Admin;
import model.User;

/**
 * Admin Controller used to connect the Admin.fxml View to Models.
 * 
 * @author	Naveenan Yogeswaran
 * @author	Savan Patel
 *
 */

public class AdminController {
	
	/**
	 * List View that is used to show list of users.
	 */
	@FXML ListView<String> userListView;
	
	/**
	 * ObservableList used to populate userListView and to keep track of new changes.
	 */
	private ObservableList<String> users;
	/**
	 * Reference to primaryStage used to display all our scenes.
	 */
	private static Stage primaryStage;
	/**
	 * Reference to Admin who has reference to all Users.
	 */
	Admin admin = Photos.getAdmin();
	
	
	/**
	 * Returns reference to users.
	 * @return	ObservableList used to populate userListView and to keep track of new changes.
	 */
	public ObservableList<String> getObservableList(){
		return users;
	}
	
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
	 * Used to start the Admin.fxml View.
	 * @param mainStage	Reference to primaryStage used to display all our scenes.
	 */
	public void start(Stage mainStage) {  
		setPrimaryStage(mainStage);
		
		// create an ObservableList 
		// from an ArrayList 
		ArrayList<String> savedUsers = admin.getUsers();
		
		users = FXCollections.observableArrayList(savedUsers);
		
		// set ListView to display Observable List
		userListView.setItems(users);
		
		// select the first item
		userListView.getSelectionModel().select(0);
	}
	
	/**
	 * Deletes the selected User and all associated Albums and Photos.
	 * @param e	Reference to ActionEvent
	 */
	public void deleteUser(ActionEvent e) {
		// get User object and index of selected User
		String selectedUser = userListView.getSelectionModel().getSelectedItem();
		if(selectedUser == null) {
			errorNotice("No User Error", "There is no user selected, so the action cannot be performed.");
			return;
		}
		int index = userListView.getSelectionModel().getSelectedIndex();
		
		// ask Admin to confirm if they want to delete the selected User
		if(!deleteConfirmation(AdminController.getPrimaryStage(), selectedUser)) {
			return;
		}
		
		// remove selected User's .dat file
		File file = new File("session/" + selectedUser + ".dat");
		file.delete();
		// remove selected User
		admin.getUsers().remove(index);
		users.remove(index);
		
		// select the next item
		if(users.size() != 0) {
			userListView.getSelectionModel().select(index);
		}
		
	}
	
	/**
	 * Asks User to confirm Deletion of User.
	 * @param primaryStage	Reference to primaryStage used to display all our scenes.
	 * @param username	The username of the User that is to be deleted.
	 * @return	True if User confirms action; False if User cancels action
	 */
	public boolean deleteConfirmation(Stage primaryStage, String username) {
		// get confirmation that user wants to delete the new User
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.initOwner(primaryStage);
		alert.setTitle("Delete User");
		alert.setContentText("Are you sure you want to delete:\n"
				+ "User: " + username);

		// wait for user to either cancel or confirm
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
		    return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Creates new User using a TextInputDialog Box.
	 * @param e	Reference to ActionEvent
	 * @throws Exception	Used to throw Exception relating to File.
	 */
	public void createUser(ActionEvent e) throws Exception {
		TextInputDialog dialog = new TextInputDialog();
		dialog.initOwner(primaryStage);
		dialog.setTitle("Create User");
		dialog.setContentText("Enter new Username: ");

		Optional<String> result = dialog.showAndWait();
		if (result.isPresent() && !result.get().trim().equals("")) {
			String newUsername = result.get().trim(); // New Username
			// make sure username isn't "admin"
			if(newUsername.toLowerCase().equals("admin")) {
				errorNotice("Admin Exists Error", "You may not create a username similar to \"admin\".");
				return;
			}
			
			ArrayList<String> existingUsers = admin.getUsers();
			// check if user already exists
			for(int i = 0; i < existingUsers.size(); i++) {
				if(newUsername.toLowerCase().equals(existingUsers.get(i).toLowerCase())) {
					errorNotice("Existing User Error", "An user with the same username already exists.");
					return;
				}
			}
			
			User newUser = new User(newUsername);
			// create save file for user
			File file = new File("session/" + newUsername + ".dat");
			if(!file.exists()){
				file.createNewFile();
			}
			// save initial User
			newUser.writeUser();
			// add User to list
			admin.getUsers().add(newUsername);
			users.add(newUsername);
		}
		else {
			return;
		}
		
		// select the inserted item
		if(users.size() != 0) {
			userListView.getSelectionModel().select(users.size()-1);
		}
	}
	
	/**
	 * Saves all changes made and returns User to Login.fxml View.
	 * @param e	Reference to ActionEvent
	 * @throws Exception	Used to throw Exception from FXMLLoader.
	 */
	public void logout(ActionEvent e) throws Exception {
		Admin.writeAdmin(admin);
		
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
	 * Template for error dialogs
	 * @param errorTitle	Error Title
	 * @param errorDesc	Description of the error
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
