package phattrienungdungvoij2ee.bai5_qlsp_jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import phattrienungdungvoij2ee.bai5_qlsp_jpa.model.ApartmentMonthlyBill;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApartmentMonthlyBillRepository extends JpaRepository<ApartmentMonthlyBill, Long> {
    Optional<ApartmentMonthlyBill> findByAccountIdAndMonthKey(int accountId, String monthKey);
    List<ApartmentMonthlyBill> findByAccountIdOrderByMonthKeyDesc(int accountId);

    @Query("SELECT b FROM ApartmentMonthlyBill b WHERE b.monthKey = :monthKey ORDER BY b.account.login_name ASC")
    List<ApartmentMonthlyBill> findByMonthKeyOrderByAccountLoginNameAsc(@Param("monthKey") String monthKey);
}

