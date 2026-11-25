import PhanHoiDoiLich from './PhanHoiDoiLich.jsx'
import LienKetDeXuat from '../../components/LienKetDeXuat.jsx'
import { useEffect, useState } from 'react'
import ArrowLeft from 'lucide-react/dist/esm/icons/arrow-left.mjs'
import CalendarDays from 'lucide-react/dist/esm/icons/calendar-days.mjs'
import Check from 'lucide-react/dist/esm/icons/check.mjs'
import FileText from 'lucide-react/dist/esm/icons/file-text.mjs'
import Mail from 'lucide-react/dist/esm/icons/mail.mjs'
import MapPin from 'lucide-react/dist/esm/icons/map-pin.mjs'
import Users from 'lucide-react/dist/esm/icons/users.mjs'
import X from 'lucide-react/dist/esm/icons/x.mjs'
import { NhanHoSo, TieuDeHoSo } from '../../components/GiaoDienPhongHop.jsx'
import { LOP_CHU_PHU, LOP_NOI, LOP_NUT_CHINH, LOP_NUT_PHU, LOP_NUT_XOA, LOP_THE } from '../../components/GiaoDienPhongHop.js'
import { layChiTietLoiMoi, layDanhSachLoiMoi, phanHoiLoiMoiCuocHop } from '../../api/thanhVienCuocHopApi.js'
import { layCuocHopCoNghiQuyet } from '../../api/nghiepVuCuocHopApi.js'
import { useDongBoDuLieu } from '../../services/useDongBoDuLieu.js'
import { dinhDangNgayGio } from '../../utils/thoiGianCuocHop.js'
import { TheTepHoSo } from './HoSoTaiLieuPhongHop.jsx'
import NghiQuyetCuocHop from './NghiQuyetCuocHop.jsx'

const TRANG_THAI = {
  CHO_PHAN_HOI: { ten: 'Chờ phản hồi', mau: 'vang' },
  DA_XAC_NHAN: { ten: 'Đã xác nhận', mau: 'luc' },
  TU_CHOI: { ten: 'Đã từ chối', mau: 'do' },
}
const HINH_THUC = { TRUC_TIEP: 'Trực tiếp', TRUC_TUYEN: 'Trực tuyến', KET_HOP: 'Kết hợp' }
const PHAM_VI = { NOI_BO: 'Nội bộ', LIEN_PHONG_BAN: 'Liên phòng ban', TOAN_CO_QUAN: 'Toàn cơ quan', BEN_NGOAI: 'Có khách bên ngoài' }
const LOP_LOI = 'rounded-lg border border-rose-200 bg-rose-50 p-3 text-sm text-rose-900'
const LOP_THAO_TAC = 'flex flex-wrap items-center gap-2'
const CAC_LOC = ['CHO_PHAN_HOI', 'DA_XAC_NHAN', 'TU_CHOI', 'tat-ca']
const DINH_DANG_GIO = new Intl.DateTimeFormat('vi-VN', { hour: '2-digit', minute: '2-digit', hourCycle: 'h23' })

function NhanLoiMoi({ trangThai }) {
  const nhan = TRANG_THAI[trangThai]
  return nhan && <NhanHoSo mau={nhan.mau}>{nhan.ten}</NhanHoSo>
}

function moTaLich(loiMoi) {
  const { thoiGianBatDau, thoiGianKetThuc } = loiMoi
  if (!thoiGianBatDau) return 'Chưa có lịch xác nhận'
  if (!thoiGianKetThuc) return dinhDangNgayGio(thoiGianBatDau)
  const cungNgay = thoiGianBatDau.slice(0, 10) === thoiGianKetThuc.slice(0, 10)
  const ketThuc = cungNgay ? DINH_DANG_GIO.format(new Date(thoiGianKetThuc)) : dinhDangNgayGio(thoiGianKetThuc)
  return `${dinhDangNgayGio(thoiGianBatDau)} – ${ketThuc}`
}

function ThongTin({ danhSach }) {
  return (
    <dl className="grid gap-2 sm:grid-cols-2">
      {danhSach.map(([ten, giaTri]) => (
        <div key={ten} className={LOP_NOI}>
          <dt className="text-xs font-medium text-slate-600">{ten}</dt>
          <dd className="mt-1 wrap-anywhere text-sm font-semibold">{giaTri || 'Chưa có thông tin'}</dd>
        </div>
      ))}
    </dl>
  )
}

function NhomTep({ ten, danhSach, token }) {
  return (
    <section className={LOP_THE + ' space-y-3'}>
      <TieuDeHoSo Icon={FileText}>{ten}</TieuDeHoSo>
      {danhSach.length === 0 && <p className={LOP_CHU_PHU}>Chưa có tệp đính kèm.</p>}
      <ul className="space-y-2">
        {danhSach.map((tep) => (
          <li key={tep.taiLieuId} className={LOP_THE}><TheTepHoSo tep={tep} token={token} /></li>
        ))}
      </ul>
    </section>
  )
}

function ChiTietLoiMoi({ loiMoiBanDau, token, phienDongBo, onPhanHoi, onQuayLai }) {
  const [chiTiet, setChiTiet] = useState(null)
  const [loi, setLoi] = useState('')
  const [loiGui, setLoiGui] = useState('')
  const [lanTai, setLanTai] = useState(0)
  const [dangGui, setDangGui] = useState(false)
  const [xacNhanTuChoi, setXacNhanTuChoi] = useState(false)
  const { cuocHopId } = loiMoiBanDau

  useEffect(() => {
    const controller = new AbortController()
    layChiTietLoiMoi(cuocHopId, token, controller.signal)
      .then((duLieu) => {
        if (controller.signal.aborted) return
        setChiTiet(duLieu)
        setLoi('')
      })
      .catch((error) => {
        if (controller.signal.aborted) return
        setChiTiet(null)
        setLoi(error.message)
      })
    return () => controller.abort()
  }, [cuocHopId, token, phienDongBo, lanTai])

  async function gui(thamDu) {
    if (dangGui || chiTiet?.loiMoi.trangThaiMoi !== 'CHO_PHAN_HOI') return
    setDangGui(true)
    setLoiGui('')
    try {
      await phanHoiLoiMoiCuocHop(cuocHopId, thamDu, token)
      if (thamDu) {
        setChiTiet((duLieu) => duLieu && ({
          ...duLieu, loiMoi: { ...duLieu.loiMoi, trangThaiMoi: 'DA_XAC_NHAN' },
        }))
      }
      onPhanHoi(cuocHopId, thamDu)
    } catch (error) {
      setLoiGui(error.message)
      setLanTai((lan) => lan + 1)
    } finally {
      setDangGui(false)
    }
  }

  const loiMoi = chiTiet?.loiMoi ?? loiMoiBanDau
  const choPhanHoi = chiTiet?.loiMoi.trangThaiMoi === 'CHO_PHAN_HOI'

  return (
    <div className="space-y-4">
      <button type="button" disabled={dangGui} onClick={onQuayLai} className={LOP_NUT_PHU}>
        <ArrowLeft size={16} aria-hidden="true" /> Về danh sách lời mời
      </button>
      <section className={LOP_THE + ' space-y-4'}>
        <TieuDeHoSo Icon={Mail} cap="h2" phu={loiMoi.maCuocHop} thaoTac={<NhanLoiMoi trangThai={loiMoi.trangThaiMoi} />}>
          {loiMoi.tieuDe}
        </TieuDeHoSo>
        {loiMoi.ngayMoi && <p className={LOP_CHU_PHU}>Được mời lúc {dinhDangNgayGio(loiMoi.ngayMoi)}</p>}
      </section>
      {loi && <div role="alert" className={LOP_LOI}>
        <p>{loi}</p>
        <button type="button" className={LOP_NUT_PHU + ' mt-2'} onClick={() => {
          setLoi('')
          setLanTai((lan) => lan + 1)
        }}>Tải lại chi tiết</button>
      </div>}
      {!loi && !chiTiet && <p role="status" className={LOP_CHU_PHU}>Đang tải chi tiết lời mời…</p>}
      {chiTiet && <>
        <section className={LOP_THE + ' space-y-3'}>
          <TieuDeHoSo Icon={CalendarDays}>Lịch và thông tin tổ chức</TieuDeHoSo>
          {chiTiet.dangChoDoiLich && <p className="rounded-lg bg-amber-50 p-3 text-sm text-amber-900">
            Đang chờ xác nhận lịch mới. Lịch dưới đây là lịch đã xác nhận gần nhất.
          </p>}
          <ThongTin danhSach={[
            ['Bắt đầu', loiMoi.thoiGianBatDau && dinhDangNgayGio(loiMoi.thoiGianBatDau)],
            ['Kết thúc', loiMoi.thoiGianKetThuc && dinhDangNgayGio(loiMoi.thoiGianKetThuc)],
            ['Địa điểm', loiMoi.tenPhongHop],
            ['Hình thức', HINH_THUC[loiMoi.loaiHinh]],
            ['Chủ trì', loiMoi.tenNguoiChuTri],
            ...(chiTiet.tenThuKyCuocHop ? [['Thư ký cuộc họp', chiTiet.tenThuKyCuocHop]] : []),
            ['Đơn vị chủ trì', chiTiet.phongBanChuTri],
          ]} />
        </section>
        <section className={LOP_THE + ' space-y-3'}>
          <TieuDeHoSo Icon={FileText}>Đề xuất cuộc họp</TieuDeHoSo>
          <ThongTin danhSach={[['Người đề xuất', chiTiet.tenNguoiDeXuat], ['Phạm vi', PHAM_VI[chiTiet.phamVi]]]} />
          <dl className="space-y-3">
            {[
              ['Mục đích', chiTiet.mucTieu],
              ['Kết quả kỳ vọng', chiTiet.ketQuaKyVong],
              ['Chương trình dự kiến', chiTiet.noiDungDuKien],
              ['Yêu cầu chuẩn bị trước', chiTiet.yeuCauChuanBi],
              ['Liên kết tài liệu', chiTiet.lienKetTaiLieu, true],
            ].filter(([, noiDung]) => noiDung).map(([ten, noiDung, laLienKet]) => (
              <div key={ten}>
                <dt className="text-sm font-semibold">{ten}</dt>
                <dd className={LOP_CHU_PHU + ' mt-1 whitespace-pre-wrap wrap-anywhere'}>
                  {laLienKet ? <LienKetDeXuat noiDung={noiDung} /> : noiDung}
                </dd>
              </div>
            ))}
          </dl>
        </section>
        <div className="grid items-start gap-4 xl:grid-cols-2">
          <NhomTep ten="Tài liệu đề xuất" danhSach={chiTiet.deXuat} token={token} />
          <NhomTep ten="Tài liệu kế hoạch đã duyệt" danhSach={chiTiet.keHoach} token={token} />
        </div>
      </>}
      {loiGui && <p role="alert" className={LOP_LOI}>{loiGui}</p>}
      {choPhanHoi && <section className={LOP_THE + ' space-y-3'}>
        <TieuDeHoSo Icon={Mail}>Phản hồi lời mời</TieuDeHoSo>
        {xacNhanTuChoi ? <>
          <p className={LOP_CHU_PHU}>Bạn xác nhận từ chối tham gia cuộc họp này?</p>
          <div className={LOP_THAO_TAC}>
            <button type="button" className={LOP_NUT_PHU} disabled={dangGui} onClick={() => setXacNhanTuChoi(false)}>Quay lại</button>
            <button type="button" className={LOP_NUT_XOA} disabled={dangGui} onClick={() => gui(false)}>Xác nhận từ chối</button>
          </div>
        </> : <div className={LOP_THAO_TAC}>
          <button type="button" className={LOP_NUT_CHINH} disabled={dangGui} onClick={() => gui(true)}>
            <Check size={16} aria-hidden="true" /> Đồng ý tham gia
          </button>
          <button type="button" className={LOP_NUT_XOA} disabled={dangGui} onClick={() => setXacNhanTuChoi(true)}>
            <X size={16} aria-hidden="true" /> Từ chối
          </button>
        </div>}
        {dangGui && <p role="status" className={LOP_CHU_PHU}>Đang gửi phản hồi…</p>}
      </section>}
    </div>
  )
}

export default function PhanHoiLoiMoi({ token }) {
  const phienDongBo = useDongBoDuLieu()
  const [danhSach, setDanhSach] = useState(null)
  const [nghiQuyet, setNghiQuyet] = useState([])
  const [loiNghiQuyet, setLoiNghiQuyet] = useState('')
  const [loi, setLoi] = useState('')
  const [lanTai, setLanTai] = useState(0)
  const [boLoc, setBoLoc] = useState('CHO_PHAN_HOI')
  const [dangXem, setDangXem] = useState(null)
  const [thongBao, setThongBao] = useState('')

  useEffect(() => {
    const controller = new AbortController()
    layDanhSachLoiMoi(token, controller.signal).then((duLieu) => {
      if (controller.signal.aborted) return
      setDanhSach(duLieu)
      setLoi('')
    }).catch((error) => {
      if (!controller.signal.aborted) setLoi(error.message)
    })
    layCuocHopCoNghiQuyet(token, controller.signal).then((duLieu) => {
      if (controller.signal.aborted) return
      setNghiQuyet(duLieu)
      setLoiNghiQuyet('')
    }).catch((error) => {
      if (!controller.signal.aborted) setLoiNghiQuyet(error.message)
    })
    return () => controller.abort()
  }, [token, phienDongBo, lanTai])

  function moTrang(muc) {
    setDangXem(muc)
    setThongBao('')
    window.scrollTo({ top: 0, behavior: 'instant' })
  }

  function daPhanHoi(cuocHopId, thamDu) {
    const trangThaiMoi = thamDu ? 'DA_XAC_NHAN' : 'TU_CHOI'
    setDanhSach((ds) => ds?.map((muc) => muc.cuocHopId === cuocHopId ? { ...muc, trangThaiMoi, coTheXemChiTiet: thamDu } : muc))
    setBoLoc(trangThaiMoi)
    setThongBao(thamDu ? 'Đã xác nhận tham gia cuộc họp.' : 'Đã từ chối lời mời.')
    if (!thamDu) setDangXem(null)
    window.scrollTo({ top: 0, behavior: 'instant' })
    setLanTai((lan) => lan + 1)
  }

  const thongBaoPhanHoi = thongBao && <p role="status" className="rounded-lg bg-emerald-50 p-3 text-sm font-medium text-emerald-900">{thongBao}</p>
  if (dangXem?.loai === 'loi-moi') {
    return <div className="space-y-4">
      {thongBaoPhanHoi}
      <ChiTietLoiMoi key={dangXem.muc.cuocHopId} loiMoiBanDau={dangXem.muc} token={token}
        phienDongBo={phienDongBo} onPhanHoi={daPhanHoi} onQuayLai={() => moTrang(null)} />
    </div>
  }
  if (dangXem?.loai === 'nghi-quyet') {
    return <div className={LOP_THE + ' space-y-4'}>
      <button type="button" className={LOP_NUT_PHU} onClick={() => moTrang(null)}>
        <ArrowLeft size={16} aria-hidden="true" /> Về danh sách lời mời
      </button>
      <h2 className="text-lg font-bold">{dangXem.muc.tieuDe}</h2>
      <NghiQuyetCuocHop cuocHopId={dangXem.muc.cuocHopId} token={token} />
    </div>
  }

  const daLoc = danhSach?.filter((muc) => boLoc === 'tat-ca' || muc.trangThaiMoi === boLoc)
  return (
    <div className="space-y-4">
      {thongBaoPhanHoi}
      <PhanHoiDoiLich token={token} />
      <section className={LOP_THE + ' space-y-4'}>
        <TieuDeHoSo Icon={Mail} cap="h2" thaoTac={<button type="button" className={LOP_NUT_PHU} onClick={() => setLanTai((lan) => lan + 1)}>Tải lại</button>}>
          Lời mời họp của tôi
        </TieuDeHoSo>
        <div role="group" aria-label="Lọc lời mời theo trạng thái" className={LOP_THAO_TAC}>
          {CAC_LOC.map((ma) => (
            <button type="button" key={ma} aria-pressed={boLoc === ma} onClick={() => setBoLoc(ma)}
              className={boLoc === ma ? LOP_NUT_CHINH : LOP_NUT_PHU}>
              {TRANG_THAI[ma]?.ten ?? 'Tất cả'}
              {danhSach && <span aria-hidden="true" className="tabular-nums">
                {ma === 'tat-ca' ? danhSach.length : danhSach.filter((muc) => muc.trangThaiMoi === ma).length}
              </span>}
            </button>
          ))}
        </div>
        {loi && <p role="alert" className={LOP_LOI}>{loi}</p>}
        {!loi && !danhSach && <p role="status" className={LOP_CHU_PHU}>Đang tải lời mời…</p>}
        {!loi && daLoc?.length === 0 && <p className={LOP_CHU_PHU}>
          {boLoc === 'CHO_PHAN_HOI' ? 'Bạn chưa có lời mời chờ phản hồi.' : 'Chưa có lời mời trong mục này.'}
        </p>}
        <ul className="space-y-3">
          {daLoc?.map((muc) => (
            <li key={muc.cuocHopId} className={LOP_THE + ' space-y-3'}>
              <TieuDeHoSo Icon={CalendarDays} phu={muc.maCuocHop}
                thaoTac={<NhanLoiMoi trangThai={muc.trangThaiMoi} />}>{muc.tieuDe}</TieuDeHoSo>
              <ul className="flex flex-wrap gap-x-5 gap-y-2 text-sm text-slate-700">
                <li className="flex items-center gap-2"><CalendarDays size={16} aria-hidden="true" />{moTaLich(muc)}</li>
                {(muc.tenPhongHop || HINH_THUC[muc.loaiHinh]) && <li className="flex items-center gap-2">
                  <MapPin size={16} aria-hidden="true" />{muc.tenPhongHop || HINH_THUC[muc.loaiHinh]}
                </li>}
                {muc.tenNguoiChuTri && <li className="flex items-center gap-2"><Users size={16} aria-hidden="true" />{muc.tenNguoiChuTri}</li>}
              </ul>
              {muc.coTheXemChiTiet && <button type="button" className={LOP_NUT_CHINH} onClick={() => moTrang({ loai: 'loi-moi', muc })}>
                Xem chi tiết
              </button>}
            </li>
          ))}
        </ul>
      </section>
      {loiNghiQuyet && <p role="alert" className={LOP_LOI}>{loiNghiQuyet}</p>}
      {nghiQuyet.length > 0 && <section className={LOP_THE + ' space-y-3'}>
        <TieuDeHoSo Icon={FileText} cap="h2">Nghị quyết đã công bố</TieuDeHoSo>
        <ul className="divide-y divide-slate-200">
          {nghiQuyet.map((muc) => (
            <li key={muc.cuocHopId} className="flex flex-wrap items-center justify-between gap-2 py-3">
              <p className="wrap-anywhere text-sm font-semibold">{muc.tieuDe}</p>
              <button type="button" className={LOP_NUT_PHU} onClick={() => moTrang({ loai: 'nghi-quyet', muc })}>Xem nghị quyết</button>
            </li>
          ))}
        </ul>
      </section>}
    </div>
  )
}
