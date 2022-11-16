package kernelPlugins;

import static org.jocl.CL.clEnqueueNDRangeKernel;
import static org.jocl.CL.clSetKernelArg;

import org.jocl.Pointer;
import org.jocl.Sizeof;

import memoryPlugins.IntArrayImage;
import tbh.gfxInterface.KernelPlugin;

public class Upscale extends KernelPlugin{

	@Override
	public void run(Object[] arg0) {
		int source, dest;
		IntArrayImage from, to;
		
		try {
			source = (int) arg0[0];
			dest = (int) arg0[1];
			from = (IntArrayImage) gfx.getMemoryPlugin(source);
			to = (IntArrayImage) gfx.getMemoryPlugin(dest);
		} catch (Exception e) {
			gfx.GfxLog(2, "Illegal Arguments for Upscale plugin, args are:\n"
					+ "int source\n"
					+ "int dest");
			e.printStackTrace();
			return;
		}
		
		clSetKernelArg(kernel, 0, Sizeof.cl_mem, Pointer.to(gfx.getMemoryObject(dest)));
        clSetKernelArg(kernel, 1, Sizeof.cl_mem, Pointer.to(gfx.getMemoryObject(source)));
        clSetKernelArg(kernel, 2, Sizeof.cl_int2, Pointer.to(new int[] {to.getWidth(), to.getHeight()}));
        clSetKernelArg(kernel, 3, Sizeof.cl_int2, Pointer.to(new int[] {from.getWidth(), from.getHeight()}));
        long local_work_size[] = new long[]{1, 1};
	    long global_work_size[] = new long[]{to.getWidth(), to.getHeight()};

	    int err = clEnqueueNDRangeKernel(gfx.getCommandQueue(), kernel, 2, null, global_work_size, local_work_size, 0, null, null);
	    if (err != org.jocl.CL.CL_SUCCESS) gfx.GfxLog(2, "Upscale failed: " + org.jocl.CL.stringFor_errorCode(err));
	        
	    gfx.updateResource(dest);
	}

}
