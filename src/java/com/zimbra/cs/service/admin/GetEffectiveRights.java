/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.service.admin;

import java.util.List;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Provisioning.GranteeBy;
import com.zimbra.cs.account.Provisioning.TargetBy;
import com.zimbra.cs.account.accesscontrol.AdminRight;
import com.zimbra.cs.account.accesscontrol.GranteeType;
import com.zimbra.cs.account.accesscontrol.RightCommand;
import com.zimbra.cs.account.accesscontrol.TargetType;
import com.zimbra.cs.account.accesscontrol.Rights.Admin;
import com.zimbra.soap.ZimbraSoapContext;

public class GetEffectiveRights  extends RightDocumentHandler {
    
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        
        String expandAttrs = request.getAttribute(AdminConstants.A_EXPAND_ALL_ATTRS, null);
        boolean expandSetAttrs = false;
        boolean expandGetAttrs = false;
        if (expandAttrs != null) {
            String[] eas = expandAttrs.split(",");
            for (String e : eas) {
                String exp = e.trim();
                if (exp.equals("setAttrs"))
                    expandSetAttrs = true;
                else if (exp.equals("getAttrs"))
                    expandGetAttrs = true;
                else
                    throw ServiceException.INVALID_REQUEST("invalid " + AdminConstants.A_EXPAND_ALL_ATTRS + " value: " + exp, null);
            }
        }
        
        Element eTarget = request.getElement(AdminConstants.E_TARGET);
        String targetType = eTarget.getAttribute(AdminConstants.A_TYPE);
        TargetBy targetBy = null;
        String target = null;
        if (TargetType.fromString(targetType).needsTargetIdentity()) {
            targetBy = TargetBy.fromString(eTarget.getAttribute(AdminConstants.A_BY));
            target = eTarget.getText();
        }
            
        Element eGrantee = request.getOptionalElement(AdminConstants.E_GRANTEE);
        GranteeBy granteeBy;
        String grantee;
        if (eGrantee != null) {
            String granteeType = eGrantee.getAttribute(AdminConstants.A_TYPE, GranteeType.GT_USER.getCode());
            if (GranteeType.fromCode(granteeType) != GranteeType.GT_USER)
                throw ServiceException.INVALID_REQUEST("invalid grantee type " + granteeType, null);
            granteeBy = GranteeBy.fromString(eGrantee.getAttribute(AdminConstants.A_BY));
            grantee = eGrantee.getText();
        } else {
            granteeBy = GranteeBy.id;
            grantee = zsc.getRequestedAccountId();  
        }
        
        if (!grantee.equals(zsc.getAuthtokenAccountId()))
            checkCheckRightRight(zsc, granteeBy, grantee);
        
        RightCommand.EffectiveRights er = RightCommand.getEffectiveRights(Provisioning.getInstance(),
                                                                          targetType, targetBy, target,
                                                                          granteeBy, grantee,
                                                                          expandSetAttrs, expandGetAttrs);
        
        Element resp = zsc.createElement(AdminConstants.GET_EFFECTIVE_RIGHTS_RESPONSE);
        er.toXML_getEffectiveRights(resp);
        return resp;
    }

    @Override
    public void docRights(List<AdminRight> relatedRights, List<String> notes) {
        relatedRights.add(Admin.R_checkRight);
    }
}
