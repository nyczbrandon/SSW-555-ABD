package gedcom;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

public class CheckBirthBeforeDeathofParentsTest {

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
	
	@Test
	public void test4() {
		boolean found = false;
		for (Map.Entry<String, Family> e : gr.getFamilies().entrySet()) {
			String husband_id = e.getValue().getHusbandId();
			String wife_id = e.getValue().getWifeId();
			List<String> children = e.getValue().getChildren();
			Individual husband = gr.getIndividuals().get(husband_id);
			Individual wife = gr.getIndividuals().get(wife_id);
			
			if (husband != null && husband.isAlive() == false) {
				Date husband_death_date = husband.getDeath();
				for (String child_id : children) {
					Individual child = gr.getIndividuals().get(child_id);
					if (child != null) {
						if (child.getBirthday().compareTo(husband_death_date) == 1) {
							found = true;
						}
					}
					
				}
			}
			if (wife != null && wife.isAlive() == false) {
				Date wife_death_date = wife.getDeath();
				for (String child_id : children) {
					Individual child = gr.getIndividuals().get(child_id);
					if (child != null) {
						if (child.getBirthday().compareTo(wife_death_date) == 1) {
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
