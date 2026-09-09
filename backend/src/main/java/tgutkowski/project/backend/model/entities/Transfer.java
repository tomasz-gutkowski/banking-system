package tgutkowski.project.backend.model.entities;

import jakarta.persistence.*;
import tgutkowski.project.backend.model.entities.enums.TransferStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Transfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private Account sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Account receiver;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

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
}
