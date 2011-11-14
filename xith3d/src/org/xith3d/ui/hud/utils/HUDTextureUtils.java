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
package org.xith3d.ui.hud.utils;

import org.jagatoo.opengl.enums.TextureFormat;
import org.xith3d.loaders.texture.TextureLoader;
import org.xith3d.loaders.texture.TextureLoader.FlipMode;
import org.xith3d.scenegraph.Texture2D;
import org.xith3d.scenegraph.TextureImage2D;
import org.xith3d.scenegraph.Texture.MipmapMode;

/**
 * <p>
 * <code>HUDTextureUtils</code> is a utility class that provides class methods to load and cache textures for your game.
 * </p>
 * <p>
 * Before you call one of the methods here, you need to have created a {@link ResourceLocator} with a folder as the base resource folder, which
 * will be used as the relative location for further resources. You will make this new {@link ResourceLocator} globally accessible (which you will
 * need later.) Then you need to create a {@link TextureStreamLocator} for each folder containing textures to be used.
 * </p>
 * <p>
 * Example:
 * </p>
 * <p>
 * <code>ResourceLocator resLoc = ResourceLocator.create("resources/");<br />
 * resLoc.useAsSingletonInstance();<br />
 * resLoc.createAndAddTSL("textures");
 * </code>
 * </p>
 * 
 * @see org.xith3d.resources.ResourceLocator
 * @see org.jagatoo.loaders.textures.locators.TextureStreamLocator
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class HUDTextureUtils
{
    /**
     * Returns the width (in pixels) of the specified texture image.
     * 
     * @param texture org.xith3d.scenegraph.TextureImage2D - the texture image from which to get the width.
     * @param isDrawTexture boolean - a flag to indicate whether to draw the texture.
     * @return int - the width (in pixels) of the specified texture image.
     */
    public static final int getTextureWidth( TextureImage2D texture, boolean isDrawTexture )
    {
        if ( isDrawTexture )
            return ( texture.getOriginalWidth() );
        
        return ( texture.getWidth() );
    }
    
    /**
     * Returns the height (in pixels) of the specified texture image.
     * 
     * @param texture org.xith3d.scenegraph.TextureImage2D - the texture image from which to get the height.
     * @param isDrawTexture boolean - a flag to indicate whether to draw the texture.
     * @return int - the height (in pixels) of the specified texture image.
     */
    public static final int getTextureHeight( TextureImage2D texture, boolean isDrawTexture )
    {
        if ( isDrawTexture )
            return ( texture.getOriginalHeight() );
        
        return ( texture.getHeight() );
    }
    
    /**
     * Returns the width (in pixels) of the specified texture.
     * 
     * @param texture org.xith3d.scenegraph.Texture2D - the texture from which to get the width.
     * @return int - the width (in pixels) of the specified texture.
     */
    public static final int getTextureWidth( Texture2D texture )
    {
        return ( getTextureWidth( texture.getImage0(), texture.isDrawTexture() ) );
    }
    
    /**
     * Returns the height (in pixels) of the specified texture.
     * 
     * @param texture org.xith3d.scenegraph.Texture2D - the texture from which to get the height.
     * @return int - the height (in pixels) of the specified texture.
     */
    public static final int getTextureHeight( Texture2D texture )
    {
        return ( getTextureHeight( texture.getImage0(), texture.isDrawTexture() ) );
    }
    
    /**
     * The <code>HUDTextureUtils.getTexture()</code> method is simply a wrapper for the
     * <code>org.xith3d.loaders.texture.TextureLoader.getTexture()</code> method. Returns the <code>Texture2D</code> instance specified by the
     * specified texture resource name.
     * 
     * @param textureName java.lang.String - the resource name, that is searched in all the <code>TextureStreamLocator</code>s, added to the
     * <code>TextureLoader</code>.
     * @param useCache boolean - a flag to indicate whether to retrieve the texture from the cache, if not the first time this texture is retrieved.
     * @return org.xith3d.scenegraph.Texture2D - the texture to return. If the specified resource is not found, a default fallback texture is
     * returned.
     */
    public static final Texture2D getTexture( String textureName, boolean useCache )
    {
        if ( textureName == null )
            return ( null );
        
        return ( TextureLoader.getInstance().getTexture( textureName, (FlipMode)null, (TextureFormat)null, MipmapMode.BASE_LEVEL, false, useCache, true ) );
    }
    
    /**
     * The <code>HUDTextureUtils.getTexture()</code> method is simply a wrapper for the
     * <code>org.xith3d.loaders.texture.TextureLoader.getTexture()</code> method. Returns the <code>Texture2D</code> instance specified by the
     * specified texture resource name.
     * 
     * @param textureName java.lang.String - the resource name, that is searched in all the <code>TextureStreamLocator</code>s, added to the
     * <code>TextureLoader</code>.
     * @return org.xith3d.scenegraph.Texture2D - the texture to return (from the cache if any). If the specified resource is not found, a default
     * fallback texture is returned.
     */
    public static final Texture2D getTexture( String textureName )
    {
        return ( getTexture( textureName, true ) );
    }
    
    /**
     * The <code>HUDTextureUtils.getTexture()</code> method is simply a wrapper for the
     * <code>org.xith3d.loaders.texture.TextureLoader.getTexture()</code> method. Returns the <code>Texture2D</code> instance specified by the
     * specified texture resource name.
     * 
     * @param textureName java.lang.String - the resource name, that is searched in all the <code>TextureStreamLocator</code>s, added to the
     * <code>TextureLoader</code>.
     * @param useCache boolean - a flag to indicate whether to retrieve the texture from the cache, if not the first time this texture is retrieved.
     * @return org.xith3d.scenegraph.Texture2D - the texture to return. If the specified resource is not found, <code>null</code> is returned.
     */
    public static final Texture2D getTextureOrNull( String textureName, boolean useCache )
    {
        Texture2D texture = getTexture( textureName, useCache );
        
        if ( TextureLoader.isFallbackTexture( texture ) )
            return ( null );
        
        return ( texture );
    }
    
    /**
     * The <code>HUDTextureUtils.getTexture()</code> method is simply a wrapper for the
     * <code>org.xith3d.loaders.texture.TextureLoader.getTexture()</code> method. Returns the <code>Texture2D</code> instance specified by the
     * specified texture resource name.
     * 
     * @param textureName java.lang.String - the resource name, that is searched in all the <code>TextureStreamLocator</code>s, added to the
     * <code>TextureLoader</code>.
     * @return org.xith3d.scenegraph.Texture2D - the texture to return (from the cache if any). If the specified resource is not found,
     * <code>null</code> is returned.
     */
    public static final Texture2D getTextureOrNull( String textureName )
    {
        return ( getTextureOrNull( textureName, true ) );
    }
}
