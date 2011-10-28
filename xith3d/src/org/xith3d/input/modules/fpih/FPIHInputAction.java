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
package org.xith3d.input.modules.fpih;

import org.jagatoo.input.actions.LabeledInputAction;

/**
 * Basic key commands, that can be bound with specific keys.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class FPIHInputAction implements LabeledInputAction
{
    private static FPIHInputAction[] values = new FPIHInputAction[ 0 ];
    
    private static final void addToValues( FPIHInputAction action )
    {
        if ( ( values == null ) || ( values.length == 0 ) )
        {
            values = new FPIHInputAction[] { action };
        }
        else
        {
            FPIHInputAction[] tmp = new FPIHInputAction[ values.length + 1 ];
            System.arraycopy( values, 0, tmp, 0, values.length );
            tmp[values.length] = action;
            values = tmp;
        }
    }
    
    public static final FPIHInputAction[] values()
    {
        FPIHInputAction[] result = new FPIHInputAction[ values.length ];
        System.arraycopy( values, 0, result, 0, values.length );
        
        return ( result );
    }
    
    public static final FPIHInputAction TURN_LEFT = new FPIHInputAction( "Turn Left", false, false );
    public static final FPIHInputAction TURN_RIGHT = new FPIHInputAction( "Turn Right", false, false );
    public static final FPIHInputAction AIM_UP = new FPIHInputAction( "Aim Up", false, false );
    public static final FPIHInputAction AIM_DOWN = new FPIHInputAction( "Aim Down", false, false );
    public static final FPIHInputAction WALK_FORWARD = new FPIHInputAction( "Walk Forward", true, true );
    public static final FPIHInputAction WALK_BACKWARD = new FPIHInputAction( "Walk Backward", true, true );
    public static final FPIHInputAction STRAFE_LEFT = new FPIHInputAction( "Strave Left", true, true );
    public static final FPIHInputAction STRAFE_RIGHT = new FPIHInputAction( "Strave Right", true, true );
    public static final FPIHInputAction JUMP = new FPIHInputAction( "Jump", false, true );
    public static final FPIHInputAction CROUCH = new FPIHInputAction( "Crouch", false, true );
    public static final FPIHInputAction ZOOM_IN = new FPIHInputAction( "Zoom In", false, false );
    public static final FPIHInputAction ZOOM_OUT = new FPIHInputAction( "Zoom Out", false, false );
    public static final FPIHInputAction DISCRETE_ZOOM_IN = new FPIHInputAction( "Discrete Zoom In", false, false );
    public static final FPIHInputAction DISCRETE_ZOOM_OUT = new FPIHInputAction( "Discrete Zoom Out", false, false );
    
    private static int nextOrdinal = 0;
    
    private final int ordinal = nextOrdinal++;
    private final String text;
    private final boolean isRepositioning;
    private final boolean isMovement;
    
    /**
     * {@inheritDoc}
     */
    public final int ordinal()
    {
        return ( ordinal );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return ( text );
    }
    
    /**
     * {@inheritDoc}
     */
    public final String getText()
    {
        return ( text );
    }
    
    /**
     * {@inheritDoc}
     */
    public String getLocalizedText()
    {
        return ( getText() );
    }
    
    /**
     * Does this action modify the camera'a position?
     * 
     * @return modifying camera position?
     */
    public final boolean isRepositioning()
    {
        return ( isRepositioning );
    }
    
    /**
     * Is this action one of the walking actions?
     * 
     * @return walking action?
     */
    public final boolean isMovement()
    {
        return ( isMovement );
    }
    
    /**
     * Creates a new {@link FPIHInputAction}.
     * 
     * @param text the text label for bindings manager GUIs
     * @param isRepositioning Does this action modify the camera'a position?
     * @param isMovement Walking Action?
     */
    protected FPIHInputAction( final String text, boolean isRepositioning, boolean isMovement )
    {
        this.text = text;
        this.isRepositioning = isRepositioning;
        this.isMovement = isMovement;
        
        addToValues( this );
    }
}
