import { useEffect, useState } from 'react'
import ArrowLeft from 'lucide-react/dist/esm/icons/arrow-left.mjs'
import Award from 'lucide-react/dist/esm/icons/award.mjs'
import Building2 from 'lucide-react/dist/esm/icons/building-2.mjs'
import CalendarDays from 'lucide-react/dist/esm/icons/calendar-days.mjs'
import FileSignature from 'lucide-react/dist/esm/icons/file-signature.mjs'
import NotebookPen from 'lucide-react/dist/esm/icons/notebook-pen.mjs'
import MapPin from 'lucide-react/dist/esm/icons/map-pin.mjs'
import Square from 'lucide-react/dist/esm/icons/square.mjs'
import Users from 'lucide-react/dist/esm/icons/users.mjs'
import Vote from 'lucide-react/dist/esm/icons/vote.mjs'
import { ketThucCuocHop } from '../../api/cuocHopApi.js'
import { layPhongHop, layThanhVienCuocHop, thamGiaCuocHop } from '../../api/thanhVienCuocHopApi.js'
import { useDongBoDuLieu } from '../../services/useDongBoDuLieu.js'
import { dinhDangNgayGio } from '../../utils/thoiGianCuocHop.js'
import TaoBieuQuyet from '../pheduyet/TaoBieuQuyet.jsx'
import DiemDanhThuKy from '../thuky/DiemDanhThuKy.jsx'
import GhiChuCuocHop from '../thuky/GhiChuCuocHop.jsx'
import BauChonCuocHop from './BauChonCuocHop.jsx'
import NghiQuyetCuocHop from './NghiQuyetCuocHop.jsx'
import BieuQuyetThamDu from './BieuQuyetThamDu.jsx'
import HoSoTaiLieuPhongHop from './HoSoTaiLieuPhongHop.jsx'

const LOP_NUT = 'rounded-lg bg-blue-800 phong-toi:bg-phong-nut px-4 py-2.5 text-sm font-semibold text-white hover:bg-blue-900 phong-toi:enabled:hover:bg-phong-nut-hover disabled:opacity-60'
const LOP_KHUNG = 'min-w-0 overflow-hidden rounded-2xl border border-slate-200 phong-toi:border-phong-vien bg-white phong-toi:bg-phong-mat shadow-sm'
const LOP_FOCUS = 'focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue-500 phong-toi:focus-visible:outline-phong-focus'
const LOP_NHAN = 'inline-flex w-fit items-center gap-2 rounded-full px-2.5 py-1 text-xs font-semibold'
const MAU_DIEM_DANH = {
  tot: 'bg-phong-nhan-luc text-phong-luc',
  loi: 'bg-phong-nhan-do text-phong-do',
  'chu-y': 'bg-phong-nhan-vang text-phong-vang',
  thuong: 'bg-phong-mat-noi text-phong-chu-phu',
}
const TEN_HINH_THUC = { TRUC_TIEP: 'Trực tiếp', TRUC_TUYEN: 'Trực tuyến' }
const CAC_MUC = [
  { id: 'noi-dung', ten: 'Nội dung', Icon: CalendarDays },
  { id: 'ghi-chu', ten: 'Ghi chú', Icon: NotebookPen },
  { id: 'diem-danh', ten: 'Điểm danh', tacVuChuTri: 'Xem điểm danh', Icon: Users },
  { id: 'bieu-quyet', ten: 'Biểu quyết', tacVuChuTri: 'Quản lý biểu quyết', Icon: Vote },
  { id: 'bau-chon', ten: 'Bầu chọn', tacVuChuTri: 'Quản lý bầu chọn', Icon: Award },
  { id: 'nghi-quyet', ten: 'Nghị quyết', tacVuChuTri: 'Quản lý nghị quyết', Icon: FileSignature },
  { id: 'tai-lieu', ten: 'Hồ sơ và tài liệu', Icon: FileSignature },
]

const TRANG_THAI_DIEM_DANH = {
  CO_MAT: { ten: 'Có mặt', mau: 'tot' },
  VANG_MAT: { ten: 'Vắng mặt', mau: 'loi' },
  DA_ROI: { ten: 'Đã rời họp', mau: 'chu-y' },
}
const VAI_TRO_TAI_HOP = { CHU_TRI: 'Chủ trì', THU_KY: 'Thư ký', THAM_DU: 'Thành viên' }

function TrangThaiThanhVien({ thongKe, moDanhSach }) {
  return (
    <aside className={LOP_KHUNG + ' min-[1050px]:sticky min-[1050px]:top-30'} aria-labelledby="trang-thai-thanh-vien">
      <div className="flex items-start gap-3 border-b border-slate-200 phong-toi:border-phong-vien p-4">
        <Users size={21} className="mt-0.5 shrink-0 text-blue-800 phong-toi:text-phong-xanh" aria-hidden="true" />
        <div className="grow">
          <h3 id="trang-thai-thanh-vien" className="font-bold">Trạng thái thành viên</h3>
          <p className="mt-1 text-xs text-slate-600 phong-toi:text-phong-chu-phu">{thongKe.coMat}/{thongKe.tong} đang có mặt</p>
        </div>
        <button type="button" onClick={moDanhSach}
          className={`shrink-0 whitespace-nowrap rounded-md px-2 py-1 text-xs font-semibold text-blue-800 phong-toi:text-phong-xanh hover:underline ${LOP_FOCUS}`}>
          Điểm danh
        </button>
      </div>
      {thongKe.danhSach.length === 0 && (
        <p className="p-4 text-sm text-slate-600 phong-toi:text-phong-chu-phu">Chưa có thành viên tham dự.</p>
      )}
      <ul className="max-h-[29rem] divide-y overflow-y-auto [scrollbar-gutter:stable] divide-slate-200 phong-toi:divide-phong-vien" tabIndex={thongKe.danhSach.length > 7 ? 0 : undefined}
        aria-label="Danh sách điểm danh">
        {thongKe.danhSach.map((thanhVien) => {
          const trangThai = TRANG_THAI_DIEM_DANH[thanhVien.thoiGianRoiCuocHop ? 'DA_ROI' : thanhVien.trangThaiThamDu]
            ?? { ten: 'Chưa điểm danh', mau: 'thuong' }
          return (
            <li key={thanhVien.nguoiDungId} className="flex items-center justify-between gap-3 px-4 py-3">
              <div className="min-w-0">
                <p className="wrap-anywhere text-sm font-semibold">{thanhVien.hoTen}</p>
                <p className="mt-0.5 wrap-anywhere text-xs text-slate-600 phong-toi:text-phong-chu-phu">
                  {[VAI_TRO_TAI_HOP[thanhVien.vaiTroTaiCuocHop], thanhVien.tenPhongBan].filter(Boolean).join(' · ')}
                </p>
              </div>
              <span className={`${LOP_NHAN} shrink-0 ${MAU_DIEM_DANH[trangThai.mau]}`}>{trangThai.ten}</span>
            </li>
          )
        })}
      </ul>
    </aside>
  )
}

function ThanhBenPhongHop({ cacMuc, mucHienTai, setMuc, onQuayLai, thongKe, moDanhSach }) {
  const tyLe = thongKe?.tong ? Math.round(thongKe.coMat / thongKe.tong * 100) : 0
  return (
    <aside className="flex min-w-0 flex-col [background-color:var(--color-phong-menu,#193875)] text-white md:sticky md:top-0 md:h-[var(--chieu-cao-khong-gian,100dvh)] md:self-start">
      <div className="flex items-center gap-3 border-b border-white/15 px-4 py-4 sm:px-5">
        <span className="grid size-11 shrink-0 place-items-center rounded-xl border border-white/20 bg-white/10">
          <Building2 size={23} className="text-amber-300" aria-hidden="true" />
        </span>
        <div>
          <p className="text-lg font-bold leading-tight">Họp hành chính</p>
          <p className="mt-0.5 text-xs text-blue-100">Điều hành phiên họp</p>
        </div>
      </div>
      <div className="px-3 pt-3">
        <button type="button" onClick={onQuayLai}
          className={`flex w-full items-center gap-2 rounded-lg px-3 py-2 text-left text-sm text-blue-100 hover:bg-white/10 ${LOP_FOCUS}`}>
          <ArrowLeft size={17} aria-hidden="true" /> Về Tổng quan
        </button>
      </div>
      <nav aria-label="Công cụ cuộc họp" className="flex flex-wrap items-start gap-2 p-3 md:flex-1 md:flex-col md:items-stretch md:overflow-y-auto">
        {cacMuc.map(({ id, ten, Icon }) => (
          <button type="button" key={id} aria-current={id === mucHienTai ? 'page' : undefined} onClick={() => setMuc(id)}
            className={`flex min-w-0 items-center gap-2 rounded-xl px-3 py-2.5 text-left text-sm font-semibold text-blue-50 hover:bg-white/10 aria-[current=page]:[background-color:var(--color-phong-menu-chon,#101e39)] aria-[current=page]:text-white md:py-3 ${LOP_FOCUS}`}>
            <Icon size={18} className="shrink-0" aria-hidden="true" />{ten}
          </button>
        ))}
      </nav>
      {thongKe && <div className="mx-3 mb-3 space-y-2 rounded-xl border border-white/15 bg-blue-950/60 p-3 text-sm">
        <div className="flex flex-wrap justify-between gap-1">
          <span className="text-blue-100">Đang có mặt</span>
          <span className="font-bold tabular-nums text-teal-200">{thongKe.coMat}/{thongKe.tong} ({tyLe}%)</span>
        </div>
        <div role="progressbar" aria-label="Tỷ lệ thành viên đang có mặt" aria-valuenow={tyLe} className="h-1.5 overflow-hidden rounded-full bg-blue-800 phong-toi:bg-phong-nut">
          <div className="h-full rounded-full bg-teal-300" style={{ width: `${tyLe}%` }} />
        </div>
        <button type="button" onClick={moDanhSach} className={`text-xs text-blue-100 underline ${LOP_FOCUS}`}>Xem điểm danh</button>
      </div>}
    </aside>
  )
}

export default function PhongCuocHop({ cuocHopId, nguoiDung, onQuayLai, onDaKetThuc, onMoHoSo, chiXemHoSo = false }) {
  const { token } = nguoiDung
  const phienDongBo = useDongBoDuLieu()
  const [hop, setHop] = useState(null)
  const [loiTai, setLoiTai] = useState('')
  const [loiLuu, setLoiLuu] = useState('')
  const [thaoTac, setThaoTac] = useState(null)
  const [xacNhanKetThuc, setXacNhanKetThuc] = useState(false)
  const [muc, setMuc] = useState('')
  const [lanTai, setLanTai] = useState(0)
  const [thongKePhong, setThongKePhong] = useState(null)

  useEffect(() => {
    const controller = new AbortController()
    layPhongHop(cuocHopId, token, controller.signal)
      .then((duLieu) => {
        if (controller.signal.aborted) return
        if (duLieu.trangThai === 'DA_KET_THUC' && !chiXemHoSo) {
          onDaKetThuc(duLieu)
          return
        }
        setHop(duLieu)
        setLoiTai('')
      })
      .catch((loi) => {
        if (controller.signal.aborted) return
        if ([403, 404, 409].includes(loi.status)) setHop(null)
        setLoiTai(loi.message)
      })
    return () => controller.abort()
  }, [cuocHopId, token, phienDongBo, lanTai, onDaKetThuc, chiXemHoSo])

  async function xuLy(loai) {
    setLoiLuu('')
    setThaoTac(loai)
    try {
      if (loai === 'tham-gia') {
        const ketQua = await thamGiaCuocHop(cuocHopId, token)
        setHop((hienTai) => hienTai && {
          ...hienTai, thoiGianTuDiemDanh: ketQua.thoiGianDiemDanh,
        })
      } else {
        await ketThucCuocHop(cuocHopId, token)
      }
      setLanTai((lan) => lan + 1)
    } catch (loi) {
      setLoiLuu(loi.message)
      if (loi.status === 409) setLanTai((lan) => lan + 1)
    } finally {
      setThaoTac(null)
    }
  }

  const dangHop = hop?.trangThai === 'DANG_DIEN_RA'
  const laThuKy = hop?.laThuKyToChuc
  const laChuTri = hop?.laChuTri
  const laNguoiDieuHanh = laThuKy || laChuTri
  const daRoi = Boolean(hop?.thoiGianRoiCuocHop)
  const daTuDiemDanh = Boolean(hop?.thoiGianTuDiemDanh)
  const choThamGia = dangHop && !daRoi && !daTuDiemDanh
  const coMat = hop?.trangThaiThamDu === 'CO_MAT' && !daRoi
  const vaiTro = laChuTri ? 'Chủ trì' : laThuKy ? 'Thư ký tổ chức' : 'Thành viên'
  const thongKe = laNguoiDieuHanh && thongKePhong?.cuocHopId === cuocHopId ? thongKePhong : null

  useEffect(() => {
    if (!laNguoiDieuHanh || chiXemHoSo) return undefined
    const controller = new AbortController()
    layThanhVienCuocHop(cuocHopId, token, controller.signal)
      .then((danhSach) => {
        if (controller.signal.aborted) return
        const dsThamDu = danhSach.filter((tv) => !['TU_CHOI', 'DA_HUY'].includes(tv.trangThaiMoi))
        const coMat = dsThamDu.filter((tv) => tv.trangThaiThamDu === 'CO_MAT' && !tv.thoiGianRoiCuocHop).length
        setThongKePhong({ cuocHopId, tong: dsThamDu.length, coMat, danhSach: dsThamDu })
      })
      .catch(() => {
        if (!controller.signal.aborted) setThongKePhong(null)
      })
    return () => controller.abort()
  }, [cuocHopId, token, laNguoiDieuHanh, phienDongBo, lanTai, chiXemHoSo])

  const cacMuc = hop ? CAC_MUC.filter(({ id }) => {
    if (chiXemHoSo) return id === 'tai-lieu'
    if (id === 'ghi-chu') return laThuKy && dangHop
    if (id === 'diem-danh') return laNguoiDieuHanh
    if (id === 'bieu-quyet' || id === 'bau-chon') return laChuTri || coMat
    if (id === 'nghi-quyet') return laNguoiDieuHanh || coMat
    return true
  }) : []
  const mucHienTai = cacMuc.find(({ id }) => id === muc)
    ?? CAC_MUC.find(({ id }) => id === (chiXemHoSo ? 'tai-lieu' : laThuKy && dangHop ? 'diem-danh' : 'noi-dung'))
  const IconMuc = mucHienTai.Icon
  const hienThanhVien = thongKe && ['noi-dung', 'bieu-quyet', 'bau-chon'].includes(mucHienTai.id)

  function chonMuc(id) {
    setMuc(id)
    window.scrollTo({ top: 0, behavior: 'instant' })
  }

  return (
    <div className="khong-gian-toi mat-phong-toi phong-hop grid lg:mat-do-cuoc-hop min-h-[var(--chieu-cao-khong-gian,100dvh)] grid-cols-1 md:grid-cols-[16rem_minmax(0,1fr)] lg:grid-cols-[18rem_minmax(0,1fr)]">
      <ThanhBenPhongHop cacMuc={cacMuc} mucHienTai={mucHienTai.id} setMuc={chonMuc}
        onQuayLai={onQuayLai} thongKe={thongKe} moDanhSach={() => chonMuc('diem-danh')} />

      <div className="min-w-0">
        <header className="border-b border-slate-200 phong-toi:border-phong-vien bg-white phong-toi:bg-phong-mat md:sticky md:top-0 md:z-20">
          <div className="mx-auto flex max-w-[124rem] flex-wrap items-center justify-between gap-4 px-4 py-4 sm:px-6 lg:px-8">
            <div className="min-w-0 grow sm:basis-64">
              {hop && <p className={`${LOP_NHAN} ${dangHop ? MAU_DIEM_DANH.loi : MAU_DIEM_DANH.thuong}`}>
                <span aria-hidden="true" className="size-2 rounded-full bg-current" />
                {dangHop ? 'Đang diễn ra' : 'Đã kết thúc'}
              </p>}
              <h1 className="mt-1 break-words text-xl font-bold leading-snug tracking-tight sm:text-2xl">{hop?.tieuDe || 'Phòng họp'}</h1>
            </div>
            <div className="flex min-w-0 items-center gap-2.5 rounded-xl border border-slate-200 phong-toi:border-phong-vien bg-slate-50 phong-toi:bg-phong-mat-noi px-3 py-2">
              <span className="grid size-9 shrink-0 place-items-center rounded-lg bg-blue-100 phong-toi:bg-phong-nhan-xanh text-sm font-bold text-blue-900 phong-toi:text-phong-xanh" aria-hidden="true">
                {nguoiDung.hoTen?.trim().split(/\s+/).at(-1)?.slice(0, 1).toLocaleUpperCase('vi')}
              </span>
              <div className="min-w-0">
                <p className="break-words text-sm font-semibold">{nguoiDung.hoTen}</p>
                {hop && <p className="text-xs text-slate-600 phong-toi:text-phong-chu-phu">{vaiTro}</p>}
              </div>
            </div>
          </div>
        </header>

        <main className="mx-auto max-w-[124rem] space-y-5 px-4 py-5 sm:px-6 lg:px-8">
          {loiTai && <p role="alert" className="rounded-xl border border-rose-200 phong-toi:border-phong-vien-do bg-rose-50 phong-toi:bg-phong-nhan-do p-4 text-sm text-rose-900 phong-toi:text-phong-do">{loiTai}</p>}
          {!hop && !loiTai && <p role="status" className={LOP_KHUNG + ' p-4'}>Đang mở phòng họp…</p>}
          {hop && <>
            <section className={LOP_KHUNG + ' space-y-4 p-4 sm:p-5'} aria-label="Thông tin phiên họp">
              {mucHienTai.id === 'noi-dung' && <dl className="grid gap-2 sm:grid-cols-2 xl:grid-cols-4">
                {[
                  ['Chủ trì', hop.tenChuTri, Users],
                  ['Bắt đầu', hop.thoiGianBatDau && dinhDangNgayGio(hop.thoiGianBatDau), CalendarDays],
                  ['Hình thức', TEN_HINH_THUC[hop.loaiHinh], Building2],
                  ['Địa điểm', hop.tenPhongHop, MapPin],
                ].filter(([, noiDung]) => noiDung).map(([ten, noiDung, Icon]) => (
                  <div key={ten} className="flex min-w-0 items-start gap-3 rounded-xl border border-slate-100 phong-toi:border-phong-vien bg-slate-50 phong-toi:bg-phong-mat-noi p-3">
                    <span className="grid size-9 shrink-0 place-items-center rounded-lg bg-blue-100 phong-toi:bg-phong-nhan-xanh text-blue-900 phong-toi:text-phong-xanh">
                      <Icon size={18} aria-hidden="true" />
                    </span>
                    <div className="min-w-0">
                      <dt className="text-xs font-medium text-slate-600 phong-toi:text-phong-chu-phu">{ten}</dt>
                      <dd className="mt-0.5 break-words text-sm font-semibold">{noiDung}</dd>
                    </div>
                  </div>
                ))}
              </dl>}
              <div className={`flex flex-wrap items-center justify-between gap-3 ${mucHienTai.id === 'noi-dung' ? 'border-t border-slate-100 phong-toi:border-phong-vien pt-3' : ''}`}>
                <div aria-live="polite" className="space-y-1 text-sm">
                  {chiXemHoSo && !dangHop
                    ? <p className="font-medium text-slate-700 phong-toi:text-phong-chu-phu">Cuộc họp đã kết thúc</p>
                    : daRoi
                    ? <p className="font-medium text-amber-900 phong-toi:text-phong-vang">Đã rời họp lúc {dinhDangNgayGio(hop.thoiGianRoiCuocHop)}</p>
                    : hop.trangThaiThamDu === 'VANG_MAT'
                      ? <p className="font-medium text-rose-800 phong-toi:text-phong-do">Thư ký đã ghi nhận bạn vắng mặt.</p>
                      : coMat
                        ? <p className="font-medium text-teal-800 phong-toi:text-phong-luc">Bạn đang có mặt</p>
                        : daTuDiemDanh
                          ? <p className="font-medium text-blue-900 phong-toi:text-phong-xanh">Đã tự điểm danh lúc {dinhDangNgayGio(hop.thoiGianTuDiemDanh)}</p>
                          : <p className="font-medium text-slate-700 phong-toi:text-phong-chu-phu">Bạn chưa điểm danh</p>}
                  {choThamGia && hop.trangThaiMoi === 'CHO_PHAN_HOI' && <p className="text-slate-600 phong-toi:text-phong-chu-phu">Tham gia sẽ đồng thời xác nhận lời mời.</p>}
                </div>
                <div className="flex flex-wrap gap-2">
                  {choThamGia && <button type="button" className={LOP_NUT} disabled={thaoTac !== null} onClick={() => xuLy('tham-gia')}>
                    {thaoTac === 'tham-gia' ? 'Đang điểm danh…' : 'Tham gia'}
                  </button>}
                  {mucHienTai.id !== 'tai-lieu' && (
                    <button type="button" onClick={() => chonMuc('tai-lieu')}
                      className={`inline-flex items-center gap-2 rounded-lg border border-slate-300 px-4 py-2.5 text-sm font-semibold text-blue-900 hover:bg-blue-50 phong-toi:border-phong-vien phong-toi:text-phong-xanh phong-toi:hover:bg-phong-mat-noi ${LOP_FOCUS}`}>
                      <FileSignature size={16} aria-hidden="true" /> Hồ sơ và tài liệu
                    </button>
                  )}
                  {dangHop && laNguoiDieuHanh && !xacNhanKetThuc && (
                    <button type="button" onClick={() => setXacNhanKetThuc(true)}
                      className="inline-flex items-center gap-2 rounded-lg border border-rose-200 phong-toi:border-phong-vien-do px-4 py-2.5 text-sm font-semibold text-rose-800 phong-toi:text-phong-do hover:bg-rose-50 phong-toi:hover:bg-phong-nhan-do">
                      <Square size={15} aria-hidden="true" /> Kết thúc cuộc họp
                    </button>
                  )}
                </div>
              </div>
              {loiLuu && <p role="alert" className="text-sm text-rose-800 phong-toi:text-phong-do">{loiLuu}</p>}
              {dangHop && laNguoiDieuHanh && xacNhanKetThuc && (
                <div className="flex flex-wrap items-center gap-3 rounded-xl border border-rose-200 phong-toi:border-phong-vien-do bg-rose-50 phong-toi:bg-phong-nhan-do p-3">
                  <p className="grow text-sm text-rose-900 phong-toi:text-phong-do">Xác nhận kết thúc để đóng điểm danh và chuyển sang xử lý văn bản sau họp.</p>
                  <button type="button" disabled={thaoTac !== null} onClick={() => setXacNhanKetThuc(false)} className="rounded-lg px-3 py-2 text-sm font-semibold">Quay lại</button>
                  <button type="button" disabled={thaoTac !== null} onClick={() => xuLy('ket-thuc')}
                    className="rounded-lg bg-rose-800 px-3 py-2 text-sm font-semibold text-white disabled:opacity-60">
                    {thaoTac === 'ket-thuc' ? 'Đang kết thúc…' : 'Xác nhận kết thúc'}
                  </button>
                </div>
              )}
            </section>

            {laChuTri && dangHop && mucHienTai.id === 'noi-dung' && (
              <section className={LOP_KHUNG + ' space-y-3 p-4 sm:p-5'} aria-labelledby="tac-vu-chu-tri">
                <h2 id="tac-vu-chu-tri" className="text-base font-bold">Điều hành cuộc họp</h2>
                <ul className="grid gap-2 sm:grid-cols-2 xl:grid-cols-4">
                  {cacMuc.filter(({ tacVuChuTri }) => tacVuChuTri).map(({ id, tacVuChuTri, Icon }) => (
                    <li key={id}>
                      <button type="button" onClick={() => chonMuc(id)}
                        className={`flex w-full items-center gap-2 rounded-lg border border-blue-200 bg-blue-50 px-3 py-2.5 text-left text-sm font-semibold text-blue-900 hover:bg-blue-100 phong-toi:border-phong-vien-xanh phong-toi:bg-phong-nhan-xanh phong-toi:text-phong-xanh phong-toi:hover:bg-phong-mat-noi ${LOP_FOCUS}`}>
                        <Icon size={18} className="shrink-0" aria-hidden="true" />
                        {tacVuChuTri}
                      </button>
                    </li>
                  ))}
                </ul>
              </section>
            )}

            <div className={hienThanhVien ? 'grid grid-cols-1 items-start gap-5 min-[1050px]:grid-cols-[minmax(0,1fr)_minmax(18rem,32%)]' : undefined}>
              <section className={LOP_KHUNG} aria-labelledby="ten-muc-phong-hop">
                <div className="flex items-center gap-3 [background-color:var(--color-phong-tieu-de,#254b96)] px-4 py-3.5 text-white sm:px-6">
                  <IconMuc size={21} className="shrink-0 text-amber-300" aria-hidden="true" />
                  <h2 id="ten-muc-phong-hop" className="text-base font-bold">{mucHienTai.ten}</h2>
                </div>
                <div className="min-w-0 p-4 sm:p-6">
                  {mucHienTai.id === 'noi-dung' && (
                    <div className="grid gap-4 lg:grid-cols-[minmax(0,1fr)_minmax(0,2fr)]">
                      <section className="min-w-0 rounded-xl border border-slate-200 phong-toi:border-phong-vien bg-slate-50 phong-toi:bg-phong-mat-noi p-4">
                        <h3 className="font-semibold">Mục tiêu</h3>
                        <p className="mt-2 whitespace-pre-wrap break-words text-sm leading-relaxed text-slate-700 phong-toi:text-phong-chu-phu">{hop.mucTieu || 'Chưa có mục tiêu.'}</p>
                      </section>
                      <section className="min-w-0 rounded-xl border border-slate-200 phong-toi:border-phong-vien p-4">
                        <h3 className="font-semibold">Nội dung dự kiến</h3>
                        <p className="mt-2 whitespace-pre-wrap break-words text-sm leading-relaxed text-slate-700 phong-toi:text-phong-chu-phu">{hop.noiDungDuKien || 'Chưa có nội dung dự kiến.'}</p>
                      </section>
                    </div>
                  )}
                  {mucHienTai.id === 'noi-dung' && coMat && dangHop && (
                    <BieuQuyetThamDu token={token} cuocHopId={cuocHopId} chiPhienDangMo />
                  )}
                  {mucHienTai.id === 'tai-lieu' && (
                    <HoSoTaiLieuPhongHop cuocHopId={cuocHopId} token={token}
                      onQuanLyHoSo={laNguoiDieuHanh && onMoHoSo ? () => onMoHoSo(cuocHopId, laThuKy) : undefined} />
                  )}
                  {laThuKy && dangHop && !chiXemHoSo && (
                    <div hidden={mucHienTai.id !== 'ghi-chu'}>
                      <GhiChuCuocHop key={cuocHopId} cuocHopId={cuocHopId} token={token}
                        kichHoat={mucHienTai.id === 'ghi-chu'} />
                    </div>
                  )}
                  {mucHienTai.id === 'diem-danh' && <DiemDanhThuKy cuocHopId={cuocHopId} token={token} readOnly={!laThuKy || !dangHop} />}
                  {mucHienTai.id === 'bau-chon' && <BauChonCuocHop cuocHopId={cuocHopId} token={token} hienTieuDe={false} />}
                  {mucHienTai.id === 'nghi-quyet' && <NghiQuyetCuocHop cuocHopId={cuocHopId} token={token} duocQuanLy={laNguoiDieuHanh} chuTri={laChuTri ? nguoiDung : undefined} hienTieuDe={false} />}
                  {mucHienTai.id === 'bieu-quyet' && (laChuTri
                    ? <TaoBieuQuyet nguoiDung={nguoiDung} cuocHopId={cuocHopId} />
                    : <BieuQuyetThamDu token={token} cuocHopId={cuocHopId} />)}
                </div>
              </section>
              {hienThanhVien && <TrangThaiThanhVien thongKe={thongKe} moDanhSach={() => chonMuc('diem-danh')} />}
            </div>
          </>}
        </main>
      </div>
    </div>
  )
}
