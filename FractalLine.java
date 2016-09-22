import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.io.Serializable;

public class FractalLine{

	private FractalPoint p1, p2;
	public boolean recursive, permanent, reference, inverted;
	private boolean focused;

    /* Provide a negative orientation for lines */

	private GeneratorPanel genPanel;
	private DecimalFormat df;

	private class FractalPoint{
		public double x;
		public double y;

		public FractalPoint(double x, double y){
			this.x = x;
			this.y = y;
		}
	}

	public FractalLine(double x1, double y1, double x2, double y2){
		p1 = new FractalPoint(x1, y1);
		p2 = new FractalPoint(x2, y2);
	}

	public double x1(){
		return p1.x;
	}
	public double y1(){
		return p1.y;
	}
	public double x2(){
		return p2.x;
	}
	public double y2(){
		return p2.y;
	}

	public void setX1(double a){
		p1.x = a;
	}
	public void setY1(double a){
		p1.y = a;
	}
	public void setX2(double a){
		p2.x = a;
	}
	public void setY2(double a){
		p2.y = a;
	}

    public void setInverted(boolean in) {
        inverted = in;
    }

	public double[][] getPolygon(){
		//creates a central triangle
        double in = inverted ? 0.0 : 0.06;

		double[] xpoints = {p1.x + 0.54*(p2.x-p1.x),
                           p1.x + 0.46*(p2.x-p1.x) - in * (p2.y-p1.y),
                           p1.x + 0.46*(p2.x-p1.x) + (0.06-in)*(p2.y-p1.y)};

		double[] ypoints = {p1.y + 0.54*(p2.y-p1.y),
                            p1.y + 0.46*(p2.y-p1.y) + in * (p2.x-p1.x),
                            p1.y + 0.46*(p2.y-p1.y) - (0.06-in)*(p2.x-p1.x)};
		double[][] res = new double[2][3];
		res[0] = xpoints;
		res[1] = ypoints;
		return res;
	}

	public double[] getCircle(){
		double res[] = {(p1.x+p2.x)/2.0, (p1.y+p2.y)/2.0, length()*0.05};
		return res;
	}

	public double length(){
	   return Math.sqrt(Math.pow(p2.x-p1.x, 2) + Math.pow(p2.y-p1.y, 2));
	}

	public double angle(){
	   double cX = p2.x-p1.x;
	   double cY = p2.y-p1.y;
	   if(cX > 0){
	       return Math.atan(cY/cX);
	   } else if(cX == 0){
	       if(cY > 0){
	           return Math.PI/2.0;
	       } else if(cY == 0){
	           return 0;
	       } else {
	           return 3.0*Math.PI/2.0;
	       }
	   } else {
	       return Math.atan(cY/cX) + Math.PI;
	   }
	}

	public boolean recursive(){ return recursive; }
	public boolean permanent(){ return permanent; }
	public boolean reference(){ return reference; }
    public boolean inverted(){ return inverted; }

	//static function!
	public static FractalLine makeReferenceLine(FractalLine line){
        FractalLine res = new FractalLine(line.p1.x, line.p1.y, line.p2.x, line.p2.y);
        res.recursive = false;
        res.permanent = false;
        return res;
	}
}
