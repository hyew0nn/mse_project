let editingPartId = null;
let adjustingPartId = null;

document.addEventListener('DOMContentLoaded', function() {
    if (document.getElementById('partsTableBody')) {
        loadParts();
        loadMachinesForSelect();

        document.getElementById('partForm').addEventListener('submit', savePart);
        document.getElementById('stockForm').addEventListener('submit', adjustStock);
    }
});

function loadParts() {
    const parts = getData(STORAGE_KEYS.PARTS);
    const machines = getData(STORAGE_KEYS.MACHINES);
    const activeParts = parts.filter(p => !p.is_deleted);
    const tbody = document.getElementById('partsTableBody');

    if (activeParts.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8" class="empty-state">등록된 부품이 없습니다.</td></tr>';
        return;
    }

    tbody.innerHTML = activeParts.map(part => {
        const machine = machines.find(m => m.machine_id === part.machine_id);
        const isLowStock = part.stock_quantity < part.safety_stock;
        const stockClass = isLowStock ? 'stock-warning' : 'stock-safe';
        const statusBadge = isLowStock ? '<span class="badge badge-danger">재고 부족</span>' : '<span class="badge badge-success">정상</span>';

        return `
            <tr>
                <td>#${part.part_id}</td>
                <td>${part.part_name}</td>
                <td>${machine ? machine.machine_name : '-'}</td>
                <td class="${stockClass}">${formatNumber(part.stock_quantity)}</td>
                <td>${formatNumber(part.safety_stock)}</td>
                <td>${part.production_time}</td>
                <td>${statusBadge}</td>
                <td>
                    <div class="action-buttons">
                        <button class="btn-icon btn-stock" onclick="openStockModal(${part.part_id})">재고조정</button>
                        <button class="btn-icon btn-edit" onclick="openEditPartModal(${part.part_id})">수정</button>
                        <button class="btn-icon btn-delete" onclick="deletePart(${part.part_id})">삭제</button>
                    </div>
                </td>
            </tr>
        `;
    }).join('');
}

function loadMachinesForSelect() {
    const machines = getData(STORAGE_KEYS.MACHINES);
    const activeMachines = machines.filter(m => !m.is_deleted);
    const select = document.getElementById('machineId');

    if (select) {
        select.innerHTML = '<option value="">선택하세요</option>' +
            activeMachines.map(m => `<option value="${m.machine_id}">${m.machine_name}</option>`).join('');
    }
}

function openAddModal() {
    editingPartId = null;
    document.getElementById('modalTitle').textContent = '부품 등록';
    document.getElementById('partForm').reset();
    document.getElementById('partId').value = '';
    openModal('partModal');
}

function openEditPartModal(partId) {
    editingPartId = partId;
    const parts = getData(STORAGE_KEYS.PARTS);
    const part = parts.find(p => p.part_id === partId);

    if (!part) {
        alert('부품을 찾을 수 없습니다.');
        return;
    }

    document.getElementById('modalTitle').textContent = '부품 수정';
    document.getElementById('partId').value = part.part_id;
    document.getElementById('partName').value = part.part_name;
    document.getElementById('machineId').value = part.machine_id;
    document.getElementById('stockQuantity').value = part.stock_quantity;
    document.getElementById('safetyStock').value = part.safety_stock;
    document.getElementById('productionTime').value = part.production_time;

    openModal('partModal');
}

function savePart(e) {
    e.preventDefault();

    const parts = getData(STORAGE_KEYS.PARTS);
    const admin = getCurrentAdmin();

    const partData = {
        part_name: document.getElementById('partName').value.trim(),
        machine_id: parseInt(document.getElementById('machineId').value),
        stock_quantity: parseInt(document.getElementById('stockQuantity').value),
        safety_stock: parseInt(document.getElementById('safetyStock').value),
        production_time: parseFloat(document.getElementById('productionTime').value),
        updated_by: admin.admin_id,
        updated_at: new Date().toISOString()
    };

    if (editingPartId) {
        const index = parts.findIndex(p => p.part_id === editingPartId);
        if (index !== -1) {
            parts[index] = {
                ...parts[index],
                ...partData
            };
        }
    } else {
        const newPart = {
            part_id: generateId(parts),
            ...partData,
            is_deleted: false,
            created_at: new Date().toISOString()
        };
        parts.push(newPart);
    }

    setData(STORAGE_KEYS.PARTS, parts);
    loadParts();
    closeModal('partModal');
    checkStockAndNotify();

    alert(editingPartId ? '부품 정보가 수정되었습니다.' : '부품이 등록되었습니다.');
}

function openStockModal(partId) {
    adjustingPartId = partId;
    const parts = getData(STORAGE_KEYS.PARTS);
    const part = parts.find(p => p.part_id === partId);

    if (!part) {
        alert('부품을 찾을 수 없습니다.');
        return;
    }

    document.getElementById('stockInfo').innerHTML = `
        <p><strong>부품명:</strong> ${part.part_name}</p>
        <p><strong>현재 재고:</strong> ${formatNumber(part.stock_quantity)}</p>
        <p><strong>안전 재고:</strong> ${formatNumber(part.safety_stock)}</p>
    `;
    document.getElementById('stockPartId').value = partId;
    document.getElementById('stockAdjustment').value = '';

    openModal('stockModal');
}

function adjustStock(e) {
    e.preventDefault();

    const parts = getData(STORAGE_KEYS.PARTS);
    const adjustment = parseInt(document.getElementById('stockAdjustment').value);
    const index = parts.findIndex(p => p.part_id === adjustingPartId);

    if (index !== -1) {
        const newQuantity = parts[index].stock_quantity + adjustment;

        if (newQuantity < 0) {
            alert('재고가 부족합니다.');
            return;
        }

        parts[index].stock_quantity = newQuantity;
        parts[index].updated_at = new Date().toISOString();
        parts[index].updated_by = getCurrentAdmin().admin_id;

        setData(STORAGE_KEYS.PARTS, parts);
        loadParts();
        closeModal('stockModal');
        checkStockAndNotify();

        alert('재고가 조정되었습니다.');
    }
}

function deletePart(partId) {
    if (!confirm('정말 이 부품을 삭제하시겠습니까?')) {
        return;
    }

    const bom = getData(STORAGE_KEYS.BOM);
    const isUsed = bom.some(b => b.part_id === partId && !b.valid_to);

    if (isUsed) {
        alert('이 부품은 제품 BOM에 사용중이어서 삭제할 수 없습니다.');
        return;
    }

    const parts = getData(STORAGE_KEYS.PARTS);
    const index = parts.findIndex(p => p.part_id === partId);

    if (index !== -1) {
        parts[index].is_deleted = true;
        parts[index].updated_at = new Date().toISOString();
        parts[index].updated_by = getCurrentAdmin().admin_id;
        setData(STORAGE_KEYS.PARTS, parts);
    }

    loadParts();
    alert('부품이 삭제되었습니다.');
}