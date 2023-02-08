public class DBConfiguration {
    protected static final String DB_URL= "jdbc:mariadb://localhost:3307";
    protected static final String DB_USER = "root";
    protected static final String DB_PASSWORD = "root";
    protected static final String DB_BOOKS = "/books";
    protected static final String MARIA_DB_DRIVER = "org.mariadb.jdbc.Driver";

    /**
     * Authors
     */
    protected static final String DB_BOOKS_AUTHORS_ID = "authorID";
    protected static final String DB_BOOKS_AUTHORS_FNAME = "firstName";
    protected static final String DB_BOOKS_AUTHORS_LNAME = "lastName";

    /**
     * Titles
     */

    protected static final String DB_BOOKS_TITLES_ISBN = "isbn";
    protected static final String DB_BOOKS_TITLES_TITLE = "title";
    protected static final String DB_BOOKS_TITLES_EDITION = "editionNumber";
    protected static final String DB_BOOKS_TITLES_COPYRIGHT = "copyright";

    public static void doClassForNameRegistration(){
        final String driverName=DBConfiguration.MARIA_DB_DRIVER;
        try{
            Class.forName(driverName);
            System.out.println("Registered Driver!");
        }catch(ClassNotFoundException cnf){
            System.out.println("Unable to load Class");
            System.exit(1);
        }
    }

}
