package notes;

import org.junit.*;

import java.sql.*;
import org.apache.derby.jdbc.EmbeddedDriver;

import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;

import java.time.LocalDateTime;

public class DatabaseTest
{
	private static Path path;

	@BeforeClass
	public static void initPath() throws IOException
	{
		path = Files.createTempDirectory("DatabaseTest");
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
        }

	@Test
	public void testInsert() throws SQLException, IOException
	{
		Database db = null;
		try {
			db = new Database(path);

			db.insert(LocalDateTime.now().minusHours(2), "first");
			db.insert(LocalDateTime.now().minusHours(1), "second");
			db.insert(LocalDateTime.now(), "third");

			int count = db.count();
			Assert.assertTrue(count == 3);
		} finally {
			db.close();
		}
	}
}
