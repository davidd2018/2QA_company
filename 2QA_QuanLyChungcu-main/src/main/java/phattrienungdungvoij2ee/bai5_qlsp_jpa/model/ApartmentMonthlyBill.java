package phattrienungdungvoij2ee.bai5_qlsp_jpa.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "apartment_monthly_bill", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"account_id", "month_key"})
})
public class ApartmentMonthlyBill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    // yyyy-MM
    @Column(name = "month_key", nullable = false, length = 7)
    private String monthKey;

    // DA_THANH_TOAN / CHUA_THANH_TOAN
    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null || status.trim().isEmpty()) {
            status = "CHUA_THANH_TOAN";
        }
    }
}

