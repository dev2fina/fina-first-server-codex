package net.fina.server.person.entity;

import net.fina.server.i18n.helper.Description;
import net.fina.server.person.model.AcademicDegreeLevel;
import net.fina.server.person.model.EducationLevel;
import org.hibernate.annotations.Type;

import jakarta.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity(name = "IN_PERSON_EDUCATION")
@Table(name = "IN_PERSON_EDUCATION")
public class PersonEducation {
    @Id
    @SequenceGenerator(name = "person_education_sequence", sequenceName = "person_education_sequence", allocationSize = 1)
    @GeneratedValue(generator = "person_education_sequence", strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(name = "INSTITUTE_STR_ID")
    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    private Description instituteName;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "EDUCATION_LEVEL")
    private EducationLevel educationLevel;

    @Column(name = "SPECIALITY_STR_ID")
    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    private Description speciality;

    @Column(name = "COURSE_STR_ID")
    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    private Description completeCourseName;

    @Column(name = "SEMINAR_ORGANIZER_STR_ID")
    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    private Description seminarOrganizer;

    @Column(name = "TRAINING_PLACE_STR_ID")
    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    private Description trainingPlace;

    @Column(name = "COMPLETION_DATE")
    private Date completionDate;

    @Column(name = "HAS_CERTIFICATES")
    private boolean certificates;

    @Column(name = "SUPPORT_DOCS__STR_ID")
    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    private Description supportDocuments;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "DEGREE_LEVEL")
    private AcademicDegreeLevel academicDegreeLevel;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Description getInstituteName() {
        return instituteName;
    }

    public void setInstituteName(Description instituteName) {
        this.instituteName = instituteName;
    }

    public EducationLevel getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(EducationLevel educationLevel) {
        this.educationLevel = educationLevel;
    }

    public Description getSpeciality() {
        return speciality;
    }

    public void setSpeciality(Description speciality) {
        this.speciality = speciality;
    }

    public Description getCompleteCourseName() {
        return completeCourseName;
    }

    public void setCompleteCourseName(Description completeCourseName) {
        this.completeCourseName = completeCourseName;
    }

    public Description getSeminarOrganizer() {
        return seminarOrganizer;
    }

    public void setSeminarOrganizer(Description seminarOrganizer) {
        this.seminarOrganizer = seminarOrganizer;
    }

    public Description getTrainingPlace() {
        return trainingPlace;
    }

    public void setTrainingPlace(Description trainingPlace) {
        this.trainingPlace = trainingPlace;
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

    public Description getSupportDocuments() {
        return supportDocuments;
    }

    public void setSupportDocuments(Description supportDocuments) {
        this.supportDocuments = supportDocuments;
    }

    public AcademicDegreeLevel getAcademicDegreeLevel() {
        return academicDegreeLevel;
    }

    public void setAcademicDegreeLevel(AcademicDegreeLevel academicDegreeLevel) {
        this.academicDegreeLevel = academicDegreeLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonEducation education = (PersonEducation) o;
        return getId() == education.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
