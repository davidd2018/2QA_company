package phattrienungdungvoij2ee.bai5_qlsp_jpa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import phattrienungdungvoij2ee.bai5_qlsp_jpa.model.Account;
import phattrienungdungvoij2ee.bai5_qlsp_jpa.service.AccountService;
import phattrienungdungvoij2ee.bai5_qlsp_jpa.service.ApartmentInvoiceService;

@Controller
public class HoaDonController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private ApartmentInvoiceService invoiceService;

    @GetMapping("/hoa-don")
    public String myInvoices(Model model, Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }
        Account user = accountService.findByLoginName(authentication.getName());
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("invoices", invoiceService.getInvoicesForUser(user));
        return "hoa-don/list";
    }

    @PostMapping("/hoa-don/pay/{monthKey}")
    public String payMonth(@PathVariable String monthKey,
                           Authentication authentication,
                           RedirectAttributes redirectAttributes) {
        if (authentication == null) {
            return "redirect:/login";
        }
        Account user = accountService.findByLoginName(authentication.getName());
        if (user == null) {
            return "redirect:/login";
        }

        try {
            invoiceService.payInvoiceMonth(user, monthKey);
            redirectAttributes.addFlashAttribute("successMsg", String.format("Thanh toan hoa don thang %s thanh cong!", monthKey));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", String.format("Thanh toan that bai: %s", e.getMessage()));
        }
        return "redirect:/hoa-don";
    }
}

