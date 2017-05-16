package notes;
import java.util.Iterator;
import org.apache.commons.cli.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class Application
{

private Database database;
private boolean debug;

public Application() throws IOException, SQLException
{
	this.database = new Database(Paths.get("/tmp", "NotesApplication"));
}

public Application(Path path) throws IOException, SQLException
{
	this.database = new Database(path);
}

public Application(Database db)
{
	this.database = db;
}

public void quit()
{
	this.database.close();
}

public void setDebug()
{
	this.debug = true;
}

public void run(String... args) throws ParseException, SQLException
{
	try {
		StringBuilder output = new StringBuilder();

		Options options = new Options();
		options.addOption("r", "read", true, "date to be applied");
		options.addOption("w", "write", true, "text to be write");
		options.addOption("e", "update", true, "update note");
		options.addOption("d", "delete", true, "delete note");
		options.addOption(null, "id", true, "note id");

		DefaultParser parser = new DefaultParser();
		CommandLine cli = parser.parse(options, args);

		if(cli.hasOption("write")) {
			write(cli.getOptionValue("write"));
		} else if(cli.hasOption("read")) {
			read(cli.getOptionValue("read"));
		} else if(cli.hasOption("update")) {
			if(cli.hasOption("id")) {
				update(Integer.parseInt(cli.getOptionValue("id")),
					cli.getOptionValue("update"));
			} else {
				throw new ParseException("id option is missing");
			}
		} else if(cli.hasOption("delete")) {
			delete(Integer.parseInt(cli.getOptionValue("delete")));
		}
	} catch(SQLException e) {
		printError("database error", e);
	} catch(ParseException e) {
		printError("option parsing error", e);
	} catch(Throwable e) {
		printError("unknown error", e);
	}
}

private void printError(String message, Throwable e)
{
	System.err.println(message + ": " + e.getLocalizedMessage());
	if(this.debug) {
		e = e.getCause();
		while(e != null) {
			System.err.println(e.getLocalizedMessage());
			e = e.getCause();
		}
	}
}

public void read(String arg) throws SQLException, ParseException
{
	ArrayList<Note> notes = null;

	if(arg.equals("all")) {
		notes = this.database.select();
	} else if(arg.equals("today")) {
		LocalDateTime begin = LocalDate.now().atStartOfDay();
		notes = this.database.select(begin, begin.plusDays(1));
	} else if(arg.equals("yesterday")) {
		LocalDateTime begin = LocalDate.now().atStartOfDay().minusDays(1);
		notes = this.database.select(begin, begin.plusDays(1));
	} else {
		LocalDateTime date = parseDate(arg);
		if(date != null) {
			notes = this.database.select(date, date.plusDays(1));
		}
	}

	if(notes == null) {
		throw new ParseException("failed to parse date to read");
	}

	if(notes.size() == 0) {
		throw new SQLException("not found notes at this date: " + arg);
	}

	for(Note n : notes) {
		System.out.println(n);
	}
}

private LocalDateTime parseDate(String arg)
{
	LocalDateTime date = null;

	try {
		date = LocalDate.parse(arg, DateTimeFormatter.ofPattern("yyyy/MM/dd")).atStartOfDay();
	} catch(DateTimeParseException e) {}

	return date;
}

public void write(String text) throws SQLException
{
	this.database.insert(LocalDateTime.now(), text);
}

public void update(int id, String text) throws SQLException
{
	if(!this.database.update(id, text)) {
		throw new SQLException("failed to update note: invalid id");
	}
}

public void delete(int id) throws SQLException
{
	if(!this.database.delete(id)) {
		throw new SQLException("failed to delete note: invalid id");
	}
}

}
