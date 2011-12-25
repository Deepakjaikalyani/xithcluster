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
import br.edu.univercidade.cc.xithcluster.comparators.ClassNameComparator;

public class RendererMessageBrokerTest {
	
	private static final byte[] BYTES = new byte[] { 1 };

	private RendererMessageBroker rendererMessageBroker;
	
	private INonBlockingConnection connectionMock;
	
	@Before
	public void setUp() {
		rendererMessageBroker = new RendererMessageBroker();
		connectionMock = createMock(INonBlockingConnection.class);
	}
	
	@Test
	public void testShouldPostStartSessionMessage() throws BufferUnderflowException, ClosedChannelException, MaxReadSizeExceededException, IOException {
		expectToReadMessageType(MessageType.START_SESSION);
		expectToSetMessageHandler(new RendererStartSessionDataHandler(rendererMessageBroker));
		
		connectionMock.markReadPosition();
		expect(connectionMock.readInt()).andReturn(1); // id
		expect(connectionMock.readInt()).andReturn(1); // screenWidth
		expect(connectionMock.readInt()).andReturn(1); // screenHeight
		expect(connectionMock.readDouble()).andReturn(1.0); // targetFPS
		expect(connectionMock.readInt()).andReturn(1); // length
		expect(connectionMock.readBytesByLength(1)).andReturn(BYTES); // pointOfViewData
		expect(connectionMock.readInt()).andReturn(1); // length
		expect(connectionMock.readBytesByLength(1)).andReturn(BYTES); // sceneData
		connectionMock.removeReadMark();
		
		expectToSetMessageBrokerAsHandler();
		
		replay(connectionMock);
		
		// ---
		
		rendererMessageBroker.onData(connectionMock);
		
		// ---
		
		verify(connectionMock);
		
		Message message = getFirstInMessageQueue();
		
		assertThat(message.getType(), equalTo(MessageType.START_SESSION));
		assertThat(message.getSource(), equalTo(connectionMock));
		assertThat(message.getParameters(), not(nullValue()));
		assertThat((Integer) message.getParameters()[0], equalTo(1));
		assertThat((Integer) message.getParameters()[1], equalTo(1));
		assertThat((Integer) message.getParameters()[2], equalTo(1));
		assertThat((Double) message.getParameters()[3], equalTo(1.0));
		byte[] pointOfViewData = (byte[]) message.getParameters()[4];
		assertThat(pointOfViewData, isA(byte[].class));
		assertThat(pointOfViewData[0], equalTo((byte)1));
		byte[] sceneData = (byte[]) message.getParameters()[5];
		assertThat(sceneData, isA(byte[].class));
		assertThat(sceneData[0], equalTo((byte)1));
	}
	
	@Test
	public void testShouldPostStartFrameMessage() throws IOException {
		expectToReadMessageType(MessageType.START_FRAME);
		expectToSetMessageHandler(new RendererStartFrameDataHandler(rendererMessageBroker));
		
		connectionMock.markReadPosition();
		expect(connectionMock.readLong()).andReturn(1L); // currentFrame
		expect(connectionMock.readLong()).andReturn(1L); // clockCount
		connectionMock.removeReadMark();
		
		expectToSetMessageBrokerAsHandler();
		
		replay(connectionMock);
		
		// ---
		
		rendererMessageBroker.onData(connectionMock);
		
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
	public void testShouldPostUpdateMessage() throws IOException {
		expectToReadMessageType(MessageType.UPDATE);
		expectToSetMessageHandler(new UpdateDataHandler(rendererMessageBroker));
		
		connectionMock.markReadPosition();
		expect(connectionMock.readInt()).andReturn(1); // length
		expect(connectionMock.readBytesByLength(1)).andReturn(BYTES); // updatesData
		connectionMock.removeReadMark();
		
		expectToSetMessageBrokerAsHandler();
		
		replay(connectionMock);
		
		// ---
		
		rendererMessageBroker.onData(connectionMock);
		
		// ---
		
		verify(connectionMock);
		
		Message message = getFirstInMessageQueue();
		
		assertThat(message.getType(), equalTo(MessageType.UPDATE));
		assertThat(message.getSource(), equalTo(connectionMock));
		assertThat(message.getParameters(), not(nullValue()));
		byte[] updatesData = (byte[]) message.getParameters()[0];
		assertThat(updatesData, isA(byte[].class));
		assertThat(updatesData[0], equalTo((byte)1));
	}
	
	@Test
	public void testShouldIgnoreInvalidMessages() throws IOException {
		connectionMock.markReadPosition();
		expect(connectionMock.readInt()).andReturn(-1); // Invalid message type.
		expect(connectionMock.resetToReadMark()).andReturn(true);
		
		replay(connectionMock);
		
		// ---
		
		rendererMessageBroker.onData(connectionMock);
		
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
		
		rendererMessageBroker.onData(connectionMock);
		
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
		connectionMock.setHandler(rendererMessageBroker);
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
