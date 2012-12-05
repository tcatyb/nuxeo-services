/*
 * (C) Copyright 2006-2012 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 *
 * Contributors:
 *     tmartins - Minimal descriptor to get a versioning configuration for the file service
 *
 */
package org.nuxeo.ecm.platform.filemanager.service.extension;

import java.io.Serializable;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

/**
 * @since 5.7
 * @author Thierry Martins <tm@nuxeo.com>
 */

@XObject("versioning")
public class VersioningDescriptor implements Serializable {

    private static final long serialVersionUID = 8615121233156981874L;

    @XNode("defaultVersioningOption")
    protected String defaultVersioningOption;

    public String getDefaultVersioningOption() {
        return defaultVersioningOption;
    }

}