package gedcom;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

public class GEDCOMCheckMarriageTest {
	private static GEDCOMReader gr;
	private static Date currentDate;
	
	@BeforeClass
	public static void before() throws Exception {
		gr = new GEDCOMReader( "resources/BrandonCheungSSW555Project01.ged" );
	}
	
	@Test
	public void test1() {
		assertNotNull( gr.getFile() );
	}
	
	@Test
	public void test2() {
		assertNotNull( gr.getIndividuals() );
	}
	
	@Test
	public void test3() {
		assertNotNull( gr.getFamilies() );
	}
	
	@Test
	public void test4() {
		List<String> errors = gr.checkMarriage();
		assertTrue( errors.size() > 0 );
	}
}
