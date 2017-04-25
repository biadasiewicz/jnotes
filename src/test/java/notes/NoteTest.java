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


		Note other = new Note("message2");

		assertFalse(other.equals(note));
        }

	/**
	 * test Note class as a hashSet Element
	 */
	public void testHashSetElement()
	{
		ArrayList<Note> arr = new ArrayList<Note>();
		arr.add(new Note("m1"));
		arr.add(new Note("m2"));
		Note note = new Note("m3");
		arr.add(note);
		arr.add(note);
		arr.add(new Note(new String("m1")));

		int arr_size = arr.size();
		int unique_size = arr_size - 1;

		for(int i = 0; i < 30; ++i) {
			HashSet<Note> set = new HashSet<Note>();

			assertTrue(set.add(arr.get(0)));
			assertTrue(set.add(arr.get(1)));
			assertTrue(set.add(arr.get(2)));
			assertFalse(set.add(arr.get(3)));
			assertTrue(set.add(arr.get(4)));

			assertTrue(set.size() != arr_size);
			assertTrue(set.size() == unique_size);
		}
	}
}

