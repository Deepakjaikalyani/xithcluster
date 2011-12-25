package br.edu.univercidade.cc.xithcluster.messages;

import static org.easymock.EasyMock.cmp;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
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

public class MasterMessageBrokerTest {
	
	private MasterMessageBroker masterMessageBroker;
	
	private INonBlockingConnection connectionMock;
	
	@Before
	public void setUp() {
		masterMessageBroker = new MasterMessageBroker();
		connectionMock = createMock(INonBlockingConnection.class);
	}
	
	@Test
	public void testShouldPostComponentConnectedMessage() throws BufferUnderflowException, MaxReadSizeExceededException, IOException {
		replay(connectionMock);
		
		// ---
		
		masterMessageBroker.onConnect(connectionMock);
		
		// ---
		
		verify(connectionMock);
		
		Message message = getFirstInMessageQueue();
		
		assertThat(message.getType(), equalTo(MessageType.CONNECTED));
		assertThat(message.getSource(), equalTo(connectionMock));
	}
	
	@Test
	public void testShouldPostComponentDisconnectedMessage() throws BufferUnderflowException, MaxReadSizeExceededException, IOException {
		replay(connectionMock);
		
		// ---
		
		masterMessageBroker.onDisconnect(connectionMock);
		
		// ---
		
		verify(connectionMock);
		
		Message message = getFirstInMessageQueue();
		
		assertThat(message.getType(), equalTo(MessageType.DISCONNECTED));
		assertThat(message.getSource(), equalTo(connectionMock));
	}
	
	@Test
	public void testShouldPostSessionStartedMessage() throws BufferUnderflowException, ClosedChannelException, MaxReadSizeExceededException, IOException {
		expectToReadMessageType(MessageType.SESSION_STARTED);
		
		replay(connectionMock);
		
		// ---
		
		masterMessageBroker.onData(connectionMock);
		
		// ---
		
		verify(connectionMock);
		
		Message message = getFirstInMessageQueue();
		
		assertThat(message.getType(), equalTo(MessageType.SESSION_STARTED));
		assertThat(message.getSource(), equalTo(connectionMock));
	}
	
	@Test
	public void testShouldPostFinishedFrameMessage() throws IOException {
		expectToReadMessageType(MessageType.FINISHED_FRAME);
		expectToSetMessageHandler(new FinishedFrameDataHandler(masterMessageBroker));
		
		connectionMock.markReadPosition();
		expect(connectionMock.readLong()).andReturn(1L);
		connectionMock.removeReadMark();
		
		expectToSetMessageBrokerAsHandler();
		
		replay(connectionMock);
		
		// ---
		
		masterMessageBroker.onData(connectionMock);
		
		// ---
		
		verify(connectionMock);
		
		Message message = getFirstInMessageQueue();
		
		assertThat(message.getType(), equalTo(MessageType.FINISHED_FRAME));
		assertThat(message.getSource(), equalTo(connectionMock));
		assertThat(message.getParameters(), not(nullValue()));
		assertThat((Long) message.getParameters()[0], equalTo(1L));
	}
	
	@Test
	public void testShouldIgnoreInvalidMessages() throws IOException {
		connectionMock.markReadPosition();
		expect(connectionMock.readInt()).andReturn(-1);
		expect(connectionMock.resetToReadMark()).andReturn(true);
		
		replay(connectionMock);
		
		// ---
		
		masterMessageBroker.onData(connectionMock);
		
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
		
		masterMessageBroker.onData(connectionMock);
		
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
		connectionMock.setHandler(masterMessageBroker);
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
