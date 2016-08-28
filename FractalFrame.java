import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.*;

public class FractalFrame extends JFrame implements ActionListener{

    private FractalLine[] fractal;
    private FractalPanel panel;
    private JTextField iterationsOption;

    private int iterations;

	/*
	 *  This class is a data structure used to enclose normal lines with
	 *  their respective reference line.
	 *
	 **/
    private class FractalPacket{
		public FractalLine[] lines;
		public FractalLine   ref;
		public FractalPacket(FractalLine[] l, FractalLine r){
			lines = l;
			ref = r;
		}
	}

	private class FractalLineInfo{
	   public double lineAngle;
	   public double linePerLength; // percentage = len of line over length of ref
	   public double transAngle;
	   public double transPerLength;
	   public FractalLineInfo(double la, double ll, double ta, double tl){
	       lineAngle = la;
	       linePerLength = ll;
	       transAngle = ta;
	       transPerLength = tl;
	   }
	}

	public FractalFrame(String title){
		super(title);
		JMenuBar fracMenuBar;
		JMenu fracFileMenu;
		JMenuItem fracSaveOption, fracNewOption;
		JLabel iter;

        iterations = 5;

		fracMenuBar = new JMenuBar();
		fracFileMenu = new JMenu("File    ");
		fracSaveOption = new JMenuItem("save image");
		fracSaveOption.setActionCommand("save");
		fracSaveOption.addActionListener(this);
		fracFileMenu.add(fracSaveOption);
		fracMenuBar.add(fracFileMenu);

		iter = new JLabel("iterations");
		iterationsOption = new JTextField(10);
		iterationsOption.setActionCommand("iter");
		iterationsOption.addActionListener(this);
		iterationsOption.setText(String.valueOf(iterations));
		fracMenuBar.add(iter);
		fracMenuBar.add(iterationsOption);

		setJMenuBar(fracMenuBar);

		panel = new FractalPanel();
		add(panel);

		fractal = new FractalLine[0];

		setSize(new Dimension(700, 700));
		setLocation(new Point(550, 10));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(true);
		setVisible(true);
	}

	private class FractalPanel extends JPanel{

        private double minX, maxX, minY, maxY;

        public FractalLine[] data;
        public FractalLine ref;

		public FractalPanel(){
			setBackground(Color.white);
			minX = -1;
			maxX = 1;
			minY = -1;
			maxY = 1;
		}

		public void paintComponent(Graphics g){
			super.paintComponent(g);
			for(int i=0; i<fractal.length; i++){
			 FractalLine line = fractal[i];
			    g.drawLine(getXDispRef(line.x1(), getWidth()), getYDispRef(line.y1(), getHeight()),
			               getXDispRef(line.x2(), getWidth()), getYDispRef(line.y2(), getHeight()));
			}
		}

        public int getXDispRef(double a, int width){
            return (int)((a-minX)/(maxX-minX)*width);
        }

        public int getYDispRef(double a, int height){
            return (int)(height*(1 - (a-minY)/(maxY-minY)));
        }

		public void setFieldBounds(double iX, double aX, double iY, double aY){
		  minX = iX; maxX = aX; minY = iY; maxY = aY;
		}

        private int getNumRecursive(FractalLine[] base){
            int num = 0;
            for(FractalLine line : base){
                if(line.recursive()){
                    num ++;
                }
            }
            return num;
        }

        public void drawFractal(){
            FractalLineInfo[] infos = new FractalLineInfo[data.length];
            double refAngle = ref.angle();
            double refLength = ref.length();
            for(int i=0; i<data.length; i++){
                FractalLine current = data[i];
                double lineAngle = current.angle();
                double lineLength = current.length();
                double lineRelAng = lineAngle - refAngle;
                double linePerLen = lineLength / refLength;

                FractalLine trans = new FractalLine(ref.x1(), ref.y1(), current.x1(), current.y1());

                double transAngle = trans.angle();
                double transLength = trans.length();
                double transRelAng = transAngle - refAngle;
                double transPerLength = transLength / refLength;

                FractalLineInfo lineInfo = new FractalLineInfo(lineRelAng, linePerLen, transRelAng, transPerLength);
                infos[i] =  lineInfo;
            }
            FractalPacket base = new FractalPacket(data, ref);
            FractalPacket[] it1 = new FractalPacket[1];
            it1[0] = base;
            FractalPacket[] it2;
            ArrayList<FractalLine> permanents = new ArrayList<FractalLine>();
            for(int i=0; i<iterations; i++){ // each iteration...
                it2 = new FractalPacket[it1.length*getNumRecursive(data)];
                int it2index = 0;
                for(int j=0; j<it1.length; j++){ // each packet
                    FractalPacket cPacket = it1[j];
                    for(int k=0; k<cPacket.lines.length; k++){ //each line in the packet
                        FractalLine cLine = cPacket.lines[k];
                        if(cLine.permanent()){
                            permanents.add(cLine);
                        }
                        if(cLine.recursive()){
                            FractalLine[] newLines = new FractalLine[base.lines.length];
                            FractalLine newRef = FractalLine.makeReferenceLine(cLine);
                            FractalPacket newPacket = new FractalPacket(newLines, cLine);
                            for(int m=0; m<cPacket.lines.length; m++){ // for, again, all lines in the packet
                                FractalLine lineMirror;
                                double transX1 = newPacket.ref.x1();
                                double transY1 = newPacket.ref.y1();
                                double transAngle = newPacket.ref.angle() + infos[m].transAngle;
                                double transLength = newPacket.ref.length() * infos[m].transPerLength;
                                double lineX1 = transX1 + transLength*Math.cos(transAngle);
                                double lineY1 = transY1 + transLength*Math.sin(transAngle);
                                double lineAngle = newPacket.ref.angle() + infos[m].lineAngle;
                                double lineLength = newPacket.ref.length() * infos[m].linePerLength;
                                double lineX2 = lineX1 + lineLength*Math.cos(lineAngle);
                                double lineY2 = lineY1 + lineLength*Math.sin(lineAngle);
                                lineMirror = new FractalLine(lineX1, lineY1, lineX2, lineY2);
                                lineMirror.recursive = cPacket.lines[m].recursive;
                                lineMirror.permanent = cPacket.lines[m].permanent;
                                newPacket.lines[m] = lineMirror;
                            }
                            it2[it2index] = newPacket;
                            it2index++;
                        }
                    }
                }
                it1 = it2;
            }
            fractal = new FractalLine[it1.length * data.length + permanents.size()];
            for(int i=0; i<permanents.size(); i++){
                fractal[i] = permanents.get(i);
            }
            for(int i=0; i<it1.length; i++){
                for(int j=0; j<it1[i].lines.length; j++){
                    fractal[permanents.size() + data.length*i + j] = it1[i].lines[j];
                }
            }
            repaint();
        }
	}

    public void drawFractal(FractalLine[] data, FractalLine ref){
        panel.data = data;
        panel.ref = ref;
        panel.drawFractal();
    }

    public void actionPerformed(ActionEvent e){
        if(e.getActionCommand().equals("iter")){

            try{
                iterations = Integer.parseInt(iterationsOption.getText());
                panel.drawFractal();
            }catch(Exception x){
                iterationsOption.setText(String.valueOf(iterations));
            }
        } else if(e.getActionCommand().equals("save")){
            int width, height;
			width = Integer.parseInt(JOptionPane.showInputDialog("Image Width"));
            height = Integer.parseInt(JOptionPane.showInputDialog("Image Height"));
            JFileChooser saver = new JFileChooser();
            int r = saver.showSaveDialog(this);
			if(r == JFileChooser.APPROVE_OPTION){
				File file = saver.getSelectedFile();
				String fileName = file.getName();
				String extension = fileName.substring(fileName.lastIndexOf(".")+1, fileName.length());
				BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
	            Graphics g = image.getGraphics();
	            g.setColor(Color.white);
	            g.fillRect(0,0,width,height);
	            g.setColor(Color.black);
	            for(int i=0; i<fractal.length; i++){
	               FractalLine line = fractal[i];
	               g.drawLine(panel.getXDispRef(line.x1(), width), panel.getYDispRef(line.y1(), height),
	                          panel.getXDispRef(line.x2(), width), panel.getYDispRef(line.y2(), height));
	            }
	            try{
	                ImageIO.write(image, "jpg", file);
	            } catch (IOException x){
	                JOptionPane.showMessageDialog(this,"Could Not Save");
	            }
			}
        }
    }

    public void setFieldBounds(double iX, double aX, double iY, double aY){
        panel.setFieldBounds(iX,aX,iY,aY);
    }
}