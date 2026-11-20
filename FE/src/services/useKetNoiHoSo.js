import { useEffect } from 'react'
import { Client } from '@stomp/stompjs'
import { capNhatKhiMayChuBaoThayDoi } from './dongBoDuLieu.js'

export function useKetNoiHoSo(token) {
  useEffect(() => {
    if (!token || typeof window.WebSocket !== 'function') return

    const url = new URL('/ws', window.location.href)
    url.protocol = url.protocol === 'https:' ? 'wss:' : 'ws:'
    const client = new Client({
      brokerURL: url.toString(),
      connectHeaders: { Authorization: `Bearer ${token}` },
      reconnectDelay: 5000,
      connectionTimeout: 8000,
      heartbeatIncoming: 10000,
      heartbeatOutgoing: 10000,
      onConnect: () => {
        client.subscribe('/user/queue/cap-nhat', (tinNhan) => {
          if (client.active && ['KE_HOACH_CAP_NHAT', 'DU_LIEU_CAP_NHAT'].includes(tinNhan.body)) {
            capNhatKhiMayChuBaoThayDoi()
          }
        })
        capNhatKhiMayChuBaoThayDoi()
      },
      onStompError: () => {
        // Tránh tiếp tục gửi cùng JWT khi máy chủ đã từ chối phiên này.
        void client.deactivate({ force: true })
      },
    })
    client.activate()

    return () => { void client.deactivate({ force: true }) }
  }, [token])
}
