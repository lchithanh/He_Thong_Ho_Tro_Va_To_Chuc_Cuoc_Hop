import XemTruocTepKeHoach from './XemTruocTepKeHoach.jsx'
import { useDongBoDuLieu } from '../../services/useDongBoDuLieu.js'
import { useEffect, useState } from 'react'
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

export default function HoSoDaTrinh({ yeuCauId, cuocHopId, token, children }) {
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
      .then((duLieu) => { if (!controller.signal.aborted) setHoSo(duLieu) })
      .catch((error) => { if (!controller.signal.aborted) setLoi(error.message) })
    return () => controller.abort()
  }, [yeuCauId, cuocHopId, token, phienDongBo])

  useEffect(() => {
    if (lanTrinhId == null) return
    const controller = new AbortController()
    layTaiLieuLanTrinh(lanTrinhId, token, controller.signal)
      .then((duLieu) => { if (!controller.signal.aborted) setTep(duLieu) })
      .catch((error) => { if (!controller.signal.aborted) setLoiTep(error.message) })
    return () => controller.abort()
  }, [lanTrinhId, token, phienDongBo])

  if (loi) return <p role="alert" className="text-sm text-red-800">{loi}</p>
  if (hoSo === undefined) return <p role="status" className="text-sm">Đang tải hồ sơ…</p>
  if (!hoSo) return <p className="text-sm text-slate-600">Chưa có lần trình kế hoạch.</p>
  const gio = (moc) => moc ? new Date(moc).toLocaleString('vi-VN') : null

  return (
    <section className="space-y-4 rounded-xl border border-slate-200 bg-slate-50 p-4">
      <header>
        <p className="text-xs font-semibold text-emerald-800">{TEN_TRANG_THAI[hoSo.trangThai] || hoSo.trangThai}</p>
        <h3 className="mt-1 font-bold">{hoSo.tieuDe}</h3>
        <p className="mt-1 text-xs text-slate-600">Lần trình #{lanTrinhId}</p>
      </header>
      <XemTruocTepKeHoach key={lanTrinhId} danhSach={tep} yeuCauId={lanTrinhId} token={token} loiDanhSach={loiTep}>
        <dl className="grid gap-3 text-sm sm:grid-cols-2">
          {[
            ['Người đề xuất', hoSo.tenNguoiDeXuat],
            ['Mục tiêu', hoSo.mucTieu],
            ['Nội dung dự kiến', hoSo.noiDungDuKien],
            ['Người lập và trình', hoSo.tenNguoiTrinh],
            ['Thời điểm trình', gio(hoSo.thoiGianTrinh)],
            ['Chủ trì', hoSo.tenChuTri],
            ['Thư ký cuộc họp', hoSo.tenThuKyCuocHop],
            ['Phạm vi', TEN_PHAM_VI[hoSo.phamViCuocHop] || hoSo.phamViCuocHop],
            ['Hình thức', { TRUC_TUYEN: 'Trực tuyến', TRUC_TIEP: 'Trực tiếp' }[hoSo.loaiHinh]],
            ['Cấp duyệt', { TRUONG_PHONG: 'Trưởng phòng', LANH_DAO: 'Lãnh đạo' }[hoSo.capPheDuyet]],
            ['Lần trình cấp 1 liên kết', hoSo.yeuCauTruocId == null ? 'Không có' : `Yêu cầu #${hoSo.yeuCauTruocId}`],
            ['Lịch gửi duyệt', hoSo.lichHopId == null ? 'Chưa gắn lịch' : `Lịch #${hoSo.lichHopId}`],
            ['Bắt đầu theo lần trình', gio(hoSo.thoiGianBatDau)],
            ['Kết thúc theo lần trình', gio(hoSo.thoiGianKetThuc)],
            ['Phòng họp', hoSo.loaiHinh === 'TRUC_TUYEN' ? 'Trực tuyến' : [hoSo.tenPhongHop, hoSo.viTriPhongHop].filter(Boolean).join(' · ')],
            ['Ghi chú trình', hoSo.ghiChuTrinh || 'Không có ghi chú'],
            ['Người duyệt', hoSo.quyetDinh?.hoTenNguoiDuyet || hoSo.tenNguoiDuocChiDinhDuyet],
            ['Thời điểm duyệt', gio(hoSo.quyetDinh?.thoiGianDuyet) || 'Chưa ghi nhận quyết định'],
            ['Ý kiến phê duyệt', hoSo.quyetDinh?.yKien],
          ].map(([nhan, giaTri]) => (
            <div key={nhan}><dt className="font-semibold">{nhan}</dt><dd className="whitespace-pre-wrap break-words">{giaTri || 'Chưa có thông tin'}</dd></div>
          ))}
        </dl>
        {children}
      </XemTruocTepKeHoach>
    </section>
  )
}
