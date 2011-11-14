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
package org.xith3d.ui.hud.widgets;

import org.openmali.types.twodee.Dim2i;
import org.openmali.types.twodee.Rect2i;
import org.openmali.vecmath2.Tuple2i;
import org.xith3d.input.FirstPersonInputHandler;
import org.xith3d.render.RenderPass;
import org.xith3d.render.RenderPassConfig;
import org.xith3d.scenegraph.Texture2DCanvas;
import org.xith3d.ui.hud.HUD;
import org.xith3d.ui.hud.base.Widget;
import org.xith3d.ui.hud.base.Window;
import org.xith3d.ui.hud.listeners.WidgetLocationAdapter;
import org.xith3d.ui.hud.listeners.WidgetLocationListener;

/**
 * <p>
 * The {@link Widget3D} is connected to a {@link RenderPass} and controls its
 * Viewport, so that it always covers the Widgets (inner) area.
 * </p>
 * <p>
 * Note, that it doesn't care about the z-order (z-index). The {@link RenderPass}
 * is rendered in the order, that is defined by the {@link RenderPass}es' list.
 * </p>
 * <p>
 * If you're using a {@link FirstPersonInputHandler}, you should apply
 * a view-transform (see {@link RenderPassConfig#setViewTransform(org.xith3d.scenegraph.Transform3D)})
 * to the RenderPassConfig, so that the {@link FirstPersonInputHandler} doesn't
 * affect the &quot;nested&quot; RenderPass.
 * </p>
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class Widget3D extends Widget
{
    private final RenderPassConfig rpConfig;
    private final WidgetLocationListener windowLocationListener;
    
    /**
     * @return the attached {@link RenderPassConfig}.
     */
    public final RenderPassConfig getRenderPassConfig()
    {
        return ( rpConfig );
    }
    
    protected void applyLocationToRenderPass( float x, float y )
    {
        final Tuple2i loc2 = Tuple2i.fromPool();
        
        if ( getBorder() != null )
        {
            x += getBorder().getLeftWidth();
            y += getBorder().getTopHeight();
        }
        
        getLocationHUD2Pixels_( x, y, loc2 );
        
        Rect2i viewport = rpConfig.getViewport();
        if ( viewport == null )
        {
            viewport = new Rect2i();
            rpConfig.setViewport( viewport );
        }
        
        viewport.setLocation( loc2.getX(), loc2.getY() );
        
        Tuple2i.toPool( loc2 );
    }
    
    protected void applySizeToRenderPass( float w, float h )
    {
        final Dim2i size2 = Dim2i.fromPool();
        
        if ( getBorder() != null )
        {
            w -= getBorder().getLeftWidth() + getBorder().getRightWidth();
            h -= getBorder().getTopHeight() + getBorder().getBottomHeight();
        }
        
        getSizeHUD2Pixels_( w, h, size2 );
        
        Rect2i viewport = rpConfig.getViewport();
        if ( viewport == null )
        {
            viewport = new Rect2i();
            rpConfig.setViewport( viewport );
        }
        
        viewport.setSize( size2.getWidth(), size2.getHeight() );
        
        Dim2i.toPool( size2 );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onAttachedToHUD( HUD hud )
    {
        super.onAttachedToHUD( hud );
        
        applyLocationToRenderPass( getLeft(), getTop() );
        applySizeToRenderPass( getWidth(), getHeight() );
        
        Window w = getParentWindow();
        if ( w != null )
        {
            w.addLocationListener( windowLocationListener );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDetachedFromHUD( HUD hud )
    {
        super.onDetachedFromHUD( hud );
        
        Window w = getParentWindow();
        if ( w != null )
        {
            w.addLocationListener( windowLocationListener );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onLocationChanged( float oldLeft, float oldTop, float newLeft, float newTop )
    {
        super.onLocationChanged( oldLeft, oldTop, newLeft, newTop );
        
        if ( getHUD() != null )
        {
            applyLocationToRenderPass( newLeft, newTop );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onSizeChanged( float oldWidth, float oldHeight, float newWidth, float newHeight )
    {
        super.onSizeChanged( oldWidth, oldHeight, newWidth, newHeight );
        
        if ( getHUD() != null )
        {
            applySizeToRenderPass( newWidth, newHeight );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void drawWidget( Texture2DCanvas texCanvas, int offsetX, int offsetY, int width, int height, boolean drawsSelf )
    {
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void init()
    {
    }
    
    /**
     * Creates a new Widget3D.
     * 
     * @param width
     * @param height
     * @param rpConfig
     */
    public Widget3D( float width, float height, RenderPassConfig rpConfig )
    {
        super( false, false, width, height );
        
        this.rpConfig = rpConfig;
        
        this.windowLocationListener = new WidgetLocationAdapter()
        {
            @Override
            public void onWidgetLocationChanged( Widget widget, float oldLeft, float oldTop, float newLeft, float newTop )
            {
                applyLocationToRenderPass( getLeft(), getTop() );
            }
        };
    }
}
