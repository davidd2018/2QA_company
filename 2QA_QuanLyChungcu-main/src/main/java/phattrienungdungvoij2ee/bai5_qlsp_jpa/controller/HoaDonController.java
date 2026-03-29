package phattrienungdungvoij2ee.bai5_qlsp_jpa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
    public String myInvoice(Model model, Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }
        Account user = accountService.findByLoginName(authentication.getName());
        if (user == null) {
            return "redirect:/login";
        }

        ApartmentInvoiceService.InvoiceView invoice = invoiceService.getInvoiceForUser(user);
        model.addAttribute("invoice", invoice);
        return "hoa-don/list";
    }
}
