import { useEffect, useState } from 'react'
import { layTepKeHoach } from '../../api/cuocHopApi.js'

const SO_HANG_XEM = 100
const SO_COT_XEM = 30
const DINH_DANG_NGAY = new Intl.DateTimeFormat('vi-VN', { timeZone: 'UTC' })

function tenCot(viTri) {
  const chu = String.fromCharCode(65 + viTri % 26)
  return viTri < 26 ? chu : `A${chu}`
}

function hienThiGiaTri(giaTri) {
  if (giaTri == null) return ''
  if (giaTri instanceof Date) return DINH_DANG_NGAY.format(giaTri)
  return String(giaTri)
}

export default function TrangExcel({ taiLieuId, cuocHopId, yeuCauId, token, tenTep, layTep = layTepKeHoach }) {
  const [ketQua, setKetQua] = useState(null)
  const [trangId, setTrangId] = useState(0)
  const [lanThu, setLanThu] = useState(0)

  useEffect(() => {
    const controller = new AbortController()

    async function docBangTinh() {
      const [{ default: docExcel }, tep] = await Promise.all([
        import('read-excel-file/browser'),
        layTep({ cuocHopId, yeuCauId }, taiLieuId, token, controller.signal),
      ])
      if (controller.signal.aborted) return
      const cacTrang = await docExcel(tep)
      if (!controller.signal.aborted) setKetQua({ cacTrang })
    }

    docBangTinh().catch((error) => {
      if (!controller.signal.aborted) {
        setKetQua({ loi: error.status ? error.message : 'Không đọc được bảng tính. Bạn có thể tải tệp gốc để mở trong Excel.' })
      }
    })
    return () => controller.abort()
  }, [taiLieuId, cuocHopId, yeuCauId, token, lanThu, layTep])

  if (!ketQua) return <p role="status" className="p-4 text-sm">Đang chuẩn bị bảng tính…</p>
  if (ketQua.loi) return (
    <div className="space-y-2 p-4 text-sm">
      <p role="alert" className="text-red-800">{ketQua.loi}</p>
      <button type="button" className="font-semibold text-blue-900 underline" onClick={() => {
        setKetQua(null)
        setLanThu((lan) => lan + 1)
      }}>Thử lại</button>
    </div>
  )

  const { cacTrang } = ketQua
  if (!cacTrang.length) return <p className="p-4 text-sm text-slate-600">Tệp không có trang tính.</p>
  const trang = cacTrang[trangId] ?? cacTrang[0]
  const hang = trang.data.slice(0, SO_HANG_XEM)
  const soCot = hang.reduce((lonNhat, dong) => Math.max(lonNhat, dong.length), 0)
  const cot = Array.from({ length: Math.min(soCot, SO_COT_XEM) }, (_, viTri) => viTri)

  return (
    <div className="bg-white">
      <label className="flex flex-wrap items-center gap-2 border-b border-slate-200 px-4 py-2 text-sm font-medium">
        Trang tính
        <select value={trangId} onChange={(event) => setTrangId(Number(event.target.value))}
          className="max-w-full rounded-md border border-slate-300 px-2 py-1">
          {cacTrang.map((muc, viTri) => <option key={muc.sheet} value={viTri}>{muc.sheet}</option>)}
        </select>
      </label>
      {(trang.data.length > SO_HANG_XEM || soCot > SO_COT_XEM) && (
        <p className="border-b border-slate-200 px-4 py-2 text-xs text-slate-600">
          Bản xem trước hiển thị tối đa {SO_HANG_XEM} hàng và {SO_COT_XEM} cột đầu tiên. Tải tệp gốc để xem toàn bộ.
        </p>
      )}
      {cot.length ? (
        <div tabIndex={0} role="region" aria-label={`Bảng tính ${trang.sheet}`}
          className="max-h-[min(72vh,52rem)] overflow-auto focus-visible:outline-2 focus-visible:outline-blue-700">
          <table aria-label={`${tenTep} · ${trang.sheet}`} className="w-max min-w-full border-separate border-spacing-0 text-left text-sm">
            <thead className="sticky top-0 bg-slate-100 text-center text-slate-700">
              <tr>
                <th aria-label="Số hàng" className="border-b border-r border-slate-200 px-2 py-1" />
                {cot.map((viTri) => (
                  <th key={viTri} scope="col" className="border-b border-r border-slate-200 px-2 py-1">
                    {tenCot(viTri)}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody>
              {hang.map((dong, soHang) => (
                <tr key={soHang}>
                  <th scope="row" className="border-b border-r border-slate-200 bg-slate-50 px-2 py-1 text-right font-normal">{soHang + 1}</th>
                  {cot.map((viTri) => (
                    <td key={viTri} className="max-w-64 min-w-20 whitespace-pre-wrap break-words border-b border-r border-slate-200 px-2 py-1">
                      {hienThiGiaTri(dong[viTri])}
                    </td>
                  ))}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      ) : <p className="p-4 text-sm text-slate-600">Trang tính này trống.</p>}
    </div>
  )
}
