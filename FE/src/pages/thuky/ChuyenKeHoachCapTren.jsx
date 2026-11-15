import { useEffect, useState } from 'react'
import { guiKeHoachLenCapTren, layLuaChonKeHoach } from '../../api/cuocHopApi.js'

export default function ChuyenKeHoachCapTren({
  cuocHopId,
  yeuCauId,
  token,
  onDaGui,
}) {
  const [luaChon, setLuaChon] = useState(null)
  const [lanhDaoId, setLanhDaoId] = useState('')
  const [ghiChu, setGhiChu] = useState('')
  const [loi, setLoi] = useState('')
  const [thongBao, setThongBao] = useState('')
  const [dangGui, setDangGui] = useState(false)
  const [daGui, setDaGui] = useState(false)

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
  }, [cuocHopId, token])

  async function gui(event) {
    event.preventDefault()
    if (dangGui || daGui || !lanhDaoId) return
    setDangGui(true)
    setLoi('')
    setThongBao('')

    try {
      const ketQua = await guiKeHoachLenCapTren(cuocHopId, yeuCauId, {
        lanhDaoId: Number(lanhDaoId),
        ghiChu: ghiChu.trim() || null,
      }, token)
      setDaGui(true)
      setThongBao(ketQua.message || 'Đã chuyển hồ sơ lên thư ký lãnh đạo.')
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

  return (
    <section className="space-y-3 rounded-xl border border-blue-200 bg-blue-50/70 p-4">
      <div>
        <h2 className="font-semibold text-blue-950">Chuyển hồ sơ lên cấp trên</h2>
        <p className="mt-1 text-sm text-blue-900">
          Trưởng phòng đã đồng ý trình. Chọn lãnh đạo; hệ thống sẽ gửi hồ sơ và các tệp của đúng lần trình này đến thư ký mặc định.
        </p>
      </div>
      {loi && <p role="alert" className="text-sm text-red-800">{loi}</p>}
      {thongBao && <p role="status" className="text-sm font-medium text-emerald-800">{thongBao}</p>}
      <form onSubmit={gui} className="space-y-3">
        <label className="block text-sm font-medium">
          Lãnh đạo duyệt
          <select
            required
            value={lanhDaoId}
            onChange={(event) => setLanhDaoId(event.target.value)}
            disabled={dangGui || daGui || luaChon === null}
            className="mt-1 w-full rounded-lg border border-slate-300 bg-white px-3 py-2"
          >
            <option value="">
              {luaChon === null ? 'Đang tải danh sách lãnh đạo…' : 'Chọn lãnh đạo'}
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
            Hiện chưa có lãnh đạo đủ điều kiện nhận hồ sơ. Vui lòng liên hệ quản trị viên.
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
    </section>
  )
}
