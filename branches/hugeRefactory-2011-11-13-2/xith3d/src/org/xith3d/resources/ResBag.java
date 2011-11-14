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

import org.xith3d.loaders.models.Model;
import org.xith3d.scenegraph.Shader;
import org.xith3d.scenegraph.Texture;
import org.xith3d.sound.SoundContainer;

/**
 * This is a "shortcut" to the singleton instance of <code>ResourceBag</code>.
 * 
 * @see ResourceBag
 * @see ResourceBag#setSingletonInstance(ResourceBag)
 * @see ResourceBag#getInstance()
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class ResBag
{
	/**
	 * Retrieves the given <code>Texture</code> resource from the <code>ResourceBag</code>.
	 * 
	 * @param name the name to use as the key
	 * 
	 * @return the retrieved <code>Texture</code> resource or <code>null</code>, if the name does
	 * not exist in the bag
	 */
    public static Texture getTexture( String name )
    {
        return ( ResourceBag.getInstance().getTexture( name ) );
    }
    
	/**
	 * Returns a <code>Collection</code> of all <code>Texture</code>s stored in the
	 * <code>ResourceBag</code>.
	 * 
	 * @return the <code>Collection</code> of <code>Texture</code>s
	 */
    public static Collection< Texture > getTextures()
    {
        return ( ResourceBag.getInstance().getTextures() );
    }
    
	/**
	 * Returns the number of <code>Texture</code> resources stored in this <code>ResourceBag</code>.
	 * 
	 * @return the number of <code>Texture</code> resources
	 */
    public static int numTextures()
    {
        return ( ResourceBag.getInstance().numTextures() );
    }
    
	/**
	 * Retrieves the given <code>Model</code> resource from the <code>ResourceBag</code>.
	 * 
	 * @param name the name to use as the key
	 * 
	 * @return the <code>Model</code> resource or <code>null</code> if the resource with the name
	 * does not exist in the bag
	 */
    public static Model getModel( String name )
    {
        return ( ResourceBag.getInstance().getModel( name ) );
    }
    
	/**
	 * Retrieves a new shared instance of the given <code>Model</code> resource from the
	 * <code>ResourceBag</code>.
	 * 
	 * @param name the name to use as the key
	 * 
	 * @return the shared instance of the <code>Model</code> resource or <code>null</code> if the
	 * resource with the name does not exist in the bag
	 */
    public static Model getModelInstance( String name )
    {
        return ( ResourceBag.getInstance().getModelInstance( name ) );
    }
    
	/**
	 * Returns a <code>Collection</code> of all <code>Model</code>s stored in the
	 * <code>ResourceBag</code>.
	 * 
	 * @return the <code>Collection</code> of <code>Model</code>s
	 */
    public static Collection< Model > getModels()
    {
        return ( ResourceBag.getInstance().getModels() );
    }
    
	/**
	 * Returns the number of <code>Model</code> resources stored in this <code>ResourceBag</code>.
	 * 
	 * @return the number of <code>Model</code> resources
	 */
    public static int numModels()
    {
        return ( ResourceBag.getInstance().numModels() );
    }

	/**
	 * Retrieves the given <code>SoundContainer</code> resource from the <code>ResourceBag</code>.
	 * 
	 * @param name the name to use as the key
	 * 
	 * @return the <code>SoundContainer</code> resource or <code>null</code> if the resource with
	 * the name does not exist in the bag
	 */
    public static SoundContainer getSound( String name )
    {
        return ( ResourceBag.getInstance().getSound( name ) );
    }
    
	/**
	 * Returns a <code>Collection</code> of all <code>SoundContainer</code> resources stored in the
	 * <code>ResourceBag</code>.
	 * 
	 * @return the <code>Collection</code> of <code>SoundContainer</code> resources
	 */
    public static Collection< SoundContainer > getSounds()
    {
        return ( ResourceBag.getInstance().getSounds() );
    }
    
	/**
	 * Return the number of <code>SoundContainer</code> resources stored in this
	 * <code>ResourceBag</code>.
	 * 
	 * @return the number of <code>SoundContainer</code> resources
	 */
    public static int numSounds()
    {
        return ( ResourceBag.getInstance().numSounds() );
    }
    
	/**
	 * Retrieves the given <code>Shader</code> resource from the <code>ResourceBag</code>.
	 * 
	 * @param name the name to use as the key
	 * 
	 * @return the <code>Shader</code> resource or <code>null</code> if the resource with the name
	 * does not exist in the bag
	 */
    public static Shader getShader( String name )
    {
        return ( ResourceBag.getInstance().getShader( name ) );
    }

	/**
	 * Returns a <code>Collection</code> of all <code>Shader</code>s stored in the
	 * <code>ResourceBag</code>.
	 * 
	 * @return the <code>Collection</code> of <code>Shader</code>s
	 */
    public static Collection< Shader > getShaders()
    {
        return ( ResourceBag.getInstance().getShaders() );
    }

	/**
	 * Returns the number of <code>Shader</code> resources stored in this <code>ResourceBag</code>.
	 * 
	 * @return the number of <code>Shader</code> resources
	 */
    public static int numShaders()
    {
        return ( ResourceBag.getInstance().numShaders() );
    }

	/**
	 * Return the total number of all resources stored in this <code>ResourceBag</code>.
	 * 
	 * @return the number of all resources
	 */
    public static int numResources()
    {
        return ( ResourceBag.getInstance().numResources() );
    }
    
	/**
	 * Private constructor to creates a new <code>ResBag</code>.
	 */
    private ResBag()
    {
    }
}
