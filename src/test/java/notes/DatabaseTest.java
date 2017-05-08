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
	public void testSelect() throws SQLException
	{
		ArrayList<Note> notes = database.select(firstTimeStamp,
			thirdTimeStamp.plusHours(1));

		Assert.assertTrue("should select all notes", notes.size() == 3);

		notes = database.select(firstTimeStamp, thirdTimeStamp);
		Assert.assertTrue(notes.size() == 2);

		notes = database.select(thirdTimeStamp.plusHours(123),
			thirdTimeStamp.plusHours(124));
		Assert.assertTrue(notes.size() == 0);

		notes = database.select(secondTimeStamp, firstTimeStamp);
		Assert.assertTrue(notes.size() == 0);

		notes = database.select(firstTimeStamp, firstTimeStamp);
		Assert.assertTrue(notes.size() == 0);
	}

	@Test
	public void testSelectAll() throws SQLException
	{
		ArrayList<Note> notes = database.select();

		Assert.assertTrue(notes.size() == database.count());
	}

	@Test
	public void testSelectById() throws SQLException
	{
		Note note = database.select(1);

		Assert.assertTrue(note != null);
		Assert.assertTrue(note.getText().equals("first"));

		note = database.select(3);

		Assert.assertTrue(note != null);
		Assert.assertTrue(note.getText().equals("third"));

		note = database.select(4);

		Assert.assertTrue(note == null);
	}

	@Test
	public void testDelete() throws SQLException
	{
		database.insert(LocalDateTime.now(), "fourth");

		Assert.assertTrue(database.count() == 4);
		Assert.assertTrue(database.delete(4));
		Assert.assertTrue(database.count() == 3);
	}

	@Test
	public void testUpdate() throws SQLException
	{
		Note note = database.select(1);

		Assert.assertTrue(database.update(1, note.getTimeStamp(), "text changed"));
		Note updatedNote = database.select(1);

		Assert.assertTrue("must be different objects", note != updatedNote);

		Assert.assertTrue(note.getTimeStamp().equals(updatedNote.getTimeStamp()));
		Assert.assertFalse(note.getText().equals(updatedNote.getText()));

		Assert.assertTrue(database.update(1, note.getTimeStamp(), "first"));
	}
}

