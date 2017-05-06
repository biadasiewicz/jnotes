package notes;

import org.junit.*;

import java.sql.*;
import org.apache.derby.jdbc.EmbeddedDriver;

import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;

/**
 * Unit test for simple App.
 */
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
}
