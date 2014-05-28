/**
 * ******************************************************************************************
 * Copyright (C) 2014 - Food and Agriculture Organization of the United Nations (FAO).
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice,this list
 *       of conditions and the following disclaimer.
 *    2. Redistributions in binary form must reproduce the above copyright notice,this list
 *       of conditions and the following disclaimer in the documentation and/or other
 *       materials provided with the distribution.
 *    3. Neither the name of FAO nor the names of its contributors may be used to endorse or
 *       promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,STRICT LIABILITY,OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
package org.geotools.map.extended.layer;

import java.util.List;
import org.geotools.swing.extended.exception.InitializeLayerException;

/**
 * An extended layer to be used to attach an image in the map control. <br/>It uses the {
 *
 * @see org.geotools.map.extended.layer.DirectImageLayer} to load the layer with the image in the
 * map control.
 * @author Elton Manoku
 */
public class ExtendedWmsLiteLayer extends ExtendedLayer {

    private WmsLiteLayer layer;

    /**
     * Constructor of the layer.
     * The layer acts as a wrapper of the WmsLiteLayer.
     * 
     * @param name Name of the layer
     * @param title Title of the layer.
     * @param url The Url of the WMS Server without the query
     * @param layerNames The list of names of the layers in the server 
     * that will be asked for rendering
     * @param srid The Srid of the SRS
     * @param version The Version of the WMS that will be used for the requests
     * @param format The format of the output image
     * @throws InitializeLayerException
     */
    public ExtendedWmsLiteLayer(String name, String title, String url,
            List<String> layerNames, Integer srid, String version, String format)
            throws InitializeLayerException {
        this.setLayerName(name);
        this.setTitle(title);
        this.layer = new WmsLiteLayer(url, layerNames, version);
        this.getMapLayers().add(this.layer);
        this.layer.setSrid(srid);
        if (format != null) {
            this.layer.setFormat(format);
        }
    }
}
