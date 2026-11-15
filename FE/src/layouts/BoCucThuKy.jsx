import DanhSachDeXuatThuKy from '../pages/thuky/DanhSachDeXuatThuKy.jsx'
import PhanHoiLoiMoi from '../pages/thamdu/PhanHoiLoiMoi.jsx'
import KetLuanDaPhatHanh from '../pages/thamdu/KetLuanDaPhatHanh.jsx'
import BieuQuyetThamDu from '../pages/thamdu/BieuQuyetThamDu.jsx'

export default function BoCucThuKy({ nguoiDung, loi, onDangXuat }) {
  return (
    <div className="min-h-screen bg-slate-100 text-slate-900 md:grid md:grid-cols-[224px_1fr]">
      <aside className="bg-emerald-950 px-5 py-6 text-white">
        <p className="font-semibold">Quản lý cuộc họp</p>
        <p className="mt-1 text-sm text-emerald-100">Không gian thư ký</p>

        <nav aria-label="Điều hướng thư ký" className="mt-6">
          <a
            href="#tong-quan"
            aria-current="page"
            className="block rounded-lg bg-emerald-800 px-3 py-2 text-sm font-medium"
          >
            Tổng quan
          </a>
        </nav>
      </aside>

      <div className="min-w-0">
        <header className="flex items-center justify-between gap-4 border-b border-slate-200 bg-white px-5 py-4 md:px-8">
          <div>
            <p className="font-semibold">{nguoiDung.hoTen}</p>
            <p className="text-sm text-slate-600">
              {nguoiDung.tenVaiTro || 'Thư ký'}
            </p>
          </div>

          <button
            type="button"
            onClick={onDangXuat}
            className="rounded-lg border border-slate-300 px-3 py-2 text-sm font-semibold hover:bg-slate-50"
          >
            Đăng xuất
          </button>
        </header>

        <main id="tong-quan" className="mx-auto max-w-6xl space-y-5 p-5 md:p-8">
          {loi && (
            <p role="alert" className="rounded-lg bg-red-50 p-3 text-sm text-red-800">
              {loi}
            </p>
          )}

          <h1 className="text-2xl font-bold">Tổng quan</h1>

          <section className="rounded-xl border border-slate-200 bg-white p-5">
            <h2 className="font-semibold">Thông tin công tác</h2>
            <dl className="mt-4 grid gap-4 text-sm sm:grid-cols-2">
              <div>
                <dt className="text-slate-600">Họ và tên</dt>
                <dd className="mt-1 font-medium">{nguoiDung.hoTen}</dd>
              </div>
              <div>
                <dt className="text-slate-600">Phòng ban</dt>
                <dd className="mt-1 font-medium">
                  {nguoiDung.tenPhongBan || 'Chưa có thông tin'}
                </dd>
              </div>
            </dl>
          </section>
          <DanhSachDeXuatThuKy token={nguoiDung.token} />
            <PhanHoiLoiMoi token={nguoiDung.token} />
            <BieuQuyetThamDu token={nguoiDung.token} />
            <KetLuanDaPhatHanh token={nguoiDung.token} />
        </main>
      </div>
    </div>
  )
}