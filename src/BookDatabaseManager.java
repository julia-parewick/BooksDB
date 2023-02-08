import org.mariadb.jdbc.export.Prepare;

import java.sql.*;
import java.util.*;

public class BookDatabaseManager {

    private static Connection getConnection(){
        Connection connection = null;
        try{
            connection = DriverManager
                    .getConnection(DBConfiguration.DB_URL+DBConfiguration.DB_BOOKS,DBConfiguration.DB_USER,DBConfiguration.DB_PASSWORD);
        }catch (SQLException sqlException){
            sqlException.printStackTrace();
            System.exit(1);
        }
        return connection;
    }

    public static List<Book> loadBookList(Connection connection) throws SQLException {
        LinkedList<Book> bookList = new LinkedList<>();
        Statement statement = connection.createStatement();
        String sql = "select * from titles";
        ResultSet results = statement.executeQuery(sql);
        while(results.next()){
            bookList.add(new Book(
               results.getString(DBConfiguration.DB_BOOKS_TITLES_TITLE),
               results.getString(DBConfiguration.DB_BOOKS_TITLES_ISBN),
               results.getInt(DBConfiguration.DB_BOOKS_TITLES_EDITION),
               results.getString(DBConfiguration.DB_BOOKS_TITLES_COPYRIGHT)

            ));
        }
        return bookList;
    }

    public static List<Author> loadAuthorList(Connection connection) throws SQLException {
        LinkedList<Author> authorList = new LinkedList<>();
        Statement statement = connection.createStatement();
        String sql = "select * from authors";
        ResultSet results = statement.executeQuery(sql);
        while(results.next()){
            authorList.add(new Author(
                    results.getInt(DBConfiguration.DB_BOOKS_AUTHORS_ID),
                    results.getString(DBConfiguration.DB_BOOKS_AUTHORS_FNAME),
                    results.getString(DBConfiguration.DB_BOOKS_AUTHORS_LNAME)
            ));
        }
        return authorList;
    }

    public static List<Integer> getBookAuthors(Book book) throws SQLException {
        Connection connection =  getConnection();

        List<Integer> authorIDs = new LinkedList<>();

        String sql = "select authorID from authorisbn where isbn=?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, book.getIsbn());
        ResultSet results = statement.executeQuery();
        while(results.next()){
            authorIDs.add(results.getInt(1));
        }
        return authorIDs;
    }

    public static boolean checkBook(Connection connection, String isbn) throws SQLException {
        String checkBooksQuery = "select * from authorisbn where isbn=?";
        PreparedStatement checkBooksStmt = connection.prepareStatement(checkBooksQuery);
        checkBooksStmt.setString(1, isbn);
        ResultSet bookCheck = checkBooksStmt.executeQuery();
        if(bookCheck.last()){
            return true;
        }
        return false;
    }

    public static boolean checkAuthor(Connection connection, String fname, String lname) throws SQLException {
        String checkAuthorQuery = "select * from authors where firstName=? and lastName=?";
        PreparedStatement checkAuthorStmt = connection.prepareStatement(checkAuthorQuery);
        checkAuthorStmt.setString(1, fname);
        checkAuthorStmt.setString(2, lname);
        ResultSet authorCheck = checkAuthorStmt.executeQuery();
        if (authorCheck.last()){
            return true;
        }
        return false;
    }

    public static void registerBook(Connection connection, String isbn, String title, int edition, String copyright) throws SQLException {
        if(!checkBook(connection, isbn)){
            String titlesInsert = "insert into titles values(?,?,?,?)";
            PreparedStatement titlesInsertStmt = connection.prepareStatement(titlesInsert);
            titlesInsertStmt.setString(1,isbn);
            titlesInsertStmt.setString(2,title);
            titlesInsertStmt.setInt(3, edition);
            titlesInsertStmt.setString(4,copyright);
            titlesInsertStmt.execute();
            System.out.printf("%s has been registered in the DB!\n", title);
        }else{
            System.out.println("Book already registered.");

        }
    }

    public static void registerAuthor(Connection connection, String fname, String lname) throws SQLException {
        if(!checkAuthor(connection,fname,lname)){
            String insertAuthorQuery = "insert into authors values(null,?,?)";
            PreparedStatement insertAuthorStmt = connection.prepareStatement(insertAuthorQuery);
            insertAuthorStmt.setString(1, fname);
            insertAuthorStmt.setString(2, lname);
            insertAuthorStmt.execute();
            System.out.printf("Author %s %s has been registered to DB!\n", fname, lname);
        }else{
            System.out.println("Author already registered.");
        }
    }

    public static int findID(Connection connection, String fname, String lname) throws SQLException {
        int authorID;

        String authorIdQuery = "select authorID from authors where firstName=? and lastName=?";
        PreparedStatement getAuthorId = connection.prepareStatement(authorIdQuery);
        getAuthorId.setString(1,fname);
        getAuthorId.setString(2,lname);

        ResultSet id = getAuthorId.executeQuery();
        id.last();
        authorID = id.getInt(1);
        return authorID;
    }

    public static void updateForeignKeys(Connection connection, int authorID, String isbn) throws SQLException {
        String sql = "insert into authorisbn values(?,?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1,authorID);
        statement.setString(2, isbn);
        statement.executeUpdate();
    }

    public static void removeBook(Connection connection, String isbn) throws SQLException {
        String deleteFKQuery = "delete from authorisbn where isbn=?";
        PreparedStatement deleteFKStmt = connection.prepareStatement(deleteFKQuery);
        deleteFKStmt.setString(1,isbn);
        deleteFKStmt.execute();

        String deleteTitleQuery = "delete from titles where isbn=?";
        PreparedStatement deleteTitleStmt = connection.prepareStatement(deleteTitleQuery);
        deleteTitleStmt.setString(1, isbn);
        deleteTitleStmt.execute();
        System.out.printf("This title entry has been deleted!");

    }

    public static void removeAuthorWorks(Connection connection, int authorID) throws SQLException {

        String checkAuthorQuery = "select * from authorisbn where authorID=?";
        PreparedStatement checkAuthorStmt = connection.prepareStatement(checkAuthorQuery);
        checkAuthorStmt.setInt(1, authorID);
        ResultSet dbEntry = checkAuthorStmt.executeQuery();
        if(dbEntry.last()){
            String deleteFKQuery = "delete from authorisbn where authorID=?";
            PreparedStatement deleteFKStmt = connection.prepareStatement(deleteFKQuery);
            deleteFKStmt.setInt(1, authorID);
            deleteFKStmt.execute();

            String deleteAuthorQuery = "delete from authors where authorID=?";
            PreparedStatement deleteAuthorStmt = connection.prepareStatement(deleteAuthorQuery);
            deleteAuthorStmt.setInt(1, authorID);
            deleteAuthorStmt.execute();

            while(dbEntry.next()){
                String deleteTitleQuery = "delete from titles where isbn=?";
                PreparedStatement deleteTitleStmt = connection.prepareStatement(deleteTitleQuery);

                String checkAuthorsQuery = "select authorID from authorisbn where isbn=?";
                PreparedStatement checkAuthorsStmt = connection.prepareStatement(checkAuthorsQuery);
                checkAuthorsStmt.setString(1, dbEntry.getString(2));
                ResultSet authors = checkAuthorsStmt.executeQuery();
                authors.last();
                if(authors.getRow()==1){
                    deleteTitleStmt.setString(1, dbEntry.getString(2));
                    deleteTitleStmt.execute();
                }
            }

            System.out.printf("Author and all associated works have been deleted!");
        }
        else{
            System.out.println("Author with this ID does not exist!");
        }
    }

    public static void updateBook(Connection connection, String isbn, String title, int edition, String copyright) throws SQLException {
        String sql = "update titles set title=?, editionNumber=?, copyright=? where isbn=?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1,title);
        statement.setInt(2,edition);
        statement.setString(3,copyright);
        statement.setString(4,isbn);
        statement.executeUpdate();
        System.out.printf("Entry %s has been updated!", isbn);
    }
    public static void updateBookPrompt(Connection connection, String isbn) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        if(isbn.equals("")){
            System.out.println("ISBN: ");
            while(true){
                try{
                    isbn = scanner.nextLine();
                    if(checkBook(connection, isbn)){
                        break;
                    }else{
                        throw new InputMismatchException();
                    }
                }catch(InputMismatchException ime){
                    System.out.println("Invalid input. Please retry: ");
                }
            }
        }

        System.out.println("Please enter required fields or enter 0 to exit...\nTitle: ");

        String new_title;
        int new_ed;
        String new_copyright;
        while(true){
            try{
                new_title = scanner.nextLine();
                break;
            }catch(InputMismatchException ime){
                System.out.println("Invalid input. Please retry: ");
            }
        }
        System.out.println("Edition: ");
        while(true){
            try{
                new_ed = scanner.nextInt();
                break;
            }catch(InputMismatchException ime){
                System.out.println("Invalid input. Please retry: ");
            }
        }
        System.out.println("Copyright: ");
        while(true){
            try{
                new_copyright = scanner.next();
                break;
            }catch(InputMismatchException ime){
                System.out.println("Invalid input. Please retry: ");
            }
        }
        updateBook(connection,isbn,new_title,new_ed,new_copyright);
    }

    public static void updateAuthorPrompt(Connection connection, int id){
        Scanner scanner = new Scanner(System.in);
        if(id==0){

        }
    }

    public static void viewBookInfo(Connection connection, List<Book> bookList) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        String isbn="";
        System.out.println("ISBN: ");
        while(true){
            try{
                isbn = scanner.nextLine();
                if (!checkBook(connection, isbn)) {
                    throw new InputMismatchException();
                }
                break;
            }catch (InputMismatchException ime) {
                System.out.println("Invalid input. Please retry: ");
            }

        }
        String finalIsbn = isbn;
        bookList.forEach(
                book -> {
                    if (finalIsbn.equals(book.getIsbn())) {

                        System.out.printf("%-15s%15s%60s%15s\n", "ISBN", "Title", "Edition", "Copyright");
                        System.out.format("%-15s%-60s%15s%15s", book.getIsbn(), book.getTitle(), book.getEdition(), book.getCopyright());
                        System.out.println();
                        System.out.print("Authors:");
                        int last = book.getAuthorList().size();

                        for(int i = 1; i<=last;i++){
                            if(i==last){
                                System.out.printf("%s %s\n", book.getAuthorList().get(i-1).getFname(), book.getAuthorList().get(i-1).getLname());
                                i++;
                            }
                            else{
                                System.out.printf("%s %s, ", book.getAuthorList().get(i-1).getFname(), book.getAuthorList().get(i-1).getLname());
                            }
                        }

                        System.out.println("0 - Back\n1 - Edit\n2 - Delete");
                        int choose;
                        while(true){
                            try {
                                choose = scanner.nextInt();
                                if(choose>2){
                                    throw new InputMismatchException();
                                }
                                break;
                            } catch (InputMismatchException ime) {
                                System.out.println("Invalid input. Please retry: ");
                            }
                        }
                        switch(choose){
                            case 0 -> {

                            }
                            case 1 -> {
                                try {
                                    updateBookPrompt(connection, finalIsbn);
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            case 2 -> {
                                try {
                                    removeBook(connection, finalIsbn);
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    }
                }
        );
    }

    public static void viewAuthorInfo(Connection connection, List<Author> authorList) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        int authorID = 0;
        while(true){
            try{
                System.out.print("Enter author ID: ");
                authorID = scanner.nextInt();
                break;
            }catch(InputMismatchException ime){
                System.out.println("Invalid input. Please retry: ");
            }
        }
        int finalAuthorID = authorID;
        authorList.forEach(
                author->{
                    if(author.getAuthorID()== finalAuthorID){
                        System.out.printf("%-5s%-15s%-15s\n", "ID", "First Name", "LastName");
                        System.out.format("%-5s%-15s%-15s", author.getAuthorID(), author.getFname(), author.getLname());
                        System.out.println();
                        System.out.println("Titles by author:");
                        System.out.printf("%-15s%15s%60s%15s\n", "ISBN", "Title", "Edition", "Copyright");
                        author.getBookList().forEach(
                                book -> {
                                    System.out.format("%-15s%-60s%15s%15s", book.getIsbn(), book.getTitle(), book.getEdition(), book.getCopyright());
                                    System.out.println();
                                }
                        );

                    }
                }
        );
        System.out.println("0 - Back\n1 - Edit\n2 - Delete");
        int chooseAction;
        while(true){
            try{
                chooseAction = scanner.nextInt();
                if(chooseAction>2){
                    throw new InputMismatchException();
                }
                break;
            }catch(InputMismatchException ime){
                System.out.println("Invalid input. Please retry: ");
            }
        }
        switch (chooseAction){
            case 0 -> {
            }
            case 2 -> {
                updateAuthorPrompt(connection, finalAuthorID);
                connection.close();
            }
            case 3 -> {
                removeAuthorWorks(connection, finalAuthorID);
                connection.close();
            }
        }
    }

    public static void addBookPrompt(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("ISBN: ");

        String isbn = scanner.nextLine();

        String title = "";
        int edition = 0;
        Integer year = 0;
        int a;

        String fname;
        String lname;

        if(!checkBook(connection, isbn)){

            System.out.println("Title: ");
            title = scanner.nextLine();

            while(true) {
                try{
                    System.out.println("Edition: ");
                    edition = scanner.nextInt();
                    break;
                }catch(InputMismatchException ime){
                    System.out.println("Invalid input. Please retry: ");
                }

            }
            System.out.println("Copyright: ");
            year = scanner.nextInt();

            registerBook(connection, isbn,title,edition,year.toString());
            while(true){
                try{
                    System.out.print("How many authors?: ");
                    a = scanner.nextInt();
                    break;
                }catch(InputMismatchException ime){
                    System.out.println("Invalid input. Please retry: ");
                }
            }
            while(true){
                try{
                    for(int i=0;i<a;i++){
                        System.out.println(i+1+":");
                        System.out.print("First Name: ");
                        fname = scanner.next();
                        System.out.print("Last Name: ");
                        lname = scanner.next();

                        if(!checkAuthor(connection, fname,lname)){
                            registerAuthor(connection, fname,lname);
                        }
                        int authorID = findID(connection, fname,lname);
                        updateForeignKeys(connection, authorID, isbn);
                    }
                    break;
                }catch(InputMismatchException ime){
                    System.out.println("Invalid input. Please retry: ");
                }
            }

        }else{
            System.out.println("Title already registered!");
        }
    }

    public static void deleteBookPrompt(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter ISBN of book to delete: ");
        while(true) {
            String isbn = scanner.next();

            if(checkBook(connection, isbn)){
                removeBook(connection, isbn);
            }else {
                System.out.println("Title not found.");
            }
        }
    }

    public static void addAuthorPrompt(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("First Name: ");
        String fname = scanner.nextLine();
        System.out.print("Last Name: ");
        String lname = scanner.nextLine();
        if(!checkAuthor(connection, fname,lname)){
            registerAuthor(connection, fname, lname);
        }
        else{
            System.out.println("Author is already registered!");
        }
    }

    public static void deleteAuthorPrompt(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("*WARNING* Deleting an author will result in all the author titles also being deleted.\nEnter ID of author you wish to delete:");
        int id;
        while(true){
            try{
               id = scanner.nextInt();
               break;
            }catch (InputMismatchException ime) {
                System.out.print("Invalid input. Please retry: ");
            }
        }
        removeAuthorWorks(connection, id);
        connection.close();
    }

    public static void printTitles(Connection connection, List<Book> bookList) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.printf("%-15s%15s%60s%15s\n", "ISBN", "Title", "Edition", "Copyright");
        bookList.forEach(
                book -> {
                    System.out.format("%-15s%-60s%15s%15s", book.getIsbn(), book.getTitle(), book.getEdition(), book.getCopyright());
                    System.out.println();
                }
        );
        int chooseAction = 4;
        System.out.println("0 - Exit\n1 - Select\n2 - Add/Remove\n3 - Edit");
        while (true) {
            try {
                chooseAction = scanner.nextInt();
            } catch (InputMismatchException ime) {
                System.out.println("Invalid input. Please retry: ");
            }
            switch (chooseAction) {
                case 0 -> {
                    exitProgram(connection);
                }
                case 1 -> {
                    viewBookInfo(connection, bookList);
                }
                case 2 -> {
                    System.out.println("Would you like to add or remove a book?\n0 - Exit\n1 - Add\n2 - Remove");
                    int chooseAction2 = 3;
                    while(true){
                        try{
                            chooseAction2 = scanner.nextInt();
                            if(chooseAction2>2){
                                throw new InputMismatchException();
                            }
                        }catch(InputMismatchException ime){
                            System.out.println("Invalid input. Please retry: ");
                        }
                        switch(chooseAction2){
                            case 0 -> {
                                exitProgram(connection);
                            }
                            case 1 -> {
                                addBookPrompt(connection);
                                connection.close();
                            }
                            case 2 -> {
                                deleteBookPrompt(connection);
                                connection.close();
                            }
                        }
                        break;
                    }
                }
                case 3 -> {
                    updateBookPrompt(connection, "");
                    connection.close();
                }
            }
            break;
        }
    }

    public static void printAuthors(Connection connection, List<Author> authorList) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.printf("%-5s%-15s%-15s\n", "ID", "First Name", "LastName");
        authorList.forEach(
                author -> {
                    System.out.format("%-5s%-15s%-15s", author.getAuthorID(), author.getFname(), author.getLname());
                    System.out.println();
                }
        );
        int chooseAction = 4;
        System.out.println("0 - Exit\n1 - Select\n2 - Add/Remove\n3 - Edit");
        while (true) {
            try {
                chooseAction = scanner.nextInt();
            } catch (InputMismatchException ime) {
                System.out.println("Invalid input. Please retry: ");
            }
            switch (chooseAction) {
                case 0 -> {
                    exitProgram(connection);
                }
                case 1 -> {
                    viewAuthorInfo(connection, authorList);
                }
                case 2 -> {
                    System.out.println("Would you like to add or remove an author?\n0 - Exit\n1 - Add\n2 - Remove");
                    int chooseAction2 = 3;
                    while(true){
                        try{
                            chooseAction2 = scanner.nextInt();
                            if(chooseAction2>2){
                                throw new InputMismatchException();
                            }
                        }catch(InputMismatchException ime){
                            System.out.println("Invalid input. Please retry: ");
                        }
                        switch(chooseAction2){
                            case 0 -> {
                                exitProgram(connection);
                            }
                            case 1 -> {
                                addAuthorPrompt(connection);
                                connection.close();
                            }
                            case 2 -> {
                                deleteAuthorPrompt(connection);
                                connection.close();
                            }
                        }
                        break;
                    }
                }
            }
            break;
        }
    }

    public static void exitProgram(Connection connection) throws SQLException {
        connection.close();
        System.out.println("Shutting down...");
        System.exit(1);
    }

    public static void main(String[] args) throws SQLException {
        DBConfiguration.doClassForNameRegistration();
        System.out.println("Welcome to the Books Database Manager.");
        while(true){
            Connection connection = getConnection();

            List<Book> bookList = loadBookList(connection);

            List<Author> authorList = loadAuthorList(connection);

            bookList.forEach(
                    book -> {
                        List<Integer> authorIds = null;
                        try {
                            authorIds = getBookAuthors(book);
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

            System.out.println("\n0 - Exit\n1 - View books database\n2 - View authors database");
            Integer chooseTable = 4;
            while(true){
                Scanner scanner = new Scanner(System.in);

                try{
                    chooseTable = scanner.nextInt();
                    if(chooseTable>2){
                        throw new InputMismatchException();
                    }
                }
                catch(InputMismatchException ime){
                    System.out.println("Invalid input. Please retry: ");
                }

                switch (chooseTable) {
                    case 0 -> {
                        exitProgram(connection);
                    }
                    case 1 -> {
                        printTitles(connection, bookList);
                    }
                    case 2 -> {
                        printAuthors(connection, authorList);
                    }
                }
                break;
            }
        }
    }
}