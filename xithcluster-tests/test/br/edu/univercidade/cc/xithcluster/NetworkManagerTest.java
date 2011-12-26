package br.edu.univercidade.cc.xithcluster;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.fail;
import java.awt.Dimension;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.BufferOverflowException;
import java.nio.channels.ClosedChannelException;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Queue;
import org.junit.Before;
import org.junit.Test;
import org.xith3d.loop.UpdatingThread.TimingMode;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.View;
import org.xsocket.connection.INonBlockingConnection;
import br.edu.univercidade.cc.xithcluster.distribution.DistributionStrategy;
import br.edu.univercidade.cc.xithcluster.distribution.RoundRobinDistribution;
import br.edu.univercidade.cc.xithcluster.messages.Message;
import br.edu.univercidade.cc.xithcluster.messages.MessageType;
import br.edu.univercidade.cc.xithcluster.serialization.packagers.PointOfViewPackager;
import br.edu.univercidade.cc.xithcluster.serialization.packagers.ScenePackager;

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
	public void testShouldAssignARendererIdWhenANewRendererConnects() {
		expect(connectionMock.getLocalPort()).andReturn(RENDERERS_CONNECTION_PORT);
		expectToSetRendererId(0);
		connectionMock.setAutoflush(false);
		
		replay(connectionMock);
		
		// Forcing session state as opened.
		networkManager.sessionState = SessionState.OPENED;
		
		Queue<Message> messages = new PriorityQueue<Message>(Arrays.asList(new Message(MessageType.CONNECTED, connectionMock)));
		
		// ---
		
		networkManager.processMessages(1L, 1L, TimingMode.MILLISECONDS, messages);
		
		// ---
		
		verify(connectionMock);
	}
	
	@Test
	public void testShouldStoreReferenceWhenANewRendererConnects() {
		expect(connectionMock.getLocalPort()).andReturn(RENDERERS_CONNECTION_PORT);
		expectToSetRendererId(0);
		connectionMock.setAutoflush(false);
		
		replay(connectionMock);
		
		// Forcing session state as opened.
		networkManager.sessionState = SessionState.OPENED;
		
		Queue<Message> messages = new PriorityQueue<Message>(1);
		messages.add(new Message(MessageType.CONNECTED, connectionMock));
		
		// ---
		
		networkManager.processMessages(1L, 1L, TimingMode.MILLISECONDS, messages);
		
		// ---
		
		verify(connectionMock);
		
		assertThat(networkManager.renderersConnections.size(), equalTo(1));
		assertThat(networkManager.renderersConnections.get(0), equalTo(connectionMock));
	}
	
	@Test
	public void testShouldCloseSessionWhenANewRendererConnects() {
		expect(connectionMock.getLocalPort()).andReturn(RENDERERS_CONNECTION_PORT);
		expectToSetRendererId(0);
		connectionMock.setAutoflush(false);
		
		replay(connectionMock);
		
		// Forcing session state as opened.
		networkManager.sessionState = SessionState.OPENED;
		
		Queue<Message> messages = new PriorityQueue<Message>(1);
		messages.add(new Message(MessageType.CONNECTED, connectionMock));
		
		// ---
		
		networkManager.processMessages(1L, 1L, TimingMode.MILLISECONDS, messages);
		
		// ---
		
		verify(connectionMock);
		
		assertThat(networkManager.sessionState, equalTo(SessionState.CLOSED));
	}
	
	private void expectToSetRendererId(int rendererId) {
		connectionMock.setAttachment(rendererId);
	}
	
	@Test
	public void testShouldRemoveReferenceWhenRendererDisconnects() {
		expect(connectionMock.getLocalPort()).andReturn(RENDERERS_CONNECTION_PORT);
		expectToGetRendererId(connectionMock, 0);
		
		replay(connectionMock);
		
		// Adding connection mock as a renderer connection.
		networkManager.renderersConnections.add(connectionMock);
		
		// Forcing session state as opened.
		networkManager.sessionState = SessionState.OPENED;
		
		Queue<Message> messages = new PriorityQueue<Message>(1);
		messages.add(new Message(MessageType.DISCONNECTED, connectionMock));
		
		// ---
		
		networkManager.processMessages(1L, 1L, TimingMode.MILLISECONDS, messages);
		
		// ---
		
		verify(connectionMock);
		
		assertThat(networkManager.renderersConnections.size(), equalTo(0));
	}
	
	@Test
	public void testShouldCloseSessionWhenRendererDisconnects() {
		expect(connectionMock.getLocalPort()).andReturn(RENDERERS_CONNECTION_PORT);
		expectToGetRendererId(connectionMock, 0);
		
		replay(connectionMock);
		
		// Adding connection mock as a renderer connection.
		networkManager.renderersConnections.add(connectionMock);
		
		// Forcing session state as opened.
		networkManager.sessionState = SessionState.OPENED;
		
		Queue<Message> messages = new PriorityQueue<Message>(1);
		messages.add(new Message(MessageType.DISCONNECTED, connectionMock));
		
		// ---
		
		networkManager.processMessages(1L, 1L, TimingMode.MILLISECONDS, messages);
		
		// ---
		
		verify(connectionMock);
		
		assertThat(networkManager.sessionState, equalTo(SessionState.CLOSED));
	}
	
	private void expectToGetRendererId(INonBlockingConnection connectionMock, int rendererId) {
		expect(connectionMock.getAttachment()).andReturn(rendererId);
	}
	
	@Test
	public void testShouldStoreReferenceWhenNewComposerConnects() {
		expect(connectionMock.getLocalPort()).andReturn(COMPOSER_CONNECTION_PORT);
		connectionMock.setAutoflush(false);
		
		replay(connectionMock);
		
		// Nullifying composer connection reference.
		networkManager.composerConnection = null;
		
		// Forcing session state as opened.
		networkManager.sessionState = SessionState.OPENED;
		
		Queue<Message> messages = new PriorityQueue<Message>(1);
		messages.add(new Message(MessageType.CONNECTED, connectionMock));
		
		// ---
		
		networkManager.processMessages(1L, 1L, TimingMode.MILLISECONDS, messages);
		
		// ---
		
		verify(connectionMock);
		
		assertThat(networkManager.composerConnection, equalTo(connectionMock));
	}
	
	@Test
	public void testShouldCloseSessionWhenNewComposerConnects() {
		expect(connectionMock.getLocalPort()).andReturn(COMPOSER_CONNECTION_PORT);
		connectionMock.setAutoflush(false);
		
		replay(connectionMock);
		
		// Forcing session state as opened.
		networkManager.sessionState = SessionState.OPENED;
		
		Queue<Message> messages = new PriorityQueue<Message>(1);
		messages.add(new Message(MessageType.CONNECTED, connectionMock));
		
		// ---
		
		networkManager.processMessages(1L, 1L, TimingMode.MILLISECONDS, messages);
		
		// ---
		
		verify(connectionMock);
		
		assertThat(networkManager.sessionState, equalTo(SessionState.CLOSED));
	}
	
	@Test
	public void testShouldCloseSessionWhenComposerDisconnects() {
		expect(connectionMock.getLocalPort()).andReturn(COMPOSER_CONNECTION_PORT);
		
		replay(connectionMock);
		
		// Forcing session state as opened.
		networkManager.sessionState = SessionState.OPENED;
		
		Queue<Message> messages = new PriorityQueue<Message>(1);
		messages.add(new Message(MessageType.DISCONNECTED, connectionMock));
		
		// ---
		
		networkManager.processMessages(1L, 1L, TimingMode.MILLISECONDS, messages);
		
		// ---
		
		verify(connectionMock);
		
		assertThat(networkManager.sessionState, equalTo(SessionState.CLOSED));
	}
	
	@Test
	public void testShouldOpenSessionWhenThereIsOneRendererAndOneComposer() throws IOException {
		BranchGroup root = new BranchGroup();
		View pointOfView = new View();
		
		SceneManager sceneManager = createMock(SceneManager.class);
		expect(sceneManager.getSceneInfo()).andReturn(new SceneInfo(root, pointOfView));
		expect(sceneManager.getScreenSize()).andReturn(new Dimension(800, 600)).times(2);
		expect(sceneManager.getTargetFPS()).andReturn(80.0f).times(2);
		replay(sceneManager);
		
		DistributionStrategy distributionStrategyMock = createMock(DistributionStrategy.class);
		expect(distributionStrategyMock.distribute(root, 1)).andReturn(Arrays.asList(root));
		replay(distributionStrategyMock);
		
		PointOfViewPackager pointOfViewPackagerMock = createMock(PointOfViewPackager.class);
		expect(pointOfViewPackagerMock.serialize(pointOfView)).andReturn(new byte[0]);
		replay(pointOfViewPackagerMock);
		
		ScenePackager scenePackagerMock = createMock(ScenePackager.class);
		expect(scenePackagerMock.serialize(root)).andReturn(new byte[0]);
		replay(scenePackagerMock);
		
		INonBlockingConnection rendererConnection = createMock(INonBlockingConnection.class);
		expect(connectionMock.getAttachment()).andReturn(0).times(2);
		expectToSendRendererStartSessionMessage(rendererConnection, 0, 800, 600, 80.0f, new byte[0], new byte[0]);
		replay(rendererConnection);
		
		INonBlockingConnection composerConnection = createMock(INonBlockingConnection.class);
		expectToSendComposerStartSessionMessage(composerConnection, 800, 600, 80.0f);
		replay(composerConnection);
		
		networkManager.distributionStrategy = distributionStrategyMock;
		networkManager.sceneManager = sceneManager;
		networkManager.pointOfViewPackager = pointOfViewPackagerMock;
		networkManager.scenePackager = scenePackagerMock;
		networkManager.renderersConnections.add(rendererConnection);
		networkManager.composerConnection = composerConnection;
		networkManager.sessionState = SessionState.CLOSED;
		
		// ---
		
		networkManager.update(0);
		
		// ---
		
		verify(sceneManager);
		verify(distributionStrategyMock);
		verify(pointOfViewPackagerMock);
		verify(scenePackagerMock);
		verify(rendererConnection);
		verify(composerConnection);
		
		assertThat(networkManager.sessionState, equalTo(SessionState.OPENING));
	}
	
	private void expectToSendComposerStartSessionMessage(INonBlockingConnection connectionMock,
			int screenWidth,
			int screenHeight,
			float targetFps) throws BufferOverflowException, IOException {
		expect(connectionMock.write(MessageType.START_SESSION.ordinal())).andReturn(0);
		connectionMock.flush();
		expect(connectionMock.write(screenWidth)).andReturn(0);
		expect(connectionMock.write(screenHeight)).andReturn(0);
		expect(connectionMock.write(targetFps)).andReturn(0);
		connectionMock.flush();
	}
	
	private void expectToSendRendererStartSessionMessage(INonBlockingConnection connectionMock,
			int rendererId,
			int screenWidth,
			int screenHeight,
			float targetFps,
			byte[] pointOfViewData,
			byte[] sceneData) throws IOException, ClosedChannelException, SocketTimeoutException {
		expect(connectionMock.write(MessageType.START_SESSION.ordinal())).andReturn(0);
		connectionMock.flush();
		expect(connectionMock.write(rendererId)).andReturn(0);
		expect(connectionMock.write(screenWidth)).andReturn(0);
		expect(connectionMock.write(screenHeight)).andReturn(0);
		expect(connectionMock.write(targetFps)).andReturn(0);
		expect(connectionMock.write(pointOfViewData.length)).andReturn(0);
		expect(connectionMock.write(pointOfViewData)).andReturn(0);
		expect(connectionMock.write(sceneData.length)).andReturn(0);
		expect(connectionMock.write(sceneData)).andReturn(0);
		connectionMock.flush();
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
