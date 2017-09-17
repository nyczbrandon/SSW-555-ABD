package gedcom;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GEDCOMReader {
	private File gedcomFile;
	public static final List GEDCOM_TAGS = new ArrayList<String>(Arrays.asList( "INDI", "NAME", "SEX", "BIRT", "DEAT", "FAMC", "FAMS", "FAM", "MARR", "HUSB", "WIFE", "CHIL", "DIV", "DATE", "HEAD", "TRLR", "NOTE" ) );
	
	public GEDCOMReader( String gedcomFile ) {
		this.gedcomFile = new File( gedcomFile );
	}
	
	public void printGEDCOMFile() throws Exception {
		BufferedReader br = new BufferedReader( new FileReader( gedcomFile ) );
		File outputFile = new File( "resources/output.txt" );
		if ( !outputFile.exists() ) {
			outputFile.createNewFile();
		}
		BufferedWriter bw = new BufferedWriter( new FileWriter( outputFile.getAbsoluteFile() ) );
		String line;
		while ( ( line = br.readLine() ) != null ) {
			bw.write( "--> " + line );
			bw.newLine();
			List split = new ArrayList<String>( Arrays.asList( line.split( " ", 3 ) ) );
			String format;
			if ( split.size() == 3 && ( split.get(2).equals( "INDI" ) || split.get(2).equals( "FAM" ) ) ) {
				Collections.swap( split, 1, 2 );
				split.add( 2, "Y" );
			} else {
				if ( GEDCOM_TAGS.contains( split.get( 1 ) ) ) {
					split.add( 2, "Y" );
				} else {
					split.add( 2, "N" );
				}
			}
			format = String.join( "|", split );
			bw.write( "<-- " + format );
			bw.newLine();
		}
		bw.close();
		br.close();
	}
	
	public static void main(String[] args) throws Exception {
		for ( String s: args ) {
			System.out.println( s );
		}
		GEDCOMReader gr = new GEDCOMReader( args[0] );
		gr.printGEDCOMFile();
	}

}
