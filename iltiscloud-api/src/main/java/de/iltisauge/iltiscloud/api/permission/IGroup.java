package de.iltisauge.iltiscloud.api.permission;

public interface IGroup extends Comparable<IGroup> {
	
	/**
	 * 
	 * @return the name of the group.
	 */
	String getName();
	
	/**
	 * 
	 * @return the display name of the group.
	 */
	String getDisplayName();
	
	/**
	 * The sorting index is used to create a ranking of groups. <br>
	 * The group with the lowest index will be displayed before the others.
	 * @return the sorting index of this group.
	 */
	Integer getSortingIndex();

}
