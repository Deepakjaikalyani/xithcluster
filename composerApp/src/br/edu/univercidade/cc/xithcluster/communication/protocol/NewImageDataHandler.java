package br.edu.univercidade.cc.xithcluster.communication.protocol;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;
import org.xsocket.MaxReadSizeExceededException;
import org.xsocket.connection.INonBlockingConnection;
import br.edu.univercidade.cc.xithcluster.CompressionMethod;

public final class NewImageDataHandler extends ChainedSafeDataHandler<ComposerProtocolHandler> {
	
	private CompressionMethod compressionMethod;
	
	private byte[] colorAndAlphaBuffer;
	
	private byte[] depthBuffer;
	
	public NewImageDataHandler(ComposerProtocolHandler chainedDataHandler) {
		super(chainedDataHandler);
	}

	@Override
	protected boolean onHandleData(INonBlockingConnection arg0) throws IOException, BufferUnderflowException, ClosedChannelException, MaxReadSizeExceededException {
		compressionMethod = CompressionMethod.values()[arg0.readInt()];
		colorAndAlphaBuffer = arg0.readBytesByLength(arg0.readInt());
		depthBuffer = arg0.readBytesByLength(arg0.readInt());
		
		return true;
	}

	@Override
	protected void onDataReady(INonBlockingConnection arg0) throws IOException {
		getNextDataHandler().onNewImageCompleted(arg0, compressionMethod, colorAndAlphaBuffer, depthBuffer);
	}
	
}
