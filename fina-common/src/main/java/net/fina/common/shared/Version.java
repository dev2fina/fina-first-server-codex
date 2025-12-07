package net.fina.common.shared;

import java.io.Serializable;

public class Version implements Serializable {
    //FinA Modules
    private String finaVersion;
    private String fullVersion;
    private String databaseSchema;
    private String configVersion;
    private String buildTime;
    private String implementationVersion;
    //App
    private String javaVersion;
    private String javaHome;
    private String jbossVersion;
    private String jbossReleaseVersion;
    private String aooVersion;
    private String databaseName;
    private String databaseVersion;
    private String osArchitecture;
    private String osName;
    private String osVersion;
    private String osUserName;

    public String getFinaVersion() {
        return finaVersion;
    }

    public void setFinaVersion(String finaVersion) {
        this.finaVersion = finaVersion;
    }


    public String getFullVersion() {
        return fullVersion;
    }

    public void setFullVersion(String fullVersion) {
        this.fullVersion = fullVersion;
    }

    public String getBuildTime() {
        return buildTime;
    }

    public void setBuildTime(String buildTime) {
        this.buildTime = buildTime;
    }

    public String getJavaVersion() {
        return javaVersion;
    }

    public void setJavaVersion(String javaVersion) {
        this.javaVersion = javaVersion;
    }

    public String getJbossVersion() {
        return jbossVersion;
    }

    public void setJbossVersion(String jbossVersion) {
        this.jbossVersion = jbossVersion;
    }

    public String getAooVersion() {
        return aooVersion;
    }

    public void setAooVersion(String aooVersion) {
        this.aooVersion = aooVersion;
    }

    public String getDatabaseSchema() {
        return databaseSchema;
    }

    public void setDatabaseSchema(String databaseSchema) {
        this.databaseSchema = databaseSchema;
    }

    public String getJavaHome() {
        return javaHome;
    }

    public void setJavaHome(String javaHome) {
        this.javaHome = javaHome;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getDatabaseVersion() {
        return databaseVersion;
    }

    public void setDatabaseVersion(String databaseVersion) {
        this.databaseVersion = databaseVersion;
    }

    public String getOsArchitecture() {
        return osArchitecture;
    }

    public void setOsArchitecture(String osArchitecture) {
        this.osArchitecture = osArchitecture;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getOsUserName() {
        return osUserName;
    }

    public void setOsUserName(String osUserName) {
        this.osUserName = osUserName;
    }

    public String getConfigVersion() {
        return configVersion;
    }

    public void setConfigVersion(String configVersion) {
        this.configVersion = configVersion;
    }

    public String getImplementationVersion() {
        return implementationVersion;
    }

    public void setImplementationVersion(String implementationVersion) {
        this.implementationVersion = implementationVersion;
    }

    public String getJbossReleaseVersion() {
        return jbossReleaseVersion;
    }

    public void setJbossReleaseVersion(String jbossReleaseVersion) {
        this.jbossReleaseVersion = jbossReleaseVersion;
    }
}
