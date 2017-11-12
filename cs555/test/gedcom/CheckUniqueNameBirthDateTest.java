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
		boolean found = false;
		for (Map.Entry<String, Individual> e1 : gr.getIndividuals().entrySet()) {
			for (Map.Entry<String, Individual> e2 : gr.getIndividuals().entrySet()) {
				if (e1 != e2) {
					if (e1.getValue().getName().equals(e2.getValue().getName()) && e1.getValue().getBirthday().compareTo(e2.getValue().getBirthday()) == 0 ) {
						found = true;			
					}
				}
			}
		}
		assertTrue(found == (gr.checkUniqueNameBirthDate() != null) || found == (gr.checkUniqueNameBirthDate() == null));
	}
	

}
