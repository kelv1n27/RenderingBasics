package kernelPlugins;

import static org.jocl.CL.clEnqueueNDRangeKernel;
import static org.jocl.CL.clSetKernelArg;

import org.jocl.Pointer;
import org.jocl.Sizeof;

import memoryPlugins.IntArrayImage;
import tbh.gfxInterface.KernelPlugin;

public class DrawLine extends KernelPlugin{
	

	@Override
	public void run(Object[] arg0) {
		int startX, startY, endX, endY, color, canvas;
		IntArrayImage img;
		try {
			startX = (int) arg0[0];
			startY = (int) arg0[1];
			endX = (int) arg0[2];
			endY = (int) arg0[3];
			color = (int) arg0[4];
			canvas = (int) arg0[5];
			img = (IntArrayImage) gfx.getMemoryPlugin(canvas);
		} catch (Exception e) {
			gfx.GfxLog(2, "Illegal Arguments for DrawLine plugin, args are:\n"
					+ "int startX\n"
					+ "int startY\n"
					+ "int endX\n"
					+ "int endY\n"
					+ "int color\n"
					+ "int canvas");
			e.printStackTrace();
			return;
		}
		int xDiff = endX - startX;
		int yDiff = endY - startY;
		int length = (int) Math.sqrt((double)(xDiff * xDiff) + (yDiff * yDiff));
		clSetKernelArg(kernel, 0, Sizeof.cl_mem, Pointer.to(gfx.getMemoryObject(canvas)));
		clSetKernelArg(kernel, 1, Sizeof.cl_int2, Pointer.to(new int[] {img.getWidth(), img.getHeight()}));
		clSetKernelArg(kernel, 2, Sizeof.cl_int2, Pointer.to(new int[] {startX, startY}));
		clSetKernelArg(kernel, 3, Sizeof.cl_int2, Pointer.to(new int[] {xDiff, yDiff}));
		clSetKernelArg(kernel, 4, Sizeof.cl_uint, Pointer.to(new int[] {length}));
		clSetKernelArg(kernel, 5, Sizeof.cl_uint, Pointer.to(new int[] {color}));
		
		long local_work_size[] = new long[]{1, 1};
		long global_work_size[] = new long[]{ (long) length, 0L};

		int err = clEnqueueNDRangeKernel(gfx.getCommandQueue(), kernel, 1, null, global_work_size, local_work_size, 0, null, null);
		if (err != org.jocl.CL.CL_SUCCESS) System.out.println("Failed to render line: " + org.jocl.CL.stringFor_errorCode(err));
		gfx.updateResource(canvas);
	}

}
