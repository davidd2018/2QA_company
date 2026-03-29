package phattrienungdungvoij2ee.bai5_qlsp_jpa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import phattrienungdungvoij2ee.bai5_qlsp_jpa.model.Account;
import phattrienungdungvoij2ee.bai5_qlsp_jpa.model.Dichvu;
import phattrienungdungvoij2ee.bai5_qlsp_jpa.model.Payment;
import phattrienungdungvoij2ee.bai5_qlsp_jpa.model.Subscription;
import phattrienungdungvoij2ee.bai5_qlsp_jpa.service.AccountService;
import phattrienungdungvoij2ee.bai5_qlsp_jpa.service.DichvuServiceImpl;
import phattrienungdungvoij2ee.bai5_qlsp_jpa.service.PaymentService;
import phattrienungdungvoij2ee.bai5_qlsp_jpa.service.SubscriptionService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/dich-vu")
public class UserServiceController {

    @Autowired
    private DichvuServiceImpl dichvuService;

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private AccountService accountService;

    // ===== CATALOG: DANH SACH DICH VU (CARD LAYOUT) =====
    @GetMapping
    public String catalog(Model model, Authentication authentication) {
        List<Dichvu> services = dichvuService.getAllServices();
        model.addAttribute("services", services);

        if (authentication != null) {
            Account user = accountService.findByLoginName(authentication.getName());
            if (user != null) {
                Map<Long, Boolean> subscribedMap = new HashMap<>();
                for (Dichvu s : services) {
                    subscribedMap.put(s.getId(), subscriptionService.isSubscribed(user.getId(), s.getId()));
                }
                model.addAttribute("subscribedMap", subscribedMap);
            }
            // Check if user is admin.css
            boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
            model.addAttribute("isAdmin", isAdmin);
        }
        return "dich-vu/catalog";
    }

    // ===== CHI TIET DICH VU =====
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model,
                         Authentication authentication,
                         RedirectAttributes redirectAttributes) {
        Dichvu service = dichvuService.getServiceById(id);
        if (service == null) {
            redirectAttributes.addFlashAttribute("errorMsg", "Không tìm thấy dịch vụ!");
            return "redirect:/dich-vu";
        }
        model.addAttribute("service", service);

        boolean isAdmin = false;
        if (authentication != null) {
            Account user = accountService.findByLoginName(authentication.getName());
            isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
            model.addAttribute("isAdmin", isAdmin);

            if (user != null) {
                model.addAttribute("isSubscribed",
                    subscriptionService.isSubscribed(user.getId(), id));
            }
        }

        // Lay danh sach nguoi dang ky va payment cua dich vu nay
        List<Subscription> subscriptions = subscriptionService.getSubscriptionsByServiceId(id);
        Map<Long, Payment> paymentMap = new HashMap<>();
        for (Subscription sub : subscriptions) {
            List<Payment> payments = paymentService.getPaymentsBySubscriptionId(sub.getId());
            if (!payments.isEmpty()) {
                paymentMap.put(sub.getId(), payments.get(0));
            }
        }
        model.addAttribute("subscriptions", subscriptions);
        model.addAttribute("paymentMap", paymentMap);

        return "dich-vu/detail";
    }

    // ===== DANG KY DICH VU =====
    @PostMapping("/subscribe/{id}")
    public String subscribe(@PathVariable Long id,
                            Authentication authentication,
                            RedirectAttributes redirectAttributes) {
        try {
            Account user = accountService.findByLoginName(authentication.getName());
            Dichvu dichvu = dichvuService.getServiceById(id);

            if (user == null || dichvu == null) {
                redirectAttributes.addFlashAttribute("errorMsg", "Dữ liệu không hợp lệ!");
                return "redirect:/dich-vu";
            }

            subscriptionService.subscribe(user, dichvu);
            redirectAttributes.addFlashAttribute("successMsg", "Đăng ký dịch vụ thành công!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/dich-vu/" + id;
    }

    // ===== DICH VU CUA TOI =====
    @GetMapping("/my-subscriptions")
    public String mySubscriptions(Model model, Authentication authentication) {
        Account user = accountService.findByLoginName(authentication.getName());
        if (user == null) {
            return "redirect:/login";
        }

        List<Subscription> subscriptions = subscriptionService.getSubscriptionsByUserId(user.getId());

        Map<Long, Payment> paymentMap = new HashMap<>();
        for (Subscription sub : subscriptions) {
            List<Payment> payments = paymentService.getPaymentsBySubscriptionId(sub.getId());
            if (!payments.isEmpty()) {
                paymentMap.put(sub.getId(), payments.get(0));
            }
        }

        model.addAttribute("subscriptions", subscriptions);
        model.addAttribute("paymentMap", paymentMap);
        return "dich-vu/my-subscriptions";
    }

    // ===== HUY DANG KY =====
    @PostMapping("/unsubscribe/{subscriptionId}")
    public String unsubscribe(@PathVariable Long subscriptionId,
                              RedirectAttributes redirectAttributes) {
        try {
            subscriptionService.unsubscribe(subscriptionId);
            redirectAttributes.addFlashAttribute("successMsg", "Hủy đăng ký thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Hủy thất bại: " + e.getMessage());
        }
        return "redirect:/dich-vu/my-subscriptions";
    }

    // ===== THANH TOAN GIA (USER) =====
    @PostMapping("/pay/{paymentId}")
    public String payService(@PathVariable Long paymentId,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        try {
            // Cap nhat trang thai thanh toan
            paymentService.updatePaymentStatus(paymentId, "DA_THANH_TOAN");
            redirectAttributes.addFlashAttribute("successMsg", "Thanh toán thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Thanh toán thất bại: " + e.getMessage());
        }
        return "redirect:/dich-vu/my-subscriptions";
    }
}
