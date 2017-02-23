package MyTree;

import javafx.scene.control.TreeItem;

public class MyTreeItem<T> extends TreeItem<T>{
	private Integer id;

	public void setId(Integer i){
		id = i;
	}
	public Integer getId(){
		return id;
	}
	public MyTreeItem(T a){
		super(a);
	}
}
