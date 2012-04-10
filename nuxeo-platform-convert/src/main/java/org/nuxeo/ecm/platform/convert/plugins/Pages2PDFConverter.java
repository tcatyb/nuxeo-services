/*
 * (C) Copyright 2002-2010 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 *     Nuxeo - initial API and implementation
 *
 */

package org.nuxeo.ecm.platform.convert.plugins;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;

import org.nuxeo.common.utils.ZipUtils;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.core.api.impl.blob.FileBlob;
import org.nuxeo.ecm.core.convert.api.ConversionException;
import org.nuxeo.ecm.core.convert.cache.SimpleCachableBlobHolder;
import org.nuxeo.ecm.core.convert.extension.Converter;
import org.nuxeo.ecm.core.convert.extension.ConverterDescriptor;

/**
 * Pages2PDF converter.
 *
 * @author ldoguin
 */
public class Pages2PDFConverter implements Converter {

    private static final String PAGES_PREVIEW_FILE = "QuickLook/Preview.pdf";

    @Override
    public BlobHolder convert(BlobHolder blobHolder,
            Map<String, Serializable> parameters) throws ConversionException {
        try {
            // retrieve the blob and verify its mimeType
            Blob blob = blobHolder.getBlob();
            String mimeType = blob.getMimeType();
            if (mimeType == null || !mimeType.equals("application/vnd.apple.pages")) {
                throw new ConversionException("not a pages file");
            }
            // look for the pdf file
            if (ZipUtils.hasEntry(blob.getStream(), PAGES_PREVIEW_FILE)) {
                // pdf file exist, let's extract it and return it as a
                // BlobHolder.
                InputStream previewPDFFile = ZipUtils.getEntryContentAsStream(
                        blob.getStream(), PAGES_PREVIEW_FILE);
                Blob previewBlob = new FileBlob(previewPDFFile);
                return new SimpleCachableBlobHolder(previewBlob);
            } else {
                // Pdf file does not exist, conversion cannot be done.
                throw new ConversionException(
                        "Pages file does not contain a pdf preview.");
            }
        } catch (Exception e) {
            throw new ConversionException(
                    "Could not find the pdf preview in the pages file", e);
        }
    }

    @Override
    public void init(ConverterDescriptor descriptor) {
    }

}
