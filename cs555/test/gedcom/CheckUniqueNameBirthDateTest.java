package gedcom;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

public class CheckUniqueNameBirthDateTest {
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
		Set<String> name_set = new HashSet<>();
		Set<Date> birth_date_set = new HashSet<>();
		boolean found = false;
		for (Map.Entry<String, Individual> e : gr.individuals.entrySet()) {
			String name = e.getValue().getName();
			Date birth_date = e.getValue().getBirthday();
			if (name_set.add(name) == false) {
				found = true;
			}
			if (birth_date_set.add(birth_date) == false) {
				found = true;
			}
		}
		assertTrue( (found == (gr.checkUniqueNameBirthDate() != null)) 
				 || (found == (gr.checkUniqueNameBirthDate() == null))
				                                                        );
	}
}
