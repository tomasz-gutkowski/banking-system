package tgutkowski.project.backend.model.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;
import tgutkowski.project.backend.model.dtos.responses.account.AccountStatusChangeLogInfoDto;
import tgutkowski.project.backend.model.dtos.responses.paging.Cursorable;
import tgutkowski.project.backend.model.entities.enums.ResolutionStatus;
import tgutkowski.project.backend.model.entities.enums.ResolutionType;
import tgutkowski.project.backend.model.entities.enums.StatusChange;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class AccountStatusChangeLog implements Cursorable<AccountStatusChangeLogInfoDto>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @UuidGenerator
    @Column(unique = true, nullable = false, updatable = false)
    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, updatable = false)
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private StatusChange requestType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, updatable = false)
    private AppUser requestedBy;

    @Column(nullable = true, length = 200)
    private String reason;

    @Column(nullable = false)
    private LocalDateTime requestDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResolutionType resolutionType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = true)
    private AppUser resolvedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResolutionStatus resolutionStatus;

    @Column(nullable = true)
    private LocalDateTime resolutionDate;

    public Long getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public Account getAccount() {
        return account;
    }

    public AppUser getRequestedBy() {
        return requestedBy;
    }

    public StatusChange getRequestType() {
        return requestType;
    }

    public String getReason() {
        return reason;
    }

    public AppUser getResolvedBy() {
        return resolvedBy;
    }

    public LocalDateTime getResolutionDate() {
        return resolutionDate;
    }

    public ResolutionStatus getResolutionStatus() {
        return resolutionStatus;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public void setRequestedBy(AppUser requestedBy) {
        this.requestedBy = requestedBy;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setRequestType(StatusChange requestType) {
        this.requestType = requestType;
    }

    public ResolutionType getResolutionType() {
        return resolutionType;
    }

    public void setResolutionType(ResolutionType resolutionType) {
        this.resolutionType = resolutionType;
    }

    public void setResolvedBy(AppUser resolvedBy) {
        this.resolvedBy = resolvedBy;
    }

    public void setResolutionDate(LocalDateTime resolutionDate) {
        this.resolutionDate = resolutionDate;
    }

    public void setResolutionStatus(ResolutionStatus resolutionStatus) {
        this.resolutionStatus = resolutionStatus;
    }

    @Override
    public String getAsCursor() {
        return this.uuid.toString();
    }

    @Override
    public AccountStatusChangeLogInfoDto convertToDTO() {
        return AccountStatusChangeLogInfoDto.from(this);
    }
}
