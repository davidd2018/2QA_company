package phattrienungdungvoij2ee.bai5_qlsp_jpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import phattrienungdungvoij2ee.bai5_qlsp_jpa.model.Payment;
import phattrienungdungvoij2ee.bai5_qlsp_jpa.repository.PaymentRepository;

import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    // Lay danh sach payment theo subscription
    public List<Payment> getPaymentsBySubscriptionId(Long subscriptionId) {
        return paymentRepository.findBySubscriptionId(subscriptionId);
    }

    // Lay danh sach payment cua user
    public List<Payment> getPaymentsByUserId(int userId) {
        return paymentRepository.findBySubscriptionUserId(userId);
    }

    // Cap nhat trang thai thanh toan
    public Payment updatePaymentStatus(Long paymentId, String status) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Khong tim thay payment!"));
        payment.setStatus(status);
        return paymentRepository.save(payment);
    }

    // Toggle trang thai
    public Payment togglePaymentStatus(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Khong tim thay payment!"));
        if ("DA_THANH_TOAN".equals(payment.getStatus())) {
            payment.setStatus("CHUA_THANH_TOAN");
        } else {
            payment.setStatus("DA_THANH_TOAN");
        }
        return paymentRepository.save(payment);
    }

    // Đếm tổng số lượng hóa đơn đã thanh toán
    public long countPaidPayments() {
        // Giả sử bạn lấy tất cả rồi lọc, hoặc viết thêm query trong Repository
        return paymentRepository.findAll().stream()
                .filter(p -> "DA_THANH_TOAN".equals(p.getStatus()))
                .count();
    }
}
