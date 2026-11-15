import { useEffect, useState } from 'react'
import CalendarDays from 'lucide-react/dist/esm/icons/calendar-days.mjs'
import CheckCircle2 from 'lucide-react/dist/esm/icons/check-circle-2.mjs'
import ShieldCheck from 'lucide-react/dist/esm/icons/shield-check.mjs'
import Sparkles from 'lucide-react/dist/esm/icons/sparkles.mjs'
import { dangNhap, layThongTinNguoiDung } from './api/authApi.js'
import KhongGianLamViec from './layouts/KhongGianLamViec.jsx'

const KHOA_PHIEN = 'phienDangNhap'

const THONG_BAO_LOI = {
  400: 'Vui lòng nhập tài khoản và mật khẩu.',
  401: 'Tài khoản hoặc mật khẩu không chính xác.',
  403: 'Tài khoản chưa được cấp quyền hoặc đã ngừng hoạt động.',
}

function docPhienDangNhap() {
  try {
    const phien = JSON.parse(sessionStorage.getItem(KHOA_PHIEN))
    return phien?.token ? phien : null
  } catch {
    return null
  }
}

export default function App() {
  const [dangGui, setDangGui] = useState(false)
  const [loi, setLoi] = useState('')
  const [nguoiDung, setNguoiDung] = useState(docPhienDangNhap)

  const token = nguoiDung?.token

  useEffect(() => {
    if (!token) return

    const controller = new AbortController()

    async function kiemTraPhien() {
      try {
        const thongTin = await layThongTinNguoiDung(token, controller.signal)
        if (controller.signal.aborted) return

        const phien = { ...thongTin, token }
        sessionStorage.setItem(KHOA_PHIEN, JSON.stringify(phien))
        setNguoiDung(phien)
      } catch (loiKiemTra) {
        if (controller.signal.aborted) return

        if ([401, 403].includes(loiKiemTra.status)) {
          sessionStorage.removeItem(KHOA_PHIEN)
          setNguoiDung(null)
          setLoi('Phiên không còn hợp lệ. Vui lòng đăng nhập lại.')
        } else {
          setLoi('Chưa kiểm tra được phiên. Hãy tải lại trang để thử lại.')
        }
      }
    }

    kiemTraPhien()
    return () => controller.abort()
  }, [token])

  async function xuLyDangNhap(event) {
    event.preventDefault()
    if (dangGui) return

    const form = new FormData(event.currentTarget)
    const taiKhoan = form.get('taiKhoan').trim()
    const matKhau = form.get('matKhau')

    setLoi('')

    if (!taiKhoan || !matKhau.trim()) {
      setLoi(THONG_BAO_LOI[400])
      return
    }

    setDangGui(true)

    try {
      const ketQua = await dangNhap(taiKhoan, matKhau)

      if (!ketQua?.token) {
        setLoi('Backend chưa trả về mã đăng nhập.')
        return
      }

      const phien = {
        token: ketQua.token,
        nguoiDungId: ketQua.nguoiDungId,
        hoTen: ketQua.hoTen,
        maVaiTro: ketQua.maVaiTro,
      }

      sessionStorage.setItem(KHOA_PHIEN, JSON.stringify(phien))
      setNguoiDung(phien)
    } catch (loiDangNhap) {
      setLoi(
        loiDangNhap.status
          ? THONG_BAO_LOI[loiDangNhap.status] || 'Đăng nhập thất bại.'
          : 'Không đọc được phản hồi. Kiểm tra Spring Boot và địa chỉ API.',
      )
    } finally {
      setDangGui(false)
    }
  }

  function dangXuat() {
    sessionStorage.removeItem(KHOA_PHIEN)
    setNguoiDung(null)
    setLoi('')
  }
  if (nguoiDung) {
    return (
      <KhongGianLamViec
        nguoiDung={nguoiDung}
        loi={loi}
        onDangXuat={dangXuat}
      />
    )
  }

  return (
    <main className="min-h-screen bg-[#f3f6f5] p-4 sm:p-6 lg:grid lg:place-items-center">
      <div className="mx-auto grid min-h-[min(760px,calc(100vh-3rem))] w-full max-w-6xl overflow-hidden rounded-[28px] bg-white shadow-xl shadow-slate-900/10 lg:grid-cols-[1.05fr_0.95fr]">
        <section className="relative hidden overflow-hidden bg-gradient-to-br from-[#123a32] via-[#145345] to-[#18705a] p-10 text-white lg:flex lg:flex-col lg:justify-between xl:p-14">
          <div className="absolute -right-20 -top-20 size-96 rounded-full border border-white/10" />
          <div className="absolute -bottom-36 -left-24 size-[30rem] rounded-full border border-white/10" />
          <div className="relative z-10 flex items-center gap-3">
            <span className="grid size-12 place-items-center rounded-2xl bg-white/10 text-emerald-50">
              <CalendarDays size={25} />
            </span>
            <span>
              <span className="block text-sm font-bold tracking-[0.12em]">HỌP HÀNH CHÍNH</span>
              <span className="mt-1 block text-xs text-emerald-100/70">Nền tảng điều phối cuộc họp</span>
            </span>
          </div>

          <div className="relative z-10 max-w-lg">
            <span className="inline-flex items-center gap-2 rounded-full border border-white/15 bg-white/10 px-3 py-1.5 text-xs font-medium text-emerald-50">
              <Sparkles size={14} />
              Quản lý tập trung, phối hợp hiệu quả
            </span>
            <h1 className="mt-6 text-4xl font-bold leading-tight tracking-tight xl:text-5xl">
              Mọi cuộc họp, trong một quy trình rõ ràng.
            </h1>
            <p className="mt-5 max-w-md text-base leading-7 text-emerald-50/75">
              Theo dõi đề xuất, kế hoạch, thành viên, biểu quyết và kết luận trên cùng một không gian làm việc.
            </p>
            <ul className="mt-8 space-y-4 text-sm text-emerald-50/85">
              {[
                'Quản lý trạng thái cuộc họp theo thời gian',
                'Ghi nhận điểm danh và kết quả biểu quyết',
                'Tập trung biên bản, kết luận và tài liệu',
              ].map((muc) => (
                <li key={muc} className="flex items-center gap-3">
                  <CheckCircle2 size={17} className="text-emerald-200" />
                  {muc}
                </li>
              ))}
            </ul>
          </div>

          <p className="relative z-10 text-xs text-emerald-100/55">Hệ thống quản lý và hỗ trợ tổ chức cuộc họp</p>
        </section>

        <section className="flex items-center justify-center px-5 py-10 sm:px-10 lg:px-12 xl:px-16">
          <div className="w-full max-w-md">
            <div className="mb-8 flex items-center gap-3 lg:hidden">
              <span className="grid size-11 place-items-center rounded-2xl bg-emerald-900 text-white">
                <CalendarDays size={22} />
              </span>
              <span>
                <span className="block text-sm font-bold tracking-wide text-slate-900">HỌP HÀNH CHÍNH</span>
                <span className="mt-0.5 block text-xs text-slate-500">Điều phối cuộc họp</span>
              </span>
            </div>

            <div className="mb-7">
              <span className="grid size-12 place-items-center rounded-2xl bg-emerald-50 text-emerald-800">
                <ShieldCheck size={24} />
              </span>
              <h2 className="mt-5 text-3xl font-bold tracking-tight text-slate-900">Đăng nhập</h2>
              <p className="mt-2 text-sm leading-6 text-slate-500">Sử dụng tài khoản được cấp để vào không gian làm việc của bạn.</p>
            </div>

            {loi && (
              <p role="alert" className="mb-5 rounded-xl border border-red-200 bg-red-50 p-3.5 text-sm text-red-800">
                {loi}
              </p>
            )}

            <form onSubmit={xuLyDangNhap} className="space-y-5">
              <label className="block text-sm font-semibold text-slate-800">
                Tên đăng nhập hoặc email
                <input
                  name="taiKhoan"
                  autoComplete="username"
                  required
                  className="mt-2 w-full rounded-xl border border-slate-200 bg-white px-3.5 py-3 font-normal outline-none transition placeholder:text-slate-400 focus:border-emerald-600 focus:ring-4 focus:ring-emerald-100"
                  placeholder="Nhập tài khoản"
                />
              </label>

              <label className="block text-sm font-semibold text-slate-800">
                Mật khẩu
                <input
                  name="matKhau"
                  type="password"
                  autoComplete="current-password"
                  required
                  className="mt-2 w-full rounded-xl border border-slate-200 bg-white px-3.5 py-3 font-normal outline-none transition placeholder:text-slate-400 focus:border-emerald-600 focus:ring-4 focus:ring-emerald-100"
                  placeholder="Nhập mật khẩu"
                />
              </label>

              <button
                type="submit"
                disabled={dangGui}
                className="mt-2 w-full rounded-xl bg-emerald-800 px-4 py-3 font-semibold text-white shadow-sm transition hover:bg-emerald-900 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-emerald-700 disabled:cursor-not-allowed disabled:opacity-60"
              >
                {dangGui ? 'Đang đăng nhập…' : 'Đăng nhập'}
              </button>
            </form>
            <p className="mt-8 text-center text-xs text-slate-400">Kết nối an toàn · Hỗ trợ tổ chức cuộc họp</p>
          </div>
        </section>
      </div>
    </main>
  )
}
