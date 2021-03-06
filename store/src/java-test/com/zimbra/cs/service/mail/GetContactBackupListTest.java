/*
 * ***** BEGIN LICENSE BLOCK ***** Zimbra Collaboration Suite Server Copyright
 * (C) 2017 Synacor, Inc.
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>. *****
 * END LICENSE BLOCK *****
 */
package com.zimbra.cs.service.mail;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Maps;
import com.zimbra.common.account.Key;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.common.soap.SoapProtocol;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.mailbox.MailboxTestUtil;
import com.zimbra.cs.service.AuthProvider;
import com.zimbra.cs.service.MockHttpServletRequest;
import com.zimbra.soap.MockSoapEngine;
import com.zimbra.soap.SoapEngine;
import com.zimbra.soap.SoapServlet;
import com.zimbra.soap.ZimbraSoapContext;

import junit.framework.Assert;

public class GetContactBackupListTest {
    @BeforeClass
    public static void init() throws Exception {
        MailboxTestUtil.initServer();
        Provisioning prov = Provisioning.getInstance();
        Map<String, Object> attrs = Maps.newHashMap();
        prov.createAccount("test@zimbra.com", "secret", attrs);
    }

    @Before
    public void setUp() throws Exception {
        MailboxTestUtil.clearData();
    }

    @Test
    public void testGetContactBackupListXML() throws Exception {
        Account acct = Provisioning.getInstance().get(Key.AccountBy.name, "test@zimbra.com");

        Element request = new Element.XMLElement(MailConstants.E_GET_CONTACT_BACKUP_LIST_REQUEST);
        Element response = new GetContactBackupList().handle(request, ServiceTestUtil.getRequestContext(acct));

        String expectedResponse = "<GetContactBackupListResponse xmlns=\"urn:zimbraMail\">\n"
                + "  <backups>\n"
                + "    <backup>file1.tgz</backup>\n"
                + "    <backup>file2.tgz</backup>\n"
                + "    <backup>file3.tgz</backup>\n"
                + "    <backup>file4.tgz</backup>\n"
                + "  </backups>\n"
                + "</GetContactBackupListResponse>";

        Assert.assertEquals("GetContactBackupListResponse is not as expected", expectedResponse, response.prettyPrint());
    }

    @Test
    public void testGetContactBackupListJSON() throws Exception {
        Account acct = Provisioning.getInstance().get(Key.AccountBy.name, "test@zimbra.com");

        Map<String, Object> context = new HashMap<String, Object>();
        context.put(SoapEngine.ZIMBRA_CONTEXT, new ZimbraSoapContext(AuthProvider.getAuthToken(acct), acct.getId(), SoapProtocol.Soap12, SoapProtocol.SoapJS));
        context.put(SoapServlet.SERVLET_REQUEST, new MockHttpServletRequest("test".getBytes("UTF-8"), new URL("http://localhost:7070/service/FooRequest"), ""));
        context.put(SoapEngine.ZIMBRA_ENGINE, new MockSoapEngine(new MailService()));


        Element request = new Element.JSONElement(MailConstants.E_GET_CONTACT_BACKUP_LIST_REQUEST);
        Element response = new GetContactBackupList().handle(request, context);

        String expectedResponse = "{\n"
                + "  \"backups\": [{\n"
                + "      \"backup\": [\n"
                + "        {\n"
                + "          \"_content\": \"file1.tgz\"\n"
                + "        },\n"
                + "        {\n"
                + "          \"_content\": \"file2.tgz\"\n"
                + "        },\n"
                + "        {\n"
                + "          \"_content\": \"file3.tgz\"\n"
                + "        },\n"
                + "        {\n"
                + "          \"_content\": \"file4.tgz\"\n"
                + "        }]\n"
                + "    }],\n"
                + "  \"_jsns\": \"urn:zimbraMail\"\n"
                + "}";

        Assert.assertEquals("GetContactBackupListResponse is not as expected", expectedResponse, response.prettyPrint());
    }
}
