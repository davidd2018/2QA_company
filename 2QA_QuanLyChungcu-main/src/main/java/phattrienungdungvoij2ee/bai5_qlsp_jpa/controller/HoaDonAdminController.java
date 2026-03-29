package phattrienungdungvoij2ee.bai5_qlsp_jpa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import phattrienungdungvoij2ee.bai5_qlsp_jpa.model.Account;
import phattrienungdungvoij2ee.bai5_qlsp_jpa.model.Role;
import phattrienungdungvoij2ee.bai5_qlsp_jpa.repository.AccountRepository;
import phattrienungdungvoij2ee.bai5_qlsp_jpa.service.ApartmentInvoiceService;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HoaDonAdminController {

    @Autowired
    private ApartmentInvoiceService invoiceService;

    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/hoa-don/admin")
    public String allInvoices(Model model) {
        // Lấy tất cả user có role USER và có căn hộ
        List<Account> allAccounts = accountRepository.findAll();
        List<ApartmentInvoiceService.InvoiceView> invoices = new ArrayList<>();
        for (Account acc : allAccounts) {
            if (acc.getChungCu() == null) continue;
            boolean isUser = false;
            for (Role r : acc.getRoles()) {
                if ("ROLE_USER".equals(r.getName())) { isUser = true; break; }
            }
            if (!isUser) continue;
            ApartmentInvoiceService.InvoiceView inv = invoiceService.getInvoiceForUser(acc);
            if (inv != null) {
                invoices.add(inv);
            }
        }
        model.addAttribute("invoices", invoices);
        return "hoa-don/admin";
    }

    @GetMapping("/hoa-don/admin/detail/{userId}")
    public String apartmentDetail(@PathVariable int userId, Model model, RedirectAttributes redirectAttributes) {
        Account user = accountRepository.findById(userId).orElse(null);
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMsg", "Không tìm thấy người dùng!");
            return "redirect:/hoa-don/admin";
        }
        ApartmentInvoiceService.ApartmentDetailView detail = invoiceService.getApartmentDetail(user);
        if (detail == null) {
            redirectAttributes.addFlashAttribute("errorMsg", "Người dùng chưa được gán căn hộ!");
            return "redirect:/hoa-don/admin";
        }
        model.addAttribute("detail", detail);
        model.addAttribute("userId", userId);
        return "hoa-don/detail";
    }
}
