import { dinhDangNgayGio } from '../../utils/thoiGianCuocHop.js'
import { useEffect, useState } from 'react'
import { useDongBoDuLieu } from '../../services/useDongBoDuLieu.js'
import CalendarDays from 'lucide-react/dist/esm/icons/calendar-days.mjs'
import CheckCircle2 from 'lucide-react/dist/esm/icons/check-circle-2.mjs'
import CircleDot from 'lucide-react/dist/esm/icons/circle-dot.mjs'
import Clock3 from 'lucide-react/dist/esm/icons/clock-3.mjs'
import FileClock from 'lucide-react/dist/esm/icons/file-clock.mjs'
import Inbox from 'lucide-react/dist/esm/icons/inbox.mjs'
import { layDanhSachDeXuat, layHoSoThuKyLanhDao, layTrangThaiHienThiCuocHop } from '../../api/cuocHopApi.js'
import NhanTrangThai from './NhanTrangThaiDeXuat.jsx'
import NoiDungPhanHoi from './NoiDungPhanHoi.jsx'
import { canChinhSuaKeHoach, canChuanBiCuocHopDuocGiao, khopTrangThaiHoSo, layNhanTrangThaiHoSo, TEN_PHAM_VI, TEN_TRANG_THAI } from './hienThiDeXuat.js'
import ChiTietDeXuat from './ChiTietDeXuat.jsx'

export { ChiTietDeXuat }
export { default as LichCuaHoSo } from './LichCuaHoSo.jsx'
export { default as HoSoDaTrinh } from './HoSoDaTrinh.jsx'

import LichCongTacNhom from '../nhom/LichCongTacNhom.jsx'

export default function DanhSachDeXuatThuKy({ token, nguoiDung, onLapLich, onMoPhongHop, tuViecChoXuLy, chiXemPhanHoi = false, chiXemBanGiao = false, chiXemRaSoat = false, chiXemChuyenCap = false, hienLichCongTacNhom = false }) {
  const phienDongBo = useDongBoDuLieu()
  const [danhSach, setDanhSach] = useState(null)
  const [loi, setLoi] = useState('')
  const [lanTai, setLanTai] = useState(0)
  const [cuocHopDangXem, setCuocHopDangXem] = useState(null)
  const [tuKhoa, setTuKhoa] = useState('')
  const [phamVi, setPhamVi] = useState('')
  const [trangThai, setTrangThai] = useState('')


  useEffect(() => {
    if (!token) return

    const controller = new AbortController()

    async function taiDanhSach() {
      try {
        const taiDuLieu = chiXemRaSoat ? layHoSoThuKyLanhDao : layDanhSachDeXuat
        const duLieu = await taiDuLieu(token, controller.signal)
        if (!controller.signal.aborted) { setDanhSach(duLieu); setLoi('') }
      } catch (loiTaiDanhSach) {
        if (controller.signal.aborted) return

        setLoi(
          loiTaiDanhSach.status === 403
            ? 'Tài khoản không có quyền xem danh sách đề xuất.'
            : 'Không tải được danh sách đề xuất. Hãy tải lại trang để thử lại.',
        )
      }
    }

    taiDanhSach()
    return () => controller.abort()
  }, [token, lanTai, chiXemRaSoat, phienDongBo])

  if (cuocHopDangXem !== null) {
    return (
      <ChiTietDeXuat
        cuocHopId={cuocHopDangXem}
        token={token}
        nguoiDung={nguoiDung}
        onLapLich={onLapLich}
        onMoPhongHop={onMoPhongHop}
        tabBanDau={chiXemRaSoat || chiXemChuyenCap || chiXemPhanHoi ? 'ke-hoach' : 'thong-tin'}
        onQuayLai={() => {
          setCuocHopDangXem(null)
          setLanTai((lan) => lan + 1)
        }}
        onDaTiepNhan={(cuocHopId, thayDoi) => {
          setDanhSach((danhSachHienTai) =>
            danhSachHienTai.map((deXuat) =>
              deXuat.cuocHopId === cuocHopId
                ? { ...deXuat, ...thayDoi }
                : deXuat,
            ),
          )
        }}
      />
    )
  }

  if (chiXemRaSoat) {
    return (
      <section className="space-y-4">
        <h1 className="text-2xl font-bold">Hồ sơ chờ rà soát</h1>
        {hienLichCongTacNhom && <LichCongTacNhom token={token} />}
        {loi ? <p role="alert">{loi}</p> : !danhSach ? <p role="status">Đang tải hồ sơ…</p>
          : !danhSach.length && <p>Chưa có hồ sơ chờ rà soát.</p>}
        {danhSach?.map((muc) => (
          <article key={muc.yeuCauPheDuyetId} className="flex flex-wrap items-center justify-between gap-3 rounded-xl border border-slate-200 bg-white p-4">
            <div><h2 className="font-semibold">{muc.tieuDe}</h2>
              <p className="text-sm text-slate-600">Đã trình {dinhDangNgayGio(muc.thoiGianTrinh)}</p></div>
            <button type="button" className="rounded-lg border px-3 py-2 text-sm font-semibold text-emerald-800"
              onClick={() => setCuocHopDangXem(muc.cuocHopId)}>Xem hồ sơ</button>
          </article>
        ))}
      </section>
    )
  }

  if (chiXemPhanHoi) {
    const phanHoi = danhSach?.filter((muc) => tuViecChoXuLy ? canChinhSuaKeHoach(muc) : Boolean(muc.phanHoiKeHoach))
      .sort((a, b) => (b.phanHoiKeHoach.thoiGian ?? '').localeCompare(a.phanHoiKeHoach.thoiGian ?? ''))
    return (
      <section className="space-y-4">
        <div className="flex flex-wrap items-center justify-between gap-3">
          <h1 className="text-2xl font-bold">{tuViecChoXuLy ? 'Kế hoạch cần chỉnh sửa' : 'Phản hồi kế hoạch'}</h1>
          <button type="button" onClick={() => setLanTai((lan) => lan + 1)}
            className="rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm font-semibold">Tải lại</button>
        </div>
        {loi ? <p role="alert" className="text-sm text-red-800">{loi}</p>
          : phanHoi == null ? <p role="status">Đang tải phản hồi…</p>
            : !phanHoi.length && <p className="text-sm text-slate-600">{tuViecChoXuLy ? 'Chưa có kế hoạch cần chỉnh sửa.' : 'Chưa có kế hoạch bị trả lại hoặc từ chối.'}</p>}
        {phanHoi?.map((muc) => (
          <article key={muc.cuocHopId} className="space-y-2 rounded-xl border border-slate-200 bg-white p-4">
            <div className="flex flex-wrap items-center gap-2">
              <h2 className="font-semibold">{muc.tieuDe}</h2>
              <NhanTrangThai trangThai={muc.phanHoiKeHoach.trangThai} />
            </div>
            <NoiDungPhanHoi phanHoi={muc.phanHoiKeHoach} />
            <button type="button" onClick={() => setCuocHopDangXem(muc.cuocHopId)}
              className="text-sm font-semibold text-emerald-800 underline">
              {muc.phanHoiKeHoach.trangThai === 'TRA_LAI' ? 'Chỉnh sửa và trình lại' : 'Xem hồ sơ'}
            </button>
          </article>
        ))}
      </section>
    )
  }

  if (chiXemChuyenCap) {
    const choChuyenCap = danhSach?.filter((muc) => muc.yeuCauChoGuiCapTrenId != null)
    return (
      <section className="space-y-4">
        <h1 className="text-2xl font-bold">Hồ sơ chờ chuyển cấp</h1>
        {loi ? <p role="alert">{loi}</p> : !choChuyenCap ? <p role="status">Đang tải hồ sơ…</p>
          : !choChuyenCap.length && <p>Chưa có hồ sơ được giao cho bạn chuyển cấp.</p>}
        {choChuyenCap?.map((muc) => (
          <article key={muc.yeuCauChoGuiCapTrenId}
            className="flex flex-wrap items-center justify-between gap-3 rounded-xl border border-slate-200 bg-white p-4">
            <div>
              <h2 className="font-semibold">{muc.tieuDe}</h2>
              <p className="mt-1 text-sm text-slate-600">Lần trình #{muc.yeuCauChoGuiCapTrenId} · Đã qua duyệt cấp đầu</p>
              <p className="text-sm text-slate-600">Đồng ý chuyển cấp · {dinhDangNgayGio(muc.thoiGianDongYChuyenCap)}</p>
            </div>
            <button type="button" className="rounded-lg border px-3 py-2 text-sm font-semibold text-emerald-800"
              onClick={() => setCuocHopDangXem(muc.cuocHopId)}>Mở hồ sơ</button>
          </article>
        ))}
      </section>
    )
  }

  if (chiXemBanGiao) {
    const daGiao = danhSach?.filter((muc) => tuViecChoXuLy ? canChuanBiCuocHopDuocGiao(muc) : muc.yeuCauBanGiaoId != null)
    return (
      <section className="space-y-4">
        <h1 className="text-2xl font-bold">{tuViecChoXuLy ? 'Cuộc họp cần chuẩn bị' : 'Cuộc họp được giao'}</h1>
        {loi ? <p role="alert">{loi}</p> : !daGiao ? <p role="status">Đang tải hồ sơ…</p>
          : !daGiao.length && <p>{tuViecChoXuLy ? 'Chưa có cuộc họp cần chuẩn bị.' : 'Chưa có hồ sơ được bàn giao.'}</p>}
        {daGiao?.map((muc) => (
          <article key={muc.cuocHopId} className="flex flex-wrap items-center justify-between gap-3 rounded-xl border border-slate-200 bg-white p-4">
            <div><h2 className="font-semibold">{muc.tieuDe}</h2>
              <p className="mt-1 text-sm text-emerald-800">{muc.daHoanTatHoSo ? 'Đã hoàn tất hồ sơ' : muc.trangThai === 'DANG_CHUAN_BI' ? 'Đang cập nhật kế hoạch đã bàn giao' : 'Đã bàn giao hồ sơ'}</p>
            </div>
            <button type="button" className="rounded-lg border px-3 py-2 text-sm font-semibold text-emerald-800"
              onClick={() => setCuocHopDangXem(muc.cuocHopId)}>Mở hồ sơ</button>
          </article>
        ))}
      </section>
    )
  }

  const timKiem = tuKhoa.trim().toLocaleLowerCase('vi')
  const danhSachHienThi = danhSach?.filter((deXuat) => {
    const thongTin = `${deXuat.tieuDe ?? ''} ${deXuat.maCuocHop || deXuat.cuocHopId}`
    return (!phamVi || deXuat.phamViCuocHop === phamVi)
      && khopTrangThaiHoSo(deXuat, trangThai)
      && thongTin.toLocaleLowerCase('vi').includes(timKiem)
  })

  return (
    <div className="space-y-5">
      <section className="space-y-5">
        <div className="flex flex-wrap items-end justify-between gap-3">
          <div>
            <p className="text-xs font-bold uppercase tracking-[0.16em] text-emerald-800">Quản lý hồ sơ</p>
            <h1 className="mt-1 text-2xl font-bold tracking-tight text-slate-900">Đề xuất cuộc họp</h1>
            <p className="mt-1 text-sm text-slate-500">Theo dõi tiến độ xử lý từ lúc tiếp nhận đến khi kết thúc.</p>
          </div>
          {danhSach && (
            <span className="rounded-full bg-white px-3 py-1.5 text-sm font-semibold text-slate-600 shadow-sm ring-1 ring-slate-200">
              {danhSachHienThi.length} / {danhSach.length} hồ sơ
            </span>
          )}
        </div>

        <div className="grid gap-3 sm:grid-cols-2 xl:grid-cols-3 2xl:grid-cols-6">
          {[
            { ten: 'Chờ tiếp nhận', ma: 'CHO_TIEP_NHAN', BieuTuong: Inbox, mau: 'bg-amber-50 text-amber-700' },
            { ten: 'Đang chuẩn bị', ma: 'DANG_CHUAN_BI', BieuTuong: FileClock, mau: 'bg-sky-50 text-sky-700' },
            { ten: 'Chờ duyệt', ma: 'CHO_DUYET', BieuTuong: Clock3, mau: 'bg-violet-50 text-violet-700' },
            { ten: 'Đã duyệt', ma: 'DA_DUYET', BieuTuong: CheckCircle2, mau: 'bg-emerald-50 text-emerald-700' },
            { ten: 'Đang diễn ra', ma: 'DANG_DIEN_RA', BieuTuong: CircleDot, mau: 'bg-rose-50 text-rose-700' },
            { ten: 'Đã kết thúc', ma: 'DA_KET_THUC', BieuTuong: CalendarDays, mau: 'bg-slate-100 text-slate-600' },
          ].map(({ ten, ma, BieuTuong, mau }) => (
            <button
              key={ma}
              type="button"
              onClick={() => setTrangThai((hienTai) => hienTai === ma ? '' : ma)}
              aria-pressed={trangThai === ma}
              className={`rounded-2xl border p-4 text-left shadow-sm shadow-slate-900/[0.02] transition hover:-translate-y-0.5 hover:shadow-md ${
                trangThai === ma
                  ? 'border-emerald-500 bg-emerald-50/60 ring-2 ring-emerald-100'
                  : 'border-slate-200/80 bg-white'
              }`}
            >
              <span className={`grid size-9 place-items-center rounded-xl ${mau}`}><BieuTuong size={18} /></span>
              <span className="mt-3 block text-2xl font-bold tabular-nums text-slate-900">
                {danhSach ? danhSach.filter((deXuat) => layTrangThaiHienThiCuocHop(deXuat) === ma).length : '—'}
              </span>
              <span className="mt-1 block text-xs font-semibold text-slate-600">{ten}</span>
            </button>
          ))}
        </div>
      </section>

      <section className="min-w-0 rounded-2xl border border-slate-200/80 bg-white p-4 shadow-sm shadow-slate-900/[0.02] sm:p-5">
        <div className="flex flex-wrap items-center justify-between gap-3">
          <div>
            <h2 className="text-lg font-bold text-slate-900">Danh sách cuộc họp</h2>
            <p className="mt-1 text-sm text-slate-500">Tìm kiếm, lọc trạng thái và mở hồ sơ chi tiết.</p>
          </div>
        </div>

        {danhSach?.length > 0 && (
          <div className="mt-4 flex flex-wrap gap-3">
          <label>
            <span className="sr-only">Tìm đề xuất theo tên hoặc mã cuộc họp</span>
            <input
              type="search"
              placeholder="Tìm tên hoặc mã cuộc họp…"
              value={tuKhoa}
              onChange={(event) => setTuKhoa(event.target.value)}
              className="w-full rounded-xl border border-slate-200 px-3 py-2.5 text-sm outline-none transition focus:border-emerald-600 focus:ring-4 focus:ring-emerald-100 sm:w-72"
            />
          </label>
          <label>
            <span className="sr-only">Phạm vi cuộc họp</span>
            <select value={phamVi} onChange={(event) => setPhamVi(event.target.value)} className="rounded-xl border border-slate-200 bg-white px-3 py-2.5 text-sm outline-none transition focus:border-emerald-600 focus:ring-4 focus:ring-emerald-100">
              <option value="">Tất cả phạm vi</option>
              {Object.entries(TEN_PHAM_VI).map(([ma, ten]) => (
                <option key={ma} value={ma}>{ten}</option>
              ))}
            </select>
          </label>
          <label>
            <span className="sr-only">Lọc theo trạng thái</span>
            <select value={trangThai} onChange={(event) => setTrangThai(event.target.value)} className="rounded-xl border border-slate-200 bg-white px-3 py-2.5 text-sm outline-none transition focus:border-emerald-600 focus:ring-4 focus:ring-emerald-100">
              <option value="">Tất cả trạng thái</option>
              {Object.entries(TEN_TRANG_THAI).map(([ma, ten]) => (
                <option key={ma} value={ma}>{ten}</option>
              ))}
            </select>
          </label>
        </div>
      )}

      {loi ? (
        <p role="alert" className="mt-4 rounded-xl bg-red-50 p-3 text-sm text-red-800">
          {loi}
        </p>
      ) : danhSach === null ? (
        <p role="status" className="mt-4 text-sm text-slate-600">
          Đang tải danh sách…
        </p>
      ) : danhSach.length === 0 ? (
        <p className="mt-4 text-sm text-slate-600">Chưa có đề xuất cuộc họp.</p>
      ) : danhSachHienThi.length === 0 ? (
        <p className="mt-4 text-sm text-slate-600">Không tìm thấy đề xuất phù hợp.</p>
      ) : (
        <div className="mt-4 overflow-x-auto rounded-xl border border-slate-200 focus-visible:outline-2 focus-visible:outline-emerald-700" tabIndex={0} role="region" aria-label="Danh sách đề xuất cuộc họp">
          <table className="w-full min-w-[680px] text-left text-sm">
            <thead className="bg-slate-50 text-slate-500">
              <tr>
                <th scope="col" className="px-3 py-3">Cuộc họp</th>
                <th scope="col" className="px-3 py-3">Người đề xuất</th>
                <th scope="col" className="px-3 py-3">Phạm vi</th>
                <th scope="col" className="px-3 py-3">Trạng thái</th>
                <th scope="col" className="px-3 py-3">Thao tác</th>
              </tr>
            </thead>

            <tbody className="divide-y divide-slate-100">
              {danhSachHienThi.map((deXuat) => (
              
                <tr key={deXuat.cuocHopId} className="transition hover:bg-slate-50/80">
                  <td className="px-3 py-3">
                    <p className="font-medium">{deXuat.tieuDe}</p>
                    <p className="text-xs text-slate-600">
                      {[deXuat.tenPhongBanChuTri, deXuat.ngayTao && dinhDangNgayGio(deXuat.ngayTao)].filter(Boolean).join(' · ')}
                    </p>
                    {deXuat.thoiGianChuyenCap && <p className="text-xs text-slate-600">Đã chuyển cấp · {dinhDangNgayGio(deXuat.thoiGianChuyenCap)}</p>}
                  </td>
                  <td className="px-3 py-3">{deXuat.tenNguoiDeXuat || '—'}</td>
                  <td className="px-3 py-3">
                    {TEN_PHAM_VI[deXuat.phamViCuocHop] ?? deXuat.phamViCuocHop ?? '—'}
                  </td>
                  <td className="px-3 py-3">
                    <NhanTrangThai trangThai={layNhanTrangThaiHoSo(deXuat)} />
                  </td>
                  <td className="px-3 py-3">
                    <button
                      type="button"
                      onClick={() => setCuocHopDangXem(deXuat.cuocHopId)}
                      className="whitespace-nowrap rounded-lg border border-emerald-200 px-3 py-1.5 font-semibold text-emerald-800 transition hover:bg-emerald-50 focus-visible:outline focus-visible:outline-2 focus-visible:outline-emerald-700"
                    >
                      {deXuat.trangThai === 'CHO_TIEP_NHAN' && deXuat.duocLapKeHoach ? 'Mở hồ sơ tiếp nhận' : 'Xem chi tiết'}
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
      </section>
    </div>
  )
}
