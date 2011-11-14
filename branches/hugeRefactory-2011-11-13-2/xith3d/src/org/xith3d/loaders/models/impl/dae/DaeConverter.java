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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.jagatoo.loaders.models.collada.datastructs.AssetFolder;
import org.jagatoo.loaders.models.collada.datastructs.effects.Effect;
import org.jagatoo.loaders.models.collada.datastructs.effects.Profile;
import org.jagatoo.loaders.models.collada.datastructs.effects.ProfileCommon;
import org.jagatoo.loaders.models.collada.datastructs.geometries.Mesh;
import org.jagatoo.loaders.models.collada.datastructs.geometries.PolygonsGeometry;
import org.jagatoo.loaders.models.collada.datastructs.geometries.TrianglesGeometry;
import org.jagatoo.loaders.models.collada.datastructs.images.Surface;
import org.jagatoo.loaders.models.collada.datastructs.visualscenes.AbstractInstance;
import org.jagatoo.loaders.models.collada.datastructs.visualscenes.ControllerInstance;
import org.jagatoo.loaders.models.collada.datastructs.visualscenes.GeometryInstance;
import org.openmali.vecmath2.Matrix4f;
import org.xith3d.loaders.texture.TextureLoader;
import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.Geometry;
import org.xith3d.scenegraph.Group;
import org.xith3d.scenegraph.Material;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.Transform;
import org.xith3d.scenegraph.TransparencyAttributes;
import org.xith3d.scenegraph.TriangleArray;

/**
 * This class is used to retrieve Xith3D models from a File
 * object, which corresponds to the data loaded from an XML object.
 * 
 * @author Amos Wenger (aka BlueSky)
 */
@Deprecated
class DaeConverter
{
//    /**
//     * Creates a Xith3D Appearance from informations.
//     *
//     * @param material
//     *
//     * @return the Appearance
//     */
//    public static Appearance toXith3D( org.jagatoo.loaders.models.collada.datastructs.materials.Material material )
//    {
//        AssetFolder file = material.getFile();
//
//        Appearance app = new Appearance();
//        app.setMaterial( new Material( true ) );
//        app.getMaterial().setColorTarget( Material.AMBIENT );
//
//        String texture = null;
//        String effectUrl = material.getEffect();
//        Effect effect = ( effectUrl.length() == 0 ) ?
//                        file.getLibraryEffects().getEffects().values().iterator().next() :
//                        file.getLibraryEffects().getEffects().get( material.getEffect() );
//        for ( Profile profile : effect.profiles )
//        {
//            if ( profile instanceof ProfileCommon )
//            {
//                ProfileCommon profileCommon = (ProfileCommon)profile;
//
//                for ( Surface surface : profileCommon.getSurfaces().values() )
//                {
//                    for ( String imageId : surface.imageIds )
//                    {
//                        if ( texture == null )
//                        {
//                            texture = file.getLibraryImages().getImages().get( imageId );
//                            //System.out.println( "Found texture : " + texture );
//                        }
//                        else
//                        {
//                            System.err.println( "Ignoring extra texture : " + file.getLibraryImages().getImages().get( imageId ) );
//                        }
//                    }
//                }
//            }
//            else
//            {
//                System.err.println( "Ignoring profile type : " + profile.getClass().getName() );
//            }
//        }
//
//        if ( texture != null )
//        {
//            try
//            {
//                app.setTexture( TextureLoader.getInstance().loadTexture( new URL( file.getBasePath().toExternalForm() + texture ) ) );
//            }
//            catch ( MalformedURLException e )
//            {
//                e.printStackTrace();
//            }
//        }
//        else
//        {
//            System.out.println( "No texture found" );
//        }
//
//        if ( ( app.getTexture() != null ) && ( app.getTexture().getFormat().hasAlpha() ) )
//        {
//            app.getTransparencyAttributes( true ).setMode( TransparencyAttributes.BLENDED );
//        }
//
//        return ( app );
//    }
//
//    public static Group toXith3D( org.jagatoo.loaders.models.collada.datastructs.visualscenes.Node node, Map< AbstractInstance, Geometry > instanceMap )
//    {
//        Group grp = null;
//
//        if(node.getTransform().getMatrixTransform().getMatrix().equals( Matrix4f.IDENTITY ))
//        {
//            grp = new Group();
//        }
//        else
//        {
//            Transform trans = new Transform();
//            trans.setMatrix( node.getTransform().getMatrixTransform().getMatrix() );
//            grp = trans;
//        }
//
//        for ( org.jagatoo.loaders.models.collada.datastructs.visualscenes.Node n: node.getChildren() )
//        {
//            grp.addChild( toXith3D( n, instanceMap ) );
//        }
//
//        for ( org.jagatoo.loaders.models.collada.datastructs.visualscenes.AbstractInstance ai: node.getGeometryInstances() )
//        {
//            grp.addChild( toXith3D( ai, instanceMap ) );
//        }
//
//        for ( org.jagatoo.loaders.models.collada.datastructs.visualscenes.AbstractInstance ai: node.getControllerInstances() )
//        {
//            grp.addChild( toXith3D( ai, instanceMap ) );
//        }
//
//        return grp;
//    }
//
//    public static Shape3D toXith3D( org.jagatoo.loaders.models.collada.datastructs.visualscenes.AbstractInstance instance, Map< AbstractInstance, Geometry > instanceMap )
//    {
//        Geometry geom = null;
//        Appearance app = null;
//
//        if ( instance instanceof GeometryInstance )
//        {
//            GeometryInstance colladaGINode = (GeometryInstance)instance;
//
//            geom = DaeConverter.toXith3D( colladaGINode.getGeometry() );
//            app = DaeConverter.toXith3D( colladaGINode.getMaterial() );
//            instanceMap.put( colladaGINode, geom );
//        }
//        else if ( instance instanceof ControllerInstance )
//        {
//            ControllerInstance colladaCINode = (ControllerInstance)instance;
//
//            /*
//             * Here goes the initialization of the Xith3D geometry that
//             * should be updated by the controller.
//             * The geom is just initial : it will be updated whenever animations are played on the model.
//             * But the number of vertices, normals, etc.. will stay the same.
//             */
//            colladaCINode.getController().updateDestinationGeometry( 5000 );
//            geom = DaeConverter.toXith3D( colladaCINode.getController().getDestinationGeometry() );
//            app = DaeConverter.toXith3D( colladaCINode.getMaterial() );
//            instanceMap.put( colladaCINode, geom );
//        }
//        else
//        {
//            throw new Error( "Type " + instance.getClass().getSimpleName() + " not implemented yet." );
//        }
//
//        return new Shape3D( geom, app );
//    }
//
//    /**
//     * Creates a Xith3D geometry from a  Geometry information.
//     *
//     * @param geometry
//     *
//     * @return A Xith3D geometry from the given informations
//     */
//    public static Geometry toXith3D( org.jagatoo.loaders.models.collada.datastructs.geometries.Geometry geometry )
//    {
//        if ( geometry instanceof PolygonsGeometry )
//        {
//            throw new Error( "PolygonsGeometry isn't supported yet ! If you're e.g. exporting from blender," +
//                             "\n try to select the option \"Triangles\" : THAT is supported."
//                           );
//        }
//        else if ( geometry instanceof TrianglesGeometry )
//        {
//            return ( createGeometryFromTriangles( (TrianglesGeometry)geometry ) );
//        }
//        else if ( geometry == null )
//        {
//        	throw new Error( "This Geometry is null." );
//        }
//        else
//        {
//            throw new Error( geometry.getClass().getSimpleName() + " isn't supported yet ! If you're e.g. exporting from blender," +
//                             "\n try to select the option \"Triangles\" : THAT is supported."
//                           );
//        }
//    }
//
//    /**
//     * Creates a Xith3D geometry from a  Geometry information.
//     *
//     * @param geometry
//     *
//     * @return A Xith3D geometry from the given informations
//     */
//    public static void update( org.jagatoo.loaders.models.collada.datastructs.geometries.Geometry geometry, Geometry xithGeometry )
//    {
//        if ( geometry instanceof PolygonsGeometry )
//        {
//            throw new Error( "PolygonsGeometry isn't supported yet ! If you're e.g. exporting from blender," +
//                             "\n try to select the option \"Triangles\" : THAT is supported."
//                           );
//        }
//        else if ( geometry instanceof TrianglesGeometry )
//        {
//            updateGeometryFromTriangles( (TrianglesGeometry)geometry, xithGeometry );
//        }
//        else if ( geometry == null )
//        {
//            throw new Error( "This Geometry is null." );
//        }
//        else
//        {
//            throw new Error(geometry.getClass().getSimpleName()+" isn't supported yet ! If you're e.g. exporting from blender," +
//            "\n try to select the option \"Triangles\" : THAT is supported.");
//        }
//    }
//
//    /**
//     * Creates a Xith3D geometry from informations (triangles).
//     *
//     * @param geometry
//     *
//     * @return A Xith3D geometry from the given informations
//     *
//     * @throws Error (We never know)
//     */
//    private static void updateGeometryFromTriangles(TrianglesGeometry geometry, Geometry xithGeometry) {
//
//        Mesh mesh = geometry.getMesh();
//        int indexCount = mesh.getVertexIndices().length;
//
//        // VERTICES
//        float[] realVertices = null;
//        if ( mesh.getVertexIndices() != null )
//        {
//            realVertices = new float[indexCount * 3];
//            float[] fs = mesh.getSources().getVertices();
//            for ( int i = 0; i < indexCount; i++ )
//            {
//                int j = mesh.getVertexIndices()[i];
//
//                realVertices[i * 3] = fs[j * 3];
//                realVertices[i * 3 + 1] = fs[j * 3 + 1];
//                realVertices[i * 3 + 2] = fs[j * 3 + 2];
//            }
//        }
//        else
//        {
//            throw new Error( "Huh.. we have no vertices... Your file has a problem :)" );
//        }
//
//        // NORMALS
//        float[] realNormals = null;
//        if ( mesh.getNormalIndices() != null )
//        {
//            realNormals = new float[indexCount * 3];
//            float[] fs = mesh.getSources().getNormals();
//            for ( int i = 0; i < indexCount; i++ )
//            {
//                int j = mesh.getNormalIndices()[i];
//
//                realNormals[i * 3] = fs[j * 3];
//                realNormals[i * 3 + 1] = fs[j * 3 + 1];
//                realNormals[i * 3 + 2] = fs[j * 3 + 2];
//            }
//        }
//
//        // COLORS
//        float[] realColors = null;
//        if ( mesh.getColorIndices() != null )
//        {
//            realColors = new float[indexCount * 4];
//            float[] fs = mesh.getSources().getColors();
//            for ( int i = 0; i < indexCount; i++ )
//            {
//                int j = mesh.getColorIndices()[i];
//
//                realColors[i * 3] = fs[j * 3];
//                realColors[i * 3 + 1] = fs[j * 3 + 1];
//                realColors[i * 3 + 2] = fs[j * 3 + 2];
//                realColors[i * 3 + 3] = fs[j * 3 + 2];
//            }
//        }
//
//        // UVS
//        float[][] realUVs = null;
//        if ( mesh.getUVIndices().length > 0 )
//        {
//            realUVs = new float[ mesh.getUVIndices().length ][ indexCount * 2 ];
//            for ( int j = 0; j < mesh.getUVIndices().length; j++ )
//            {
//                float[] fs = mesh.getSources().getUVs()[ j ];
//                for ( int i = 0; i < indexCount; i++ )
//                {
//                    int k = mesh.getUVIndices()[j][i];
//
//                    realUVs[j][i * 2] = fs[k * 2];
//                    realUVs[j][i * 2 + 1] = fs[k * 2 + 1];
//                }
//            }
//        }
//
//        /*
//         * Xith3D part
//         */
//
//        // VERTICES
//        if ( mesh.getVertexIndices() != null )
//        {
//            xithGeometry.setCoordinates( 0, realVertices );
//        }
//
//        // NORMALS
//        if ( mesh.getNormalIndices() != null )
//        {
//            xithGeometry.setNormals( 0, realNormals );
//        }
//
//        // COLORS
//        if ( mesh.getColorIndices() != null )
//        {
//            xithGeometry.setColors( 0, 4, realColors );
//        }
//
//        // UVS
//        if ( mesh.getUVIndices().length > 0 )
//        {
//            for ( int i = 0; i < realUVs.length; i++ )
//            {
//                xithGeometry.setTextureCoordinates( i, 0, 2, realUVs[i] );
//            }
//        }
//    }
//
//    /**
//     * Creates a Xith3D geometry from informations (triangles).
//     *
//     * @param geometry
//     *
//     * @return A Xith3D geometry from the given informations
//     *
//     * @throws Error (We never know)
//     */
//    private static Geometry createGeometryFromTriangles( TrianglesGeometry geometry )
//    {
//        int indexCount = geometry.getMesh().getVertexIndices().length;
//
//        /*
//         * Xith3D part
//         */
//
//        Geometry xithGeometry = new TriangleArray(indexCount);
//
//        updateGeometryFromTriangles( geometry, xithGeometry );
//
//        return ( xithGeometry );
//    }
//    
    /** This class cannot be instantiated, it's static only */
    private DaeConverter() {}
}
