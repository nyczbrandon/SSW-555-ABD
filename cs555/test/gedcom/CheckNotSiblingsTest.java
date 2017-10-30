package gedcom;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

public class CheckNotSiblingsTest {

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
		for (Map.Entry<String, Family> e: gr.getFamilies().entrySet()) {
			String husband_id = e.getValue().getHusbandId();
			String wife_id = e.getValue().getWifeId();
			Individual husband = gr.getIndividuals().get(husband_id);
			Individual wife = gr.getIndividuals().get(wife_id);
			List<String> husbandFamily = husband.getChildren();
			List<String> wifeFamily = wife.getChildren();
			if(husbandFamily != null && wifeFamily != null){
				for(String hfam: husbandFamily) {
					for(String wfam: wifeFamily) {
						if(hfam.equals(wfam)){
							found = true;
						}
					}
				}
			}
		}
		
		assertTrue( found == (gr.checkNotSiblings() != null) );
	}
	
		

}
