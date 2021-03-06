/*
 * (C) Copyright 2010 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * Contributors:
 * Nuxeo - initial API and implementation
 */

package org.nuxeo.ecm.platform.types.localconfiguration;

import static org.nuxeo.ecm.platform.types.localconfiguration.UITypesConfigurationConstants.UI_TYPES_CONFIGURATION_ALLOWED_TYPES_PROPERTY;
import static org.nuxeo.ecm.platform.types.localconfiguration.UITypesConfigurationConstants.UI_TYPES_CONFIGURATION_DEFAULT_TYPE;
import static org.nuxeo.ecm.platform.types.localconfiguration.UITypesConfigurationConstants.UI_TYPES_CONFIGURATION_DENIED_TYPES_PROPERTY;
import static org.nuxeo.ecm.platform.types.localconfiguration.UITypesConfigurationConstants.UI_TYPES_CONFIGURATION_DENY_ALL_TYPES_PROPERTY;
import static org.nuxeo.ecm.platform.types.localconfiguration.UITypesConfigurationConstants.UI_TYPES_DEFAULT_TYPE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.localconfiguration.AbstractLocalConfiguration;
import org.nuxeo.ecm.platform.types.SubType;

/**
 * Default implementation of {@code UITypesConfiguration}.
 *
 * @author <a href="mailto:troger@nuxeo.com">Thomas Roger</a>
 */
public class UITypesConfigurationAdapter extends
        AbstractLocalConfiguration<UITypesConfiguration> implements
        UITypesConfiguration {

    private static final Log log = LogFactory.getLog(UITypesConfigurationAdapter.class);

    protected DocumentRef documentRef;

    protected List<String> allowedTypes;

    protected List<String> deniedTypes;

    protected boolean denyAllTypes;

    protected boolean canMerge = true;

    protected String defaultType;

    public UITypesConfigurationAdapter(DocumentModel doc) {
        documentRef = doc.getRef();
        allowedTypes = getTypesList(doc,
                UI_TYPES_CONFIGURATION_ALLOWED_TYPES_PROPERTY);
        deniedTypes = getTypesList(doc,
                UI_TYPES_CONFIGURATION_DENIED_TYPES_PROPERTY);
        defaultType = getDefaultType(doc);

        denyAllTypes = getDenyAllTypesProperty(doc);
        if (denyAllTypes) {
            canMerge = false;
        }
    }

    protected List<String> getTypesList(DocumentModel doc, String property) {
        String[] types;
        try {
            types = (String[]) doc.getPropertyValue(property);
        } catch (ClientException e) {
            return Collections.emptyList();
        }
        if (types == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(Arrays.asList(types));
    }

    protected boolean getDenyAllTypesProperty(DocumentModel doc) {
        try {
            Boolean value = (Boolean) doc.getPropertyValue(UI_TYPES_CONFIGURATION_DENY_ALL_TYPES_PROPERTY);
            return Boolean.TRUE.equals(value);
        } catch (ClientException e) {
            return false;
        }
    }

    protected String getDefaultType(DocumentModel doc) {
        String value = UI_TYPES_DEFAULT_TYPE;
        try {
            value = (String) doc.getPropertyValue(UI_TYPES_CONFIGURATION_DEFAULT_TYPE);
        } catch (ClientException e) {
            log.debug("can't get default type for:" + doc.getPathAsString(), e);
        }
        return value;
    }

    @Override
    public List<String> getAllowedTypes() {
        return allowedTypes;
    }

    @Override
    public List<String> getDeniedTypes() {
        return deniedTypes;
    }

    @Override
    public boolean denyAllTypes() {
        return denyAllTypes;
    }

    @Override
    public DocumentRef getDocumentRef() {
        return documentRef;
    }

    @Override
    public boolean canMerge() {
        return canMerge;
    }

    @Override
    public UITypesConfiguration merge(UITypesConfiguration other) {
        if (other == null) {
            return this;
        }

        // set the documentRef to the other UITypesConfiguration to continue
        // merging, if needed
        documentRef = other.getDocumentRef();

        List<String> deniedTypes = new ArrayList<String>(this.deniedTypes);
        deniedTypes.addAll(other.getDeniedTypes());
        this.deniedTypes = Collections.unmodifiableList(deniedTypes);

        denyAllTypes = other.denyAllTypes();
        if (denyAllTypes) {
            canMerge = false;
        }

        return this;
    }

    @Override
    public Map<String, SubType> filterSubTypes(
            Map<String, SubType> allowedSubTypes) {
        if (denyAllTypes()) {
            return Collections.emptyMap();
        }

        List<String> allowedTypes = getAllowedTypes();
        List<String> deniedTypes = getDeniedTypes();
        if (allowedTypes.isEmpty() && deniedTypes.isEmpty()) {
            return allowedSubTypes;
        }

        Map<String, SubType> filteredAllowedSubTypes = new HashMap<String, SubType>(
                allowedSubTypes);
        for (Iterator<String> it = filteredAllowedSubTypes.keySet().iterator(); it.hasNext();) {
            String subTypeName = it.next();
            if (deniedTypes.contains(subTypeName) || !allowedTypes.isEmpty()
                    && !allowedTypes.contains(subTypeName)) {
                it.remove();
            }
        }
        return filteredAllowedSubTypes;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.nuxeo.ecm.platform.types.localconfiguration.UITypesConfiguration#
     * getDefaultType()
     */
    @Override
    public String getDefaultType() {
        return defaultType;
    }

}
