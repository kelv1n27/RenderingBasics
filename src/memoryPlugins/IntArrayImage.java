package memoryPlugins;

import static org.jocl.CL.CL_MEM_COPY_HOST_PTR;
import static org.jocl.CL.CL_MEM_READ_ONLY;
import static org.jocl.CL.CL_TRUE;
import static org.jocl.CL.clCreateBuffer;
import static org.jocl.CL.clEnqueueReadBuffer;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.jocl.Pointer;
import org.jocl.Sizeof;

import tbh.gfxInterface.MemoryPlugin;

public class IntArrayImage extends MemoryPlugin{
	
	private String path;
	private int width, height;
	
	BufferedImage image;
	
	public IntArrayImage(Integer width, Integer height) {
		path = null;
		this.width = width;
		this.height = height;
		forceCollision = true;
	}
	
	public IntArrayImage(String filepath) {
		path = filepath;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}

	@Override
	public void addMemoryObject(int arg0) {
		BufferedImage image;
		int[] err = new int[1];
		if (path == null) {//if canvas
			int canvas[] = new int [width * height];
			Pointer ptr = Pointer.to(canvas);
			gfx.getMemObjects()[arg0] = clCreateBuffer(gfx.getContext(), CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_int * (width * height), ptr, err);
			if (err[0] != org.jocl.CL.CL_SUCCESS) {
				gfx.GfxLog(2, "Failed to allocate memory for new canvas " + org.jocl.CL.stringFor_errorCode(err[0]));
			}
			return;
		}
		try {//if texture
			image = ImageIO.read(getClass().getResourceAsStream(path));
			width = image.getWidth();
			height = image.getHeight();
			int[] texture = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
			Pointer texturePtr = Pointer.to(texture);
		
			gfx.getMemObjects()[arg0] = clCreateBuffer(gfx.getContext(), CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_int * (image.getWidth()*image.getHeight()), texturePtr, err);
			if (err[0] != org.jocl.CL.CL_SUCCESS) {
				gfx.GfxLog(2, "Failed to allocate memory for texture '" + path + "': " + org.jocl.CL.stringFor_errorCode(err[0]));
			}
						
		} catch (IOException e) {
			gfx.GfxLog(2, "Failed to load texture '" + path + "'");
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			gfx.GfxLog(2, "Failed to load texture '" + path + "', possibly misspelled filepath");
			e.printStackTrace();
		}
		
	}

	@Override
	public String getDefaultDesignation() {
		return (path != null ? path : "canvas");
	}

	@Override
	public void removeMemoryObject(int arg0) {
		org.jocl.CL.clReleaseMemObject(gfx.getMemObjects()[arg0]);
	}

	@Override
	public void updateDebug(int arg0) {
		image = (BufferedImage) retrieveMemoryObject(arg0);
		JLabel label = new JLabel();
		label.setIcon(new ImageIcon(image));
		getContentPane().removeAll();
		add(label);
		//setVisible(true);
		pack();
	}

	@Override
	public Object retrieveMemoryObject(int arg0) {
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		DataBufferInt output_buffer = (DataBufferInt) img.getRaster().getDataBuffer();
	    int output_data[] = output_buffer.getData();
	    int err = clEnqueueReadBuffer(gfx.getCommandQueue(), gfx.getMemoryObject(arg0), CL_TRUE, 0, Sizeof.cl_int * (width*height), Pointer.to(output_data), 0, null, null);
	    if (err != org.jocl.CL.CL_SUCCESS) gfx.GfxLog(2, "Readbuffer Failed: " + org.jocl.CL.stringFor_errorCode(err));
		return img;
	}

}
