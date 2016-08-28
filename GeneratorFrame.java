import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.io.*;


public class GeneratorFrame extends JFrame{

	JMenuBar genMenuBar;
	JMenu genViewMenu;
	JCheckBox squareGridCB, triangularGridCB;
	JButton genDrawOption;
	GeneratorPanel panel;

	public GeneratorFrame(String title){
		super(title);

		panel = new GeneratorPanel();
		add(panel);

		genMenuBar = new JMenuBar();

		genViewMenu = new JMenu("view");
		squareGridCB = new JCheckBox("Square Grid", false);
		squareGridCB.setActionCommand("squareGridCB");
		squareGridCB.addActionListener(panel);
		genViewMenu.add(squareGridCB);
		triangularGridCB = new JCheckBox("Triangular Grid", false);
		triangularGridCB.setActionCommand("triangularGridCB");
		triangularGridCB.addActionListener(panel);
		genViewMenu.add(triangularGridCB);
		genMenuBar.add(genViewMenu);

		genDrawOption = new JButton("draw");
		genDrawOption.setActionCommand("draw");
		genDrawOption.addActionListener(panel);
		genMenuBar.add(genDrawOption);

		setJMenuBar(genMenuBar);

		setSize(new Dimension(500, 500));
		setLocation(new Point(10 , 10));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(true);
	    setVisible(true);
	}
}