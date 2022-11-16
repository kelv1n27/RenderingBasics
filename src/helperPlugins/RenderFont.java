package helperPlugins;

import memoryPlugins.BasicFont;
import tbh.gfxInterface.RunnablePlugin;

public class RenderFont extends RunnablePlugin{

	@Override
	public void run(Object[] arg0) {
		int canvas, fontIndex, x, y;
		float scale;
		String message;
		BasicFont font;
		try {
			canvas = (int) arg0[0];
			fontIndex = (int) arg0[1];
			x = (int) arg0[2];
			y = (int) arg0[3];
			scale = (float) arg0[4];
			message = (String) arg0[5];
			font = (BasicFont) gfx.getMemoryPlugin(fontIndex);
		} catch (Exception e) {
			gfx.GfxLog(2, "Illegal arguments for RenderFont plugin, args are:\n"
					+ "int canvas\n"
					+ "int fontIndex\n"
					+ "int x\n"
					+ "int y\n"
					+ "float scale\n"
					+ "String message");
			e.printStackTrace();
			return;
		}
		message = message.toLowerCase();
		for(int i = 0; i < message.length(); i++) {
			int index = 0;
			while (index < font.getAlphabet().length() && message.charAt(i) != font.getAlphabet().charAt(index)) {
				index++;
			}
			gfx.runPlugin("Render",
					new Object[] {canvas, 
					fontIndex,
					x + (int)(i * font.getCharWidth() * scale), 
					y, 
					(index * font.getCharWidth())%font.getWidth(), 
					((index * font.getCharWidth())/font.getWidth())*font.getCharHeight(), 
					font.getCharWidth(), 
					font.getCharHeight(), 
					scale,
					scale,
					false, 
					false, 
					1f});
		}
	}

}
