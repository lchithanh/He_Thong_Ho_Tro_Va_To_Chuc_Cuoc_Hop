import { useDongBoDuLieu } from '../../services/useDongBoDuLieu.js'
import { dinhDangNgayGio } from '../../utils/thoiGianCuocHop.js'
import { useEffect, useState } from 'react'
import TepThuMoiFields from './TepThuMoiFields.jsx'
import { tenNguoiNhanGiayMoi, themTepVaoThuMoi, thongTinGiayMoi } from './taoGiayMoi.js'
import { chiaSeTaiLieuKhach, chiaSeTepCoSan, taiLieuCoTheChiaSe, layKhachNgoaiCuocHop, moiKhachNgoaiQuaEmail } from '../../api/khachNgoaiCuocHopApi.js'

const TRANG_THAI = {
  CHO_PHAN_HOI: 'Chờ phản hồi',
  DONG_Y: 'Đã đồng ý',
  TU_CHOI: 'Đã từ chối',
  DA_HUY: 'Đã hủy',
}

const O_NHAP = 'mt-1 block w-full rounded-lg border border-slate-300 px-3 py-2 text-sm'

export default function KhachNgoaiCuocHop({ cuocHopId, token, laThuKy, choPhepMoi, trucTuyen, thongTinCuocHop }) {
  const phienDongBo = useDongBoDuLieu()
  const [danhSach, setDanhSach] = useState(null)
  const [loiDanhSach, setLoiDanhSach] = useState('')
  const [loiGui, setLoiGui] = useState('')
  const [thongBao, setThongBao] = useState('')
  const [dangGui, setDangGui] = useState(false)
  const [tepCoSan, setTepCoSan] = useState(null)

  useEffect(() => {
    const controller = new AbortController()
    layKhachNgoaiCuocHop(cuocHopId, token, controller.signal)
      .then(setDanhSach)
      .catch((loi) => {
        if (!controller.signal.aborted) setLoiDanhSach(loi.message)
      })
    return () => controller.abort()
  }, [cuocHopId, token, phienDongBo])

  function docThongTinKhach(duLieu) {
    return Object.fromEntries(['hoTen', 'email', 'donViCongTac', 'chucVu', 'truSo', 'duongDanHop']
      .map((ten) => [ten, duLieu.get(ten)?.trim() || '']))
  }

  function layThongTin(duLieu) {
    const khach = docThongTinKhach(duLieu)
    return thongTinGiayMoi(thongTinCuocHop, tenNguoiNhanGiayMoi(khach), khach.duongDanHop)
  }

  async function guiThuMoi(event) {
    event.preventDefault()
    if (dangGui || !choPhepMoi || !thongTinCuocHop) return

    const form = event.currentTarget
    const duLieu = new FormData(form)
    const khach = docThongTinKhach(duLieu)
    setDangGui(true)
    setLoiGui('')
    setThongBao('')
    try {
      const loiMoi = await moiKhachNgoaiQuaEmail(cuocHopId, token, {
        ...khach, thuMoi: await themTepVaoThuMoi({}, duLieu, layThongTin(duLieu)),
      })
      setDanhSach((hienTai) => [loiMoi, ...(hienTai ?? []).filter((muc) => muc.khachMoiCuocHopId !== loiMoi.khachMoiCuocHopId)])
      setThongBao('Đã gửi thư mời đến ' + loiMoi.email + '.')
      form.reset()
    } catch (loi) {
      setLoiGui(loi.message)
    } finally {
      setDangGui(false)
    }
  }

  async function chiaSe(event) {
    event.preventDefault()
    const form = event.currentTarget
    setDangGui(true)
    setLoiGui('')
    setThongBao('')
    try {
      await chiaSeTaiLieuKhach(cuocHopId, token, new FormData(form))
      form.reset()
      setThongBao('Đã chia sẻ tài liệu trong hồ sơ dành cho khách.')
    } catch (error) {
      setLoiGui(error.message)
    } finally {
      setDangGui(false)
    }
  }

  async function layTepCoSan() {
    setLoiGui('')
    try {
      setTepCoSan(await taiLieuCoTheChiaSe(cuocHopId, token))
    } catch (error) {
      setLoiGui(error.message)
    }
  }

  async function chiaSeCoSan(taiLieuId) {
    setDangGui(true)
    setLoiGui('')
    try {
      await chiaSeTepCoSan(cuocHopId, token, taiLieuId)
      setThongBao('Đã chia sẻ tài liệu trong hồ sơ dành cho khách.')
      await layTepCoSan()
    } catch (error) {
      setLoiGui(error.message)
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
              <p className="text-xs text-slate-600">Đã mời · {dinhDangNgayGio(khach.ngayMoi)}</p>
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
              Đơn vị công tác hiện tại
              <input className={O_NHAP} name="donViCongTac" maxLength={200} />
            </label>
            <label className="block text-sm font-medium">
              Chức vụ
              <input className={O_NHAP} name="chucVu" maxLength={120} />
            </label>
            <label className="block text-sm font-medium">
              Trụ sở đơn vị công tác (nếu có)
              <input className={O_NHAP} name="truSo" maxLength={250} />
            </label>
            {trucTuyen && (
              <label className="block text-sm font-medium">
                Link họp trực tuyến
                <input className={O_NHAP} name="duongDanHop" type="url" maxLength={500} placeholder="https://" required />
              </label>
            )}
            <TepThuMoiFields layThongTin={layThongTin} thongTinCuocHop={thongTinCuocHop} />
            <button type="submit" className="rounded-lg bg-emerald-700 px-3 py-2 text-sm font-semibold text-white disabled:opacity-60">
              {dangGui ? 'Đang xử lý…' : 'Gửi thư mời hoặc gửi bản cập nhật'}
            </button>
          </fieldset>
        </form>
      )}
      {laThuKy && <form className="space-y-2 border-t pt-3" onSubmit={chiaSe}>
        <label className="block text-sm font-semibold">Thêm tài liệu bổ sung cho tất cả khách của cuộc họp
          <input type="file" name="tep" className={O_NHAP} accept=".pdf,.doc,.docx,.xls,.xlsx" required disabled={dangGui} />
        </label>
        <button type="submit" className="rounded-lg border px-3 py-2 text-sm font-semibold" disabled={dangGui}>Chia sẻ tài liệu</button>
      </form>}
      {laThuKy && <div className="space-y-2">
        <button className="text-sm font-semibold underline" type="button" disabled={dangGui} onClick={layTepCoSan}>
          Chọn tệp từ đề xuất hoặc kế hoạch
        </button>
        {tepCoSan?.length === 0 && <p className="text-sm">Chưa có tệp đề xuất hoặc kế hoạch.</p>}
        {tepCoSan?.map((tep) => <div key={tep.taiLieuId} className="flex items-center justify-between gap-2 border-b py-2 text-sm">
          <span className="break-words">{tep.tenTaiLieu}</span>
          {tep.daChiaSe ? <span>Đã chia sẻ</span> :
            <button type="button" disabled={dangGui} className="font-semibold underline"
              onClick={() => chiaSeCoSan(tep.taiLieuId)}>Chia sẻ cho khách</button>}
        </div>)}
      </div>}
      {loiGui && <p role="alert" className="text-sm text-red-700">{loiGui}</p>}
      {thongBao && <p role="status" className="break-words text-sm text-emerald-800">{thongBao}</p>}
    </section>
  )
}
