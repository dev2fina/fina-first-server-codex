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

package net.fina.server.st.impl;

import java.util.ArrayList;
import java.util.List;

public class MdtReleaseVersion {

	private List<Version> versions = new ArrayList<MdtReleaseVersion.Version>();

	public List<Version> getVersions() {
		return versions;
	}

	public void setVersions(List<Version> versions) {
		this.versions = versions;
	}

	public static class Version {

		// Version
		private String t;
		// Fi type code
		private int v;

		public Version() {

		}

		public Version(String t, int v) {
			this.t = t;
			this.v = v;
		}

		public String getT() {
			return t;
		}

		public void setT(String t) {
			this.t = t;
		}

		public int getV() {
			return v;
		}

		public void setV(int v) {
			this.v = v;
		}
	}
}
