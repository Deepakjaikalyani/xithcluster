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
package org.xith3d.utility.input;

import org.jagatoo.input.InputSystem;
import org.jagatoo.input.devices.Keyboard;
import org.jagatoo.input.devices.components.Key;
import org.jagatoo.input.devices.components.Keys;
import org.xith3d.loop.UpdatingThread.TimingMode;
import org.xith3d.loop.opscheduler.OperationScheduler;
import org.xith3d.loop.opscheduler.impl.ScheduledOperationImpl;

/**
 * A float value adjustable by keyboard events.
 * You can adjust the keys which are used
 * to increase/decrease the value.
 * 
 * @author Amos Wenger (aka BlueSky)
 */
public class KeyAdjustableFloat extends ScheduledOperationImpl
{
    private final Keyboard keyboard;
    
    private final Key vkDown;
    private final Key vkUp;
    private final Key vkDisp;
    
    private final float speed;
    private float value;
    
    public void setValue( float value )
    {
        this.value = value;
    }
    
    public void setValue( Float value )
    {
        this.value = value;
    }
    
    public final float getValue()
    {
        return ( value );
    }
    
    /**
     * {@inheritDoc}
     */
    public void update( long gameTime, long frameTime, TimingMode timingMode )
    {
        float diff = timingMode.getSecondsAsFloat( frameTime ) * speed;
        
        if ( keyboard.isKeyPressed( vkDown ) )
        {
            value -= diff;
        }
        
        if ( keyboard.isKeyPressed( vkUp ) )
        {
            value += diff;
        }
        
        if ( keyboard.isKeyPressed( vkDisp ) )
        {
            System.out.println( "Value = " + value );
        }
    }
    
    /**
     * New KeyAdjustableFloat
     * 
     * @param vkDown
     * @param vkUp
     * @param vkDisp
     * @param speed
     * @param opSched
     */
    public KeyAdjustableFloat( Key vkDown, Key vkUp, Key vkDisp, float speed, OperationScheduler opSched )
    {
        super( true );
        
        if ( !InputSystem.hasInstance() )
        {
            throw new Error( "No InputSystem registered." );
        }
        
        this.keyboard = InputSystem.getInstance().getKeyboard();
        
        if ( keyboard == null )
        {
            throw new Error( "No Keyboard registered at the InputSystem." );
        }
        
        this.vkDown = vkDown;
        this.vkUp = vkUp;
        this.vkDisp = vkDisp;
        this.speed = speed;
        
        opSched.scheduleOperation( this );
    }
    
    /**
     * New KeyAdjustableFloat
     * 
     * @param vkDown
     * @param vkUp
     * @param speed
     * @param opSched
     */
    public KeyAdjustableFloat( Key vkDown, Key vkUp, float speed, OperationScheduler opSched )
    {
        this( vkDown, vkUp, Keys.ESCAPE, speed, opSched );
    }
}
