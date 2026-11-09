package tgutkowski.project.backend.model.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;
import tgutkowski.project.backend.model.dtos.responses.paging.Cursorable;
import tgutkowski.project.backend.model.dtos.responses.transfer.TransferInfoDto;
import tgutkowski.project.backend.model.entities.enums.TransferStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(indexes = {
        @Index(name="idx_account_sender" ,columnList = "sender_id"),
        @Index(name="idx_account_receiver" ,columnList = "receiver_id")
})
public class Transfer implements Cursorable<TransferInfoDto>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @UuidGenerator
    @Column(unique = true, nullable = false)
    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", referencedColumnName = "id", nullable = false)
    private Account sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", referencedColumnName = "id", nullable = false)
    private Account receiver;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    public Long getId() {
        return id;
    }

    public UUID getUUID() {
        return uuid;
    }

    public Account getSender() {
        return sender;
    }

    public Account getReceiver() {
        return receiver;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public TransferStatus getStatus() {
        return status;
    }

    @Column(nullable = false)
    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    private TransferStatus status;


    public void setSender(Account sender) {
        this.sender = sender;
    }

    public void setReceiver(Account receiver) {
        this.receiver = receiver;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setStatus(TransferStatus status) {
        this.status = status;
    }

    @Override
    public String getAsCursor() {
        return this.uuid.toString();
    }

    @Override
    public TransferInfoDto convertToDTO() {
        return TransferInfoDto.from(this);
    }
}
