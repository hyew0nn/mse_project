let editingProductId = null;
let currentBomProductId = null;
let selectedProductId = null;

document.addEventListener('DOMContentLoaded', function() {
    loadProducts();

    document.getElementById('productForm').addEventListener('submit', saveProduct);
    document.getElementById('bomItemForm').addEventListener('submit', addBomItem);
});

function loadProducts() {
    const products = getData(STORAGE_KEYS.PRODUCTS);
    const activeProducts = products.filter(p => !p.is_deleted);
    const tbody = document.getElementById('productsTableBody');

    if (activeProducts.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="empty-state">등록된 제품이 없습니다.</td></tr>';
        return;
    }

    tbody.innerHTML = activeProducts.map(product => `
        <tr onclick="showProductDetail(${product.product_id})" class="${selectedProductId === product.product_id ? 'selected' : ''}">
            <td>#${product.product_id}</td>
            <td>${product.product_name}</td>
            <td>${product.product_code}</td>
            <td>v${product.current_version}</td>
            <td>${product.product_description || '-'}</td>
            <td>${formatDateOnly(product.created_at)}</td>
            <td>
                <button class="btn-icon btn-bom" onclick="openBomModal(${product.product_id})">BOM</button>
            </td>
        </tr>
    `).join('');
}

function openAddModal() {
    editingProductId = null;
    document.getElementById('modalTitle').textContent = '제품 등록';
    document.getElementById('productForm').reset();
    document.getElementById('productId').value = '';
    document.getElementById('currentVersion').value = '1.0';
    openModal('productModal');
}

function saveProduct(e) {
    e.preventDefault();

    const products = getData(STORAGE_KEYS.PRODUCTS);
    const admin = getCurrentAdmin();

    const productData = {
        product_name: document.getElementById('productName').value.trim(),
        product_code: document.getElementById('productCode').value.trim(),
        current_version: document.getElementById('currentVersion').value.trim(),
        product_description: document.getElementById('productDescription').value.trim(),
        updated_by: admin.admin_id,
        updated_at: new Date().toISOString()
    };

    // 제품 코드 중복 체크
    const existingProduct = products.find(p =>
        p.product_code === productData.product_code &&
        p.product_id !== editingProductId &&
        !p.is_deleted
    );

    if (existingProduct) {
        alert('이미 사용중인 제품 코드입니다.');
        return;
    }

    if (editingProductId) {
        // 수정
        const index = products.findIndex(p => p.product_id === editingProductId);
        if (index !== -1) {
            products[index] = {
                ...products[index],
                ...productData
            };
        }
    } else {
        // 신규 등록
        const newProduct = {
            product_id: generateId(products),
            ...productData,
            is_deleted: false,
            created_at: new Date().toISOString()
        };
        products.push(newProduct);
    }

    setData(STORAGE_KEYS.PRODUCTS, products);
    loadProducts();
    closeModal('productModal');

    alert(editingProductId ? '제품 정보가 수정되었습니다.' : '제품이 등록되었습니다.');
}

function openBomModal(productId) {
    currentBomProductId = productId;
    const products = getData(STORAGE_KEYS.PRODUCTS);
    const product = products.find(p => p.product_id === productId);

    if (!product) {
        alert('제품을 찾을 수 없습니다.');
        return;
    }

    document.getElementById('bomProductName').textContent = product.product_name;
    document.getElementById('bomProductVersion').textContent = `v${product.current_version}`;

    loadBomItems();
    openModal('bomModal');
}

function loadBomItems() {
    const bom = getData(STORAGE_KEYS.BOM);
    const parts = getData(STORAGE_KEYS.PARTS);
    const tbody = document.getElementById('bomTableBody');

    const bomItems = bom.filter(b =>
        b.product_id === currentBomProductId &&
        !b.valid_to
    );

    if (bomItems.length === 0) {
        tbody.innerHTML = '<tr><td colspan="3" class="empty-state">구성 부품이 없습니다.</td></tr>';
        return;
    }

    tbody.innerHTML = bomItems.map(item => {
        const part = parts.find(p => p.part_id === item.part_id);
        return `
            <tr>
                <td>${part ? part.part_name : '알 수 없는 부품'}</td>
                <td>${formatNumber(item.quantity_required)}</td>
                <td>
                    <button class="btn-icon btn-delete" onclick="deleteBomItem(${item.bom_id})">삭제</button>
                </td>
            </tr>
        `;
    }).join('');
}

function openAddBomItemModal() {
    const parts = getData(STORAGE_KEYS.PARTS);
    const activeParts = parts.filter(p => !p.is_deleted);
    const select = document.getElementById('bomPartId');

    select.innerHTML = '<option value="">선택하세요</option>' +
        activeParts.map(part => `<option value="${part.part_id}">${part.part_name}</option>`).join('');

    document.getElementById('bomItemForm').reset();
    document.getElementById('bomQuantity').value = '1';
    openModal('bomItemModal');
}

function addBomItem(e) {
    e.preventDefault();

    const bom = getData(STORAGE_KEYS.BOM);
    const products = getData(STORAGE_KEYS.PRODUCTS);
    const product = products.find(p => p.product_id === currentBomProductId);

    const partId = parseInt(document.getElementById('bomPartId').value);
    const quantity = parseInt(document.getElementById('bomQuantity').value);

    // 이미 추가된 부품인지 확인
    const existing = bom.find(b =>
        b.product_id === currentBomProductId &&
        b.part_id === partId &&
        !b.valid_to
    );

    if (existing) {
        alert('이미 추가된 부품입니다.');
        return;
    }

    const newBomItem = {
        bom_id: generateId(bom),
        product_id: currentBomProductId,
        part_id: partId,
        product_version: product.current_version,
        quantity_required: quantity,
        valid_from: new Date().toISOString(),
        valid_to: null
    };

    bom.push(newBomItem);
    setData(STORAGE_KEYS.BOM, bom);

    closeModal('bomItemModal');
    alert('부품이 추가되었습니다.');

    loadBomItems();
}

function deleteBomItem(bomId) {
    if (!confirm('이 부품을 BOM에서 제거하시겠습니까?')) {
        return;
    }

    const bom = getData(STORAGE_KEYS.BOM);
    const index = bom.findIndex(b => b.bom_id === bomId);

    if (index !== -1) {
        bom[index].valid_to = new Date().toISOString();
        setData(STORAGE_KEYS.BOM, bom);
    }

    loadBomItems();
    alert('부품이 제거되었습니다.');
}

// 상세 정보 패널 관련 함수
function showProductDetail(productId) {
    selectedProductId = productId;
    const products = getData(STORAGE_KEYS.PRODUCTS);
    const admins = getData(STORAGE_KEYS.ADMINS);
    const product = products.find(p => p.product_id === productId);

    if (!product) return;

    const admin = admins.find(a => a.admin_id === product.updated_by);

    // 기본 정보 채우기
    document.getElementById('detail_product_id').value = `#${product.product_id}`;
    document.getElementById('detail_product_name').value = product.product_name;
    document.getElementById('detail_product_code').value = product.product_code;
    document.getElementById('detail_current_version').value = product.current_version;
    document.getElementById('detail_product_description').value = product.product_description || '';
    document.getElementById('detail_created_at').value = formatDate(product.created_at);

    // 관리자 정보 채우기
    if (admin) {
        document.getElementById('detail_admin_code').value = admin.admin_code;
        document.getElementById('detail_admin_name').value = admin.admin_name;
        document.getElementById('detail_admin_department').value = admin.department;
        document.getElementById('detail_admin_position').value = admin.position;
    } else {
        document.getElementById('detail_admin_code').value = '-';
        document.getElementById('detail_admin_name').value = '-';
        document.getElementById('detail_admin_department').value = '-';
        document.getElementById('detail_admin_position').value = '-';
    }
    document.getElementById('detail_updated_at').value = formatDate(product.updated_at);

    // 패널 표시
    document.getElementById('productDetailPanel').scrollIntoView({ behavior: 'smooth', block: 'start' });

    // 테이블 선택 표시
    loadProducts();
}

function closeDetailPanel() {
    selectedProductId = null;
    const panel = document.getElementById('productDetailPanel');

    const inputs = panel.querySelectorAll('input, textarea');
    inputs.forEach(input => input.value = '');

    loadProducts();
}

function saveProductDetail() {
    if (!selectedProductId) return;

    const products = getData(STORAGE_KEYS.PRODUCTS);
    const admin = getCurrentAdmin();
    const index = products.findIndex(p => p.product_id === selectedProductId);

    if (index === -1) {
        alert('제품을 찾을 수 없습니다.');
        return;
    }

    const productData = {
        product_name: document.getElementById('detail_product_name').value.trim(),
        product_code: document.getElementById('detail_product_code').value.trim(),
        current_version: document.getElementById('detail_current_version').value.trim(),
        product_description: document.getElementById('detail_product_description').value.trim(),
        updated_by: admin.admin_id,
        updated_at: new Date().toISOString()
    };

    // 제품 코드 중복 체크
    const existingProduct = products.find(p =>
        p.product_code === productData.product_code &&
        p.product_id !== selectedProductId &&
        !p.is_deleted
    );

    if (existingProduct) {
        alert('이미 사용중인 제품 코드입니다.');
        return;
    }

    products[index] = {
        ...products[index],
        ...productData
    };

    setData(STORAGE_KEYS.PRODUCTS, products);
    loadProducts();
    showProductDetail(selectedProductId);
    alert('제품 정보가 저장되었습니다.');
}

function deleteProductFromDetail() {
    if (!selectedProductId) return;

    if (!confirm('정말 이 제품을 삭제하시겠습니까?')) {
        return;
    }

    // 해당 제품의 주문이 있는지 확인
    const orders = getData(STORAGE_KEYS.ORDERS);
    const hasOrders = orders.some(o => o.product_id === selectedProductId);

    if (hasOrders) {
        alert('이 제품과 연결된 주문이 있어 삭제할 수 없습니다.');
        return;
    }

    const products = getData(STORAGE_KEYS.PRODUCTS);
    const index = products.findIndex(p => p.product_id === selectedProductId);

    if (index !== -1) {
        products[index].is_deleted = true;
        products[index].updated_at = new Date().toISOString();
        products[index].updated_by = getCurrentAdmin().admin_id;
        setData(STORAGE_KEYS.PRODUCTS, products);
    }

    closeDetailPanel();
    loadProducts();
    alert('제품이 삭제되었습니다.');
}