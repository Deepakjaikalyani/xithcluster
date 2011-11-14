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
package org.xith3d.render.preprocessing;

import org.xith3d.render.OpenGLCapabilities;
import org.xith3d.render.states.StateUnit;
import org.xith3d.render.states.units.ColoringStateUnit;
import org.xith3d.render.states.units.LineAttribsStateUnit;
import org.xith3d.render.states.units.MaterialStateUnit;
import org.xith3d.render.states.units.PointAttribsStateUnit;
import org.xith3d.render.states.units.PolygonAttribsStateUnit;
import org.xith3d.render.states.units.RenderingAttribsStateUnit;
import org.xith3d.render.states.units.ShaderProgramStateUnit;
import org.xith3d.render.states.units.TextureUnitStateUnit;
import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.ColoringAttributes;
import org.xith3d.scenegraph.RenderingAttributes;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.TexCoordGeneration;
import org.xith3d.scenegraph.Texture;
import org.xith3d.scenegraph.TextureAttributes;
import org.xith3d.scenegraph.TextureUnit;
import org.xith3d.scenegraph.TransparencyAttributes;
import org.xith3d.scenegraph._SG_PrivilegedAccess;

/**
 * Atom for rendering a single Shape3D along with its geometry arrays.
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 * @author Florian Hofmann (aka Goliat) GLSL Shader support
 */
public class ShapeAtom extends ShadowAtom
{
    public static final int STATE_TYPE = 0;
    public static final Appearance DEFAULT_APPEARANCE = new Appearance();
    private static RenderingAttributes transparentRenderAttribs = null;
    private final StateUnit[] stateUnitCache = new StateUnit[ StateUnit.MAX_STATE_TYPES ];
    
    private int numValidTUs = 0;
    
    public int lastComputedPolysCount = 0;
    
    /**
     * 
     * @param shape
     * @param glCaps
     */
    public ShapeAtom( Shape3D shape, OpenGLCapabilities glCaps )
    {
        super( ShapeAtom.STATE_TYPE, shape );
        
        // make sure view state shaders are first
        // now set all the materials
        
        Appearance a = shape.getAppearance();
        if ( a == null )
            a = DEFAULT_APPEARANCE;
        
        updateStateUnit( MaterialStateUnit.makeMaterialStateUnit( a.getMaterial(), stateUnitCache ) );
        getStateUnit( MaterialStateUnit.STATE_TYPE ).updateCachedStateId();
        updateStateUnit( PolygonAttribsStateUnit.makePolygonAttribsStateUnit( a.getPolygonAttributes(), stateUnitCache ) );
        getStateUnit( PolygonAttribsStateUnit.STATE_TYPE ).updateCachedStateId();
        updateStateUnit( LineAttribsStateUnit.makeLineAttribsStateUnit( a.getLineAttributes(), stateUnitCache ) );
        getStateUnit( LineAttribsStateUnit.STATE_TYPE ).updateCachedStateId();
        updateStateUnit( PointAttribsStateUnit.makePointAttribsStateUnit( a.getPointAttributes(), stateUnitCache ) );
        getStateUnit( PointAttribsStateUnit.STATE_TYPE ).updateCachedStateId();
        updateStateUnit( ColoringStateUnit.makeColoringStateUnit( a.getColoringAttributes(), a.getTransparencyAttributes(), stateUnitCache ) );
        getStateUnit( ColoringStateUnit.STATE_TYPE ).updateCachedStateId();
        
        if ( ( a.getRenderingAttributes() == null ) && ( a.getTransparencyAttributes() != null ) )
        {
            if ( transparentRenderAttribs == null )
            {
                transparentRenderAttribs = new RenderingAttributes( true, true, 0.0f, RenderingAttributes.GREATER );
            }
            
            updateStateUnit( RenderingAttribsStateUnit.makeRenderingStateUnit( transparentRenderAttribs, stateUnitCache ) );
        }
        else
        {
            updateStateUnit( RenderingAttribsStateUnit.makeRenderingStateUnit( a.getRenderingAttributes(), stateUnitCache ) );
        }
        getStateUnit( RenderingAttribsStateUnit.STATE_TYPE ).updateCachedStateId();
        
        updateStateUnit( ShaderProgramStateUnit.makeShaderProgramStateUnit( a.getShaderProgramContext(), stateUnitCache ) );
        getStateUnit( ShaderProgramStateUnit.STATE_TYPE ).updateCachedStateId();
        
        this.numValidTUs = a.getTextureUnitsCount();
        for ( int i = 0; i < numValidTUs; i++ )
        {
            updateStateUnit( TextureUnitStateUnit.makeTextureUnitStateUnit( i, a.getTextureUnit( i ), stateUnitCache ) );
            getStateUnit( TextureUnitStateUnit.STATE_TYPES[ i ] ).updateCachedStateId();
        }
        for ( int i = numValidTUs; i < TextureUnitStateUnit.DEFAULT_UNIT.length; i++ )
        {
            updateStateUnit( TextureUnitStateUnit.DEFAULT_UNIT[ i ] );
            getStateUnit( TextureUnitStateUnit.STATE_TYPES[ i ] ).updateCachedStateId();
        }
    }
    
    /**
     * 
     * @param app
     * @param glCaps
     */
    public void updateStateUnits( Appearance app, OpenGLCapabilities glCaps )
    {
        // This is already checked before the call to the method now!
        /*
        if ( app.isStatic() && !_SG_PrivilegedAccess.isStaticDirty( app ) )
        {
            return;
        }
        */
        
        // Reset translucent flag, so that the updateStateUnit() methods can set it properly.
        translucent = false;
        
        updateLightsAndFogs();
        
        // check and update Material StateUnit
        MaterialStateUnit materialUnit = (MaterialStateUnit)getStateUnit( MaterialStateUnit.STATE_TYPE );
        if ( materialUnit.isDefault() )
        {
            if ( app.getMaterial() != null )
            {
                updateStateUnit( materialUnit = MaterialStateUnit.makeMaterialStateUnit( app.getMaterial(), stateUnitCache ) );
                
                _SG_PrivilegedAccess.setChanged( app.getMaterial(), false );
            }
        }
        else if ( app.getMaterial() == null )
        {
            updateStateUnit( materialUnit = MaterialStateUnit.makeMaterialStateUnit( null, null ) );
        }
        else if ( app.getMaterial().isChanged() )
        {
            materialUnit.update( app.getMaterial() );
            updateStateMap( materialUnit );
            
            _SG_PrivilegedAccess.setChanged( app.getMaterial(), false );
        }
        materialUnit.updateCachedStateId();
        translucent = translucent || materialUnit.isTranslucent();
        
        // check and update PolygonAttributes StateUnit
        PolygonAttribsStateUnit polygonAttrUnit = (PolygonAttribsStateUnit)getStateUnit( PolygonAttribsStateUnit.STATE_TYPE );
        if ( polygonAttrUnit.isDefault() )
        {
            if ( app.getPolygonAttributes() != null )
            {
                updateStateUnit( polygonAttrUnit = PolygonAttribsStateUnit.makePolygonAttribsStateUnit( app.getPolygonAttributes(), stateUnitCache ) );
                
                _SG_PrivilegedAccess.setChanged( app.getPolygonAttributes(), false );
            }
        }
        else if ( app.getPolygonAttributes() == null )
        {
            updateStateUnit( polygonAttrUnit = PolygonAttribsStateUnit.makePolygonAttribsStateUnit( null, null ) );
        }
        else if ( app.getPolygonAttributes().isChanged() )
        {
            polygonAttrUnit.update( app.getPolygonAttributes() );
            updateStateMap( polygonAttrUnit );
            
            _SG_PrivilegedAccess.setChanged( app.getPolygonAttributes(), false );
        }
        polygonAttrUnit.updateCachedStateId();
        translucent = translucent || polygonAttrUnit.isTranslucent();
        
        // check and update LineAttributes StateUnit
        LineAttribsStateUnit lineAttrUnit = (LineAttribsStateUnit)getStateUnit( LineAttribsStateUnit.STATE_TYPE );
        if ( lineAttrUnit.isDefault() )
        {
            if ( app.getLineAttributes() != null )
            {
                updateStateUnit( lineAttrUnit = LineAttribsStateUnit.makeLineAttribsStateUnit( app.getLineAttributes(), stateUnitCache ) );
                
                _SG_PrivilegedAccess.setChanged( app.getLineAttributes(), false );
            }
        }
        else if ( app.getLineAttributes() == null )
        {
            updateStateUnit( lineAttrUnit = LineAttribsStateUnit.makeLineAttribsStateUnit( null, null ) );
        }
        else if ( app.getLineAttributes().isChanged() )
        {
            lineAttrUnit.update( app.getLineAttributes() );
            updateStateMap( lineAttrUnit );
            
            _SG_PrivilegedAccess.setChanged( app.getLineAttributes(), false );
        }
        lineAttrUnit.updateCachedStateId();
        translucent = translucent || lineAttrUnit.isTranslucent();
        
        // check and update LineAttributes StateUnit
        PointAttribsStateUnit pointAttrUnit = (PointAttribsStateUnit)getStateUnit( PointAttribsStateUnit.STATE_TYPE );
        if ( pointAttrUnit.isDefault() )
        {
            if ( app.getPointAttributes() != null )
            {
                updateStateUnit( pointAttrUnit = PointAttribsStateUnit.makePointAttribsStateUnit( app.getPointAttributes(), stateUnitCache ) );
                
                _SG_PrivilegedAccess.setChanged( app.getPointAttributes(), false );
            }
        }
        else if ( app.getPointAttributes() == null )
        {
            updateStateUnit( pointAttrUnit = PointAttribsStateUnit.makePointAttribsStateUnit( null, null ) );
        }
        else if ( app.getPointAttributes().isChanged() )
        {
            pointAttrUnit.update( app.getPointAttributes() );
            updateStateMap( pointAttrUnit );
            
            _SG_PrivilegedAccess.setChanged( app.getPointAttributes(), false );
        }
        pointAttrUnit.updateCachedStateId();
        translucent = translucent || pointAttrUnit.isTranslucent();
        
        // check and update ColoringAttributes StateUnit
        ColoringStateUnit colorUnit = (ColoringStateUnit)getStateUnit( ColoringStateUnit.STATE_TYPE );
        //if ( colorShader.isDefault() )
        if ( ColoringStateUnit.isDefault( colorUnit ) )
        {
            if ( ( app.getColoringAttributes() != null ) || ( app.getTransparencyAttributes() != null ) )
            {
                updateStateUnit( colorUnit = ColoringStateUnit.makeColoringStateUnit( app.getColoringAttributes(), app.getTransparencyAttributes(), stateUnitCache ) );
                
                if ( app.getColoringAttributes() != null )
                    _SG_PrivilegedAccess.setChanged( app.getColoringAttributes(), false );
                if ( app.getTransparencyAttributes() != null )
                    _SG_PrivilegedAccess.setChanged( app.getTransparencyAttributes(), false );
            }
        }
        else if ( ( app.getColoringAttributes() == null ) && ( app.getTransparencyAttributes() == null ) )
        {
            updateStateUnit( colorUnit = ColoringStateUnit.makeColoringStateUnit( app.getColoringAttributes(), app.getTransparencyAttributes(), stateUnitCache ) );
        }
        else
        {
            final ColoringAttributes colorAttribs = app.getColoringAttributes();
            final boolean c1 = ( colorAttribs == null );
            final boolean c2 = ( colorUnit.getColoringAttributes() == ColoringStateUnit.DEFAULT_COLOR_ATTR );
            
            final boolean c;
            if ( c1 )
            {
                if ( !c2 )
                    c = true;
                else
                    c = false;
            }
            else if ( colorAttribs.isChanged() )
                c = true;
            else
                c = false;
            
            final TransparencyAttributes transAttribs = app.getTransparencyAttributes();
            final boolean t1 = ( transAttribs == null );
            final boolean t2 = ( colorUnit.getTransparencyAttributes() == ColoringStateUnit.DEFAULT_TRANS_ATTR );
            
            final boolean t;
            if ( t1 )
            {
                if ( !t2 )
                    t = true;
                else
                    t = false;
            }
            else if ( transAttribs.isChanged() )
                t = true;
            else
                t = false;
            
            if ( c || t )
            {
                colorUnit.update( app.getColoringAttributes(), app.getTransparencyAttributes() );
                updateStateMap( colorUnit );
                
                if ( !c1 )
                    _SG_PrivilegedAccess.setChanged( app.getColoringAttributes(), false );
                if ( !t1 )
                    _SG_PrivilegedAccess.setChanged( app.getTransparencyAttributes(), false );
            }
        }
        colorUnit.updateCachedStateId();
        translucent = translucent || colorUnit.isTranslucent();
        
        // check and update RenderingAttributes StateUnit
        RenderingAttribsStateUnit renderingAttrUnit = (RenderingAttribsStateUnit)getStateUnit( RenderingAttribsStateUnit.STATE_TYPE );
        if ( renderingAttrUnit.isDefault() )
        {
            if ( app.getRenderingAttributes() != null )
            {
                updateStateUnit( renderingAttrUnit = RenderingAttribsStateUnit.makeRenderingStateUnit( app.getRenderingAttributes(), stateUnitCache ) );
                
                _SG_PrivilegedAccess.setChanged( app.getRenderingAttributes(), false );
            }
        }
        else if ( app.getRenderingAttributes() == null )
        {
            updateStateUnit( renderingAttrUnit = RenderingAttribsStateUnit.makeRenderingStateUnit( null, null ) );
        }
        else if ( app.getRenderingAttributes().isChanged() )
        {
            renderingAttrUnit.update( app.getRenderingAttributes() );
            updateStateMap( renderingAttrUnit );
            
            _SG_PrivilegedAccess.setChanged( app.getRenderingAttributes(), false );
        }
        renderingAttrUnit.updateCachedStateId();
        translucent = translucent || renderingAttrUnit.isTranslucent();
        
        ShaderProgramStateUnit shaderProgramStateUnit = (ShaderProgramStateUnit)getStateUnit( ShaderProgramStateUnit.STATE_TYPE );
        if ( shaderProgramStateUnit.isDefault() )
        {
            if ( app.getShaderProgramContext() != null )
            {
                updateStateUnit( shaderProgramStateUnit = ShaderProgramStateUnit.makeShaderProgramStateUnit( app.getShaderProgramContext(), stateUnitCache ) );
                
                _SG_PrivilegedAccess.setChanged( app.getShaderProgramContext(), false );
            }
        }
        else if ( app.getShaderProgramContext() == null )
        {
            updateStateUnit( shaderProgramStateUnit = ShaderProgramStateUnit.makeShaderProgramStateUnit( null, null ) );
        }
        else if ( app.getShaderProgramContext().isChanged() )
        {
            shaderProgramStateUnit.update( app.getShaderProgramContext() );
            updateStateMap( shaderProgramStateUnit );
            
            _SG_PrivilegedAccess.setChanged( app.getShaderProgramContext(), false );
        }
        shaderProgramStateUnit.updateCachedStateId();
        translucent = translucent || shaderProgramStateUnit.isTranslucent();
        
        // check and update Texture StateUnit
        final int tuc = app.getTextureUnitsCount();
        for ( int i = 0; i < tuc; i++ )
        {
            TextureUnitStateUnit texStateUnit = (TextureUnitStateUnit)getStateUnit( TextureUnitStateUnit.STATE_TYPES[ i ] );
            
            final TextureUnit tu = app.getTextureUnit( i );
            final Texture texture = ( tu == null ) ? null : tu.getTexture();
            final TextureAttributes texAttribs = ( tu == null ) ? null : tu.getTextureAttributes();
            final TexCoordGeneration texCoordGen = ( tu == null ) ? null : tu.getTexCoordGeneration();
            
            if ( texStateUnit.isDefault() )
            {
                if ( ( texture != null ) || ( texAttribs != null ) || ( texCoordGen != null ) )
                {
                    updateStateUnit( texStateUnit = TextureUnitStateUnit.makeTextureUnitStateUnit( i, tu, stateUnitCache ) );
                    
                    //texture.setChanged( false );
                }
            }
            else if ( ( tu == null ) || ( ( texture == null ) && ( texAttribs == null ) && ( texCoordGen == null ) ) )
            {
                updateStateUnit( texStateUnit = TextureUnitStateUnit.makeTextureUnitStateUnit( i, null, null ) );
            }
            else if ( ( texture != null && texture.isChanged() ) || ( texAttribs != null && texAttribs.isChanged() ) || ( texCoordGen != null && texCoordGen.isChanged() ) )
            {
                texStateUnit.update( texture, texAttribs, texCoordGen );
                updateStateMap( texStateUnit );
                
                //texture.setChanged( false );
            }
            
            texStateUnit.updateCachedStateId();
            translucent = translucent || texStateUnit.isTranslucent();
        }
        
        if ( tuc < numValidTUs )
        {
            for ( int i = tuc; i < numValidTUs; i++ )
            {
                updateStateUnit( TextureUnitStateUnit.DEFAULT_UNIT[ i ] );
                updateStateMap( TextureUnitStateUnit.DEFAULT_UNIT[ i ] );
                
                getStateUnit( TextureUnitStateUnit.STATE_TYPES[ i ] ).updateCachedStateId();
            }
        }
        
        numValidTUs = tuc;
        
        if ( app.isStatic() )
        {
            _SG_PrivilegedAccess.markStaticClean( app );
        }
    }
}
