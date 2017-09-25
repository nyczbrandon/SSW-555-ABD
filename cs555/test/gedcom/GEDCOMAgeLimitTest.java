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
		gr.setAgeLimit();
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
		assertEquals( gr.AGE_LIMIT, 150);
	}
	
	@Test
	public void test5() {
		for ( Map.Entry<String, Individual> e : gr.getIndividuals().entrySet() ) {
			assertTrue( e.getValue().getAge() < 150 );
		}
	}
}
