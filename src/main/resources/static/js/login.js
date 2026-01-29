document.addEventListener('DOMContentLoaded', function() {
    // 이미 로그인되어 있으면 대시보드로 이동
    const currentAdmin = getCurrentAdmin();
    if (currentAdmin) {
        window.location.href = 'dashboard.html';
        return;
    }

    const loginForm = document.getElementById('loginForm');
    const errorMessage = document.getElementById('errorMessage');

    loginForm.addEventListener('submit', async function (e) {
        e.preventDefault();

        const adminCode = document.getElementById('adminCode').value.trim();
        const password = document.getElementById('password').value;

        try {
            // 2. 백엔드 API 호출
            const response = await fetch('/admin/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-XSRF-TOKEN': getCookie('XSRF-TOKEN')
                },
                body: JSON.stringify({
                    adminCode: adminCode,
                    password: password
                })
            });

            const result = await response.json();

            if (response.ok) {
                // 3. 로그인 성공 처리
                // 백엔드에서 준 관리자 정보(LoginResponse)를 저장
                const adminData = result.data;

                // 로컬 스토리지에 세션 대용으로 저장 (UI 표시용)
                localStorage.setItem(STORAGE_KEYS.ADMIN, JSON.stringify(adminData));

                // 대시보드로 이동
                window.location.href = 'dashboard.html';
            } else {
                // 4. 로그인 실패 처리 (401, 403 등)
                throw new Error(result.message || '로그인에 실패했습니다.');
            }

        } catch (error) {
            console.error('Login Error:', error);
            errorMessage.textContent = error.message;
            errorMessage.classList.add('show');

            setTimeout(() => {
                errorMessage.classList.remove('show');
            }, 3000);
        }
    });

        // 입력 필드 변경 시 에러 메시지 숨기기
        document.getElementById('adminCode').addEventListener('input', function () {
            errorMessage.classList.remove('show');
        });

        document.getElementById('password').addEventListener('input', function () {
            errorMessage.classList.remove('show');
        });
    });