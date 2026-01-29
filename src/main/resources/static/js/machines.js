let editingMachineId = null;

document.addEventListener('DOMContentLoaded', function() {
    if (document.getElementById('machinesTableBody')) {
        loadMachines();
        document.getElementById('machineForm').addEventListener('submit', saveMachine);
    }
});

function loadMachines() {
    const machines = getData(STORAGE_KEYS.MACHINES);
    const activeMachines = machines.filter(m => !m.is_deleted);
    const tbody = document.getElementById('machinesTableBody');

    if (activeMachines.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" class="empty-state">등록된 기계가 없습니다.</td></tr>';
        return;
    }

    tbody.innerHTML = activeMachines.map(machine => `
        <tr>
            <td>#${machine.machine_id}</td>
            <td>${machine.machine_name}</td>
            <td>${machine.serial_number}</td>
            <td>${formatNumber(machine.uph)}</td>
            <td>${machine.op_mode}</td>
            <td>${getStatusBadge(machine.status)}</td>
            <td>
                <div class="action-buttons">
                    <button class="btn-icon btn-edit" onclick="openEditMachineModal(${machine.machine_id})">수정</button>
                    <button class="btn-icon btn-delete" onclick="deleteMachine(${machine.machine_id})">삭제</button>
                </div>
            </td>
        </tr>
    `).join('');
}

function openAddMachineModal() {
    editingMachineId = null;
    document.getElementById('modalTitle').textContent = '기계 등록';
    document.getElementById('machineForm').reset();
    document.getElementById('machineId').value = '';
    document.getElementById('status').value = 'on';
    openModal('machineModal');
}

function openEditMachineModal(machineId) {
    editingMachineId = machineId;
    const machines = getData(STORAGE_KEYS.MACHINES);
    const machine = machines.find(m => m.machine_id === machineId);

    if (!machine) {
        alert('기계를 찾을 수 없습니다.');
        return;
    }

    document.getElementById('modalTitle').textContent = '기계 수정';
    document.getElementById('machineId').value = machine.machine_id;
    document.getElementById('machineName').value = machine.machine_name;
    document.getElementById('serialNumber').value = machine.serial_number;
    document.getElementById('uph').value = machine.uph;
    document.getElementById('opMode').value = machine.op_mode;
    document.getElementById('status').value = machine.status;

    openModal('machineModal');
}

function saveMachine(e) {
    e.preventDefault();

    const machines = getData(STORAGE_KEYS.MACHINES);
    const admin = getCurrentAdmin();

    const machineData = {
        machine_name: document.getElementById('machineName').value.trim(),
        serial_number: document.getElementById('serialNumber').value.trim(),
        uph: parseInt(document.getElementById('uph').value),
        op_mode: document.getElementById('opMode').value,
        status: document.getElementById('status').value,
        updated_by: admin.admin_id,
        updated_at: new Date().toISOString()
    };

    const existingMachine = machines.find(m =>
        m.serial_number === machineData.serial_number &&
        m.machine_id !== editingMachineId &&
        !m.is_deleted
    );

    if (existingMachine) {
        alert('이미 사용중인 관리번호입니다.');
        return;
    }

    if (editingMachineId) {
        const index = machines.findIndex(m => m.machine_id === editingMachineId);
        if (index !== -1) {
            const oldStatus = machines[index].status;
            machines[index] = {
                ...machines[index],
                ...machineData
            };

            if (oldStatus !== machineData.status && machineData.status === 'fault') {
                createNotification('기계 오류', `기계 "${machineData.machine_name}"에 오류가 발생했습니다.`, editingMachineId);
            }
        }
    } else {
        const newMachine = {
            machine_id: generateId(machines),
            ...machineData,
            is_deleted: false,
            created_at: new Date().toISOString()
        };
        machines.push(newMachine);
    }

    setData(STORAGE_KEYS.MACHINES, machines);
    loadMachines();
    closeModal('machineModal');

    alert(editingMachineId ? '기계 정보가 수정되었습니다.' : '기계가 등록되었습니다.');
}

function deleteMachine(machineId) {
    if (!confirm('정말 이 기계를 삭제하시겠습니까?')) {
        return;
    }

    const parts = getData(STORAGE_KEYS.PARTS);
    const hasParts = parts.some(p => p.machine_id === machineId && !p.is_deleted);

    if (hasParts) {
        alert('이 기계와 연결된 부품이 있어 삭제할 수 없습니다.');
        return;
    }

    const machines = getData(STORAGE_KEYS.MACHINES);
    const index = machines.findIndex(m => m.machine_id === machineId);

    if (index !== -1) {
        machines[index].is_deleted = true;
        machines[index].updated_at = new Date().toISOString();
        machines[index].updated_by = getCurrentAdmin().admin_id;
        setData(STORAGE_KEYS.MACHINES, machines);
    }

    loadMachines();
    alert('기계가 삭제되었습니다.');
}