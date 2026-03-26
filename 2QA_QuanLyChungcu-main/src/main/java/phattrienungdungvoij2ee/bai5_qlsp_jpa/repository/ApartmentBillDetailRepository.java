package phattrienungdungvoij2ee.bai5_qlsp_jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import phattrienungdungvoij2ee.bai5_qlsp_jpa.model.ApartmentBillDetail;

import java.util.List;

@Repository
public interface ApartmentBillDetailRepository extends JpaRepository<ApartmentBillDetail, Long> {
    List<ApartmentBillDetail> findByBillIdOrderByIdAsc(Long billId);
}

