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
 * image. AM Extended so that the target geometry can be drawn on an existing map so that other
 * context details such as roads and abutting parcels can be included in the snapshot.
 *
 */
public class MapFeatureImageGenerator extends MapImageGenerator {

    private static final String LAYER_FIELD_LABEL = "label";
    private static final String LAYER_FIELD_MINOR_LABEL = "minorLabel";
    /*
     * Defines the additional attributes for the layer. The format of this string is <field
     * name>:<data type>. Note that empty string (i.e. "") can be used to indicate the type as
     * string, but specifying the data type is easier to maintain.
     */
    private static final String LAYER_ATTRIBUTE_DEFINITION =
            String.format("%s:String,%s:String", LAYER_FIELD_LABEL, LAYER_FIELD_MINOR_LABEL);
    private static final String LAYER_NAME = "feature_image";
    private static final String LAYER_STYLE_RESOURCE = "samoa_feature_image.xml";
    int imageHeightPixels = 400;
    int imageWidthPixels = 400;
    boolean newMap = true;

    /**
     * Used to render the target geometry only. This does not require a Map to be displayed to the
     * user.
     */
    public MapFeatureImageGenerator(int srid) throws InitializeMapException {
        super(new Map(srid));
        setDrawFrame(false);
    }

    /**
     * Used to snapshot an existing map with the target geometry drawn on top. The getFeatureImage
     * method will ensure the complete target geometry is displayed on the map for the snapshot.
     */
    public MapFeatureImageGenerator(Map map) throws InitializeMapException {
        super(map);
        setDrawText(false);
        newMap = false;
    }

    /**
     * Default image height is 400 pixels.
     */
    public int getImageHeightPixels() {
        return imageHeightPixels;
    }

    /**
     * Used to control the height of the image generated. Not used if an existing map is used as the
     * basis for the snapshot (i.e a map that is resizable by the user).
     *
     * @param imageHeightPixels
     */
    public void setImageHeightPixels(int imageHeightPixels) {
        this.imageHeightPixels = imageHeightPixels;
    }

    /**
     * Default image width is 400 pixels.
     */
    public int getImageWidthPixels() {
        return imageWidthPixels;
    }

    /**
     * Used to control the width of the image generated. Not used if an existing map is used as the
     * basis for the snapshot (i.e a map that is resizable by the user).
     *
     * @param imageWidthPixels
     */
    public void setImageWidthPixels(int imageWidthPixels) {
        this.imageWidthPixels = imageWidthPixels;
    }

    /**
     * Creates an image for the geometry feature with the label positioned in the middle. When using
     * an existing map as the basis for the snapshot, this method will ensure the geom to be drawn
     * is displayed on the map.
     *
     * @param geom The geometry to generate the image for
     * @param label The label to use in the image
     * @param imageFormat The image format, one of png, jpg or bmp.
     * @return The path and file name location of the new image file or null if the image could not
     * be generated.
     */
    public String getFeatureImage(byte[] geom, String label, String minorLabel, String imageFormat) {
        String result = null;
        try {
            if (geom != null) {

                // Create a layer that will be used to render the geometry
                ExtendedLayerGraphics drawLayer = (ExtendedLayerGraphics) getMap().getSolaLayers().get(LAYER_NAME);
                if (drawLayer == null) {
                    drawLayer = new ExtendedLayerGraphics(LAYER_NAME, Geometries.GEOMETRY,
                            LAYER_STYLE_RESOURCE, LAYER_ATTRIBUTE_DEFINITION);
                    drawLayer.setShowInToc(false);
                    getMap().addLayer(drawLayer);
                }

                HashMap<String, Object> fields = new HashMap<String, Object>();
                fields.put(LAYER_FIELD_LABEL, label);
                if (minorLabel != null) {
                    fields.put(LAYER_FIELD_MINOR_LABEL, minorLabel);
                }
                SimpleFeature feature = drawLayer.addFeature("1", geom, fields, false);

                // Zoom to the area of the new geometry. Buffer the shape by 50 units to prevent
                // any truncation of the image and show some location context
                ReferencedEnvelope boundsToZoom = JTS.toEnvelope((Geometry) feature.getDefaultGeometry());
                boundsToZoom.expandBy(50);

                // Make sure the map is located over the target geometry and check to ensure the
                // zoom scale is appropraite. 
                ReferencedEnvelope tempZoom = getMap().getDisplayArea();
                tempZoom.expandToInclude(boundsToZoom);
                if (tempZoom.getArea() > boundsToZoom.getArea() * 30) {
                    // The zoom scale is very large, focus the map on the target geometry and explicitly
                    // set the map size. 
                    tempZoom = boundsToZoom;
                    getMap().setSize(imageHeightPixels, imageHeightPixels);
                }
                getMap().setDisplayArea(tempZoom);

                // Allow the map up to 10 seconds to redraw before trying to take the 
                // snapshot of map. Taking a snapshot before the map is redrawn causes 
                // map drawing exceptions. 
                int count = 0;
                while (getMap().IsRendering() && count < 20) {
                    Thread.sleep(500);
                    count++;
                }

                // Generate the image using the bounding envelope of the parcel. 
                result = getImageAsFileLocation((int) getMap().getSize().getWidth(),
                        tempZoom, imageFormat);
            }
        } catch (Exception ex) {
            LogUtility.log("Unable to generate feature image for " + label, ex);
            result = null;
        } finally {

            if (getMap().getMapContent() != null && newMap) {
                // This is a temporary map, so dispose the map layers to avoid any memory leaks
                getMap().getMapContent().dispose();
                getMap().setMapContent(null);
            } else if (!newMap) {
                // This is a permanent map, so just remove the features from the temporary draw layer. 
                ExtendedLayerGraphics drawLayer = (ExtendedLayerGraphics) getMap().getSolaLayers().get(LAYER_NAME);
                if (drawLayer != null) {
                    drawLayer.removeFeatures(true);
                }
                getMap().refresh();
            }
        }
        return result;
    }
}
