package kernelPlugins;

import static org.jocl.CL.clEnqueueNDRangeKernel;
import static org.jocl.CL.clSetKernelArg;

import org.jocl.Pointer;
import org.jocl.Sizeof;

import memoryPlugins.IntArrayImage;
import tbh.gfxInterface.KernelPlugin;

public class Render extends KernelPlugin{

	@Override
	public void run(Object[] arg0) {
		int canvas, textureIndex, x, y, xSpriteOffset, ySpriteOffset, spriteWidth, spriteHeight;
		float xScale, yScale, alphaShift;
		boolean flipX, flipY;
		IntArrayImage source, dest;
		
		try {
			canvas = (int) arg0[0];
			textureIndex = (int) arg0[1];
			x = (int) arg0[2];
			y = (int) arg0[3];
			xSpriteOffset = (int) arg0[4];
			ySpriteOffset = (int) arg0[5];
			spriteWidth = (int) arg0[6];
			spriteHeight = (int) arg0[7];
			xScale = (float) arg0[8];
			yScale = (float) arg0[9];
			flipX = (boolean) arg0[10];
			flipY = (boolean) arg0[11];
			alphaShift = (float) arg0[12];
			source = (IntArrayImage) gfx.getMemoryPlugin(textureIndex);
			dest = (IntArrayImage) gfx.getMemoryPlugin(canvas);
		} catch (Exception e) {
			gfx.GfxLog(2, "Illegal Arguments for Render plugin, args are:\n"
					+ "int canvas\n"
					+ "int textureIndex\n"
					+ "int x\n"
					+ "int y\n"
					+ "int xSpriteOffset\n"
					+ "int ySpriteOffset\n"
					+ "float xScale\n"
					+ "float yScale\n"
					+ "boolean flipX\n"
					+ "boolean flipY\n"
					+ "float alphaShift");
			e.printStackTrace();
			return;
		}
		
		if (source != null) {
			clSetKernelArg(kernel, 0, Sizeof.cl_mem, Pointer.to(gfx.getMemoryObject(canvas)));
			clSetKernelArg(kernel, 1, Sizeof.cl_mem, Pointer.to(gfx.getMemoryObject(textureIndex)));
			clSetKernelArg(kernel, 2, Sizeof.cl_int2, Pointer.to(new int[] {x, y}));
			clSetKernelArg(kernel, 3, Sizeof.cl_int2, Pointer.to(new int[] {dest.getWidth(), dest.getHeight()}));//needs changing?
			clSetKernelArg(kernel, 4, Sizeof.cl_int2, Pointer.to(new int[] {xSpriteOffset, ySpriteOffset}));
			clSetKernelArg(kernel, 5, Sizeof.cl_int2, Pointer.to(new int[] {source.getWidth(), source.getHeight()}));
			clSetKernelArg(kernel, 6, Sizeof.cl_float2, Pointer.to(new float[] {xScale, yScale}));
			clSetKernelArg(kernel, 7, Sizeof.cl_int2, Pointer.to(new int[] {(flipX?1:0), (flipY?1:0)}));
			clSetKernelArg(kernel, 8, Sizeof.cl_int2, Pointer.to(new int[] {(int)(spriteWidth*xScale), (int)(spriteHeight*yScale)}));
			clSetKernelArg(kernel, 9, Sizeof.cl_float, Pointer.to(new float[] {alphaShift}));
				
			long local_work_size[] = new long[]{1, 1};
			long global_work_size[] = new long[]{ (long) (spriteWidth*xScale), (long) (spriteHeight*yScale)};

			int err = clEnqueueNDRangeKernel(gfx.getCommandQueue(), kernel, 2, null, global_work_size, local_work_size, 0, null, null);
			if (err != org.jocl.CL.CL_SUCCESS) gfx.GfxLog(2, "Failed to render sprite: " + org.jocl.CL.stringFor_errorCode(err));
			gfx.updateResource(canvas);
		} else {
			gfx.GfxLog(2, "Tried to render invalid texture at index " + textureIndex);
		}
	}

}
