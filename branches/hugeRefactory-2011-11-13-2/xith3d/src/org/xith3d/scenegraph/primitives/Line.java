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
package org.xith3d.scenegraph.primitives;

import org.jagatoo.opengl.enums.LinePattern;
import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.Tuple3f;

import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.ColoringAttributes;
import org.xith3d.scenegraph.LineArray;
import org.xith3d.scenegraph.LineAttributes;
import org.xith3d.scenegraph.Shape3D;

/**
 * This primitive <code>Shape</code> represents a simple 3D-line.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class Line extends Shape3D
{
    private float[] coords;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public LineArray getGeometry()
    {
        return ( (LineArray)super.getGeometry() );
    }
    
    /**
     * Sets this <code>Line</code>'s coordinates.
     * 
     * @param coords a 6-element array with starting and ending point coordinates with this format {x0,y0,z0,x1,y1,z1}
     */
    public void setCoordinates( float[] coords )
    {
        this.coords = coords;
        
        getGeometry().setCoordinates( 0, coords );
        setBoundsDirty();
    }
    
    /**
     * Sets this <code>Line</code>'s coordinates.
     * 
     * @param x0 start and end point coordinates
     * @param y0 start and end point coordinates
     * @param z0 start and end point coordinates
     * @param x1 start and end point coordinates
     * @param y1 start and end point coordinates
     * @param z1 start and end point coordinates
     */
    public void setCoordinates( float x0, float y0, float z0, float x1, float y1, float z1 )
    {
        setCoordinates( new float[]
        {
            x0, y0, z0,
            x1, y1, z1
        } );
    }
    
    /**
     * Sets this <code>Line</code>'s coordinates.<br>
     * This sets the starting position to the origin (0, 0, 0).
     * 
     * @param x1 start and end point coordinates
     * @param y1 start and end point coordinates
     * @param z1 start and end point coordinates
     */
    public void setCoordinates( float x1, float y1, float z1 )
    {
        setCoordinates( 0f, 0f, 0f, x1, y1, z1 );
    }
    
    /**
     * Sets this <code>Line</code>'s coordinates.
     * 
     * @param start starting point coordinates
     * @param end ending point coordinates
     */
    public void setCoordinates( Tuple3f start, Tuple3f end )
    {
        setCoordinates( start.getX(), start.getY(), start.getZ(), end.getX(), end.getY(), end.getZ() );
    }
    
    /**
     * Sets this <code>Line</code>'s coordinates.<br>
     * This sets the starting point to the origin (0, 0, 0).
     * 
     * @param end ending point coordinates
     */
    public void setCoordinates( Tuple3f end )
    {
        setCoordinates( Point3f.ZERO, end );
    }
    
    /**
     * Returns this <code>Line</code>'s coordinates.
     * 
     * @return this <code>Line</code>'s coordinates
     */
    public float[] getCoordinates()
    {
        return ( coords );
    }
    
    /**
     * Sets the <code>Line</code>'s color.
     * 
     * @param color the new color to set
     */
    public void setColor( Colorf color )
    {
        getAppearance().getColoringAttributes().setColor( color );
        
        if ( color.hasAlpha() )
            getAppearance().getTransparencyAttributes( true ).setTransparency( color.getAlpha() );
        else
            getAppearance().setTransparencyAttributes( null );
    }
    
    /**
     * Returns the <code>Line</code>'s color.
     * 
     * @return the <code>Line</code>'s color
     */
    public Colorf getColor()
    {
        return ( getAppearance().getColoringAttributes().getColor() );
    }
    
    /**
     * Sets the the pattern for how this line is to be rendered.
     * 
     * @param pattern the new pattern to set
     * 
     * @see LinePattern
     */
    public void setPattern( LinePattern pattern )
    {
        getAppearance().getLineAttributes().setLinePattern( pattern );
    }
    
    /**
     * Returns the <code>Line</code>'s pattern.
     * 
     * @return the <code>Line</code>'s pattern
     * 
     * @see LinePattern
     */
    public LinePattern getPattern()
    {
        return ( getAppearance().getLineAttributes().getLinePattern() );
    }
    
    /**
     * Sets the <code>Line</code>'s width in pixels.
     * 
     * @param width the new line width in pixels
     */
    public void setWidth( float width )
    {
        getAppearance().getLineAttributes().setLineWidth( width );
    }
    
    /**
     * Return the <code>Line</code>'s width in pixels.
     * 
     * @return the <code>Line</code>'s width in pixels
     */
    public float getWidth()
    {
        return ( getAppearance().getLineAttributes().getLineWidth() );
    }
    
    /**
     * Sets the <code>Line</code>'s antialiasing flag.
     * 
     * @param enabled <code>true</code> to turn on antialiasing for this line; <code>false</code> otherwise
     */
    public void setAntialiasingEnabled( boolean enabled )
    {
        getAppearance().getLineAttributes().setLineAntialiasingEnabled( enabled );
    }
    
    /**
     * Returns the current antialiasing flag for this line.
     * 
     * @return <code>true</code> if antialiasing is on for this line; <code>false</code> otherwise
     */
    public boolean isAntialiasingEnabled()
    {
        return ( getAppearance().getLineAttributes().isLineAntialiasingEnabled() );
    }
    
    /**
     * Creates a new <code>Line</code>.
     * 
     * @param start starting point coordinates
     * @param end ending point coordinates
     * @param width this <code>Line</code>'s width in pixels
     * @param antiAliasing flag to indicate whether to turn on antialiasing for this <code>Line</code>
     * @param color this <code>Line</code>'s color
     * @param linePattern the pattern for how this line is to be rendered
     * 
     * @see LinePattern
     */
    public Line( Tuple3f start, Tuple3f end, float width, boolean antiAliasing, Colorf color, LinePattern linePattern )
    {
        super();
        
        final boolean ib = isIgnoreBounds();
        setIgnoreBounds( true );
        setGeometry( new LineArray( 2 ) );
        setIgnoreBounds( ib );
        setCoordinates( start, end );
        updateBounds( true );
        
        setAppearance( new Appearance() );
        
        ColoringAttributes ca = new ColoringAttributes();
        ca.setShadeModel( ColoringAttributes.SHADE_FLAT );
        getAppearance().setColoringAttributes( ca );
        
        setColor( color );
        
        getAppearance().setLineAttributes( new LineAttributes() );
        setWidth( width );
        setPattern( linePattern );
        setAntialiasingEnabled( antiAliasing );
    }
    
    /**
     * Creates a new solid <code>Line</code>.
     * 
     * @param start starting point coordinates
     * @param end ending point coordinates
     * @param width the <code>Line</code>'s width in pixels
     * @param antiAliasing flag to indicate whether to turn on antialiasing for this <code>Line</code>
     * @param color this <code>Line</code>'s color
     */
    public Line( Tuple3f start, Tuple3f end, float width, boolean antiAliasing, Colorf color )
    {
        this( start, end, width, antiAliasing, color, LinePattern.SOLID );
    }
    
    /**
     * Creates a new solid <code>Line</code> without antialiasing.
     * 
     * @param start starting point coordinates
     * @param end ending point coordinates
     * @param width the <code>Line</code>'s width in pixels
     * @param color this <code>Line</code>'s color
     */
    public Line( Tuple3f start, Tuple3f end, float width, Colorf color )
    {
        this( start, end, width, false, color, LinePattern.SOLID );
    }
    
    /**
     * Creates a new <code>Line</code> of 1 pixel width.
     * 
     * @param start starting point coordinates
     * @param end ending point coordinates
     * @param antiAliasing flag to indicate whether to turn on antialiasing for this <code>Line</code>
     * @param color this <code>Line</code>'s color
     * @param linePattern the pattern for how this line is to be rendered
     * 
     * @see LinePattern
     */
    public Line( Tuple3f start, Tuple3f end, boolean antiAliasing, Colorf color, LinePattern linePattern )
    {
        this( start, end, 1.0f, antiAliasing, color, linePattern );
    }
    
    /**
     * Creates a new <code>Line</code> of 1 pixel width without antialiasing.
     * 
     * @param start starting point coordinates
     * @param end ending point coordinates
     * @param color this <code>Line</code>'s color
     * @param linePattern the pattern for how this line is to be rendered
     * 
     * @see LinePattern
     */
    public Line( Tuple3f start, Tuple3f end, Colorf color, LinePattern linePattern )
    {
        this( start, end, 1.0f, false, color, linePattern );
    }
    
    /**
     * Creates a new solid <code>Line</code> of 1 pixel width.
     * 
     * @param start starting point coordinates
     * @param end ending point coordinates
     * @param antiAliasing flag to indicate whether to turn on antialiasing for this <code>Line</code>
     * @param color this <code>Line</code>'s color
     */
    public Line( Tuple3f start, Tuple3f end, boolean antiAliasing, Colorf color )
    {
        this( start, end, 1.0f, antiAliasing, color, LinePattern.SOLID );
    }
    
    /**
     * Creates a new solid <code>Line</code> of 1 pixel width without antialiasing.
     * 
     * @param start starting point coordinates
     * @param end ending point coordinates
     * @param color this <code>Line</code>'s color
     */
    public Line( Tuple3f start, Tuple3f end, Colorf color )
    {
        this( start, end, 1.0f, false, color, LinePattern.SOLID );
    }
    
    /**
     * Creates a new <code>Line</code> starting at the origin (0, 0, 0).
     * 
     * @param end ending point coordinates
     * @param width the <code>Line</code>'s width in pixels
     * @param antiAliasing flag to indicate whether to turn on antialiasing for this <code>Line</code>
     * @param color this <code>Line</code>'s color
     * @param linePattern the pattern for how this line is to be rendered
     * 
     * @see LinePattern
     */
    public Line( Tuple3f end, float width, boolean antiAliasing, Colorf color, LinePattern linePattern )
    {
        this( new Point3f( 0.0f, 0.0f, 0.0f ), end, width, antiAliasing, color, linePattern );
    }
    
    /**
     * Creates a new <code>Line</code> starting at the origin (0, 0, 0) without antialiasing.
     * 
     * @param end ending point coordinates
     * @param width the <code>Line</code>'s width in pixels
     * @param color this <code>Line</code>'s color
     * @param linePattern the pattern for how this line is to be rendered
     * 
     * @see LinePattern
     */
    public Line( Tuple3f end, float width, Colorf color, LinePattern linePattern )
    {
        this( new Point3f( 0.0f, 0.0f, 0.0f ), end, width, false, color, linePattern );
    }
    
    /**
     * Creates a new solid <code>Line</code> starting at the origin (0, 0, 0).
     * 
     * @param end ending point coordinates
     * @param width the <code>Line</code>'s width in pixels
     * @param antiAliasing flag to indicate whether to turn on antialiasing for this <code>Line</code>
     * @param color this <code>Line</code>'s color
     */
    public Line( Tuple3f end, float width, boolean antiAliasing, Colorf color )
    {
        this( new Point3f( 0.0f, 0.0f, 0.0f ), end, width, antiAliasing, color, LinePattern.SOLID );
    }
    
    /**
     * Creates a new solid <code>Line</code> starting at the origin (0, 0, 0) without antialiasing.
     * 
     * @param end ending point coordinates
     * @param width the <code>Line</code>'s width in pixels
     * @param color this <code>Line</code>'s color
     */
    public Line( Tuple3f end, float width, Colorf color )
    {
        this( new Point3f( 0.0f, 0.0f, 0.0f ), end, width, false, color, LinePattern.SOLID );
    }
    
    /**
     * Creates a new <code>Line</code> of 1 pixel width starting at the origin (0, 0, 0).
     * 
     * @param end ending point coordinates
     * @param antiAliasing flag to indicate whether to turn on antialiasing for this <code>Line</code>
     * @param color this <code>Line</code>'s color
     * @param linePattern the pattern for how this line is to be rendered
     * 
     * @see LinePattern
     */
    public Line( Tuple3f end, boolean antiAliasing, Colorf color, LinePattern linePattern )
    {
        this( new Point3f( 0.0f, 0.0f, 0.0f ), end, 1.0f, antiAliasing, color, linePattern );
    }
    
    /**
     * Creates a new <code>Line</code> of 1 pixel width starting at the origin (0, 0, 0) without antialiasing.
     * 
     * @param end ending point coordinates
     * @param color this <code>Line</code>'s color
     * @param linePattern the pattern for how this line is to be rendered
     * 
     * @see LinePattern
     */
    public Line( Tuple3f end, Colorf color, LinePattern linePattern )
    {
        this( new Point3f( 0.0f, 0.0f, 0.0f ), end, 1.0f, false, color, linePattern );
    }
    
    /**
     * Creates a new solid <code>Line</code> of 1 pixel width starting at the origin (0, 0, 0).
     * 
     * @param end ending point coordinates
     * @param antiAliasing flag to indicate whether to turn on antialiasing for this <code>Line</code>
     * @param color this <code>Line</code>'s color
     */
    public Line( Tuple3f end, boolean antiAliasing, Colorf color )
    {
        this( new Point3f( 0.0f, 0.0f, 0.0f ), end, 1.0f, antiAliasing, color, LinePattern.SOLID );
    }
    
    /**
     * Creates a new solid <code>Line</code> of 1 pixel width starting at the origin (0, 0, 0) without antialiasing.
     * 
     * @param end ending point coordinates
     * @param color this <code>Line</code>'s color
     */
    public Line( Tuple3f end, Colorf color )
    {
        this( new Point3f( 0.0f, 0.0f, 0.0f ), end, 1.0f, false, color, LinePattern.SOLID );
    }
}
