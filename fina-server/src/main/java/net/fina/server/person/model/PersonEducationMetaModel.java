package net.fina.server.person.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonEducationMetaModel {
    private long id;
    private String instituteName;
    private long instituteNameStrId;
    private EducationLevel educationLevel;
    private String speciality;
    private long specialityStrId;
    private String completeCourseName;
    private long completeCourseNameStrId;
    private String seminarOrganizer;
    private long seminarOrganizerStrId;
    private String trainingPlace;
    private long trainingPlaceStrId;
    private Date completionDate;
    private boolean certificates;
    private String supportDocuments;
    private long supportDocumentsStrId;
    private AcademicDegreeLevel academicDegreeLevel;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getInstituteName() {
        return instituteName;
    }

    public void setInstituteName(String instituteName) {
        this.instituteName = instituteName;
    }

    public long getInstituteNameStrId() {
        return instituteNameStrId;
    }

    public void setInstituteNameStrId(long instituteNameStrId) {
        this.instituteNameStrId = instituteNameStrId;
    }

    public EducationLevel getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(EducationLevel educationLevel) {
        this.educationLevel = educationLevel;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public long getSpecialityStrId() {
        return specialityStrId;
    }

    public void setSpecialityStrId(long specialityStrId) {
        this.specialityStrId = specialityStrId;
    }

    public String getCompleteCourseName() {
        return completeCourseName;
    }

    public void setCompleteCourseName(String completeCourseName) {
        this.completeCourseName = completeCourseName;
    }

    public long getCompleteCourseNameStrId() {
        return completeCourseNameStrId;
    }

    public void setCompleteCourseNameStrId(long completeCourseNameStrId) {
        this.completeCourseNameStrId = completeCourseNameStrId;
    }

    public String getSeminarOrganizer() {
        return seminarOrganizer;
    }

    public void setSeminarOrganizer(String seminarOrganizer) {
        this.seminarOrganizer = seminarOrganizer;
    }

    public long getSeminarOrganizerStrId() {
        return seminarOrganizerStrId;
    }

    public void setSeminarOrganizerStrId(long seminarOrganizerStrId) {
        this.seminarOrganizerStrId = seminarOrganizerStrId;
    }

    public String getTrainingPlace() {
        return trainingPlace;
    }

    public void setTrainingPlace(String trainingPlace) {
        this.trainingPlace = trainingPlace;
    }

    public long getTrainingPlaceStrId() {
        return trainingPlaceStrId;
    }

    public void setTrainingPlaceStrId(long trainingPlaceStrId) {
        this.trainingPlaceStrId = trainingPlaceStrId;
    }

    public Date getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Date completionDate) {
        this.completionDate = completionDate;
    }

    public boolean isCertificates() {
        return certificates;
    }

    public void setCertificates(boolean certificates) {
        this.certificates = certificates;
    }

    public String getSupportDocuments() {
        return supportDocuments;
    }

    public void setSupportDocuments(String supportDocuments) {
        this.supportDocuments = supportDocuments;
    }

    public long getSupportDocumentsStrId() {
        return supportDocumentsStrId;
    }

    public void setSupportDocumentsStrId(long supportDocumentsStrId) {
        this.supportDocumentsStrId = supportDocumentsStrId;
    }

    public AcademicDegreeLevel getAcademicDegreeLevel() {
        return academicDegreeLevel;
    }

    public void setAcademicDegreeLevel(AcademicDegreeLevel academicDegreeLevel) {
        this.academicDegreeLevel = academicDegreeLevel;
    }
}
