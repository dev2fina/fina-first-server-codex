package net.fina.common.client.fis;

import net.fina.common.client.exception.FinATypeException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * vamekh on 9/19/16.
 */
public class FiImportResult {

    private Set<String> importedFis;
    private Set<String> modifiedFis;
    private Set<String> notImportedFis;
    private Set<String> nonExistentTypes;
    private Set<String> nonExistentLicenseTypes;
    private Set<String> nonExistentGroups;
    private Set<String> nonExistentManagements;
    private Set<String> nonExistentRegions;
    private Set<String> nonExistentLanguages;
    private Set<String> nonExistentLicenses;
    private Set<FinATypeException> exceptions;
    private List<String> exceptionMessages;
    public FiImportResult() {
        importedFis = new HashSet<String>();
        notImportedFis = new HashSet<String>();
        modifiedFis = new HashSet<String>();
        nonExistentTypes = new HashSet<String>();
        nonExistentLicenseTypes = new HashSet<String>();
        nonExistentGroups = new HashSet<String>();
        nonExistentManagements = new HashSet<String>();
        nonExistentRegions = new HashSet<String>();
        nonExistentLanguages = new HashSet<String>();
        nonExistentLicenses = new HashSet<String>();
        exceptions = new HashSet<FinATypeException>();
        exceptionMessages = new ArrayList<String>();
    }

    public Set<String> getModifiedFis() {
        return modifiedFis;
    }

    public void setModifiedFis(Set<String> modifiedFis) {
        this.modifiedFis = modifiedFis;
    }

    public Set<String> getNonExistentTypes() {
        return nonExistentTypes;
    }

    public void setNonExistentTypes(Set<String> nonExistentTypes) {
        this.nonExistentTypes = nonExistentTypes;
    }

    public Set<String> getNonExistentLicenseTypes() {
        return nonExistentLicenseTypes;
    }

    public void setNonExistentLicenseTypes(Set<String> nonExistentLicenseTypes) {
        this.nonExistentLicenseTypes = nonExistentLicenseTypes;
    }

    public Set<String> getNonExistentGroups() {
        return nonExistentGroups;
    }

    public void setNonExistentGroups(Set<String> nonExistentGroups) {
        this.nonExistentGroups = nonExistentGroups;
    }

    public Set<String> getNonExistentManagements() {
        return nonExistentManagements;
    }

    public void setNonExistentManagements(Set<String> nonExistentManagements) {
        this.nonExistentManagements = nonExistentManagements;
    }

    public Set<String> getNonExistentRegions() {
        return nonExistentRegions;
    }

    public void setNonExistentRegions(Set<String> nonExistentRegions) {
        this.nonExistentRegions = nonExistentRegions;
    }

    public Set<String> getNonExistentLanguages() {
        return nonExistentLanguages;
    }

    public void setNonExistentLanguages(Set<String> nonExistentLanguages) {
        this.nonExistentLanguages = nonExistentLanguages;
    }

    public Set<FinATypeException> getExceptions() {
        return exceptions;
    }

    public void setExceptions(Set<FinATypeException> exceptions) {
        this.exceptions = exceptions;
    }

    public Set<String> getNonExistentLicenses() {
        return nonExistentLicenses;
    }

    public void setNonExistentLicenses(Set<String> nonExistentLicenses) {
        this.nonExistentLicenses = nonExistentLicenses;
    }

    public Set<String> getImportedFis() {
        return importedFis;
    }

    public void setImportedFis(Set<String> importedFis) {
        this.importedFis = importedFis;
    }

    public Set<String> getNotImportedFis() {
        return notImportedFis;
    }

    public void setNotImportedFis(Set<String> notImportedFis) {
        this.notImportedFis = notImportedFis;
    }

    public List<String> getExceptionMessages() {
        return exceptionMessages;
    }

    public void setExceptionMessages(List<String> exceptionMessages) {
        this.exceptionMessages = exceptionMessages;
    }
}
