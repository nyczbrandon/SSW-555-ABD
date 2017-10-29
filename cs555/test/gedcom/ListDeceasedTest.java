package gedcom;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

public class ListDeceasedTest {

private static GEDCOMReader gr;
	
	@BeforeClass
	public static void before() throws Exception {
		gr = new GEDCOMReader( "resources/project01.ged" );
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
		
		for (Map.Entry<String, Individual> e: gr.getIndividuals().entrySet()) {
			if(e.getValue().isAlive() == false) {
				found = true;
			}
		}
		
		assertTrue( (found == (gr.listDeceased() != null)) 
				 || (found == (gr.listDec() == null))
				                                                        );
	}
	
		

}
