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
package org.xith3d.resources;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.xith3d.loaders.models.Model;
import org.xith3d.render.Canvas3D;
import org.xith3d.render.CanvasPeer;
import org.xith3d.scenegraph.Shader;
import org.xith3d.scenegraph.Texture;
import org.xith3d.sound.SoundContainer;

/**
 * A <code>ResourceBag</code> holds references to all kinds of data to be used in Xith3D. Currently
 * handled resource types are:
 * <ul>
 * 	 <li>Textures (loaded by {@link org.xith3d.loaders.texture.TextureLoader})</li>
 *   <li>Models (loaded by e.g. {@link org.xith3d.loaders.models.ModelLoader})</li>
 *   <li>Scenes (loaded by e.g. {@link org.xith3d.loaders.models.ModelLoader})</li>
 *   <li>Sounds (loaded by e.g. {@link org.xith3d.loaders.sound.SoundLoader})</li>
 *   <li>ShaderPrograms (loaded by e.g. {@link org.xith3d.loaders.shaders.impl.glsl.GLSLShaderLoader})</li>
 * </ul>
 * The resources are mapped to a user defined name and are accessible only through this name or the
 * <code>Map</code>'s <code>Iterator</code>.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class ResourceBag
{
    private static ResourceBag singletonInstance = null;
    
    private final Map< String, Texture >         textures;
    private final Map< String, Model >           models;
    private final Map< String, SoundContainer >  sounds;
    private final Map< String, Shader >          shaders;

	/**
	 * If you want to use one <code>ResourceBag</code> as a singleton, first invoke this and then
	 * use the <code>getInstance()</code> method to access the instance everywhere.
	 * 
	 * @param resBag the <code>ResourceBag</code> instance to use as the singleton instance
	 * 
	 * @see #getInstance()
	 */
    public static void setSingletonInstance( ResourceBag resBag )
    {
        singletonInstance = resBag;
    }
    
	/**
	 * If you want to use one <code>ResourceBag</code> as a singleton, use this. But remember to
	 * first (once) invoke the <code>setSingletonInstance()</code> method.
	 * 
	 * @see #setSingletonInstance(ResourceBag)
	 * 
	 * @return the singleton instance (if already set)
	 */
    public static ResourceBag getInstance()
    {
        return ( singletonInstance );
    }
    
	/**
	 * Adds a <code>Texture</code> resource to the <code>ResourceBag</code>.
	 * 
	 * @param name the name to access the resource at a later time
	 * @param texture the resource to add to the bag
	 */
    public void addTexture( String name, Texture texture )
    {
        textures.put( name, texture );
    }
    
	/**
	 * Retrieves the given <code>Texture</code> resource from the <code>ResourceBag</code>.
	 * 
	 * @param name the name to use as the key
	 * 
	 * @return the retrieved <code>Texture</code> resource or <code>null</code>, if the name does not
	 * exist in the bag
	 */
    public Texture getTexture( String name )
    {
        return ( textures.get( name ) );
    }
    
    /**
	 * Returns a <code>Collection</code> of all <code>Texture</code>s stored in the
	 * <code>ResourceBag</code>.
	 * 
	 * @return the <code>Collection</code> of <code>Texture</code>s
     */
    public Collection< Texture > getTextures()
    {
        return ( textures.values() );
    }
    
    /**
	 * Returns the number of <code>Texture</code> resources stored in this <code>ResourceBag</code>.
	 * 
	 * @return the number of <code>Texture</code> resources
     */
    public int numTextures()
    {
        return ( textures.size() );
    }
    
	/**
	 * Adds a <code>Model</code> resource to the <code>ResourceBag</code>.
	 * 
	 * @param name the name to access the resource at a later time
	 * @param model the resource to add to the bag
	 */
    public void addModel( String name, Model model )
    {
        models.put( name, model );
    }
    
    /**
     * Retrieves the given <code>Model</code> resource from the <code>ResourceBag</code>.
     * 
     * @param name the name to use as the key
     * 
     * @return the retrieved <code>Model</code> resource or <code>null</code>, if the name does not exist in the bag
     */
    public Model getModel( String name )
    {
        return ( models.get( name ) );
    }
    
    /**
     * Retrieves a new shared instance of the given <code>Model</code> resource from the <code>ResourceBag</code>.
     * 
     * @param name the name to use as the key
     * 
     * @return a new shared instance of the <code>Model</code> resource or <code>null</code>, if the name does not exist in the bag.
     */
    public Model getModelInstance( String name )
    {
        final Model model = getModel( name );
        
        if ( model == null )
            return ( null );
        
        return ( model.getSharedInstance() );
    }
    
	/**
	 * Returns a <code>Collection</code> of all <code>Model</code>s stored in the
	 * <code>ResourceBag</code>.
	 * 
	 * @return the <code>Collection</code> of <code>Model</code>s
	 */
    public Collection< Model > getModels()
    {
        return ( models.values() );
    }
    
    /**
	 * Returns the number of <code>Model</code> resources stored in this <code>ResourceBag</code>.
	 * 
	 * @return the number of <code>Model</code> resources
     */
    public int numModels()
    {
        return ( models.size() );
    }
    
	/**
	 * Adds a <code>Sound</code> resource to the <code>ResourceBag</code>.
	 * 
	 * @param name the name to access the resource at a later time
	 * @param sound the resource to add to the bag
	 */
    public void addSound( String name, SoundContainer sound )
    {
        sounds.put( name, sound );
    }
    
    /**
     * Retrieves the given <code>Sound</code> resource from the <code>ResourceBag</code>.
     * 
     * @param name the name to use as the key
     * 
     * @return the retrieved <code>Sound</code> resource or <code>null</code>, if the name does not exist in the bag.
     */
    public SoundContainer getSound( String name )
    {
        return ( sounds.get( name ) );
    }
    
    /**
	 * Returns a <code>Collection</code> of all <code>Sound</code>s stored in the
	 * <code>ResourceBag</code>.
	 * 
	 * @return the <code>Collection</code> of <code>Sound</code>s
     */
    public Collection< SoundContainer > getSounds()
    {
        return ( sounds.values() );
    }
    
    /**
	 * Return the number of <code>Sound</code> resources stored in this
	 * <code>ResourceBag</code>.
	 * 
	 * @return the number of <code>Sound</code> resources
     */
    public int numSounds()
    {
        return ( sounds.size() );
    }
    
    /**
     * Adds a <code>Shader</code> resource to the <code>ResourceBag</code>.
     * 
     * @param name the name to access the resource at a later time
     * @param shader the resource to add to the bag
     */
    public void addShader( String name, Shader shader )
    {
        shaders.put( name, shader );
    }
    
	/**
	 * Retrieves the given <code>Shader</code> resource from the <code>ResourceBag</code>.
	 * 
	 * @param name the name to use as the key
	 * 
	 * @return the retrieved <code>Shader</code> resource or <code>null</code>, if the name does not exist in the bag.
	 */
    public Shader getShader( String name )
    {
        return ( shaders.get( name ) );
    }
    
    /**
	 * Returns a <code>Collection</code> of all <code>Shader</code>s stored in the
	 * <code>ResourceBag</code>.
	 * 
	 * @return the <code>Collection</code> of <code>Shader</code>s
     */
    public Collection< Shader > getShaders()
    {
        return ( shaders.values() );
    }
    
    /**
	 * Return the number of <code>Shader</code> resources stored in this
	 * <code>ResourceBag</code>.
	 * 
	 * @return the number of <code>Shader</code> resources
     */
    public int numShaders()
    {
        return ( shaders.size() );
    }
    
    /**
	 * Return the total number of all resources stored in this <code>ResourceBag</code>.
	 * 
	 * @return the number of all resources
     */
    public int numResources()
    {
        return ( numTextures() + numModels() + numShaders() );
    }
    
	/**
	 * This method frees OpenGL resources (names) for all this <code>NodeComponent</code> and all
	 * child-components.
	 * 
	 * @param canvasPeer the <code>CanvasPeer</code> for which to free the resources
	 * 
	 * @see NodeComponent
	 * @see CanvasPeer
	 */
    public void freeOpenGLResources( CanvasPeer canvasPeer )
    {
        for ( Texture texture: textures.values() )
            texture.freeOpenGLResources( canvasPeer );
        
        for ( Model model: models.values() )
            model.freeOpenGLResources( canvasPeer );
        
        /*
        for ( SoundContainer sound: sounds.values() )
            sound.freeOpenGLResources( canvasPeer );
        */
        
        for ( Shader shader: shaders.values() )
            shader.freeOpenGLResources( canvasPeer );
    }
    
	/**
	 * This method frees OpenGL resources (names) for all this <code>NodeComponent</code> and all
	 * child-components.
	 * 
	 * @param canvas the <code>Canvas3D</code> for which to free the resources
	 * 
	 * @see NodeComponent
	 * @see Canvas3D
	 */
    public final void freeOpenGLResources( Canvas3D canvas )
    {
        if ( canvas.getPeer() == null )
            throw new Error( "The given Canvas3D is not linked to a CanvasPeer." );
        
        freeOpenGLResources( canvas.getPeer() );
    }
    
	/**
	 * Creates a new <code>ResourceBag</code>. Each supported resource type is stored in a
	 * <code>HashMap</code> each.
	 */
    public ResourceBag()
    {
        this.textures = new HashMap< String, Texture >();
        this.models   = new HashMap< String, Model >();
        this.sounds   = new HashMap< String, SoundContainer >();
        this.shaders  = new HashMap< String, Shader >();
    }
}
