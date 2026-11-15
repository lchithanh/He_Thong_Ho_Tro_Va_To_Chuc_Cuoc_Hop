import { useEffect, useState } from 'react'
import {
  layTaiLieuKetLuan,
  taiLenTaiLieuKetLuan,
  taiXuongTaiLieuKetLuan,
} from '../../api/ketLuanCuocHopApi.js'

export default function TaiLieuKetLuan({ cuocHopId, token, choPhepTaiLen }) {
  const [danhSach, setDanhSach] = useState(null)
  const [lanTai, setLanTai] = useState(0)
  const [dangTaiLen, setDangTaiLen] = useState(false)
  const [loiDanhSach, setLoiDanhSach] = useState('')
  const [loiThaoTac, setLoiThaoTac] = useState('')

  useEffect(() => {
    const controller = new AbortController()

    layTaiLieuKetLuan(cuocHopId, token, controller.signal)
      .then((duLieu) => {
        if (controller.signal.aborted) return

        setDanhSach(duLieu)
        setLoiDanhSach('')
      })
      .catch((loi) => {
        if (!controller.signal.aborted) setLoiDanhSach(loi.message)
      })

    return () => controller.abort()
  }, [cuocHopId, token, lanTai])

  async function xuLyTaiLen(event) {
    event.preventDefault()
    if (dangTaiLen || !choPhepTaiLen) return

    const form = event.currentTarget
    const file = new FormData(form).get('file')

    if (!file?.name) {
      setLoiThaoTac('Vui lòng chọn một tệp.')
      return
    }

    setDangTaiLen(true)
    setLoiThaoTac('')

    try {
      await taiLenTaiLieuKetLuan(cuocHopId, file, token)
      form.reset()
      setLanTai((hienTai) => hienTai + 1)
    } catch (loi) {
      setLoiThaoTac(loi.message)
    } finally {
      setDangTaiLen(false)
    }
  }

  async function xuLyTaiXuong(taiLieu) {
    setLoiThaoTac('')

    try {
      await taiXuongTaiLieuKetLuan(cuocHopId, taiLieu, token)
    } catch (loi) {
      setLoiThaoTac(loi.message)
    }
  }

  return (
    <div className="space-y-3 border-t border-slate-200 pt-4">
      <h4 className="font-semibold">Tài liệu kết luận</h4>

      {choPhepTaiLen && (
        <form onSubmit={xuLyTaiLen} className="flex flex-wrap items-end gap-3">
          <label className="grid gap-1 text-sm font-medium">
            Chọn tệp đính kèm
            <input
              name="file"
              type="file"
              required
              disabled={dangTaiLen}
              className="max-w-full rounded-lg border border-slate-300 p-2"
            />
          </label>

          <button
            type="submit"
            disabled={dangTaiLen}
            className="rounded-lg bg-emerald-800 px-4 py-2 text-sm font-semibold text-white disabled:opacity-60"
          >
            {dangTaiLen ? 'Đang tải lên…' : 'Tải lên'}
          </button>
        </form>
      )}

      {loiThaoTac && <p role="alert" className="text-sm text-red-800">{loiThaoTac}</p>}

      {loiDanhSach ? (
        <p role="alert" className="text-sm text-red-800">{loiDanhSach}</p>
      ) : danhSach === null ? (
        <p role="status" className="text-sm text-slate-600">Đang tải tài liệu…</p>
      ) : danhSach.length === 0 ? (
        <p className="text-sm text-slate-600">Chưa có tệp đính kèm.</p>
      ) : (
        <ul className="divide-y divide-slate-200 text-sm">
          {danhSach.map((taiLieu) => (
            <li key={taiLieu.taiLieuId} className="flex flex-wrap items-center justify-between gap-2 py-2">
              <span className="break-all">
                {taiLieu.tenFileGoc || `Tài liệu #${taiLieu.taiLieuId}`}
              </span>

              <button
                type="button"
                onClick={() => xuLyTaiXuong(taiLieu)}
                className="rounded-lg border border-slate-300 px-3 py-1.5 font-semibold text-emerald-800"
              >
                Tải xuống
              </button>
            </li>
          ))}
        </ul>
      )}
    </div>
  )
}