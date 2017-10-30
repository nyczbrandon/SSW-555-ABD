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
import java.util.concurrent.TimeUnit;
import java.util.Date;

public class GEDCOMReader {
	private File gedcomFile;
	Map<String, Individual> individuals;
	Map<String, Family> families;
	public static final List<String> GEDCOM_TAGS = new ArrayList<String>(Arrays.asList( "INDI", "NAME", "SEX", "BIRT", "DEAT", "FAMC", "FAMS", "FAM", "MARR", "HUSB", "WIFE", "CHIL", "DIV", "DATE", "HEAD", "TRLR", "NOTE" ) );
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
										i.setDeath(formatter.parse( split.get( 2 ) ) );
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
	
	//checks  individuals with age over the age limit
	public List<String> setAgeLimit() {
		List<String> errors = new ArrayList<String>();
		for ( Map.Entry<String, Individual> e: individuals.entrySet() ) {
			Individual i = e.getValue();
			if ( i.getAge() >= AGE_LIMIT ) {
				errors.add("Error (US07) : " + i.getName() + "(" + i.getId() + ") has an age of 150 or more.");
			}
		}
		return errors;
	}
	
	//check individuals and families with dates after current date
	public List<String> checkDates() {
		List<String> errors = new ArrayList<String>();
		Date currentDate = new Date();
		checkIndividualsDate(errors, currentDate);
		checkFamiliesDate(errors, currentDate);
		return errors;
	}

	//checks individuals with dates after current date
	private void checkIndividualsDate(List<String> errors, Date currentDate) {
		for ( Map.Entry<String, Individual> e: individuals.entrySet() ) {
			Individual i = e.getValue();
			if ( currentDate.compareTo( i.getBirthday() ) < 0 || ( i.getDeath() != null && currentDate.compareTo( i.getDeath() ) < 0 ) ) {
				errors.add("Error (US01) : " + i.getName() + "(" + i.getId() + ") has a date after current date.");
			}
		}
	}
	
	//checks individuals with dates after current date
	private void checkFamiliesDate(List<String> errors, Date currentDate) {
		for ( Map.Entry<String, Family> e: families.entrySet() ) {
			Family f = e.getValue();
			if ( currentDate.compareTo( f.getMarried() ) < 0 || ( f.getDivorced() != null && currentDate.compareTo( f.getDivorced() ) < 0 ) ) {
				errors.add("Error (US01) : " + f.getId() + " has a date after current date.");
			}
		}
	}
	
	//checks for individuals who were born after marriage of parents and not more than 9 months after their divorce
	public List<String> checkBornUnwed() {
		List<String> errors = new ArrayList<String>();
		for ( Map.Entry<String, Family> e: families.entrySet() ) {
			Family f = e.getValue();
			for ( String s: f.getChildren() ) {
				Individual c = individuals.get( s );
				Date childBirthday = c.getBirthday();
				if ( childBirthday.before( f.getMarried() ) ) {
					errors.add("Error (US08) : " + c.getName() + "(" + c.getId() + ") was born before the marriage of his parents " + f.getHusbandName() + "(" + f.getHusbandId() + ") and " + f.getWifeName() + "(" + f.getWifeId() + ")." );
				} else {
					if ( f.getDivorced() != null ) {
						Calendar cal = Calendar.getInstance();
						cal.setTime( f.getDivorced() );
						cal.add( Calendar.MONTH, -9 );
						if ( childBirthday.after( cal.getTime() ) ) {
							errors.add("Error (US08) : " + c.getName() + "(" + c.getId() + ") was born more than 9 months after the divorce of his parents " + f.getHusbandName() + "(" + f.getHusbandId() + ") and " + f.getWifeName() + "(" + f.getWifeId() + ")." );
						}
					}
				}
				
			}
		}
		return errors;
	}
	
	//checks if the Individual's death date comes before their birthday
	public List<String> checkDeaths() {
		List<String> errors = new ArrayList<String>();
		for ( Map.Entry<String, Individual> e: individuals.entrySet() ) {
			if ( e.getValue().getDeath() != null && e.getValue().getBirthday().compareTo(e.getValue().getDeath()) > 0 ) {
				errors.add("Error (US03) : Death before birth of " + e.getValue().getName() + "(" + e.getValue().getId() + ").");
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
					errors.add("Error (US02) : Family " + m.getValue().getId() + " married before birth of " + e.getValue().getName() + "(" + e.getValue().getId() + ").");
				}
			}
		}
		return errors;
	}
	
	//checks if a family has had sextuplets or more
	public List<String> checkBabies() {
		List<String> errors = new ArrayList<String>();
		for ( Map.Entry<String, Family> e: families.entrySet() ) {
			Family f = e.getValue();
			if ( f.getChildren().size() > 5 ) {
				for ( String s: f.getChildren() ) {
					int count = 0;
					for ( String s2: f.getChildren() ) {
						if ( individuals.get(s).getBirthday().compareTo( individuals.get( s2 ).getBirthday() ) == 0 ) {
							count++;
						}
					}
					if ( count > 5) {
						errors.add( "Error (US14) : Family " + f.getId() + " has 6 or more children born on the same day." );
						break;
					}
				}
			}
		}
		return errors;
	}
	
	//checks if parents are too old
	public List<String> checkOldParents() {
		List<String> errors = new ArrayList<String>();
		for ( Map.Entry<String, Family> e: families.entrySet() ) {
			Family f = e.getValue();
			Calendar oldestMom = Calendar.getInstance();
			Calendar oldestDad = Calendar.getInstance();
			oldestMom.setTime( individuals.get( f.getWifeId() ).getBirthday() );
			oldestDad.setTime( individuals.get( f.getHusbandId() ).getBirthday() );
			oldestMom.add( Calendar.YEAR, 60 );
			oldestDad.add( Calendar.YEAR, 80 );
			for ( String s: f.getChildren() ) {
				Individual child = individuals.get( s );
				if ( oldestMom.getTime().compareTo( child.getBirthday() ) < 0 ) {
					errors.add( "Error (US12) : Family " + f.getId() + " has mother " + f.getWifeName() + "(" + f.getWifeId() + ") that is 60 or more years older than child " + child.getName() + "(" + child.getId() + ")." );
				}
				if ( oldestDad.getTime().compareTo( child.getBirthday() ) < 0 ) {
					errors.add( "Error (US12) : Family " + f.getId() + " has father " + f.getHusbandName() + "(" + f.getHusbandId() + ") that is 80 or more years older than child " + child.getName() + "(" + child.getId() + ")." );
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
				errors.add("Error (US04) : Divorce date of " +  husband_name + "(" + husband_id +")" + "occurs before his marriage date.");
				errors.add("Error (US04) : Divorce date of " +  wife_name + "(" + wife_id +")" +"occurs before her marriage date.");
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
							errors.add("Error (US05) : Death date of " + name + "(" + id +") " + "occurs before his marriage date.");
						}
					}
				}
			}
		}
		return errors;
	}
	
	// check people whose divorce date is before death
	public List<String> checkDivorceBeforeDeath() {
		List<String> errors = new ArrayList<String>();
		for (Map.Entry<String, Family> e: families.entrySet()) {
			Date divorce_date = e.getValue().getDivorced();
			if (divorce_date != null) {
				String husband_name = e.getValue().getHusbandName();
				String wife_name = e.getValue().getWifeName();
				String husband_id = e.getValue().getHusbandId();
				String wife_id = e.getValue().getWifeId();
				for (Map.Entry<String, Individual> e2: individuals.entrySet()) {
					if (e2.getValue().getName().equals(husband_name)) {
						Date death_date = e2.getValue().getDeath();
						if (death_date != null && divorce_date.compareTo(death_date) == 1) {
							errors.add("Error (US06) : Death date of " + husband_name + "(" + husband_id +") " + "is before his divorce date.");
						}
					}
					else if (e2.getValue().getName().equals(wife_name)) {
						Date death_date = e2.getValue().getDeath();
						if (death_date != null && divorce_date.compareTo(death_date) == 1) {
							errors.add("Error (US06) : Death date of " + wife_name + "(" + wife_id +") " + "is before her divorce date.");
						}
					}
				}
			}
		}
		return errors;
	}
	
	// check people whose birth date is before death of his or her parents
	public List<String> checkBirthBeforeDeathofParents() {
		List<String> errors = new ArrayList<String>();
		for (Map.Entry<String, Family> e : families.entrySet()) {
			String husband_id = e.getValue().getHusbandId();
			String wife_id = e.getValue().getWifeId();
			List<String> children = e.getValue().getChildren();
			Individual husband = individuals.get(husband_id);
			Individual wife = individuals.get(wife_id);
			
			if (husband != null && husband.isAlive() == false) {
				Date husband_death_date = husband.getDeath();
				for (String child_id : children) {
					Individual child = individuals.get(child_id);
					if (child != null) {
						String child_name = child.getName();
						if (child.getBirthday().compareTo(husband_death_date) == 1) {
							errors.add("Error (US09) : Birthday of " + child_name + "(" + child_id +") " + "is after death date of parents");
						}
					}
					
				}
			}
			if (wife != null && wife.isAlive() == false) {
				Date wife_death_date = wife.getDeath();
				for (String child_id : children) {
					Individual child = individuals.get(child_id);
					if (child != null) {
						String child_name = child.getName();
						if (child.getBirthday().compareTo(wife_death_date) == 1) {
							errors.add("Error (US09) : Birthday of " + child_name + "(" + child_id +") " + "is after death date of parents");
						}
					}
					
				}
			}
		}
		return errors;
	}
	
	//check people for minimum of 14 years of age before marriage
	public List<String> checkMinAgeForMarriage(){
		List<String> errors = new ArrayList<String>();
		for (Map.Entry<String, Family> e: families.entrySet()) {
			String husband_id = e.getValue().getHusbandId();
			String wife_id = e.getValue().getWifeId();
			Individual husband = individuals.get(husband_id);
			Individual wife = individuals.get(wife_id);

			Calendar today = Calendar.getInstance();
			Calendar marriage_date = Calendar.getInstance();
			int married_years = 0;
			marriage_date.setTime( e.getValue().getMarried());
			if (today.get( Calendar.MONTH ) > marriage_date.get( Calendar.MONTH ) ) {
				married_years = today.get( Calendar.YEAR ) - marriage_date.get( Calendar.YEAR);
			} else {
				married_years = today.get(Calendar.YEAR) - marriage_date.get( Calendar.YEAR ) - 1;
			}

			if (husband != null && husband.isAlive() == true) {
				String husband_name = husband.getName();
				if (14 > husband.getAge() - married_years) {
					errors.add("Error (US10) : Husband " + husband_name + "(" + husband_id +") " + "married before age 14");
				}
			} else if(husband != null && husband.isAlive() == false) {
				String husband_name = husband.getName();
				Calendar death_date = Calendar.getInstance();
				death_date.setTime( husband.getDeath());
				int been_dead = 0;
				if (today.get( Calendar.MONTH ) > death_date.get( Calendar.MONTH ) ){
					been_dead = today.get( Calendar.YEAR ) - death_date.get( Calendar.YEAR);
				} else {
					been_dead = today.get(Calendar.YEAR) - death_date.get( Calendar.YEAR ) - 1;
				}
				if(14 > (husband.getAge() - married_years + been_dead) ) {
					errors.add("Error (US10) : Husband " + husband_name + "(" + husband_id + ") " + "married before age 14");
				}
			}

			if (wife != null && wife.isAlive() == true) {
				String wife_name = wife.getName();
				if (14 > wife.getAge() - married_years) {
					errors.add("Error (US10) : Wife " + wife_name + "(" + wife_id +") " + "married before age 14");
				}
			} else if(wife != null && wife.isAlive() == false) {
				String wife_name = wife.getName();
				Calendar death_date = Calendar.getInstance();
				death_date.setTime( wife.getDeath());
				int been_dead = 0;
				if (today.get( Calendar.MONTH ) > death_date.get( Calendar.MONTH ) ){
					been_dead = today.get( Calendar.YEAR ) - death_date.get( Calendar.YEAR);
				} else {
					been_dead = today.get(Calendar.YEAR) - death_date.get( Calendar.YEAR ) - 1;
				}
				if(14 > (wife.getAge() - married_years + been_dead) ) {
					errors.add("Error (US10) : Wife " + wife_name + "(" + wife_id +") " + "married before age 14");
				}
			}			
		}
		return errors;
	}

	//check married couples for correct gender
	public List<String> checkMarriageGenderRoles(){
		List<String> errors = new ArrayList<String>();
		for (Map.Entry<String, Family> e: families.entrySet()) {
			String husband_id = e.getValue().getHusbandId();
			String wife_id = e.getValue().getWifeId();
			Individual husband = individuals.get(husband_id);
			Individual wife = individuals.get(wife_id);

			if (husband != null && !(husband.getGender().equals("M")) ){
				String husband_name = husband.getName();
				errors.add("Error (US21) : Gender role of " + husband_name + "(" + husband_id +") " + "is not correct");
			}

			if (wife != null && !(wife.getGender().equals("F"))) {
				String wife_name = wife.getName();
				errors.add("Error (US21) : Gender role of " + wife_name + "(" + wife_id + ") " + "is not correct");
			}
		}
		return errors;
	}
	
	// check siblings spacing
	public List<String> checkSiblingsSpacing() {
		List<String> errors = new ArrayList<>();
		for (Map.Entry<String, Family> e: families.entrySet()) {
			List<String> children = e.getValue().getChildren();
			if (children.size() < 2) {
				continue;
			}
			for (int i = 0; i < children.size(); i++) {
				for (int j = i + 1; j < children.size(); j++) {
					String child_id_1 = children.get(i);
					String child_id_2 = children.get(j);
					Individual child_1 = individuals.get(child_id_1);
					Individual child_2 = individuals.get(child_id_2);
					String child_name_1 = child_1.getName();
					String child_name_2 = child_2.getName();			
					Date birthday_1 = child_1.getBirthday();
					Date birthday_2 = child_2.getBirthday();
					long diff = Math.abs(birthday_1.getTime() - birthday_2.getTime());
					long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
					if (days >= 2 && days <= 240) {
						errors.add("Error (US13) : Birth dates of siblings " + child_name_1 + " (" + child_id_1 + ") and " + child_name_2 + "(" + child_id_2 + ") should be more than 8 months apart or less than 2 days apart.");
					}
				}
			}
		}
		return errors;
	}
	
	// check no marriage to descendants
	public List<String> checkNoMarriageToDescendants() {
		List<String> errors = new ArrayList<>();
		
		for (Map.Entry<String, Family> e: families.entrySet()) {
			
			if (e.getValue().getDivorced() == null) {
				continue;
			}
			List<String> children = e.getValue().getChildren();
			
			String husband = e.getValue().getHusbandId();
			String husband_id = e.getValue().getHusbandId();
			List<String> spouses1 = individuals.get(husband_id).getSpouses();
		
			for (String child : children) {
				for (String spouse: spouses1) {
					if (child.equals(spouse)) {
						System.out.println("Error (17) : " + husband + "(" + husband_id + ") married to his descendant " + child);
						errors.add("Error (17) : " + husband + "(" + husband_id + ") married to his descendant " + child);
					}
				}
			}
			
			String wife = e.getValue().getWifeId();
			String wife_id = e.getValue().getWifeId();
			List<String> spouses2 = individuals.get(wife_id).getSpouses();
			
			for (String child : children) {
				for (String spouse: spouses2) {
					if (child.equals(spouse)) {
						System.out.println("Error (17) : " + wife + "(" + wife_id + ") married to his descendant " + child);
						errors.add("Error (17) : " + wife + "(" + wife_id + ") married to her descendant " + child);
					}
				}
			}
			
		}
		return errors;
	}

	//lists all deceased individuals
	public List<String> listDeceased() {
		List<String> dList = new ArrayList<String>();	
		for (Map.Entry<String, Individual> e: individuals.entrySet()) {
			if(e.getValue().isAlive() == false) {
				dList.add("List deceased (US29): Individual " + e.getValue().getId() + " is dead.");
			}
		}
		return dList;
	}

	//checks couples who are related
	public List<String> checkNotSiblings(){
		List<String> errors = new ArrayList<String>();
		for (Map.Entry<String, Family> e: families.entrySet()) {
			if (e.getValue().getChildren() != null) {
				List<String> kids = new ArrayList<String>();
				kids = e.getValue().getChildren();
				for (int i = 0; i < kids.size(); i++) {
					List<String> spouses = new ArrayList<String>();
					spouses = individuals.get(kids.get(i)).getSpouses();
					if(spouses != null) {
						for (int j = i+1; j < kids.size(); j++) {
							List<String> spouses2 = new ArrayList<String>();
							spouses2 = individuals.get(kids.get(j)).getSpouses();
							for( int spouseCount = 0; spouseCount < spouses2.size(); spouseCount++) {
								if( spouses.contains(spouses2.get(i))) {
									errors.add("Error (18): Family " + spouses2.get(i) + " is a marriage of siblings.");
								}
							}
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
		errors.addAll( checkBornUnwed() );
		errors.addAll( checkDeaths() );
		errors.addAll( checkMarriage() );
		errors.addAll( checkBabies() );
		errors.addAll( checkOldParents() );
		errors.addAll( checkDivorceBeforeMarriage() );
		errors.addAll( checkDeathBeforeMarriage() );
		errors.addAll( checkDivorceBeforeDeath() );
		errors.addAll( checkBirthBeforeDeathofParents() );
		errors.addAll( checkMinAgeForMarriage() );
		errors.addAll( checkMarriageGenderRoles() );
		errors.addAll( checkSiblingsSpacing() );
		errors.addAll( checkNoMarriageToDescendants() );
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
