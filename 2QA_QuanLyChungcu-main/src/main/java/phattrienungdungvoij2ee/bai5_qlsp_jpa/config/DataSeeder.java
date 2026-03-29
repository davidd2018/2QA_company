package phattrienungdungvoij2ee.bai5_qlsp_jpa.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import phattrienungdungvoij2ee.bai5_qlsp_jpa.model.*;
import phattrienungdungvoij2ee.bai5_qlsp_jpa.repository.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Seed dữ liệu cơ bản:
 *  - Roles (ADMIN, USER)
 *  - Loại chung cư (Category)
 *  - Nhóm dịch vụ & dịch vụ
 *
 * Chạy SAU ApartmentFeeTypeSeeder (Order 2).
 * Idempotent: kiểm tra tồn tại trước khi insert.
 */
@Component
@Order(2)
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepo;
    private final CategoryRepository categoryRepo;
    private final CategoryDichvuRepository catDichvuRepo;
    private final DichvuRepository dichvuRepo;

    @Override
    public void run(String... args) {
        seedRoles();
        seedCategories();
        List<Category_Dichvu> catDvs = seedCategoryDichvu();
        seedDichvu(catDvs);
    }

    // ------------------------------------------------------------------ ROLES
    private void seedRoles() {
        if (roleRepo.findByName("ROLE_ADMIN").isEmpty()) {
            Role r = new Role(); r.setName("ROLE_ADMIN"); roleRepo.save(r);
        }
        if (roleRepo.findByName("ROLE_USER").isEmpty()) {
            Role r = new Role(); r.setName("ROLE_USER"); roleRepo.save(r);
        }
    }

    // --------------------------------------------------------------- CATEGORY (loại chung cư)
    private void seedCategories() {
        findOrCreateCategory("Căn hộ cao cấp");
        findOrCreateCategory("Căn hộ trung cấp");
        findOrCreateCategory("Căn hộ bình dân");
    }

    private Category findOrCreateCategory(String name) {
        return categoryRepo.findByName(name).orElseGet(() -> {
            Category c = new Category(); c.setName(name); return categoryRepo.save(c);
        });
    }

    // --------------------------------------------------------------- CATEGORY DICH VU
    private List<Category_Dichvu> seedCategoryDichvu() {
        return List.of(
            findOrCreateCatDv("Vệ sinh & Môi trường", "Dịch vụ dọn dẹp, thu gom rác"),
            findOrCreateCatDv("An ninh & Bảo vệ",     "Dịch vụ bảo vệ 24/7"),
            findOrCreateCatDv("Tiện ích nội khu",      "Hồ bơi, gym, sân chơi"),
            findOrCreateCatDv("Sửa chữa & Bảo trì",   "Điện, nước, điều hòa")
        );
    }

    private Category_Dichvu findOrCreateCatDv(String name, String desc) {
        return catDichvuRepo.findByName(name).orElseGet(() -> {
            Category_Dichvu c = new Category_Dichvu();
            c.setName(name); c.setDescription(desc);
            return catDichvuRepo.save(c);
        });
    }

    // --------------------------------------------------------------- DICH VU
    private void seedDichvu(List<Category_Dichvu> cats) {
        // cats: [0] Vệ sinh & Môi trường, [1] An ninh & Bảo vệ, [2] Tiện ích nội khu, [3] Sửa chữa & Bảo trì
        findOrCreateDichvu("Wifi",       "Dịch vụ internet wifi tốc độ cao",         "Ban Quản Lý", "tháng", "FIXED", new BigDecimal("150000"), cats.get(2));
        findOrCreateDichvu("Giữ xe",     "Dịch vụ giữ xe an toàn 24/7",             "BV An Toàn",   "xe",    "FIXED", new BigDecimal("100000"), cats.get(1));
        findOrCreateDichvu("Phòng gym",  "Sử dụng phòng gym không giới hạn",        "Khu tiện ích", "tháng", "FIXED", new BigDecimal("300000"), cats.get(2));
        findOrCreateDichvu("Rác",        "Thu gom rác hàng ngày",                    "Công ty Sạch Sẽ", "tháng", "FIXED", new BigDecimal("100000"), cats.get(0));
        findOrCreateDichvu("Máy lạnh",   "Dịch vụ cho thuê máy lạnh",               "Đội kỹ thuật", "tháng", "FIXED", new BigDecimal("200000"), cats.get(2));
        findOrCreateDichvu("Máy giặt",   "Dịch vụ cho thuê máy giặt",               "Đội kỹ thuật", "tháng", "FIXED", new BigDecimal("150000"), cats.get(2));
        findOrCreateDichvu("Bình nước",  "Giao bình nước uống tận nơi",             "Nhà cung cấp", "bình",  "FIXED", new BigDecimal("30000"),  cats.get(2));
    }

    private Dichvu findOrCreateDichvu(String name, String desc, String provider,
                                      String unit, String costType, BigDecimal price,
                                      Category_Dichvu cat) {
        return dichvuRepo.findByName(name).orElseGet(() -> {
            Dichvu d = new Dichvu();
            d.setName(name); d.setDescription(desc); d.setProvider(provider);
            d.setUnit(unit); d.setCostType(costType); d.setCostValue(price);
            d.setPrice(price); d.setCategory(cat);
            return dichvuRepo.save(d);
        });
    }
}
