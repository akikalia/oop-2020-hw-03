import javax.swing.*;
import javax.swing.border.TitledBorder;

import java.awt.*;
import java.awt.event.*;

public class TableFrame extends JFrame {



    public TableFrame() {
        super("Metropolis Viewer");

        MetropolisesTableModel model = new MetropolisesTableModel();

        setLayout(new BorderLayout());
        Container c = getContentPane();

        //text fields and labels
        JPanel textFields = new JPanel();
        JLabel metrLbl = new JLabel("Metropolis :");
        JLabel contLbl = new JLabel("Content :");
        JLabel populLbl = new JLabel("Population :");
        JTextArea metrTxt = new JTextArea(1, 10);
        metrTxt.setBorder(new TitledBorder(""));
        JTextArea contTxt = new JTextArea(1, 10);
        contTxt.setBorder(new TitledBorder(""));
        JTextArea populTxt = new JTextArea(1, 10);
        populTxt.setBorder(new TitledBorder(""));
        textFields.add(metrLbl);
        textFields.add(metrTxt);
        textFields.add(contLbl);
        textFields.add(contTxt);
        textFields.add(populLbl);
        textFields.add(populTxt);
        textFields.setLayout(new FlowLayout(FlowLayout.LEADING));



        JPanel searchControl= new JPanel();

        JButton addBtn = new JButton("Add");
        JButton searchBtn = new JButton("Search");

        //comboBoxes
        JPanel searchOptions= new JPanel();
        String [] PLTcommands = {"Population Larger Than","Population Smaller Than"};
        JComboBox PLTCombo = new JComboBox(PLTcommands);
        PLTCombo.setSelectedIndex(0);
        String [] EMcommands = {"Exact Match","Partial Match"};
        JComboBox EMCombo = new JComboBox(EMcommands);
        PLTCombo.setSelectedIndex(0);
        searchOptions.add(PLTCombo);
        searchOptions.add(EMCombo);
        searchOptions.setBorder(new TitledBorder("Search Options"));
        searchOptions.setLayout(new BoxLayout(searchOptions, BoxLayout.Y_AXIS));
        searchOptions.setMaximumSize(searchOptions.getPreferredSize());

        searchControl.add(addBtn);
        searchControl.add(searchBtn);
        searchControl.add(searchOptions);
        searchControl.setLayout(new BoxLayout(searchControl, BoxLayout.PAGE_AXIS));




        //Table
        JTable infoTable = new JTable(model);
        JScrollPane tableScroll = new JScrollPane(infoTable,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);



        //buttonListeners
        addBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //add action
                model.addRow(new Metropolis(metrTxt.getText(), contTxt.getText(), populTxt.getText()));
                return;

            }
        });
        searchBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //search action
                model.searchRows(new Metropolis(metrTxt.getText(), contTxt.getText(), populTxt.getText()),
                        EMCombo.getSelectedIndex() == 1, PLTCombo.getSelectedIndex() == 1);
                return;
            }
        });


        c.add(textFields, BorderLayout.NORTH);
        c.add(searchControl,BorderLayout.EAST);
        c.add(tableScroll,BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
    }
}

