package gedcom;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

public class GEDCOMCheckDateTest {
	private static GEDCOMReader gr;
	private static Date currentDate;
	
	@BeforeClass
	public static void before() throws Exception {
		gr = new GEDCOMReader( "resources/project01.ged" );
		currentDate = new Date();
		gr.checkDates();
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
		for ( Map.Entry<String, Individual> e: gr.getIndividuals().entrySet() ) {
			assertTrue( e.getValue().getBirthday().compareTo( currentDate ) <= 0 && ( e.getValue().getDeath() == null || e.getValue().getDeath().compareTo( currentDate ) <= 0 ) );
		}
	}
	
	@Test
	public void test5() {
		for ( Map.Entry<String, Family> e: gr.getFamilies().entrySet() ) {
			assertTrue( e.getValue().getMarried().compareTo( currentDate ) <= 0 && ( e.getValue().getDivorced() == null || e.getValue().getDivorced().compareTo( currentDate ) <= 0 ) );
		}
	}
}
