package husaccttest.graphics;

import static org.junit.Assert.assertEquals;
import husacct.graphics.util.helpers.PathHelper;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class PathHelperTest {

	@Before
	public void setup() {
	}
	
	@Test
	public void createCombinedPathHelper(){
		ArrayList<String> paths = new ArrayList<String>();
		paths.add("a");
		paths.add("b");
		assertEquals("a+b", PathHelper.createCombinedPathHelper(paths));
		paths.add("c");
		assertEquals("a+b+c", PathHelper.createCombinedPathHelper(paths));
	}
	
	@Test
	public void createCombinedPathHelperForRoot(){
		ArrayList<String> paths = new ArrayList<String>();
		assertEquals("", PathHelper.createCombinedPathHelper(paths));
		
		paths.add("");
		assertEquals("", PathHelper.createCombinedPathHelper(paths));
	}
}
