package kernelPlugins;

import static org.jocl.CL.clEnqueueNDRangeKernel;
import static org.jocl.CL.clSetKernelArg;

import org.jocl.Pointer;
import org.jocl.Sizeof;

import memoryPlugins.IntArrayImage;
import tbh.gfxInterface.KernelPlugin;

public class FillColor extends KernelPlugin{

	@Override
	public void run(Object[] args) {
		int canvas;
		int color;
		IntArrayImage img;
		try {
			canvas = (int) args[0];
			color = (int) args[1];
			img = (IntArrayImage) gfx.getMemoryPlugin(canvas);
		} catch (Exception e) {
			gfx.GfxLog(2, "Illegal arguments for FillColor plugin, args are:\n"
					+ "int canvas\n"
					+ "int color");
			e.printStackTrace();
			return;
		}
		
		clSetKernelArg(kernel, 0, Sizeof.cl_mem, Pointer.to(gfx.getMemoryObject(canvas)));
	    clSetKernelArg(kernel, 1, Sizeof.cl_int2, Pointer.to(new int[] {img.getWidth(), img.getHeight()}));
	    clSetKernelArg(kernel, 2, Sizeof.cl_uint, Pointer.to(new int[] {color}));

	    long local_work_size[] = new long[]{1, 1};
	    long global_work_size[] = new long[]{img.getWidth(), img.getHeight()};

	    int err = clEnqueueNDRangeKernel(gfx.getCommandQueue(), kernel, 2, null, global_work_size, local_work_size, 0, null, null);
	    if (err != org.jocl.CL.CL_SUCCESS) gfx.GfxLog(2, "Screen fill color failed: " + org.jocl.CL.stringFor_errorCode(err));
	    gfx.updateResource(canvas);
	}

}