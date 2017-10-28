package gedcom;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

public class CheckNoMarriageToDescendantsTest {

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
		
		for (Map.Entry<String, Family> e: gr.getFamilies().entrySet()) {
			
			if (e.getValue().getDivorced() == null) {
				continue;
			}
			List<String> children = e.getValue().getChildren();
			
			String husband = e.getValue().getHusbandId();
			String husband_id = e.getValue().getHusbandId();
			List<String> spouses1 = gr.getIndividuals().get(husband_id).getSpouses();
		
			for (String child : children) {
				for (String spouse: spouses1) {
					if (child.equals(spouse)) {
						found = true;
					}
				}
			}
			
			String wife = e.getValue().getWifeId();
			String wife_id = e.getValue().getWifeId();
			List<String> spouses2 = gr.getIndividuals().get(wife_id).getSpouses();
			
			for (String child : children) {
				for (String spouse: spouses2) {
					if (child.equals(spouse)) {
						found = true;
					}
				}
			}
		
		}
		
		assertTrue( (found == (gr.checkNoMarriageToDescendants() != null)) 
				 || (found == (gr.checkNoMarriageToDescendants() == null))
				                                                        );
	}
	
		

}
