let editingCustomerId = null;

document.addEventListener('DOMContentLoaded', function() {
    loadCustomers();

    document.getElementById('customerForm').addEventListener('submit', saveCustomer);
});

function loadCustomers() {
    const customers = getData(STORAGE_KEYS.CUSTOMERS);
    const tbody = document.getElementById('customersTableBody');

    if (customers.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" class="empty-state">등록된 고객이 없습니다.</td></tr>';
        return;
    }

    tbody.innerHTML = customers.map(customer => `
        <tr>
            <td>#${customer.customer_id}</td>
            <td>${customer.customer_name}</td>
            <td>${customer.customer_phone}</td>
            <td>${customer.customer_email || '-'}</td>
            <td><span class="customer-type-badge customer-type-${customer.customer_type}">${customer.customer_type}</span></td>
            <td>${formatDateOnly(customer.created_at)}</td>
            <td>
                <div class="action-buttons">
                    <button class="btn-icon btn-edit" onclick="openEditModal(${customer.customer_id})">수정</button>
                    <button class="btn-icon btn-delete" onclick="deleteCustomer(${customer.customer_id})">삭제</button>
                </div>
            </td>
        </tr>
    `).join('');
}

function openAddModal() {
    editingCustomerId = null;
    document.getElementById('modalTitle').textContent = '고객 등록';
    document.getElementById('customerForm').reset();
    document.getElementById('customerId').value = '';
    openModal('customerModal');
}

function openEditModal(customerId) {
    editingCustomerId = customerId;
    const customers = getData(STORAGE_KEYS.CUSTOMERS);
    const customer = customers.find(c => c.customer_id === customerId);

    if (!customer) {
        alert('고객을 찾을 수 없습니다.');
        return;
    }

    document.getElementById('modalTitle').textContent = '고객 수정';
    document.getElementById('customerId').value = customer.customer_id;
    document.getElementById('customerName').value = customer.customer_name;
    document.getElementById('customerPhone').value = customer.customer_phone;
    document.getElementById('customerEmail').value = customer.customer_email || '';
    document.getElementById('customerType').value = customer.customer_type;

    openModal('customerModal');
}

function saveCustomer(e) {
    e.preventDefault();

    const customers = getData(STORAGE_KEYS.CUSTOMERS);
    const admin = getCurrentAdmin();

    const customerData = {
        customer_name: document.getElementById('customerName').value.trim(),
        customer_phone: document.getElementById('customerPhone').value.trim(),
        customer_email: document.getElementById('customerEmail').value.trim(),
        customer_type: document.getElementById('customerType').value,
        updated_by: admin.admin_id,
        updated_at: new Date().toISOString()
    };

    if (editingCustomerId) {
        // 수정
        const index = customers.findIndex(c => c.customer_id === editingCustomerId);
        if (index !== -1) {
            customers[index] = {
                ...customers[index],
                ...customerData
            };
        }
    } else {
        // 신규 등록
        const newCustomer = {
            customer_id: generateId(customers),
            ...customerData,
            created_at: new Date().toISOString()
        };
        customers.push(newCustomer);
    }

    setData(STORAGE_KEYS.CUSTOMERS, customers);
    loadCustomers();
    closeModal('customerModal');

    alert(editingCustomerId ? '고객 정보가 수정되었습니다.' : '고객이 등록되었습니다.');
}

function deleteCustomer(customerId) {
    if (!confirm('정말 이 고객을 삭제하시겠습니까?')) {
        return;
    }

    // 해당 고객의 주문이 있는지 확인
    const orders = getData(STORAGE_KEYS.ORDERS);
    const hasOrders = orders.some(o => o.customer_id === customerId);

    if (hasOrders) {
        alert('이 고객과 연결된 주문이 있어 삭제할 수 없습니다.');
        return;
    }

    let customers = getData(STORAGE_KEYS.CUSTOMERS);
    customers = customers.filter(c => c.customer_id !== customerId);
    setData(STORAGE_KEYS.CUSTOMERS, customers);

    loadCustomers();
    alert('고객이 삭제되었습니다.');
}