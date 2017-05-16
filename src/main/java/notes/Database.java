package notes;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Statement;
import java.sql.PreparedStatement;
import org.apache.derby.jdbc.EmbeddedDriver;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.OutputStream;
import java.io.IOException;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Database
{

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

	boolean exists = Files.exists(database_path);
	if(!exists) {
		constring = constring + ";create=true";
	}

	Driver driver = new EmbeddedDriver();
	DriverManager.registerDriver(driver);
	con = DriverManager.getConnection(constring);

	if(!exists) {
		String createSQL = "create table notes ( "
				+ "id integer not null generated always as"
				+ " identity (start with 1, increment by 1), "
				+ "time_stamp timestamp not null, msg varchar(255) not null, "
				+ "constraint primary_key primary key (id) )";

		Statement st = con.createStatement();
		st.execute(createSQL);
	}
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

public void insert(LocalDateTime timeStamp, String text) throws SQLException
{
	try {
		PreparedStatement pst = con.prepareStatement(
			"insert into notes (time_stamp, msg) values (?, ?)");
		pst.setTimestamp(1, Timestamp.valueOf(timeStamp));
		pst.setString(2, text);

		pst.executeUpdate();

	} catch(SQLException e) {
		throw new SQLException("failed to write new note", e);
	}
}

public int count()
{
	try {
		Statement st = con.createStatement();
		ResultSet result = st.executeQuery("select count(*) from notes");

		int count = 0;
		while(result.next()) {
			count = result.getInt(1);
		}

		return count;
	} catch(SQLException e) {
		return -1;
	}
}

public ArrayList<Note> select(LocalDateTime start,
				LocalDateTime end) throws SQLException
{
	try {
		PreparedStatement pst = con.prepareStatement(
			"select * from notes" +
			" where time_stamp >= ? and time_stamp < ?");
		pst.setTimestamp(1, Timestamp.valueOf(start));
		pst.setTimestamp(2, Timestamp.valueOf(end));

		ResultSet result = pst.executeQuery();

		ArrayList<Note> notes = new ArrayList<Note>();
		while(result.next()) {
			int id = result.getInt("id");
			LocalDateTime ts = result.getTimestamp(
				"time_stamp").toLocalDateTime();
			String text = result.getString("msg");
			notes.add(new Note(id, text, ts));
		}

		return notes;

	} catch(SQLException e) {
		throw new SQLException(
			"failed to select notes from date range", e);
	}
}

public ArrayList<Note> select() throws SQLException
{
	try {
		PreparedStatement pst = con.prepareStatement(
			"select * from notes");

		ResultSet result = pst.executeQuery();

		ArrayList<Note> notes = new ArrayList<Note>();
		while(result.next()) {
			int id = result.getInt("id");
			LocalDateTime ts = result.getTimestamp(
				"time_stamp").toLocalDateTime();
			String text = result.getString("msg");
			notes.add(new Note(id, text, ts));
		}

		return notes;

	} catch(SQLException e) {
		throw new SQLException("failed to query all notes", e);
	}
}

public Note select(int id) throws SQLException
{
	try {
		PreparedStatement pst = con.prepareStatement(
			"select * from notes where id = ?");
		pst.setInt(1, id);

		Note note = null;

		ResultSet result = pst.executeQuery();
		if(result.next()) {
			note = new Note(
				result.getInt("id"),
				result.getString("msg"),
				result.getTimestamp("time_stamp").toLocalDateTime());
		}

		return note;

	} catch(SQLException e) {
		throw new SQLException("failed to query note", e);
	}
}

public boolean delete(int id) throws SQLException
{
	try {
		PreparedStatement pst = con.prepareStatement(
			"delete from notes where id = ?");
		pst.setInt(1, id);

		return pst.executeUpdate() > 0;
	} catch(SQLException e) {
		throw new SQLException("failed to remove note", e);
	}
}

public boolean update(int id, LocalDateTime timeStamp, String text) throws SQLException
{
	try {
		PreparedStatement pst = con.prepareStatement(
			"update notes " +
			"set time_stamp = ?, msg = ?" +
			" where id = ?");

		pst.setTimestamp(1, Timestamp.valueOf(timeStamp));
		pst.setString(2, text);
		pst.setInt(3, id);

		return pst.executeUpdate() > 0;

	} catch(SQLException e) {
		throw new SQLException("failed to update note", e);
	}
}

public boolean update(int id, String text) throws SQLException
{
	try {
		PreparedStatement pst = con.prepareStatement(
			"update notes " +
			"set msg = ? " +
			"where id = ?");

		pst.setString(1, text);
		pst.setInt(2, id);

		return pst.executeUpdate() > 0;
	} catch(SQLException e) {
		throw new SQLException("failed to update note text", e);
	}
}

}
