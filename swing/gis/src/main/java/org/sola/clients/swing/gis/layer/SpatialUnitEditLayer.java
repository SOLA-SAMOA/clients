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
package org.sola.clients.swing.gis.layer;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.geotools.feature.CollectionEvent;
import org.geotools.feature.CollectionListener;
import org.geotools.geometry.jts.Geometries;
import org.geotools.map.extended.layer.ExtendedLayerEditor;
import org.geotools.swing.extended.exception.InitializeLayerException;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.observablecollections.ObservableListListener;
import org.opengis.feature.simple.SimpleFeature;
import org.sola.clients.swing.gis.Messaging;
import org.sola.clients.swing.gis.beans.SpatialUnitChangeBean;
import org.sola.clients.swing.gis.beans.SpatialUnitChangeListBean;
import org.sola.common.messaging.GisMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * Layer that supports the creation and editing of spatial unit features such as Hydro polygons and
 * road centerlines. These types of features differ from cadastre objects in that there are less
 * rigorous rules around their spatial positioning e.g. Hydro parcels can overlap and do not need to
 * share common coordinates with adjoining hydro parcels. Note that the
 * {@linkplain org.sola.clients.swing.gis.tool.EditSpatialUnitTool EditSpatialUnitTool} implements
 * node snapping so that it is possible for adjoining parcels to share coordinates if necessary.
 * <p>This layer is capable of handling multiple types of spatial unit features including polygon,
 * linestring and point features</p>
 */
public class SpatialUnitEditLayer extends ExtendedLayerEditor {

    private static final String LAYER_FIELD_LEVEL = "levelName";
    private static final String LAYER_FIELD_DELETE = "deleteOnApproval";
    private static final String LAYER_FIELD_NEW_FEATURE = "newFeature";
    private static final String LAYER_FIELD_LABEL = "label";
    /*
     * Defines the additional attributes for the layer. The format of this string is <field
     * name>:<data type>. Note that empty string (i.e. "") can be used to indicate the type as
     * string, but specifying the data type is easier to maintain.
     */
    private static final String LAYER_ATTRIBUTE_DEFINITION =
            String.format("%s:String,%s:String,%s:Boolean,%s:Boolean",
            LAYER_FIELD_LEVEL, LAYER_FIELD_LABEL, LAYER_FIELD_NEW_FEATURE, LAYER_FIELD_DELETE);
    private static final String LAYER_NAME = "spatial_unit_edit";
    private static final String LAYER_STYLE_RESOURCE = "parcel_target.xml";
    private static final String HYDRO_LAYER_NAME = "hydro";
    private static final String HYDRO_LEVEL_NAME = "Hydro Features";
    private static final String ROAD_CL_LAYER_NAME = "road_cl";
    private static final String ROAD_CL_LEVEL_NAME = "Roads";
    private SpatialUnitChangeListBean listBean = new SpatialUnitChangeListBean();
    private Map<String, String> layerLevelMapping = new HashMap<String, String>();

    /**
     * Configures the layer name, style and additional attributes.
     *
     * @throws InitializeLayerException
     */
    public SpatialUnitEditLayer() throws InitializeLayerException {
        super(LAYER_NAME, Geometries.GEOMETRY, LAYER_STYLE_RESOURCE, LAYER_ATTRIBUTE_DEFINITION);

        // Configure the title for the layer using localized text
        setTitle(Messaging.getInstance().getMessageText(GisMessage.SPATIAL_UNIT_EDIT_LAYER_TITLE));

        //Setup the mappings from the map layer names to the cadastre.level names
        layerLevelMapping.put(HYDRO_LAYER_NAME, HYDRO_LEVEL_NAME);
        layerLevelMapping.put(ROAD_CL_LAYER_NAME, ROAD_CL_LEVEL_NAME);

        // Add a listener to process any changes made directly to the layer features in the map. 
        this.getFeatureCollection().addListener(new CollectionListener() {

            @Override
            public void collectionChanged(CollectionEvent ce) {
                featureCollectionChanged(ce);
            }
        });

        // Configures a property change listener for the selected property of the listBean.
        this.listBean.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(
                        SpatialUnitChangeListBean.SELECTED_SPATIAL_UNIT_CHANGE_PROPERTY)) {
                    highlightSelectedBean((SpatialUnitChangeBean) evt.getNewValue());
                }
            }
        });

        // Configures a observerable list listener to remove any features that get removed 
        //from the listBean 
        this.listBean.getSpatialUnitChanges().addObservableListListener(new ObservableListListener() {

            @Override
            public void listElementsAdded(ObservableList ol, int i, int i1) {
            }

            @Override
            public void listElementsRemoved(ObservableList ol, int i, List list) {
                removeFeatures(list);
            }

            @Override
            public void listElementReplaced(ObservableList ol, int i, Object o) {
            }

            @Override
            public void listElementPropertyChanged(ObservableList ol, int i) {
            }
        });
    }

    /**
     * Highlights the geometry of the bean on the map to help users identify which feature they are
     * editing.
     *
     * @param bean The bean to highlight
     */
    private void highlightSelectedBean(SpatialUnitChangeBean bean) {
        this.getMapControl().clearSelectedFeatures();
        if (bean != null && bean.getGeom() != null) {
            String fid = bean.getSpatialUnitId() == null ? bean.getId() : bean.getSpatialUnitId();
            SimpleFeature feature = this.getFeatureCollection().getFeature(fid);
            if (feature != null) {
                this.getMapControl().selectFeature(fid, (Geometry) feature.getDefaultGeometry());
            }
        }
        getMapControl().refresh();
    }

    /**
     * Removes a list of features from the map. The list is expected to be a list of
     * SpatialUnitChangeBean.
     */
    private void removeFeatures(List list) {
        if (list != null && !list.isEmpty()) {
            for (Object removedItem : list) {
                SpatialUnitChangeBean removedBean = (SpatialUnitChangeBean) removedItem;
                String fid = removedBean.getSpatialUnitId() == null ? removedBean.getId()
                        : removedBean.getSpatialUnitId();
                this.removeFeature(fid, false);
            }
        }
    }

    /**
     * Returns the list bean managed by the layer.
     */
    public SpatialUnitChangeListBean getSpatialUnitChangeListBean() {
        return listBean;
    }

    /**
     * Process any events on the feature collection to ensure the spatialUnitChanges list stays in
     * sync.
     *
     * @param ev
     */
    private void featureCollectionChanged(CollectionEvent ev) {
        if (ev.getFeatures() == null || ev.getEventType() == CollectionEvent.FEATURES_ADDED) {
            return;
        }
        for (SimpleFeature feature : ev.getFeatures()) {
            SpatialUnitChangeBean bean = getBean(feature.getID());
            if (bean != null) {
                if (ev.getEventType() == CollectionEvent.FEATURES_CHANGED) {
                    // Feature was changed, make sure the bean has up to date geometry
                    bean.setGeom(wkbWriter.write((Geometry) feature.getDefaultGeometry()));
                }
                if (ev.getEventType() == CollectionEvent.FEATURES_REMOVED) {
                    // Feature was removed, so remove the matching bean from the collection
                    listBean.getSpatialUnitChanges().remove(bean);
                }
            }
        }
    }

    /**
     * Uses the feature Id to search for a matching bean in the spatialUnitChanges collection.
     *
     * @param fid Id of the feature
     * @return The bean if a match is found otherwise null.
     */
    private SpatialUnitChangeBean getBean(String fid) {
        SpatialUnitChangeBean bean = new SpatialUnitChangeBean();
        bean.setId(fid);
        int index = listBean.getSpatialUnitChanges().indexOf(bean);
        if (index > -1) {
            return listBean.getSpatialUnitChanges().get(index);
        } else {
            return null;
        }
    }

    /**
     * Creates a new SpatialUnitChange bean using the feature details. The feature must support the
     * SpatialUnitEditLayer extension attributes.
     *
     * @param feature A feature with the SpatialUnitEditLayer extension attributes.
     * @return A bean generated from the feature details.
     */
    private SpatialUnitChangeBean featureToBean(SimpleFeature feature) {
        SpatialUnitChangeBean bean = new SpatialUnitChangeBean();
        bean.setId(feature.getID());

        // Obtain the attributes from the feature
        String levelName = (String) feature.getAttribute(LAYER_FIELD_LEVEL);
        String label = (String) feature.getAttribute(LAYER_FIELD_LABEL);

        Boolean deleteOnApproval = (Boolean) feature.getAttribute(LAYER_FIELD_DELETE);
        deleteOnApproval = deleteOnApproval == null ? Boolean.FALSE : deleteOnApproval;

        Boolean newFeature = (Boolean) feature.getAttribute(LAYER_FIELD_NEW_FEATURE);
        newFeature = newFeature == null ? Boolean.FALSE : newFeature;

        bean.setLevelName(levelName);
        bean.setDeleteOnApproval(deleteOnApproval.booleanValue());
        bean.setLabel(label);
        if (!newFeature) {
            // This is an existing spatial unit featutre so setup the id values. 
            bean.setSpatialUnitId(bean.getId());
            bean.setId(null);
        }
        bean.setGeom(wkbWriter.write((Geometry) feature.getDefaultGeometry()));
        return bean;
    }

    /**
     * Adds the list of SpatialUnitChangeBeans as features into the layer. This will replace all
     * features currently displayed in the layer. To add a bean into the layer without replacing the
     * current features, use
     * {@linkplain #addFeature(org.sola.clients.swing.gis.beans.SpatialUnitChangeBean) addFeature}
     *
     * @param spatialUnitChanges The list of spatial unit change beans to display in the layer.
     */
    public void setSpatialUnitChangeList(List<SpatialUnitChangeBean> spatialUnitChanges) {
        // Clear the features on the map and from the bean list. 
        // Note that addFeature will add the bean back into the bean list. 
        listBean.getSpatialUnitChanges().clear();
        removeFeatures(true);
        if (spatialUnitChanges != null && !spatialUnitChanges.isEmpty()) {
            for (SpatialUnitChangeBean bean : spatialUnitChanges) {
                this.listBean.getSpatialUnitChanges().add(bean);
                boolean isNew = bean.getSpatialUnitId() == null;
                String fid = isNew ? bean.getId() : bean.getSpatialUnitId();
                addFeature(fid, bean.getGeom(), bean.getLevelName(), bean.getLabel(),
                        bean.isDeleteOnApproval(), isNew);
            }
            getMapControl().refresh();
        }
    }

    /*
     * Returns the list of SpatialUnitChangeBean representing the features in the layer.
     */
    public List<SpatialUnitChangeBean> getSpatialUnitChangeList() {
        List<SpatialUnitChangeBean> result = new ArrayList<SpatialUnitChangeBean>();
        result.addAll(listBean.getSpatialUnitChanges());
        return result;
    }

    /**
     * Maps a layer name to the cadastre level it relates to. Used for setting the level name for
     * spatial unit changes.
     *
     * @param layerName The layer name to map to a level name
     * @return The level name or null
     */
    public String mapLayerToLevel(String layerName) {
        String result = null;
        if (layerLevelMapping.containsKey(layerName)) {
            result = layerLevelMapping.get(layerName);
        }
        return result;
    }

    /**
     * Adds a feature to the SpatialUnitEditLayer. This method does not force a redraw of the map
     * after the feature has been added.
     *
     * @param fid The feature Id
     * @param geom The geometry for the feature in Well Known Binary (WKB) format.
     * @param levelName The level name the feature relates to. Can be mapped from the layer name
     * using {@linkplain #mapLayerToLevel(java.lang.String) mapLayerToLevel}.
     * @param label The label for the new feature
     * @param deleteOnApproval Flag to indicate if the feature should be deleted when the associated
     * application transaction is approved.
     * @param newFeature Flag to indicate if the feature is new or represents an existing Spatial
     * Unit feature.
     * @return The SimpleFeature added to the map.
     * @see #addFeature(java.lang.String, com.vividsolutions.jts.geom.Geometry, java.lang.String,
     * java.lang.String, boolean, boolean) addFeature
     */
    public SimpleFeature addFeature(String fid, byte[] geom, String levelName, String label,
            boolean deleteOnApproval, boolean newFeature) {
        SimpleFeature result = null;
        try {
            Geometry geometry = wkbReader.read(geom);
            result = addFeature(fid, geometry, levelName, label, deleteOnApproval, newFeature);
        } catch (ParseException ex) {
            MessageUtility.displayMessage(GisMessage.SPATIAL_UNIT_ADD_FEATURE_ERROR,
                    new String[]{label == null ? "" : label, getTitle()});
            org.sola.common.logging.LogUtility.log(
                    GisMessage.SPATIAL_UNIT_ADD_FEATURE_ERROR, ex);
        }
        return result;
    }

    /**
     * Adds a feature to the SpatialUnitEditLayer. This method does not force a redraw of the map
     * after the feature has been added.
     *
     * @param fid The feature Id
     * @param geom The geometry for the feature
     * @param levelName The level name the feature relates to. Can be mapped from the layer name
     * using {@linkplain #mapLayerToLevel(java.lang.String) mapLayerToLevel}.
     * @param label The label for the new feature
     * @param deleteOnApproval Flag to indicate if the feature should be deleted when the associated
     * application transaction is approved.
     * @param newFeature Flag to indicate if the feature is new or represents an existing Spatial
     * Unit feature.
     * @return The SimpleFeature added to the map.
     * @see #addFeature(java.lang.String, byte[], java.lang.String, java.lang.String, boolean,
     * boolean) addFeature
     */
    public SimpleFeature addFeature(String fid, Geometry geom, String levelName, String label,
            boolean deleteOnApproval, boolean newFeature) {
        SimpleFeature result = null;
        if (!layerLevelMapping.containsValue(levelName == null ? "" : levelName)) {
            MessageUtility.displayMessage(GisMessage.SPATIAL_UNIT_LEVEL_NAME_ERROR,
                    new String[]{label == null ? "" : label, getTitle()});
        } else {
            HashMap attributes = new HashMap<String, Object>();
            attributes.put(LAYER_FIELD_LEVEL, levelName);
            attributes.put(LAYER_FIELD_LABEL, label);
            attributes.put(LAYER_FIELD_DELETE, deleteOnApproval);
            attributes.put(LAYER_FIELD_NEW_FEATURE, newFeature);
            result = super.addFeature(fid, geom, attributes, false);
            if (getBean(fid) == null) {
                this.listBean.getSpatialUnitChanges().add(featureToBean(result));
            }
        }
        return result;
    }

    /**
     * Not implemented. Use {@linkplain #addFeature(java.lang.String, byte[], java.lang.String,
     * java.lang.String, boolean, boolean) addFeature} instead.
     */
    @Override
    public SimpleFeature addFeature(String fid,
            Geometry geom, HashMap<String, Object> fieldsWithValues, boolean redraw) {
        return null;
    }

    /**
     * Not implemented. Use {@linkplain #addFeature(java.lang.String, byte[], java.lang.String,
     * java.lang.String, boolean, boolean) addFeature} instead.
     */
    @Override
    public SimpleFeature addFeature(String fid,
            byte[] geom, HashMap<String, Object> fieldsWithValues, boolean redraw) {
        return null;
    }

    /**
     * Overrides {@linkplain ExtendedLayerEditor#removeFeatures()
     * ExtendedLayerEditor.removeFeatures} to ensure the all SpatialUnitChangeBeans are removed from
     * the spatialUnitChange collection.
     */
    @Override
    public void removeFeatures(boolean refreshMap) {
        super.removeFeatures(refreshMap);
        this.listBean.getSpatialUnitChanges().clear();
    }
}
