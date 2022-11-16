package memoryPlugins;

public class BasicFont extends IntArrayImage{
	
	private int charWidth, charHeight;
	private String alphabet;

	public BasicFont(String filepath, Integer charWidth, Integer charHeight, String alphabet) {
		super(filepath);
		this.charWidth = charWidth;
		this.charHeight = charHeight;
		this.alphabet = alphabet;
	}
	
	public int getCharWidth() {
		return charWidth;
	}
	
	public int getCharHeight() {
		return charHeight;
	}
	
	public String getAlphabet() {
		return alphabet;
	}
	
	@Override
	public String getDefaultDesignation() {
		return super.getDefaultDesignation() + "[font]";
	}

}
