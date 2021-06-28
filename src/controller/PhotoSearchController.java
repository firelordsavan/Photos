package controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.time.LocalDate;
import java.time.ZoneId;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import app.Photos;
import model.Admin;
import model.Album;
import model.Photo;
import model.Tag;
import model.User;

/**
 * PhotoSearch Controller used to connect the PhotoSearch.fxml View to Models.
 * 
 * @author	Naveenan Yogeswaran
 * @author	Savan Patel
 *
 */

public class PhotoSearchController {
	
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
	@FXML ComboBox<String> tagTypeChoice1;
	/**
	 * Combo Box used to display available tag types for user to pick from when adding new tag.
	 */
	@FXML ComboBox<String> tagTypeChoice2;
	/**
	 * Combo Box used to display all conditionals.
	 */
	@FXML ComboBox<String> conditionalChoice;
	/**
	 * TextField to take in value for first Tag.
	 */
	@FXML TextField tagValue1;
	/**
	 * TextField to take in value for second Tag.
	 */
	@FXML TextField tagValue2;
	/**
	 * List View that is used to show list of tags belonging to displayed Photo.
	 */
	@FXML ListView<Tag> photoTagsListView;
	/**
	 * Used to take input from User about the From Date.
	 */
	@FXML DatePicker fromDate;
	/**
	 * Used to take input from User about the To Date.
	 */
	@FXML DatePicker toDate;
	
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
	 * ObservableList used to populate conditionalChoice and to keep track of new changes.
	 */
	ObservableList<String> conditionals;
	
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
	 * Contains list of photos that are currently in the photoListView.
	 */
	ArrayList<Photo> searchResults;
	
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
	 * Used to start the PhotoSearch.fxml View.
	 * @param mainStage	Reference to primaryStage used to display all our scenes.
	 * @param selectedUser	Reference to the selected User
	 */
	public void start(Stage mainStage, User selectedUser) {
		setPrimaryStage(mainStage);
		setUser(selectedUser);
		searchResults = new ArrayList<Photo>();
		
		// fill in ComboBox with user's tag types
		ArrayList<String> savedTagTypes = getUser().getTagTypes();
		tagTypes = FXCollections.observableArrayList(savedTagTypes);
		tagTypeChoice1.setItems(tagTypes);
		tagTypeChoice2.setItems(tagTypes);
		
		// fill in ComboBox with optional conditionals
		conditionals = FXCollections.observableArrayList("NONE", "AND", "OR");
		conditionalChoice.setItems(conditionals);
		
		// set default details for the display area
		caption.setText("");
		date.setText("");
		
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
	 * Search's through the User's Albums and Photos to find all Photos that fall within the provided date range.
	 */
	public void searchByDate() {
		// get LocalDate objects from selected Dates
		LocalDate selectedFromDate = fromDate.getValue();
		LocalDate selectedToDate = toDate.getValue();
		
		ArrayList<Photo> queryResult = new ArrayList<Photo>(); // Holds results of our query.
		
		// check that both dates were selected
		if(selectedFromDate == null || selectedToDate == null) {
			errorNotice("Invalid Input", "Please make sure to select both a \"From Date\" and a \"To Date\"");
			return;
		}
		
		// go through all of User's Album objects and Photo objects to find Photo objects that fall within the date range
		ArrayList<Album> userAlbums = getUser().getAlbums();
		// hashset to keep track of already added photos
		Set<Photo> addedPhotos = new HashSet<Photo>();
		for(Album album : userAlbums) {
			ArrayList<Photo> albumPhotos = album.getPhotos();
			
			for(Photo photo : albumPhotos) {
				// get the LocalDate of the Photo
				LocalDate photoDate = photo.getDate().getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				if(!addedPhotos.contains(photo) && photoDate.compareTo(selectedFromDate) >= 0 && photoDate.compareTo(selectedToDate) <= 0) {
					queryResult.add(photo);
					addedPhotos.add(photo);
				}
			}
		}
		
		// saved the query result for future reference
		searchResults = queryResult;
		// clear List Views in order to prepare it for re-population.
		photoListView.getItems().clear();
		photoTagsListView.getItems().clear();
		// set Observable List to our queried results
		photos = FXCollections.observableArrayList(queryResult);
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
		
		// select the first item if exists
		if(photos.size() > 0) {
			photoListView.getSelectionModel().select(0);
		}
	}
	
	/**
	 * Search for Photos given Tags and a conditional
	 * @param e	ActionEvent e
	 */
	public void searchByTags(ActionEvent e) {
		if(conditionalChoice.getValue() == null) {
			errorNotice("No Conditional Selected", "Please select \"NONE\", \"AND\", or \"OR\" using the dropdown before attempting to search by tag.");
			return;
		}
		else if(conditionalChoice.getValue().equals("NONE")) {
			searchByTagsNONE();
		}
		else if(conditionalChoice.getValue().equals("AND")) {
			searchByTagsAND();
		}
		else if(conditionalChoice.getValue().equals("OR")) {
			searchByTagsOR();
		}
		else {
			return;
		}
		
		// clear List Views in order to prepare it for re-population.
		photoListView.getItems().clear();
		photoTagsListView.getItems().clear();
		// set Observable List to our queried results
		photos = FXCollections.observableArrayList(searchResults);
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
		
		// select the first item if exists
		if(photos.size() > 0) {
			photoListView.getSelectionModel().select(0);
		}
	}
	
	/*
	 * Search for Photos given a specific Tag
	 */
	public void searchByTagsNONE() {
		if(tagTypeChoice1.getValue() == null) {
			errorNotice("No Tag Type Selected", "Please select a tag type using the first dropdown before attempting to search a tag.");
			return;
		}
		if(tagValue1.getText().trim().length() == 0) {
			errorNotice("No Tag Value", "Please type a tag value using the first textfield before attempting to search a tag.");
			return;
		}
		
		String type = tagTypeChoice1.getValue(); // Tag Type to check for
		String value = tagValue1.getText().trim().toLowerCase(); // Tag Value to check for
		ArrayList<Photo> queryResult = new ArrayList<Photo>(); // Holds results of our query.
		
		// go through all of User's Album objects and Photo objects to find Photo objects that fall within the date range
		ArrayList<Album> userAlbums = getUser().getAlbums();
		// hashset to keep track of already added photos
		Set<Photo> addedPhotos = new HashSet<Photo>();
		for(Album album : userAlbums) {
			ArrayList<Photo> albumPhotos = album.getPhotos();
			
			for(Photo photo : albumPhotos) {
				if(!addedPhotos.contains(photo)) {
					ArrayList<Tag> photoTags = photo.getTags();
					
					for(Tag tag : photoTags) {
						if(tag.getTagType().equals(type) && tag.getTagValue().toLowerCase().equals(value)) {
							queryResult.add(photo);
							addedPhotos.add(photo);
							break;
						}
					}
				}
			}
		}
		// saved the query result for future reference
		searchResults = queryResult;
	}
	
	/**
	 * Search for Photos that have both given Tags
	 */
	public void searchByTagsAND() {
		if(tagTypeChoice1.getValue() == null) {
			errorNotice("No Tag Type Selected", "Please select a tag type using the first dropdown before attempting to search a tag.");
			return;
		}
		if(tagValue1.getText().trim().length() == 0) {
			errorNotice("No Tag Value", "Please type a tag value using the first textfield before attempting to search a tag.");
			return;
		}
		if(tagTypeChoice2.getValue() == null) {
			errorNotice("No Tag Type Selected", "Please select a tag type using the second dropdown before attempting to search a tag.");
			return;
		}
		if(tagValue2.getText().trim().length() == 0) {
			errorNotice("No Tag Value", "Please type a tag value using the second textfield before attempting to search a tag.");
			return;
		}
		
		String type1 = tagTypeChoice1.getValue(); // Tag Type to check for
		String value1 = tagValue1.getText().trim().toLowerCase(); // Tag Value to check for
		String type2 = tagTypeChoice2.getValue(); // Tag Type to check for
		String value2 = tagValue2.getText().trim().toLowerCase(); // Tag Value to check for
		ArrayList<Photo> queryResult = new ArrayList<Photo>(); // Holds results of our query.
		
		// go through all of User's Album objects and Photo objects to find Photo objects that fall within the date range
		ArrayList<Album> userAlbums = getUser().getAlbums();
		// hashset to keep track of already added photos
		Set<Photo> addedPhotos = new HashSet<Photo>();
		for(Album album : userAlbums) {
			ArrayList<Photo> albumPhotos = album.getPhotos();
			
			for(Photo photo : albumPhotos) {
				if(!addedPhotos.contains(photo)) {
					ArrayList<Tag> photoTags = photo.getTags();
					boolean checkTag1 = false;
					boolean checkTag2 = false;
					
					for(Tag tag : photoTags) {
						if(tag.getTagType().equals(type1) && tag.getTagValue().toLowerCase().equals(value1)) {
							checkTag1 = true;
						}
						if(tag.getTagType().equals(type2) && tag.getTagValue().toLowerCase().equals(value2)) {
							checkTag2 = true;
						}
						if(checkTag1 && checkTag2) {
							queryResult.add(photo);
							addedPhotos.add(photo);
							break;
						}
					}
				}
			}
		}
		// saved the query result for future reference
		searchResults = queryResult;
	}
	
	/**
	 * Search for Photos that have either given Tags
	 */
	public void searchByTagsOR() {
		if(tagTypeChoice1.getValue() == null) {
			errorNotice("No Tag Type Selected", "Please select a tag type using the first dropdown before attempting to search a tag.");
			return;
		}
		if(tagValue1.getText().trim().length() == 0) {
			errorNotice("No Tag Value", "Please type a tag value using the first textfield before attempting to search a tag.");
			return;
		}
		if(tagTypeChoice2.getValue() == null) {
			errorNotice("No Tag Type Selected", "Please select a tag type using the second dropdown before attempting to search a tag.");
			return;
		}
		if(tagValue2.getText().trim().length() == 0) {
			errorNotice("No Tag Value", "Please type a tag value using the second textfield before attempting to search a tag.");
			return;
		}
		
		String type1 = tagTypeChoice1.getValue(); // Tag Type to check for
		String value1 = tagValue1.getText().trim().toLowerCase(); // Tag Value to check for
		String type2 = tagTypeChoice2.getValue(); // Tag Type to check for
		String value2 = tagValue2.getText().trim().toLowerCase(); // Tag Value to check for
		ArrayList<Photo> queryResult = new ArrayList<Photo>(); // Holds results of our query.
		
		// go through all of User's Album objects and Photo objects to find Photo objects that fall within the date range
		ArrayList<Album> userAlbums = getUser().getAlbums();
		// hashset to keep track of already added photos
		Set<Photo> addedPhotos = new HashSet<Photo>();
		for(Album album : userAlbums) {
			ArrayList<Photo> albumPhotos = album.getPhotos();
			
			for(Photo photo : albumPhotos) {
				if(!addedPhotos.contains(photo)) {
					ArrayList<Tag> photoTags = photo.getTags();
					
					for(Tag tag : photoTags) {
						if((tag.getTagType().equals(type1) && tag.getTagValue().toLowerCase().equals(value1)) || 
								(tag.getTagType().equals(type2) && tag.getTagValue().toLowerCase().equals(value2))) {
							queryResult.add(photo);
							addedPhotos.add(photo);
							break;
						}
					}
				}
			}
		}
		// saved the query result for future reference
		searchResults = queryResult;
	}
	
	/**
	 * Create a new Album from the Search Results
	 * @param e	Reference to ActionEvent
	 */
	public void createAlbumFromSearch(ActionEvent e) {
		ArrayList<Photo> cSearchResults = searchResults; // creates a copy of searchResults
		if(cSearchResults.size() == 0) { // this means the search query had no photo returned
			errorNotice("Can't create an empty album like this", "Find a search query that actually produces photos please!");
			return;			
		}
		
		TextInputDialog dialog = new TextInputDialog();
		dialog.initOwner(primaryStage);
		dialog.setTitle("Create Album from Search");
		dialog.setContentText("Enter name of new Album: ");
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
			
			Album searched = new Album(newAlbumName); // creates a new album
			
			for(Photo photo : cSearchResults) { // go through each photo of the searchResults copy
				searched.addPhoto(photo); // add each photo into our new album Searched
			}
			user.getAlbums().add(searched); // adds the new created album into the users Album list!
		}
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
