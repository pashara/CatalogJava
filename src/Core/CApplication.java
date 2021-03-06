package Core;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.apache.log4j.Logger;

import com.devcolibri.logpack.OrderLogic;

import Controllers.LoginController;
import Controllers.MainGridController;
import Models.MailModel;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;




public class CApplication {
	
	
	private static final Logger log = Logger.getLogger(OrderLogic.class);
	
	public static MailModel tlsSender = new MailModel("serial-i@ya.ru", "*******");
	
	
	
	public static boolean isItImage(String ext){
		String[] exts = {
				"JPG", "PNG","BMP","JPEG","GIF"
		};
		return Arrays.asList(exts).contains(ext.toUpperCase());
	}
	public static Stage stage;
	public static String LastFilePath = "NULL";
	public static boolean debug = true;
	public static String getTimestampString() {
		return new String(String.valueOf(ZonedDateTime.now().toInstant().toEpochMilli()));
	}



	public static void alertError(String TEXT){
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("������!");
		alert.setHeaderText("��������� ������:");
		alert.setContentText(TEXT);	
		alert.showAndWait();
		System.out.println(TEXT);
	}
	public static void alert(String TEXT){
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("����������");
		alert.setHeaderText("");
		alert.setContentText(TEXT);	
		alert.showAndWait();
		System.out.println(TEXT);
	}
	public static void openFile(File file) {
		try {
			Desktop.getDesktop().open(file);
		} catch (IOException ex) {
			CApplication.log.info("Open file "+file);
		}
	}
	
	public static void run() {
		CController loginForm = new LoginController();
		loginForm.run();
	}
	public static int daysBeetwen(){
		String string = "January 2, 2010";
		DateFormat format = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
		try {
			Date date = format.parse(string);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		Date dNow = new Date( );
	      SimpleDateFormat ft = 
	      new SimpleDateFormat ("E yyyy.MM.dd 'at' hh:mm:ss a zzz");
	      ft.format(dNow);  
	      
	      
	      SimpleDateFormat myFormat = new SimpleDateFormat("dd MM yyyy hh:mm:ss");
	      String inputString1 = "23 01 1997 19:00:00";
	      String inputString2 = "24 01 1997 00:00:00";

	      try {
	          Date date1 = myFormat.parse(inputString1);
	          Date date2 = myFormat.parse(inputString2);
	          long diff = date2.getTime() - date1.getTime();
	          System.out.println ("Days: " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
	      } catch (ParseException e) {
	          e.printStackTrace();
	      }
	      
	      
	      
		return 1;
	}

}
