import { useRef, useState } from 'react'
import ClipboardCheck from 'lucide-react/dist/esm/icons/clipboard-check.mjs'
import { TieuDeHoSo } from '../../components/GiaoDienPhongHop.jsx'
import { xuLyYeuCauKeHoach } from '../../api/cuocHopApi.js'
import TiepNhanAction from './thaoTacKeHoach/TiepNhanAction.jsx'
import ChuyenLanhDaoAction from './thaoTacKeHoach/ChuyenLanhDaoAction.jsx'
import PheDuyetAction from './thaoTacKeHoach/PheDuyetAction.jsx'
import TraLaiAction from './thaoTacKeHoach/TraLaiAction.jsx'
import TuChoiAction from './thaoTacKeHoach/TuChoiAction.jsx'

const CAU_HINH_THAC_TAC = {
  choTiepNhan: [
    { ma: 'tiep-nhan', thongBao: 'Đã tiếp nhận hồ sơ.' },
    { ma: 'tra-lai', thongBao: 'Đã trả hồ sơ cho thư ký.', batBuocLyDo: true },
    { ma: 'tu-choi-tiep-nhan', thongBao: 'Đã từ chối tiếp nhận.', batBuocLyDo: true },
  ],
  dangRaSoat: [
    { ma: 'trinh-lanh-dao', thongBao: 'Đã chuyển lãnh đạo duyệt.' },
    { ma: 'tra-lai', thongBao: 'Đã trả hồ sơ cho thư ký.', batBuocLyDo: true },
  ],
  duyet: [
    { ma: 'phe-duyet', thongBao: 'Đã duyệt kế hoạch và xác nhận lịch họp.' },
    { ma: 'tra-lai', thongBao: 'Đã trả lại kế hoạch cho thư ký.', batBuocLyDo: true },
    { ma: 'tu-choi', thongBao: 'Đã từ chối kế hoạch.', batBuocLyDo: true },
  ],
}

function NutTheoThaoTac({ thaoTac, onClick, disabled, choPhepTrinhCapTren }) {
  const props = { onClick, disabled }

  switch (thaoTac.ma) {
    case 'tiep-nhan':
      return <TiepNhanAction {...props} />
    case 'trinh-lanh-dao':
      return <ChuyenLanhDaoAction {...props} />
    case 'phe-duyet':
      return <PheDuyetAction {...props} choPhepTrinhCapTren={choPhepTrinhCapTren} />
    case 'tra-lai':
      return <TraLaiAction {...props} />
    case 'tu-choi':
    case 'tu-choi-tiep-nhan':
      return <TuChoiAction {...props} tuChoiTiepNhan={thaoTac.ma === 'tu-choi-tiep-nhan'} />
    default:
      return null
  }
}

export default function ThaoTacKeHoach({
  cuocHopId,
  yeuCauPheDuyetId,
  token,
  cheDo,
  choPhepTrinhCapTren = false,
  onDaXuLy,
  onDangXuLy,
}) {
  const [yKien, setYKien] = useState('')
  const [loi, setLoi] = useState('')
  const [dangGui, setDangGui] = useState(false)
  const [daGui, setDaGui] = useState(false)
  const daKhoaGui = useRef(false)
  const oYKien = useRef(null)
  const cacThaoTac = CAU_HINH_THAC_TAC[cheDo]

  async function xuLy(ma, thongBaoMacDinh, batBuocLyDo) {
    if (daKhoaGui.current) return

    const lyDo = yKien.trim()
    if (batBuocLyDo && !lyDo) {
      setLoi('Nhập lý do để người lập kế hoạch biết cần xử lý gì.')
      oYKien.current?.focus()
      return
    }

    daKhoaGui.current = true
    setDangGui(true)
    onDangXuLy?.(true)
    setLoi('')

    let ketQua
    try {
      ketQua = await xuLyYeuCauKeHoach(
        cuocHopId, yeuCauPheDuyetId, ma, lyDo || null, token,
      )
    } catch (error) {
      setLoi(error.message)
      daKhoaGui.current = false
      setDangGui(false)
      onDangXuLy?.(false)
      return
    }

    setDaGui(true)
    setDangGui(false)

    try {
      await onDaXuLy(yeuCauPheDuyetId, ketQua?.message || thongBaoMacDinh)
    } catch {
      setLoi('Đã lưu thao tác. Hãy mở lại hồ sơ để cập nhật trạng thái.')
    } finally {
      onDangXuLy?.(false)
    }
  }

  if (!cacThaoTac || yeuCauPheDuyetId == null) return null

  return (
    <section
      aria-label="Xử lý kế hoạch"
      className="space-y-4 rounded-xl border border-blue-200 bg-blue-50/60 p-4 sm:p-5"
    >
      <TieuDeHoSo Icon={ClipboardCheck}
        phu="Nhập lý do khi yêu cầu chỉnh sửa hoặc từ chối. Khi đồng ý, bạn có thể ghi thêm ý kiến.">
        {cheDo === 'duyet' ? 'Quyết định duyệt' : 'Xử lý hồ sơ'}
      </TieuDeHoSo>

      <label className="block text-sm font-semibold text-slate-800">
        Ý kiến xử lý
        <textarea
          ref={oYKien}
          rows={3}
          value={yKien}
          onChange={(event) => setYKien(event.target.value)}
          disabled={dangGui || daGui}
          placeholder="Ghi chú thêm cho người tiếp nhận hồ sơ (không bắt buộc với thao tác đồng ý)."
          className="mt-1 w-full rounded-lg border border-slate-300 bg-white px-3 py-2 font-normal outline-none transition focus:border-emerald-700 focus:ring-2 focus:ring-emerald-100"
        />
      </label>

      {loi && <p role="alert" className="text-sm text-red-800">{loi}</p>}

      <div className="flex flex-wrap gap-2">
        {cacThaoTac.map((thaoTac) => (
          <NutTheoThaoTac
            key={thaoTac.ma}
            thaoTac={thaoTac}
            disabled={dangGui || daGui}
            choPhepTrinhCapTren={choPhepTrinhCapTren}
            onClick={() => xuLy(thaoTac.ma, thaoTac.thongBao, thaoTac.batBuocLyDo)}
          />
        ))}
      </div>

      {dangGui && <p role="status" className="text-sm font-medium text-slate-600">Đang lưu kết quả xử lý…</p>}
    </section>
  )
}