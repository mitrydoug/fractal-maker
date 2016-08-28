import javax.swing.UIManager;

public class FractalMaker{
	public static void main(String[] args){
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e){
		}
		GeneratorFrame genFrame = new GeneratorFrame("Generator");
	}
}