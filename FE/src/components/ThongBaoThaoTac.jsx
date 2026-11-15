import { useEffect, useState } from 'react'
import { SU_KIEN_THONG_BAO } from '../api/apiClient.js'

export default function ThongBaoThaoTac() {
  const [thongBao, setThongBao] = useState(null)

  useEffect(() => {
    function nhanThongBao(event) {
      setThongBao(event.detail)
    }

    window.addEventListener(SU_KIEN_THONG_BAO, nhanThongBao)
    return () => window.removeEventListener(SU_KIEN_THONG_BAO, nhanThongBao)
  }, [])

  useEffect(() => {
    if (!thongBao?.thanhCong) return

    const henGio = window.setTimeout(() => setThongBao(null), 4500)
    return () => window.clearTimeout(henGio)
  }, [thongBao])

  if (!thongBao) return null

  return (
    <div
      role={thongBao.thanhCong ? 'status' : 'alert'}
      className={`fixed right-4 bottom-4 left-4 z-50 flex items-start
        justify-between gap-4 rounded-xl border bg-white p-4 text-sm
        shadow-lg sm:left-auto sm:max-w-sm ${
          thongBao.thanhCong ? 'border-emerald-200 text-emerald-900' : 'border-red-200 text-red-900'
        }`}
    >
      <p>{thongBao.noiDung}</p>
      <button
        type="button"
        aria-label="Đóng thông báo"
        onClick={() => setThongBao(null)}
        className="shrink-0 font-semibold"
      >
        ×
      </button>
    </div>
  )
}