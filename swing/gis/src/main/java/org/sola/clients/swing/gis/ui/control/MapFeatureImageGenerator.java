/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations (FAO). All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,this list of conditions
 * and the following disclaimer. 2. Redistributions in binary form must reproduce the above
 * copyright notice,this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution. 3. Neither the name of FAO nor the names of its
 * contributors may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT,STRICT LIABILITY,OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
package org.sola.clients.swing.gis.ui.control;

import com.vividsolutions.jts.geom.Geometry;
import java.util.HashMap;
import org.geotools.geometry.jts.Geometries;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.extended.layer.ExtendedLayerGraphics;
import org.geotools.swing.extended.Map;
import org.geotools.swing.extended.exception.InitializeMapException;
import org.geotools.swing.extended.util.MapImageGenerator;
import org.opengis.feature.simple.SimpleFeature;
import org.sola.common.logging.LogUtility;

/**
 * Extends {@linkplain MapImageGenerator} to produce an image for one feature rather than all
 * features in the map. This generator does not need to display the map in order to generate the
 * image.
 *
 */
public class MapFeatureImageGenerator extends MapImageGenerator {

    private static final String LAYER_FIELD_LABEL = "label";
    /*
     * Defines the additional attributes for the layer. The format of this string is <field
     * name>:<data type>. Note that empty string (i.e. "") can be used to indicate the type as
     * string, but specifying the data type is easier to maintain.
     */
    private static final String LAYER_ATTRIBUTE_DEFINITION =
            String.format("%s:String", LAYER_FIELD_LABEL);
    private static final String LAYER_NAME = "feature_image";
    private static final String LAYER_STYLE_RESOURCE = "samoa_feature_image.xml";
    int imageHeightPixels = 425;
    int imageWidthPixels = 425;

    public MapFeatureImageGenerator(int srid) throws InitializeMapException {
        super(new Map(srid));
        setDrawFrame(false);
    }

    /**
     * Default image height is 425 pixels.
     */
    public int getImageHeightPixels() {
        return imageHeightPixels;
    }

    /**
     * Used to control the height of the image generated.
     *
     * @param imageHeightPixels
     */
    public void setImageHeightPixels(int imageHeightPixels) {
        this.imageHeightPixels = imageHeightPixels;
    }

    /**
     * Default image width is 425 pixels.
     */
    public int getImageWidthPixels() {
        return imageWidthPixels;
    }

    /**
     * Used to control the width of the image generated.
     *
     * @param imageWidthPixels
     */
    public void setImageWidthPixels(int imageWidthPixels) {
        this.imageWidthPixels = imageWidthPixels;
    }

    /**
     * Creates an image for the geometry feature with the label positioned in the middle.
     *
     * @param geom The geometry to generate the image for
     * @param label The label to use in the image
     * @param imageFormat The image format, one of png, jpg or bmp.
     * @return The path and file name location of the new image file or null if the image could not
     * be generated.
     */
    public String getFeatureImage(byte[] geom, String label, String imageFormat) {
        String result = null;
        try {
            if (geom != null) {
                getMap().setSize(imageWidthPixels, imageHeightPixels);
                // Create a layer that will be used to render the geometry
                ExtendedLayerGraphics drawLayer = new ExtendedLayerGraphics(LAYER_NAME,
                        Geometries.GEOMETRY, LAYER_STYLE_RESOURCE, LAYER_ATTRIBUTE_DEFINITION);

                getMap().addLayer(drawLayer);
                HashMap<String, Object> fields = new HashMap<String, Object>();
                fields.put(LAYER_FIELD_LABEL, label);
                SimpleFeature feature = drawLayer.addFeature("1", geom, fields, false);

                // Zoom to the area of the new geometry. Buffer the shape by 1 unit to prevent
                // any truncation of the image. 
                ReferencedEnvelope boundsToZoom = JTS.toEnvelope((Geometry) feature.getDefaultGeometry());
                boundsToZoom.expandBy(1);
                getMap().setDisplayArea(boundsToZoom);

                // Generate the image using the bounding envelope of the parcel. 
                result = getImageAsFileLocation(imageWidthPixels, boundsToZoom, imageFormat);
            }
        } catch (Exception ex) {
            LogUtility.log("Unable to generate feature image for " + label, ex);
            result = null;
        } finally {
            if (getMap().getMapContent() != null) {
                // This is a temporary map, so dispose the map layers to avoid any memory leaks
                getMap().getMapContent().dispose();
                getMap().setMapContent(null);
            }
        }
        return result;
    }
}
