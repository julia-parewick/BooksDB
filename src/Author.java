import java.util.ArrayList;
import java.util.List;

/**
 * Author class that store info about author identity, database id, and associated Book objects.
 */
public class Author {
    /**
     * Foreign key value
     */
    private int authorID;
    /**
     * Name fields
     */
    private String fname;
    private String lname;
    /**
     * List of Book objects created from database
     */
    private List<Book> bookList = new ArrayList<Book>();

    /**
     * constructor
     * @param authorID
     * @param fname
     * @param lname
     */
    public Author(int authorID, String fname, String lname){
        this.fname = fname;
        this.lname = lname;
        this.authorID = authorID;
    }

    /**
     * Method that adds a Book object to the bookList param
     * @param book
     */
    public void addBook(Book book){
        this.bookList.add(book);
    }

    /**
     * getter method for bookList
     * @return
     */
    public List<Book> getBookList(){
        return this.bookList;
    }

    /**
     * getter method for author ID
     * @return
     */
    public int getAuthorID(){
        return this.authorID;
    }

    /**
     * Getter method for first name
     * @return
     */
    public String getFname(){
        return this.fname;
    }

    /**
     * Getter method for last name
     * @return
     */
    public String getLname(){
        return this.lname;
    }

    /**
     * Setter method for first name
     * @param fname
     */
    public void setFname(String fname){
        this.fname=fname;
    }

    /**
     * Setter method for last name
     * @param lname
     */
    public void setLname(String lname){
        this.lname=lname;
    }


}
