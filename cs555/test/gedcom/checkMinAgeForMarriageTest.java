package gedcom;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

public class checkMinAgeForMarriageTest {

	private static GEDCOMReader gr;
	
	@BeforeClass
	public static void before() throws Exception {
		gr = new GEDCOMReader( "project01.ged" );
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
			String husband_id = e.getValue().getHusbandId();
			String wife_id = e.getValue().getWifeId();
			Individual husband = gr.getIndividuals().get(husband_id);
			Individual wife = gr.getIndividuals().get(wife_id);

			Calendar today = Calendar.getInstance();
			Calendar marriage_date = Calendar.getInstance();
			int married_years = 0;
			marriage_date.setTime( e.getValue().getMarried());
			if (today.get( Calendar.MONTH ) > marriage_date.get( Calendar.MONTH ) ) {
				married_years = today.get( Calendar.YEAR ) - marriage_date.get( Calendar.YEAR);
			} else {
				married_years = today.get(Calendar.YEAR) - marriage_date.get( Calendar.YEAR ) - 1;
			}

			if (husband != null && husband.isAlive() == true) {
				String husband_name = husband.getName();
				if (14 > husband.getAge() - married_years) {
					found = true;
				}
			} else if(husband != null && husband.isAlive() == false) {
				String husband_name = husband.getName();
				Calendar death_date = Calendar.getInstance();
				death_date.setTime( husband.getDeath());
				int been_dead = 0;
				if (today.get( Calendar.MONTH ) > death_date.get( Calendar.MONTH ) ){
					been_dead = today.get( Calendar.YEAR ) - death_date.get( Calendar.YEAR);
				} else {
					been_dead = today.get(Calendar.YEAR) - death_date.get( Calendar.YEAR ) - 1;
				}
				if(14 > (husband.getAge() - married_years + been_dead) ) {
					found = true;
				}
			}

			if (wife != null && wife.isAlive() == true) {
				String wife_name = wife.getName();
				if (14 > wife.getAge() - married_years) {
					found = true;
				}
			} else if(wife != null && wife.isAlive() == false) {
				String wife_name = wife.getName();
				Calendar death_date = Calendar.getInstance();
				death_date.setTime( wife.getDeath());
				int been_dead = 0;
				if (today.get( Calendar.MONTH ) > death_date.get( Calendar.MONTH ) ){
					been_dead = today.get( Calendar.YEAR ) - death_date.get( Calendar.YEAR);
				} else {
					been_dead = today.get(Calendar.YEAR) - death_date.get( Calendar.YEAR ) - 1;
				}
				if(14 > (wife.getAge() - married_years + been_dead) ) {
					found = true;
				}
			}			
		}
		assertTrue(found == (gr.checkMinAgeForMarriage() != null) || found == (gr.checkMinAgeForMarriage() == null));
		
	}
	
	
}
