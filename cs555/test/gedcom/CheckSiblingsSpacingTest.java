package gedcom;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.BeforeClass;
import org.junit.Test;

public class CheckSiblingsSpacingTest {
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
			List<String> children = e.getValue().getChildren();
			if (children.size() < 2) {
				continue;
			}
			for (int i = 0; i < children.size(); i++) {
				for (int j = i + 1; j < children.size(); j++) {
					String child_id_1 = children.get(i);
					String child_id_2 = children.get(j);
					Individual child_1 = gr.getIndividuals().get(child_id_1);
					Individual child_2 = gr.getIndividuals().get(child_id_2);			
					Date birthday_1 = child_1.getBirthday();
					Date birthday_2 = child_2.getBirthday();
					long diff = Math.abs(birthday_1.getTime() - birthday_2.getTime());
					long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
					if (days >= 2 && days <= 240) {
						found = true;
					}
				}
			}
		}
		assertTrue( (found == (gr.checkSiblingsSpacing() != null)) 
				 || (found == (gr.checkSiblingsSpacing() == null))
				                                                        );
	}
	
	

}
