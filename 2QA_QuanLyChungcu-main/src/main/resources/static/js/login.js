    document.addEventListener('DOMContentLoaded', function() {
        const tabs = document.querySelectorAll('.tab-button');
        const panels = document.querySelectorAll('.form-panel');

        // 1. Xử lý chuyển đổi Tab Đăng nhập / Đăng ký
        tabs.forEach(tab => {
            tab.addEventListener('click', () => {
                tabs.forEach(t => t.classList.remove('active'));
                panels.forEach(p => p.classList.remove('active'));

                tab.classList.add('active');
                const target = tab.getAttribute('data-target');
                const targetPanel = document.querySelector(target);
                if (targetPanel) {
                    targetPanel.classList.add('active');
                }
            });
        });

        // 2. Xử lý Hiện/Ẩn mật khẩu
        const toggleBtns = document.querySelectorAll('.toggle-password');
        toggleBtns.forEach(btn => {
            btn.addEventListener('click', function() {
                const targetId = this.getAttribute('data-target');
                const passwordInput = document.querySelector(targetId);

                if (passwordInput.type === "password") {
                    passwordInput.type = "text";
                    this.textContent = "Ẩn";
                } else {
                    passwordInput.type = "password";
                    this.textContent = "Hiện";
                }
            });
        });

        // 3. Mở tab đăng ký nếu server trả về lỗi đăng ký
        if (window.__openRegister) {
            const registerTab = document.querySelector('[data-target="#register-form"]');
            if (registerTab) registerTab.click();
        }
    });
