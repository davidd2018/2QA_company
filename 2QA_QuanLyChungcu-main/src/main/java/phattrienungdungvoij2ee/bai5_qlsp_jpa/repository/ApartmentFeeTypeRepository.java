package phattrienungdungvoij2ee.bai5_qlsp_jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import phattrienungdungvoij2ee.bai5_qlsp_jpa.model.ApartmentFeeType;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApartmentFeeTypeRepository extends JpaRepository<ApartmentFeeType, Long> {
    Optional<ApartmentFeeType> findByCode(String code);
    List<ApartmentFeeType> findByActiveTrueOrderBySortOrderAsc();
}

