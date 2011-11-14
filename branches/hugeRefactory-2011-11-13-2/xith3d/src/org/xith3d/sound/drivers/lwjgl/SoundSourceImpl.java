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
package org.xith3d.sound.drivers.lwjgl;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import org.openmali.vecmath2.Tuple3f;
import org.xith3d.sound.SoundBuffer;
import org.xith3d.sound.SoundContainer;
import org.xith3d.sound.SoundDriver;
import org.xith3d.sound.SoundSource;
import org.xith3d.sound.SoundState;
import org.xith3d.utility.logging.X3DLog;

/**
 * LWJGL OpenAL implementation of SoundSource.
 * 
 * @author David Yazel
 * @author Agrv
 */
public class SoundSourceImpl implements SoundSource
{
    static int count = 0;
    int handle;
    
    private SoundDriverImpl driver;
    
    public SoundSourceImpl( SoundDriverImpl driver )
    {
        this.driver = driver;
        IntBuffer buffer = BufferUtils.createIntBuffer(1);
        int error = AL10.alGetError();
        AL10.alGenSources(buffer);
        error = AL10.alGetError();
        if ( error != AL10.AL_NO_ERROR )
            throw new Error( "no more sources available : " + driver.decodeSoundError( error ) );
        handle = buffer.get(0);
        X3DLog.debug( "Created new source.. handle = ", handle );
        setRolloffFactor( 0.5f );
        setMaxDistance( 30 );
        setReferenceDistance( 5 );
        setMinVolume( 0 );
        setMaxVolume( 1 );
    }
    
    public void queueBuffer( SoundBuffer buffer )
    {
        IntBuffer intbuf = BufferUtils.createIntBuffer(1).put(((SoundBufferImpl)buffer ).handle);
        intbuf.rewind();
        AL10.alSourceQueueBuffers( handle, intbuf);
        driver.checkError();
    }
    
    public int[] unqueueProcessedBuffers()
    {
        int num = AL10.alGetSourcei( handle, AL10.AL_BUFFERS_PROCESSED );
        IntBuffer buffers = BufferUtils.createIntBuffer(num);
        AL10.alSourceUnqueueBuffers( handle, buffers);
        driver.checkError();
        
        return ( buffers.array() );        
    }
    
    public void setBuffer( SoundBuffer buffer )
    {
        AL10.alSourcei( handle, AL10.AL_BUFFER, ( (SoundBufferImpl)buffer ).handle );
        driver.checkError();
    }
    
    public void setContainer( SoundContainer container )
    {
        SoundBuffer b = container.getData( driver );
        if ( b != null )
            setBuffer( b );
    }
    
    public void setVolume( float gain )
    {
        AL10.alSourcef( handle, AL10.AL_GAIN, gain );
        driver.checkError();
    }
    
    public void play()
    {
        AL10.alSourcePlay( handle );
        driver.checkError();
    }
    
    public void pause()
    {
        AL10.alSourcePause( handle );
        driver.checkError();
    }
    
    public void rewind()
    {
        AL10.alSourceRewind( handle );
        driver.checkError();
    }
    
    public void stop()
    {
        AL10.alSourceStop( handle );
        driver.checkError();
    }
    
    public boolean isPlaying()
    {
        return ( getState() == SoundState.PLAYING );
    }
    
    private float[] pos = new float[ 3 ];
    private boolean hasCachedPosition = false;
    
    void releaseCachedResources()
    {
        hasCachedPosition = false;
    }
    
    public void setPosition( float posX, float posY, float posZ )
    {
        if ( hasCachedPosition )
        {
            if ( ( pos[ 0 ] == posX ) && ( pos[ 1 ] == posY ) && ( pos[ 2 ] == posZ ) )
                return;
            
            hasCachedPosition = true;
        }
        
        pos[ 0 ] = posX;
        pos[ 1 ] = posY;
        pos[ 2 ] = posZ;
        AL10.alSource3f( handle, AL10.AL_POSITION, posX, posY, posZ);
        driver.checkError();
        //if (((++count) % 200) == 0) System.out.println( "Position for source" + handle + " is " + position );
    }
    
    public void setPosition( Tuple3f position )
    {
        setPosition( position.getX(), position.getY(), position.getZ() );
    }
    
    public void setVelocity( float veloX, float veloY, float veloZ )
    {
        AL10.alSource3f( handle, AL10.AL_VELOCITY, veloX, veloY, veloZ );
        driver.checkError();
    }
    
    public void setVelocity( Tuple3f velocity )
    {
        setVelocity( velocity.getX(), velocity.getY(), velocity.getZ() );
    }
    
    public void setDirection( float dirX, float dirY, float dirZ )
    {
    }
    
    public void setDirection( Tuple3f direction )
    {
    }
    
    public SoundState getState()
    {
        int state = AL10.alGetSourcei( handle, AL10.AL_SOURCE_STATE);
        switch ( state )
        {
            case AL10.AL_INITIAL:
                return ( SoundState.INITIAL  );
            case AL10.AL_PLAYING:
                return ( SoundState.PLAYING  );
            case AL10.AL_PAUSED:
                return ( SoundState.PAUSED  );
            case AL10.AL_STOPPED:
                return ( SoundState.STOPPED  );
            default:
                throw new Error( "Illegal OpenAL state found" );
        }
    }
    
    public void setReferenceDistance( float refDistance )
    {
        AL10.alSourcef( handle, AL10.AL_REFERENCE_DISTANCE, refDistance );
        driver.checkError();
    }
    
    /**
     * Set to zero if this is an unattenuated sound, 1 would be normal otherwise
     * 
     * @param factor
     */
    public void setRolloffFactor( float factor )
    {
        AL10.alSourcef( handle, AL10.AL_ROLLOFF_FACTOR, factor );
        driver.checkError();
    }
    
    public void setRelative( boolean relative )
    {
        AL10.alSourcei( handle, AL10.AL_SOURCE_RELATIVE, relative ? 1 : 0 );
        driver.checkError();
    }
    
    public void setLoop( boolean loop )
    {
        AL10.alSourcei( handle, AL10.AL_LOOPING, loop ? 1 : 0 );
    }
    
    public void setMaxVolume( float maxVolume )
    {
        AL10.alSourcef( handle, AL10.AL_MAX_GAIN, maxVolume );
        driver.checkError();
    }
    
    public void setMinVolume( float minVolume )
    {
        AL10.alSourcef( handle, AL10.AL_MIN_GAIN, minVolume );
        driver.checkError();
    }
    
    public void setMaxDistance( float maxDistance )
    {
        AL10.alSourcef( handle, AL10.AL_MAX_DISTANCE, maxDistance );
        driver.checkError();
    }
    
    public void setMaxTime( long ms )
    {
    }
    
    public SoundDriver getSoundDriver()
    {
        return ( driver );
    }
}
