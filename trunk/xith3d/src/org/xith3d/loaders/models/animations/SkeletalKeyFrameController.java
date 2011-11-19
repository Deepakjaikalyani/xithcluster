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
import org.jagatoo.loaders.models._util.AnimationType;
import org.openmali.vecmath2.Matrix4f;
import org.xith3d.scenegraph.Geometry;
import org.xith3d.scenegraph.Shape3D;

import java.util.Map;

/**
 *
 */
public class SkeletalKeyFrameController extends KeyFrameController
{
    private final float[] weights;
    private final short[] jointIndices;

    private final Shape3D shape;

    private final Skeleton skeleton;

    private float[] bindPoseCoords;
    private float[] bindPoseNormals;

    private final int influencesPerVertex;

    /**
     * {@inheritDoc}
     */
    @Override
    public final Shape3D getTarget()
    {
        return ( shape );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void updateTarget( float absAnimTime, int baseFrame, int nextFrame, float alpha, ModelAnimation animation )
    {
        skeleton.update( absAnimTime );
        updateGeometry( shape.getGeometry() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SkeletalKeyFrameController sharedCopy( Map<String, NamedObject> namedObjects )
    {
        String shapeName = this.shape.getName();
        Shape3D newShape = ( Shape3D ) namedObjects.get( shapeName );
        if ( newShape == null )
        {
            throw new Error( "Can't clone this AnimationController!" );
        }
        //todo
        return ( new SkeletalKeyFrameController(
                newShape,
                skeleton.sharedCopy(),
                ( SkeletalKeyFrame[] ) this.getKeyFrames(),
                influencesPerVertex,
                weights,
                jointIndices ) );

    }

    private void updateGeometry( Geometry geom )
    {
        float x, y, z;
        float nX = 0, nY = 0, nZ = 0;
        float vSumX, vSumY, vSumZ;
        float nSumX, nSumY, nSumZ;
        float tmpX, tmpY, tmpZ;

        for ( int i = 0; i < geom.getVertexCount(); i++ )
        {
            x = bindPoseCoords[ i * 3 ];
            y = bindPoseCoords[ i * 3 + 1 ];
            z = bindPoseCoords[ i * 3 + 2 ];
            if ( geom.hasNormals() )
            {
                nX = bindPoseNormals[ i * 3 ];
                nY = bindPoseNormals[ i * 3 + 1 ];
                nZ = bindPoseNormals[ i * 3 + 2 ];
            }
            vSumX = 0f;
            vSumY = 0f;
            vSumZ = 0f;

            nSumX = 0f;
            nSumY = 0f;
            nSumZ = 0f;

            for ( int j = 0; j < influencesPerVertex; j++ )
            {
                int k = i * influencesPerVertex + j;
                final float weight = weights[ k ];
                if ( weight == 0f )
                {
                    continue;
                }
                final int jointIndex = jointIndices[ k ];

                Matrix4f m = skeleton.getMatrixPallete()[ jointIndex ];
                //vertices
                tmpX = m.m00() * x + m.m01() * y + m.m02() * z + m.m03();
                tmpY = m.m10() * x + m.m11() * y + m.m12() * z + m.m13();
                tmpZ = m.m20() * x + m.m21() * y + m.m22() * z + m.m23();

                vSumX += tmpX * weight;
                vSumY += tmpY * weight;
                vSumZ += tmpZ * weight;

                //normals
                if ( geom.hasNormals() )
                {
                    tmpX = m.m00() * nX + m.m01() * nY + m.m02() * nZ;
                    tmpY = m.m10() * nX + m.m11() * nY + m.m12() * nZ;
                    tmpZ = m.m20() * nX + m.m21() * nY + m.m22() * nZ;

                    nSumX += tmpX * weight;
                    nSumY += tmpY * weight;
                    nSumZ += tmpZ * weight;
                }
            }

            geom.setCoordinateWithoutOpenGlHandling( i, vSumX, vSumY, vSumZ );
            if ( geom.hasNormals() )
            {
                geom.setNormalWithoutOpenGlHandling( i, nSumX, nSumY, nSumZ );
            }
        }
        geom.setBoundsDirty();
        geom.getOpenGLReference_DL_GeomData().invalidateNames();
        geom.getOpenGLReference_DL().invalidateNames();

        shape.updateBounds( false );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset()
    {
        skeleton.reset();
        Geometry geom = shape.getGeometry();
        geom.setCoordinates( 0, bindPoseCoords );
        if ( geom.hasNormals() )
        {
            geom.setNormals( 0, bindPoseNormals );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean checkUpdateConditions( boolean forced, float absAnimTime, int frame, float animDuration )
    {
        return ( true );
    }

    public SkeletalKeyFrameController( Shape3D shape, Skeleton skeleton, SkeletalKeyFrame[] frames, int influencesPerVertex, float[] weights, short[] jointIndices )
    {
        super( AnimationType.SKELETAL, frames );

        this.skeleton = skeleton;
        this.influencesPerVertex = influencesPerVertex;
        this.weights = weights;
        this.jointIndices = jointIndices;

        this.shape = shape;
        Geometry geom = shape.getGeometry();
        this.bindPoseCoords = new float[geom.getVertexCount() * 3];
        geom.getCoordinates( 0, bindPoseCoords );
        if ( geom.hasNormals() )
        {
            this.bindPoseNormals = new float[geom.getVertexCount() * 3];
            geom.getNormals( 0, bindPoseNormals );
        }
    }
}
