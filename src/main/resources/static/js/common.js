// 로컬 스토리지 키
const STORAGE_KEYS = {
    ADMIN: 'currentAdmin',
    ADMINS: 'admins',
    CUSTOMERS: 'customers',
    PRODUCTS: 'products',
    PARTS: 'parts',
    MACHINES: 'machines',
    ORDERS: 'orders',
    NOTIFICATIONS: 'notifications',
    BOM: 'bom'
};

// 초기 데이터 설정
function initializeData() {
    if (!localStorage.getItem(STORAGE_KEYS.ADMINS)) {
        localStorage.setItem(STORAGE_KEYS.ADMINS, JSON.stringify([]));
    }

    if (!localStorage.getItem(STORAGE_KEYS.CUSTOMERS)) {
        localStorage.setItem(STORAGE_KEYS.CUSTOMERS, JSON.stringify([]));
    }

    if (!localStorage.getItem(STORAGE_KEYS.PRODUCTS)) {
        localStorage.setItem(STORAGE_KEYS.PRODUCTS, JSON.stringify([]));
    }

    if (!localStorage.getItem(STORAGE_KEYS.PARTS)) {
        localStorage.setItem(STORAGE_KEYS.PARTS, JSON.stringify([]));
    }

    if (!localStorage.getItem(STORAGE_KEYS.MACHINES)) {
        localStorage.setItem(STORAGE_KEYS.MACHINES, JSON.stringify([]));
    }

    if (!localStorage.getItem(STORAGE_KEYS.ORDERS)) {
        localStorage.setItem(STORAGE_KEYS.ORDERS, JSON.stringify([]));
    }

    if (!localStorage.getItem(STORAGE_KEYS.NOTIFICATIONS)) {
        localStorage.setItem(STORAGE_KEYS.NOTIFICATIONS, JSON.stringify([]));
    }

    if (!localStorage.getItem(STORAGE_KEYS.BOM)) {
        localStorage.setItem(STORAGE_KEYS.BOM, JSON.stringify([]));
    }
}

function getCookie(name) {
    let value = "; " + document.cookie;
    let parts = value.split("; " + name + "=");
    if (parts.length === 2) return parts.pop().split(";").shift();
}

// 현재 로그인한 관리자 가져오기
function getCurrentAdmin() {
    const adminStr = localStorage.getItem(STORAGE_KEYS.ADMIN);
    return adminStr ? JSON.parse(adminStr) : null;
}

// 로그인 체크
function checkAuth() {
    const admin = getCurrentAdmin();
    if (!admin && !window.location.pathname.includes('index.html') && window.location.pathname !== '/') {
        window.location.href = 'index.html';
        return false;
    }
    return true;
}

// 로그아웃
async function logout() {
    if (confirm('로그아웃 하시겠습니까?')) {
        try {
            // 서버에 로그아웃 요청 (SecurityConfig가 쿠키를 삭제함)
            await fetch('/logout', {
                method: 'POST',
                headers: {
                    'X-XSRF-TOKEN': getCookie('XSRF-TOKEN')
                }
            });
        } catch (error) {
            console.error('서버 로그아웃 통신 실패:', error);
        } finally {
            // 통신 성공 여부와 상관없이 클라이언트 정보는 무조건 삭제
            localStorage.removeItem(STORAGE_KEYS.ADMIN);

            // 자바스크립트로 한 번 더 쿠키 초기화 (보완책)
            document.cookie = "XSRF-TOKEN=; Max-Age=0; path=/";
            document.cookie = "JSESSIONID=; Max-Age=0; path=/";

            window.location.href = 'index.html';
        }
    }
}

// 데이터 가져오기
function getData(key) {
    const data = localStorage.getItem(key);
    return data ? JSON.parse(data) : [];
}

// 데이터 저장하기
function setData(key, data) {
    localStorage.setItem(key, JSON.stringify(data));
}

// ID 생성
function generateId(items) {
    if (items.length === 0) return 1;
    return Math.max(...items.map(item => {
        const keys = Object.keys(item);
        const idKey = keys.find(key => key.includes('_id'));
        return item[idKey] || 0;
    })) + 1;
}

// 날짜 포맷팅
function formatDate(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleString('ko-KR', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
    });
}

// 날짜만 포맷팅
function formatDateOnly(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleDateString('ko-KR');
}

// 모달 열기
function openModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.add('active');
    }
}

// 모달 닫기
function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.remove('active');
    }
}

// 알림 생성
function createNotification(type, message, relatedId = null) {
    const notifications = getData(STORAGE_KEYS.NOTIFICATIONS);
    const newNotification = {
        notification_id: generateId(notifications),
        notification_type: type,
        message: message,
        related_id: relatedId,
        is_read: false,
        created_at: new Date().toISOString()
    };
    notifications.unshift(newNotification);
    setData(STORAGE_KEYS.NOTIFICATIONS, notifications);
    updateNotificationBadge();
}

// 알림 배지 업데이트
function updateNotificationBadge() {
    const notifications = getData(STORAGE_KEYS.NOTIFICATIONS);
    const unreadCount = notifications.filter(n => !n.is_read).length;
    const badge = document.querySelector('.notification-badge');
    if (badge) {
        if (unreadCount > 0) {
            badge.textContent = unreadCount > 99 ? '99+' : unreadCount;
            badge.style.display = 'inline-block';
        } else {
            badge.style.display = 'none';
        }
    }
}

// 재고 체크 및 알림
function checkStockAndNotify() {
    const parts = getData(STORAGE_KEYS.PARTS);
    parts.forEach(part => {
        if (!part.is_deleted && part.stock_quantity < part.safety_stock) {
            const existingNotifications = getData(STORAGE_KEYS.NOTIFICATIONS);
            const hasRecentNotification = existingNotifications.some(n =>
                n.notification_type === '재고 부족' &&
                n.related_id === part.part_id &&
                !n.is_read
            );

            if (!hasRecentNotification) {
                createNotification(
                    '재고 부족',
                    `부품 "${part.part_name}"의 재고가 안전 재고(${part.safety_stock}) 이하입니다. 현재 재고: ${part.stock_quantity}`,
                    part.part_id
                );
            }
        }
    });
}

// 주문 지연 체크 및 알림
function checkOrderDelaysAndNotify() {
    const orders = getData(STORAGE_KEYS.ORDERS);
    const now = new Date();

    orders.forEach(order => {
        if (order.status !== '완료' && order.deadline) {
            const deadline = new Date(order.deadline);
            const estimatedCompletion = order.estimated_completion_at ? new Date(order.estimated_completion_at) : null;

            if (estimatedCompletion && estimatedCompletion > deadline && order.status !== '지연') {
                order.status = '지연';

                const existingNotifications = getData(STORAGE_KEYS.NOTIFICATIONS);
                const hasRecentNotification = existingNotifications.some(n =>
                    n.notification_type === '주문 지연' &&
                    n.related_id === order.order_id &&
                    !n.is_read
                );

                if (!hasRecentNotification) {
                    createNotification(
                        '주문 지연',
                        `주문 #${order.order_id}이 마감일을 초과할 예정입니다.`,
                        order.order_id
                    );
                }
            }
        }
    });

    setData(STORAGE_KEYS.ORDERS, orders);
}

// 사이드바 활성화 메뉴 설정
function setActiveMenu() {
    const currentPage = window.location.pathname.split('/').pop();
    const menuLinks = document.querySelectorAll('.sidebar-menu a');

    menuLinks.forEach(link => {
        link.classList.remove('active');
        if (link.getAttribute('href') === currentPage) {
            link.classList.add('active');
        }
    });
}

// 페이지 로드 시 실행
document.addEventListener('DOMContentLoaded', function() {
    initializeData();

    if (checkAuth()) {
        const admin = getCurrentAdmin();
        console.log("현재 관리자 데이터:", admin); // 여기서 필드명을 확인하세요!

        if (admin) {
            const adminInfoElements = document.querySelectorAll('.admin-info');
            adminInfoElements.forEach(el => {
                el.textContent = `${admin.adminName} (${admin.position})`;
            });

            updateNotificationBadge();
            setActiveMenu();

            // 주기적으로 알림 체크 (30초마다)
            setInterval(() => {
                checkStockAndNotify();
                checkOrderDelaysAndNotify();
            }, 30000);

            // 즉시 한 번 실행
            checkStockAndNotify();
            checkOrderDelaysAndNotify();
        }
    }
});

// 검색 함수
function searchTable(inputId, tableId) {
    const input = document.getElementById(inputId);
    const filter = input.value.toLowerCase();
    const table = document.getElementById(tableId);
    const tr = table.getElementsByTagName('tr');

    for (let i = 1; i < tr.length; i++) {
        const td = tr[i].getElementsByTagName('td');
        let found = false;

        for (let j = 0; j < td.length; j++) {
            if (td[j]) {
                const textValue = td[j].textContent || td[j].innerText;
                if (textValue.toLowerCase().indexOf(filter) > -1) {
                    found = true;
                    break;
                }
            }
        }

        tr[i].style.display = found ? '' : 'none';
    }
}

// 숫자 포맷팅
function formatNumber(num) {
    return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
}

// 상태 배지 생성
function getStatusBadge(status) {
    const badgeMap = {
        '접수': 'badge-info',
        '진행중': 'badge-warning',
        '완료': 'badge-success',
        '지연': 'badge-danger',
        'on': 'badge-success',
        'off': 'badge-secondary',
        'fault': 'badge-danger'
    };

    const badgeClass = badgeMap[status] || 'badge-secondary';
    return `<span class="badge ${badgeClass}">${status}</span>`;
}