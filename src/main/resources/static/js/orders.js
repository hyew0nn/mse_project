let editingOrderId = null;

document.addEventListener('DOMContentLoaded', function() {
    if (document.getElementById('ordersTableBody')) {
        loadOrders();
        loadCustomersForOrderSelect();
        loadProductsForOrderSelect();

        document.getElementById('orderForm').addEventListener('submit', saveOrder);
        document.getElementById('productIdSelect').addEventListener('change', calculateEstimatedCompletion);
        document.getElementById('quantityInput').addEventListener('input', calculateEstimatedCompletion);
    }
});

function loadOrders() {
    const orders = getData(STORAGE_KEYS.ORDERS);
    const customers = getData(STORAGE_KEYS.CUSTOMERS);
    const products = getData(STORAGE_KEYS.PRODUCTS);
    const tbody = document.getElementById('ordersTableBody');

    if (orders.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8" class="empty-state">등록된 주문이 없습니다.</td></tr>';
        return;
    }

    tbody.innerHTML = orders.map(order => {
        const customer = customers.find(c => c.customer_id === order.customer_id);
        const product = products.find(p => p.product_id === order.product_id);

        return `
            <tr>
                <td>#${order.order_id}</td>
                <td>${customer ? customer.customer_name : '-'}</td>
                <td>${product ? product.product_name : '-'}</td>
                <td>${formatNumber(order.quantity)}</td>
                <td>${getStatusBadge(order.status)}</td>
                <td>${formatDateOnly(order.started_at)}</td>
                <td>${formatDateOnly(order.deadline)}</td>
                <td>
                    <div class="action-buttons">
                        <button class="btn-icon btn-edit" onclick="openEditOrderModal(${order.order_id})">수정</button>
                        <button class="btn-icon btn-delete" onclick="deleteOrder(${order.order_id})">삭제</button>
                    </div>
                </td>
            </tr>
        `;
    }).join('');
}

function loadCustomersForOrderSelect() {
    const customers = getData(STORAGE_KEYS.CUSTOMERS);
    const select = document.getElementById('customerIdSelect');

    if (select) {
        select.innerHTML = '<option value="">선택하세요</option>' +
            customers.map(c => `<option value="${c.customer_id}">${c.customer_name}</option>`).join('');
    }
}

function loadProductsForOrderSelect() {
    const products = getData(STORAGE_KEYS.PRODUCTS);
    const activeProducts = products.filter(p => !p.is_deleted);
    const select = document.getElementById('productIdSelect');

    if (select) {
        select.innerHTML = '<option value="">선택하세요</option>' +
            activeProducts.map(p => `<option value="${p.product_id}">${p.product_name} (v${p.current_version})</option>`).join('');
    }
}

function calculateEstimatedCompletion() {
    const productId = parseInt(document.getElementById('productIdSelect').value);
    const quantity = parseInt(document.getElementById('quantityInput').value) || 0;

    if (!productId || !quantity) return;

    const products = getData(STORAGE_KEYS.PRODUCTS);
    const bom = getData(STORAGE_KEYS.BOM);
    const parts = getData(STORAGE_KEYS.PARTS);
    const machines = getData(STORAGE_KEYS.MACHINES);

    const product = products.find(p => p.product_id === productId);
    if (!product) return;

    const bomItems = bom.filter(b => b.product_id === productId && !b.valid_to);
    let totalMinutes = 0;

    bomItems.forEach(item => {
        const part = parts.find(p => p.part_id === item.part_id);
        if (part) {
            const requiredQuantity = quantity * item.quantity_required;
            totalMinutes += requiredQuantity * part.production_time;
        }
    });

    const now = new Date();
    const completionDate = new Date(now.getTime() + totalMinutes * 60000);

    document.getElementById('estimatedCompletionDisplay').textContent =
        `예상 완료: ${formatDate(completionDate.toISOString())} (약 ${Math.ceil(totalMinutes / 60)}시간)`;
    document.getElementById('estimatedCompletionAt').value = completionDate.toISOString();
}

function openAddOrderModal() {
    editingOrderId = null;
    document.getElementById('modalTitle').textContent = '주문 등록';
    document.getElementById('orderForm').reset();
    document.getElementById('orderId').value = '';
    document.getElementById('estimatedCompletionDisplay').textContent = '';
    document.getElementById('orderStatus').value = '접수';
    openModal('orderModal');
}

function openEditOrderModal(orderId) {
    editingOrderId = orderId;
    const orders = getData(STORAGE_KEYS.ORDERS);
    const order = orders.find(o => o.order_id === orderId);

    if (!order) {
        alert('주문을 찾을 수 없습니다.');
        return;
    }

    document.getElementById('modalTitle').textContent = '주문 수정';
    document.getElementById('orderId').value = order.order_id;
    document.getElementById('customerIdSelect').value = order.customer_id;
    document.getElementById('productIdSelect').value = order.product_id;
    document.getElementById('quantityInput').value = order.quantity;
    document.getElementById('orderStatus').value = order.status;
    document.getElementById('startedAt').value = order.started_at ? order.started_at.substring(0, 10) : '';
    document.getElementById('deadline').value = order.deadline ? order.deadline.substring(0, 10) : '';
    document.getElementById('estimatedCompletionAt').value = order.estimated_completion_at || '';

    if (order.estimated_completion_at) {
        document.getElementById('estimatedCompletionDisplay').textContent =
            `예상 완료: ${formatDate(order.estimated_completion_at)}`;
    }

    openModal('orderModal');
}

function saveOrder(e) {
    e.preventDefault();

    const orders = getData(STORAGE_KEYS.ORDERS);
    const products = getData(STORAGE_KEYS.PRODUCTS);
    const admin = getCurrentAdmin();

    const productId = parseInt(document.getElementById('productIdSelect').value);
    const product = products.find(p => p.product_id === productId);

    const orderData = {
        customer_id: parseInt(document.getElementById('customerIdSelect').value),
        product_id: productId,
        product_version: product ? product.current_version : '1.0',
        quantity: parseInt(document.getElementById('quantityInput').value),
        status: document.getElementById('orderStatus').value,
        started_at: document.getElementById('startedAt').value || new Date().toISOString(),
        deadline: document.getElementById('deadline').value || null,
        estimated_completion_at: document.getElementById('estimatedCompletionAt').value || null,
        updated_by: admin.admin_id,
        updated_at: new Date().toISOString()
    };

    if (editingOrderId) {
        const index = orders.findIndex(o => o.order_id === editingOrderId);
        if (index !== -1) {
            orders[index] = {
                ...orders[index],
                ...orderData
            };
        }
    } else {
        const newOrder = {
            order_id: generateId(orders),
            ...orderData,
            created_at: new Date().toISOString()
        };
        orders.push(newOrder);
    }

    setData(STORAGE_KEYS.ORDERS, orders);
    loadOrders();
    closeModal('orderModal');
    checkOrderDelaysAndNotify();

    alert(editingOrderId ? '주문 정보가 수정되었습니다.' : '주문이 등록되었습니다.');
}

function deleteOrder(orderId) {
    if (!confirm('정말 이 주문을 삭제하시겠습니까?')) {
        return;
    }

    let orders = getData(STORAGE_KEYS.ORDERS);
    orders = orders.filter(o => o.order_id !== orderId);
    setData(STORAGE_KEYS.ORDERS, orders);

    loadOrders();
    alert('주문이 삭제되었습니다.');
}