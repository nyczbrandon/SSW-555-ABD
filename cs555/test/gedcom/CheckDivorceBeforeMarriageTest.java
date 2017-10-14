package gedcom;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

public class CheckDivorceBeforeMarriageTest {
	private static GEDCOMReader gr;
	
	@BeforeClass
	public static void before() throws Exception {
		gr = new GEDCOMReader( "resources/DaweiSunSSW555Project01.ged" );
		gr.setAgeLimit();
	}
	
	@Test
	public void test1() {
		assertNotNull(gr.getFile());
	}
	
	@Test
	public void test2() {
		assertNotNull(gr.getIndividuals());
	}
	
	@Test
	public void test3() {
		assertNotNull(gr.getFamilies());
	}
	
	
	@Test
	public void test4() {
		boolean found = false;
		for (Map.Entry<String, Family> e : gr.getFamilies().entrySet()) {
			Date divorce_date = e.getValue().getDivorced();
			Date marriage_date = e.getValue().getMarried();
			if (divorce_date != null && divorce_date.compareTo(marriage_date) == -1) {
				found = true;
			}
		}
		assertTrue( (found == (gr.checkDivorceBeforeMarriage() != null)) 
				 || (found == (gr.checkDivorceBeforeMarriage() == null))
				                                                        );
	}

}
