package notes;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.apache.derby.jdbc.EmbeddedDriver;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.OutputStream;
import java.io.IOException;

public class Database
{

public static final OutputStream DEV_NULL = new OutputStream() {
	public void write(int x) throws IOException { }
	public void write(byte[] x) throws IOException { }
	public void write(byte[] x, int off, int len) throws IOException { }
	public void flush() throws IOException { }
};

private Connection con;
private Path path;
private Path database_path;

public Database() throws IOException, SQLException
{
	this(null);
}

public Database(Path pathToDatabase) throws IOException, SQLException
{
	if(pathToDatabase == null) {
		String home = System.getenv("HOME");
		path = Paths.get(home, ".jnotes");
		if(!Files.exists(path)) {
			path = Files.createDirectory(path);
		}
	} else {
		path = pathToDatabase;
	}

	database_path = Paths.get(path.toString(), "database");

	System.setProperty("derby.stream.error.file", "/dev/null");

	String constring = "jdbc:derby:" + database_path;
	if(!Files.exists(database_path)) {
		constring = constring + ";create=true";
	}

	Driver driver = new EmbeddedDriver();
	DriverManager.registerDriver(driver);
	con = DriverManager.getConnection(constring);
	con.setAutoCommit(false);
}

public void close()
{
	try {
		con.close();
		con = null;
		DriverManager.getConnection("jdbc:derby:;shutdown=true");
	} catch (SQLException ex) {
		if (((ex.getErrorCode() == 50000) &&
				("XJ015".equals(ex.getSQLState())))) {
		} else {
			System.err.println("Database did not shut down normally");
					   System.err.println(ex.getMessage());
		}
	}
}

@Override
protected void finalize()
{
	close();
}


}
