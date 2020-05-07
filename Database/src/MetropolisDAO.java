import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class MetropolisDAO {
    Connection conn;
    List<Metropolis> view;

    /**
     * database constructor
     */
    public MetropolisDAO(Connection conn, List<Metropolis> view){
        this.conn = conn;
        this.view = view;

    }

    /**
     * adds metropolis entry to MYSQL database
     * sets view array to contain only current entry
     *@param entry
     */
    public void addMetropolis(Metropolis entry){
        PreparedStatement stm = null;
        int popul;
        try {
            try {
                popul = Integer.parseInt(entry.getPopulation());
            } catch(NumberFormatException e){
                popul = 0;
            }
            stm = conn.prepareStatement(
                    "INSERT INTO metropolises VALUES\n" +
                            "(\""+entry.getMetropolis()+"\",\""+entry.getContinent()+"\","+popul+");");
            stm.executeUpdate();
            view.clear();
            view.add(new Metropolis(entry.getMetropolis(), entry.getContinent(), String.valueOf(popul)));
        } catch (SQLException e) { e.printStackTrace(); }
    }

    /**
     * constructs sql query based on given flags and textfield state
     *
     * @param entry entry to search for
     * @param mt exact match flag
     * @param plt population less than flag
     *
     */
    private String createSearchQuery(Metropolis entry, boolean mt, boolean plt){
        String population =  entry.getPopulation();
        String continent = entry.getContinent();
        String metropolis = entry.getMetropolis();

        String query;
        String matchType;
        String popCmp;
        int popul;
        matchType = mt ? "%" : "";
        popCmp = plt ? "<" : ">";


        query = " SELECT * FROM metropolises ";

        try {
            popul = Integer.parseInt(entry.getPopulation());
        } catch(NumberFormatException e){
            popul = 0;
        }
        query = !metropolis.isEmpty() || !continent.isEmpty() || !population.isEmpty() ? query +  " where " : query;
        query = !metropolis.isEmpty() ?  query + " metropolis "+ "LIKE" +" \""+matchType+metropolis+matchType+"\" " : query ;
        query = !metropolis.isEmpty() && !continent.isEmpty() ? query + " and ": query;
        query = !continent.isEmpty() ?  query + " continent "+ "LIKE" +" \""+matchType+ continent+matchType+"\" " : query ;
        query = (!metropolis.isEmpty() || !continent.isEmpty()) && !population.isEmpty()  ? query + " and ": query;
        query = !population.isEmpty() ?  query + " population " + popCmp+" "+popul : query;

        query += ";";
        return query;
    }

    /**
     * searches sql table for specified
     * sets view array to contain search result
     * search result can be empty (in case entry was not found)
     * or it might contain full table (in case given metropolis fields were empty)
     *
     * @param entry entry to search for
     * @param mt exact match flag
     * @param plt population less than flag
     *
     */

    public void searchMetropolis(Metropolis entry, boolean mt, boolean plt){
        PreparedStatement stm = null;

        String query = createSearchQuery(entry, mt, plt);
        try {
            stm = conn.prepareStatement(query);
            ResultSet res = stm.executeQuery();

            view.clear();
            while(res.next())
                view.add(new Metropolis(res.getString(1),res.getString(2), String.valueOf(res.getInt(3))));
        } catch (SQLException e){ e.printStackTrace();}
    }
}
