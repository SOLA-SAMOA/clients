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
package org.sola.clients.beans.administrative.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.sola.clients.beans.administrative.RelatedBaUnitInfoBean;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * Validates {@link RelatedBaUnitInfoBean} object for extra logic validation (i.e the Related
 * Ba Unit does not reference itself in a circular relationship
 */
public class RelatedBaUnitValidator implements ConstraintValidator<RelatedBaUnitCheck, RelatedBaUnitInfoBean> {

    @Override
    public void initialize(RelatedBaUnitCheck a) {
    }

    /**
     * Verifies that the RelatedBaUnit has not been configured to reference itself.
     *
     * @param relatedBaUnitBean
     * @param constraintContext
     * @return
     */
    @Override
    public boolean isValid(RelatedBaUnitInfoBean relatedBaUnitBean, ConstraintValidatorContext constraintContext) {
        if (relatedBaUnitBean == null) {
            return true;
        }

        boolean result = true;

        constraintContext.disableDefaultConstraintViolation();

        if (relatedBaUnitBean.getBaUnitId() != null && relatedBaUnitBean.getRelatedBaUnitId() != null
                && relatedBaUnitBean.getBaUnitId().equals(relatedBaUnitBean.getRelatedBaUnitId())) {
            result = false;
            constraintContext.buildConstraintViolationWithTemplate(
                    MessageUtility.getLocalizedMessageText(
                    ClientMessage.CHECK_RELATED_BA_UNIT)).addConstraintViolation();
        }

        return result;
    }
}
