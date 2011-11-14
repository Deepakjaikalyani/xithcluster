/**
 * Copyright (c) 2003-2010, Xith3D Project Group all rights reserved.
 * 
 * Portions based on the Java3D interface, Copyright by Sun Microsystems.
 * Many thanks to the developers of Java3D and Sun Microsystems for their
 * innovation and design.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * Neither the name of the 'Xith3D Project Group' nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) A
 * RISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE
 */
package org.xith3d.render;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.xith3d.scenegraph.GroupNode;

public class DirectByteBufferRenderTarget implements RenderTarget {
	
	private static ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;
	
	private GroupNode group;
	
	private int width;
	
	private int height;
	
	private ByteBuffer directByteBuffer;
	
	private byte[] directByteBufferAsArray;
	
	private int format;
	
	private int type;
	
	private int elementSize;
	
	public DirectByteBufferRenderTarget(GroupNode group, int width, int height, int format, int type, int elementSize) {
		this.group = group;
		this.width = width;
		this.height = height;
		this.format = format;
		this.type = type;
		this.elementSize = elementSize;
		
		allocateDirectByteBuffer();
	}
	
	public static ByteOrder getByteOrder() {
		return byteOrder;
	}
	
	public static void setByteOrder(ByteOrder byteOrder) {
		DirectByteBufferRenderTarget.byteOrder = byteOrder;
	}
	
	private void allocateDirectByteBuffer() {
		int size;
		
		size = width * height * elementSize;
		
		directByteBuffer = ByteBuffer.allocateDirect(size);
		directByteBuffer.order(DirectByteBufferRenderTarget.byteOrder);
		
		directByteBufferAsArray = new byte[size];
	}
	
	public GroupNode getGroup() {
		return group;
	}
	
	public ByteBuffer getDirectByteBuffer() {
		return directByteBuffer;
	}
	
	public int getFormat() {
		return format;
	}
	
	public void setBackgroundRenderingEnabled(boolean enabled) {
	}
	
	public boolean isBackgroundRenderingEnabled() {
		return false;
	}
	
	public void freeOpenGLResources(CanvasPeer canvasPeer) {
	}
	
	public void freeOpenGLResources(Canvas3D canvas) {
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public byte[] getDirectByteBufferAsArray() {
		directByteBuffer.get(directByteBufferAsArray);
		directByteBuffer.rewind();
		
		return directByteBufferAsArray;
	}

	public int getType() {
		return type;
	}
	
}
