package br.edu.univercidade.cc.xithcluster.communication;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;
import org.xsocket.MaxReadSizeExceededException;
import org.xsocket.connection.INonBlockingConnection;
import br.edu.univercidade.cc.xithcluster.CompressionMethod;
import br.edu.univercidade.cc.xithcluster.utils.BufferUtils;

public final class NewImageDataHandler extends ChainedTransactionalDataHandler<ComposerMessageBroker> {
	
	private long frameIndex;
	
	private CompressionMethod compressionMethod;
	
	private byte[] colorAndAlphaBuffer;
	
	private byte[] depthBuffer;
	
	public NewImageDataHandler(ComposerMessageBroker nextDataHandler) {
		super(nextDataHandler);
	}

	@Override
	protected boolean onHandleData(INonBlockingConnection arg0) throws IOException, BufferUnderflowException, ClosedChannelException, MaxReadSizeExceededException {
		frameIndex = arg0.readLong();
		compressionMethod = CompressionMethod.values()[arg0.readInt()];
		colorAndAlphaBuffer = arg0.readBytesByLength(arg0.readInt());
		depthBuffer = arg0.readBytesByLength(arg0.readInt());
				
		return true;
	}

	@Override
	protected void onDataReady(INonBlockingConnection arg0) throws IOException {
		getNextDataHandler().onNewImageCompleted(arg0, frameIndex, compressionMethod, colorAndAlphaBuffer, BufferUtils.safeBufferRead(BufferUtils.wrapAsFloatBuffer(depthBuffer)));
	}

}
