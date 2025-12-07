package net.fina.server.license.model;

import java.util.Date;
import java.util.List;

public class LicenseBankingOperationMetaModel {
    private long id;
    private boolean active;
    private Date changeDate;
    private List<BankingOperationCommentMetaModel> comments;

    private BankingOperationMetaModel bankingOperation;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Date getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(Date changeDate) {
        this.changeDate = changeDate;
    }

    public List<BankingOperationCommentMetaModel> getComments() {
        return comments;
    }

    public void setComments(List<BankingOperationCommentMetaModel> comments) {
        this.comments = comments;
    }

    public BankingOperationMetaModel getBankingOperation() {
        return bankingOperation;
    }

    public void setBankingOperation(BankingOperationMetaModel bankingOperation) {
        this.bankingOperation = bankingOperation;
    }
}
