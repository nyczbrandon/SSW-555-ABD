package gedcom;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

public class CheckDivorceBeforeDeathTest {
	private static GEDCOMReader gr;
	
	@BeforeClass
	public static void before() throws Exception {
		gr = new GEDCOMReader( "resources/DaweiSunSSW555Project01.ged" );
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
	
	@Test
	public void test4() {
		boolean found = false;
		for (Map.Entry<String, Family> e: gr.getFamilies().entrySet()) {
			Date divorce_date = e.getValue().getDivorced();
			if (divorce_date != null) {
				String husband_name = e.getValue().getHusbandName();
				String wife_name = e.getValue().getWifeName();
				for (Map.Entry<String, Individual> e2: gr.getIndividuals().entrySet()) {
					if (e2.getValue().getName().equals(husband_name)) {
						Date death_date = e2.getValue().getDeath();
						if (death_date != null && divorce_date.compareTo(death_date) == -1) {
							found = true;
						}
					}
					else if (e2.getValue().getName().equals(wife_name)) {
						Date death_date = e2.getValue().getDeath();
						if (death_date != null && divorce_date.compareTo(death_date) == -1) {
							found = true;
						}
					}
				}
			}
		}
		assertTrue(found == (gr.checkDivorceBeforeDeath() != null) 
				 || found == (gr.checkDivorceBeforeDeath() == null)
				 												    ); 
		
	}
	
	

}
