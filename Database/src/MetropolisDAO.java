import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class MetropolisDAO {
    Connection conn;
    List<Metropolis> view;
    public MetropolisDAO(Connection conn, List<Metropolis> view){
        this.conn = conn;
        this.view = view;

    }

    public void addMetropolis(Metropolis entry){
        PreparedStatement stm = null;
        try {
            stm = conn.prepareStatement(
                    "INSERT INTO metropolises VALUES\n" +
                            "(\""+entry.getMetropolis()+"\",\""+entry.getContinent()+"\",\""+entry.getPopulation()+"\");");
            stm.executeUpdate();
            view.clear();
            view.add(entry);
        } catch (SQLException e) { e.printStackTrace(); }
    }


    private String createSearchQuery(Metropolis entry, boolean mt, boolean plt){
        String population =  entry.getPopulation();
        String continent = entry.getContinent();
        String metropolis = entry.getMetropolis();

        String query;
        String matchType;
        String popCmp;

        matchType = mt ? "%" : "";
        popCmp = plt ? "<" : ">";


        query = " SELECT * FROM metropolises ";


        query = !metropolis.isEmpty() || !continent.isEmpty() || !population.isEmpty() ? query +  " where " : query;
        query = !metropolis.isEmpty() ?  query + " metropolis "+ "LIKE" +" \""+matchType+metropolis+matchType+"\" " : query ;
        query = !metropolis.isEmpty() && !continent.isEmpty() ? query + " and ": query;
        query = !continent.isEmpty() ?  query + " continent "+ "LIKE" +" \""+matchType+ continent+matchType+"\" " : query ;
        query = (!metropolis.isEmpty() || !continent.isEmpty()) && !population.isEmpty()  ? query + " and ": query;
        query = !population.isEmpty() ?  query + " population " + popCmp+" "+entry.getPopulation() : query;

        query += ";";
        return query;
    }

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
