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
package org.xith3d.sound;

import java.util.ArrayList;

import org.openmali.vecmath2.Tuple3f;
import org.xith3d.utility.logging.X3DLog;

/**
 * Implementations of a sound driver must implement this interface. This
 * represents the various capabilities of the sound system.
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class SoundDriver
{
    protected final ArrayList<SoundSource> sources = new ArrayList<SoundSource>();
    protected final ArrayList<SoundSource> availableSources = new ArrayList<SoundSource>();
    protected final ArrayList<SoundBuffer> buffers = new ArrayList<SoundBuffer>();
    
    private float listenerVolume = 1.0f;
    private float dopplerVelocity = 1.0f;
    private float dopplerFactor = 0.0f;
    
    private boolean online = false;
    
    protected void setOnline( boolean online )
    {
        this.online = online;
    }
    
    public final boolean isOnline()
    {
        return ( online );
    }
    
    protected abstract void startImpl();
    
    public final void start()
    {
        startImpl();
        
        setOnline( true );
    }
    
    /**
     * Call this method once a frame to check and possibly load the next buffer
     * from all the streaming sources, as well as dequeue all processed buffers.
     * This will operate synchronously, so it will not return until the work is
     * complete.
     */
    public abstract void newFrameSync();
    
    /**
     * Call this method once a frame to check and possibly load the next buffer
     * from all the streaming sources, as well as dequeue all processed buffers.
     * This will operate asynchronously and will return immediately. If it is
     * already processing from the last frame then it will skip this frame. The
     * thread used is a high priority thread so that it can complete its task in
     * as little time as possible while still reducing frame stutter. This is
     * because this is mostly I/O bound and will enter wait states, thus freeing
     * CPU for rendering.
     */
    public abstract void newFrameAsync();
    
    /**
     * 
     * @param velocity
     */
    public void setListenerVelocity( Tuple3f velocity )
    {
    }
    
    /**
     * 
     * @param position
     */
    public void setListenerPosition( Tuple3f position )
    {
    }
    
    /**
     * 
     * @param direction
     * @param up
     */
    public void setListenerOrientation( Tuple3f direction, Tuple3f up )
    {
    }
    
    public void setListenerVolume( float gain )
    {
        this.listenerVolume = gain;
    }
    
    public final float getListenerVolume()
    {
        return ( listenerVolume );
    }
    
    public void setDopplerVelocity( float velocity )
    {
        this.dopplerVelocity = velocity;
    }
    
    public final float getDopplerVelocity()
    {
        return ( dopplerVelocity );
    }
    
    public void setDopplerFactor( float factor )
    {
        this.dopplerFactor = factor;
    }
    
    public final float getDopplerFactor()
    {
        return ( dopplerFactor );
    }
    
    protected abstract SoundSource createSoundSource();
    
    public SoundSource allocateSoundSource() throws SoundException
    {
        if ( availableSources.size() == 0 )
            throw new SoundException( "no sound sources available" );
        
        SoundSource s = availableSources.remove( availableSources.size() - 1 );
        sources.add( s );
        
        return ( s );
    }
    
    public abstract SoundBuffer allocateSoundBuffer();
    
    /**
     * 
     * @param source
     * @return <code>true</code>, if the source was part of this driver, <code>false</code> otherwise.
     */
    public boolean delete( SoundSource source )
    {
        if ( sources.remove( source ) )
        {
            availableSources.add( source );
            
            return ( true );
        }
        
        return ( false );
    }
    
    /**
     * 
     * @param buffer
     * @return <code>true</code>, if the buffer was part of this driver, <code>false</code> otherwise.
     */
    public boolean delete( SoundBuffer buffer )
    {
        // TODO
        
        return ( false );
    }
    
    public final int getNumAvailableSources()
    {
        return ( availableSources.size() );
    }
    
    public final int getNumActiveSources()
    {
        return ( sources.size() );
    }
    
    public final int getNumSources()
    {
        return ( sources.size() + availableSources.size() );
    }
    
    protected abstract void shutdownImpl();
    
    public final void shutdown()
    {
        if ( !isOnline() )
            return;
        
        shutdownImpl();
        
        setOnline( false );
    }
    
    private void allocateAvailableSources()
    {
        while ( availableSources.size() < 60 )
        {
            try
            {
                availableSources.add( createSoundSource() );
            }
            catch ( Error e )
            {
                X3DLog.print( e );
                break;
            }
        }
    }
    
    protected abstract void initSoundDevice();
    
    protected SoundDriver()
    {
        initSoundDevice();
        
        allocateAvailableSources();
        
        setListenerVolume( 1.0f );
        setDopplerVelocity( 1.0f );
        setDopplerFactor( 0.0f );
    }
}
