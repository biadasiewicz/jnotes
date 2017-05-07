package notes;

import org.junit.*;

import java.sql.*;
import org.apache.derby.jdbc.EmbeddedDriver;

import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class DatabaseTest
{
	private static Path path;
	private static Database database;
	private static LocalDateTime firstTimeStamp;
	private static LocalDateTime secondTimeStamp;
	private static LocalDateTime thirdTimeStamp;

	@BeforeClass
	public static void initPath() throws IOException, SQLException
	{
		path = Files.createTempDirectory("DatabaseTest");
		database = new Database(path);
		thirdTimeStamp = LocalDateTime.now();
		secondTimeStamp = thirdTimeStamp.minusHours(1);
		firstTimeStamp = thirdTimeStamp.minusHours(2);

		insert();
	}

	private static void insert() throws SQLException
	{
		database.insert(firstTimeStamp, "first");
		database.insert(secondTimeStamp, "second");
		database.insert(thirdTimeStamp, "third");
	}

	@Test
        public void testInitialization() throws IOException, SQLException
        {
		Database db = new Database(path);
		db.close();
		Assert.assertTrue(true);

		db = new Database(path);
		db.close();
		Assert.assertTrue(true);

		database = new Database(path);
        }

	@Test
	public void testInsert() throws SQLException, IOException
	{
		int count = database.count();
		Assert.assertTrue(count == 3);
	}

	@Test
	public void testFind() throws SQLException
	{
		ArrayList<Note> notes = database.find(firstTimeStamp,
			thirdTimeStamp.plusHours(1));

		Assert.assertTrue("should find all notes", notes.size() == 3);

		notes = database.find(firstTimeStamp, thirdTimeStamp);
		Assert.assertTrue(notes.size() == 2);

		notes = database.find(thirdTimeStamp.plusHours(123),
			thirdTimeStamp.plusHours(124));
		Assert.assertTrue(notes.size() == 0);

		notes = database.find(secondTimeStamp, firstTimeStamp);
		Assert.assertTrue(notes.size() == 0);

		notes = database.find(firstTimeStamp, firstTimeStamp);
		Assert.assertTrue(notes.size() == 0);
	}
}

