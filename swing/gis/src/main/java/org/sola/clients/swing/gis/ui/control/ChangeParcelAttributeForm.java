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
package org.sola.clients.swing.gis.ui.control;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.swing.common.tasks.SolaTask;
import org.sola.clients.swing.common.tasks.TaskManager;
import org.sola.clients.swing.gis.beans.CadastreObjectBean;
import org.sola.clients.swing.ui.renderers.FormattersFactory;
import org.sola.common.WindowUtility;
import org.sola.common.messaging.GisMessage;
import org.sola.common.messaging.MessageUtility;
import org.sola.services.boundary.wsclients.WSManager;

/**
 * Form allows user to edit specific details of a parcel such as name parts and
 * area as well as force a parcel to become historic.
 *
 * @author soladev
 */
public class ChangeParcelAttributeForm extends javax.swing.JDialog {

    private String parcelId = null;
    private static final String INVALID_PARCEL_ID = "0";

    /**
     * Creates new form ChangeParcelAttributeForm
     */
    public ChangeParcelAttributeForm(String parcelId, java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.parcelId = parcelId;
        initComponents();
        customizeForm();

    }

    private CadastreObjectBean createCadastreObject() {
        if (cadObjBean == null) {
            loadParcel(this.parcelId);
        }
        return cadObjBean;
    }

    private void customizeForm() {
        // Set the Title for the form based on the selecetec parcel
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/gis/ui/control/Bundle"); // NOI18N
        setTitle(bundle.getString("ChangeParcelAttributeForm.title"));
        if (isValidParcel()) {
            this.setTitle(String.format("%s - %s", this.getTitle(), cadObjBean.toString()));
        } else {
            disableForm();
        }
    }

    /**
     * Execute the web service method to update the parcel attributes.
     *
     * @param parcelId
     * @param namePart1
     * @param namePart2
     * @param officialArea
     * @param makeHistoric
     */
    private void changeParceAttributes(final String parcelId, final String namePart1,
            final String namePart2, final BigDecimal officialArea, final boolean makeHistoric) {

        final boolean[] result = {false};
        SolaTask<Void, Void> t = new SolaTask<Void, Void>() {
            @Override
            public Void doTask() {
                setMessage(MessageUtility.getLocalizedMessageText(GisMessage.PROGRESS_MSG_CHANGE_PARCEL_ATTR));
                WSManager.getInstance().getCadastreService().changeParcelAttribute(parcelId,
                        namePart1, namePart2, officialArea, makeHistoric);
                result[0] = true;
                return null;
            }

            @Override
            protected void taskDone() {
                if (result[0]) {
                    MessageUtility.displayMessage(GisMessage.RESULT_MSG_CHANGE_PARCEL_ATTR);
                }
            }
        };
        TaskManager.getInstance().runTask(t);
    }

    /**
     * Load parcel details from the database.
     *
     * @param parcelId
     */
    private void loadParcel(String parcelId) {
        this.parcelId = parcelId;
        List<CadastreObjectBean> cadastreObjectList = new ArrayList<CadastreObjectBean>();
        if (parcelId != null) {
            List<String> ids = new ArrayList<String>();
            ids.add(parcelId);
            TypeConverters.TransferObjectListToBeanList(WSManager.getInstance().getCadastreService().getCadastreObjects(ids),
                    CadastreObjectBean.class, (List) cadastreObjectList);
        }
        if (cadastreObjectList.size() > 0) {
            cadObjBean = cadastreObjectList.get(0);
        } else {
            // No parcel found so indicate the parcel is invalid. 
            cadObjBean = new CadastreObjectBean();
            cadObjBean.setId(INVALID_PARCEL_ID);
        }
    }

    private void disableForm() {
        btnChangeArea.setEnabled(false);
        btnChangeName.setEnabled(false);
        btnMakeHistoric.setEnabled(false);
        txtLotNumber.setEditable(false);
        txtLotNumber.setEnabled(false);
        txtPlanNumber.setEditable(false);
        txtPlanNumber.setEnabled(false);
        txtArea.setEditable(false);
        txtArea.setEnabled(false);
        txtAreaImperial.setEnabled(false);
        txtAreaImperial.setEditable(false);
    }

    /**
     * Indicates if the parcel loaded is a valid parcel or not
     *
     * @return true if valid, false otherwise.
     */
    public boolean isValidParcel() {
        return !INVALID_PARCEL_ID.equals(cadObjBean.getId());
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

        cadObjBean = createCadastreObject();
        jToolBar1 = new javax.swing.JToolBar();
        btnClose = new javax.swing.JButton();
        btnChangeName = new javax.swing.JButton();
        btnChangeArea = new javax.swing.JButton();
        btnMakeHistoric = new javax.swing.JButton();
        pnlMain = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        lblLotNum = new javax.swing.JLabel();
        txtLotNumber = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        lblPlanNum = new javax.swing.JLabel();
        txtPlanNumber = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtArea = new javax.swing.JFormattedTextField();
        jPanel5 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtAreaImperial = new javax.swing.JFormattedTextField();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/gis/ui/control/Bundle"); // NOI18N
        setTitle(bundle.getString("ChangeParcelAttributeForm.title")); // NOI18N
        setResizable(false);

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/sola/clients/swing/gis/mapaction/resources/cancel.png"))); // NOI18N
        btnClose.setText(bundle.getString("ChangeParcelAttributeForm.btnClose.text")); // NOI18N
        btnClose.setFocusable(false);
        btnClose.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        btnClose.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        jToolBar1.add(btnClose);

        btnChangeName.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/sola/clients/swing/gis/tool/resources/save.png"))); // NOI18N
        btnChangeName.setText(bundle.getString("ChangeParcelAttributeForm.btnChangeName.text")); // NOI18N
        btnChangeName.setFocusable(false);
        btnChangeName.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnChangeName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChangeNameActionPerformed(evt);
            }
        });
        jToolBar1.add(btnChangeName);

        btnChangeArea.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/sola/clients/swing/gis/tool/resources/save.png"))); // NOI18N
        btnChangeArea.setText(bundle.getString("ChangeParcelAttributeForm.btnChangeArea.text")); // NOI18N
        btnChangeArea.setFocusable(false);
        btnChangeArea.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnChangeArea.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChangeAreaActionPerformed(evt);
            }
        });
        jToolBar1.add(btnChangeArea);

        btnMakeHistoric.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/sola/clients/swing/gis/tool/resources/save.png"))); // NOI18N
        btnMakeHistoric.setText(bundle.getString("ChangeParcelAttributeForm.btnMakeHistoric.text")); // NOI18N
        btnMakeHistoric.setFocusable(false);
        btnMakeHistoric.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnMakeHistoric.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMakeHistoricActionPerformed(evt);
            }
        });
        jToolBar1.add(btnMakeHistoric);

        pnlMain.setLayout(new java.awt.GridLayout(2, 2, 15, 0));

        lblLotNum.setText(bundle.getString("ChangeParcelAttributeForm.lblLotNum.text")); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, cadObjBean, org.jdesktop.beansbinding.ELProperty.create("${nameFirstpart}"), txtLotNumber, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblLotNum, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(txtLotNumber)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(lblLotNum)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtLotNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pnlMain.add(jPanel2);

        lblPlanNum.setText(bundle.getString("ChangeParcelAttributeForm.lblPlanNum.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, cadObjBean, org.jdesktop.beansbinding.ELProperty.create("${nameLastpart}"), txtPlanNumber, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblPlanNum, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(txtPlanNumber)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(lblPlanNum)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPlanNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pnlMain.add(jPanel3);

        jLabel1.setText(bundle.getString("ChangeParcelAttributeForm.jLabel1.text")); // NOI18N

        txtArea.setFormatterFactory(FormattersFactory.getInstance().getMetricAreaFormatterFactory());
        txtArea.setText(bundle.getString("ChangeParcelAttributeForm.txtArea.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, cadObjBean, org.jdesktop.beansbinding.ELProperty.create("${officialArea}"), txtArea, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(txtArea, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtArea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pnlMain.add(jPanel4);

        jLabel2.setText(bundle.getString("ChangeParcelAttributeForm.jLabel2.text")); // NOI18N

        txtAreaImperial.setFormatterFactory(FormattersFactory.getInstance().getImperialFormatterFactory());
        txtAreaImperial.setText(bundle.getString("ChangeParcelAttributeForm.txtAreaImperial.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, cadObjBean, org.jdesktop.beansbinding.ELProperty.create("${officialArea}"), txtAreaImperial, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(txtAreaImperial)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAreaImperial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pnlMain.add(jPanel5);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pnlMain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlMain, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_btnCloseActionPerformed

    private void btnChangeNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChangeNameActionPerformed
        changeParceAttributes(this.parcelId, cadObjBean.getNameFirstpart(),
                cadObjBean.getNameLastpart(), null, false);
    }//GEN-LAST:event_btnChangeNameActionPerformed

    private void btnChangeAreaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChangeAreaActionPerformed
        // Force the text fields to commit any outstanding edits so the correct
        // value is used when updating the area. 
        WindowUtility.commitChanges(pnlMain);
        txtArea.transferFocus();
        changeParceAttributes(this.parcelId, null, null,
                cadObjBean.getOfficialArea(), false);
    }//GEN-LAST:event_btnChangeAreaActionPerformed

    private void btnMakeHistoricActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMakeHistoricActionPerformed
        // Check that the user is aware that this action will prevent further edits to the parcel
        if (MessageUtility.displayMessage(GisMessage.CONFIRM_MAKE_HISTORIC) == MessageUtility.BUTTON_ONE) {
            changeParceAttributes(this.parcelId, null, null, null, true);
        }
    }//GEN-LAST:event_btnMakeHistoricActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnChangeArea;
    private javax.swing.JButton btnChangeName;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnMakeHistoric;
    private org.sola.clients.swing.gis.beans.CadastreObjectBean cadObjBean;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblLotNum;
    private javax.swing.JLabel lblPlanNum;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JFormattedTextField txtArea;
    private javax.swing.JFormattedTextField txtAreaImperial;
    private javax.swing.JTextField txtLotNumber;
    private javax.swing.JTextField txtPlanNumber;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
