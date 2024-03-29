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
package org.geotools.swing.tool.extended.ui;

import java.awt.Component;
import java.awt.Dialog;
import javax.swing.JDialog;

/**
 * Singletone class that provides utilities related with user interface.
 *
 * @author Elton Manoku
 */
public class UiUtil {

    private static UiUtil singleInstance = new UiUtil();
    private JDialog modalDialog = null;

    /**
     * Gets the singletone instance
     */
    public static UiUtil getInstance() {
        return singleInstance;
    }

    /**
     * Gets a modalform showing the component passed as parameter. Also a title can be provided.
     */
    public JDialog getDialog(String title, Component componentToShow) {
        JDialog dialog = new JDialog();
        dialog.setAlwaysOnTop(true);
        dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setPreferredSize(componentToShow.getPreferredSize());
        dialog.getContentPane().add(componentToShow);
        dialog.setTitle(title);
        dialog.pack();
        modalDialog = dialog;
        return dialog;
    }

    /**
     * UiUtil creates a modal dialog, so it should be save to close it 
     */
    public void closeDialog() {
        if (modalDialog != null) {
            modalDialog.setVisible(false);
        }

    }
}
