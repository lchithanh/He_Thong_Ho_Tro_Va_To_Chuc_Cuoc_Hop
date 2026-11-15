import { useState } from 'react'
import { guiPhieuBieuQuyet } from '../api/bieuQuyetApi'


export default function FormBoPhieu({ cuocHopId, chiTiet, luaChon, token }) {
  const [luaChonId, setLuaChonId] = useState(null)
  const [dangGui, setDangGui] = useState(false)
  const [daGui, setDaGui] = useState(false)
  const [loi, setLoi] = useState('')

  async function xuLyGuiPhieu(event) {
    event.preventDefault()
    if (luaChonId === null || dangGui || daGui) return

    setDangGui(true)
    setLoi('')

    try {
      await guiPhieuBieuQuyet(cuocHopId, chiTiet.bieuQuyetId, luaChonId, token)
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
        <button type="submit" disabled={luaChonId === null || dangGui}>
          {dangGui ? 'Đang gửi…' : 'Gửi phiếu'}
        </button>
      </fieldset>
      {loi && <p role="alert">{loi}</p>}
    </form>
  )
}