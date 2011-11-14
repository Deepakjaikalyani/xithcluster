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

import org.openmali.vecmath2.Matrix4f;
import org.openmali.vecmath2.Quaternion4f;
import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.Vector3f;
import org.openmali.vecmath2.util.Interpolation;

/**
 *
 */
public class Skeleton
{
    private final Joint[] joints;

    private final Matrix4f[] matrixPallete;
    //temporary vars
    //private final Matrix4f transform = new Matrix4f();
    private final Vector3f translation = new Vector3f();
    private final Quaternion4f rotation = new Quaternion4f();
    private final Tuple3f scale = new Tuple3f();

    public final Joint getRootJoint()
    {
        return ( joints[ 0 ] );
    }

    public final Joint getJoint( int i )
    {
        return ( joints[ i ] );
    }

    public final int getJointsCount()
    {
        return ( joints.length );
    }

    public final Matrix4f[] getMatrixPallete()
    {
        return ( matrixPallete );
    }

    public void reset()
    {
        //Matrix4f transform = Matrix4f.fromPool();
        Vector3f t = Vector3f.fromPool();
        Quaternion4f r = Quaternion4f.fromPool();
        Tuple3f s = Tuple3f.fromPool();
        for ( int i = 0; i < getJointsCount(); i++ )
        {
            Joint joint = getJoint( i );
            t.set( joint.getBindTranslation() );
            r.set( joint.getBindRotation() );
            s.set( joint.getBindScale() );
            setAbsolutes( joint, t, r, s );

//            if ( joint.hasParent() )
//            {
//                Joint parent = getJoint( joint.getParentIndex() );
//                transform.mul( getJoint( joint.getParentIndex() ).getWorldMatrix(), transform );
//            }
//            joint.getWorldMatrix().set( transform );


        }

        //Matrix4f.toPool( transform );
        Vector3f.toPool( t );
        Quaternion4f.toPool( r );
        Tuple3f.toPool( s );
    }

//    public void update( float absAnimTime )
//    {
//        //Matrix4f transform = Matrix4f.fromPool();
//        for ( int i = 0; i < getJointsCount(); i++ )
//        {
//            Skeleton.Joint joint = getJoint( i );
//
//            calcTransform( absAnimTime, joint, transform );
//
//            if ( joint.hasParent() )
//            {
//                transform.mul( skeleton.getJoint( joint.getParentIndex() ).getWorldMatrix(), transform );
//            }
//            joint.getWorldMatrix().set( transform );
//            getMatrixPallete()[ i ].mul(transform, joint.getInvBindPoseMatrix() );
//        }
//
//       // Matrix4f.toPool( transform );
//    }

    public void update( float absAnimTime )
    {
        for ( int i = 0; i < getJointsCount(); i++ )
        {
            Joint joint = getJoint( i );

            calcTransform( absAnimTime, joint, translation, rotation, scale );

            setAbsolutes( joint, translation, rotation, scale );

            mul( joint.getInvBindPoseMatrix(),
                    joint.getAbsTranslation(),
                    joint.getAbsRotation(),
                    joint.getAbsScale(),
                    getMatrixPallete()[ i ] );
        }
    }

    private void setAbsolutes( Joint joint, Vector3f translation, Quaternion4f rotation, Tuple3f scale )
    {
        if ( joint.hasParent() )
        {
            Joint parent = getJoint( joint.getParentIndex() );

            joint.getAbsRotation().mul( parent.getAbsRotation(), rotation );

            joint.getAbsScale().set( parent.getAbsScale() );
            joint.getAbsScale().mul( scale.getX(), scale.getY(), scale.getZ() );

            translation.mul( scale.getX(), scale.getY(), scale.getZ() );
            joint.getAbsTranslation().set( parent.getAbsRotation().transform( translation ) );
            joint.getAbsTranslation().add( parent.getAbsTranslation() );
        }
        else
        {
            joint.getAbsRotation().set( rotation );
            joint.getAbsTranslation().set( translation );
            joint.getAbsScale().set( scale );

            joint.getAbsTranslation().mul( scale.getX(), scale.getY(), scale.getZ() );
        }
    }

    private static void mul( Matrix4f matrix, Vector3f translation, Quaternion4f rotation, Tuple3f scale, Matrix4f out )
    {
        //out.set( matrix );
        Matrix4f tmp = Matrix4f.fromPool();

        tmp.setIdentity();
        tmp.setTranslation( translation );
        out.set( tmp );

        tmp.set( rotation );
        out.mul( tmp );

        tmp.setIdentity();
        tmp.m00( scale.getX() );
        tmp.m11( scale.getY() );
        tmp.m22( scale.getZ() );
        out.mul( tmp );

        out.mul( matrix/* , out */ );

        Matrix4f.toPool( tmp );
    }

    private static void calcTransform( float absAnimTime, Joint joint, Vector3f translation, Quaternion4f rotation, Tuple3f scale )
    {
        calcRotation( absAnimTime, joint, rotation );
        calcScale( absAnimTime, joint, scale );
        calcTranslation( absAnimTime, joint, translation );
    }

//    private void calcTransform( float absAnimTime, Joint joint, Matrix4f transform )
//    {
//        calcRotation( absAnimTime, joint, rotation );
//        calcTranslation( absAnimTime, joint, translation );
//        calcScale( absAnimTime, joint, scale );
//
//        MatrixUtils.compose( translation, rotation, scale, transform );
//    }

    /**
     * Searches the next key frame according to the current time.
     *
     * @param currentTime currentTime in seconds
     * @param timeline    key frames time values in second
     * @return selected key frame index
     */
    private static int searchNextFrameIndex( float currentTime, float[] timeline )
    {
        int frameIdx = 0;
        final int maxFrameIdx = timeline.length - 1;
        while ( frameIdx < maxFrameIdx && timeline[ frameIdx ] < currentTime )
        {
            frameIdx++;
        }

        return ( frameIdx );
    }

    private static void calcTranslation( float absAnimTime, Joint joint, Vector3f translation )
    {
        float[] timeline = joint.getTranslationTimeline();
        if ( timeline.length == 0 || timeline[ 0 ] > absAnimTime )
        {
            translation.set( joint.getBindTranslation() );
            return;
        }
        if ( timeline.length == 1 )
        {
            translation.set( joint.getTranslations()[ 0 ] );
            return;
        }
        int nextFrameIdx = searchNextFrameIndex( absAnimTime, timeline );
        if ( nextFrameIdx != 0 && nextFrameIdx != timeline.length - 1 )
        {
            Tuple3f t1 = joint.getTranslations()[ nextFrameIdx - 1 ];
            Tuple3f t2 = joint.getTranslations()[ nextFrameIdx ];
            Interpolation.interpolate( t1, t2, calcDelta( nextFrameIdx, absAnimTime, timeline ), translation );
        }
        else
        {
            translation.set( joint.getTranslations()[ nextFrameIdx ] );
        }
    }

    private static void calcRotation( float absAnimTime, Joint joint, Quaternion4f rotation )
    {
        float[] timeline = joint.getRotationTimeline();
        if ( timeline.length == 0 || timeline[ 0 ] > absAnimTime )
        {
            rotation.set( joint.getBindRotation() );
            return;
        }
        if ( timeline.length == 1 )
        {
            rotation.set( joint.getRotations()[ 0 ] );
        }
        else
        {
            int nextFrameIdx = searchNextFrameIndex( absAnimTime, timeline );
            if ( nextFrameIdx != 0 && nextFrameIdx != timeline.length - 1 )
            {
                Quaternion4f r1 = joint.getRotations()[ nextFrameIdx - 1 ];
                Quaternion4f r2 = joint.getRotations()[ nextFrameIdx ];
                // Interpolation.nlerp( r1, r2, calcDelta( nextFrameIdx, absAnimTime, timeline ), rotation );
                nlerp( r1, r2, calcDelta( nextFrameIdx, absAnimTime, timeline ), rotation );
            }
            else
            {
                rotation.set( joint.getRotations()[ nextFrameIdx ] );
            }
        }
    }

    private static void nlerp( Quaternion4f quatOrigin, Quaternion4f quatDestiny, float deltaT, Quaternion4f toInterpolate )
    {
        float dot = quatOrigin.getA() * quatDestiny.getA() + quatOrigin.getB() * quatDestiny.getB() +
                quatOrigin.getC() * quatDestiny.getC() + quatOrigin.getD() * quatDestiny.getD();

        float sign;
        if ( dot < 0.0f )
        {
            dot = -dot;
            sign = -1.0f;
        }
        else
        {
            sign = 1.0f;
        }
        if ( dot >= 0.99999f )
        {
            toInterpolate.set( quatOrigin );
            return;
        }

        float weight1 = 1.0f - deltaT;
        float weight2 = sign * deltaT;
        toInterpolate.set( weight1 * quatOrigin.getA() + weight2 * quatDestiny.getA(),
                weight1 * quatOrigin.getB() + weight2 * quatDestiny.getB(),
                weight1 * quatOrigin.getC() + weight2 * quatDestiny.getC(),
                weight1 * quatOrigin.getD() + weight2 * quatDestiny.getD()
        );
        toInterpolate.normalize();
    }

//    private static void slerp( Quaternion4f quatOrigin, Quaternion4f quatDestiny, float deltaT, Quaternion4f toInterpolate )
//    {
//        float dot = quatOrigin.getA() * quatDestiny.getA() + quatOrigin.getB() * quatDestiny.getB() +
//                quatOrigin.getC() * quatDestiny.getC() + quatOrigin.getD() * quatDestiny.getD();
//
//        float sign;
//        if ( dot < 0.0f )
//        {
//            dot = -dot;
//            sign = -1.0f;
//        }
//        else
//        {
//            sign = 1.0f;
//        }
//        if ( dot >= 0.99999f )
//        {
//            toInterpolate.set( quatOrigin );
//            return;
//        }
//        float angle = FastMath.acos( dot );
//        float denom = FastMath.sin( angle );
//        float weight1 = FastMath.sin( ( 1.0f - deltaT ) * angle ) / denom;
//        float weight2 = sign * FastMath.sin( deltaT * angle ) / denom;
//
//        toInterpolate.set( weight1 * quatOrigin.getA() + weight2 * quatDestiny.getA(),
//                weight1 * quatOrigin.getB() + weight2 * quatDestiny.getB(),
//                weight1 * quatOrigin.getC() + weight2 * quatDestiny.getC(),
//                weight1 * quatOrigin.getD() + weight2 * quatDestiny.getD()
//        );
//        toInterpolate.normalize();
//    }

    private static void calcScale( float absAnimTime, Joint joint, Tuple3f scale )
    {
        float[] timeline = joint.getScaleTimeline();
        if ( timeline.length == 0 || timeline[ 0 ] > absAnimTime )
        {
            scale.set( joint.getBindScale() );
            return;
        }
        if ( timeline.length == 1 )
        {
            scale.set( joint.getScales()[ 0 ] );
            return;
        }
        int nextFrameIdx = searchNextFrameIndex( absAnimTime, timeline );
        if ( nextFrameIdx != 0 && nextFrameIdx != timeline.length - 1 )
        {
            Tuple3f t1 = joint.getScales()[ nextFrameIdx - 1 ];
            Tuple3f t2 = joint.getScales()[ nextFrameIdx ];
            Interpolation.interpolate( t1, t2, calcDelta( nextFrameIdx, absAnimTime, timeline ), scale );
        }
        else
        {
            scale.set( joint.getScales()[ nextFrameIdx ] );
        }
    }

    private static float calcDelta( int nextFrameIdx, float absAnimTime, float[] timeline )
    {
        float prevTime = timeline[ nextFrameIdx - 1 ];
        // time distance beetween both frames
        float timeDist = timeline[ nextFrameIdx ] - prevTime;
        // compute the "delta" = value in the [0-1] range that
        // represents our "position" between the two frames.
        return ( ( absAnimTime - prevTime ) / timeDist );
    }

    public Skeleton sharedCopy()
    {
        Joint[] newJoints = new Joint[joints.length];
        for ( int i = 0; i < joints.length; i++ )
        {
            Joint joint = joints[ i ];
            Joint parent = joint.hasParent() ? joints[ joint.getParentIndex() ] : null;
            newJoints[ i ] = new Joint( joint, parent );
        }

        Skeleton newSkeleton = new Skeleton( newJoints );

        return ( newSkeleton );
    }

    @Override
    public String toString()
    {
        return ( getRootJoint().getName() );
    }

    public Skeleton( Joint[] joints )
    {
        this.joints = joints;
        this.matrixPallete = new Matrix4f[joints.length];
        for ( int i = 0; i < joints.length; i++ )
        {
            matrixPallete[ i ] = new Matrix4f();
        }
    }
}
