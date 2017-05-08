package notes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Note implements Comparable<Note>
{
	private String text;
	private LocalDateTime timeStamp;
	private HashSet<String> tags;
	private int id;

	public Note(int id, String text)
	{
		this(id, text, LocalDateTime.now());
	}

	public Note(int id, String text, LocalDateTime timeStamp)
	{
		setText(text);
		setTimeStamp(timeStamp);
		this.id = id;
	}

	public String getText()
	{
		return this.text;
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

	public LocalDateTime getTimeStamp()
	{
		return this.timeStamp;
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

		Integer integer = new Integer(this.id);
		String idStr = integer.toString();

		StringBuilder builder = new StringBuilder(
			idStr.length() + 2 + timeStampStr.length() +
			this.text.length() + 2);

		builder.append(idStr);
		builder.append(") ");
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

	public int compareTo(int id)
	{
		return Integer.compare(this.id, id);
	}
}
