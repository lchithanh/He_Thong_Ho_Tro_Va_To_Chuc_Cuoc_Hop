import { useEffect, useState } from 'react'
import ArrowLeft from 'lucide-react/dist/esm/icons/arrow-left.mjs'
import Award from 'lucide-react/dist/esm/icons/award.mjs'
import Building2 from 'lucide-react/dist/esm/icons/building-2.mjs'
import CalendarDays from 'lucide-react/dist/esm/icons/calendar-days.mjs'
import FileSignature from 'lucide-react/dist/esm/icons/file-signature.mjs'
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
import BauChonCuocHop from './BauChonCuocHop.jsx'
import NghiQuyetCuocHop from './NghiQuyetCuocHop.jsx'
import BieuQuyetThamDu from './BieuQuyetThamDu.jsx'

const LOP_NUT = 'rounded-lg bg-blue-800 px-4 py-2.5 text-sm font-semibold text-white hover:bg-blue-900 disabled:opacity-60'
const LOP_KHUNG = 'min-w-0 overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm'
const LOP_FOCUS = 'focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue-500'
const TEN_HINH_THUC = { TRUC_TIEP: 'Trực tiếp', TRUC_TUYEN: 'Trực tuyến' }
const CAC_MUC = [
  { id: 'noi-dung', ten: 'Nội dung', Icon: CalendarDays },
  { id: 'diem-danh', ten: 'Danh sách và điểm danh', Icon: Users },
  { id: 'bieu-quyet', ten: 'Biểu quyết', Icon: Vote },
  { id: 'bau-chon', ten: 'Bầu chọn', Icon: Award },
  { id: 'nghi-quyet', ten: 'Nghị quyết', Icon: FileSignature },
]

function ThanhBenPhongHop({ cacMuc, mucHienTai, setMuc, onQuayLai, thongKe, moDanhSach }) {
  const tyLe = thongKe?.tong ? Math.round(thongKe.coMat / thongKe.tong * 100) : 0
  return (
    <aside className="flex min-w-0 flex-col bg-blue-900 text-white md:sticky md:top-0 md:h-[var(--chieu-cao-khong-gian,100dvh)] md:self-start">
      <div className="flex items-center gap-3 border-b border-white/15 px-4 py-4 sm:px-5">
        <span className="grid size-11 shrink-0 place-items-center rounded-xl border border-white/20 bg-white/10">
          <Building2 size={23} className="text-amber-300" aria-hidden="true" />
        </span>
        <div>
          <p className="text-lg font-bold leading-tight">Họp hành chính</p>
          <p className="mt-0.5 text-xs text-blue-100">Phòng họp</p>
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
            className={`flex min-w-0 items-center gap-2 rounded-xl px-3 py-2.5 text-left text-sm font-semibold md:py-3 ${LOP_FOCUS} ${id === mucHienTai
              ? 'bg-white text-blue-900 shadow-sm' : 'text-blue-50 hover:bg-white/10'}`}>
            <Icon size={18} className="shrink-0" aria-hidden="true" />{ten}
          </button>
        ))}
      </nav>
      {thongKe && <div className="mx-3 mb-3 space-y-2 rounded-xl border border-white/15 bg-blue-950/60 p-3 text-sm">
        <div className="flex flex-wrap justify-between gap-1">
          <span className="text-blue-100">Đang có mặt</span>
          <span className="font-bold tabular-nums text-teal-200">{thongKe.coMat}/{thongKe.tong} ({tyLe}%)</span>
        </div>
        <div role="progressbar" aria-label="Tỷ lệ thành viên đang có mặt" aria-valuenow={tyLe} className="h-1.5 overflow-hidden rounded-full bg-blue-800">
          <div className="h-full rounded-full bg-teal-300" style={{ width: `${tyLe}%` }} />
        </div>
        <button type="button" onClick={moDanhSach} className={`text-xs text-blue-100 underline ${LOP_FOCUS}`}>Xem điểm danh</button>
      </div>}
    </aside>
  )
}

export default function PhongCuocHop({ cuocHopId, nguoiDung, onQuayLai, onDaKetThuc }) {
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
        if (duLieu.trangThai === 'DA_KET_THUC') {
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
  }, [cuocHopId, token, phienDongBo, lanTai, onDaKetThuc])

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
    if (!laNguoiDieuHanh) return undefined
    const controller = new AbortController()
    layThanhVienCuocHop(cuocHopId, token, controller.signal)
      .then((danhSach) => {
        if (controller.signal.aborted) return
        const dsThamDu = danhSach.filter((tv) => !['TU_CHOI', 'DA_HUY'].includes(tv.trangThaiMoi))
        const coMat = dsThamDu.filter((tv) => tv.trangThaiThamDu === 'CO_MAT' && !tv.thoiGianRoiCuocHop).length
        setThongKePhong({ cuocHopId, tong: dsThamDu.length, coMat })
      })
      .catch(() => {
        if (!controller.signal.aborted) setThongKePhong(null)
      })
    return () => controller.abort()
  }, [cuocHopId, token, laNguoiDieuHanh, phienDongBo, lanTai])

  const cacMuc = hop ? CAC_MUC.filter(({ id }) => {
    if (id === 'diem-danh') return laNguoiDieuHanh
    if (id === 'bieu-quyet' || id === 'bau-chon') return laChuTri || coMat
    if (id === 'nghi-quyet') return laNguoiDieuHanh || coMat
    return true
  }) : []
  const mucHienTai = cacMuc.find(({ id }) => id === muc)
    ?? CAC_MUC.find(({ id }) => id === (laThuKy && dangHop ? 'diem-danh' : 'noi-dung'))
  const IconMuc = mucHienTai.Icon

  return (
    <div className="grid min-h-[var(--chieu-cao-khong-gian,100dvh)] grid-cols-1 bg-slate-50 text-slate-950 md:grid-cols-[16rem_minmax(0,1fr)] lg:grid-cols-[18rem_minmax(0,1fr)]">
      <ThanhBenPhongHop cacMuc={cacMuc} mucHienTai={mucHienTai.id} setMuc={setMuc}
        onQuayLai={onQuayLai} thongKe={thongKe} moDanhSach={() => setMuc('diem-danh')} />

      <div className="min-w-0">
        <header className="border-b border-slate-200 bg-white md:sticky md:top-0 md:z-20">
          <div className="mx-auto flex max-w-7xl flex-wrap items-center justify-between gap-4 px-4 py-4 sm:px-6 lg:px-8">
            <div className="min-w-0 grow sm:basis-64">
              {hop && <p className="flex items-center gap-2 text-xs font-bold tracking-wide text-blue-900">
                <span aria-hidden="true" className={`size-2 rounded-full ${dangHop ? 'bg-rose-500 ring-4 ring-rose-100' : 'bg-slate-400'}`} />
                {dangHop ? 'Đang diễn ra' : 'Đã kết thúc'}
              </p>}
              <h1 className="mt-1 break-words text-xl font-bold leading-snug tracking-tight sm:text-2xl">{hop?.tieuDe || 'Phòng họp'}</h1>
            </div>
            <div className="flex min-w-0 items-center gap-2.5 rounded-xl border border-slate-200 bg-slate-50 px-3 py-2">
              <span className="grid size-9 shrink-0 place-items-center rounded-lg bg-blue-100 text-sm font-bold text-blue-900" aria-hidden="true">
                {nguoiDung.hoTen?.trim().split(/\s+/).at(-1)?.slice(0, 1).toLocaleUpperCase('vi')}
              </span>
              <div className="min-w-0">
                <p className="break-words text-sm font-semibold">{nguoiDung.hoTen}</p>
                {hop && <p className="text-xs text-slate-600">{vaiTro}</p>}
              </div>
            </div>
          </div>
        </header>

        <main className="mx-auto max-w-7xl space-y-5 px-4 py-5 sm:px-6 lg:px-8">
          {loiTai && <p role="alert" className="rounded-xl border border-rose-200 bg-rose-50 p-4 text-sm text-rose-900">{loiTai}</p>}
          {!hop && !loiTai && <p role="status" className={LOP_KHUNG + ' p-4'}>Đang mở phòng họp…</p>}
          {hop && <>
            <section className={LOP_KHUNG + ' space-y-4 p-4 sm:p-5'} aria-label="Thông tin phiên họp">
              <dl className="grid gap-2 sm:grid-cols-2 xl:grid-cols-4">
                {[
                  ['Chủ trì', hop.tenChuTri, Users],
                  ['Bắt đầu', hop.thoiGianBatDau && dinhDangNgayGio(hop.thoiGianBatDau), CalendarDays],
                  ['Hình thức', TEN_HINH_THUC[hop.loaiHinh], Building2],
                  ['Địa điểm', hop.tenPhongHop, MapPin],
                ].filter(([, noiDung]) => noiDung).map(([ten, noiDung, Icon]) => (
                  <div key={ten} className="flex min-w-0 items-start gap-3 rounded-xl border border-slate-100 bg-slate-50 p-3">
                    <span className="grid size-9 shrink-0 place-items-center rounded-lg bg-blue-100 text-blue-900">
                      <Icon size={18} aria-hidden="true" />
                    </span>
                    <div className="min-w-0">
                      <dt className="text-xs font-medium text-slate-600">{ten}</dt>
                      <dd className="mt-0.5 break-words text-sm font-semibold">{noiDung}</dd>
                    </div>
                  </div>
                ))}
              </dl>
              <div className="flex flex-wrap items-center justify-between gap-3 border-t border-slate-100 pt-3">
                <div aria-live="polite" className="space-y-1 text-sm">
                  {daRoi
                    ? <p className="font-medium text-amber-900">Đã rời họp lúc {dinhDangNgayGio(hop.thoiGianRoiCuocHop)}</p>
                    : hop.trangThaiThamDu === 'VANG_MAT'
                      ? <p className="font-medium text-rose-800">Thư ký đã ghi nhận bạn vắng mặt.</p>
                      : coMat
                        ? <p className="font-medium text-teal-800">Bạn đang có mặt</p>
                        : daTuDiemDanh
                          ? <p className="font-medium text-blue-900">Đã tự điểm danh lúc {dinhDangNgayGio(hop.thoiGianTuDiemDanh)}</p>
                          : <p className="font-medium text-slate-700">Bạn chưa điểm danh</p>}
                  {choThamGia && hop.trangThaiMoi === 'CHO_PHAN_HOI' && <p className="text-slate-600">Tham gia sẽ đồng thời xác nhận lời mời.</p>}
                </div>
                <div className="flex flex-wrap gap-2">
                  {choThamGia && <button type="button" className={LOP_NUT} disabled={thaoTac !== null} onClick={() => xuLy('tham-gia')}>
                    {thaoTac === 'tham-gia' ? 'Đang điểm danh…' : 'Tham gia'}
                  </button>}
                  {dangHop && laNguoiDieuHanh && !xacNhanKetThuc && (
                    <button type="button" onClick={() => setXacNhanKetThuc(true)}
                      className="inline-flex items-center gap-2 rounded-lg border border-rose-200 px-4 py-2.5 text-sm font-semibold text-rose-800 hover:bg-rose-50">
                      <Square size={15} aria-hidden="true" /> Kết thúc cuộc họp
                    </button>
                  )}
                </div>
              </div>
              {loiLuu && <p role="alert" className="text-sm text-rose-800">{loiLuu}</p>}
              {dangHop && laNguoiDieuHanh && xacNhanKetThuc && (
                <div className="flex flex-wrap items-center gap-3 rounded-xl border border-rose-200 bg-rose-50 p-3">
                  <p className="grow text-sm text-rose-900">Xác nhận kết thúc để đóng điểm danh và chuyển sang xử lý văn bản sau họp.</p>
                  <button type="button" disabled={thaoTac !== null} onClick={() => setXacNhanKetThuc(false)} className="rounded-lg px-3 py-2 text-sm font-semibold">Quay lại</button>
                  <button type="button" disabled={thaoTac !== null} onClick={() => xuLy('ket-thuc')}
                    className="rounded-lg bg-rose-800 px-3 py-2 text-sm font-semibold text-white disabled:opacity-60">
                    {thaoTac === 'ket-thuc' ? 'Đang kết thúc…' : 'Xác nhận kết thúc'}
                  </button>
                </div>
              )}
            </section>

            <section className={LOP_KHUNG} aria-labelledby="ten-muc-phong-hop">
              <div className="flex items-center gap-3 bg-linear-to-r from-blue-900 to-blue-800 px-4 py-3.5 text-white sm:px-6">
                <IconMuc size={21} className="shrink-0 text-amber-300" aria-hidden="true" />
                <h2 id="ten-muc-phong-hop" className="text-base font-bold">{mucHienTai.ten}</h2>
              </div>
              <div className="min-w-0 p-4 sm:p-6">
                {mucHienTai.id === 'noi-dung' && (
                  <div className="grid gap-4 lg:grid-cols-[minmax(0,1fr)_minmax(0,2fr)]">
                    <section className="min-w-0 rounded-xl border border-slate-200 bg-slate-50 p-4">
                      <h3 className="font-semibold">Mục tiêu</h3>
                      <p className="mt-2 whitespace-pre-wrap break-words text-sm leading-relaxed text-slate-700">{hop.mucTieu || 'Chưa có mục tiêu.'}</p>
                    </section>
                    <section className="min-w-0 rounded-xl border border-slate-200 p-4">
                      <h3 className="font-semibold">Nội dung dự kiến</h3>
                      <p className="mt-2 whitespace-pre-wrap break-words text-sm leading-relaxed text-slate-700">{hop.noiDungDuKien || 'Chưa có nội dung dự kiến.'}</p>
                    </section>
                  </div>
                )}
                {mucHienTai.id === 'diem-danh' && <DiemDanhThuKy cuocHopId={cuocHopId} token={token} readOnly={!laThuKy || !dangHop} />}
                {mucHienTai.id === 'bau-chon' && <BauChonCuocHop cuocHopId={cuocHopId} token={token} hienTieuDe={false} />}
                {mucHienTai.id === 'nghi-quyet' && <NghiQuyetCuocHop cuocHopId={cuocHopId} token={token} duocQuanLy={laNguoiDieuHanh} />}
                {mucHienTai.id === 'bieu-quyet' && (laChuTri
                  ? <TaoBieuQuyet nguoiDung={nguoiDung} cuocHopId={cuocHopId} />
                  : <BieuQuyetThamDu token={token} cuocHopId={cuocHopId} />)}
              </div>
            </section>
          </>}
        </main>
      </div>
    </div>
  )
}
