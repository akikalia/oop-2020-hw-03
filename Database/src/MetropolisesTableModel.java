
import javax.swing.table.AbstractTableModel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import java.sql.*;

public class MetropolisesTableModel extends AbstractTableModel {

    private Connection conn;
    private MetropolisDAO metropolisDAO;
    List<Metropolis> viewEntries;
    private String database = "mydatabase";
    private String password = "";
    private String username = "root";

    /**
     * initializes mySQL database
     * constructs an array for view entry bookkeeping
     *
     */
    public MetropolisesTableModel() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost",
                    username,
                    password);
            Statement stmt = conn.createStatement();
            stmt.executeQuery("USE " + database);
            viewEntries = new ArrayList<>();
            metropolisDAO = new MetropolisDAO(conn, viewEntries);
        }
        catch(ClassNotFoundException c){
            c.printStackTrace();
        }
        catch (SQLException s ){
            s.printStackTrace();
        }
    }

    @Override
    public int getRowCount() {
        return viewEntries.size();
    }

    @Override
    public int getColumnCount() {
        return Metropolis.columnNames.length;
    }

    @Override
    public Object getValueAt(int row, int col) {

        Metropolis curr = viewEntries.get(row);
        return curr.getByColumn(col);
    }

    @Override
    public String getColumnName(int column) {
        return Metropolis.columnNames[column];
    }

    /**
     *calls database function which searches database for specified entry
     * updates JTable afterward
     *@param metropolis
     *@param isPLT is less than textCombo selected
     *@param isEM is exact match textbox selected
     */
    public void searchRows(Metropolis metropolis, Boolean isPLT, Boolean isEM ) {
        metropolisDAO.searchMetropolis(metropolis, isPLT, isEM);
        fireTableDataChanged();
    }

    /**
     *calls database function which adds entry to database
     * updates JTable afterward
     *@param metropolis
     */
    public void addRow(Metropolis metropolis) {
        metropolisDAO.addMetropolis(metropolis);
        fireTableDataChanged();
    }
}
