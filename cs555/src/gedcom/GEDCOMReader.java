package gedcom;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;

public class GEDCOMReader {
	private File gedcomFile;
	Map<String, Individual> individuals;
	Map<String, Family> families;
	public static final List GEDCOM_TAGS = new ArrayList<String>(Arrays.asList( "INDI", "NAME", "SEX", "BIRT", "DEAT", "FAMC", "FAMS", "FAM", "MARR", "HUSB", "WIFE", "CHIL", "DIV", "DATE", "HEAD", "TRLR", "NOTE" ) );
	public static final int AGE_LIMIT = 150;
	private static DateFormat formatter = new SimpleDateFormat( "dd MMM yyyy" );
	
	public GEDCOMReader( String gedcomFile ) throws Exception {
		this.gedcomFile = new File( gedcomFile );
		createGEDCOMObjects();
	}
	
	public File getFile() {
		return gedcomFile;
	}
	
	public Map<String, Individual> getIndividuals() {
		return individuals;
	}
	
	public Map<String, Family> getFamilies() {
		return families;
	}
	
	public void trimGEDCOMFile() throws Exception {
		BufferedReader br = new BufferedReader( new FileReader( gedcomFile ) );
		File outputFile = new File( "resources/" + gedcomFile.getName().split("\\.")[0] + "-copy.txt" );
		BufferedWriter bw = new BufferedWriter( new FileWriter( outputFile ) );
		String line;
		while ( ( line = br.readLine() ) != null ) {
			bw.write( line.trim() );
			bw.newLine();
		}
		br.close();
		bw.close();
		Files.delete( gedcomFile.toPath() );
		outputFile.renameTo( gedcomFile );
	}
	
	public void printGEDCOMFile() throws Exception {
		BufferedReader br = new BufferedReader( new FileReader( gedcomFile ) );
		File outputFile = new File( "resources/" + gedcomFile.getName().split("\\.")[0] + "output.txt" );
		if ( !outputFile.exists() ) {
			outputFile.createNewFile();
		}
		BufferedWriter bw = new BufferedWriter( new FileWriter( outputFile.getAbsoluteFile() ) );
		String line;
		while ( ( line = br.readLine() ) != null ) {
			bw.write( "--> " + line );
			bw.newLine();
			List<String> split = new ArrayList<String>( Arrays.asList( line.split( " ", 3 ) ) );
			split.replaceAll(String::trim);
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
	
	public void createGEDCOMObjects() throws Exception {
		BufferedReader br = new BufferedReader( new FileReader( gedcomFile ) );
		String line = br.readLine();
		individuals = new HashMap<String, Individual>();
		families = new HashMap<String, Family>();
		while( line != null) {
			List<String> split = new ArrayList<String>( Arrays.asList( line.split( " ", 3 ) ) );
			split.replaceAll(String::trim);
			if ( split.size() == 3 ) {
				if ( split.get(2).equals( "INDI" ) ) {
					Individual i = new Individual();
					i.setId( split.get(1) );
					while ( ( line = br.readLine() ) != null ) {
						split = new ArrayList<String>( Arrays.asList( line.split( " ", 3 ) ) );
						split.replaceAll(String::trim);
						if ( split.get( 0 ).equals( "0" ) ) {
							if ( i.getDeath() == null ) {
								i.setAlive( true );
								Calendar today = Calendar.getInstance();
								Calendar birthday = Calendar.getInstance();
								birthday.setTime( i.getBirthday() );
								if ( today.get( Calendar.MONTH ) > birthday.get( Calendar.MONTH ) ) {
									i.setAge( today.get( Calendar.YEAR ) - birthday.get( Calendar.YEAR ) );
								} else {
									i.setAge( today.get( Calendar.YEAR ) - birthday.get( Calendar.YEAR ) - 1 );
								}
							} else {
								i.setAlive( false );
								Calendar birthday = Calendar.getInstance();
								Calendar death = Calendar.getInstance();
								birthday.setTime( i.getBirthday() );
								death.setTime( i.getDeath() );
								if ( death.get( Calendar.MONTH ) >= birthday.get( Calendar.MONTH ) && death.get( Calendar.DAY_OF_MONTH ) >= birthday.get( Calendar.DAY_OF_MONTH ) ) {
									i.setAge( death.get( Calendar.YEAR ) - birthday.get( Calendar.YEAR ) );
								} else {
									i.setAge( death.get( Calendar.YEAR ) - birthday.get( Calendar.YEAR ) - 1 );
								}
							}
							individuals.put( i.getId(), i );
							break;
						}
						if ( split.get( 0 ).equals( "1" ) ) {
							if ( split.size() == 2 ) {
								if ( split.get( 1 ).equals( "BIRT") ) {
									line = br.readLine();
									split = new ArrayList<String>( Arrays.asList( line.split( " ", 3 ) ) );
									split.replaceAll(String::trim);
									if (split.get( 1 ).equals( "DATE" ) ) {
										i.setBirthday(formatter.parse( split.get( 2 ) ) );
									}	
								} else if ( split.get( 1 ).equals( "DEAT") ) {
									line = br.readLine();
									split = new ArrayList<String>( Arrays.asList( line.split( " ", 3 ) ) );
									split.replaceAll(String::trim);
									i.setDeath(formatter.parse( line.split(" ", 3)[2]) );
									if (split.get( 1 ).equals( "DATE" ) ) {
										i.setBirthday(formatter.parse( split.get( 2 ) ) );
									}	
								}
							} else {
								if ( split.get( 1 ).equals( "NAME" ) ) {
									i.setName( split.get( 2 ) );
								} else if ( split.get( 1 ).equals( "SEX" ) ) {
									i.setGender( split.get( 2 ) );
								} else if ( split.get( 1 ).equals( "FAMS" ) ) {
									List<String> spouses;
									if ( i.getSpouses() != null ) {
										spouses = i.getSpouses();
										
									} else {
										spouses = new ArrayList<String>();
									}
									spouses.add( split.get( 2 ) );
									i.setSpouses( spouses );
								} else if ( split.get( 1 ).equals( "FAMC") ) {
									List<String> children;
									if ( i.getChildren() != null ) {
										children = i.getChildren();
									} else {
										children = new ArrayList<String>();
									}
									children.add( split.get( 2 ) );
									i.setChildren( children );
									
								}
							}
						}
					}
				} else if ( split.get( 2 ).equals( "FAM" ) ) {
					Family f = new Family();
					f.setId( split.get( 1 ) );
					while ( ( line = br.readLine() ) != null ) {
						split = new ArrayList<String>( Arrays.asList( line.split( " ", 3 ) ) );
						split.replaceAll(String::trim);
						if ( split.get( 0 ).equals( "0" ) ) {
							families.put( f.getId(), f ); 
							break;
						}
						if ( split.get( 0 ).equals( "1" ) ) {
							if ( split.size() == 2 ) {
								if (split.get( 1 ).equals( "MARR") ) {
									line = br.readLine();
									split = new ArrayList<String>( Arrays.asList( line.split( " ", 3 ) ) );
									split.replaceAll(String::trim);
									if (split.get( 1 ).equals( "DATE" ) ) {
										f.setMarried(formatter.parse( split.get( 2 ) ) );
									}
								} else if ( split.get( 1 ).equals( "DIV" ) ) {
									line = br.readLine();
									split = new ArrayList<String>( Arrays.asList( line.split( " ", 3 ) ) );
									split.replaceAll(String::trim);
									if (split.get( 1 ).equals( "DATE" ) ) {
										f.setDivorced( formatter.parse( split.get( 2 ) ) );
									}
								}
							} else {
								if ( split.get( 1 ).equals( "HUSB") ) {
									f.setHusbandId( split.get( 2 ) );
								} else if ( split.get( 1 ).equals( "WIFE" ) ) {
									f.setWifeId( split.get( 2 ) );
								} else if ( split.get( 1 ).equals( "CHIL" ) ) {
									List<String> children;
									if ( f.getChildren() != null ) {
										children = f.getChildren();
									} else {
										children = new ArrayList<String>();
									}
									children.add( split.get( 2 ) );
									f.setChildren( children );
								}
							}
						}
					}
				} else {
					line = br.readLine();
				}
			} else {
				line = br.readLine();
			}
		}
		// remove families if the ids of husband and wife doesnt exist
		families.entrySet().removeIf(entry -> individuals.get( entry.getValue().getWifeId() ) == null || individuals.get( entry.getValue().getHusbandId() ) == null );
		for ( Map.Entry<String, Family> e : families.entrySet() ) {
			String husbandId = e.getValue().getHusbandId();
			String wifeId = e.getValue().getWifeId();
			e.getValue().setHusbandName( individuals.get( husbandId ).getName() );
			e.getValue().setWifeName( individuals.get( wifeId ).getName() );
		}
		br.close();
	}
	
	public void writeGEDCOMTable() throws Exception {
		File outputFile = new File( "resources/" + gedcomFile.getName().split("\\.")[0] + "outputtable.csv" );
		if ( !outputFile.exists() ) {
			outputFile.createNewFile();
		}
		BufferedWriter bw = new BufferedWriter( new FileWriter( outputFile.getAbsoluteFile() ) );
		bw.write( "Individuals" );
		bw.newLine();
		bw.write( "ID,Name,Gender,Birthday,Age,Alive,Death,Child,Spouse");
		bw.newLine();
		for ( Map.Entry<String, Individual> e: individuals.entrySet() ) {
			bw.write( e.getValue().getIndividual() );
			bw.newLine();
		}
		bw.newLine();
		bw.write("Families");
		bw.newLine();
		bw.write( "ID,Married,Divorce,Husband ID,Husband Name,Wife ID,Wife Name,Children");
		bw.newLine();
		for ( Map.Entry<String, Family> e: families.entrySet() ) {
			bw.write( e.getValue().getFamily() );
			bw.newLine();
		}
		bw.newLine();
		List<String> errors = getErrors();
		for( String e: errors ) {
			bw.write( e );
			bw.newLine();
		}
		bw.close();
	}
	
	//removes individuals with age over the age limit
	public List<String> setAgeLimit() {
		List<String> errors = new ArrayList<String>();
		//individuals.entrySet().removeIf( e -> e.getValue().getAge() >= AGE_LIMIT );
		for ( Map.Entry<String, Individual> e: individuals.entrySet() ) {
			if ( e.getValue().getAge() >= AGE_LIMIT ) {
				errors.add("Error : " + e.getValue().getName() + "(" + e.getValue().getId() + ") has an age of 150 or more.");
			}
		}
		return errors;
	}
	
	//removes individuals and families with dates after current date
	public List<String> checkDates() {
		List<String> errors = new ArrayList<String>();
		Date currentDate = new Date();
		//individuals.entrySet().removeIf( e -> currentDate.compareTo( e.getValue().getBirthday() ) < 0 || ( e.getValue().getDeath() != null && currentDate.compareTo( e.getValue().getDeath() ) < 0 ) );
		//families.entrySet().removeIf( e -> currentDate.compareTo( e.getValue().getMarried() ) < 0 || ( e.getValue().getDivorced() != null && currentDate.compareTo( e.getValue().getDivorced() ) < 0 ) );
		for ( Map.Entry<String, Individual> e: individuals.entrySet() ) {
			if ( currentDate.compareTo( e.getValue().getBirthday() ) < 0 || ( e.getValue().getDeath() != null && currentDate.compareTo( e.getValue().getDeath() ) < 0 ) ) {
				errors.add("Error : " + e.getValue().getName() + "(" + e.getValue().getId() + ") has a date after current date.");
			}
		}
		for ( Map.Entry<String, Family> e: families.entrySet() ) {
			if ( currentDate.compareTo( e.getValue().getMarried() ) < 0 || ( e.getValue().getDivorced() != null && currentDate.compareTo( e.getValue().getDivorced() ) < 0 ) ) {
				errors.add("Error : " + e.getValue().getId() + " has a date after current date.");
			}
		}
		return errors;
	}
	
	//removes Individual if the death date comes before their birthday
	public List<String> checkDeaths() {
		List<String> errors = new ArrayList<String>();
		//individuals.entrySet().removeIf( e -> ( e.getValue().getDeath() != null && e.getValue().getBirthday().compareTo(e.getValue().getDeath()) > 0 ) );
		for ( Map.Entry<String, Individual> e: individuals.entrySet() ) {
			if ( e.getValue().getDeath() != null && e.getValue().getBirthday().compareTo(e.getValue().getDeath()) > 0 ) {
				errors.add("Error : Death before birth of " + e.getValue().getName() + "(" + e.getValue().getId() + ").");
			}
		}
		return errors;
	}
	
	//changes the individual's marriage date to their birthday if marriage came before their birthday
	public List<String> checkMarriage() {
		List<String> errors = new ArrayList<String>();
		for ( Map.Entry<String, Individual> e: individuals.entrySet() ) {
			Date iDate = e.getValue().getBirthday();
			List<String> spouses = e.getValue().getSpouses();
			for ( Map.Entry<String, Family> m: families.entrySet() ) {
				if( (spouses != null && spouses.contains( m.getValue().getId() )) && m.getValue().getMarried().compareTo( iDate ) <= 0 ) {
					errors.add("Error : Married before birth of " + m.getValue().getId() + " " + e.getValue().getName() + "(" + e.getValue().getId() + ").");
				}
			}
		}
		return errors;
	}
	
	// find people whose divorce date is before to marriage date
	public List<String> checkDivorceBeforeMarriage() {
		List<String> errors = new ArrayList<String>();
		for (Map.Entry<String, Family> e : families.entrySet()) {
			Date divorce_date = e.getValue().getDivorced();
			Date marriage_date = e.getValue().getMarried();
			if (divorce_date != null && divorce_date.compareTo(marriage_date) == -1) {
				String husband_name = e.getValue().getHusbandName();
				String wife_name = e.getValue().getWifeName();
				String husband_id = e.getValue().getHusbandId();
				String wife_id = e.getValue().getWifeId();
				errors.add("Error : Divorce date of " +  husband_name + "(" + husband_id +")" + "occurs before his marriage date.");
				errors.add("Error : Divorce date of " +  wife_name + "(" + wife_id +")" +"occurs before her marriage date.");
			}
		}
		return errors;
	}
	
	// find people whose death date is before marriage date 
	public List<String> checkDeathBeforeMarriage() {
		List<String> errors = new ArrayList<String>();
		for (Map.Entry<String, Individual> e: individuals.entrySet()) {
			if (e.getValue().isAlive() == false) {
				Date death_date = e.getValue().getDeath();
				String name = e.getValue().getName();
				String id = e.getValue().getId();
				for (Map.Entry<String, Family> e2: families.entrySet()) {
					if (e2.getValue().getHusbandId().equals(id) || e2.getValue().getWifeId().equals(id)) {
						Date marriage_date = e2.getValue().getMarried();
						if (death_date.compareTo(marriage_date) == -1) {
							errors.add("Error: Death date of " + name + "(" + id +") " + "occurs before his marriage date.");
						}
					}
				}
			}
		}
		return errors;
	}
	
	public List<String> getErrors() {
		List<String> errors = new ArrayList<String>();
		errors.addAll( setAgeLimit() );
		errors.addAll( checkDates() );
		errors.addAll( checkDeaths() );
		errors.addAll( checkMarriage() );
		errors.addAll( checkDivorceBeforeMarriage() );
		errors.addAll( checkDeathBeforeMarriage() );
		return errors;
	}
	

	public static void main(String[] args) throws Exception {
		for ( String s: args ) {
			System.out.println( s );
		}
		GEDCOMReader gr = new GEDCOMReader( args[0] );
		gr.trimGEDCOMFile();
		gr.printGEDCOMFile();
		gr.writeGEDCOMTable();
	}
}
