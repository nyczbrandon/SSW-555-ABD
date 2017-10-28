package gedcom;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

public class checkMarriageGenderRolesTest {

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

			if (husband != null && !(husband.getGender().equals("M")) ){
				String husband_name = husband.getName();
				found = true;
			}

			if (wife != null && !(wife.getGender().equals("F"))) {
				String wife_name = wife.getName();
				found = true;
			}
		}
		assertTrue(found == (gr.checkMarriageGenderRoles() != null) || found == (gr.checkMarriageGenderRoles() == null));
		
	}
	
	
}
