package phattrienungdungvoij2ee.bai5_qlsp_jpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import phattrienungdungvoij2ee.bai5_qlsp_jpa.model.*;
import phattrienungdungvoij2ee.bai5_qlsp_jpa.repository.PaymentRepository;
import phattrienungdungvoij2ee.bai5_qlsp_jpa.repository.SubscriptionRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class ApartmentInvoiceService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    // ===== Phí cố định =====
    private static final BigDecimal DEPOSIT_RATE = new BigDecimal("0.02");          // 2%
    private static final BigDecimal MANAGEMENT_FEE = new BigDecimal("500000");      // 500k
    private static final BigDecimal ELECTRIC_FEE = new BigDecimal("800000");        // 800k
    private static final BigDecimal UTILITY_FEE = new BigDecimal("300000");         // 300k

    /**
     * Tạo hóa đơn hàng tháng cho user dựa trên:
     * - Tiền thuê căn hộ
     * - Phí bảo trì (2%)
     * - Phí quản lý & dịch vụ
     * - Điện, nước, internet
     * - Phí tiện ích
     * - Các dịch vụ đã đăng ký (subscriptions)
     */
    public InvoiceView getInvoiceForUser(Account user) {
        if (user == null || user.getChungCu() == null) {
            return null;
        }

        ChungCu chungCu = user.getChungCu();
        BigDecimal rentPrice = BigDecimal.valueOf(chungCu.getPrice());
        BigDecimal deposit = rentPrice.multiply(DEPOSIT_RATE).setScale(0, RoundingMode.HALF_UP);

        // Lấy tất cả dịch vụ đã đăng ký
        List<Subscription> subs = subscriptionRepository.findByUserId(user.getId());
        List<Payment> allPayments = paymentRepository.findBySubscriptionUserId(user.getId());

        List<InvoiceLineItem> lines = new ArrayList<>();
        BigDecimal totalServiceFee = BigDecimal.ZERO;

        // Dòng 1: Tiền thuê căn hộ
        lines.add(new InvoiceLineItem("Tiền thuê căn hộ", chungCu.getName(), rentPrice, "APARTMENT"));

        // Dòng 2: Phí bảo trì (2%)
        lines.add(new InvoiceLineItem("Phí bảo trì (2%)", "2% giá trị căn hộ", deposit, "APARTMENT"));

        // Dòng 3: Phí quản lý & dịch vụ
        lines.add(new InvoiceLineItem("Phí quản lý & dịch vụ", "Hàng tháng", MANAGEMENT_FEE, "APARTMENT"));

        // Dòng 4: Điện, nước, internet
        lines.add(new InvoiceLineItem("Điện, nước, internet", "Hàng tháng", ELECTRIC_FEE, "APARTMENT"));

        // Dòng 5: Phí tiện ích
        lines.add(new InvoiceLineItem("Phí tiện ích", "Hàng tháng", UTILITY_FEE, "APARTMENT"));

        // Các dòng dịch vụ đã đăng ký
        for (Subscription sub : subs) {
            if (sub.getServiceEntity() != null) {
                Dichvu dv = sub.getServiceEntity();
                BigDecimal price = dv.getPrice() != null ? dv.getPrice() : BigDecimal.ZERO;

                // Tìm payment status
                String status = "CHUA_THANH_TOAN";
                for (Payment p : allPayments) {
                    if (p.getSubscription() != null && p.getSubscription().getId().equals(sub.getId())) {
                        status = p.getStatus();
                        break;
                    }
                }

                lines.add(new InvoiceLineItem(dv.getName(), dv.getUnit() != null ? "/" + dv.getUnit() : "", price, "SERVICE", status));
                totalServiceFee = totalServiceFee.add(price);
            }
        }

        // Tổng = thuê + phí bảo trì + phí QL + điện nước + tiện ích + dịch vụ
        BigDecimal totalApartmentFees = rentPrice.add(deposit).add(MANAGEMENT_FEE).add(ELECTRIC_FEE).add(UTILITY_FEE);
        BigDecimal totalMonthly = totalApartmentFees.add(totalServiceFee);

        return new InvoiceView(
                user.getLogin_name(),
                user.getRoom(),
                chungCu.getName(),
                chungCu.getMaChungCu() != null ? chungCu.getMaChungCu() : String.valueOf(chungCu.getId()),
                rentPrice,
                deposit,
                MANAGEMENT_FEE,
                ELECTRIC_FEE,
                UTILITY_FEE,
                totalServiceFee,
                totalMonthly,
                lines
        );
    }

    // ===== APARTMENT DETAIL VIEW (cho trang xem chi tiết) =====
    public ApartmentDetailView getApartmentDetail(Account user) {
        if (user == null || user.getChungCu() == null) {
            return null;
        }
        ChungCu chungCu = user.getChungCu();
        BigDecimal rentPrice = BigDecimal.valueOf(chungCu.getPrice());
        BigDecimal deposit = rentPrice.multiply(DEPOSIT_RATE).setScale(0, RoundingMode.HALF_UP);

        // Dịch vụ đã đăng ký
        List<Subscription> subs = subscriptionRepository.findByUserId(user.getId());
        List<Payment> allPayments = paymentRepository.findBySubscriptionUserId(user.getId());

        List<ServiceLineItem> serviceLines = new ArrayList<>();
        BigDecimal totalServiceFee = BigDecimal.ZERO;

        for (Subscription sub : subs) {
            if (sub.getServiceEntity() != null) {
                String name = sub.getServiceEntity().getName();
                BigDecimal price = sub.getServiceEntity().getPrice() != null ? sub.getServiceEntity().getPrice() : BigDecimal.ZERO;

                // Tìm payment status
                String status = "CHUA_THANH_TOAN";
                for (Payment p : allPayments) {
                    if (p.getSubscription() != null && p.getSubscription().getId().equals(sub.getId())) {
                        status = p.getStatus();
                        break;
                    }
                }

                serviceLines.add(new ServiceLineItem(name, price, status));
                totalServiceFee = totalServiceFee.add(price);
            }
        }

        // Tổng tất cả phí
        BigDecimal totalApartmentFees = rentPrice.add(deposit).add(MANAGEMENT_FEE).add(ELECTRIC_FEE).add(UTILITY_FEE);
        BigDecimal totalMonthly = totalApartmentFees.add(totalServiceFee);

        return new ApartmentDetailView(
                user.getLogin_name(),
                user.getRoom(),
                chungCu.getName(),
                chungCu.getMaChungCu() != null ? chungCu.getMaChungCu() : String.valueOf(chungCu.getId()),
                rentPrice,
                deposit,
                MANAGEMENT_FEE,
                ELECTRIC_FEE,
                UTILITY_FEE,
                totalServiceFee,
                serviceLines,
                totalMonthly
        );
    }

    // ===== VIEW CLASSES =====

    public static class InvoiceView {
        public final String username;
        public final String room;
        public final String apartmentName;
        public final String apartmentCode;
        public final BigDecimal rentPrice;
        public final BigDecimal deposit;
        public final BigDecimal managementFee;
        public final BigDecimal electricFee;
        public final BigDecimal utilityFee;
        public final BigDecimal totalServiceFee;
        public final BigDecimal totalMonthly;
        public final List<InvoiceLineItem> lines;

        public InvoiceView(String username, String room, String apartmentName, String apartmentCode,
                           BigDecimal rentPrice, BigDecimal deposit, BigDecimal managementFee,
                           BigDecimal electricFee, BigDecimal utilityFee,
                           BigDecimal totalServiceFee, BigDecimal totalMonthly,
                           List<InvoiceLineItem> lines) {
            this.username = username;
            this.room = room;
            this.apartmentName = apartmentName;
            this.apartmentCode = apartmentCode;
            this.rentPrice = rentPrice;
            this.deposit = deposit;
            this.managementFee = managementFee;
            this.electricFee = electricFee;
            this.utilityFee = utilityFee;
            this.totalServiceFee = totalServiceFee;
            this.totalMonthly = totalMonthly;
            this.lines = lines;
        }
    }

    public static class InvoiceLineItem {
        public final String name;
        public final String description;
        public final BigDecimal amount;
        public final String type; // APARTMENT or SERVICE
        public final String status; // DA_THANH_TOAN or CHUA_THANH_TOAN (chỉ cho SERVICE)

        public InvoiceLineItem(String name, String description, BigDecimal amount, String type) {
            this(name, description, amount, type, null);
        }

        public InvoiceLineItem(String name, String description, BigDecimal amount, String type, String status) {
            this.name = name;
            this.description = description;
            this.amount = amount;
            this.type = type;
            this.status = status;
        }
    }

    public static class ApartmentDetailView {
        public final String username;
        public final String room;
        public final String apartmentName;
        public final String apartmentCode;
        public final BigDecimal rentPrice;
        public final BigDecimal deposit;
        public final BigDecimal managementFee;
        public final BigDecimal electricFee;
        public final BigDecimal utilityFee;
        public final BigDecimal totalServiceFee;
        public final List<ServiceLineItem> serviceLines;
        public final BigDecimal totalMonthly;

        public ApartmentDetailView(String username, String room, String apartmentName, String apartmentCode,
                                   BigDecimal rentPrice, BigDecimal deposit, BigDecimal managementFee,
                                   BigDecimal electricFee, BigDecimal utilityFee,
                                   BigDecimal totalServiceFee, List<ServiceLineItem> serviceLines,
                                   BigDecimal totalMonthly) {
            this.username = username;
            this.room = room;
            this.apartmentName = apartmentName;
            this.apartmentCode = apartmentCode;
            this.rentPrice = rentPrice;
            this.deposit = deposit;
            this.managementFee = managementFee;
            this.electricFee = electricFee;
            this.utilityFee = utilityFee;
            this.totalServiceFee = totalServiceFee;
            this.serviceLines = serviceLines;
            this.totalMonthly = totalMonthly;
        }
    }

    public static class ServiceLineItem {
        public final String serviceName;
        public final BigDecimal price;
        public final String status; // DA_THANH_TOAN or CHUA_THANH_TOAN

        public ServiceLineItem(String serviceName, BigDecimal price, String status) {
            this.serviceName = serviceName;
            this.price = price;
            this.status = status;
        }
    }

    // ===== ADMIN VIEW (cho trang Theo dõi thanh toán admin) =====
    public static class ServicePaymentAdminView {
        public final String username;
        public final String room;
        public final String apartmentName;
        public final String serviceName;
        public final BigDecimal amount;
        public final String status;
        public final String createdAt;

        public ServicePaymentAdminView(String username, String room, String apartmentName,
                                       String serviceName, BigDecimal amount, String status, String createdAt) {
            this.username = username;
            this.room = room;
            this.apartmentName = apartmentName;
            this.serviceName = serviceName;
            this.amount = amount;
            this.status = status;
            this.createdAt = createdAt;
        }
    }

    public List<ServicePaymentAdminView> getServicePaymentsForAdmin() {
        List<Subscription> all = subscriptionRepository.findAll();
        List<Payment> allPayments = paymentRepository.findAll();
        List<ServicePaymentAdminView> result = new ArrayList<>();

        for (Subscription sub : all) {
            if (sub.getUser() == null || sub.getServiceEntity() == null) continue;
            Account u = sub.getUser();
            Dichvu dv = sub.getServiceEntity();

            String status = "CHUA_THANH_TOAN";
            for (Payment p : allPayments) {
                if (p.getSubscription() != null && p.getSubscription().getId().equals(sub.getId())) {
                    status = p.getStatus();
                    break;
                }
            }

            result.add(new ServicePaymentAdminView(
                    u.getLogin_name(),
                    u.getRoom(),
                    u.getChungCu() != null ? u.getChungCu().getName() : "N/A",
                    dv.getName(),
                    dv.getPrice() != null ? dv.getPrice() : BigDecimal.ZERO,
                    status,
                    sub.getCreatedAt() != null ? sub.getCreatedAt().toString() : ""
            ));
        }
        return result;
    }
}
