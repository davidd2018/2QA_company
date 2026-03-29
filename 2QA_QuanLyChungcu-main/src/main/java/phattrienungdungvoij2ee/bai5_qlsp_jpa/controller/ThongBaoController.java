package phattrienungdungvoij2ee.bai5_qlsp_jpa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import phattrienungdungvoij2ee.bai5_qlsp_jpa.model.ThongBao;
import phattrienungdungvoij2ee.bai5_qlsp_jpa.service.ThongBaoService;
import phattrienungdungvoij2ee.bai5_qlsp_jpa.service.CategoryService;

@Controller
@RequestMapping("/thongbao")
public class ThongBaoController {

    @Autowired
    private ThongBaoService thongBaoService;

    @Autowired
    private CategoryService categoryService;

    // Trang quan ly thong bao (ADMIN va MANAGER)
    @GetMapping
    public String list(Model model) {
        model.addAttribute("thongbaos", thongBaoService.getAllThongBao());
        return "thongbao/list";
    }

    // Trang xem thong bao cho tat ca user
    @GetMapping("/xem")
    public String viewAll(Model model) {
        model.addAttribute("thongbaos", thongBaoService.getAllThongBao());
        return "thongbao/view";
    }

    // Form them thong bao
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("thongbao", new ThongBao());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "thongbao/add";
    }

    // Luu thong bao (them moi hoac cap nhat)
    @PostMapping("/save")
    public String save(@ModelAttribute("thongbao") ThongBao thongBao,
                       @RequestParam(value = "schedDate", required = false) String schedDate,
                       @RequestParam(value = "schedHour", required = false) Integer schedHour,
                       @RequestParam(value = "schedMinute", required = false) Integer schedMinute,
                       Authentication authentication,
                       RedirectAttributes redirectAttributes) {
        try {
            if (thongBao.getId() == null) {
                // Thong bao moi -> gan nguoi tao
                thongBao.setNguoiTao(authentication.getName());
            } else {
                // Dang cap nhat -> giu nguyen nguoi tao cu
                ThongBao existing = thongBaoService.getById(thongBao.getId());
                if (existing != null) {
                    thongBao.setNguoiTao(existing.getNguoiTao());
                    // Neu khong phai loai lich trinh, giu nguyen ngayTao cu
                    if (!"LICH_CAT_DIEN_NUOC".equals(thongBao.getLoaiThongBao())
                            && !"LICH_BAO_TRI".equals(thongBao.getLoaiThongBao())) {
                        thongBao.setNgayTao(existing.getNgayTao());
                    }
                }
            }

            // Neu la loai lich trinh va co ngay/gio -> set ngayTao tu form
            if (("LICH_CAT_DIEN_NUOC".equals(thongBao.getLoaiThongBao())
                    || "LICH_BAO_TRI".equals(thongBao.getLoaiThongBao()))
                    && schedDate != null && !schedDate.isEmpty()) {
                java.time.LocalDate date = java.time.LocalDate.parse(schedDate);
                int hour = (schedHour != null) ? schedHour : 0;
                int minute = (schedMinute != null) ? schedMinute : 0;
                thongBao.setNgayTao(date.atTime(hour, minute));
            }

            // Tu dong ghim thong bao khan cap
            if ("TIN_KHAN_CAP".equals(thongBao.getLoaiThongBao())) {
                thongBao.setGhim(true);
            }

            thongBaoService.save(thongBao);
            redirectAttributes.addFlashAttribute("successMsg", "Lưu thông báo thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lưu thất bại: " + e.getMessage());
        }
        return "redirect:/thongbao";
    }

    // Form sua thong bao
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        ThongBao thongBao = thongBaoService.getById(id);
        if (thongBao == null) {
            return "redirect:/thongbao";
        }
        model.addAttribute("thongbao", thongBao);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "thongbao/edit";
    }

    // Xoa thong bao
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            thongBaoService.delete(id);
            redirectAttributes.addFlashAttribute("successMsg", "Xóa thông báo thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Xóa thất bại: " + e.getMessage());
        }
        return "redirect:/thongbao";
    }
}
