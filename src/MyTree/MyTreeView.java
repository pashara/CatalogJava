package MyTree;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class MyTreeView<T> extends TreeView<T>{
	public MyTreeView(MyTreeItem<String> el){
		super((TreeItem<T>) el);
	}
	
	/*public final ObjectProperty<MultipleSelectionModel<MyTreeItem<T>>> selectionModelProperty() {
		if (selectionModel == null) {
			selectionModel = new SimpleObjectProperty<MultipleSelectionModel<MyTreeItem<T>>>(this, "selectionModel");
		}
		return selectionModel;
     }*/
	//getSelectionModel(){}
}
