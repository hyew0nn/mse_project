document.addEventListener('DOMContentLoaded', function() {
    loadDashboardData();

    // 30초마다 데이터 새로고침
    setInterval(loadDashboardData, 30000);
});

function loadDashboardData() {
    loadStatistics();
    loadRecentOrders();
    loadMachineStatus();
    loadRecentNotifications();
    loadLowStockParts();
}

function loadStatistics() {
    const orders = getData(STORAGE_KEYS.ORDERS);
    const products = getData(STORAGE_KEYS.PRODUCTS);
    const parts = getData(STORAGE_KEYS.PARTS);

    // 전체 주문
    document.getElementById('totalOrders').textContent = orders.length;

    // 진행중 주문
    const activeOrders = orders.filter(o => o.status === '접수' || o.status === '진행중').length;
    document.getElementById('activeOrders').textContent = activeOrders;

    // 등록 제품
    const activeProducts = products.filter(p => !p.is_deleted).length;
    document.getElementById('totalProducts').textContent = activeProducts;

    // 재고 부족 부품
    const lowStockParts = parts.filter(p =>
        !p.is_deleted && p.stock_quantity < p.safety_stock
    ).length;
    document.getElementById('lowStockParts').textContent = lowStockParts;
}

function loadRecentOrders() {
    const orders = getData(STORAGE_KEYS.ORDERS);
    const customers = getData(STORAGE_KEYS.CUSTOMERS);
    const products = getData(STORAGE_KEYS.PRODUCTS);
    const tbody = document.getElementById('recentOrdersTable');

    // 최근 5개 주문만 표시
    const recentOrders = orders.slice(0, 5);

    if (recentOrders.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="empty-state">주문이 없습니다.</td></tr>';
        return;
    }

    tbody.innerHTML = recentOrders.map(order => {
        const customer = customers.find(c => c.customer_id === order.customer_id);
        const product = products.find(p => p.product_id === order.product_id);

        return `
            <tr>
                <td>#${order.order_id}</td>
                <td>${customer ? customer.customer_name : '-'}</td>
                <td>${product ? product.product_name : '-'}</td>
                <td>${formatNumber(order.quantity)}</td>
                <td>${getStatusBadge(order.status)}</td>
                <td>${formatDateOnly(order.deadline)}</td>
            </tr>
        `;
    }).join('');
}

function loadMachineStatus() {
    const machines = getData(STORAGE_KEYS.MACHINES);
    const activeMachines = machines.filter(m => !m.is_deleted);

    const onCount = activeMachines.filter(m => m.status === 'on').length;
    const offCount = activeMachines.filter(m => m.status === 'off').length;
    const faultCount = activeMachines.filter(m => m.status === 'fault').length;

    document.getElementById('machinesOn').textContent = onCount;
    document.getElementById('machinesOff').textContent = offCount;
    document.getElementById('machinesFault').textContent = faultCount;

    // 기계 목록 표시
    const machinesList = document.getElementById('machinesList');

    if (activeMachines.length === 0) {
        machinesList.innerHTML = '<p class="empty-state">등록된 기계가 없습니다.</p>';
        return;
    }

    machinesList.innerHTML = activeMachines.slice(0, 5).map(machine => `
        <div class="machine-item">
            <span class="machine-name">${machine.machine_name}</span>
            ${getStatusBadge(machine.status)}
        </div>
    `).join('');
}

function loadRecentNotifications() {
    const notifications = getData(STORAGE_KEYS.NOTIFICATIONS);
    const container = document.getElementById('recentNotifications');

    // 최근 5개 알림만 표시
    const recentNotifications = notifications.slice(0, 5);

    if (recentNotifications.length === 0) {
        container.innerHTML = '<p class="empty-state">알림이 없습니다.</p>';
        return;
    }

    container.innerHTML = recentNotifications.map(notification => {
        let itemClass = 'notification-item';
        if (!notification.is_read) {
            itemClass += ' unread';
        }
        if (notification.notification_type === '재고 부족' || notification.notification_type === '주문 지연') {
            itemClass += ' warning';
        }
        if (notification.notification_type === '기계 오류') {
            itemClass += ' danger';
        }

        return `
            <div class="${itemClass}">
                <div class="notification-type">${notification.notification_type}</div>
                <div class="notification-message">${notification.message}</div>
                <div class="notification-time">${formatDate(notification.created_at)}</div>
            </div>
        `;
    }).join('');
}

function loadLowStockParts() {
    const parts = getData(STORAGE_KEYS.PARTS);
    const tbody = document.getElementById('lowStockTable');

    const lowStockParts = parts.filter(p =>
        !p.is_deleted && p.stock_quantity < p.safety_stock
    ).slice(0, 5);

    if (lowStockParts.length === 0) {
        tbody.innerHTML = '<tr><td colspan="3" class="empty-state">재고 부족 부품이 없습니다.</td></tr>';
        return;
    }

    tbody.innerHTML = lowStockParts.map(part => `
        <tr>
            <td>${part.part_name}</td>
            <td style="color: #e74c3c; font-weight: bold;">${formatNumber(part.stock_quantity)}</td>
            <td>${formatNumber(part.safety_stock)}</td>
        </tr>
    `).join('');
}