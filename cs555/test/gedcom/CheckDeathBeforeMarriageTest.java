package gedcom;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

public class CheckDeathBeforeMarriageTest {
	private static GEDCOMReader gr;
	private static Date currentDate;
	
	@BeforeClass
	public static void before() throws Exception {
		gr = new GEDCOMReader( "resources/DaweiSunSSW555Project01.ged" );
		currentDate = new Date();
		gr.checkDates();
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
	
	public void test4() {
		boolean found = false;
		for (Map.Entry<String, Individual> e: gr.getIndividuals().entrySet()) {
			if (e.getValue().isAlive() == false) {
				Date death_date = e.getValue().getDeath();
				String id = e.getValue().getId();
				for (Map.Entry<String, Family> e2: gr.getFamilies().entrySet()) {
					if (e2.getValue().getHusbandId().equals(id) || e2.getValue().getWifeId().equals(id)) {
						Date marriage_date = e2.getValue().getMarried();
						if (death_date.compareTo(marriage_date) == -1) {
							found = true;
						}
					}
				}
			}
		}
		assertTrue(found == (gr.checkDeathBeforeMarriage() != null) 
				 || found == (gr.checkDeathBeforeMarriage() == null)
				 												    ); 
	}



}
