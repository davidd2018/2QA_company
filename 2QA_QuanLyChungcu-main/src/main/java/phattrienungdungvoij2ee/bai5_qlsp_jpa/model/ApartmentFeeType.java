package phattrienungdungvoij2ee.bai5_qlsp_jpa.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "apartment_fee_type")
public class ApartmentFeeType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 150)
    private String name;

    // KHI_MUA, KHI_MUA_BAN, HANG_THANG
    @Column(name = "charge_timing", nullable = false, length = 50)
    private String chargeTiming;

    @Column(name = "main_content", columnDefinition = "TEXT")
    private String mainContent;

    @Column(name = "calc_method", length = 200)
    private String calcMethod;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder = 0;
}

