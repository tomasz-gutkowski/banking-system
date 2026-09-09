package tgutkowski.project.backend.model.entities;

import jakarta.persistence.*;
import tgutkowski.project.backend.model.entities.enums.AccountStatus;

@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private String accountNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false, unique = true)
    private AppUser owner;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setOwner(AppUser owner) {
        this.owner = owner;
    }

    public void  setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }
}
