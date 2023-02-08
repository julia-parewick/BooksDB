import java.sql.Connection;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * Application class containing main run method with initial user prompts
 */
public class BookApplication {
    public static void main(String[] args) throws SQLException {
        DBConfiguration.doClassForNameRegistration();
        System.out.println("Welcome to the Books Database Manager.");
        while(true){
            BookDatabaseManager db = new BookDatabaseManager();
            Connection connection = db.getConnection();

            List<Book> bookList = db.loadBookList(connection);

            List<Author> authorList = db.loadAuthorList(connection);

            bookList.forEach(
                    book -> {
                        List<Integer> authorIds = null;
                        try {
                            authorIds = db.getBookAuthors(book);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                        authorIds.forEach(
                                id -> {
                                    for(Author author: authorList){
                                        if(author.getAuthorID()==id){
                                            book.addAuthor(author);
                                        }
                                    }
                                }
                        );
                        book.getAuthorList().forEach(
                                author->{
                                    author.addBook(book);
                                }
                        );

                    }
            );

            System.out.print("\n0 - Exit\n1 - View books database\n2 - View authors database\n>>>");
            Integer chooseTable;
            while(true){
                Scanner scanner = new Scanner(System.in);

                try{
                    chooseTable = scanner.nextInt();
                    if(chooseTable>2){
                        throw new InputMismatchException();
                    }
                    break;
                }
                catch(InputMismatchException ime){
                    System.out.println("Invalid input. Please retry: ");
                }
            }
            switch (chooseTable) {
                case 0 -> {
                    db.exitProgram(connection);
                }
                case 1 -> {
                    db.printTitles(connection, bookList);
                }
                case 2 -> {
                    db.printAuthors(connection, authorList);
                }
            }
        }
    }
}

