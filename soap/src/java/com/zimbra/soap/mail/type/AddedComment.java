/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2012, 2013, 2014, 2016 Synacor, Inc.
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 * ***** END LICENSE BLOCK *****
 */

package com.zimbra.soap.mail.type;

import com.google.common.base.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import com.zimbra.common.soap.MailConstants;

@XmlAccessorType(XmlAccessType.NONE)
public class AddedComment {

    /**
     * @zm-api-field-tag item-id-of-parent
     * @zm-api-field-description Item ID of parent
     */
    @XmlAttribute(name=MailConstants.A_PARENT_ID /* parentId */, required=true)
    private final String parentId;

    /**
     * @zm-api-field-tag comment-text
     * @zm-api-field-description Comment text
     */
    @XmlAttribute(name=MailConstants.A_TEXT /* text */, required=true)
    private final String text;

    /**
     * no-argument constructor wanted by JAXB
     */
    @SuppressWarnings("unused")
    private AddedComment() {
        this((String) null, (String) null);
    }

    public AddedComment(String parentId, String text) {
        this.parentId = parentId;
        this.text = text;
    }

    public String getParentId() { return parentId; }
    public String getText() { return text; }

    public Objects.ToStringHelper addToStringInfo(Objects.ToStringHelper helper) {
        return helper
            .add("parentId", parentId)
            .add("text", text);
    }

    @Override
    public String toString() {
        return addToStringInfo(Objects.toStringHelper(this)).toString();
    }
}
