package phattrienungdungvoij2ee.bai5_qlsp_jpa.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "apartment_bill_detail")
public class ApartmentBillDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bill_id", nullable = false)
    private ApartmentMonthlyBill bill;

    // APARTMENT_FEE / SERVICE / ADJUSTMENT / PENALTY
    @Column(name = "line_type", nullable = false, length = 30)
    private String lineType;

    // optional: map den loai phi can ho
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fee_type_id")
    private ApartmentFeeType feeType;

    // optional: map den dich vu user dang ky
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "service_id")
    private Dichvu service;

    // Ten dong chi phi de hien thi minh bach
    @Column(nullable = false, length = 200)
    private String title;

    @Column(precision = 15, scale = 3)
    private BigDecimal quantity;

    @Column(name = "unit_price", precision = 15, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (amount == null && unitPrice != null) {
            BigDecimal qty = (quantity == null) ? BigDecimal.ONE : quantity;
            amount = unitPrice.multiply(qty).setScale(2, RoundingMode.HALF_UP);
        }
        if (amount == null) {
            amount = BigDecimal.ZERO;
        }
    }
}

