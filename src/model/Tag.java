package model;

import java.io.Serializable;

/**
 * Used to store information that is relevant to Tags.
 * 
 * @author Naveenan Yogeswaran
 * @author Savan Patel
 *
 */

public class Tag implements Serializable{
	
	/**
	 * Default serial version UID for serialization.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The type of the Tag.
	 */
	private String tagType;
	
	/**
	 * The value of the Tag.
	 */
	private String tagValue;
	
	/**
	 * Constructor for Tag.
	 * @param tagType	The type of the Tag.
	 * @param tagValue	The value of the Tag.
	 */
	public Tag(String tagType, String tagValue) {
		this.tagType = tagType;
		this.tagValue = tagValue;
	}

	/**
	 * Returns reference to tagType.
	 * @return	The type of the Tag.
	 */
	public String getTagType() {
		return tagType;
	}

	/**
	 * Sets the type of the Tag.
	 * @param tagType	The type of the Tag.
	 */
	public void setTagType(String tagType) {
		this.tagType = tagType;
	}

	/**
	 * Returns reference to tagValue.
	 * @return	The value of the Tag.
	 */
	public String getTagValue() {
		return tagValue;
	}

	/**
	 * Sets the value of the Tag.
	 * @param tagValue	The value of the Tag.
	 */
	public void setTagValue(String tagValue) {
		this.tagValue = tagValue;
	}
	
	/**
	 * Returns String representation of Tag for ListView purposes.
	 */
	public String toString() {
		return tagType + " : " + tagValue;
	}
}
