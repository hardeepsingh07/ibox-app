package edu.csupomona.cs585.ibox;

import junit.framework.TestCase;
import org.junit.*;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.ArrayList;

import com.google.api.client.http.AbstractInputStreamContent;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.Drive.Files.*;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import edu.csupomona.cs585.ibox.sync.GoogleDriveFileSyncManager;

public class GoogleDriveFileSyncManagerTest extends TestCase {
	public Drive mockDrive;
	public GoogleDriveFileSyncManager gdfsm;
	public File sendingFile, sendingUFile;
	public java.io.File localFile;
	public Files files;
	public Insert insert;
	public Update update;
	public Delete delete;
	public List list;
	public FileList fileList;
	public ArrayList<File> listOfFile;
	public String fileName = "testingFile.rtf";
	public String fileID = "TF921";

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		mockDrive = mock(Drive.class);
		gdfsm = new GoogleDriveFileSyncManager(mockDrive);
		sendingFile = new File();
		sendingUFile = new File();
		localFile = mock(java.io.File.class);
		files = mock(Files.class);
		insert = mock(Insert.class);
		update = mock(Update.class);
		delete = mock(Delete.class);
		list = mock(List.class);
		fileList = new FileList();
		listOfFile = new ArrayList<File>();

		// make a mock file data
		sendingFile.setTitle(fileName);
		sendingFile.setId(fileID);
		listOfFile.add(sendingFile);
		fileList.setItems(listOfFile);
		//
	}

	public void testAdd() throws IOException {
		// mock method calls inside addFile method
		when(mockDrive.files()).thenReturn(files);
		when(files.insert(isA(File.class), isA(AbstractInputStreamContent.class))).thenReturn(insert);
		when(insert.execute()).thenReturn(sendingFile);
		
		gdfsm.addFile(localFile);
		
		// Have to verify atLeast(1) for localFile and mockDrive else error is thrown
		verify(mockDrive, atLeast(1)).files();
		verify(files).insert(isA(File.class), isA(AbstractInputStreamContent.class));
		verify(insert).execute();
	}

	public void testUpdate() throws IOException {
		// Mock the getFileId Method
		when(localFile.getName()).thenReturn(fileName);
		when(files.list()).thenReturn(list);
		when(list.execute()).thenReturn(fileList);

		// setUp mock for update method
		when(mockDrive.files()).thenReturn(files);
		when(files.update(isA(String.class), isA(File.class), isA(AbstractInputStreamContent.class)))
				.thenReturn(update);
		when(update.execute()).thenReturn(sendingUFile);

		gdfsm.updateFile(localFile);

		// Have to verify atLeast(1) for localFile and mockDrive else error is thrown
		verify(localFile, atLeast(1)).getName();
		verify(files).list();
		verify(list).execute();
		verify(mockDrive, atLeast(1)).files();
		verify(files).update(isA(String.class), isA(File.class), isA(AbstractInputStreamContent.class));
		verify(update).execute();

	}

	public void testDelete() throws IOException {
		// Mock the getFileId Method
		when(localFile.getName()).thenReturn(fileName);
		when(files.list()).thenReturn(list);
		when(list.execute()).thenReturn(fileList);

		// setUp mock for delete method
		when(mockDrive.files()).thenReturn(files);
		when(files.delete(isA(String.class))).thenReturn(delete);
		when(delete.execute()).thenReturn(null);

		gdfsm.deleteFile(localFile);

		// Have to verify atLeast(1) for localFile and mockDrive else error is thrown
		verify(localFile, atLeast(1)).getName();
		verify(files).list();
		verify(list).execute();
		verify(mockDrive, atLeast(1)).files();
		verify(files).delete(fileID);
		verify(delete).execute();
	}
}
