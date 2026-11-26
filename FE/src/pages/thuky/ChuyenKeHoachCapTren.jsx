import { useDongBoDuLieu } from '../../services/useDongBoDuLieu.js'
import { useEffect, useState } from 'react'
import FileText from 'lucide-react/dist/esm/icons/file-text.mjs'
import { HopThoaiHoSo } from '../../components/GiaoDienPhongHop.jsx'
import { guiKeHoachLenCapTren, layLuaChonKeHoach, xuLyYeuCauKeHoach } from '../../api/cuocHopApi.js'

export default function ChuyenKeHoachCapTren({
  cuocHopId,
  yeuCauId,
  token,
  onDaGui,
}) {
  const phienDongBo = useDongBoDuLieu()
  const [luaChon, setLuaChon] = useState(null)
  const [lanhDaoId, setLanhDaoId] = useState('')
  const [ghiChu, setGhiChu] = useState('')
  const [loi, setLoi] = useState('')
  const [thongBao, setThongBao] = useState('')
  const [dangGui, setDangGui] = useState(false)
  const [daGui, setDaGui] = useState(false)
  const [dangMo, setDangMo] = useState(false)
  const lanhDaoMacDinh = luaChon?.thuKyNhomCoDinh && luaChon.lanhDaoDuyet.some(
    (nguoi) => nguoi.nguoiDungId === luaChon.nguoiDuyetNhomId,
  ) ? String(luaChon.nguoiDuyetNhomId) : ''
  const nguoiDuyetId = lanhDaoId || lanhDaoMacDinh

  useEffect(() => {
    const controller = new AbortController()
    layLuaChonKeHoach(cuocHopId, token, controller.signal)
      .then((duLieu) => {
        if (!controller.signal.aborted) setLuaChon(duLieu)
      })
      .catch((error) => {
        if (!controller.signal.aborted) setLoi(error.message)
      })
    return () => controller.abort()
  }, [cuocHopId, token, phienDongBo])

  async function gui(event) {
    event.preventDefault()
    if (dangGui || daGui || !nguoiDuyetId) return
    setDangGui(true)
    setLoi('')
    setThongBao('')

    try {
      const ketQua = await guiKeHoachLenCapTren(cuocHopId, yeuCauId, {
        lanhDaoId: Number(nguoiDuyetId),
        ghiChu: ghiChu.trim() || null,
      }, token)
      setDaGui(true)
      setDangMo(false)
      setThongBao(ketQua.message || 'Đã chuyển hồ sơ lên cấp trên.')
      try {
        await onDaGui?.(ketQua)
      } catch {
        setLoi('Đã chuyển hồ sơ. Hãy tải lại trang để cập nhật tiến độ mới nhất.')
      }
    } catch (error) {
      setLoi(error.message)
    } finally {
      setDangGui(false)
    }
  }

  async function deXuatLaiLich() {
    if (dangGui || daGui) return
    setDangGui(true)
    setLoi('')
    try {
      const ketQua = await xuLyYeuCauKeHoach(cuocHopId, yeuCauId, 'de-xuat-lai-lich', null, token)
      setDaGui(true)
      setDangMo(false)
      setThongBao(ketQua.message)
      try {
        await onDaGui?.(ketQua)
      } catch {
        setLoi('Đã mở lại kế hoạch. Hãy tải lại trang để tiếp tục.')
      }
    } catch (error) {
      setLoi(error.message)
    } finally {
      setDangGui(false)
    }
  }

  return (
    <section className="space-y-3 rounded-xl border border-blue-200 bg-blue-50/70 p-4">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <p className="text-sm text-blue-900">Hồ sơ đã qua bước duyệt đầu tiên.</p>
        {!daGui && <button type="button" className="rounded-lg bg-emerald-800 px-4 py-2 text-sm font-semibold text-white"
          onClick={() => setDangMo(true)}>Chuyển hồ sơ</button>}
      </div>
      {!dangMo && loi && <p role="alert" className="text-sm text-red-800">{loi}</p>}
      {!dangMo && thongBao && <p role="status" className="text-sm font-medium text-emerald-800">{thongBao}</p>}
      {dangMo && <HopThoaiHoSo tieuDe="Chuyển hồ sơ lên cấp trên"
        Icon={FileText} dangLuu={dangGui} onDong={() => setDangMo(false)}>
      {loi && <p role="alert" className="text-sm text-red-800">{loi}</p>}
      {thongBao && <p role="status" className="text-sm font-medium text-emerald-800">{thongBao}</p>}
      <form onSubmit={gui} className="space-y-3">
        <label className="block text-sm font-medium">
          Lãnh đạo duyệt
          <select
            required
            value={nguoiDuyetId}
            onChange={(event) => setLanhDaoId(event.target.value)}
            disabled={dangGui || daGui || luaChon === null}
            className="mt-1 w-full rounded-lg border border-slate-300 bg-white px-3 py-2"
          >
            <option value="">
              {luaChon === null ? 'Đang tải…' : 'Chọn lãnh đạo'}
            </option>
            {luaChon?.lanhDaoDuyet.map((nguoi) => (
              <option key={nguoi.nguoiDungId} value={nguoi.nguoiDungId}>
                {nguoi.hoTen}{nguoi.tenPhongBan ? ` · ${nguoi.tenPhongBan}` : ''}
              </option>
            ))}
          </select>
        </label>
        {luaChon?.lanhDaoDuyet.length === 0 && (
          <p role="alert" className="text-sm text-amber-900">
            Chưa có người đủ điều kiện nhận hồ sơ ở cấp này.
          </p>
        )}
        <label className="block text-sm font-medium">
          Ghi chú chuyển cấp (không bắt buộc)
          <textarea
            rows={2}
            value={ghiChu}
            onChange={(event) => setGhiChu(event.target.value)}
            disabled={dangGui || daGui}
            className="mt-1 w-full rounded-lg border border-slate-300 bg-white px-3 py-2"
          />
        </label>
        <button
          type="submit"
          disabled={dangGui || daGui || luaChon === null || !luaChon.lanhDaoDuyet.length}
          className="rounded-lg bg-emerald-800 px-4 py-2 text-sm font-semibold text-white disabled:opacity-60"
        >
          {dangGui ? 'Đang chuyển hồ sơ…' : 'Gửi hồ sơ lên cấp trên'}
        </button>
      </form>
      <div className="space-y-2 border-t border-blue-200 pt-3">
        <p className="text-sm text-blue-900">
          Nếu lịch đã quá hạn hoặc cần đổi giờ, mở lại hồ sơ rồi chọn lịch mới và trình lại cấp duyệt đầu tiên.
        </p>
        <button
          type="button"
          onClick={deXuatLaiLich}
          disabled={dangGui || daGui}
          className="rounded-lg border border-blue-300 bg-white px-4 py-2 text-sm font-semibold text-blue-900 disabled:opacity-60"
        >
          Đề xuất lại lịch
        </button>
      </div>
      </HopThoaiHoSo>}
    </section>
  )
}
