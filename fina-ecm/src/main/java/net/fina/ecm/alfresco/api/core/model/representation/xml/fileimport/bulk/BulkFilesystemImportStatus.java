
package net.fina.ecm.alfresco.api.core.model.representation.xml.fileimport.bulk;

import jakarta.xml.bind.annotation.*;
import java.util.Date;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "BulkFilesystemImportStatus")
public class BulkFilesystemImportStatus {

    @XmlElement(name = "CurrentStatus")
    protected String currentStatus;
    @XmlElement(name = "ResultOfLastExecution")
    protected String resultOfLastExecution;
    @XmlElement(name = "SourceDirectory")
    protected String sourceDirectory;
    @XmlElement(name = "TargetSpace")
    protected String targetSpace;
    @XmlElement(name = "StartDate")
    @XmlSchemaType(name = "dateTime")
    protected Date startDate;
    @XmlElement(name = "EndDate")
    @XmlSchemaType(name = "dateTime")
    protected Date endDate;
    @XmlElement(name = "DurationInNS")
    protected int durationInNS;
    @XmlElement(name = "CompletedBatches")
    protected byte completedBatches;
    @XmlElement(name = "SourceStatistics")
    protected SourceStatistics sourceStatistics;
    @XmlElement(name = "TargetStatistics")
    protected TargetStatistics targetStatistics;

    /**
     * Gets the value of the currentStatus property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getCurrentStatus() {
        return currentStatus;
    }

    /**
     * Sets the value of the currentStatus property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setCurrentStatus(String value) {
        this.currentStatus = value;
    }

    /**
     * Gets the value of the resultOfLastExecution property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getResultOfLastExecution() {
        return resultOfLastExecution;
    }

    /**
     * Sets the value of the resultOfLastExecution property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setResultOfLastExecution(String value) {
        this.resultOfLastExecution = value;
    }

    /**
     * Gets the value of the sourceDirectory property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getSourceDirectory() {
        return sourceDirectory;
    }

    /**
     * Sets the value of the sourceDirectory property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSourceDirectory(String value) {
        this.sourceDirectory = value;
    }

    /**
     * Gets the value of the targetSpace property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTargetSpace() {
        return targetSpace;
    }

    /**
     * Sets the value of the targetSpace property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTargetSpace(String value) {
        this.targetSpace = value;
    }

    /**
     * Gets the value of the startDate property.
     *
     * @return possible object is
     * {@link Date }
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Sets the value of the startDate property.
     *
     * @param value allowed object is
     *              {@link Date }
     */
    public void setStartDate(Date value) {
        this.startDate = value;
    }

    /**
     * Gets the value of the endDate property.
     *
     * @return possible object is
     * {@link Date }
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * Sets the value of the endDate property.
     *
     * @param value allowed object is
     *              {@link Date }
     */
    public void setEndDate(Date value) {
        this.endDate = value;
    }

    /**
     * Gets the value of the durationInNS property.
     */
    public int getDurationInNS() {
        return durationInNS;
    }

    /**
     * Sets the value of the durationInNS property.
     */
    public void setDurationInNS(int value) {
        this.durationInNS = value;
    }

    /**
     * Gets the value of the completedBatches property.
     */
    public byte getCompletedBatches() {
        return completedBatches;
    }

    /**
     * Sets the value of the completedBatches property.
     */
    public void setCompletedBatches(byte value) {
        this.completedBatches = value;
    }

    /**
     * Gets the value of the sourceStatistics property.
     *
     * @return possible object is
     * {@link SourceStatistics }
     */
    public SourceStatistics getSourceStatistics() {
        return sourceStatistics;
    }

    /**
     * Sets the value of the sourceStatistics property.
     *
     * @param value allowed object is
     *              {@link SourceStatistics }
     */
    public void setSourceStatistics(SourceStatistics value) {
        this.sourceStatistics = value;
    }

    /**
     * Gets the value of the targetStatistics property.
     *
     * @return possible object is
     * {@link TargetStatistics }
     */
    public TargetStatistics getTargetStatistics() {
        return targetStatistics;
    }

    /**
     * Sets the value of the targetStatistics property.
     *
     * @param value allowed object is
     *              {@link TargetStatistics }
     */
    public void setTargetStatistics(TargetStatistics value) {
        this.targetStatistics = value;
    }

    @Override
    public String toString() {
        return "BulkFilesystemImportStatus{" +
                "currentStatus='" + currentStatus + '\'' +
                ", resultOfLastExecution='" + resultOfLastExecution + '\'' +
                ", sourceDirectory='" + sourceDirectory + '\'' +
                ", targetSpace='" + targetSpace + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", durationInNS=" + durationInNS +
                ", completedBatches=" + completedBatches +
                ", sourceStatistics=" + sourceStatistics +
                ", targetStatistics=" + targetStatistics +
                '}';
    }
}
