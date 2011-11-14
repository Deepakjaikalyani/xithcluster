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

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.openmali.vecmath2.Tuple3f;
import org.xith3d.sound.SoundBuffer;
import org.xith3d.sound.SoundDriver;
import org.xith3d.sound.SoundSource;
import org.xith3d.utility.logging.X3DLog;

/**
 * Sound Driver Implementation for LWJGL OpenAL.
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 * @author Agrv
 */
public class SoundDriverImpl extends SoundDriver
{   
    protected String decodeSoundError( int error )
    {
        switch ( error )
        {
            case AL10.AL_NO_ERROR:
                return ( "NO ERROR" );
            case AL10.AL_INVALID_ENUM:
                return ( "INVALID ENUM" );
            case AL10.AL_INVALID_VALUE:
                return ( "INVALID VALUE" );
            case AL10.AL_INVALID_NAME:
                return ( "INVALID NAME" );
            case AL10.AL_INVALID_OPERATION:
                return ( "INVALID OPERATION" );
            case AL10.AL_OUT_OF_MEMORY:
                return ( "OUT OF MEMORY" );
            default:
                return ( "UNKNOWN ERROR" );
        }
    }
    
    protected void checkError()
    {
        int error = AL10.alGetError();
        if ( error != AL10.AL_NO_ERROR )
            throw new Error( decodeSoundError( error ) );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void startImpl()
    {
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void newFrameSync()
    {
        // step through and remove all the processed buffers
        
        int sourceList[] = new int[ sources.size() ];
        for ( int i = 0; i < sourceList.length; i++ )
        {
            SoundSourceImpl ss = (SoundSourceImpl)sources.get( i );
            sourceList[ i ] = ss.handle;
            ss.stop();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void newFrameAsync()
    {
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setListenerVelocity( Tuple3f velocity )
    {
        AL10.alListener3f( AL10.AL_VELOCITY, velocity.getX(), velocity.getY(), velocity.getZ() );
        checkError();
        
        super.setListenerVelocity( velocity );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setListenerPosition( Tuple3f position )
    {
        AL10.alListener3f( AL10.AL_POSITION, position.getX(), position.getY(), position.getZ());
        checkError();
        
        super.setListenerPosition( position );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setListenerOrientation( Tuple3f direction, Tuple3f up )
    {
        FloatBuffer orientation = BufferUtils.createFloatBuffer(6).put(
            new float[]
            {
                direction.getX(), direction.getY(), direction.getZ(),
                up.getX(), up.getY(), up.getZ()
            });
        orientation.rewind();
        AL10.alListener( AL10.AL_ORIENTATION, orientation);
        
        super.setListenerOrientation( direction, up );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setListenerVolume( float gain )
    {
        AL10.alListenerf( AL10.AL_GAIN, gain );
        checkError();
        
        super.setListenerVolume( gain );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setDopplerVelocity( float velocity )
    {
        AL10.alDopplerVelocity( velocity );
        checkError();
        
        super.setDopplerVelocity( velocity );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setDopplerFactor( float factor )
    {
        AL10.alDopplerFactor( factor );
        
        super.setDopplerFactor( factor );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected SoundSource createSoundSource()
    {
        return ( new SoundSourceImpl( this ) );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public SoundBuffer allocateSoundBuffer()
    {
        return ( new SoundBufferImpl() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean delete( SoundSource source )
    {
        if ( super.delete( source ) )
        {
            ( (SoundSourceImpl)source ).releaseCachedResources();
            
            return ( true );
        }
        
        return ( false );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void shutdownImpl()
    {
        // stop all the sources
        
        int[] sourceList = new int[ sources.size() + availableSources.size() ];
        int i = 0;
        for ( int j = 0; j < sources.size(); j++ )
        {
            SoundSourceImpl ss = (SoundSourceImpl)sources.get( j );
            sourceList[ i++ ] = ss.handle;
            ss.stop();
        }
        
        for ( int j = 0; j < availableSources.size(); j++ )
        {
            SoundSourceImpl ss = (SoundSourceImpl)availableSources.get( j );
            sourceList[ i++ ] = ss.handle;
            ss.stop();
        }
        
        // destroy the sources
        
        if ( sourceList.length > 0 )
        {
            IntBuffer sources = BufferUtils.createIntBuffer(sourceList.length).put(sourceList);
            sources.rewind();
            AL10.alDeleteSources(sources);
            checkError();
        }
        
        // delete all the buffers
        
        int[] bufferList = new int[ buffers.size() ];
        for ( int j = 0; i < bufferList.length; j++ )
        {
            SoundBufferImpl ss = (SoundBufferImpl)buffers.get( j );
            bufferList[ j ] = ss.handle;
        }
        
        if ( bufferList.length > 0 )
        {
            IntBuffer buffers = BufferUtils.createIntBuffer(bufferList.length).put(bufferList);
            buffers.rewind();
            AL10.alDeleteBuffers(buffers);
            checkError();
        }
        
        AL.destroy();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void initSoundDevice()
    {
        try
        {
            AL.create(System.getProperty( "XITH3D_OPENAL_DEVICE" ), 44100, 60, false);
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
        checkError();
        
        AL10.alDistanceModel( AL10.AL_INVERSE_DISTANCE );
        checkError();
    }
    
    public SoundDriverImpl()
    {
        super();
        
        X3DLog.debug( "LWJGL OpenAL sound driver initialized with ", availableSources.size(), " available sources" );
    }
}