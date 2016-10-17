package edu.csupomona.cs585.ibox;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.*;

import com.google.api.services.drive.Drive;

import edu.csupomona.cs585.ibox.sync.GoogleDriveFileSyncManager;
import edu.csupomona.cs585.ibox.sync.GoogleDriveServiceProvider;


public class GoogleDriveFileSyncManagerIntegrationTest {

	public static Drive drive;
	public static GoogleDriveFileSyncManager gdfsm;
	public static File myFile;
	public static String fileName, path;

	@BeforeClass
	public static void setUp() throws Exception{
		drive = GoogleDriveServiceProvider.get().getGoogleDriveClient();
		gdfsm = new GoogleDriveFileSyncManager(drive);
		path = "/Users/hardeepsingh/Desktop/DriveTestFile.txt";
		createFile();
		fileName = myFile.getName();
	}
	
	@Test
	public void testAdd() throws Exception {
		System.out.println("-----Testing File Add to Drive-----");
		try {
			gdfsm.addFile(myFile);
		} catch (Exception e) {
			System.out.println("File Add Failed...");
			System.out.println(e.getMessage());
		}
		String id = gdfsm.getFileId(fileName);
		System.out.println("File Add was Successful");
		assertNotNull(gdfsm.getFileId(fileName));
		System.out.println("File: " + fileName + " -- ID: " + id);
	}

	@Test
	public void testBupdate() {
		System.out.println("-----Testing File Update to Drive-----");
		String id = gdfsm.getFileId(fileName);
		assertNotNull(id);
		
		//For Testing make changes to the File
		try {
			makeChangeToFile();
			gdfsm.updateFile(myFile);
		} catch (Exception e) {
			System.out.println("File Update Failed...");
			System.out.println(e.getMessage());
		}
		System.out.println("File Update was Successful");
		assertNotNull(gdfsm.getFileId(fileName));
		System.out.println("File: " + fileName + " -- ID: " + id);
	}

	@Test
	public void testCdelete() {
		System.out.println("-----Testing File Delete to Drive-----");
		try {
			gdfsm.deleteFile(myFile);
		} catch (IOException e) {
			System.out.println("File Delete Failed...");
			System.out.println(e.getMessage());
		}
		String id = gdfsm.getFileId(fileName);
		assertNull(id);
		System.out.println("File Delete was Successful");		
	}

	public void makeChangeToFile() throws Exception {
		String data = " Adding Text from update testing method. Bye.";
		BufferedWriter writer = new BufferedWriter(new FileWriter(path, true));
		writer.write(data);
		writer.flush();
		writer.close();
		System.out.println("File Modified...");
	}
	
	public static void createFile() throws Exception {
		myFile = new File(path);
		
		//Write Content
		FileWriter writer = new FileWriter(myFile);
		writer.write("File Created: New file First Line");
		writer.close();
		System.out.println("File Created...");
	}
}
