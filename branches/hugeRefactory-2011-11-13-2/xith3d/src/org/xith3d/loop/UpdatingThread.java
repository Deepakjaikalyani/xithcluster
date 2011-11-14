/**
 * Copyright (c) 2003-2010, Xith3D Project Group all rights reserved.
 * 
 * Portions based on the Java3D interface, Copyright by Sun Microsystems.
 * Many thanks to the developers of Java3D and Sun Microsystems for their
 * innovation and design.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * Neither the name of the 'Xith3D Project Group' nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) A
 * RISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE
 */
package org.xith3d.loop;

import org.jagatoo.util.timing.JavaTimer;
import org.jagatoo.util.timing.TimerInterface;

/**
 * This is the base for any threaded operation. RenderLoop uses it and you can
 * use it in OperationScheduler, etc.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class UpdatingThread implements Runnable, Updatable, GameTimeHost
{
    /**
     * This enum contains constants to control the timing of this thread.
     * 
     * @author Marvin Froehlich (aka Qudus)
     */
    public static enum TimingMode
    {
        MILLISECONDS( 1000.0, 1L, 1000f, 1000000L ),
        MICROSECONDS( 1000000.0, 1000L, 1f, 1000L ),
        NANOSECONDS( 1000000000.0, 1000000L, 1f / 1000f, 1L );
        
        private final double divisor;
        private final long milliDivisor;
        private final long nanoFactor;
        
        private final float floatDivisor;
        private final float floatMilliDivisor;
        private final float floatMicroFactor;
        private final float floatNanoFactor;
        
        /**
         * @return the divisor to divide by to get full seconds.
         */
        public final double getDivisor()
        {
            return ( divisor );
        }
        
        /**
         * @return the divisor to divide nanos by to get millis.
         */
        public final long getMilliDivisor()
        {
            return ( milliDivisor );
        }
        
        /**
         * @return the factor to multiply <code>this</code> by to get nanos.
         */
        public final long getNanoFactor()
        {
            return ( nanoFactor );
        }
        
        /**
         * @return the divisor to divide by to get full seconds.
         */
        public final float getFloatDivisor()
        {
            return ( floatDivisor );
        }
        
        /**
         * @return the divisor to divide nanos by to get millis.
         */
        public final float getFloatMilliDivisor()
        {
            return ( floatMilliDivisor );
        }
        
        /**
         * @return the factor to multiply <code>this</code> by to get micros.
         */
        public final float getFloatMicroFactor()
        {
            return ( floatMicroFactor );
        }
        
        /**
         * @return the factor to multiply <code>this</code> by to get nanos.
         */
        public final float getFloatNanoFactor()
        {
            return ( floatNanoFactor );
        }
        
        /**
         * @param t the input time measured in millis, micros or nanos
         * 
         * @return the corresponding millis
         */
        public final long getMilliSeconds( long t )
        {
            /*
            switch ( this )
            {
                case MILLISECONDS:
                    return ( t );
                case MICROSECONDS:
                    return ( t / 1000L );
                case NANOSECONDS:
                    return ( t / 1000000L );
                default:
                    throw new Error( "This conversion is not yet implemented." );
            }
            */
            
            return ( t / milliDivisor );
        }
        
        /**
         * @param t the input time measured in millis, micros or nanos
         * 
         * @return the corresponding micros
         */
        public final long getMicroSeconds( long t )
        {
            /*
            switch ( this )
            {
                case MILLISECONDS:
                    return ( t * 1000L );
                case MICROSECONDS:
                    return ( t );
                case NANOSECONDS:
                    return ( t / 1000L );
                default:
                    throw new Error( "This conversion is not yet implemented." );
            }
            */
            
            return ( (long)( t * floatMicroFactor ) );
        }
        
        /**
         * @param t the input time measured in millis, micros or nanos
         * 
         * @return the corresponding nanos
         */
        public final long getNanoSeconds( long t )
        {
            /*
            switch ( this )
            {
                case MILLISECONDS:
                    return ( t * 1000000L );
                case MICROSECONDS:
                    return ( t * 1000L );
                case NANOSECONDS:
                    return ( t );
                default:
                    throw new Error( "This conversion is not yet implemented." );
            }
            */
            
            return ( t * nanoFactor );
        }
        
        /**
         * @param t the input time measured in millis, micros or nanos
         * 
         * @return the corresponding seconds as float
         */
        public final float getSecondsAsFloat( long t )
        {
            return ( t / this.getFloatDivisor() );
        }
        
        /**
         * @param t the input time measured in millis
         * 
         * @return the corresponding time units measured in the current TimingMode
         */
        public final long getFromMilliSeconds( long t )
        {
            /*
            switch ( this )
            {
                case MILLISECONDS:
                    return ( t );
                case MICROSECONDS:
                    return ( t * 1000L );
                case NANOSECONDS:
                    return ( t * 1000000L );
                default:
                    throw new Error( "This conversion is not yet implemented." );
            }
            */
            
            return ( t * milliDivisor );
        }
        
        /**
         * @param t the input time measured in micros
         * 
         * @return the corresponding time units measured in the current TimingMode
         */
        public final long getFromMicroSeconds( long t )
        {
            /*
            switch ( this )
            {
                case MILLISECONDS:
                    return ( t / 1000L );
                case MICROSECONDS:
                    return ( t );
                case NANOSECONDS:
                    return ( t * 1000L );
                default:
                    throw new Error( "This conversion is not yet implemented." );
            }
            */
            
            return ( (long)( t / floatMicroFactor ) );
        }
        
        /**
         * @param t the input time measured in nanos
         * 
         * @return the corresponding time units measured in the current TimingMode
         */
        public final long getFromNanoSeconds( long t )
        {
            /*
            switch ( this )
            {
                case MILLISECONDS:
                    return ( t / 1000000L );
                case MICROSECONDS:
                    return ( t / 1000L );
                case NANOSECONDS:
                    return ( t );
                default:
                    throw new Error( "This conversion is not yet implemented." );
            }
            */
            
            return ( t / nanoFactor );
        }
        
        private TimingMode( double divisor, long milliDivisor, float microFactor, long nanoFactor )
        {
            this.divisor = divisor;
            this.milliDivisor = milliDivisor;
            this.nanoFactor = nanoFactor;
            
            this.floatDivisor = (float)divisor;
            this.floatMilliDivisor = milliDivisor;
            this.floatMicroFactor = microFactor;
            this.floatNanoFactor = nanoFactor;
        }
    }
    
    public static final int PAUSE_NONE = 0;
    public static final int PAUSE_TOTAL = 1;
    
    private TimerInterface timer = new JavaTimer();
    private TimingMode timingMode = TimingMode.MICROSECONDS;
    private long t0 = -1L, t02, t03, now;
    private long gameNanoTime = -1L;
    private GameTimeHost gameTimeHost;
    private long iterations = -1L;
    private FPSLimiter fpsLimiter = new DefaultFPSLimiter();
    
    Thread thread = null;
    private boolean isStopping = false;
    
    private long minItTime;
    private long bruttoFrameTime;
    
    private int pauseMode;
    
    /**
     * Sets the timer used by the loop.
     * 
     * @param timer could be an instance of org.xith3d.utility.timing.JavaTimer, e.g.
     */
    public final void setTimer( TimerInterface timer )
    {
        if ( timer == null )
            throw new IllegalArgumentException( "timer MUST NOT be null." );
        
        this.timer = timer;
    }
    
    /**
     * @return the timer used by the loop.
     */
    public final TimerInterface getTimer()
    {
        return ( timer );
    }
    
    /**
     * Sets the minimum time, an iteration must take.
     * 
     * @param minItTime
     */
    protected final void setMinIterationTime( long minItTime )
    {
        this.minItTime = minItTime;
    }
    
    /**
     * @return the minimum time, an iteration must take.
     */
    public final long getMinIterationTime()
    {
        return ( minItTime );
    }
    
    /**
     * Sets the FPSLimiter capable of limiting the FPS/iteration time.
     * 
     * @param fpsLimiter
     */
    public void setFPSLimiter( FPSLimiter fpsLimiter )
    {
        this.fpsLimiter = fpsLimiter;
    }
    
    /**
     * @return the FPSLimiter capable of limiting the FPS/iteration time.
     */
    public final FPSLimiter getFPSLimiter()
    {
        return ( fpsLimiter );
    }
    
    /**
     * @return the count of iterations done by this RenderLoop
     */
    public final long getIterationsCount()
    {
        return ( iterations );
    }
    
    /**
     * Sets the timing mode for the <b>frameTime</b>.
     * 
     * @param mode
     */
    public final void setTimingMode( TimingMode mode )
    {
        this.timingMode = mode;
    }
    
    /**
     * @return the timing mode for the <b>frameTime</b>.
     */
    public final TimingMode getTimingMode()
    {
        if ( gameTimeHost == null )
            return ( timingMode );
        
        return ( gameTimeHost.getTimingMode() );
    }
    
    /**
     * {@inheritDoc}
     */
    public final long getGameNanoTime()
    {
        if ( gameTimeHost == null )
            return ( gameNanoTime );
        
        return ( gameTimeHost.getGameNanoTime() );
    }
    
    /**
     * {@inheritDoc}
     */
    public final long getGameMicroTime()
    {
        return ( getTimingMode().getMicroSeconds( getGameTime() ) );
    }
    
    /**
     * {@inheritDoc}
     */
    public final long getGameMilliTime()
    {
        return ( getTimingMode().getMilliSeconds( getGameTime() ) );
    }
    
    /**
     * {@inheritDoc}
     */
    public final long getGameTime()
    {
        return ( getGameNanoTime() / timingMode.getNanoFactor() );
    }
    
    /**
     * {@inheritDoc}
     */
    public final long getLastNanoFrameTime()
    {
        return ( bruttoFrameTime );
    }
    
    /**
     * {@inheritDoc}
     */
    public final long getLastFrameTime()
    {
        return ( getLastNanoFrameTime() / timingMode.getNanoFactor() );
    }
    
    /**
     * Sets the pauseMode.
     * 
     * @see #PAUSE_NONE
     * @see #PAUSE_TOTAL
     * 
     * @param pauseMode
     */
    public final void setPauseMode( int pauseMode )
    {
        this.pauseMode = pauseMode;
    }
    
    /**
     * @return the pauseMode.
     * 
     * @see #PAUSE_NONE
     * @see #PAUSE_TOTAL
     */
    public final int getPauseMode()
    {
        return ( pauseMode );
    }
    
    /**
     * {@inheritDoc}
     */
    public abstract void update( long gameTime, long frameTime, TimingMode timingMode );
    
    protected long nextIteration( boolean force )
    {
        if ( t0 == -1L )
            t0 = timer.getNanoseconds();
        
        this.gameNanoTime = ( now - t0 );
        final long gameTime = getGameNanoTime();
        t03 = timer.getNanoseconds();
        final long nanoDivisor = getTimingMode().getNanoFactor();
        
        if ( force || ( getPauseMode() != PAUSE_TOTAL ) )
        {
            update( gameTime / nanoDivisor, bruttoFrameTime / nanoDivisor, getTimingMode() );
        }
        
        now = timer.getNanoseconds();
        
        if ( ( getFPSLimiter() != null ) && ( minItTime != 0L ) )
        {
            getFPSLimiter().limitFPS( getIterationsCount(), now - t03, getMinIterationTime(), getTimer() );
        }
        
        now = timer.getNanoseconds();
        bruttoFrameTime = now - t02;
        t02 = now;
        
        iterations++;
        
        return ( bruttoFrameTime );
    }
    
    protected final boolean isStopping()
    {
        return ( isStopping );
    }
    
    /**
     * This method defines the main loop of the Thread.<br>
     * Override it to change its behavior.
     */
    protected void loop()
    {
        while ( !isStopping() )
        {
            nextIteration( false );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void run()
    {
        t0 = timer.getNanoseconds();
        t02 = t0;
        now = t0;
        gameNanoTime = 0L;
        bruttoFrameTime = 0L;
        iterations = 0L;
        isStopping = false;
        
        pauseMode = PAUSE_NONE;
        
        try
        {
            loop();
        }
        catch ( Throwable t )
        {
            t.printStackTrace();
            this.end();
        }
        
        this.thread = null;
    }
    
    /**
     * @return the Thread, that runs this loop.
     */
    public Thread getThread()
    {
        return ( thread );
    }
    
    /**
     * @return true, if this thread is running.
     */
    public boolean isRunning()
    {
        return ( getThread() != null );
    }
    
    protected void begin( boolean startNewThread )
    {
        if ( getThread() == null )
        {
            if ( startNewThread )
            {
                this.thread = new Thread( this );
                
                this.thread.start();
            }
            else
            {
                this.thread = Thread.currentThread();
                
                run();
            }
        }
        else
        {
            throw new IllegalStateException( "This Thread is already running." );
        }
    }
    
    public void end()
    {
        if ( getThread() != null )
        {
            this.isStopping = true;
        }
        else
        {
            throw new IllegalStateException( "This Thread is not running." );
        }
    }
    
    public UpdatingThread( long minItTime, GameTimeHost gameTimeHost )
    {
        this.setMinIterationTime( minItTime );
        this.gameTimeHost = gameTimeHost;
    }
    
    public UpdatingThread( long minItTime )
    {
        this( minItTime, null );
    }
    
    public UpdatingThread( GameTimeHost gameTimeHost )
    {
        this( 0L, gameTimeHost );
    }
    
    public UpdatingThread()
    {
        this( 0L, null );
    }
}
