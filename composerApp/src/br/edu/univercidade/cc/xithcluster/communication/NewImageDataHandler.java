package br.edu.univercidade.cc.xithcluster.communication;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.channels.ClosedChannelException;
import org.xsocket.MaxReadSizeExceededException;
import org.xsocket.connection.INonBlockingConnection;
import br.edu.univercidade.cc.xithcluster.CompressionMethod;

public final class NewImageDataHandler extends ChainedTransactionalDataHandler<ComposerMessageBroker> {
	
	private int frameIndex;
	
	private CompressionMethod compressionMethod;
	
	private byte[] colorAndAlphaBuffer;
	
	private byte[] depthBuffer;
	
	public NewImageDataHandler(ComposerMessageBroker nextDataHandler) {
		super(nextDataHandler);
	}

	@Override
	protected boolean onHandleData(INonBlockingConnection arg0) throws IOException, BufferUnderflowException, ClosedChannelException, MaxReadSizeExceededException {
		frameIndex = arg0.readInt();
		compressionMethod = CompressionMethod.values()[arg0.readInt()];
		colorAndAlphaBuffer = arg0.readBytesByLength(arg0.readInt());
		depthBuffer = arg0.readBytesByLength(arg0.readInt());
				
		return true;
	}

	@Override
	protected void onDataReady(INonBlockingConnection arg0) throws IOException {
		ByteBuffer bb = ByteBuffer.wrap(depthBuffer);
	    bb.rewind();
	    FloatBuffer fb = ((ByteBuffer) bb.rewind()).asFloatBuffer();
	    float[] fs = new float[fb.capacity()];
	    fb.rewind();
	    fb.get(fs);
		
		getNextDataHandler().onNewImageCompleted(arg0, frameIndex, compressionMethod, colorAndAlphaBuffer, fs);
	}
	
}
