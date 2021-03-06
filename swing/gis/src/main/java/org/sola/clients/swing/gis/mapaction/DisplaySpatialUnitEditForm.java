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
package org.sola.clients.swing.gis.mapaction;

import org.geotools.swing.extended.Map;
import org.geotools.swing.mapaction.extended.ExtendedAction;
import org.sola.clients.swing.gis.layer.SpatialUnitEditLayer;
import org.sola.clients.swing.gis.ui.control.SpatialUnitEditForm;
import org.sola.common.messaging.GisMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * Map Action that displays the SpatialUnitEditForm allowing the user to edit the label for the
 * spatial unit feature.
 */
public class DisplaySpatialUnitEditForm extends ExtendedAction {

    /**
     * Name of this MapAction - spatial-unit-edit-show
     */
    public final static String MAPACTION_NAME = "spatial-unit-edit-show";
    private SpatialUnitEditLayer layer;
    private SpatialUnitEditForm displayFrom;

    /**
     * Constructor for the map action.
     *
     * @param mapObj The map object to associate the tool with
     * @param layer The SpatialUnitEditLayer to associate the tool with.
     */
    public DisplaySpatialUnitEditForm(Map mapObj, SpatialUnitEditLayer layer) {
        super(mapObj, MAPACTION_NAME, MessageUtility.getLocalizedMessage(
                GisMessage.SPATIAL_UNIT_TOOLTIP_EDIT_FORM).getMessage(),
                "resources/table-pencil.png");
        this.layer = layer;
    }

    /**
     * Displays the SpatialUnitEditForm to the user. If the form is hidden or not visible, it will
     * appear on top of the other forms.
     */
    @Override
    public void onClick() {
        if (displayFrom == null) {
            displayFrom = new SpatialUnitEditForm(layer.getSpatialUnitChangeListBean(),
                    null, false);
        }
        displayFrom.setVisible(true);
    }
}
