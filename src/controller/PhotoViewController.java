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
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.scene.control.ListCell;

import app.Photos;
import model.Admin;
import model.Album;
import model.Photo;
import model.Tag;
import model.User;

/**
 * PhotoView Controller used to connect the PhotoView.fxml View to Models.
 * 
 * @author	Naveenan Yogeswaran
 * @author	Savan Patel
 *
 */

public class PhotoViewController {
	
	/**
	 * List View that is used to show list of photos belonging to selected user and selected album.
	 */
	@FXML ListView<Photo> photoListView;
	/**
	 * Image View that is used to show the selected photo in the display area.
	 */
	@FXML ImageView photoDisplay;
	/**
	 * Text box used to display photo caption.
	 */
	@FXML Text caption;
	/**
	 * Text box used to display photo capture date.
	 */
	@FXML Text date;
	/**
	 * Combo Box used to display available tag types for user to pick from when adding new tag.
	 */
	@FXML ComboBox<String> tagTypeChoice;
	/**
	 * List View that is used to show list of tags belonging to displayed Photo.
	 */
	@FXML ListView<Tag> photoTagsListView;
	
	/**
	 * ObservableList used to populate photoListView and to keep track of new changes.
	 */
	ObservableList<Photo> photos;
	/**
	 * ObservableList used to populate tagTypeChoice and to keep track of new changes.
	 */
	ObservableList<String> tagTypes;
	/**
	 * ObservableList used to populated photoTagsListView and to keep track of new changes.
	 */
	ObservableList<Tag> photoTags;
	/**
	 * Reference to primaryStage used to display all our scenes.
	 */
	private static Stage primaryStage;
	/**
	 * Reference to Album who has references to all of the selected Album's Photos.
	 */
	Album album;
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
	 * Returns reference to the selected Album.
	 * @return	Reference to Album who has references to all of the selected Album's Photos.
	 */
	public Album getAlbum() {
		return album;
	}
	/**
	 * Sets the reference to Album who has reference to all of the User's Album's Photos.
	 * @param album	Reference to Album who has references to all of the selected Album's Photos.
	 */
	public void setAlbum(Album album) {
		this.album = album;
	}
	
	/**
	 * Used to start the PhotoView.fxml View.
	 * @param mainStage	Reference to primaryStage used to display all our scenes.
	 * @param selectedAlbum	Reference to the selected Album
	 * @param selectedUser	Reference to the selected User
	 */
	public void start(Stage mainStage, Album selectedAlbum, User selectedUser) {
		setPrimaryStage(mainStage);
		setUser(selectedUser);
		setAlbum(selectedAlbum);
		
		// create an ObservableList 
		// from an ArrayList 
		ArrayList<Photo> savedPhotos = album.getPhotos();
		
		photos = FXCollections.observableArrayList(savedPhotos);
		
		// set ListView to display Observable List
		photoListView.setItems(photos);
		
		// Fill ListView Cells with Thumbnail and Caption
		photoListView.setCellFactory(listView -> new ListCell<Photo>() {
		    private ImageView imageView = new ImageView();

		    @Override
		    public void updateItem(Photo photo, boolean empty) {
		        super.updateItem(photo, empty);
		        if (empty) {
		        	setText("");
		            setGraphic(null);
		        } else {
		        	// set the caption of the photo
		        	setText(photo.toString());
		        	// set the image of the photo
		            Image image = new Image("file:" + photo.getLocation(), 60, 60, true, true, true) ; 
		            imageView.setImage(image);
		            setGraphic(imageView);
		        }
		    }
		});
		
		// select the first item
		photoListView.getSelectionModel().select(0);
		
		// set default details for the display area
		if(photos.size() > 0) {
			showItem(mainStage);
		}
		else {
			caption.setText("");
			date.setText("");
		}
		
		// fill in ComboBox with user's tag types
		ArrayList<String> savedTagTypes = getUser().getTagTypes();
		tagTypes = FXCollections.observableArrayList(savedTagTypes);
		tagTypeChoice.setItems(tagTypes);
		
		// set ListView to display Observable List
		photoListView.setItems(photos);
		
		
		// set listener for the items
		photoListView
		.getSelectionModel()
		.selectedIndexProperty()
		.addListener(
				(obs, oldVal, newVal) -> 
				showItem(mainStage));
	}
	
	/**
	 * Display the selected photo, caption, data, and tags.
	 * @param mainStage	Reference to primaryStage used to display all our scenes.
	 */
	public void showItem(Stage mainStage) {
		// if no photo is selected (list is empty)
		if(photos.size() == 0) {
			photoDisplay.setImage(null);
			caption.setText("");
			date.setText("");
			photoTagsListView.getItems().clear();
			return;
		}
		
		// get reference to selected photo
		Photo selectedPhoto = photoListView.getSelectionModel().getSelectedItem();
		
		// set the photo
		Image image = new Image("file:" + selectedPhoto.getLocation(), true);
		photoDisplay.setImage(image);
		
		// set the caption
		caption.setText(selectedPhoto.getCaption());
		
		// set the date
		date.setText(selectedPhoto.getDate().getTime().toString());
		
		//set the tags
		ArrayList<Tag> savedPhotoTags = selectedPhoto.getTags();
		photoTags = FXCollections.observableArrayList(savedPhotoTags);
		photoTagsListView.setItems(photoTags);
	}
	
	/**
	 * Adds new Photo to Album using a TextInputDialog Box to take in the location of the Photo.
	 * If file does not exist, an Alert is given to the User.
	 * @param e	Reference to ActionEvent
	 */
	public void addPhoto(ActionEvent e) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Image File");
		fileChooser.getExtensionFilters().addAll(
		        new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif", "*.jpeg"));
		File selectedFile = fileChooser.showOpenDialog(getPrimaryStage());
		if (selectedFile != null) {
			ArrayList<Photo> existingPhotos = getAlbum().getPhotos();
			String imageLocation = selectedFile.getAbsolutePath();
			// check if photo already exists
			for(int i = 0; i < existingPhotos.size(); i++) {
				if(imageLocation.toLowerCase().equals(existingPhotos.get(i).getLocation().toLowerCase())) {
					errorNotice("Existing Photo Error", "A photo with the same file path already exists.");
					return;
				}
			}
			
			Photo newPhoto = null; // Reference to new Photo to be added
			// go through all of User's Album objects and Photo objects to see if Photo already exists
			ArrayList<Album> userAlbums = getUser().getAlbums();
			for(Album album : userAlbums) {
				ArrayList<Photo> albumPhotos = album.getPhotos();
				
				for(Photo photo : albumPhotos) {
					if(imageLocation.equals(photo.getLocation())) {
						newPhoto = photo;
						break;
					}
				}
			}
			
			if(newPhoto == null) {
				newPhoto = new Photo(imageLocation);
			}
			album.addPhoto(newPhoto);
			photos.add(newPhoto);
		}
		else {
			return;
		}
		
		// select the inserted item
		if(photos.size() != 0) {
			photoListView.getSelectionModel().select(photos.size()-1);
		}
	}
	
	/**
	 * Deletes the selected Photo and all associated Tags.
	 * @param e	Reference to ActionEvent
	 */
	public void deletePhoto(ActionEvent e) {
		// get Photo object and index of selected Photo
		Photo selectedPhoto = photoListView.getSelectionModel().getSelectedItem();
		if(selectedPhoto == null) {
			errorNotice("No Photo Error", "There is no photo selected, so the action cannot be performed.");
			return;
		}
		int index = photoListView.getSelectionModel().getSelectedIndex();
		
		// ask User to confirm if they want to delete the selected Photo
		if(!deleteConfirmation(AdminController.getPrimaryStage(), "Delete Photo", "Photo: " + selectedPhoto.getLocation())) {
			return;
		}
		
		// remove selected Photo
		album.removePhoto(index);
		photos.remove(index);
		
		// select the next item
		if(photos.size() != 0) {
			photoListView.getSelectionModel().select(index);
		}
		
	}
	
	/**
	 * Asks User to confirm Deletion of Photo.
	 * @param primaryStage	Reference to primaryStage used to display all our scenes.
	 * @param title	Title of the Alert dialog.
	 * @param content	Brief description of item to be deleted.
	 * @return	True if User confirms action; False if User cancels action
	 */
	public boolean deleteConfirmation(Stage primaryStage, String title, String content) {
		// get confirmation that User wants to delete the new Photo
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.initOwner(primaryStage);
		alert.setTitle(title);
		alert.setContentText("Are you sure you want to delete:\n"
				+ content);

		// wait for user to either cancel or confirm
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
		    return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Moves Photo from one Album to another Album
	 * @param e	Reference to ActionEvent
	 */
	public void movePhoto(ActionEvent e) {
		ChoiceDialog<Album> dialog = new ChoiceDialog<Album>(getUser().getAlbums().get(0), getUser().getAlbums());
		dialog.initOwner(primaryStage);
		dialog.setTitle("Move Photo");
		dialog.setContentText("Enter the Album that you want to move the Photo to: ");
		
		// wait for user to pick Album
		Optional<Album> result = dialog.showAndWait();
		if (result.isPresent()){
			Photo photoToMove = photoListView.getSelectionModel().getSelectedItem(); // Photo to move
			Album albumToMove = result.get(); // New Album to move Photo to
			// ensure Photo doesn't already exist
			for(Photo photoPTR : albumToMove.getPhotos()) {
				if(photoToMove == photoPTR) {
					errorNotice("Photo Already Exists", "The Album already contains this Photo");
					return;
				}
			}
			
			int photoIndex = photoListView.getSelectionModel().getSelectedIndex();
			// Remove Photo from current Album
			album.removePhoto(photoIndex);
			photos.remove(photoIndex);
			// Add Photo to new Album
			albumToMove.addPhoto(photoToMove);
		}
	}
	
	/**
	 * Copy Photo to another album.
	 * @param e	Reference to ActionEvent
	 */
	public void copyPhoto(ActionEvent e) {
		ChoiceDialog<Album> dialog = new ChoiceDialog<Album>(getUser().getAlbums().get(0), getUser().getAlbums());
		dialog.initOwner(primaryStage);
		dialog.setTitle("Copy Photo");
		dialog.setContentText("Enter the Album that you want to copy the Photo to: ");
		
		// wait for user to pick Album
		Optional<Album> result = dialog.showAndWait();
		if (result.isPresent()){
			Photo photoToMove = photoListView.getSelectionModel().getSelectedItem(); // Photo to copy
			Album albumToMove = result.get(); // New Album to copy Photo to
			// ensure Photo doesn't already exist
			for(Photo photoPTR : albumToMove.getPhotos()) {
				if(photoToMove == photoPTR) {
					errorNotice("Photo Already Exists", "The Album already contains this Photo");
					return;
				}
			}
			
			// Add Photo to new Album
			albumToMove.addPhoto(photoToMove);
		}
	}
	
	/**
	 * Adds new Tag for Photo given the Tag type and Tag value.
	 * @param e	Reference to ActionEvent
	 */
	public void addTag(ActionEvent e) {
		if(tagTypeChoice.getValue() == null) {
			errorNotice("No Tag Type Selected", "Please select a tag type using the dropdown before attempting to add a tag.");
			return;
		}
		TextInputDialog dialog = new TextInputDialog();
		dialog.initOwner(primaryStage);
		dialog.setTitle("Add Tag");
		dialog.setContentText("Enter the new Tag for type " + tagTypeChoice.getValue() + ": ");

		// get Photo object of selected Photo
		Photo selectedPhoto = photoListView.getSelectionModel().getSelectedItem();
		
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent() && !result.get().trim().equals("")) {
			String newTagValue = result.get().trim(); // New Tag Value
			ArrayList<Tag> existingTags = selectedPhoto.getTags();
			// check if tag already exists
			for(int i = 0; i < existingTags.size(); i++) {
				if(tagTypeChoice.getValue().equals(existingTags.get(i).getTagType()) && newTagValue.toLowerCase().equals(existingTags.get(i).getTagValue().toLowerCase())) {
					errorNotice("Existing Tag Error", "A tag with the same type and value already exists.");
					return;
				}
			}
			
			Tag newTag = new Tag(tagTypeChoice.getValue(), newTagValue);
			selectedPhoto.getTags().add(newTag);
			photoTags.add(newTag);
		}
		else {
			return;
		}
		
		// select the inserted item
		if(photoTags.size() != 0) {
			photoTagsListView.getSelectionModel().select(photoTags.size()-1);
		}
	}
	
	/**
	 * Deletes the selected Tag.
	 * @param e	Reference to ActionEvent
	 */
	public void deleteTag(ActionEvent e) {
		// get Photo object of selected Photo
		Photo selectedPhoto = photoListView.getSelectionModel().getSelectedItem();
		// get Tag object and index of selected Tag
		Tag selectedTag = photoTagsListView.getSelectionModel().getSelectedItem();
		if(selectedPhoto == null || selectedTag == null) {
			errorNotice("No Tag Error", "There is no tag selected, so the action cannot be performed.");
			return;
		}
		int index = photoTagsListView.getSelectionModel().getSelectedIndex();
		
		// ask User to confirm if they want to delete the selected Tag
		if(!deleteConfirmation(AdminController.getPrimaryStage(), "Delete Tag", selectedTag.getTagType() + " : " + selectedTag.getTagValue())) {
			return;
		}
		
		// remove selected Tag
		selectedPhoto.getTags().remove(index);
		photoTags.remove(index);
		
		// select the next item
		if(photoTags.size() != 0) {
			photoTagsListView.getSelectionModel().select(index);
		}
		
	}
	
	/**
	 * Creates a tag type for User which can be used among all of User's albums.
	 * @param e	Reference to ActionEvent
	 */
	public void createTag(ActionEvent e) {
		TextInputDialog dialog = new TextInputDialog();
		dialog.initOwner(primaryStage);
		dialog.setTitle("Create Tag");
		dialog.setContentText("Enter the new Tag Type:");
		
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent() && !result.get().trim().equals("")) {
			String newTagName = result.get().trim();
			ArrayList<String> existingTagTypes = getUser().getTagTypes();
			// check if tag type already exists
			for(int i = 0; i < existingTagTypes.size(); i++) {
				if(newTagName.toLowerCase().equals(existingTagTypes.get(i).toLowerCase())) {
					errorNotice("Existing Tag Type Error", "A tag type with the same name already exists.");
					return;
				}
			}
			
			getUser().getTagTypes().add(newTagName);
			tagTypes.add(newTagName);
		}
	}
	
	/**
	 * Changes Caption of Photo
	 * @param e	Reference to ActionEvent
	 */
	public void captioning(ActionEvent e) {
		// captioning a photo or recaptioning a photo
		Photo snap = photoListView.getSelectionModel().getSelectedItem(); // the photo in question
		int index = photoListView.getSelectionModel().getSelectedIndex();
		if (snap == null) { // checks to see if there is a photo selected or not!
			return; // wont let you caption unless you selected a photo
		}
		TextInputDialog dbox = new TextInputDialog();
		dbox.initOwner(primaryStage);
		dbox.setTitle("Captioning");
		dbox.setContentText("Enter the new caption for this photograph");
		Optional<String> cap = dbox.showAndWait(); 
		String temp = cap.get();
		String test = temp.trim(); // gets rid of blanks
		if (test.length() == 0) { // this means the user inserted blanks!
			snap.setCaption("No Caption");
		}
		else {
			snap.setCaption(cap.get());
		}
		
		photos.get(index).setCaption(snap.getCaption());
		photos.remove(index);
		photos.add(index, snap);
		photoListView.getSelectionModel().select(index);
		showItem(getPrimaryStage());
	}
	
	/**
	 * Goes to the previous photo in the list.
	 * @param e	Reference to ActionEvent
	 */
	public void previous (ActionEvent e) {
		// shows previous photo in album
		int cindex = photoListView.getSelectionModel().getSelectedIndex(); // get index of the current selected photo
		int pindex = cindex - 1; // gets index of previous photo
		if (pindex < 0) { // we are out of bounds!
			return;
		}
		photoListView.getSelectionModel().select(pindex);
	}
	
	/**
	 * Goes to the next photo in the list.
	 * @param e	Reference to ActionEvent
	 */
	public void next (ActionEvent e) {
		int sindex = photoListView.getSelectionModel().getSelectedIndex(); // get index of the current selected photo
		int nindex = sindex + 1; // gets index of next photo
		if (nindex > photos.size()) { // checks if we are out of bounds or not
			return;
		}
		photoListView.getSelectionModel().select(nindex);
	}
	
	/**
	 * Saves all changes made and returns User to selected User's Album.fxml View.
	 * @param e	Reference to ActionEvent
	 * @throws Exception	Used to throw Exception from FXMLLoader.
	 */
	public void back(ActionEvent e) throws Exception {
		getUser().writeUser();
		
		FXMLLoader loader = new FXMLLoader();   
		loader.setLocation( getClass().getResource("/view/Album.fxml"));
		AnchorPane root = (AnchorPane)loader.load();
		
		AlbumController albumController = loader.getController();
		albumController.start(primaryStage, user);
		
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
