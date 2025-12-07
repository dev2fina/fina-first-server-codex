/*
 * Copyright (C) 2014 FINA Ltd.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */

package net.fina.server.st.api;

import java.io.FileNotFoundException;

/**
 * @author nikoloz
 */
public interface TemplateLocal {

    byte[] loadTemplate(String identity, String fiType) throws TemplateException;

    String getVersion(String identity, String fiType);

    String getOstVersion() throws TemplateException;

    String getCertificateCN(String userLogin) throws TemplateException;

    byte[] loadSubmissionTool(String fileName) throws FileNotFoundException;

    long getOstLastModifiedDate(String fileName);

    long getOstFileLength(String fileName);

    Boolean isSubmissionToolOnlineMode() throws TemplateException;
}
