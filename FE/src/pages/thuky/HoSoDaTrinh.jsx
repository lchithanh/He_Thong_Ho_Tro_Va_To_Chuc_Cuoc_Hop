import { useEffect, useState } from 'react'
import {
  layChiTietKeHoachChoDuyet,
  layHoSoBanGiao,
  layTaiLieuLanTrinh,
  taiXuongTaiLieuLanTrinh,
} from '../../api/cuocHopApi.js'
import { TEN_PHAM_VI } from './hienThiDeXuat.js'

export default function HoSoDaTrinh({ yeuCauId, token, daDuyet = false }) {
  const [hoSo, setHoSo] = useState(null)
  const [tep, setTep] = useState(null)
  const [loi, setLoi] = useState('')
  const [loiTep, setLoiTep] = useState('')
  useEffect(() => {
    const controller = new AbortController()
    const taiHoSo = daDuyet ? layHoSoBanGiao : layChiTietKeHoachChoDuyet
    taiHoSo(yeuCauId, token, controller.signal)
      .then((duLieu) => { if (!controller.signal.aborted) setHoSo(duLieu) })
      .catch((error) => { if (!controller.signal.aborted) setLoi(error.message) })
    layTaiLieuLanTrinh(yeuCauId, token, controller.signal)
      .then((duLieu) => { if (!controller.signal.aborted) setTep(duLieu) })
      .catch((error) => { if (!controller.signal.aborted) setLoiTep(error.message) })
    return () => controller.abort()
  }, [yeuCauId, token, daDuyet])

  if (loi) return <p role="alert" className="text-sm text-red-800">{loi}</p>
  if (!hoSo) return <p role="status" className="text-sm">Đang tải hồ sơ…</p>
  const gio = (moc) => moc ? new Date(moc).toLocaleString('vi-VN') : null

  return (
    <section className="space-y-4 rounded-xl border border-slate-200 bg-slate-50 p-4">
      <header>
        <p className="text-xs font-semibold text-emerald-800">{daDuyet ? 'Đã bàn giao hồ sơ' : 'Hồ sơ chờ rà soát'}</p>
        <h3 className="mt-1 font-bold">{hoSo.tieuDe}</h3>
        <p className="text-xs text-slate-600">Lần trình #{yeuCauId}</p>
        {hoSo.yeuCauTruocId != null && (
          <p className="mt-1 text-xs text-blue-800">
            Lần trình cấp trên này liên kết với quyết định cấp 1 #{hoSo.yeuCauTruocId}.
          </p>
        )}
      </header>
      <dl className="grid gap-3 text-sm sm:grid-cols-2">
        {[
          ['Người đề xuất', hoSo.tenNguoiDeXuat],
          ['Người lập và trình', hoSo.tenNguoiTrinh],
          ['Chủ trì', hoSo.tenChuTri],
          ['Thư ký cuộc họp', hoSo.tenThuKyCuocHop],
          ['Phạm vi', TEN_PHAM_VI[hoSo.phamViCuocHop] || hoSo.phamViCuocHop],
          ['Hình thức', hoSo.loaiHinh === 'TRUC_TUYEN' ? 'Trực tuyến' : 'Trực tiếp'],
          ['Bắt đầu theo lần trình', gio(hoSo.thoiGianBatDau)],
          ['Kết thúc theo lần trình', gio(hoSo.thoiGianKetThuc)],
          ['Phòng họp', [hoSo.tenPhongHop, hoSo.viTriPhongHop].filter(Boolean).join(' · ')],
          ['Mục tiêu', hoSo.mucTieu],
          ['Nội dung kế hoạch', hoSo.noiDungDuKien],
          ['Ghi chú trình', hoSo.ghiChuTrinh],
          ['Người duyệt', hoSo.quyetDinh?.hoTenNguoiDuyet],
          ['Thời điểm duyệt', gio(hoSo.quyetDinh?.thoiGianDuyet)],
          ['Ý kiến phê duyệt', hoSo.quyetDinh?.yKien],
        ].filter(([, giaTri]) => giaTri).map(([nhan, giaTri]) => (
          <div key={nhan}><dt className="font-semibold">{nhan}</dt><dd className="whitespace-pre-wrap break-words">{giaTri}</dd></div>
        ))}
      </dl>
      <div className="space-y-2 border-t border-slate-200 pt-3 text-sm">
        <h4 className="font-semibold">Tệp của lần trình này</h4>
        {loiTep ? <p role="alert" className="text-red-800">{loiTep}</p>
          : !tep ? <p role="status">Đang tải tài liệu…</p> : !tep.length && <p>Chưa có tệp được lưu cho lần trình này.</p>}
        <ul className="space-y-1">
          {tep?.map((muc) => <li key={muc.taiLieuId}>
            <button type="button" className="break-all text-left font-semibold text-emerald-800 underline"
              onClick={() => taiXuongTaiLieuLanTrinh(yeuCauId, muc, token).catch((error) => setLoiTep(error.message))}>
              Tải {muc.tenFileGoc || muc.tenTaiLieu}
            </button>
          </li>)}
        </ul>
      </div>
    </section>
  )
}
