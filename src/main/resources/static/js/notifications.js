document.addEventListener('DOMContentLoaded', function() {
    loadNotifications();
});

function loadNotifications() {
    const notifications = getData(STORAGE_KEYS.NOTIFICATIONS);
    const tbody = document.getElementById('notificationsTableBody');

    if (notifications.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" class="empty-state">알림이 없습니다.</td></tr>';
        return;
    }

    tbody.innerHTML = notifications.map(notification => {
        const readStatus = notification.is_read ?
            '<span class="badge badge-secondary">읽음</span>' :
            '<span class="badge badge-info">안읽음</span>';

        let typeBadge = '';
        let typeIcon = '';

        switch(notification.notification_type) {
            case '재고 부족':
                typeBadge = '<span class="badge badge-warning">재고 부족</span>';
                typeIcon = '📦';
                break;
            case '주문 지연':
                typeBadge = '<span class="badge badge-danger">주문 지연</span>';
                typeIcon = '⏰';
                break;
            case '기계 오류':
                typeBadge = '<span class="badge badge-danger">기계 오류</span>';
                typeIcon = '⚠️';
                break;
            default:
                typeBadge = `<span class="badge badge-info">${notification.notification_type}</span>`;
                typeIcon = '🔔';
        }

        const rowClass = notification.is_read ? '' : 'unread-row';

        return `
            <tr class="${rowClass}">
                <td>${readStatus}</td>
                <td><span class="notification-type-icon">${typeIcon}</span>${typeBadge}</td>
                <td class="notification-message">${notification.message}</td>
                <td>${formatDate(notification.created_at)}</td>
                <td>
                    <div class="action-buttons">
                        ${!notification.is_read ? `<button class="btn-icon btn-read" onclick="markAsRead(${notification.notification_id})">읽음</button>` : ''}
                        <button class="btn-icon btn-delete" onclick="deleteNotification(${notification.notification_id})">삭제</button>
                    </div>
                </td>
            </tr>
        `;
    }).join('');
}

function markAsRead(notificationId) {
    const notifications = getData(STORAGE_KEYS.NOTIFICATIONS);
    const index = notifications.findIndex(n => n.notification_id === notificationId);

    if (index !== -1) {
        notifications[index].is_read = true;
        setData(STORAGE_KEYS.NOTIFICATIONS, notifications);
        loadNotifications();
        updateNotificationBadge();
    }
}

function markAllAsRead() {
    const notifications = getData(STORAGE_KEYS.NOTIFICATIONS);

    if (notifications.filter(n => !n.is_read).length === 0) {
        alert('읽지 않은 알림이 없습니다.');
        return;
    }

    if (!confirm('모든 알림을 읽음 처리하시겠습니까?')) {
        return;
    }

    notifications.forEach(notification => {
        notification.is_read = true;
    });

    setData(STORAGE_KEYS.NOTIFICATIONS, notifications);
    loadNotifications();
    updateNotificationBadge();
    alert('모든 알림을 읽음 처리했습니다.');
}

function deleteNotification(notificationId) {
    if (!confirm('이 알림을 삭제하시겠습니까?')) {
        return;
    }

    let notifications = getData(STORAGE_KEYS.NOTIFICATIONS);
    notifications = notifications.filter(n => n.notification_id !== notificationId);
    setData(STORAGE_KEYS.NOTIFICATIONS, notifications);

    loadNotifications();
    updateNotificationBadge();
}

function clearAllNotifications() {
    const notifications = getData(STORAGE_KEYS.NOTIFICATIONS);

    if (notifications.length === 0) {
        alert('삭제할 알림이 없습니다.');
        return;
    }

    if (!confirm('정말 모든 알림을 삭제하시겠습니까?')) {
        return;
    }

    setData(STORAGE_KEYS.NOTIFICATIONS, []);
    loadNotifications();
    updateNotificationBadge();
    alert('모든 알림이 삭제되었습니다.');
}