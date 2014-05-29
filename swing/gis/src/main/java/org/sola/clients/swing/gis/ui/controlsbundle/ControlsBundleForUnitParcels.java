/**
 * ******************************************************************************************
 * Copyright (C) 2014 - Food and Agriculture Organization of the United Nations
 * (FAO). All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,this
 * list of conditions and the following disclaimer. 2. Redistributions in binary
 * form must reproduce the above copyright notice,this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. 3. Neither the name of FAO nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT,STRICT LIABILITY,OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
package org.sola.clients.swing.gis.ui.controlsbundle;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.extended.layer.ExtendedLayerGraphics;
import org.geotools.swing.extended.exception.InitializeLayerException;
import org.geotools.swing.extended.util.GeometryUtility;
import org.sola.clients.beans.referencedata.CadastreObjectTypeBean;
import org.sola.clients.beans.referencedata.StatusConstants;
import org.sola.clients.beans.unittitle.UnitParcelBean;
import org.sola.clients.swing.gis.Messaging;
import org.sola.clients.swing.gis.beans.CadastreObjectTargetBean;
import org.sola.clients.swing.gis.data.PojoDataAccess;
import org.sola.clients.swing.gis.data.PojoFeatureSource;
import org.sola.clients.swing.gis.layer.CadastreChangeTargetCadastreObjectLayer;
import org.sola.clients.swing.gis.tool.CadastreChangeSelectCadastreObjectTool;
import org.sola.common.messaging.GisMessage;

/**
 * Map Control Bundle used for display of the underlying parcels of a Unit Title
 * Development.
 */
public class ControlsBundleForUnitParcels extends SolaControlsBundle {

    // Default distance (in metres) to expand the the view by when zooming to the extent of the
    // underlying parcels. 
    private static final double ZOOM_TO_BUFFER = 50;
    private CadastreChangeTargetCadastreObjectLayer targetParcelsLayer = null;
    private CadastreChangeSelectCadastreObjectTool selectTargetCadastreObjectTool;
    protected ExtendedLayerGraphics layerForCadastreObjects;

    /**
     * Creates a controls bundle for display in the property form
     */
    public ControlsBundleForUnitParcels() {
        super();
    }

    /**
     * Adds the TragetCadastreObjectLayer that allows the user to add or remove
     * underlying parcels to the Unit Title Development.
     *
     * @throws InitializeLayerException
     */
    private void addLayers() throws InitializeLayerException {
        this.targetParcelsLayer = new CadastreChangeTargetCadastreObjectLayer();
        this.getMap().addLayer(targetParcelsLayer);
    }

    /**
     * Adds the SelectTargetCadastreObjectTool to add or remove underlying
     * parcels to the Unit Title Development.
     */
    private void addToolsAndCommands() {
        this.selectTargetCadastreObjectTool
                = new CadastreChangeSelectCadastreObjectTool(this.getPojoDataAccess());
        this.selectTargetCadastreObjectTool.setTargetParcelsLayer(targetParcelsLayer);
        this.selectTargetCadastreObjectTool.setCadastreObjectType(CadastreObjectTypeBean.CODE_PARCEL);
        this.getMap().addTool(this.selectTargetCadastreObjectTool, this.getToolbar(), true);
    }

    /**
     * Calculates the extent of the parcels loaded as the underlying parcels for
     * the Unit Development.
     */
    private ReferencedEnvelope getExtentOfParcels() {
        ReferencedEnvelope envelope = null;
        if (this.targetParcelsLayer.getBeanList() != null) {
            for (CadastreObjectTargetBean parcel : this.targetParcelsLayer.getBeanList()) {
                Geometry geom = GeometryUtility.getGeometryFromWkb(parcel.getGeomPolygonCurrent());
                ReferencedEnvelope tmpEnvelope = JTS.toEnvelope(geom);
                if (envelope == null) {
                    envelope = tmpEnvelope;
                } else {
                    envelope.expandToInclude(tmpEnvelope);
                }
            }
        }
        return envelope;
    }

    /**
     * Performs setup actions for the control bundle. Should be called after the
     * initial creation of the Control Bundle.
     *
     * @param pojoDataAccess the interface used to access the data for the map
     * @param readOnly Flag that indicates if the map should be readonly or not.
     * If readOnly, the select target parcel tool is not displayed.
     */
    public void setup(PojoDataAccess pojoDataAccess, boolean readOnly) {
        pojoDataAccess = pojoDataAccess == null ? PojoDataAccess.getInstance() : pojoDataAccess;
        this.Setup(pojoDataAccess);
        try {
            addLayers();
            if (!readOnly) {
                addToolsAndCommands();
            }
        } catch (InitializeLayerException ex) {
            Messaging.getInstance().show(GisMessage.CADASTRE_CHANGE_ERROR_SETUP);
            org.sola.common.logging.LogUtility.log(GisMessage.CADASTRE_CHANGE_ERROR_SETUP, ex);
        }
    }

    /**
     * Adds the list of Unit Parcels representing the underlying parcels of the
     * Unit Plan Development to the map. Any parcels marked for deleteOnApproval
     * are excluded from the display. Also exclude any parcels from the map
     * that are now canceled as these are no longer valid for the Unit Development. 
     *
     * @param parcels
     */
    public void setUnderlyingParcels(List<UnitParcelBean> parcels) {
        for (UnitParcelBean parcel : parcels) {
            if (!parcel.isDeleteOnApproval()
                    && !StatusConstants.HISTORIC.equals(parcel.getStatusCode())) {
                addUnderlyingParcel(parcel.getId(), parcel.getGeomPolygon());
            }
        }
    }

    /**
     * Adds an underlying parcel for the Unit Plan Development to the map view
     *
     * @param id The identifier of the Cadastre Object marked as the underlying
     * parcel for the Unit Development
     * @param geom The geometry of the underlying parcel.
     */
    public void addUnderlyingParcel(String id, byte[] geom) {
        CadastreObjectTargetBean bean = new CadastreObjectTargetBean();
        bean.setCadastreObjectId(id);
        bean.setGeomPolygonCurrent(geom);
        this.targetParcelsLayer.getBeanList().add(bean);
    }

    /**
     * Returns the identifiers of the cadastre objects selected as the
     * underlying parcels in the target parcels layer.
     */
    public List<String> getUnderlyingParcels() {
        List<String> result = new ArrayList<String>();
        List<CadastreObjectTargetBean> targetParcels = this.targetParcelsLayer.getBeanList();
        if (targetParcels != null) {
            for (CadastreObjectTargetBean targetBean : targetParcels) {
                result.add(targetBean.getCadastreObjectId());
            }
        }
        return result;
    }

    /**
     * Zooms the map to the extent of the target/underlying parcels or to the
     * applicationLocation if the underlying parcels are not yet set.
     *
     * @param applicationLocation The application location point. Can be null.
     */
    public void zoomToTargetArea(byte[] applicationLocation) {
        ReferencedEnvelope targetArea = getExtentOfParcels();
        if (targetArea == null && applicationLocation != null) {
            try {
                Geometry applicationLocationGeometry
                        = PojoFeatureSource.getWkbReader().read(applicationLocation);
                targetArea = JTS.toEnvelope(applicationLocationGeometry);
            } catch (ParseException ex) {
                Messaging.getInstance().show(GisMessage.CADASTRE_CHANGE_ERROR_SETUP);
                org.sola.common.logging.LogUtility.log(GisMessage.CADASTRE_CHANGE_ERROR_SETUP, ex);
            }
        }
        if (targetArea != null) {
            targetArea.expandBy(ZOOM_TO_BUFFER);
            this.getMap().setDisplayArea(targetArea);
        }
    }
}
