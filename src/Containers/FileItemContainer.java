package Containers;

public class FileItemContainer {
	private int fileId;
	private int authorId;
	private String date;
	private String title;
	private String ext;
	
	public FileItemContainer(int id, int author,String title,String ext, String date){
		this.fileId = id;
		this.authorId = author;
		this.date = date;
		this.title = title;
	}
	
	public int getFileId() {
		return fileId;
	}
	public int getAuthorId() {
		return authorId;
	}
	public String getTitle() {
		return title;
	}
	public String getDate() {
		return date;
	}
	public String getExt() {
		return ext;
	}
	public void setExt(String ext) {
		this.ext = ext;
	}
}
