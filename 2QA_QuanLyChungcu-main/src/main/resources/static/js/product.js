/**
 * Hàm định dạng tiền tệ dùng cho sự kiện oninput
 * @param {HTMLInputElement} input
 */
function formatCurrency(input) {
    if (!input) return;

    // 1. Lấy giá trị số thuần túy (loại bỏ mọi ký tự không phải số)
    let value = input.value.replace(/\D/g, "");

    // 2. Cập nhật vào ô input ẩn (Thymeleaf id="price")
    const hiddenInput = document.getElementById("price");
    if (hiddenInput) {
        hiddenInput.value = value;
    }

    // 3. Định dạng dấu chấm hiển thị cho người dùng
    if (value === "") {
        input.value = "";
    } else {
        // Định dạng theo chuẩn Việt Nam (1.000.000)
        input.value = new Intl.NumberFormat('vi-VN').format(value);
    }
}

/**
 * Tự động format khi trang load (Dành cho trang Sửa - Edit)
 */
document.addEventListener("DOMContentLoaded", function() {
    const displayInput = document.getElementById("displayPrice");
    const hiddenInput = document.getElementById("price");

    // Chỉ thực hiện nếu cả 2 phần tử tồn tại và có giá trị
    if (displayInput && hiddenInput && hiddenInput.value) {
        displayInput.value = new Intl.NumberFormat('vi-VN').format(hiddenInput.value);
    }
});