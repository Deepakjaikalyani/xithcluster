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

import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.Vector3f;
import org.xith3d.loaders.models.animations.Joint;
import org.xith3d.loaders.models.animations.Skeleton;
import org.xith3d.scenegraph.Geometry.Optimization;
import org.xith3d.scenegraph.Group;
import org.xith3d.scenegraph.Transform;
import org.xith3d.scenegraph.primitives.Line;
import org.xith3d.scenegraph.primitives.Sphere;
import org.xith3d.scenegraph.primitives.TextBillboard;

import java.awt.*;
import java.util.HashMap;

/**
 * A Skeleton visualizer in x-ray style, e.g. it
 * displays lines for each joint.
 *
 * @author Amos Wenger (aka BlueSky)
 */
public class SkeletonXRayVisualizer extends Group
{
    /**
     * The skeleton we're displaying
     */
    private final Skeleton skeleton;

    /**
     * A Joint->Line map to update them accordingly
     */
    private final HashMap<Joint, Line> mapLines = new HashMap<Joint, Line>();
    private final HashMap<Joint, Transform> mapSpheres = new HashMap<Joint, Transform>();

    /**
     * Updates the visualizer.
     */
    public void update()
    {
        Joint parentJoint = null;
        Joint joint = null;
        for ( int i = 0; i < skeleton.getJointsCount(); i++ )
        {
            joint = skeleton.getJoint( i );
            if ( i > 0 )
            {
                parentJoint = skeleton.getJoint( joint.getParentIndex() );
            }
            if ( !mapLines.containsKey( joint ) )
            {
                Line line = new Line( Point3f.ZERO, ( ( hasChildren( joint ) ) ? Colorf.GREEN : Colorf.WHITE ) );
                line.setAntialiasingEnabled( true );
                line.setWidth( 4 );
                line.getGeometry().setOptimization( Optimization.NONE );

                mapLines.put( joint, line );
                this.addChild( line );
            }

            if ( !mapSpheres.containsKey( joint ) )
            {
                Transform transform = new Transform();
                transform.add( new Sphere( 0.05f, 10, 10, Colorf.RED ) );
                transform.add(
                        TextBillboard.createFixedHeight(
                                0.2f,
                                joint.getName(),
                                Colorf.WHITE,
                                Font.decode( "Arial-plain-20" )
                        )
                );

                mapSpheres.put( joint, transform );
                this.addChild( transform );
            }

            Vector3f b = new Vector3f();
            b.set( ( parentJoint == null ) ?
                    getAbsoluteTranslation( joint ) :
                    getAbsoluteTranslation( parentJoint ) );
            Vector3f e = new Vector3f();
            e.set( getAbsoluteTranslation( joint ) );

            mapLines.get( joint ).setCoordinates( b, e );
            mapSpheres.get( joint ).setTranslation( b.getX(), b.getY(), b.getZ() );
        }
    }

    private Vector3f getAbsoluteTranslation( Joint joint )
    {
//        Vector3f t = new Vector3f();
//        Matrix4f m = joint.getWorldMatrix();
//        t.set( m.m03(), m.m13(), m.m23() );
        Vector3f t = new Vector3f( joint.getAbsTranslation() );
        return ( t );
    }

    private boolean hasChildren( Joint joint )
    {
        int num = 0;
        int index = joint.getIndex();
        for ( int i = index + 1; i < skeleton.getJointsCount(); i++ )
        {
            Joint child = skeleton.getJoint( i );
            if ( child.getParentIndex() == index )
            {
                return ( true );
            }
        }

        return ( false );
    }

    /**
     * Create a new {@link SkeletonXRayVisualizer}
     *
     * @param skeleton
     */
    public SkeletonXRayVisualizer( Skeleton skeleton )
    {
        this.skeleton = skeleton;
    }
}
