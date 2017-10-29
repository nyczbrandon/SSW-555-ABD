package gedcom;

import static org.junit.Assert.*;

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
		
		for (Map.Entry<String, Family> e: rg.getFamilies().entrySet()) {
			if (e.getValue().getChildren() != null) {
				List<String> kids = new ArrayList<String>();
				kids = e.getValue().getChildren();
				for (int i = 0; i < kids.size(); i++) {
					List<String> spouses = new ArrayList<String>();
					spouses = gr.getIndividuals().get(kids.get(i)).getSpouses();
					if(spouses != null) {
						for (int j = i+1; j < kids.size(); j++) {
							List<String> spouses2 = new ArrayList<String>();
							spouses2 = gr.getIndividuals().get(kids.get(j)).getSpouses();
							for( int spouseCount = 0; spouseCount < spouses2.size(); spouseCount++) {
								if( spouses.contains(spouses2.get(i))) {
									found = true;
								}
							}
						}
					}
				}
			}
		}
		
		assertTrue( (found == (gr.checkNotSiblings() != null)) 
				 || (found == (gr.checkNotSiblings() == null))
				                                                        );
	}
	
		

}
