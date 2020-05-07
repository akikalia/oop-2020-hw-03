import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MetropolisTest {

        private Connection conn;
        private MetropolisDAO metropolisDAO;
        private List<Metropolis> view;
        private Metropolis metropolis;
        private String username = "root";
        private String password = "";
        private String database = "test_database";

        @BeforeEach
        public void setUp() throws ClassNotFoundException, SQLException {

            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost",
                    username,
                    password);
            Statement stmt = conn.createStatement();
            //stmt.executeUpdate("DROP DATABASE " + database+"");
            stmt.executeUpdate("CREATE DATABASE " + database+"");
            stmt.executeQuery("USE " + database+";");
            stmt.executeUpdate("CREATE TABLE metropolises (" +
                    "    metropolis CHAR(64)," +
                    "    continent CHAR(64)," +
                    "    population BIGINT" +
                    ")");

            view = new ArrayList<>();
            metropolisDAO = new MetropolisDAO(conn, view);
        }

        @AfterEach
        public void tearDown() throws SQLException {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("DROP TABLE metropolises");
            stmt.executeUpdate("DROP DATABASE "+database + " ");
            conn.close();
        }

        private void createTwo(){
            metropolis = new Metropolis("Kiev", "Europe", "3000000");
            metropolisDAO.addMetropolis(new Metropolis(metropolis));
            metropolis = new Metropolis("Riga", "Europe", "700000");
            metropolisDAO.addMetropolis(new Metropolis(metropolis));
            metropolis = new Metropolis("", "", "");
        }
        //checks if add method, adds entry to the view and that entry is the only one
        @Test
        public void testSimpleAdd() {
            assertTrue(view.size() == 0);
            metropolis = new Metropolis("Kiev", "Europe", "3000000");
            metropolisDAO.addMetropolis(new Metropolis(metropolis));
            assertTrue(view.get(0).getMetropolis().equals("Kiev"));
            assertTrue(view.get(0).getPopulation().equals("3000000"));
            assertTrue(view.get(0).getContinent().equals("Europe"));
            assertTrue(view.size() == 1);
        }

        private Object getQueryFirstRes(String query, int i){
            PreparedStatement stm = null;

            try {
                stm = conn.prepareStatement(query);
                ResultSet res = stm.executeQuery();
                res.next();
                return res.getObject(i);
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }

        //tests if add places entries in sql correctly
        @Test
        public void testSQLAdd() {
            metropolis = new Metropolis("Kiev", "Europe", "3000000");
            metropolisDAO.addMetropolis(metropolis);
            assertTrue(getQueryFirstRes("SELECT * FROM metropolises;", 1).equals("Kiev"));
            assertTrue(getQueryFirstRes("SELECT * FROM metropolises;", 2).equals("Europe"));
            assertTrue(((Long)getQueryFirstRes("SELECT * FROM metropolises;", 3)).longValue() == 3000000);
            assertTrue(((Long)(getQueryFirstRes("select count(*) from metropolises;", 1))).longValue() == 1);
        }

        //tests if add places only current entry in view
        @Test
        public void testComplexAdd() {
            createTwo();

            assertTrue(view.get(0).getByColumn(0).equals("Riga"));
            assertTrue(view.get(0).getByColumn(2).equals("700000"));
            assertTrue(view.get(0).getByColumn(1).equals("Europe"));
            assertTrue(view.get(0).getByColumn(3) == null);
            assertTrue(view.size() == 1);
        }

        //tests if add places entries in sql correctly
        @Test
        public void testSQLAdd2() {
            createTwo();

            assertTrue(getQueryFirstRes("select * from metropolises where metropolis = \"Riga\";", 1).equals("Riga"));
            assertTrue(getQueryFirstRes("select * from metropolises where metropolis = \"Riga\";", 2).equals("Europe"));
            assertTrue(((Long)getQueryFirstRes("select * from metropolises where metropolis = \"Riga\";", 3)).longValue() == 700000);
            assertTrue(((Long)getQueryFirstRes("select count(*) from metropolises;", 1)).longValue() == 2);
        }

        @Test
        public void testSearchAll() {
            createTwo();

            metropolis.setPopulation("");
            metropolis.setMetropolis("");
            metropolis.setContinent("");
            metropolisDAO.searchMetropolis(metropolis, false, false);
            assertTrue(view.size() == 2);

            metropolisDAO.searchMetropolis(metropolis, true, false);
            assertTrue(view.size() == 2);

            metropolisDAO.searchMetropolis(metropolis, false, true);
            assertTrue(view.size() == 2);

            metropolisDAO.searchMetropolis(metropolis, true, true);
            assertTrue(view.size() == 2);
        }
        @Test
        public void testSearchPopulation() {
            createTwo();

            metropolis.setPopulation("800000");
            metropolis.setMetropolis("");
            metropolis.setContinent("");
            metropolisDAO.searchMetropolis(metropolis, false, true);
            assertTrue(view.size() == 1);
            assertTrue(view.get(0).getMetropolis().equals("Riga"));
            assertTrue(view.get(0).getContinent().equals("Europe"));
            assertTrue(view.get(0).getPopulation().equals("700000"));

            metropolis.setPopulation("800000");
            metropolis.setMetropolis("");
            metropolis.setContinent("");
            metropolisDAO.searchMetropolis(metropolis, false, false);
            assertTrue(view.size() == 1);
            assertTrue(view.get(0).getMetropolis().equals("Kiev"));

        }

        @Test
        public void testSearchMetropolis() {
            createTwo();

            metropolis.setPopulation("");
            metropolis.setMetropolis("Kiev");
            metropolis.setContinent("");
            metropolisDAO.searchMetropolis(metropolis, false, false);
            assertTrue(view.size() == 1);
            assertTrue(view.get(0).getMetropolis().equals("Kiev"));

            metropolis.setPopulation("");
            metropolis.setMetropolis("i");
            metropolis.setContinent("");
            metropolisDAO.searchMetropolis(metropolis, true, false);
            assertTrue(view.size() == 2);

            metropolis.setPopulation("");
            metropolis.setMetropolis("%i%");
            metropolis.setContinent("");
            metropolisDAO.searchMetropolis(metropolis, false, false);
            assertTrue(view.size() == 2);

            metropolis.setPopulation("");
            metropolis.setMetropolis("i");
            metropolis.setContinent("");
            metropolisDAO.searchMetropolis(metropolis, false, false);
            assertTrue(view.size() == 0);
        }


        //tests if two search arguments work correctly
        @Test
        public void testSearchContinent() {
            createTwo();

            metropolis.setPopulation("");
            metropolis.setMetropolis("Kiev");
            metropolis.setContinent("Europe");
            metropolisDAO.searchMetropolis(metropolis, false, false);
            assertTrue(view.size() == 1);
            assertTrue(view.get(0).getMetropolis().equals("Kiev"));

            metropolis.setPopulation("");
            metropolis.setMetropolis("i");
            metropolis.setContinent("Europe");
            metropolisDAO.searchMetropolis(metropolis, true, false);
            assertTrue(view.size() == 2);

            metropolis.setPopulation("");
            metropolis.setMetropolis("%i%");
            metropolis.setContinent("%rop%");
            metropolisDAO.searchMetropolis(metropolis, false, false);
            assertTrue(view.size() == 2);

            metropolis.setPopulation("");
            metropolis.setMetropolis("i");
            metropolis.setContinent("%rop%");
            metropolisDAO.searchMetropolis(metropolis, false, false);
            assertTrue(view.size() == 0);
        }

        //tests if all search arguments work correctly
        @Test
        public void testSearchAllArguments() {
            createTwo();

            metropolis.setPopulation("800000");
            metropolis.setMetropolis("Kiev");
            metropolis.setContinent("Europe");
            metropolisDAO.searchMetropolis(metropolis, false, true);
            assertTrue(view.size() == 0);

            metropolis.setPopulation("800000");
            metropolis.setMetropolis("i");
            metropolis.setContinent("Europe");
            metropolisDAO.searchMetropolis(metropolis, true, false);
            assertTrue(view.size() == 1);
        }

}