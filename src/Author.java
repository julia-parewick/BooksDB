import java.util.ArrayList;
import java.util.List;

public class Author {

    private int authorID;
    private String fname;
    private String lname;
    private List<Book> bookList = new ArrayList<Book>();
    public Author(int authorID, String fname, String lname){
        this.fname = fname;
        this.lname = lname;
        this.authorID = authorID;
    }

    public void addBook(Book book){
        this.bookList.add(book);
    }

    public List<Book> getBookList(){
        return this.bookList;
    }

    public int getAuthorID(){
        return this.authorID;
    }

    public String getFname(){
        return this.fname;
    }

    public String getLname(){
        return this.lname;
    }

    public void setFname(String fname){
        this.fname=fname;
    }

    public void setLname(String lname){
        this.lname=lname;
    }


}
