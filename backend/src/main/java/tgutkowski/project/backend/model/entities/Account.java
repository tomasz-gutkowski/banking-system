package tgutkowski.project.backend.model.entities;

import jakarta.persistence.*;
import tgutkowski.project.backend.model.entities.enums.AccountStatus;
import tgutkowski.project.backend.model.entities.enums.AccountType;

@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private String accountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false, unique = true)
    private AppUser owner;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountType type;

    public String getAccountId() {
        return this.accountId;
    }

    public AccountStatus getAccountStatus() {
        return this.status;
    }

    public AccountType getAccountType() {
        return this.type;
    }

    public void setOwner(AppUser owner) {
        this.owner = owner;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public void setType(AccountType type) {
        this.type = type;
    }
}
