import { useState } from 'react'
import TepHoSo from './TepHoSo.jsx'
import { NhanHoSo } from './GiaoDienPhongHop.jsx'
import { LOP_CHU_PHU, LOP_NOI, LOP_NUT_CHINH, LOP_TRUONG } from './GiaoDienPhongHop.js'
const MAU_PHIEU = {
  'Đồng ý': 'text-emerald-800 phong-toi:text-phong-luc',
  'Không đồng ý': 'text-rose-800 phong-toi:text-phong-do',
  'Phiếu trắng': 'text-slate-700 phong-toi:text-phong-chu-phu',
  'Không ý kiến': 'text-slate-700 phong-toi:text-phong-chu-phu',
}
import { guiPhieuBieuQuyet } from '../api/bieuQuyetApi'


export default function FormBoPhieu({ cuocHopId, chiTiet, luaChon, token, hienTieuDe = true }) {
  const [luaChonId, setLuaChonId] = useState(null)
  const [dangGui, setDangGui] = useState(false)
  const [daGui, setDaGui] = useState(false)
  const [loi, setLoi] = useState('')
  const [lyDo, setLyDo] = useState('')
  const laPhieuCoKhong = !chiTiet.phuongThuc || chiTiet.phuongThuc === 'TAN_THANH'
  const luaChonHienThi = laPhieuCoKhong
    ? luaChon.filter((muc) => ['Đồng ý', 'Không đồng ý', 'Phiếu trắng', 'Không ý kiến'].includes(muc.noiDung))
    : luaChon
  const canLyDo = Boolean(chiTiet.nghiQuyetId) && laPhieuCoKhong
    && luaChonHienThi.some((muc) => luaChonId === muc.luaChonId && muc.noiDung === 'Không đồng ý')

  async function xuLyGuiPhieu(event) {
    event.preventDefault()
    if (luaChonId === null || dangGui || daGui) return

    setDangGui(true)
    setLoi('')

    try {
      await guiPhieuBieuQuyet(cuocHopId, chiTiet.bieuQuyetId, luaChonId, token, canLyDo ? lyDo.trim() : undefined)
      setDaGui(true)
    } catch (error) {
      setLoi(error.message)
    } finally {
      setDangGui(false)
    }
  }

  if (daGui) return <p role="status"><NhanHoSo mau="luc">Đã gửi phiếu thành công.</NhanHoSo></p>
  if (chiTiet.trangThai !== 'DANG_MO') {
    return <p>Biểu quyết hiện không nhận phiếu.</p>
  }

  return (
    <form onSubmit={xuLyGuiPhieu} className={LOP_NOI}>
      <fieldset disabled={dangGui}>
        <legend className={hienTieuDe ? "font-semibold" : "sr-only"}>{chiTiet.tieuDe}</legend>
        {chiTiet.noiDung && <p className="my-2 whitespace-pre-wrap break-words">{chiTiet.noiDung}</p>}
        {chiTiet.taiLieuNghiQuyetId && <TepHoSo token={token} ten={chiTiet.tieuDe + '.pdf'}
          duongDan={`/api/cuoc-hop/${cuocHopId}/bieu-quyet/${chiTiet.bieuQuyetId}/tai-lieu-nghi-quyet`} />}
        <p className={LOP_CHU_PHU + ' my-2'}>
          {chiTiet.cheDoBieuQuyet === 'AN_DANH' ? 'Ẩn danh' : 'Công khai'} · Mỗi phiếu chọn một phương án
        </p>
        <div className="my-3 grid grid-cols-[repeat(auto-fit,minmax(min(100%,10rem),1fr))] gap-2">
          {luaChonHienThi.map((phuongAn) => (
            <label key={phuongAn.luaChonId}
              className={`flex cursor-pointer items-start gap-2.5 rounded-xl border border-slate-300 bg-white p-3 text-sm font-semibold has-checked:border-blue-500 has-checked:bg-blue-50 focus-within:border-blue-500 phong-toi:border-phong-vien phong-toi:bg-phong-mat phong-toi:has-checked:border-phong-focus phong-toi:has-checked:bg-phong-lua-chon phong-toi:focus-within:border-phong-focus ${laPhieuCoKhong ? MAU_PHIEU[phuongAn.noiDung] ?? '' : ''}`}>
              <input
                className="mt-1 shrink-0"
                type="radio"
                name={`bieu-quyet-${chiTiet.bieuQuyetId}`}
                checked={luaChonId === phuongAn.luaChonId}
                onChange={() => setLuaChonId(phuongAn.luaChonId)}
              />
              <span className="wrap-anywhere">{laPhieuCoKhong && phuongAn.noiDung === 'Không ý kiến' ? 'Phiếu trắng' : phuongAn.noiDung}</span>
            </label>
          ))}
        </div>
        {canLyDo && <label className="my-2 block">Lý do không đồng ý
          <textarea required maxLength={500} className={LOP_TRUONG + ' mt-1'} value={lyDo}
            onChange={(event) => setLyDo(event.target.value)} />
        </label>}
        <button type="submit" className={LOP_NUT_CHINH} disabled={luaChonId === null || dangGui || (canLyDo && !lyDo.trim())}>
          {dangGui ? 'Đang gửi…' : 'Gửi phiếu'}
        </button>
      </fieldset>
      {loi && <p role="alert">{loi}</p>}
    </form>
  )
}