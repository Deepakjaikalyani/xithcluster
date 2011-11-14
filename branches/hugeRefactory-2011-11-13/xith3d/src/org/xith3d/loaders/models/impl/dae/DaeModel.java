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
package org.xith3d.loaders.models.impl.dae;

import java.util.HashMap;

import org.jagatoo.loaders.models.collada.datastructs.AssetFolder;
import org.jagatoo.loaders.models.collada.datastructs.visualscenes.AbstractInstance;
import org.jagatoo.loaders.models.collada.datastructs.visualscenes.DaeNode;
import org.openmali.spatial.bodies.Frustum;
import org.xith3d.loaders.models.Model;
import org.xith3d.scenegraph.Geometry;
import org.xith3d.scenegraph.View;

/**
 * A model embeddable in a Xith3D scenegraph
 * 
 * @author Amos Wenger (aka BlueSky)
 */

@Deprecated
public class DaeModel extends Model
{
  //  private final AssetFolder assetFolder;
    
    private final HashMap<AbstractInstance, Geometry> instanceMap = new HashMap<AbstractInstance, Geometry>();
    
//    public final AssetFolder getAssetFolder()
//    {
//        return ( assetFolder );
//    }
    
    @Override
    public void interpolateAnimation( float animStartTime, float absAnimTime )
    {
    }
    
//    private void updateGeometry()
//    {
//        for ( Map.Entry< AbstractInstance, Geometry > entry: instanceMap.entrySet() )
//        {
//            if(entry.getKey() instanceof ControllerInstance)
//            {
//                ControllerInstance ci = (ControllerInstance)entry.getKey();
//                DaeConverter.update( ci.getController().getDestinationGeometry(), entry.getValue() );
//            }
//            else if ( entry.getKey() instanceof GeometryInstance )
//            {
//                GeometryInstance gi = (GeometryInstance)entry.getKey();
//                DaeConverter.update( gi.getGeometry(), entry.getValue() );
//            }
//        }
//    }
    
    @Override
    public boolean update( View view, Frustum frustum, long nanoTime, long nanoStep )
    {
//        if ( getCurrentAnimation() == null )
//            return ( true );
//
//        assetFolder.getModel().animate( TimingMode.NANOSECONDS.getMilliSeconds( nanoTime ) );
//
//        updateGeometry();
//
        return ( true );
    }
    
    @Override
    public Model getSharedInstance()
    {
        return ( super.getSharedInstance() );
    }
    
    /**
     * Creates a new Xith3D node.
     * 
     * @param assetFolder
     * @param colladaNode The  data node used to
     * extract information to be displayed by Xith3D
     */
    public DaeModel( AssetFolder assetFolder, DaeNode colladaNode )
    {
//        this.assetFolder = assetFolder;
//
//        this.addChild( DaeConverter.toXith3D( colladaNode, instanceMap ) );
//
//        /*
//         * Initialize the animations...
//         */
//        Iterator<Controller> i = assetFolder.getLibraryControllers().getControllers().values().iterator();
//        while ( i.hasNext() )
//        {
//            Controller c = i.next();
//            if ( c instanceof SkeletalController )
//            {
//                COLLADAAction action = assetFolder.getLibraryAnimations().getAnimations().values().iterator().next();
//
//                assetFolder.getModel().initAnimation( action.getId(), true );
//
//                @SuppressWarnings( "deprecation" )
//                ModelAnimation[] animations = new ModelAnimation[]
//                {
//                    new ModelAnimation( action.getId(), action.rotKeyFrames.size(), 25, null, action )
//                };
//                this.setAnimations( animations );
//            }
//        }
    }
}
