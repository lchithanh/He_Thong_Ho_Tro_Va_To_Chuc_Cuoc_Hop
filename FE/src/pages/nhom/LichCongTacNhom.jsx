import { useEffect, useState } from 'react'
import { goiNhomLienPhong } from '../../api/nhomLienPhongApi.js'
import { dinhDangNgayGio } from '../../utils/thoiGianCuocHop.js'
import { useDongBoDuLieu } from '../../services/useDongBoDuLieu.js'

export default function LichCongTacNhom({ token }) {
  const phienDongBo = useDongBoDuLieu()
  const [chonNgay, setChonNgay] = useState('')
  const [homNay] = useState(() => new Intl.DateTimeFormat('sv-SE', {
    timeZone: 'Asia/Ho_Chi_Minh', year: 'numeric', month: '2-digit', day: '2-digit',
  }).format(new Date()))
  const ngay = chonNgay || homNay
  const [ketQua, setKetQua] = useState(null)

  useEffect(() => {
    const controller = new AbortController()
    goiNhomLienPhong('/lich-cong-tac?' + new URLSearchParams({ ngay }), token, { signal: controller.signal })
      .then((danhSach) => {
        if (!controller.signal.aborted) setKetQua({ ngay, danhSach })
      })
      .catch((error) => {
        if (!controller.signal.aborted) setKetQua({ ngay, loi: error.message })
      })
    return () => controller.abort()
  }, [ngay, token, phienDongBo])

  const tuan = ketQua?.ngay === ngay ? ketQua : null
  return <section className="space-y-3 rounded-xl border border-slate-200 bg-white p-4">
    <div className="flex flex-wrap items-center justify-between gap-3">
      <h2 className="font-semibold">Lịch công tác tuần của các nhóm</h2>
      <label className="flex items-center gap-2 text-sm">Ngày trong tuần
        <input type="date" className="rounded-lg border border-slate-300 p-2" value={ngay}
          onChange={(event) => setChonNgay(event.target.value)} />
      </label>
    </div>
    {!tuan && <p role="status" className="text-sm">Đang tải lịch…</p>}
    {tuan?.loi && <p role="alert" className="text-sm text-red-800">{tuan.loi}</p>}
    {tuan?.danhSach?.length === 0 && <p className="text-sm text-slate-600">Chưa có lịch họp nhóm trong tuần này.</p>}
    {tuan?.danhSach?.length > 0 && <ul className="divide-y divide-slate-200">
      {tuan.danhSach.map((lich) => <li key={lich.lichHopId} className="py-2 text-sm">
        <strong>{lich.tieuDe}</strong> · {lich.tenNhom}
        <p>{dinhDangNgayGio(lich.batDau)} đến {dinhDangNgayGio(lich.ketThuc)} · {lich.tenPhong || 'Trực tuyến'}</p>
        <p className="text-slate-600">{lich.trangThai === 'DA_XAC_NHAN' ? 'Đã xác nhận' : 'Dự kiến'}</p>
      </li>)}
    </ul>}
  </section>
}
