package br.edu.univercidade.cc.xithcluster.messages;

import static org.easymock.EasyMock.cmp;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;
import java.util.Queue;
import org.easymock.LogicalOperator;
import org.junit.Before;
import org.junit.Test;
import org.xsocket.MaxReadSizeExceededException;
import org.xsocket.connection.INonBlockingConnection;
import br.edu.univercidade.cc.xithcluster.CompressionMethod;
import br.edu.univercidade.cc.xithcluster.comparators.ClassNameComparator;

public class ComposerMessageBrokerTest {
	
	private static final byte[] FOUR_ZERO_BYTES = new byte[] {
	0, 0, 0, 0
	};
	
	private static final int MY_OWN_PORT = 1;
	
	private static final int ANOTHER_PORT_BUT_MY_OWN = 2;
	
	private static final byte[] BYTES = new byte[] {
		1
	};
	
	private ComposerMessageBroker composerMessageBroker;
	
	private INonBlockingConnection connectionMock;
	
	@Before
	public void setUp() {
		composerMessageBroker = new ComposerMessageBroker(1);
		connectionMock = createMock(INonBlockingConnection.class);
	}
	
	@Test
	public void testShouldPostComponentConnectedMessage() throws BufferUnderflowException, MaxReadSizeExceededException, IOException {
		expect(connectionMock.getRemotePort()).andReturn(ANOTHER_PORT_BUT_MY_OWN);
		
		replay(connectionMock);
		
		// ---
		
		composerMessageBroker.onConnect(connectionMock);
		
		// ---
		
		verify(connectionMock);
		
		Message message = getFirstInMessageQueue();
		
		assertThat(message.getType(), equalTo(MessageType.CONNECTED));
		assertThat(message.getSource(), equalTo(connectionMock));
	}
	
	@Test
	public void testShouldIgnoreAttemptsToConnectToMyself() throws BufferUnderflowException, MaxReadSizeExceededException, IOException {
		expect(connectionMock.getRemotePort()).andReturn(MY_OWN_PORT);
		
		replay(connectionMock);
		
		// ---
		
		composerMessageBroker.onConnect(connectionMock);
		
		// ---
		
		verify(connectionMock);
		
		Queue<Message> messages = MessageQueue.startReadingMessages();
		MessageQueue.stopReadingMessages();
		assertThat(messages.size(), equalTo(0));
	}
	
	@Test
	public void testShouldPostComponentDisconnectedMessage() throws BufferUnderflowException, MaxReadSizeExceededException, IOException {
		replay(connectionMock);
		
		// ---
		
		composerMessageBroker.onDisconnect(connectionMock);
		
		// ---
		
		verify(connectionMock);
		
		Message message = getFirstInMessageQueue();
		
		assertThat(message.getType(), equalTo(MessageType.DISCONNECTED));
		assertThat(message.getSource(), equalTo(connectionMock));
	}
	
	@Test
	public void testShouldPostStartSessionMessage() throws BufferUnderflowException, ClosedChannelException, MaxReadSizeExceededException, IOException {
		expectToReadMessageType(MessageType.START_SESSION);
		expectToSetMessageHandler(new StartSessionDataHandler(composerMessageBroker));
		
		connectionMock.markReadPosition();
		expect(connectionMock.readInt()).andReturn(1); // screenWidth
		expect(connectionMock.readInt()).andReturn(1); // screenHeight
		expect(connectionMock.readDouble()).andReturn(1.0); // targetFPS
		connectionMock.removeReadMark();
		
		expectToSetMessageBrokerAsHandler();
		
		replay(connectionMock);
		
		// ---
		
		composerMessageBroker.onData(connectionMock);
		
		// ---
		
		verify(connectionMock);
		
		Message message = getFirstInMessageQueue();
		
		assertThat(message.getType(), equalTo(MessageType.START_SESSION));
		assertThat(message.getSource(), equalTo(connectionMock));
		assertThat(message.getParameters(), not(nullValue()));
		assertThat((Integer) message.getParameters()[0], equalTo(1));
		assertThat((Integer) message.getParameters()[1], equalTo(1));
		assertThat((Double) message.getParameters()[2], equalTo(1.0));
	}
	
	@Test
	public void testShouldPostStartFrameMessage() throws IOException {
		expectToReadMessageType(MessageType.START_FRAME);
		expectToSetMessageHandler(new StartFrameDataHandler(composerMessageBroker));
		
		connectionMock.markReadPosition();
		expect(connectionMock.readLong()).andReturn(1L); // currentFrame
		expect(connectionMock.readLong()).andReturn(1L); // clockCount
		connectionMock.removeReadMark();
		
		expectToSetMessageBrokerAsHandler();
		
		replay(connectionMock);
		
		// ---
		
		composerMessageBroker.onData(connectionMock);
		
		// ---
		
		verify(connectionMock);
		
		Message message = getFirstInMessageQueue();
		
		assertThat(message.getType(), equalTo(MessageType.START_FRAME));
		assertThat(message.getSource(), equalTo(connectionMock));
		assertThat(message.getParameters(), not(nullValue()));
		assertThat((Long) message.getParameters()[0], equalTo(1L));
		assertThat((Long) message.getParameters()[1], equalTo(1L));
	}
	
	@Test
	public void testShouldPostSetCompositionOrderMessage() throws IOException {
		expectToReadMessageType(MessageType.SET_COMPOSITION_ORDER);
		expectToSetMessageHandler(new SetCompositionOrderDataHandler(composerMessageBroker));
		
		connectionMock.markReadPosition();
		expect(connectionMock.readInt()).andReturn(1); // compositionOrder
		connectionMock.removeReadMark();
		
		expectToSetMessageBrokerAsHandler();
		
		replay(connectionMock);
		
		// ---
		
		composerMessageBroker.onData(connectionMock);
		
		// ---
		
		verify(connectionMock);
		
		Message message = getFirstInMessageQueue();
		
		assertThat(message.getType(), equalTo(MessageType.SET_COMPOSITION_ORDER));
		assertThat(message.getSource(), equalTo(connectionMock));
		assertThat(message.getParameters(), not(nullValue()));
		assertThat((Integer) message.getParameters()[0], equalTo(1));
	}
	
	@Test
	public void testShouldPostNewImageMessage() throws IOException {
		expectToReadMessageType(MessageType.NEW_IMAGE);
		expectToSetMessageHandler(new NewImageDataHandler(composerMessageBroker));
		
		connectionMock.markReadPosition();
		expect(connectionMock.readLong()).andReturn(1L); // frameIndex
		expect(connectionMock.readInt()).andReturn(CompressionMethod.NONE.ordinal()); // compressionMethod
		expect(connectionMock.readInt()).andReturn(1); // length
		expect(connectionMock.readBytesByLength(1)).andReturn(BYTES); // colorAndAlphaBuffer
		expect(connectionMock.readInt()).andReturn(4); // length
		expect(connectionMock.readBytesByLength(4)).andReturn(FOUR_ZERO_BYTES); // depthBuffer
		connectionMock.removeReadMark();
		
		expectToSetMessageBrokerAsHandler();
		
		replay(connectionMock);
		
		// ---
		
		composerMessageBroker.onData(connectionMock);
		
		// ---
		
		verify(connectionMock);
		
		Message message = getFirstInMessageQueue();
		
		assertThat(message.getType(), equalTo(MessageType.NEW_IMAGE));
		assertThat(message.getSource(), equalTo(connectionMock));
		assertThat(message.getParameters(), not(nullValue()));
		assertThat((Long) message.getParameters()[0], equalTo(1L));
		assertThat((CompressionMethod) message.getParameters()[1], equalTo(CompressionMethod.NONE));
		byte[] colorAndAlphaBuffer = (byte[]) message.getParameters()[2];
		assertThat(colorAndAlphaBuffer, isA(byte[].class));
		assertThat(colorAndAlphaBuffer.length, equalTo(1));
		assertThat(colorAndAlphaBuffer[0], equalTo((byte) 1));
		float[] depthBuffer = (float[]) message.getParameters()[3];
		assertThat(depthBuffer, isA(float[].class));
		assertThat(depthBuffer.length, equalTo(1));
		assertThat(depthBuffer[0], equalTo((float) 0));
	}
	
	@Test
	public void testShouldIgnoreInvalidMessages() throws IOException {
		connectionMock.markReadPosition();
		expect(connectionMock.readInt()).andReturn(-1); // Invalid message type.
		expect(connectionMock.resetToReadMark()).andReturn(true);
		
		replay(connectionMock);
		
		// ---
		
		composerMessageBroker.onData(connectionMock);
		
		// ---
		
		verify(connectionMock);
		
		Queue<Message> messages = MessageQueue.startReadingMessages();
		MessageQueue.stopReadingMessages();
		assertThat(messages.size(), equalTo(0));
	}
	
	@Test
	public void testShouldNotPostMessageWhenIOExceptionOccurs() throws IOException {
		connectionMock.markReadPosition();
		expect(connectionMock.readInt()).andThrow(new IOException());
		expect(connectionMock.resetToReadMark()).andReturn(true);
		
		replay(connectionMock);
		
		// ---
		
		composerMessageBroker.onData(connectionMock);
		
		// ---
		
		verify(connectionMock);
		
		Queue<Message> messages = MessageQueue.startReadingMessages();
		MessageQueue.stopReadingMessages();
		assertThat(messages.size(), equalTo(0));
	}
	
	private Message getFirstInMessageQueue() {
		Queue<Message> messages = MessageQueue.startReadingMessages();
		MessageQueue.stopReadingMessages();
		assertThat(messages.size(), equalTo(1));
		return messages.poll();
	}
	
	private void expectToSetMessageBrokerAsHandler() throws IOException {
		connectionMock.setHandler(composerMessageBroker);
	}
	
	private void expectToSetMessageHandler(MessageHandler<?> messageHandler) throws IOException {
		connectionMock.setHandler(cmp(messageHandler, new ClassNameComparator(), LogicalOperator.EQUAL));
	}
	
	private void expectToReadMessageType(MessageType messageType) throws IOException {
		connectionMock.markReadPosition();
		expect(connectionMock.readInt()).andReturn(messageType.ordinal());
		connectionMock.removeReadMark();
	}
	
}
