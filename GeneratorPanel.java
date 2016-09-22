import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.text.DecimalFormat;
import java.io.*;

public class GeneratorPanel extends JPanel implements ActionListener, MouseListener, MouseMotionListener, MouseWheelListener{
		private ArrayList<FractalLine> lines;
		private FractalLine referenceLine;
		private FractalLine focusedLine;
		private double minX, maxX, minY, maxY, squareGridScale, triangularGridScale;
		private boolean generating, displaySquareGrid, displayTriangularGrid;

		private JPanel x1Panel, y1Panel, swapPanel, x2Panel, y2Panel, btnPanel, coordsPanel, propPanel, fullPanel;
		private JLabel x1Label, y1Label, x2Label, y2Label;
		public JTextField x1Field, y1Field, x2Field, y2Field;
		public JCheckBox ref, per, rec;
		public JButton ok, delete, swap, invert;

		private FractalFrame fracFrame;

		private JFrame optionPane;

		private DecimalFormat df;

		public GeneratorPanel(){

		    fracFrame = new FractalFrame("Fractal Frame");

			addMouseListener(this);
			addMouseMotionListener(this);
			addMouseWheelListener(this);
			lines = new ArrayList<FractalLine>(10);
			referenceLine = null;
			minX = minY = -1;
			maxX = maxY =  1;
			generating = false;
			displaySquareGrid = false;
			displayTriangularGrid = false;

			optionPane = new JFrame("Line Options");
			optionPane.setSize(new Dimension(350, 200));
			optionPane.setResizable(false);

			x1Label = new JLabel("x1:");
			x1Field = new JTextField(10);
			x1Field.setActionCommand("x1Field");
			x1Field.addActionListener(this);
			x1Panel = new JPanel();
			x1Panel.setLayout(new FlowLayout());
			x1Panel.add(x1Label);
			x1Panel.add(x1Field);

			y1Label = new JLabel("y1:");
			y1Field = new JTextField(10);
			y1Field.setActionCommand("y1Field");
			y1Field.addActionListener(this);
			y1Panel = new JPanel();
			y1Panel.setLayout(new FlowLayout());
			y1Panel.add(y1Label);
			y1Panel.add(y1Field);

			swap = new JButton("swap");
			swap.setActionCommand("swap");
			swap.addActionListener(this);
			swapPanel = new JPanel();
			swapPanel.setLayout(new FlowLayout());
			swapPanel.add(swap);

			x2Label = new JLabel("x2:");
			x2Field = new JTextField(10);
			x2Field.setActionCommand("x2Field");
			x2Field.addActionListener(this);
			x2Panel = new JPanel();
			x2Panel.setLayout(new FlowLayout());
			x2Panel.add(x2Label);
			x2Panel.add(x2Field);

			y2Label = new JLabel("y2:");
			y2Field = new JTextField(10);
			y2Field.setActionCommand("y2Field");
			y2Field.addActionListener(this);
			y2Panel = new JPanel();
			y2Panel.setLayout(new FlowLayout());
			y2Panel.add(y2Label);
			y2Panel.add(y2Field);

			coordsPanel = new JPanel();
			coordsPanel.setLayout(new BoxLayout(coordsPanel, BoxLayout.Y_AXIS));
			coordsPanel.add(x1Panel);
			coordsPanel.add(y1Panel);
			coordsPanel.add(swapPanel);
			coordsPanel.add(x2Panel);
			coordsPanel.add(y2Panel);

			ref = new JCheckBox("Reference Line");
			ref.setActionCommand("ref");
			ref.addActionListener(this);
			per = new JCheckBox("Permanent Line");
			per.setActionCommand("per");
			per.addActionListener(this);
			rec = new JCheckBox("Recursive Line");
			rec.setActionCommand("rec");
			rec.addActionListener(this);
			ok  = new JButton("OK");
			ok.setActionCommand("ok");
			ok.addActionListener(this);
			delete = new JButton("delete");
			delete.setActionCommand("delete");
			delete.addActionListener(this);
            invert = new JButton("invert");
            invert.setActionCommand("invert");
            invert.addActionListener(this);
			btnPanel = new JPanel();
			BoxLayout layout = new BoxLayout(btnPanel, BoxLayout.Y_AXIS);
			btnPanel.setLayout(layout);
			btnPanel.add(invert);
			btnPanel.add(Box.createRigidArea(new Dimension(100, 5)));
			btnPanel.add(delete);
			btnPanel.add(Box.createRigidArea(new Dimension(100, 5)));
			btnPanel.add(ok);

			propPanel = new JPanel();
			propPanel.setLayout(new BoxLayout(propPanel, BoxLayout.Y_AXIS));
			propPanel.add(ref);
			propPanel.add(per);
			propPanel.add(rec);
			propPanel.add(btnPanel);

			fullPanel = new JPanel();
			fullPanel.setLayout(new BoxLayout(fullPanel, BoxLayout.X_AXIS));
			fullPanel.add(coordsPanel);
			fullPanel.add(propPanel);

			optionPane.add(fullPanel);
			optionPane.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

			df = new DecimalFormat("0.00");
		}

		public void paintComponent(Graphics g){
			super.paintComponent(g);
			FractalLine current;
			if(displaySquareGrid){
			    squareGridScale = nearestPowerOfTen((maxX-minX)/(getWidth()/150.0));
			    Color lightColor = new Color(200,200,200);
			    Color darkColor = new Color(140, 140, 140);
                for(double w1=Math.floor(minX/squareGridScale/10)*squareGridScale*10;
                        w1 <= Math.ceil(maxX/squareGridScale/10)*squareGridScale*10;
                        w1 += squareGridScale*10){
                            for(double h1=Math.floor(minY/squareGridScale/10)*squareGridScale*10;
                                    h1 <= Math.ceil(maxY/squareGridScale/10)*squareGridScale*10;
                                    h1 += squareGridScale*10){
                                    g.setColor(lightColor);
                                    for(double w2= w1;
                                            w2 <= w1 + squareGridScale*10;
                                            w2 += squareGridScale){
                                                for(double h2=h1;
                                                        h2 <= h1 + squareGridScale*10;
                                                        h2 += squareGridScale){
                                                     g.fillOval(getXDispRef(w2)-2, getYDispRef(h2)-2, 4, 4);
                                                }
                                    }
                                    g.setColor(darkColor);
                                    g.fillOval(getXDispRef(w1)-3, getYDispRef(h1)-3, 6, 6);
                            }
                }
			}
			if(displayTriangularGrid){
                triangularGridScale = nearestPowerOfTen((maxX-minX)/(getWidth()/150.0));
                Color lightColor = new Color(200,200,200);
                Color darkColor = new Color(140, 140, 140);
                double sr3o2 = Math.sqrt(3)/2;
                for(double h1=Math.floor(minY/triangularGridScale/sr3o2/10)*triangularGridScale*10*sr3o2;
                           h1 <= Math.ceil(maxY/triangularGridScale/sr3o2/10)*triangularGridScale*10*sr3o2;
                           h1 += triangularGridScale*sr3o2*10){
                    double shift1;
                    if(Math.abs(Math.abs((h1/triangularGridScale/10/sr3o2)%2) - 1) < 0.1){
                        shift1 = triangularGridScale*5;
                    } else {
                        shift1 = 0;
                    }
                    for(double w1 = Math.floor(minX/triangularGridScale/10)*triangularGridScale*10 - shift1;
                               w1 <= Math.ceil(maxX/triangularGridScale/10)*triangularGridScale*10 - shift1;
                               w1 += triangularGridScale*10){
                        g.setColor(lightColor);
                        for(double h2=h1;
                                   h2 <= h1 + triangularGridScale*sr3o2*10;
                                   h2 += triangularGridScale*sr3o2){
                            double shift2;
                            if(Math.abs(Math.abs((h2/triangularGridScale/sr3o2)%2) - 1) < 0.1){
                                shift2 = triangularGridScale*0.5;
                            } else {
                                shift2 = 0;
                            }
                            for(double w2= w1-shift2;
                                w2 <= w1 + triangularGridScale*10 - shift2;
                                w2 += triangularGridScale){
                                     g.fillOval(getXDispRef(w2)-2, getYDispRef(h2)-2, 4, 4);
                            }
                        }
                        g.setColor(darkColor);
                        g.fillOval(getXDispRef(w1)-3, getYDispRef(h1)-3, 6, 6);
                    }
                }
			}
			if(focusedLine != null){
				g.setColor(Color.lightGray);
				g.drawOval(getXDispRef(focusedLine.x1())-5, getYDispRef(focusedLine.y1())-5, 10, 10);
				g.drawOval(getXDispRef(focusedLine.x2())-5, getYDispRef(focusedLine.y2())-5, 10, 10);
			}
			for(int i=0; i<lines.size(); i++){
				current = lines.get(i);
				int x1 = getXDispRef(current.x1());
				int y1 = getYDispRef(current.y1());
				int x2 = getXDispRef(current.x2());
				int y2 = getYDispRef(current.y2());
				if(current.reference()){
					g.setColor(Color.blue);
				} else if(current.permanent()){
					g.setColor(Color.black);
				} else {
					g.setColor(Color.red);
				}
				g.drawLine(x1, y1, x2, y2);
				if(current.reference() || current.recursive()){
					int[] xpoints = new int[3];
					int[] ypoints = new int[3];
					double[][] data = current.getPolygon();
					for(int j=0; j<3; j++){
						xpoints[j] = getXDispRef(data[0][j]);
						ypoints[j] = getYDispRef(data[1][j]);
					}
					g.fillPolygon(xpoints, ypoints, 3);
				}else{
					double[] circ = current.getCircle();
					g.fillOval(getXDispRef(circ[0]-circ[2]), getYDispRef(circ[1]+circ[2]),getXDispRef(circ[0]+circ[2])-getXDispRef(circ[0]-circ[2]), getYDispRef(circ[1]-circ[2])-getYDispRef(circ[1]+circ[2]));
				}
			}
			if(generating){
				g.setColor(Color.black);
				g.drawLine(getXDispRef(x1), getYDispRef(y1), getXDispRef(lastMovedX), getYDispRef(lastMovedY));
			}
		}

		public int getXDispRef(double a){
			return (int)((a-minX)/(maxX-minX)*getWidth());
		}

		public int getYDispRef(double a){
			return (int)(getHeight()*(1 - (a-minY)/(maxY-minY)));
		}

		public double getXFieldRef(int a){
			return minX + (a/(double)getWidth())*(maxX-minX);
		}

		public double getYFieldRef(int a){
			return minY + (1-a/(double)getHeight())*(maxY-minY);
		}

		public FractalLine getReferenceLine(){
			return referenceLine;
		}

		public void setReferenceLine(FractalLine line){
			referenceLine = line;
		}

		public void setFocusedLine(FractalLine line){
			focusedLine = line;
			if(line == null){
				optionPane.setVisible(false);
			} else {
				if(line.reference()){
					ref.setSelected(true);
					ref.setEnabled(true);
					per.setEnabled(false);
					rec.setEnabled(false);
				} else {
					ref.setSelected(false);
					per.setEnabled(true);
					rec.setEnabled(true);
				}
				if(line.recursive()){
					rec.setSelected(true);
				} else {
					rec.setSelected(false);
				}
				if(line.permanent()){
					per.setSelected(true);
				} else {
					per.setSelected(false);
				}
				if(!(referenceLine == line || referenceLine == null)){
					ref.setEnabled(false);
				}

				x1Field.setText(df.format(line.x1()));
                y1Field.setText(df.format(line.y1()));
                x2Field.setText(df.format(line.x2()));
                y2Field.setText(df.format(line.y2()));


				optionPane.setVisible(true);
			}
			repaint();
		}

		public void actionPerformed(ActionEvent e){
			String command = e.getActionCommand();
			if(command.equals("x1Field")){
				try{
					double val = Double.parseDouble(x1Field.getText());
					focusedLine.setX1(val);
					repaint();
				}catch(Exception ex){
					x1Field.setText(df.format(focusedLine.x1()));
				}
			} else if(command.equals("y1Field")){
				try{
					double val = Double.parseDouble(y1Field.getText());
					focusedLine.setY1(val);
					repaint();
				}catch(Exception ex){
					y1Field.setText(df.format(focusedLine.y1()));
				}
			} else if(command.equals("x2Field")){
				try{
					double val = Double.parseDouble(x2Field.getText());
					focusedLine.setX2(val);
					repaint();
				}catch(Exception ex){
					x2Field.setText(df.format(focusedLine.x2()));
				}
			} else if(command.equals("y2Field")){
				try{
					double val = Double.parseDouble(y2Field.getText());
					focusedLine.setY2(val);
					repaint();
				}catch(Exception ex){
					y2Field.setText(df.format(focusedLine.y2()));
				}
			} else if(command.equals("ref")){
				if(ref.isSelected()){
					per.setEnabled(false);
					rec.setEnabled(false);
					ok.setEnabled(true);
					setReferenceLine(focusedLine);
					focusedLine.reference = true;
					focusedLine.permanent = false;
					focusedLine.recursive = false;
				} else {
					per.setEnabled(true);
					setReferenceLine(null);
					rec.setEnabled(true);
					focusedLine.reference = false;
					if(!per.isSelected() && !per.isSelected()){
						ok.setEnabled(false);
					}
					focusedLine.permanent = per.isSelected();
					focusedLine.recursive = rec.isSelected();
				}
				repaint();
			} else if(command.equals("per")){
				if(per.isSelected()){
					focusedLine.permanent = true;
					ok.setEnabled(true);
					repaint();
				} else{
					focusedLine.permanent = false;
				}
			} else if(command.equals("rec")){
				if(rec.isSelected()){
					focusedLine.recursive = true;
					ok.setEnabled(true);
					repaint();
				} else{
					focusedLine.recursive = false;
					repaint();
				}
            } else if(command.equals("delete")){
                lines.remove(focusedLine);
                if(referenceLine == focusedLine){
                    setReferenceLine(null);
                }
                setFocusedLine(null);
            } else if(command.equals("ok")){
				setFocusedLine(null);
			} else if(command.equals("swap")){
				double x1 = focusedLine.x2();
				double y1 = focusedLine.y2();
				double x2 = focusedLine.x1();
				double y2 = focusedLine.y1();
				focusedLine.setX1(x1);
				focusedLine.setY1(y1);
				focusedLine.setX2(x2);
				focusedLine.setY2(y2);
				x1Field.setText(df.format(x1));
				y1Field.setText(df.format(y1));
				x2Field.setText(df.format(x2));
				y2Field.setText(df.format(y2));
				repaint();
			} else if(command.equals("squareGridCB")){
				displaySquareGrid = !displaySquareGrid;
				repaint();
			}else if(command.equals("triangularGridCB")){
				displayTriangularGrid = !displayTriangularGrid;
				repaint();
			}else if(command.equals("draw")){
				if(referenceLine != null){
					FractalLine[] data = new FractalLine[lines.size()-1];
					int a = 0;
				    for(int i=0; i<lines.size(); i++){
				    	FractalLine current = lines.get(i);
				    	if(!(current == referenceLine)){
				    		data[i-a] = current;
				    	} else {
				    		a = 1;
				    	}
				    }
				    fracFrame.setFieldBounds(minX, maxX, minY, maxY);
				    fracFrame.drawFractal(data, referenceLine);
				}
			} else if (command.equals("invert")) {
               focusedLine.setInverted(!focusedLine.inverted());
               repaint(); 
            }
		}

		double x1, y1, x2, y2;


		public void mouseClicked(MouseEvent m){
			if(m.getButton() == 1){
				if(generating){
					generating = false;
                    x2 = lastMovedX;
                    y2 = lastMovedY;
					FractalLine newLine = new FractalLine(x1, y1, x2, y2);
					if(referenceLine == null){
						newLine.reference = true;
						newLine.recursive = false;
						newLine.permanent = false;
						setReferenceLine(newLine);
					} else {
						newLine.reference = false;
						newLine.recursive = true;
						newLine.permanent = false;
					}

					lines.add(newLine);
					setFocusedLine(newLine);
					repaint();
				} else {
					boolean lineFocused = false;
					for(int i=0; i<lines.size(); i++){
						FractalLine current = lines.get(i);
						int[] xpoints = new int[3];
						int[] ypoints = new int[3];
						double[][] data = current.getPolygon();
						for(int j=0; j<3; j++){
							xpoints[j] = getXDispRef(data[0][j]);
							ypoints[j] = getYDispRef(data[1][j]);
						}
						if((new Polygon(xpoints, ypoints, 3)).contains(m.getX(), m.getY())){
							setFocusedLine(current);
							lineFocused = true;
						}
					}
					if(!lineFocused){
						generating = true;
						if(displaySquareGrid){
                            double nearestXGridPoint = Math.round(getXFieldRef(m.getX())/squareGridScale)*squareGridScale;
                            double nearestYGridPoint = Math.round(getYFieldRef(m.getY())/squareGridScale)*squareGridScale;
                            if(Math.sqrt(Math.pow(m.getX()-getXDispRef(nearestXGridPoint), 2)+Math.pow(m.getY()-getYDispRef(nearestYGridPoint), 2)) < 4){
                                x1 = nearestXGridPoint;
                                y1 = nearestYGridPoint;
                            } else {
                                x1 = getXFieldRef(m.getX());
                                y1 = getYFieldRef(m.getY());
                            }
						} else if(displayTriangularGrid){
						    double sr3o2 = Math.sqrt(3)/2.0;
                            double nearestYGridPoint = Math.round(getYFieldRef(m.getY())/triangularGridScale/sr3o2)*triangularGridScale*sr3o2;
                            double nearestXGridPoint;
                            if(Math.abs(Math.abs((nearestYGridPoint/triangularGridScale/sr3o2)%2) - 1) < 0.1){
                                nearestXGridPoint = Math.round(getXFieldRef(m.getX())/triangularGridScale)*triangularGridScale;
                                if(getXFieldRef(m.getX()) < nearestXGridPoint){
                                    nearestXGridPoint -= 0.5*triangularGridScale;
                                } else {
                                    nearestXGridPoint += 0.5*triangularGridScale;
                                }
                            } else {
                                nearestXGridPoint = Math.round(getXFieldRef(m.getX())/triangularGridScale)*triangularGridScale;
                            }
                            if(Math.sqrt(Math.pow(m.getX()-getXDispRef(nearestXGridPoint), 2)+Math.pow(m.getY()-getYDispRef(nearestYGridPoint), 2)) < 8){
                                x1 = nearestXGridPoint;
                                y1 = nearestYGridPoint;
                            } else {
                                x1 = getXFieldRef(m.getX());
                                y1 = getYFieldRef(m.getY());
                            }
						} else{
                            x1 = getXFieldRef(m.getX());
                            y1 = getYFieldRef(m.getY());
						}
					}
				}
			} else if(m.getButton() == 3){
				generating = false;
				repaint();
			}
		}
		double lastMovedX;
		double lastMovedY;
		public void mouseMoved(MouseEvent m){
			if(generating){
			    if(displaySquareGrid){
			        double nearestXGridPoint = Math.round(getXFieldRef(m.getX())/squareGridScale)*squareGridScale;
			        double nearestYGridPoint = Math.round(getYFieldRef(m.getY())/squareGridScale)*squareGridScale;
                    if(Math.sqrt(Math.pow(m.getX()-getXDispRef(nearestXGridPoint), 2)+Math.pow(m.getY()-getYDispRef(nearestYGridPoint), 2)) < 4){
                        lastMovedX = nearestXGridPoint;
                        lastMovedY = nearestYGridPoint;
                    } else {
                        lastMovedX = getXFieldRef(m.getX());
                        lastMovedY = getYFieldRef(m.getY());
                    }
			    } else if(displayTriangularGrid){
                    double sr3o2 = Math.sqrt(3)/2.0;
                    double nearestYGridPoint = Math.round(getYFieldRef(m.getY())/triangularGridScale/sr3o2)*triangularGridScale*sr3o2;
                    double nearestXGridPoint;
                    if(Math.abs(Math.abs((nearestYGridPoint/triangularGridScale/sr3o2)%2) - 1) < 0.1){
                        nearestXGridPoint = Math.round(getXFieldRef(m.getX())/triangularGridScale)*triangularGridScale;
                        if(getXFieldRef(m.getX()) < nearestXGridPoint){
                            nearestXGridPoint -= 0.5*triangularGridScale;
                        } else {
                            nearestXGridPoint += 0.5*triangularGridScale;
                        }
                    } else {
                        nearestXGridPoint = Math.round(getXFieldRef(m.getX())/triangularGridScale)*triangularGridScale;
                    }
                    if(Math.sqrt(Math.pow(m.getX()-getXDispRef(nearestXGridPoint), 2)+Math.pow(m.getY()-getYDispRef(nearestYGridPoint), 2)) < 6){
                        lastMovedX = nearestXGridPoint;
                        lastMovedY = nearestYGridPoint;
                    } else {
                        lastMovedX = getXFieldRef(m.getX());
                        lastMovedY = getYFieldRef(m.getY());
                    }
			    } else {
                    lastMovedX = getXFieldRef(m.getX());
                    lastMovedY = getYFieldRef(m.getY());
			    }
				repaint();
			}
		}

		private final int P1GRABBED = 0,
						  P2GRABBED = 1,
						  NONE      = 2;

		private int focusedLineState = NONE;
		private boolean pointGrab = false;
		private boolean fieldGrab = false;
		private boolean lineGrab  = false;
		private int grabDispX = 0;
		private int grabDispY = 0;
		private double grabMinX = 0;
		private double grabMinY = 0;
		private double grabMaxX = 0;
		private double grabMaxY = 0;
        private double grabX1 = 0;
        private double grabY1 = 0;
        private double grabX2 = 0;
        private double grabY2 = 0;

		public void mousePressed(MouseEvent m){
            if(m.getButton() == 1){
                if(focusedLine != null &&
                   Math.sqrt(Math.pow(m.getX() - getXDispRef(focusedLine.x1()), 2)+Math.pow(m.getY() - getYDispRef(focusedLine.y1()), 2)) < 5){
                    focusedLineState = P1GRABBED;
                    pointGrab = true;
                } else if(focusedLine != null &&
                   Math.sqrt(Math.pow(m.getX() - getXDispRef(focusedLine.x2()), 2)+Math.pow(m.getY() - getYDispRef(focusedLine.y2()), 2)) < 5){
                    focusedLineState = P2GRABBED;
                    pointGrab = true;
                } else {
                    grabDispX = m.getX();
                    grabDispY = m.getY();
                    grabMinX = minX;
                    grabMinY = minY;
                    grabMaxX = maxX;
                    grabMaxY = maxY;
                    fieldGrab = true;
                }
            } else if(m.getButton() == 2 && focusedLine != null){
                grabDispX = m.getX();
                grabDispY = m.getY();
                grabX1 = focusedLine.x1();
                grabY1 = focusedLine.y1();
                grabX2 = focusedLine.x2();
                grabY2 = focusedLine.y2();
                lineGrab = true;
            }
		}

		public void mouseDragged(MouseEvent m){
			if(pointGrab){
				if(focusedLineState == P1GRABBED){
				    if(displaySquareGrid){
                        double nearestXGridPoint = Math.round(getXFieldRef(m.getX())/squareGridScale)*squareGridScale;
                        double nearestYGridPoint = Math.round(getYFieldRef(m.getY())/squareGridScale)*squareGridScale;
                        if(Math.sqrt(Math.pow(m.getX()-getXDispRef(nearestXGridPoint), 2)+Math.pow(m.getY()-getYDispRef(nearestYGridPoint), 2)) < 4){
                            focusedLine.setX1(nearestXGridPoint);
                            focusedLine.setY1(nearestYGridPoint);
                        } else {
                            focusedLine.setX1(getXFieldRef(m.getX()));
                            focusedLine.setY1(getYFieldRef(m.getY()));
                        }
				    }else if(displayTriangularGrid){
                        double sr3o2 = Math.sqrt(3)/2.0;
                        double nearestYGridPoint = Math.round(getYFieldRef(m.getY())/triangularGridScale/sr3o2)*triangularGridScale*sr3o2;
                        double nearestXGridPoint;
                        if(Math.abs(Math.abs((nearestYGridPoint/triangularGridScale/sr3o2)%2) - 1) < 0.1){
                            nearestXGridPoint = Math.round(getXFieldRef(m.getX())/triangularGridScale)*triangularGridScale;
                            if(getXFieldRef(m.getX()) < nearestXGridPoint){
                                nearestXGridPoint -= 0.5*triangularGridScale;
                            } else {
                                nearestXGridPoint += 0.5*triangularGridScale;
                            }
                        } else {
                            nearestXGridPoint = Math.round(getXFieldRef(m.getX())/triangularGridScale)*triangularGridScale;
                        }
                        if(Math.sqrt(Math.pow(m.getX()-getXDispRef(nearestXGridPoint), 2)+Math.pow(m.getY()-getYDispRef(nearestYGridPoint), 2)) < 6){
                            focusedLine.setX1(nearestXGridPoint);
                            focusedLine.setY1(nearestYGridPoint);
                        } else {
                            focusedLine.setX1(getXFieldRef(m.getX()));
                            focusedLine.setY1(getYFieldRef(m.getY()));
                        }
				    }else{
                        focusedLine.setX1(getXFieldRef(m.getX()));
                        focusedLine.setY1(getYFieldRef(m.getY()));
				    }
                    x1Field.setText(df.format(focusedLine.x1()));
                    y1Field.setText(df.format(focusedLine.y1()));
					repaint();
				} else if(focusedLineState == P2GRABBED){
                    if(displaySquareGrid){
                        double nearestXGridPoint = Math.round(getXFieldRef(m.getX())/squareGridScale)*squareGridScale;
                        double nearestYGridPoint = Math.round(getYFieldRef(m.getY())/squareGridScale)*squareGridScale;
                        if(Math.sqrt(Math.pow(m.getX()-getXDispRef(nearestXGridPoint), 2)+Math.pow(m.getY()-getYDispRef(nearestYGridPoint), 2)) < 4){
                            focusedLine.setX2(nearestXGridPoint);
                            focusedLine.setY2(nearestYGridPoint);
                        } else {
                            focusedLine.setX2(getXFieldRef(m.getX()));
                            focusedLine.setY2(getYFieldRef(m.getY()));
                        }
                    }else if(displayTriangularGrid){
                        double sr3o2 = Math.sqrt(3)/2.0;
                        double nearestYGridPoint = Math.round(getYFieldRef(m.getY())/triangularGridScale/sr3o2)*triangularGridScale*sr3o2;
                        double nearestXGridPoint;
                        if(Math.abs(Math.abs((nearestYGridPoint/triangularGridScale/sr3o2)%2) - 1) < 0.1){
                            nearestXGridPoint = Math.round(getXFieldRef(m.getX())/triangularGridScale)*triangularGridScale;
                            if(getXFieldRef(m.getX()) < nearestXGridPoint){
                                nearestXGridPoint -= 0.5*triangularGridScale;
                            } else {
                                nearestXGridPoint += 0.5*triangularGridScale;
                            }
                        } else {
                            nearestXGridPoint = Math.round(getXFieldRef(m.getX())/triangularGridScale)*triangularGridScale;
                        }
                        if(Math.sqrt(Math.pow(m.getX()-getXDispRef(nearestXGridPoint), 2)+Math.pow(m.getY()-getYDispRef(nearestYGridPoint), 2)) < 6){
                            focusedLine.setX2(nearestXGridPoint);
                            focusedLine.setY2(nearestYGridPoint);
                        } else {
                            focusedLine.setX2(getXFieldRef(m.getX()));
                            focusedLine.setY2(getYFieldRef(m.getY()));
                        }
                    }else{
                        focusedLine.setX2(getXFieldRef(m.getX()));
                        focusedLine.setY2(getYFieldRef(m.getY()));
                    }
                    x2Field.setText(df.format(focusedLine.x2()));
                    y2Field.setText(df.format(focusedLine.y2()));
                    repaint();
				}
			}
			if(fieldGrab){
				minX = grabMinX - getXFieldRef(m.getX()) + getXFieldRef(grabDispX);
				minY = grabMinY - getYFieldRef(m.getY()) + getYFieldRef(grabDispY);
				maxX = grabMaxX - getXFieldRef(m.getX()) + getXFieldRef(grabDispX);
				maxY = grabMaxY - getYFieldRef(m.getY()) + getYFieldRef(grabDispY);
				repaint();
			}
			if(lineGrab && focusedLine != null){
			    focusedLine.setX1(grabX1 + getXFieldRef(m.getX()) - getXFieldRef(grabDispX));
                focusedLine.setY1(grabY1 + getYFieldRef(m.getY()) - getYFieldRef(grabDispY));
                focusedLine.setX2(grabX2 + getXFieldRef(m.getX()) - getXFieldRef(grabDispX));
                focusedLine.setY2(grabY2 + getYFieldRef(m.getY()) - getYFieldRef(grabDispY));
                repaint();
			}
		}

		public void mouseReleased(MouseEvent m){
			focusedLineState = NONE;
			fieldGrab = false;
			pointGrab = false;
			lineGrab = false;
		}

		public void mouseExited(MouseEvent m){}
		public void mouseEntered(MouseEvent m){}

		public void mouseWheelMoved(MouseWheelEvent m){
			if(m.getWheelRotation()>0){
				minX -= (getXFieldRef(m.getX()) - minX)*0.05;
				maxX += (maxX - getXFieldRef(m.getX()))*0.05;
				minY -= (getYFieldRef(m.getY()) - minY)*0.05;
				maxY += (maxY - getYFieldRef(m.getY()))*0.05;
			} else {
				minX += (getXFieldRef(m.getX()) - minX)*0.05;
				maxX -= (maxX - getXFieldRef(m.getX()))*0.05;
				minY += (getYFieldRef(m.getY()) - minY)*0.05;
				maxY -= (maxY - getYFieldRef(m.getY()))*0.05;
			}
			repaint();
		}

		public double nearestPowerOfTen(double n){
		    if(n <= 0){
		        throw new IllegalArgumentException();
		    } else {
                int x10 = 0;
                while(n>=10 || n<0.1){
                    if(n>=10){
                        n /= 10;
                        x10 --;
                    } else if(n <0.1){
                        n *= 10;
                        x10 ++;
                    }
                }
                if(n>= 0.5) n=1;
                if(n<0.5) n = 0.1;
                return n/Math.pow(10, x10);
		    }
		}
}
