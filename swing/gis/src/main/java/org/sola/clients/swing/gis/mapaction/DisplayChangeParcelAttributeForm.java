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

import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.swing.extended.Map;
import org.geotools.swing.mapaction.extended.ExtendedAction;
import org.opengis.feature.simple.SimpleFeature;
import org.sola.clients.swing.gis.ui.control.ChangeParcelAttributeForm;
import org.sola.common.SOLAException;
import org.sola.common.WindowUtility;
import org.sola.common.messaging.GisMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * Displays the Change Parcel Attribute form allowing the user to update parcel
 * details. Requires the user to use the Map Find to locate the parcel to edit.
 */
public class DisplayChangeParcelAttributeForm extends ExtendedAction {

    Map mapControl;
    public final static String MAPACTION_NAME = "change-parcel-attribute";
    private ChangeParcelAttributeForm displayForm;

    /**
     * Constructor for the Display Parcel Attribute action.
     *
     * @param mapControl
     */
    public DisplayChangeParcelAttributeForm(Map mapControl) {
        super(mapControl, MAPACTION_NAME, MessageUtility.getLocalizedMessage(
                GisMessage.TOOLTIP_CHANGE_PARCEL_ATTR).getMessage(),
                "resources/globe-pencil.png");
        this.mapControl = mapControl;
    }

    /**
     * Open the edit screen when the user clicks the tool
     */
    @Override
    public void onClick() {
        try {
            // Check there are features selected on the map
            if (mapControl.getSelectedFeatureSource() != null) {
                String parcelId = null;
                SimpleFeatureIterator iterator = mapControl.getSelectedFeatureSource().getFeatures().features();
                try {
                    SimpleFeature feature = iterator.next();
                    parcelId = feature.getID();
                } finally {
                    if (iterator != null) {
                        // Make sure the feature iterator is closed properly. 
                        iterator.close();
                    }
                }

                // Display the edit form with the selected parcel details. 
                if (displayForm == null) {
                    displayForm = new ChangeParcelAttributeForm(parcelId, null, false);
                } else {
                    displayForm.dispose();
                    displayForm = new ChangeParcelAttributeForm(parcelId, null, false);
                }
                // Check that the feature selected is a valid parcel
                if (displayForm.isValidParcel()) {
                    WindowUtility.centerForm(displayForm);
                    displayForm.setVisible(true);
                } else {
                    MessageUtility.displayMessage(GisMessage.INVALID_PARCEL_FEATURE_SELECTED);
                }

            } else {
                MessageUtility.displayMessage(GisMessage.NO_PARCELS_SELECTED);
            }
        } catch (Exception ex) {
            throw new SOLAException(GisMessage.ERROR_MSG_CHANGE_PARCEL_ATTR, ex);
        }
    }
}
