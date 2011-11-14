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
package org.xith3d.loaders.models.animations;

import org.jagatoo.datatypes.NamedObject;
import org.jagatoo.loaders.models.collada.MathUtils;
import org.jagatoo.loaders.models.collada.Transform;
import org.openmali.vecmath2.Matrix4f;
import org.openmali.vecmath2.Quaternion4f;
import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.Vector3f;

/**
 *
 *
 */
public class Joint implements NamedObject
{
    public final int NULL_PARENT = -1;

    private final short parentIndex;
    private final String name;

    private final Vector3f bindTranslation;
    private final Quaternion4f bindRotation;
    private final Tuple3f bindScale;

    private final Matrix4f invBindPoseMatrix;

    private final float[] translationTimeline;
    private final Vector3f[] translations;

    private final float[] rotationTimeline;
    private final Quaternion4f[] rotations;

    private final float[] scaleTimeline;
    private final Tuple3f[] scales;

    private short index;

    private final Vector3f absTranslation = new Vector3f();
    private final Quaternion4f absRotation = new Quaternion4f();
    private final Tuple3f absScale = new Tuple3f();

    public final short getParentIndex()
    {
        return ( parentIndex );
    }

    public final String getName()
    {
        return ( name );
    }

    public final Vector3f getBindTranslation()
    {
        return ( bindTranslation );
    }

    public final Quaternion4f getBindRotation()
    {
        return ( bindRotation );
    }

    public final Tuple3f getBindScale()
    {
        return ( bindScale );
    }

    public final Matrix4f getInvBindPoseMatrix()
    {
        return ( invBindPoseMatrix );
    }

    //keyframes data
    public final float[] getTranslationTimeline()
    {
        return ( translationTimeline );
    }

    public final Vector3f[] getTranslations()
    {
        return ( translations );
    }

    public final float[] getRotationTimeline()
    {
        return ( rotationTimeline );
    }

    public final Quaternion4f[] getRotations()
    {
        return ( rotations );
    }

    public final float[] getScaleTimeline()
    {
        return ( scaleTimeline );
    }

    public final Tuple3f[] getScales()
    {
        return ( scales );
    }

    public final short getIndex()
    {
        return ( index );
    }

    public final boolean hasParent()
    {
        return ( index != 0 );
    }

    public final Vector3f getAbsTranslation()
    {
        return ( absTranslation );
    }

    public final Quaternion4f getAbsRotation()
    {
        return ( absRotation );
    }

    public final Tuple3f getAbsScale()
    {
        return ( absScale );
    }

    public final void calcInvBindPoseMatrix( Joint parent )
    {
        Matrix4f m = Matrix4f.fromPool();

        MathUtils.compose( bindTranslation, bindRotation, bindScale, m );
        invBindPoseMatrix.invert( m );
        if ( parent != null )
        {
            invBindPoseMatrix.mul( invBindPoseMatrix, parent.getInvBindPoseMatrix() );
        }

        Matrix4f.toPool( m );
    }

    @Override
    public String toString()
    {
        return ( "Joint{" +
                "name='" + name + '\'' +
                ", parentIndex=" + parentIndex +
                ", index=" + index +
                '}' );
    }

    /**
     * @param name
     * @param index
     * @param parent
     * @param bindTransform
     * @param translationTimeline
     * @param translations
     * @param rotationTimeline
     * @param rotations
     * @param scaleTimeline
     * @param scales
     */
    public Joint( String name, short index, Joint parent, Transform bindTransform,
                  float[] translationTimeline,
                  Vector3f[] translations,
                  float[] rotationTimeline,
                  Quaternion4f[] rotations,
                  float[] scaleTimeline,
                  Tuple3f[] scales )
    {
        this.parentIndex = parent == null ? NULL_PARENT : parent.getIndex();
        this.name = name;
        this.index = index;
        bindTranslation = bindTransform.getTranslation( null ).getReadOnly();
        bindRotation = bindTransform.getRotation( null ).getReadOnly();
        bindScale = bindTransform.getScale( null ).getReadOnly();

        invBindPoseMatrix = new Matrix4f();
        calcInvBindPoseMatrix( parent );                 //todo read it from -poses

        this.translationTimeline = translationTimeline;
        this.translations = translations;
        this.rotationTimeline = rotationTimeline;
        this.rotations = rotations;
        this.scaleTimeline = scaleTimeline;
        this.scales = scales;
    }

    /**
     * Copy constructor
     *
     * @param joint
     * @param parent
     */
    public Joint( Joint joint, Joint parent )
    {
        this.parentIndex = parent == null ? NULL_PARENT : parent.getIndex();
        name = joint.name;
        index = joint.index;

        bindTranslation = joint.bindTranslation;
        bindRotation = joint.bindRotation;
        bindScale = joint.bindScale;

        translations = joint.translations;
        rotations = joint.rotations;
        scales = joint.scales;

        invBindPoseMatrix = joint.invBindPoseMatrix;

        translationTimeline = joint.translationTimeline;
        rotationTimeline = joint.rotationTimeline;
        scaleTimeline = joint.scaleTimeline;
    }
}
