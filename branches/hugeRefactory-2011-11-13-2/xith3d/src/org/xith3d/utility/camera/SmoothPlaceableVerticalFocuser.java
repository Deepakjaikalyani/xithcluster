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
package org.xith3d.utility.camera;

import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.Tuple3f;
import org.xith3d.loop.Updater;
import org.xith3d.loop.UpdatingThread.TimingMode;
import org.xith3d.physics.util.Placeable;
import org.xith3d.scenegraph.View;

/**
 * Same as {@link BasicPlaceableVerticalFocuser}, but smooth.
 * 
 * @author Amos Wenger (aka BlueSky)
 */
public class SmoothPlaceableVerticalFocuser extends BasicPlaceableVerticalFocuser
{
    private final float alpha;
    private Point3f realPos;
    private Tuple3f offset = null;
    
    /**
     * Set a shifter for this focuser. If non-null, the shifter will modify the position of the
     * camera once it's interpolated, so that you can have variety of views.
     * 
     * @param offset the sifter offset
     */
    public void setOffset( Tuple3f offset )
    {
        this.offset = offset;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void update( long gameTime, long frameTime, TimingMode timingMode )
    {
        if ( realPos == null )
        {
            realPos = new Point3f();
            realPos.set( getFocusPoint() );
        }
        
        Tuple3f pos = getFocusPoint();
        
        realPos.interpolate( pos, alpha );
        
        if ( offset == null )
        {
            lookAt( realPos );
        }
        else
        {
            Point3f tmp = Point3f.fromPool();
            tmp.add( realPos, offset );
            lookAt( tmp );
            Point3f.toPool( tmp );
        }
    }
    
    /**
     * Creates a new {@link SmoothPlaceableVerticalFocuser}.
     * 
     * @param view the view on which to act
     * @param placeable the placeable to focus on
     * @param height the initial height of the Camera
     * @param alpha the interpolation speed, between 0 and 1
     * @param updater an Updater on which to register
     */
    public SmoothPlaceableVerticalFocuser( View view, Placeable placeable, float height, float alpha, Updater updater )
    {
        super( view, placeable, height, updater );
        
        this.alpha = alpha;
    }
}
