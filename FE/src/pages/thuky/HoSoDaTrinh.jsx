import CalendarDays from 'lucide-react/dist/esm/icons/calendar-days.mjs'
import FileCheck from 'lucide-react/dist/esm/icons/file-check.mjs'
import FileText from 'lucide-react/dist/esm/icons/file-text.mjs'
import Users from 'lucide-react/dist/esm/icons/users.mjs'
import { NhanHoSo, TieuDeHoSo } from '../../components/GiaoDienPhongHop.jsx'
import { dinhDangNgayGio } from '../../utils/thoiGianCuocHop.js'
import ThaoTacKeHoach from './ThaoTacKeHoach.jsx'
import XemTruocTepKeHoach from './XemTruocTepKeHoach.jsx'
import { useEffect, useState } from 'react'
import { useDongBoDuLieu } from '../../services/useDongBoDuLieu.js'
import {
  layChiTietKeHoachChoDuyet,
  layChiTietKeHoachHienTai,
  layTaiLieuLanTrinh,
} from '../../api/cuocHopApi.js'
import { TEN_PHAM_VI } from './hienThiDeXuat.js'

const TEN_TRANG_THAI = {
  CHO_DUYET: 'Đang chờ duyệt', DA_DUYET: 'Đã duyệt lần trình',
  TRA_LAI: 'Đã trả lại', TU_CHOI: 'Đã từ chối', DA_HUY: 'Đã hủy',
}

const MAU_TRANG_THAI = { CHO_DUYET: 'vang', DA_DUYET: 'luc', TRA_LAI: 'vang', TU_CHOI: 'do', DA_HUY: 'do' }
const TEN_CAP = { TRUONG_PHONG: 'Trưởng phòng', TRUONG_NHOM: 'Trưởng nhóm', LANH_DAO: 'Lãnh đạo', VAN_PHONG: 'Văn phòng' }

function NhomHoSo({ Icon, ten, cacMuc }) {
  return (
    <section className="min-w-0 space-y-3 rounded-xl border border-slate-200 bg-white p-3 sm:p-4">
      <TieuDeHoSo Icon={Icon} cap="h4">{ten}</TieuDeHoSo>
      <dl className="grid gap-2 sm:grid-cols-2">
        {cacMuc.map(([nhan, giaTri]) => (
          <div key={nhan} className="min-w-0 rounded-lg bg-slate-50 px-3 py-2">
            <dt className="text-xs font-semibold text-slate-600">{nhan}</dt>
            <dd className="mt-1 whitespace-pre-wrap wrap-anywhere text-sm text-slate-900">{giaTri || 'Chưa có thông tin'}</dd>
          </div>
        ))}
      </dl>
    </section>
  )
}

export default function HoSoDaTrinh({
  yeuCauId, cuocHopId, token, yeuCauDuyet, onDaXuLy, onDangXuLy, children,
}) {
  const phienDongBo = useDongBoDuLieu()
  const [hoSo, setHoSo] = useState(undefined)
  const [tep, setTep] = useState(null)
  const [loi, setLoi] = useState('')
  const [loiTep, setLoiTep] = useState('')
  const lanTrinhId = yeuCauId ?? hoSo?.yeuCauPheDuyetId

  useEffect(() => {
    const controller = new AbortController()
    const tacVu = yeuCauId != null
      ? layChiTietKeHoachChoDuyet(yeuCauId, token, controller.signal)
      : layChiTietKeHoachHienTai(cuocHopId, token, controller.signal)
    tacVu
      .then((duLieu) => {
        if (controller.signal.aborted) return
        setHoSo(duLieu)
        setLoi('')
      })
      .catch((error) => { if (!controller.signal.aborted) setLoi(error.message) })
    return () => controller.abort()
  }, [yeuCauId, cuocHopId, token, phienDongBo])

  useEffect(() => {
    if (lanTrinhId == null) return
    const controller = new AbortController()
    layTaiLieuLanTrinh(lanTrinhId, token, controller.signal)
      .then((duLieu) => {
        if (controller.signal.aborted) return
        setTep({ lanTrinhId, duLieu })
        setLoiTep('')
      })
      .catch((error) => { if (!controller.signal.aborted) setLoiTep(error.message) })
    return () => controller.abort()
  }, [lanTrinhId, token])

  if (loi) return <p role="alert" className="text-sm text-red-800">{loi}</p>
  if (hoSo === undefined) return <p role="status" className="text-sm">Đang tải hồ sơ…</p>
  if (!hoSo) return <p className="text-sm text-slate-600">Chưa có lần trình kế hoạch.</p>
  const duocDuyet = hoSo.trangThai === 'CHO_DUYET'
    && yeuCauDuyet?.yeuCauPheDuyetId === lanTrinhId
    && yeuCauDuyet?.cuocHopId === hoSo.cuocHopId
  const gio = (moc) => moc ? dinhDangNgayGio(moc) : null
  const dangCho = hoSo.trangThai === 'CHO_DUYET'
  const tenNguoiDuyet = hoSo.quyetDinh?.hoTenNguoiDuyet || hoSo.tenNguoiDuyet || hoSo.tenNguoiDuocChiDinhDuyet
  const lich = [hoSo.tenPhongHop, hoSo.viTriPhongHop].filter(Boolean).join(' · ')

  return (
    <section className="space-y-4 rounded-2xl border border-slate-200 bg-slate-50 p-3 sm:p-5">
      <TieuDeHoSo Icon={FileCheck} phu={hoSo.tieuDe} thaoTac={
        <NhanHoSo mau={MAU_TRANG_THAI[hoSo.trangThai]}>{TEN_TRANG_THAI[hoSo.trangThai] || hoSo.trangThai}</NhanHoSo>
      }>Hồ sơ kế hoạch đã trình</TieuDeHoSo>
      {dangCho && (
        <p className="rounded-lg border border-amber-200 bg-amber-50 p-3 text-sm text-amber-950">
          {duocDuyet ? 'Hồ sơ đang chờ bạn quyết định.'
            : tenNguoiDuyet ? `Đang chờ ${tenNguoiDuyet} duyệt.` : 'Hồ sơ đang chờ người được phân công duyệt.'}
        </p>
      )}
      <div className="grid items-start gap-3 xl:grid-cols-2">
        <NhomHoSo Icon={CalendarDays} ten="Lịch chuyển duyệt" cacMuc={[
          ['Bắt đầu', gio(hoSo.thoiGianBatDau)],
          ['Kết thúc', gio(hoSo.thoiGianKetThuc)],
          ['Hình thức', { TRUC_TUYEN: 'Trực tuyến', TRUC_TIEP: 'Trực tiếp' }[hoSo.loaiHinh]],
          ['Địa điểm', hoSo.loaiHinh === 'TRUC_TUYEN' ? 'Trực tuyến' : lich],
        ]} />
        <NhomHoSo Icon={Users} ten="Người phụ trách và cấp duyệt" cacMuc={[
          ['Người đề xuất', hoSo.tenNguoiDeXuat],
          ['Người lập và trình', hoSo.tenNguoiTrinh],
          ['Chủ trì', hoSo.tenChuTri],
          ['Thư ký cuộc họp', hoSo.tenThuKyCuocHop],
          ['Cấp duyệt', TEN_CAP[hoSo.capPheDuyet]],
          ['Người duyệt', tenNguoiDuyet],
          ['Thời điểm trình', gio(hoSo.thoiGianTrinh)],
          ...(hoSo.yeuCauTruocId == null ? [] : [['Lần trình trước', 'Đã qua cấp trưởng phòng']]),
        ]} />
      </div>
      <NhomHoSo Icon={FileText} ten="Nội dung kế hoạch" cacMuc={[
        ['Mục tiêu', hoSo.mucTieu],
        ['Chương trình dự kiến', hoSo.noiDungDuKien],
        ['Phạm vi', TEN_PHAM_VI[hoSo.phamViCuocHop] || hoSo.phamViCuocHop],
        ['Ghi chú trình', hoSo.ghiChuTrinh || 'Không có ghi chú'],
      ]} />
      <XemTruocTepKeHoach key={'xem-truoc-' + lanTrinhId} danhSach={tep?.lanTrinhId === lanTrinhId ? tep?.duLieu ?? null : null}
        yeuCauId={lanTrinhId} token={token} loiDanhSach={loiTep} />
      {duocDuyet && (
        <ThaoTacKeHoach
          key={'thao-tac-' + lanTrinhId}
          cuocHopId={hoSo.cuocHopId}
          yeuCauPheDuyetId={lanTrinhId}
          token={token}
          cheDo="duyet"
          choPhepTrinhCapTren={yeuCauDuyet.choPhepTrinhCapTren}
          onDaXuLy={onDaXuLy}
          onDangXuLy={onDangXuLy}
        />
      )}
      {hoSo.quyetDinh && (
        <NhomHoSo Icon={FileCheck} ten="Kết quả xử lý" cacMuc={[
          ['Quyết định', TEN_TRANG_THAI[hoSo.trangThai]],
          ['Thời điểm duyệt', gio(hoSo.quyetDinh.thoiGianDuyet)],
          ['Ý kiến phê duyệt', hoSo.quyetDinh.yKien || 'Không có ghi chú'],
        ]} />
      )}
      {children}
    </section>
  )
}
