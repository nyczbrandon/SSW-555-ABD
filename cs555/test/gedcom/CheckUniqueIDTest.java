package gedcom;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

public class CheckUniqueIDTest {

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
		Set<String> family_set = new HashSet<>();
		for (Map.Entry<String, Family> e: gr.getFamilies().entrySet()) {
			String family_id = e.getValue().getId();
			if (family_set.add(family_id) == false) {
				found = true;
			}
		}
		Set<String> individual_set = new HashSet<>();
		for (Map.Entry<String, Individual> e : gr.getIndividuals().entrySet()) {
			String individual_id = e.getValue().getId();
			if (individual_set.add(individual_id) == false) {
				found = true;
			}
		}
		assertTrue( (found == (gr.checkUniqueID() != null)) 
				 || (found == (gr.checkUniqueID() == null))
				                                                        );
	}
}
