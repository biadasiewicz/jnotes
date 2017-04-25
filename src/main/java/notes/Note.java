package notes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.Serializable;

public class Note implements Comparable<Note>, Serializable
{
	private String text;
	private LocalDateTime timeStamp;
	private HashSet<String> tags;

	public Note()
	{
		this(null, null);
	}

	public Note(String text)
	{
		this(text, LocalDateTime.now());
	}

	public Note(String text, LocalDateTime timeStamp)
	{
		setText(text);
		setTimeStamp(timeStamp);
	}

	public void setText(String text)
	{
		Pattern pattern = Pattern.compile("#(\\S+)");
		Matcher mat = pattern.matcher(text);

		if(this.tags == null) {
			this.tags = new HashSet<String>();
		} else {
			this.tags.clear();
		}

		while(mat.find()) {
			this.tags.add(mat.group(1));
		}

		this.text = text;
	}

	public void setTimeStamp(LocalDateTime time)
	{
		this.timeStamp = time;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(obj == null) {
			return false;
		}

		if(!Note.class.isAssignableFrom(obj.getClass())) {
			return false;
		}


		final Note other = (Note)obj;

		if((this.text == null) ?
			(other.text != null) :
			!this.text.equals(other.text))
		{
			return false;
		}

		if((this.timeStamp == null) ?
			(other.timeStamp != null) :
			!this.timeStamp.equals(other.timeStamp))
		{
			return false;
		}

		if((this.tags == null) ?
			(other.tags != null) :
			!this.tags.equals(other.tags))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int hash = 3;
		hash += 53 * hash + (this.text != null ? this.text.hashCode() : 0);
		hash += 53 * hash + (this.timeStamp != null ? this.timeStamp.hashCode() : 0);
		hash += 53 * hash + (this.tags != null ? this.tags.hashCode() : 0);
		return hash;
	}

	@Override
	public int compareTo(Note other)
	{
		return this.timeStamp.compareTo(other.timeStamp);
	}
}
