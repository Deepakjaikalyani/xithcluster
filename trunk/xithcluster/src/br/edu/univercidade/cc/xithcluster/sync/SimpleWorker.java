package br.edu.univercidade.cc.xithcluster.sync;


public abstract class SimpleWorker implements Runnable {

	private long delay = -1;
	
	private long interval = -1;
	
	private boolean runOnce = false;
	
	protected boolean stop = false;
	
	public SimpleWorker() {
		runOnce = true;
	}
	
	public SimpleWorker(long interval) {
		this();
		setInterval(interval);
	}
	
	public SimpleWorker(long interval, long delay) {
		this(interval);
		this.delay = delay;
	}
	
	protected void setInterval(long interval) {
		this.interval = interval;
		this.runOnce = false;
	}

	protected void runOnce() {
		runOnce = true;
	}
	
	public void stop() {
		stop = true;
	}
	
	@Override
	public void run() {
		beforeWork();
		
		if (delay > 0) {
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
			}
		}
		
		if (runOnce) {
			doWork();
		} else {
			while (!stop) {
				doWork();
				
				if (interval > 0) {
					try {
						Thread.sleep(interval);
					} catch (InterruptedException e) {
					}
				}
			}
		}
		
		afterWork();
	}
	
	protected void afterWork() {
	}

	protected void beforeWork() {
	}

	protected abstract void doWork();
}
