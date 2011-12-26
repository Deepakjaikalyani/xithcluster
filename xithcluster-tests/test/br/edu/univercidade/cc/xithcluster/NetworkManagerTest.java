package br.edu.univercidade.cc.xithcluster;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.fail;
import java.util.PriorityQueue;
import java.util.Queue;
import org.junit.Before;
import org.junit.Test;
import org.xith3d.loop.UpdatingThread.TimingMode;
import org.xsocket.connection.INonBlockingConnection;
import br.edu.univercidade.cc.xithcluster.distribution.RoundRobinDistribution;
import br.edu.univercidade.cc.xithcluster.messages.Message;
import br.edu.univercidade.cc.xithcluster.messages.MessageType;

public class NetworkManagerTest {
	
	private static final int RENDERERS_CONNECTION_PORT = 11111;
	
	private static final int COMPOSER_CONNECTION_PORT = 22222;
	
	private NetworkManager networkManager;
	
	private INonBlockingConnection connectionMock;
	
	@Before
	public void setUp() {
		networkManager = new NetworkManager("0.0.0.0", RENDERERS_CONNECTION_PORT, COMPOSER_CONNECTION_PORT, new RoundRobinDistribution());
		connectionMock = createMock(INonBlockingConnection.class);
	}
	
	@Test
	public void testShouldAssignRendererIdWhenANewRendererConnects() {
		expect(connectionMock.getLocalPort()).andReturn(RENDERERS_CONNECTION_PORT);
		connectionMock.setAttachment(0);
		connectionMock.setAutoflush(false);
		replay(connectionMock);
		
		Queue<Message> messages = new PriorityQueue<Message>(1);
		
		messages.add(new Message(MessageType.CONNECTED, connectionMock));
		
		networkManager.sessionState = SessionState.OPENED;
		
		// ---
		
		networkManager.processMessages(1L, 1L, TimingMode.MILLISECONDS, messages);
		
		// ---
		
		verify(connectionMock);
	}
	
	@Test
	public void testShouldCloseSessionWhenANewRendererConnects() {
		expect(connectionMock.getLocalPort()).andReturn(RENDERERS_CONNECTION_PORT);
		connectionMock.setAttachment(0);
		connectionMock.setAutoflush(false);
		replay(connectionMock);
		
		Queue<Message> messages = new PriorityQueue<Message>(1);
		
		messages.add(new Message(MessageType.CONNECTED, connectionMock));
		
		networkManager.sessionState = SessionState.OPENED;
		
		// ---
		
		networkManager.processMessages(1L, 1L, TimingMode.MILLISECONDS, messages);
		
		// ---
		
		verify(connectionMock);
		
		assertThat(networkManager.sessionState, equalTo(SessionState.CLOSED));
	}
	
	@Test
	public void testShouldCloseSessionWhenRendererDisconnects() {
		fail();
	}
	
	@Test
	public void testShouldCloseSessionWhenNewComposerConnects() {
		fail();
	}
	
	@Test
	public void testShouldCloseSessionWhenComposerDisconnects() {
		fail();
	}
	
	@Test
	public void testShouldOpenSessionWhenThereIsOneRendererAndOneComposer() {
		fail();
	}
	
	@Test
	public void testShouldNotOpenSessionWhenThereIsNoRenderer() {
		fail();
	}
	
	@Test
	public void testShouldNotOpenSessionWhenThereIsNoComposer() {
		fail();
	}
	
	@Test
	public void testShouldStartRenderingWhenRenderersAndComposerNotifySessionStarted() {
		fail();
	}
	
	@Test
	public void testShouldStartANewFrameWhenComposerNotifiesFinishedFrame() {
		fail();
	}
	
	@Test
	public void testShouldSendStartFrameMessageToRenderersWhenNewFrameStarts() {
		fail();
	}
	
	@Test
	public void testShouldSendSessionStartedMessageToRenderersWhenNewSessionIsOpened() {
		fail();
	}
	
	@Test
	public void testShouldSendSessionStartedMessageToComposerWhenNewSessionIsOpened() {
		fail();
	}
	
	@Test
	public void testShouldSendUpdateMessageToRenderersBeforeNewFrameStarts() {
		fail();
	}
	
}
