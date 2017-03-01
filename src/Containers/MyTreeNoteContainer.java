package Containers;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class MyTreeNoteContainer {

	private SimpleStringProperty name;
	private SimpleIntegerProperty id;

	public MyTreeNoteContainer(int id, String title) {
		this.id = new SimpleIntegerProperty(id);
		this.name = new SimpleStringProperty(title);
	}

	public Integer getId() {
		return id.get();
	}

	public String getTitle() {
		return name.get();
	}
}