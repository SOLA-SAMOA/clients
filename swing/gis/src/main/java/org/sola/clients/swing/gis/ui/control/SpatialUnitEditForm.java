/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations
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
package org.sola.clients.swing.gis.ui.control;

import com.vividsolutions.jts.geom.Geometry;
import java.util.ArrayList;
import java.util.List;
import org.geotools.geometry.jts.Geometries;
import org.geotools.swing.extended.exception.ReadGeometryException;
import org.geotools.swing.extended.util.GeometryUtility;
import org.sola.clients.swing.common.controls.JTableWithDefaultStyles;
import org.sola.clients.swing.gis.beans.SpatialUnitChangeBean;
import org.sola.clients.swing.gis.beans.SpatialUnitChangeListBean;
import org.sola.common.logging.LogUtility;
import org.sola.common.messaging.GisMessage;
import org.sola.common.messaging.MessageUtility;
import org.sola.webservices.transferobjects.EntityAction;

/**
 * Form allows users to edit details of Spatial Unit Features such as the label.
 */
public class SpatialUnitEditForm extends javax.swing.JDialog {

    /**
     * Creates new form SpatialUnitEditForm
     */
    public SpatialUnitEditForm(SpatialUnitChangeListBean listBean,
            java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.spatialUnitChangeList = listBean;
        initComponents();
    }

    /**
     * Determines the list of selected elements from the table.
     *
     * @return
     */
    private List<SpatialUnitChangeBean> getSelectedList() {
        List<SpatialUnitChangeBean> result = new ArrayList<SpatialUnitChangeBean>();
        for (SpatialUnitChangeBean bean : spatialUnitChangeList.getFilteredSpatialUnitChanges()) {
            if (bean.isSelected()) {
                result.add(bean);
            }
        }
        return result;
    }

    /**
     * Verifies that there is more than one spatial unit selected for the merge
     * and that the selected spatial units are all of the same type. Note that
     * Point units cannot be merged together.
     *
     * @return
     */
    private boolean validateForMerge() {
        boolean result = false;
        List<SpatialUnitChangeBean> selected = getSelectedList();
        if (selected.size() > 1) {
            String levelName = selected.get(0).getLevelName();
            for (SpatialUnitChangeBean bean : selected) {
                if (!levelName.equals(bean.getLevelName())) {
                    // Return false. 
                    return false;
                }
                try {
                    if (!GeometryUtility.isGeomType(bean.getGeom(), Geometries.POLYGON)) {
                        return false;
                    }
                } catch (ReadGeometryException ex) {
                    // Swallow the exception from failing to read the geom.
                    LogUtility.log("Error reading geometry!", ex);
                    return false;
                }
            }
            result = true;
        }
        return result;
    }

    /**
     * Returns the SpatialUnitChangeListBean backing the form.
     */
    public SpatialUnitChangeListBean getSpatialUnitChangeListBean() {
        return this.spatialUnitChangeList;
    }

    private SpatialUnitChangeListBean createSpatialUnitChangeList() {
        if (this.spatialUnitChangeList == null) {
            this.spatialUnitChangeList = new SpatialUnitChangeListBean();
        }
        return this.spatialUnitChangeList;
    }

    /**
     * Clears the selected item from the listBean as well as stopping any cell
     * editing that may be occurring.
     */
    private void clearSelection() {
        // If the table is being edited, stop the editing to accept the value entered by the user. 
        if (this.tblSpatialUnitChanges.getCellEditor() != null) {
            this.tblSpatialUnitChanges.getCellEditor().stopCellEditing();
        }
        // Clear the selected feature
        this.tblSpatialUnitChanges.clearSelection();
    }

    private void removeSelected() {
        if (spatialUnitChangeList.getSelectedSpatialUnitChange() != null) {
            if (this.tblSpatialUnitChanges.getCellEditor() != null) {
                this.tblSpatialUnitChanges.getCellEditor().stopCellEditing();
            }
            spatialUnitChangeList.getSpatialUnitChanges().safeRemove(
                    spatialUnitChangeList.getSelectedSpatialUnitChange(), EntityAction.DELETE);
            // Clear the selected feature
            this.tblSpatialUnitChanges.clearSelection();
        }
    }

    /**
     * Ticket #116 Merges the selected Spatial Units into a single geometry.
     */
    private void mergeSelected() {
        if (spatialUnitChangeList.getSelectedSpatialUnitChange() != null) {
            if (this.tblSpatialUnitChanges.getCellEditor() != null) {
                this.tblSpatialUnitChanges.getCellEditor().stopCellEditing();
            }
        }
        if (validateForMerge()) {
            List<byte[]> geomList = new ArrayList<byte[]>();
            List<SpatialUnitChangeBean> selected = getSelectedList();
            for (SpatialUnitChangeBean bean : selected) {
                geomList.add(bean.getGeom());
                // Mark the geom for deletion on approval
                bean.setDeleteOnApproval(true);
                bean.setSelected(false);
            }
            Geometry geom = GeometryUtility.merge(geomList);
            if (GeometryUtility.isGeomType(geom, Geometries.POLYGON)) {
                SpatialUnitChangeBean newBean = new SpatialUnitChangeBean();
                newBean.generateId();
                newBean.setGeom(GeometryUtility.getWkbFromGeometry(geom));
                newBean.setLevelName(selected.get(0).getLevelName());
                newBean.setNewFeature(true);
                newBean.setMergedGeom(geom);
                spatialUnitChangeList.getSpatialUnitChanges().addAsNew(newBean);
                // Clear the selected feature
                this.tblSpatialUnitChanges.clearSelection();
            } else {
                MessageUtility.displayMessage(GisMessage.SPATIAL_UNIT_INVALID_MERGE_RESULT);
            }
        } else {
            MessageUtility.displayMessage(GisMessage.SPATIAL_UNIT_INVALID_MERGE_SELECTION);
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        spatialUnitChangeList = createSpatialUnitChangeList();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblSpatialUnitChanges = new JTableWithDefaultStyles();
        jToolBar1 = new javax.swing.JToolBar();
        btnClose = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        btnMerge = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/gis/ui/control/Bundle"); // NOI18N
        setTitle(bundle.getString("SpatialUnitEditForm.title")); // NOI18N
        setLocationByPlatform(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        tblSpatialUnitChanges.setColumnSelectionAllowed(true);
        tblSpatialUnitChanges.getTableHeader().setReorderingAllowed(false);

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${filteredSpatialUnitChanges}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, spatialUnitChangeList, eLProperty, tblSpatialUnitChanges);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${selected}"));
        columnBinding.setColumnName("Selected");
        columnBinding.setColumnClass(Boolean.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${label}"));
        columnBinding.setColumnName("Label");
        columnBinding.setColumnClass(String.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${levelName}"));
        columnBinding.setColumnName("Level Name");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${deleteOnApproval}"));
        columnBinding.setColumnName("Delete On Approval");
        columnBinding.setColumnClass(Boolean.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${newFeature}"));
        columnBinding.setColumnName("New Feature");
        columnBinding.setColumnClass(Boolean.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, spatialUnitChangeList, org.jdesktop.beansbinding.ELProperty.create("${selectedSpatialUnitChange}"), tblSpatialUnitChanges, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        jScrollPane2.setViewportView(tblSpatialUnitChanges);
        tblSpatialUnitChanges.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (tblSpatialUnitChanges.getColumnModel().getColumnCount() > 0) {
            tblSpatialUnitChanges.getColumnModel().getColumn(0).setPreferredWidth(50);
            tblSpatialUnitChanges.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("SpatialUnitEditForm.tblSpatialUnitChanges.columnModel.title4_1")); // NOI18N
            tblSpatialUnitChanges.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("SpatialUnitEditForm.tblSpatialUnitChanges.columnModel.title0")); // NOI18N
            tblSpatialUnitChanges.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("SpatialUnitEditForm.tblSpatialUnitChanges.columnModel.title1")); // NOI18N
            tblSpatialUnitChanges.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("SpatialUnitEditForm.tblSpatialUnitChanges.columnModel.title2")); // NOI18N
            tblSpatialUnitChanges.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("SpatialUnitEditForm.tblSpatialUnitChanges.columnModel.title3_2")); // NOI18N
        }

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/sola/clients/swing/gis/tool/resources/save.png"))); // NOI18N
        btnClose.setText(bundle.getString("SpatialUnitEditForm.btnClose.text")); // NOI18N
        btnClose.setBorderPainted(false);
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        jToolBar1.add(btnClose);

        btnClear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/sola/clients/swing/gis/tool/resources/selection.png"))); // NOI18N
        btnClear.setText(bundle.getString("SpatialUnitEditForm.btnClear.text")); // NOI18N
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });
        jToolBar1.add(btnClear);

        btnMerge.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/sola/clients/swing/gis/tool/resources/layers-group.png"))); // NOI18N
        btnMerge.setText(bundle.getString("SpatialUnitEditForm.btnMerge.text")); // NOI18N
        btnMerge.setFocusable(false);
        btnMerge.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnMerge.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMergeActionPerformed(evt);
            }
        });
        jToolBar1.add(btnMerge);

        btnRemove.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/sola/clients/swing/gis/tool/resources/remove.png"))); // NOI18N
        btnRemove.setText(bundle.getString("SpatialUnitEditForm.btnRemove.text")); // NOI18N
        btnRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveActionPerformed(evt);
            }
        });
        jToolBar1.add(btnRemove);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE)
                .addContainerGap())
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        clearSelection();
        this.setVisible(false);
    }//GEN-LAST:event_btnCloseActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        clearSelection();
    }//GEN-LAST:event_btnClearActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        clearSelection();
    }//GEN-LAST:event_formWindowClosing

    private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveActionPerformed
        removeSelected();
    }//GEN-LAST:event_btnRemoveActionPerformed

    private void btnMergeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMergeActionPerformed
        mergeSelected();
    }//GEN-LAST:event_btnMergeActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnMerge;
    private javax.swing.JButton btnRemove;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JToolBar jToolBar1;
    private org.sola.clients.swing.gis.beans.SpatialUnitChangeListBean spatialUnitChangeList;
    private javax.swing.JTable tblSpatialUnitChanges;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
