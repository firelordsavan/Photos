package controller;

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
import model.Album;
import model.User;

/**
 * Album Controller used to connect the Album.fxml View to Models.
 * 
 * @author	Naveenan Yogeswaran
 * @author	Savan Patel
 *
 */

public class AlbumController {
	
	/**
	 * List View that is used to show list of albums belonging to selected user.
	 */
	@FXML ListView<Album> albumListView;

	/**
	 * ObservableList used to populate albumListView and to keep track of new changes.
	 */
	ObservableList<Album> albums; 
	/**
	 * Reference to primaryStage used to display all our scenes.
	 */
	private static Stage primaryStage;
	/**
	 * Reference to User who has reference to all of the selected User's Albums.
	 */
	User user;
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
	 * Returns reference to the selected User.
	 * @return	Reference to User who has reference to all of the selected User's Albums.
	 */
	public User getUser() {
		return user;
	}
	/**
	 * Sets the reference to User who has reference to all of the User's Albums.
	 * @param user	Reference to User who has reference to all of the selected User's Albums.
	 */
	public void setUser(User user) {
		this.user = user;
	}
	
	/**
	 * Used to start the Album.fxml View.
	 * @param mainStage	Reference to primaryStage used to display all our scenes.
	 * @param selectedUser	Reference to the selected User
	 */
	public void start(Stage mainStage, User selectedUser) {
		setPrimaryStage(mainStage);
		setUser(selectedUser);
		
		// create an ObservableList 
		// from an ArrayList 
		ArrayList<Album> savedAlbums = user.getAlbums();
		
		albums = FXCollections.observableArrayList(savedAlbums);
		
		// set ListView to display Observable List
		albumListView.setItems(albums);
		
		// select the first item
		albumListView.getSelectionModel().select(0);
	}
	
	/**
	 * Navigates to the selected User's PhotoView.fxml View.
	 * @param e	Reference to ActionEvent
	 * @throws Exception	Used to throw Exception from FXMLLoader.
	 */
	public void openAlbum(ActionEvent e) throws Exception{
		// get Album object and index of selected Album
		Album selectedAlbum = albumListView.getSelectionModel().getSelectedItem();
		if(selectedAlbum == null) {
			errorNotice("No Album Error", "There is no album selected, so the action cannot be performed.");
			return;
		}
		
		FXMLLoader loader = new FXMLLoader(); 
		loader.setLocation( getClass().getResource("/view/PhotoView.fxml"));
		AnchorPane root = (AnchorPane)loader.load();
		
		PhotoViewController photoViewController = loader.getController();
		photoViewController.start(primaryStage, selectedAlbum, user);
		
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	/**
	 * Deletes the selected Album and all associated Photos.
	 * @param e	Reference to ActionEvent
	 */
	public void deleteAlbum(ActionEvent e) {
		// get Album object and index of selected Album
		Album selectedAlbum = albumListView.getSelectionModel().getSelectedItem();
		if(selectedAlbum == null) {
			errorNotice("No Album Error", "There is no album selected, so the action cannot be performed.");
			return;
		}
		int index = albumListView.getSelectionModel().getSelectedIndex();
		
		// ask User to confirm if they want to delete the selected Album
		if(!deleteConfirmation(AdminController.getPrimaryStage(), selectedAlbum.getName())) {
			return;
		}
		
		// remove selected Album
		user.getAlbums().remove(index);
		albums.remove(index);
		
		// select the next item
		if(albums.size() != 0) {
			albumListView.getSelectionModel().select(index);
		}
		
	}
	
	/**
	 * Asks User to confirm Deletion of Album.
	 * @param primaryStage	Reference to primaryStage used to display all our scenes.
	 * @param name	The name of the Album that is to be deleted.
	 * @return	True if User confirms action; False if User cancels action
	 */
	public boolean deleteConfirmation(Stage primaryStage, String name) {
		// get confirmation that User wants to delete the new Album
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.initOwner(primaryStage);
		alert.setTitle("Delete Album");
		alert.setContentText("Are you sure you want to delete:\n"
				+ "Album: " + name);

		// wait for user to either cancel or confirm
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
		    return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Creates new Album using a TextInputDialog Box.
	 * @param e	Reference to ActionEvent
	 * @throws Exception	Used to throw Exception.
	 */
	public void createAlbum(ActionEvent e) throws Exception{
		TextInputDialog dialog = new TextInputDialog();
		dialog.initOwner(primaryStage);
		dialog.setTitle("Create Album");
		dialog.setContentText("Enter new Album Name: ");

		Optional<String> result = dialog.showAndWait();
		if (result.isPresent() && !result.get().trim().equals("")) {
			String newAlbumName = result.get().trim(); // New Album Name
			ArrayList<Album> existingAlbums = getUser().getAlbums();
			// check if album name already exists
			for(int i = 0; i < existingAlbums.size(); i++) {
				if(newAlbumName.toLowerCase().equals(existingAlbums.get(i).getName().toLowerCase())) {
					errorNotice("Existing Album Error", "An album with the same name already exists.");
					return;
				}
			}
			
			Album newAlbum = new Album(newAlbumName);
			user.getAlbums().add(newAlbum);
			albums.add(new Album(newAlbumName));
		}
		
		// select the inserted item
		if(albums.size() != 0) {
			albumListView.getSelectionModel().select(albums.size()-1);
		}
		
		reload();
	}
	
	/**
	 * Renames the selected Album to the new name that was inserted using a TextInputDialogBox.
	 * @param e	Reference to ActionEvent
	 */
	public void renameAlbum(ActionEvent e) {
		// get Album object and index of selected Album
		Album selectedAlbum = albumListView.getSelectionModel().getSelectedItem();
		if(selectedAlbum == null) {
			errorNotice("No Album Error", "There is no album selected, so the action cannot be performed.");
			return;
		}
		int index = albumListView.getSelectionModel().getSelectedIndex();
		
		TextInputDialog dialog = new TextInputDialog();
		dialog.initOwner(primaryStage);
		dialog.setTitle("Rename Album");
		dialog.setContentText("Current Album Name: " + selectedAlbum.getName() + "\n" + "Enter new Album Name: ");
		
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent() && !result.get().equals("")) {
			String newAlbumName = result.get().trim(); // New Album Name
			ArrayList<Album> existingAlbums = getUser().getAlbums();
			// check if album name already exists
			for(int i = 0; i < existingAlbums.size(); i++) {
				if(newAlbumName.toLowerCase().equals(existingAlbums.get(i).getName().toLowerCase())) {
					errorNotice("Existing Album Error", "An album with the same name already exists.");
					return;
				}
			}
			
			selectedAlbum.setName(newAlbumName);
		}
		
		// select the inserted item
		if(albums.size() != 0) {
			albums.remove(index);
			albums.add(index, selectedAlbum);
			albumListView.getSelectionModel().select(index);
		}
		
	}
	
	/**
	 * Navigates to User's PhotoSearch.fxml View
	 * @param e	Reference to ActionEvent
	 * @throws Exception	Used to throw Exception from FXMLLoader.
	 */
	public void photoSearch(ActionEvent e) throws Exception {
		FXMLLoader loader = new FXMLLoader();   
		loader.setLocation( getClass().getResource("/view/PhotoSearch.fxml"));
		AnchorPane root = (AnchorPane)loader.load();
		
		PhotoSearchController photoSearchController = loader.getController();
		photoSearchController.start(primaryStage, user);
		
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	/**
	 * Saves all changes made and returns User to Login.fxml View.
	 * @param e	Reference to ActionEvent
	 * @throws Exception	Used to throw Exception from FXMLLoader.
	 */
	public void logout(ActionEvent e) throws Exception {
		getUser().writeUser();
		
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
	 * Reloads the page.
	 * @throws Exception	Used to throw Exception relating to FXMLLoader.
	 */
	public void reload() throws Exception {
		FXMLLoader loader = new FXMLLoader();
		
		loader.setLocation( getClass().getResource("/view/Album.fxml"));
		AnchorPane root = (AnchorPane)loader.load();
		
		AlbumController albumController = loader.getController();
		albumController.start(primaryStage, getUser());
		
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.show();
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
