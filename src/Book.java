import java.util.ArrayList;
import java.util.List;

public class Book {
    private String title;
    private String isbn;
    private Integer edition;

    private String copyright;
    private List<Author> authorList = new ArrayList<Author>();

//    public Book(String title, String isbn, Integer edition, String copyright, List<Author> authors){
//        this.title = title;
//        this.isbn = isbn;
//        this.edition = edition;
//        this.copyright = copyright;
//        this.authorList=authors;
//    }

    public Book(String title, String isbn, Integer edition, String copyright){
        this.title = title;
        this.isbn = isbn;
        this.edition = edition;
        this.copyright = copyright;
    }
    public void addAuthor(Author author){
        authorList.add(author);
    }

    public List<Author> getAuthorList(){
        return this.authorList;
    }

    public String getIsbn(){
        return this.isbn;
    }

    public String getTitle(){
        return this.title;
    }

    public int getEdition(){
        return this.edition;
    }

    public String getCopyright(){
        return this.copyright;
    }

    public void setIsbn(String isbn){
        this.isbn=isbn;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setEdition(int edition){
        this.edition=edition;
    }

    public void setCopyright(String copyright){
        this.copyright=copyright;
    }
}
