package br.edu.univercidade.cc.xithcluster.messages;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.util.Queue;

import org.junit.Before;
import org.junit.Test;
import org.xsocket.MaxReadSizeExceededException;
import org.xsocket.connection.INonBlockingConnection;

public class MasterMessageBrokerTest {

	private MasterMessageBroker masterMessageBroker;
	
	@Before
	public void setUp() {
		masterMessageBroker = new MasterMessageBroker();
	}
	
	@Test
	public void testShouldPostComponentConnectedMessage() throws BufferUnderflowException, MaxReadSizeExceededException, IOException {
		INonBlockingConnection connectionMock = createMock(INonBlockingConnection.class);
		
		replay(connectionMock);
		
		// ---
		
		masterMessageBroker.onConnect(connectionMock);
		
		// ---
		
		verify(connectionMock);
		
		Queue<Message> messages = MessageQueue.startReadingMessages();
		
		assertThat(messages.size(), is(equalTo(new Integer(1))));
		
		Message message = messages.poll();
		assertThat(message.getType(), is(equalTo(MessageType.CONNECTED)));
		assertThat(message.getSource(), is(equalTo(connectionMock)));
		
		MessageQueue.stopReadingMessages();
	}

	@Test
	public void testShouldPostComponentDisconnectedMessage() {

	}

	@Test
	public void testShouldPostSessionStartedMessage() {

	}

	@Test
	public void testShouldPostFinishedFrameMessage() {

	}

	@Test
	public void testShouldIgnoreInvalidMessages() {

	}

}
