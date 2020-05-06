import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;
import javax.swing.text.Document;

import java.awt.*;
import java.awt.event.*;


 public class SudokuFrame extends JFrame {



	public SudokuFrame() {
		super("Sudoku Solver");

		//text fields
		setLayout(new BorderLayout(4,4));
		Container cont = getContentPane();
		JTextArea input = new JTextArea(15, 20);
		JTextArea output = new JTextArea(15, 20);
		cont.add(input , BorderLayout.WEST);
		cont.add(output, BorderLayout.EAST);
		input.setBorder(new TitledBorder("Puzzle"));
		output.setBorder(new TitledBorder("Solution"));

		//controls
		JPanel controls = new JPanel();
		JButton checkBtn = new JButton("Check");
		JCheckBox autoChk = new JCheckBox("Auto Check");
		autoChk.setSelected(true);
		controls.add(checkBtn);
		controls.add(autoChk);
		controls.setLayout(new FlowLayout(FlowLayout.LEADING));
		cont.add(controls, BorderLayout.SOUTH);

		//action/documentListeners
		ActionListener solve = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				checkSudoku(input, output);
			}
		};
		checkBtn.addActionListener(solve);

		DocumentListener autoSolve = new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent documentEvent) {
				if (autoChk.isSelected())
					checkSudoku(input,output);
			}
			@Override
			public void removeUpdate(DocumentEvent documentEvent) {
				if (autoChk.isSelected())
					checkSudoku(input,output);
			}
			@Override
			public void changedUpdate(DocumentEvent documentEvent) {
				if (autoChk.isSelected())
					checkSudoku(input,output);
			}
		};
		input.getDocument().addDocumentListener(autoSolve);

		// Could do this:
		//setLocationByPlatform(true);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}


	 private void checkSudoku(JTextArea input,JTextArea output){
		 try{
			 StringBuilder result;
			 Sudoku solver = new Sudoku(input.getText());
			 int solutions = solver.solve();
			 if (solutions > 0){
				 result = new StringBuilder(solver.getSolutionText());
				 result.append("\nsolutions:"+solutions+"\n");
				 result.append("elapsed:"+solver.getElapsed()+"\n");
				 output.setText(result.toString());
			 }
		 }catch(Exception e){
			 output.setText("Parsing problem");
		 }
	 }
	public static void main(String[] args) {
		// GUI Look And Feel
		// Do this incantation at the start of main() to tell Swing
		// to use the GUI LookAndFeel of the native platform. It's ok
		// to ignore the exception.
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) { }
		
		SudokuFrame frame = new SudokuFrame();
	}

}
