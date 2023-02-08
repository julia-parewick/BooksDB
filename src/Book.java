import java.util.ArrayList;
import java.util.List;

/**
 * Book class that stores all database information about a title including associated Author objects.
 */
public class Book {
    /**
     * Title of book
     */
    private String title;
    /**
     * Foreign key value
     */
    private String isbn;
    /**
     * The edition of publication
     */
    private Integer edition;
    /**
     * The year publication was copyrighted
     */
    private String copyright;
    /**
     * List of Author objects associated with Book
     */
    private List<Author> authorList = new ArrayList<Author>();

    /**
     * Constructor
     * @param title
     * @param isbn
     * @param edition
     * @param copyright
     */
    public Book(String title, String isbn, Integer edition, String copyright){
        this.title = title;
        this.isbn = isbn;
        this.edition = edition;
        this.copyright = copyright;
    }

    /**
     * Method that adds a Author object to authorList
     * @param author
     */
    public void addAuthor(Author author){
        authorList.add(author);
    }

    /**
     * Getter method for authorList
     * @return
     */
    public List<Author> getAuthorList(){
        return this.authorList;
    }

    /**
     * Getter method for ISBN
     * @return
     */
    public String getIsbn(){
        return this.isbn;
    }

    /**
     *Getter method for title
     * @return
     */
    public String getTitle(){
        return this.title;
    }

    /**
     * Getter method for Edition
     * @return
     */
    public int getEdition(){
        return this.edition;
    }

    /**
     * Getter method for copyright year
     * @return
     */
    public String getCopyright(){
        return this.copyright;
    }

    /**
     * Setter method for isbn
     * @param isbn
     */
    public void setIsbn(String isbn){
        this.isbn=isbn;
    }

    /**
     * Setter method for title
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Setter method for edition
     * @param edition
     */
    public void setEdition(int edition){
        this.edition=edition;
    }

    /**
     * Setter method for copyright year
     * @param copyright
     */
    public void setCopyright(String copyright){
        this.copyright=copyright;
    }
}
