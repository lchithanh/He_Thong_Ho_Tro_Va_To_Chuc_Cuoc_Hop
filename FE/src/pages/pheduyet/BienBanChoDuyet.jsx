import { useEffect, useState } from 'react'
import { LichCuaHoSo } from '../thuky/DanhSachDeXuatThuKy.jsx'
import {
  layBienBanChoToiDuyet,
  xuLyDuyetBienBan,
} from '../../api/bienBanApi.js'

export default function BienBanChoDuyet({ token }) {
  const [danhSach, setDanhSach] = useState(null)
  const [dangMo, setDangMo] = useState({})
  const [dangXuLy, setDangXuLy] = useState(false)
  const [loi, setLoi] = useState('')
  const [thongBao, setThongBao] = useState('')

  useEffect(() => {
    const controller = new AbortController()

    layBienBanChoToiDuyet(token, controller.signal)
      .then((duLieu) => {
        if (!controller.signal.aborted) setDanhSach(duLieu)
      })
      .catch((loiTai) => {
        if (!controller.signal.aborted) setLoi(loiTai.message)
      })

    return () => controller.abort()
  }, [token])

  async function xuLy(event, bienBan) {
    event.preventDefault()
    if (dangXuLy) return

    const thaoTac = event.nativeEvent.submitter?.value
    if (thaoTac !== 'phe-duyet' && thaoTac !== 'tra-lai') return

    const yKien = new FormData(event.currentTarget).get('yKien').trim()
    setLoi('')
    setThongBao('')

    if (thaoTac === 'tra-lai' && !yKien) {
      setLoi('Vui lòng nhập lý do trả lại biên bản.')
      return
    }

    setDangXuLy(true)

    try {
      const ketQua = await xuLyDuyetBienBan(bienBan, thaoTac, yKien || null, token)

      setDanhSach((hienTai) =>
        hienTai.filter((muc) => muc.yeuCauPheDuyetId !== bienBan.yeuCauPheDuyetId),
      )
      setThongBao(ketQua.message)
    } catch (loiXuLy) {
      setLoi(loiXuLy.message)
    } finally {
      setDangXuLy(false)
    }
  }

  return (
    <section className="space-y-4 rounded-xl border border-slate-200 bg-white p-5">
      <h2 className="text-lg font-bold">Biên bản chờ tôi duyệt</h2>

      {loi && <p role="alert" className="text-sm text-red-800">{loi}</p>}
      {thongBao && <p role="status" className="text-sm text-emerald-800">{thongBao}</p>}

      {!loi && danhSach === null && <p role="status">Đang tải biên bản…</p>}
      {danhSach?.length === 0 && <p>Hiện chưa có biên bản chờ bạn duyệt.</p>}

      {danhSach?.map((bienBan) => (
        <details
          key={bienBan.yeuCauPheDuyetId}
          onToggle={(event) => {
            const mo = event.currentTarget.open
            setDangMo((hienTai) => ({ ...hienTai, [bienBan.yeuCauPheDuyetId]: mo }))
          }}
          className="rounded-lg border border-slate-200 p-4"
        >
          <summary className="cursor-pointer font-semibold">
            {bienBan.tieuDeCuocHop || bienBan.tieuDe}
          </summary>

          <p className="mt-3 text-xs text-slate-500">{bienBan.cuocHopId && `Cuộc họp #${bienBan.cuocHopId}`}</p>
          {dangMo[bienBan.yeuCauPheDuyetId] && bienBan.cuocHopId && (
            <LichCuaHoSo cuocHopId={bienBan.cuocHopId} token={token} />
          )}
          <article className="mt-3 rounded-xl bg-slate-50 p-4">
            <h3 className="font-semibold">{bienBan.tieuDe}</h3>
            <p className="mt-1 text-xs text-slate-600">
              Phiên bản {bienBan.soPhienBan}
              {bienBan.capPheDuyet && ` · Cấp duyệt: ${bienBan.capPheDuyet === 'LANH_DAO' ? 'Lãnh đạo' : 'Chủ trì'}`}
            </p>
            <p className="mt-3 whitespace-pre-wrap break-words text-sm leading-6">{bienBan.noiDung}</p>
          </article>
          {bienBan.ghiChuTrinh && (
            <p className="mt-2 text-sm text-slate-600">
              Ghi chú của thư ký: {bienBan.ghiChuTrinh}
            </p>
          )}

          <form onSubmit={(event) => xuLy(event, bienBan)} className="mt-4 space-y-3">
            <label className="block text-sm font-medium">
              Ý kiến xử lý
              <textarea
                name="yKien"
                rows={2}
                disabled={dangXuLy}
                className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2"
              />
            </label>

            <div className="flex flex-wrap gap-2">
              <button
                type="submit"
                value="phe-duyet"
                disabled={dangXuLy}
                className="rounded-lg bg-emerald-800 px-4 py-2 text-sm font-semibold text-white disabled:opacity-60"
              >
                Phê duyệt
              </button>
              <button
                type="submit"
                value="tra-lai"
                disabled={dangXuLy}
                className="rounded-lg border border-slate-300 px-4 py-2 text-sm font-semibold disabled:opacity-60"
              >
                Trả lại
              </button>
            </div>
          </form>
        </details>
      ))}
    </section>
  )
}



