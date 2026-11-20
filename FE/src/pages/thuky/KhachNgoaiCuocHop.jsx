import { useEffect, useState } from 'react'
import { layKhachNgoaiCuocHop, moiKhachNgoaiQuaEmail } from '../../api/khachNgoaiCuocHopApi.js'

const TRANG_THAI = {
  CHO_PHAN_HOI: 'Chờ phản hồi',
  DONG_Y: 'Đã đồng ý',
  TU_CHOI: 'Đã từ chối',
  DA_HUY: 'Đã hủy',
}

const O_NHAP = 'mt-1 block w-full rounded-lg border border-slate-300 px-3 py-2 text-sm'

export default function KhachNgoaiCuocHop({ cuocHopId, token, laThuKy, choPhepMoi, trucTuyen }) {
  const [danhSach, setDanhSach] = useState(null)
  const [loiDanhSach, setLoiDanhSach] = useState('')
  const [loiGui, setLoiGui] = useState('')
  const [thongBao, setThongBao] = useState('')
  const [dangGui, setDangGui] = useState(false)

  useEffect(() => {
    const controller = new AbortController()
    layKhachNgoaiCuocHop(cuocHopId, token, controller.signal)
      .then(setDanhSach)
      .catch((loi) => {
        if (!controller.signal.aborted) setLoiDanhSach(loi.message)
      })
    return () => controller.abort()
  }, [cuocHopId, token])

  async function guiThuMoi(event) {
    event.preventDefault()
    if (dangGui || !choPhepMoi) return

    const form = event.currentTarget
    const duLieu = Object.fromEntries(
      Array.from(new FormData(form), ([ten, giaTri]) => [ten, giaTri.trim()]),
    )
    setDangGui(true)
    setLoiGui('')
    setThongBao('')
    try {
      const loiMoi = await moiKhachNgoaiQuaEmail(cuocHopId, token, duLieu)
      setDanhSach((hienTai) => [loiMoi, ...(hienTai ?? [])])
      setThongBao('Đã gửi thư mời đến ' + loiMoi.email + '.')
      form.reset()
    } catch (loi) {
      setLoiGui(loi.message)
    } finally {
      setDangGui(false)
    }
  }

  return (
    <section className="space-y-3 rounded-2xl border border-slate-200 bg-white p-5">
      <h2 className="text-lg font-bold text-slate-900">Khách mời bên ngoài</h2>
      {loiDanhSach && <p role="alert" className="text-sm text-red-700">{loiDanhSach}</p>}
      {!danhSach && !loiDanhSach && <p role="status" className="text-sm text-slate-600">Đang tải khách mời…</p>}
      {danhSach?.length === 0 && <p className="text-sm text-slate-600">Chưa có khách mời bên ngoài.</p>}
      {danhSach?.length > 0 && (
        <ul className="divide-y divide-slate-100">
          {danhSach.map((khach) => (
            <li key={khach.khachMoiCuocHopId} className="space-y-1 py-2 text-sm">
              <p className="font-semibold text-slate-900">{khach.hoTen}</p>
              <p className="break-all text-slate-600">{khach.email}</p>
              <p className="text-xs font-medium text-slate-600">{TRANG_THAI[khach.trangThaiMoi] ?? khach.trangThaiMoi}</p>
            </li>
          ))}
        </ul>
      )}
      {laThuKy && !choPhepMoi && (
        <p className="text-sm text-slate-600">Có thể gửi thư sau khi lịch được xác nhận và trước khi cuộc họp bắt đầu.</p>
      )}
      {choPhepMoi && (
        <form className="space-y-3 border-t border-slate-100 pt-3" onSubmit={guiThuMoi}>
          <fieldset disabled={dangGui || danhSach === null} className="space-y-3">
            <label className="block text-sm font-medium">
              Họ và tên
              <input className={O_NHAP} name="hoTen" maxLength={150} required />
            </label>
            <label className="block text-sm font-medium">
              Email
              <input className={O_NHAP} name="email" type="email" maxLength={190} required />
            </label>
            <label className="block text-sm font-medium">
              Đơn vị công tác
              <input className={O_NHAP} name="donViCongTac" maxLength={200} />
            </label>
            <label className="block text-sm font-medium">
              Chức vụ
              <input className={O_NHAP} name="chucVu" maxLength={120} />
            </label>
            {trucTuyen && (
              <label className="block text-sm font-medium">
                Link họp trực tuyến
                <input className={O_NHAP} name="duongDanHop" type="url" maxLength={500} placeholder="https://" required />
              </label>
            )}
            <button type="submit" className="rounded-lg bg-emerald-700 px-3 py-2 text-sm font-semibold text-white disabled:opacity-60">
              {dangGui ? 'Đang gửi…' : 'Gửi thư mời'}
            </button>
          </fieldset>
          {loiGui && <p role="alert" className="text-sm text-red-700">{loiGui}</p>}
          {thongBao && <p role="status" className="break-words text-sm text-emerald-800">{thongBao}</p>}
        </form>
      )}
    </section>
  )
}
