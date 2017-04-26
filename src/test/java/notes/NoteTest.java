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
		Note note = new Note("message");
		Note ref = note;

		assertTrue(note.equals(ref));

		ref.setTimeStamp(java.time.LocalDateTime.now());

		assertTrue(note.equals(ref));


		Note other = new Note("message");
		other.setTimeStamp(java.time.LocalDateTime.now().plusDays(5));

		assertFalse(other.equals(note));
        }

	/**
	 * test Note class as a hashSet Element
	 */
	public void testHashSetElement()
	{
		Note first = new Note("msg");
		Note second = new Note("msg",
			java.time.LocalDateTime.now().plusHours(1));

		int check = first.compareTo(second);
		assertTrue(check == -1);
	}
}

