import { useState } from 'react'
import TepHoSo from './TepHoSo.jsx'
import { guiPhieuBieuQuyet } from '../api/bieuQuyetApi'


export default function FormBoPhieu({ cuocHopId, chiTiet, luaChon, token }) {
  const [luaChonId, setLuaChonId] = useState(null)
  const [dangGui, setDangGui] = useState(false)
  const [daGui, setDaGui] = useState(false)
  const [loi, setLoi] = useState('')
  const [lyDo, setLyDo] = useState('')
  const canLyDo = Boolean(chiTiet.nghiQuyetId)
    && luaChon.some((muc) => muc.luaChonId === luaChonId && muc.noiDung === 'Không đồng ý')

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

  if (daGui) return <p role="status">Đã gửi phiếu thành công.</p>
  if (chiTiet.trangThai !== 'DANG_MO') {
    return <p>Biểu quyết hiện không nhận phiếu.</p>
  }

  return (
    <form onSubmit={xuLyGuiPhieu}>
      <fieldset disabled={dangGui}>
        <legend>{chiTiet.tieuDe}</legend>
        {chiTiet.noiDung && <p className="my-2 whitespace-pre-wrap break-words">{chiTiet.noiDung}</p>}
        {chiTiet.taiLieuNghiQuyetId && <TepHoSo token={token} ten={chiTiet.tieuDe + '.pdf'}
          duongDan={`/api/cuoc-hop/${cuocHopId}/bieu-quyet/${chiTiet.bieuQuyetId}/tai-lieu-nghi-quyet`} />}
        {luaChon.map((phuongAn) => (
          <div key={phuongAn.luaChonId}>
            <label>
              <input
                type="radio"
                name={`bieu-quyet-${chiTiet.bieuQuyetId}`}
                checked={luaChonId === phuongAn.luaChonId}
                onChange={() => setLuaChonId(phuongAn.luaChonId)}
              />
              {phuongAn.noiDung}
            </label>
          </div>
        ))}
        {canLyDo && <label className="my-2 block">Lý do không đồng ý
          <textarea required maxLength={500} className="block w-full rounded border p-2" value={lyDo}
            onChange={(event) => setLyDo(event.target.value)} />
        </label>}
        <button type="submit" disabled={luaChonId === null || dangGui || (canLyDo && !lyDo.trim())}>
          {dangGui ? 'Đang gửi…' : 'Gửi phiếu'}
        </button>
      </fieldset>
      {loi && <p role="alert">{loi}</p>}
    </form>
  )
}