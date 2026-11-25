import { useEffect, useState } from 'react'
import CalendarDays from 'lucide-react/dist/esm/icons/calendar-days.mjs'
import { TieuDeHoSo } from '../../components/GiaoDienPhongHop.jsx'
import { LOP_NUT_CHINH, LOP_NUT_PHU, LOP_THE } from '../../components/giaoDienPhongHop.js'
import { layLoiMoiDoiLich, phanHoiDoiLich } from '../../api/nghiepVuCuocHopApi.js'
import { useDongBoDuLieu } from '../../services/useDongBoDuLieu.js'
import { dinhDangNgayGio } from '../../utils/thoiGianCuocHop.js'

export default function PhanHoiDoiLich({ token }) {
  const phien = useDongBoDuLieu()
  const [danhSach, setDanhSach] = useState([])
  const [loi, setLoi] = useState('')
  const [dangGui, setDangGui] = useState(null)

  useEffect(() => {
    const controller = new AbortController()
    layLoiMoiDoiLich(token, controller.signal).then((muc) => {
      if (!controller.signal.aborted) setDanhSach(muc)
    }).catch((error) => {
      if (!controller.signal.aborted) setLoi(error.message)
    })
    return () => controller.abort()
  }, [token, phien])

  async function gui(id, dongY) {
    setDangGui(id)
    setLoi('')
    try {
      await phanHoiDoiLich(id, dongY, token)
      setDanhSach((ds) => ds.map((muc) => muc.lichHopId === id ? { ...muc, dongY } : muc))
    } catch (error) {
      setLoi(error.message)
    } finally {
      setDangGui(null)
    }
  }

  if (!danhSach.length && !loi) return null
  return (
    <section className={LOP_THE + ' space-y-3'}>
      <TieuDeHoSo Icon={CalendarDays} cap="h2">Phản hồi lịch họp mới</TieuDeHoSo>
      {loi && <p role="alert" className="text-sm text-rose-800">{loi}</p>}
      {danhSach.map((muc) => (
        <article key={muc.lichHopId} className="space-y-2 rounded-xl border border-blue-100 bg-blue-50 p-4">
          <h3 className="font-semibold">{muc.tieuDe}</h3>
          <dl className="grid gap-2 text-sm sm:grid-cols-2">
            <div><dt className="font-semibold">Lịch cũ</dt><dd>{dinhDangNgayGio(muc.batDauCu)} – {dinhDangNgayGio(muc.ketThucCu)}</dd></div>
            <div><dt className="font-semibold">Lịch mới</dt><dd>{dinhDangNgayGio(muc.batDauMoi)} – {dinhDangNgayGio(muc.ketThucMoi)}</dd></div>
            <div className="sm:col-span-2"><dt className="font-semibold">Lý do dời lịch</dt><dd className="whitespace-pre-wrap wrap-anywhere">{muc.lyDo}</dd></div>
          </dl>
          <p role="status" className="text-sm text-slate-700">
            {muc.dongY == null ? 'Chưa phản hồi' : muc.dongY ? 'Bạn đã đồng ý lịch mới' : 'Bạn không đồng ý lịch mới'}
            {!muc.conHan && ' · Đã hết thời gian phản hồi'}
          </p>
          <div className="flex flex-wrap gap-2">
            <button type="button" className={LOP_NUT_CHINH} disabled={dangGui != null || !muc.conHan || muc.dongY === true}
              onClick={() => gui(muc.lichHopId, true)}>Đồng ý lịch mới</button>
            <button type="button" className={LOP_NUT_PHU} disabled={dangGui != null || !muc.conHan || muc.dongY === false}
              onClick={() => gui(muc.lichHopId, false)}>Không đồng ý</button>
          </div>
        </article>
      ))}
    </section>
  )
}