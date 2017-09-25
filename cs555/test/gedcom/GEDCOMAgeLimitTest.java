package gedcom;

import static org.junit.Assert.*;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

public class GEDCOMAgeLimitTest {
	private static GEDCOMReader gr;
	
	@BeforeClass
	public static void before() throws Exception {
		gr = new GEDCOMReader( "resources/project01.ged" );
	}
	
	@Test
	public void test() {
		assertNotNull( gr.getIndividuals() );
		assertNotNull( gr.getFamilies() );
		gr.setAgeLimit();
		for ( Map.Entry<String, Individual> e : gr.getIndividuals().entrySet() ) {
			assertTrue( e.getValue().getAge() < 150 );
		}
	}

}
