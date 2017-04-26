package notes;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Unit test for Note class
 */
public class NoteTest
        extends TestCase
{
	private Note note;

	@Override
	public void setUp()
	{
		this.note = new Note(
			"#tag1 # this #tag1 is #tag2 message #tag3 #i",
			java.time.LocalDateTime.now());
	}

        /**
         * Create the test case
         *
         * @param testName name of the test case
         */
        public NoteTest( String testName )
        {
                super( testName );
        }

        /**
         * @return the suite of tests being tested
         */
        public static Test suite()
        {
                return new TestSuite( NoteTest.class );
        }

        /**
         * test overrided equlas method
         */
        public void testEquals()
        {
		Note ref = this.note;

		assertTrue(this.note.equals(ref));

		ref.setTimeStamp(java.time.LocalDateTime.now());

		assertTrue(this.note.equals(ref));


		Note other = new Note("message");
		other.setTimeStamp(java.time.LocalDateTime.now().plusDays(5));

		assertFalse(other.equals(this.note));
        }

	/**
	 * test Note class compare
	 */
	public void testCompareTo()
	{
		Note first = new Note("msg");
		Note second = new Note("msg",
			java.time.LocalDateTime.now().plusHours(1));

		int check = first.compareTo(second);
		assertTrue(check == -1);
	}

	/**
	 * test tags
	 */
	public void testTags()
	{
		String tag1 = "#tag1";
		String tag2 = "#tag2";
		String tag3 = "#tag3";

		StringBuilder builder = new StringBuilder();
		builder.append("this ");
		builder.append(tag1);
		builder.append(" is a");
		builder.append(tag2);
		builder.append(" message ");
		builder.append(tag3);

		this.note = new Note(builder.toString());

		assertTrue(this.note.isTagged(tag1));
		assertTrue(this.note.isTagged(tag2));
		assertTrue(this.note.isTagged(tag3));
	}
}

