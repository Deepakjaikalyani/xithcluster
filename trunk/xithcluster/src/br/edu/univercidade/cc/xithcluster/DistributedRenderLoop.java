package br.edu.univercidade.cc.xithcluster;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import javax.xml.parsers.FactoryConfigurationError;
import org.apache.log4j.xml.DOMConfigurator;
import org.xith3d.base.Xith3DEnvironment;
import org.xith3d.loop.InputAdapterRenderLoop;
import org.xith3d.loop.UpdatingThread;
import org.xith3d.loop.opscheduler.OperationScheduler;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.View;
import br.edu.univercidade.cc.xithcluster.communication.MasterNetworkManager;

public class DistributedRenderLoop extends InputAdapterRenderLoop {
	
	private static final String LOG4J_CONFIGURATION_FILE = "xithcluster-log4j.xml";
	
	private UpdateManager updateManager;
	
	private MasterNetworkManager networkManager;
	
	private DistributionStrategy distributionStrategy;
	
	private int currentFrame = -1;
	
	private final Object sceneLock = new Object();
	
	public DistributedRenderLoop() {
		super();
	}
	
	public DistributedRenderLoop(Xith3DEnvironment x3dEnv, float maxFPS) {
		super(x3dEnv, maxFPS);
	}
	
	public DistributedRenderLoop(Xith3DEnvironment x3dEnv) {
		super(x3dEnv);
	}
	
	public DistributedRenderLoop(float maxFPS) {
		super(maxFPS);
	}
	
	public DistributionStrategy getDistributionStrategy() {
		return distributionStrategy;
	}
	
	public void setDistributionStrategy(DistributionStrategy distributionStrategy) {
		this.distributionStrategy = distributionStrategy;
	}
	
	@Override
	public void begin(RunMode runMode, TimingMode timingMode) {
		initializeLog4j();
		
		if (distributionStrategy == null) {
			// TODO:
			throw new RuntimeException("You must set a distribution strategy");
		}
		
		updateManager = new UpdateManager();
		getXith3DEnvironment().addScenegraphModificationListener(updateManager);
		
		networkManager = new MasterNetworkManager(this, updateManager, distributionStrategy);
		try {
			networkManager.initialize();
			super.begin(runMode, timingMode);
		} catch (UnknownHostException e) {
			// TODO:
			throw new RuntimeException("Error starting network manager", e);
		} catch (IOException e) {
			// TODO:
			throw new RuntimeException("Error starting network manager", e);
		}
	}

	private void initializeLog4j() throws FactoryConfigurationError {
		if (new File(LOG4J_CONFIGURATION_FILE).exists()) {
			DOMConfigurator.configure(LOG4J_CONFIGURATION_FILE);
		} else {
			System.err.println("Log4j not initialized: \"xithcluster-log4j.xml\" could not be found");
		}
	}
	
	@Override
	protected void loopIteration(long gameTime, long frameTime, UpdatingThread.TimingMode timingMode) {
		int framesToSkip;
		
		if (!networkManager.isSessionStarted()) {
			networkManager.startNewSession();
		} else {
			framesToSkip = networkManager.getSkipNextFrames();
			if (framesToSkip > 0) {
				// TODO: dT = dT + (frameTime * framesToSkip)
			} else {
				networkManager.notifyFrameStart(++currentFrame % Integer.MAX_VALUE);
			}
			
			// TODO: doSimulations(dT)
			// TODO: Check if this lock is needed!
			synchronized (sceneLock) {
				prepareNextFrame(gameTime, frameTime, timingMode);
				
				networkManager.sendPendingUpdates();
			}
		}
	}
	
	@Override
	protected void prepareNextFrame(long gameTime, long frameTime, UpdatingThread.TimingMode timingMode) {
		OperationScheduler scheduler;
		
		if (getXith3DEnvironment() != null) {
			getXith3DEnvironment().updatePhysicsEngine(gameTime, frameTime, timingMode);
			getXith3DEnvironment().updateInputSystem(gameTime, timingMode);
		}
		
		scheduler = getOperationScheduler();
		if (scheduler != null) {
			scheduler.update(gameTime, frameTime, timingMode);
		}
		
		if (getUpdater() == null || getUpdater() == scheduler) {
			return;
		}
		
		getUpdater().update(gameTime, frameTime, timingMode);
	}
	
	protected void renderNextFrame(long gameTime, long frameTime, UpdatingThread.TimingMode timingMode) {
		if (getXith3DEnvironment() != null) {
			getXith3DEnvironment().render(getGameNanoTime(), getLastNanoFrameTime());
		} else {
			// TODO:
			throw new RuntimeException("You must setup a Xith3d environment");
		}
	}

	public Object getSceneLock() {
		return sceneLock;
	}
	
	public BranchGroup getScene() {
		return getXith3DEnvironment().getBranchGroup();
	}
	
	public View getPointOfView() {
		return getXith3DEnvironment().getView();
	}
	
}
