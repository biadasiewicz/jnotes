package notes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.Serializable;

public class Note implements Comparable<Note>, Serializable
{
	private static final long serialVersionUID = 1;
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

	public boolean isTagged(String tag)
	{
		if(tag.charAt(0) == '#') {
			tag = tag.substring(1);
		}

		return this.tags.contains(tag);
	}

	@Override
	public String toString()
	{
		String timeStampStr = this.timeStamp.format(
			DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));

		StringBuilder builder = new StringBuilder(
			timeStampStr.length() + this.text.length() + 2);

		builder.append(timeStampStr);
		builder.append("\n\t");
		builder.append(this.text);

		return builder.toString();
	}

	@Override
	public int compareTo(Note other)
	{
		return this.timeStamp.compareTo(other.timeStamp);
	}
}
