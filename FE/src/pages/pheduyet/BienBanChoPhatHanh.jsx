import { useEffect, useState } from 'react'
import { useDongBoDuLieu } from '../../services/useDongBoDuLieu.js'
import { layBienBanChoToiPhatHanh } from '../../api/bienBanApi.js'
import VanBanCuaHoSo from '../thuky/VanBanCuaHoSo.jsx'

export default function BienBanChoPhatHanh({ token }) {
  const phienDongBo = useDongBoDuLieu()
  const [danhSach, setDanhSach] = useState(null)
  const [dangMo, setDangMo] = useState({})
  const [loi, setLoi] = useState('')
  const [thongBao, setThongBao] = useState('')

  useEffect(() => {
    const controller = new AbortController()
    layBienBanChoToiPhatHanh(token, controller.signal)
      .then((duLieu) => { if (!controller.signal.aborted) setDanhSach(duLieu) })
      .catch((error) => { if (!controller.signal.aborted) setLoi(error.message) })
    return () => controller.abort()
  }, [token, phienDongBo])

  return (
    <section className="space-y-4 rounded-xl border border-slate-200 bg-white p-5">
      <h2 className="text-lg font-semibold">Biên bản chờ phát hành</h2>
      {loi && <p role="alert" className="text-sm text-red-800">{loi}</p>}
      {thongBao && <p role="status" className="text-sm text-emerald-800">{thongBao}</p>}
      {danhSach === null && !loi && <p role="status">Đang tải danh sách biên bản…</p>}
      {danhSach?.length === 0 && <p className="text-sm text-slate-600">Chưa có biên bản chờ bạn phát hành.</p>}
      {danhSach?.map((muc) => (
        <details key={muc.bienBanId} className="space-y-3 rounded-lg border border-slate-200 p-4"
          onToggle={(event) => {
            const mo = event.currentTarget.open
            setDangMo((hienTai) => ({ ...hienTai, [muc.bienBanId]: mo }))
          }}>
          <summary className="cursor-pointer font-semibold">{muc.tieuDeCuocHop || muc.tieuDe}</summary>
          {dangMo[muc.bienBanId] && (
            <div className="mt-4">
              <VanBanCuaHoSo loai="bien-ban" cuocHopId={muc.cuocHopId} token={token}
                choPhepXuLy onPhatHanh={() => {
                  setDanhSach((hienTai) => hienTai.filter((bienBan) => bienBan.bienBanId !== muc.bienBanId))
                  setThongBao('Đã phát hành biên bản cuộc họp.')
                }} />
            </div>
          )}
        </details>
      ))}
    </section>
  )
}
