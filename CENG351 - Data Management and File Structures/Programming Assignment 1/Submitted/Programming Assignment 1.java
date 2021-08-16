package ceng.ceng351.bookdb;

import ceng.ceng351.bookdb.IBOOKDB;
import com.mysql.cj.protocol.Resultset;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

public class BOOKDB implements IBOOKDB {
    private static String user = "2280386";
    private static String password = "3e80b4c9"; 
    private static String host = "144.122.71.65"; 
    private static String database = "db2280386";
    private static int port = 8084; 

    private Connection con;

    public BOOKDB(){
        // TODO Auto-generated constructor stub
    }

    @Override
    public void initialize() {
        String url = "jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.con =  DriverManager.getConnection(url, this.user, this.password);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } 
    }

    //ok
    @Override
    public int createTables() {

        int result;
        int numberofTablesInserted = 0;

        String queryCreateAuthorTable =     "create table           author ( " +
                                            "author_id              int not null, " +
                                            "author_name            varchar(60), " + 
                                            "PRIMARY KEY            (author_id));"; 

        String queryCreatePublisherTable  = "create table           publisher ( "+
                                            "publisher_id           int not null, " +
                                            "publisher_name         varchar(50), " +
                                            "PRIMARY KEY            (publisher_id));";

        String queryCreateBookTable =       "create table           book ( " + 
                                            "isbn                   char(13) not null, " +
                                            "book_name              varchar(120), " + 
                                            "publisher_id           int, " + 
                                            "first_publish_year     char(4), " +
                                            "page_count             int, " +
                                            "category               varchar(25), " +
                                            "rating                 float, " +
                                            "PRIMARY KEY            (isbn), " +
                                            "FOREIGN KEY            (publisher_id) REFERENCES publisher(publisher_id)  ON UPDATE CASCADE ON DELETE CASCADE);";
        
        String queryCreateAuthor_ofTable =  "create table           author_of ( " + 
                                            "isbn                   char(13) not null, " +
                                            "author_id              int, " +
                                            "PRIMARY KEY            (isbn, author_id), " +
                                            "FOREIGN KEY            (isbn) REFERENCES book(isbn) ON UPDATE CASCADE ON DELETE CASCADE, " +
                                            "FOREIGN KEY            (author_id) REFERENCES author(author_id) ON UPDATE CASCADE ON DELETE CASCADE);";

        String queryCreatePhw1Table =       "create table           phw1 ( " + 
                                            "isbn                   char(13) not null, " +
                                            "book_name              varchar(120), " + 
                                            "rating                 float, " + 
                                            "PRIMARY KEY            (isbn));";
        
        try {
            Statement statement = this.con.createStatement();

            result = statement.executeUpdate(queryCreateAuthorTable);
            numberofTablesInserted++;
            result = statement.executeUpdate(queryCreatePublisherTable);
            numberofTablesInserted++;
            result = statement.executeUpdate(queryCreateBookTable);
            numberofTablesInserted++;
            result = statement.executeUpdate(queryCreateAuthor_ofTable);
            numberofTablesInserted++;
            result = statement.executeUpdate(queryCreatePhw1Table);
            numberofTablesInserted++;

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return numberofTablesInserted;
    }

    //ok
    @Override
    public int insertAuthor(Author[] authors) {
        int result = 0;
        int size_of_authors = authors.length;
        int number_of_inserted_authors = 0;
        for(int i = 0; i < size_of_authors; i++)
        {
            String query = "insert into author values(\""   +
                            authors[i].getAuthor_id()       + "\",\"" +
                            authors[i].getAuthor_name()     + "\");";
            try {
                Statement st = this.con.createStatement();
                result = st.executeUpdate(query);
                number_of_inserted_authors++;
                st.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return number_of_inserted_authors;
    }

    //ok
    @Override
    public int insertPublisher(Publisher[] publishers) {
        int result = 0;
        int size_of_publishers = publishers.length;
        int number_of_inserted_publishers = 0;
        for (int i = 0; i < size_of_publishers; i++) {
            String query = "insert into publisher values(\""        +
                            publishers[i].getPublisher_id()         + "\",\"" +
                            publishers[i].getPublisher_name()       + "\");";
            try {
                Statement st = this.con.createStatement();
                result = st.executeUpdate(query);
                number_of_inserted_publishers++;
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return number_of_inserted_publishers;
    }

    //ok
    @Override
    public int insertBook(Book[] books) {
        int result = 0;
        int size_of_books = books.length;
        int number_of_inserted_books = 0;
        for (int i = 0; i < size_of_books; i++) {
            String query = "insert into book values(\"" +
                    books[i].getIsbn()                  + "\",\"" +
                    books[i].getBook_name()             + "\",\"" +
                    books[i].getPublisher_id()          + "\",\"" +
                    books[i].getFirst_publish_year()    + "\",\"" +
                    books[i].getPage_count()            + "\",\"" +
                    books[i].getCategory()              + "\",\"" +
                    books[i].getRating()                + "\");";
            try {
                Statement st = this.con.createStatement();
                result = st.executeUpdate(query);
                number_of_inserted_books++;
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return number_of_inserted_books;
    }

    //ok
    @Override
    public int insertAuthor_of(Author_of[] author_ofs) {
        int result = 0;
        int size_of_author_ofs = author_ofs.length;
        int number_of_inserted_author_ofs = 0;
        for (int i = 0; i < size_of_author_ofs; i++) {
            String query = "insert into author_of values(\"" +
                    author_ofs[i].getIsbn() + "\",\"" +
                    author_ofs[i].getAuthor_id() + "\");";
            try {
                Statement st = this.con.createStatement();
                result = st.executeUpdate(query);
                number_of_inserted_author_ofs++;
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return number_of_inserted_author_ofs;
    }

    //ok
    @Override
    public int dropTables() {

        int result;
        int numberofTablesDropped = 0;

        String queryDropAuthorTable       = "drop table if exists author";
        String queryDropPublisherTable    = "drop table if exists publisher";
        String queryDropBookTable         = "drop table if exists book";
        String queryDropAuthor_ofTable    = "drop table if exists author_of";
        String queryDropPhw1Table         = "drop table if exists phw1";
        try {
            Statement statement = this.con.createStatement();
            result = statement.executeUpdate(queryDropPhw1Table);
            numberofTablesDropped++;
            result = statement.executeUpdate(queryDropAuthor_ofTable);
            numberofTablesDropped++;
            result = statement.executeUpdate(queryDropBookTable);
            numberofTablesDropped++;
            result = statement.executeUpdate(queryDropAuthorTable);
            numberofTablesDropped++;
            result = statement.executeUpdate(queryDropPublisherTable);
            numberofTablesDropped++;
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return numberofTablesDropped;
    }

    //OK
    @Override
    public QueryResult.ResultQ1[] functionQ1() {
        QueryResult.ResultQ1[] rax = new QueryResult.ResultQ1[0];

        String query =  "SELECT B.isbn, B.first_publish_year, B.page_count, P.publisher_name " +
                        "FROM book B, publisher P " +
                        "WHERE B.publisher_id = P.publisher_id and B.page_count = (SELECT MAX(B2.page_count) FROM book B2) " +
                        "ORDER BY B.isbn ASC;";

        try{
            Statement statement = this.con.createStatement();
            ResultSet rsp = statement.executeQuery(query);
            int i = 0;
            while(rsp.next())
            {
                String isbn                 = rsp.getString ("B.isbn");
                String first_publish_year   = rsp.getString ("B.first_publish_year");
                int page_count              = rsp.getInt    ("B.page_count");
                String publisher_name       = rsp.getString ("P.publisher_name");
                QueryResult.ResultQ1 res = new QueryResult.ResultQ1(isbn, first_publish_year, page_count, publisher_name);
                rax = Arrays.copyOf(rax, rax.length+1);
                rax[i] = res;
                i++;
            }
            statement.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return rax;
    }

    //ok
    @Override
    public QueryResult.ResultQ2[] functionQ2(int author_id1, int author_id2) {
        QueryResult.ResultQ2[] rax = new QueryResult.ResultQ2[0];

        String query =  "SELECT B.publisher_id, avg(P.page_count) " +
                        "FROM book B, author_of A1, author_of A2, book P " +
                        "WHERE B.isbn = A1.isbn and A1.author_id = "+author_id1+ " and A2.author_id = "+ author_id2 + " and B.isbn = A2.isbn and A1.author_id <> A2.author_id and P.publisher_id = B.publisher_id " +
                        "GROUP BY B.publisher_id " +
                        "ORDER BY B.publisher_id ASC;";

        try{
            Statement statement = this.con.createStatement();
            ResultSet rsp = statement.executeQuery(query);
            int i = 0;
            while(rsp.next()) {
                int publisher_id    = rsp.getInt    ("B.publisher_id");
                double page_count   = rsp.getDouble ("AVG(P.page_count)");
                QueryResult.ResultQ2 res = new QueryResult.ResultQ2(publisher_id, page_count);
                rax = Arrays.copyOf(rax, rax.length+1);
                rax[i] = res;
                i++;
            }
            statement.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return rax;
    }

    //ok
    @Override
    public QueryResult.ResultQ3[] functionQ3(String author_name) {
        QueryResult.ResultQ3[] rax = new QueryResult.ResultQ3[0];
        String query =  "SELECT B.book_name, B.category, B.first_publish_year " +
                        "FROM book B, author_of A, author A2 " +
                        "WHERE B.isbn = A.isbn and A2.author_name = \"" + author_name + "\" and A.author_id = A2.author_id and " +
                        "B.first_publish_year = (SELECT min(B1.first_publish_year) FROM book B1 WHERE A.isbn = B1.isbn) " +
                        "ORDER BY B.book_name, B.category, B.first_publish_year ASC;";
        try{
            Statement statement = this.con.createStatement();
            ResultSet rsp = statement.executeQuery(query);
            int i = 0;
            while(rsp.next()) {
                String bn   = rsp.getString("B.book_name");
                String cate = rsp.getString("B.category");
                String fpy  = rsp.getString("B.first_publish_year");
                QueryResult.ResultQ3 res = new QueryResult.ResultQ3(bn, cate, fpy);
                rax = Arrays.copyOf(rax, rax.length+1);
                rax[i] = res;
                i++;
            }
            statement.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return rax;
    }

    //ok
    @Override
    public QueryResult.ResultQ4[] functionQ4() {
        QueryResult.ResultQ4[] rax = new QueryResult.ResultQ4[0];
        String query = "SELECT DISTINCT B.publisher_id, B.category " +
                "FROM book B, publisher P " +
                "WHERE   B.publisher_id =  P.publisher_id and (LENGTH(P.publisher_name) - LENGTH(REPLACE(P.publisher_name, ' ', '')) + 1) = 3 and " +
                " 3 <= (    SELECT count(B1.isbn) " +
                            "FROM book B1 " +
                            "WHERE B1.publisher_id = P.publisher_id " +
                            "GROUP BY P.publisher_id) and " +
                "3  <  (    SELECT AVG(B3.rating) " +
                            "FROM book B3 " +
                            "WHERE B3.publisher_id = P.publisher_id " +
                            "GROUP BY  P.publisher_id) " +
                "ORDER BY publisher_id, category ASC;";

        try {
            Statement statement = this.con.createStatement();
            ResultSet rsp = statement.executeQuery(query);
            int i = 0;
            while (rsp.next()) {
                int publisher_id    = rsp.getInt    ("B.publisher_id");
                String category     = rsp.getString ("B.category");
                QueryResult.ResultQ4 res = new QueryResult.ResultQ4(publisher_id, category);
                rax = Arrays.copyOf(rax, rax.length + 1);
                rax[i] = res;
                i++;
            }
            statement.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return rax;
    }

    //OK
    @Override
    public QueryResult.ResultQ5[] functionQ5(int author_id) {
        QueryResult.ResultQ5[] rax = new QueryResult.ResultQ5[0];
        String query =  "SELECT A.author_id, A.author_name " +
                        "FROM author A " +
                        "WHERE NOT EXISTS(  SELECT B.publisher_id " +
                                            "FROM book B, author_of A1 " +
                                            "WHERE A1.isbn = B.isbn and A1.author_id = " + author_id +
                        " and publisher_id NOT IN( " +
                                            "SELECT B2.publisher_id " +
                                            "FROM book B2, author_of A2 " +
                                            "WHERE B2.isbn = A2.isbn and A2.author_id = A.author_id)) " +
                        "ORDER BY author_id ASC;";
        try {
            Statement statement = this.con.createStatement();
            ResultSet rsp = statement.executeQuery(query);
            int i = 0;
            while(rsp.next()) {
                int au1     = rsp.getInt    ("A.author_id");
                String au2  = rsp.getString ("A.author_name");
                QueryResult.ResultQ5 res = new QueryResult.ResultQ5(au1, au2);
                rax = Arrays.copyOf(rax, rax.length+1);
                rax[i] = res;
                i++;
            }
            statement.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return rax;
    }

    @Override
    public QueryResult.ResultQ6[] functionQ6() {
        QueryResult.ResultQ6[] rax = new QueryResult.ResultQ6[0];
        String query = "SELECT A.author_id, A.isbn\n" +
                "FROM author_of A\n" +
                "WHERE A.author_id in (\n" +
                "    SELECT A1.author_id\n" +
                "    FROM author_of A1\n" +
                "    WHERE NOT EXISTS(\n" +
                "        SELECT K.isbn\n" +
                "        FROM book K\n" +
                "        WHERE K.publisher_id in (\n" +
                "            SELECT P.publisher_id\n" +
                "            FROM book B, publisher P\n" +
                "            WHERE B.isbn = A1.isbn and P.publisher_id = B.publisher_id\n" +
                "            )\n" +
                "        and K.isbn NOT IN (SELECT O.isbn FROM author_of O WHERE O.author_id = A1.author_id)\n" +
                "        )\n" +
                "    )\n" +
                "ORDER BY A.author_id, A.isbn ASC;";
        try {
            Statement statement = this.con.createStatement();
            ResultSet rsp = statement.executeQuery(query);
            int i = 0;
            while(rsp.next())
            {
                int author_id   = rsp.getInt    ("A.author_id");
                String isbn     = rsp.getString ("A.isbn");
                QueryResult.ResultQ6 res = new QueryResult.ResultQ6(author_id, isbn);
                rax = Arrays.copyOf(rax, rax.length+1);
                rax[i] = res;
                i++;
            }
            statement.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return rax;
    }

    @Override
    public QueryResult.ResultQ7[] functionQ7(double rating) {
        QueryResult.ResultQ7[] rax = new QueryResult.ResultQ7[0];
        String query = "select  DISTINCT P.publisher_id, P.publisher_name " +
                        "from    publisher P, book B1, book B2 " +
                        "where   P.publisher_id = B1.publisher_id and P.publisher_id = B2.publisher_id " +
                        "and B1.isbn <> B2.isbn and B1.category = 'Roman' and  B2.category = 'Roman' and " + rating + " < (select avg(B3.rating) from book B3 where B3.publisher_id = P.publisher_id " +
                        "order by P.publisher_id ASC);";
        try {
            Statement statement = this.con.createStatement();
            ResultSet rsp = statement.executeQuery(query);
            int i = 0;
            while(rsp.next())
            {
                int     publisher_id = rsp.getInt("publisher_id");
                String publisher_name = rsp.getString("publisher_name");
                QueryResult.ResultQ7 res = new QueryResult.ResultQ7(publisher_id, publisher_name);
                rax = Arrays.copyOf(rax, rax.length+1);
                rax[i] = res;
                i++;
            }
            statement.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return rax;
    }

    @Override
    public QueryResult.ResultQ8[] functionQ8() {
        QueryResult.ResultQ8[] rax = new QueryResult.ResultQ8[0];
        String query = "INSERT INTO phw1(isbn, book_name, rating) " +
                "select B.isbn, B.book_name, B.rating \n" +
                "from book B, book K \n" +
                "where B.isbn <> K.isbn and B.book_name = K.book_name and B.rating = (select min(P.rating) from book P where P.book_name = B.book_name);";

        String asd = "select * from phw1 order by isbn";
        try {
            Statement statement = this.con.createStatement();
            statement.executeUpdate(query);
            ResultSet rsp = statement.executeQuery(asd);
            int i = 0;
            while(rsp.next())
            {
                String isbn = rsp.getString("isbn");
                String book_name = rsp.getString("book_name");
                double rating = rsp.getDouble("rating");
                QueryResult.ResultQ8 res = new QueryResult.ResultQ8(isbn, book_name, rating);
                rax = Arrays.copyOf(rax, rax.length+1);
                rax[i] = res;
                i++;
            }
            statement.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return rax;
    }

    @Override
    public double functionQ9(String keyword) {
        String query = "update book B " +
                "set B.rating = B.rating+1 " +
                "where B.book_name like \"%" + keyword + "%\" and B.rating <= 4;";

        String suery = "select sum(rating) from book";
        double rat = 0.0;
        try{
            Statement statement = this.con.createStatement();
            statement.executeUpdate(query);
            ResultSet rsi = statement.executeQuery(suery);
            rsi.next();
            rat = rsi.getDouble(1);
            statement.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return rat;
    }

    @Override
    public int function10() {
        String query =  "Delete from publisher " +
                        "where publisher_id in " +
                        "(select P.publisher_id from (select P1.publisher_id from publisher P1 where P1.publisher_id not in (select B.publisher_id from book B)) as P);";

        String puery = "select count(*) from publisher;";
        int i = 0;
        try {
            Statement statement = this.con.createStatement();
            statement.executeUpdate(query);
            ResultSet rsi = statement.executeQuery(puery);
            rsi.next();
            i = rsi.getInt(1);
            statement.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return i;
    }

}