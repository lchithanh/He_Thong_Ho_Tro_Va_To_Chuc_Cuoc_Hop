import { useEffect, useState } from 'react'
import { useDongBoDuLieu } from '../../services/useDongBoDuLieu.js'
import KhongGianVanBan from './KhongGianVanBan.jsx'
import { NoiDungXemTruoc } from './XemTruocTepKeHoach.jsx'
import {
  layTaiLieuKetLuan,
  layTepKetLuan,
  taiLenTaiLieuKetLuan,
  taiXuongTaiLieuKetLuan,
} from '../../api/ketLuanCuocHopApi.js'

import {
  layTaiLieuBienBan,
  layTepBienBan,
  taiLenTaiLieuBienBan,
  taiXuongTaiLieuBienBan,
} from '../../api/bienBanApi.js'

const TAI_LIEU = {
  'bien-ban': {
    ten: 'Tệp đính kèm biên bản',
    lay: layTaiLieuBienBan,
    layTep: layTepBienBan,
    taiLen: taiLenTaiLieuBienBan,
    taiXuong: taiXuongTaiLieuBienBan,
  },
  'ket-luan': {
    ten: 'Tệp đính kèm kết luận',
    lay: layTaiLieuKetLuan,
    layTep: layTepKetLuan,
    taiLen: taiLenTaiLieuKetLuan,
    taiXuong: taiXuongTaiLieuKetLuan,
  },
}

function tenTep(taiLieu) {
  return taiLieu.tenFileGoc || taiLieu.tenTaiLieu || `Tài liệu #${taiLieu.taiLieuId}`
}

export default function TaiLieuKetLuan({ cuocHopId, token, choPhepTaiLen, loai = 'ket-luan', danhSachCoSan }) {
  const phienDongBo = useDongBoDuLieu()
  const taiLieuVanBan = TAI_LIEU[loai]
  const [danhSachTaiVe, setDanhSach] = useState(null)
  const [lanTai, setLanTai] = useState(0)
  const [tepIdDaChon, setTepIdDaChon] = useState(null)
  const [dangTaiLen, setDangTaiLen] = useState(false)
  const [loiDanhSach, setLoiDanhSach] = useState('')
  const [loiThaoTac, setLoiThaoTac] = useState('')

  const coDanhSachCoSan = danhSachCoSan !== undefined && !choPhepTaiLen
  const danhSach = coDanhSachCoSan ? danhSachCoSan : danhSachTaiVe
  const tep = danhSach?.find((taiLieu) => taiLieu.taiLieuId === tepIdDaChon)

  useEffect(() => {
    if (coDanhSachCoSan) return

    const controller = new AbortController()

    taiLieuVanBan.lay(cuocHopId, token, controller.signal)
      .then((duLieu) => {
        if (controller.signal.aborted) return

        setDanhSach(duLieu)
        setLoiDanhSach('')
      })
      .catch((loi) => {
        if (!controller.signal.aborted) setLoiDanhSach(loi.message)
      })

    return () => controller.abort()
  }, [cuocHopId, token, lanTai, taiLieuVanBan, coDanhSachCoSan, phienDongBo])

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
      const daTaiLen = await taiLieuVanBan.taiLen(cuocHopId, file, token)
      setTepIdDaChon(daTaiLen.taiLieuId)
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
      await taiLieuVanBan.taiXuong(cuocHopId, taiLieu, token)
    } catch (loi) {
      setLoiThaoTac(loi.message)
    }
  }

  const banXem = tep && (
    <section aria-label="Xem trước tệp đính kèm" className="min-w-0 overflow-hidden rounded-xl border border-slate-200 phong-toi:border-phong-vien bg-slate-100 phong-toi:bg-phong-mat-noi">
      <header className="flex flex-wrap items-center justify-between gap-3 border-b border-slate-200 phong-toi:border-phong-vien bg-white phong-toi:bg-phong-mat px-4 py-3">
        <div className="min-w-0">
          <h4 className="text-sm font-bold text-slate-900 phong-toi:text-phong-chu">Xem trước tài liệu</h4>
          <p className="mt-1 break-all text-xs text-slate-600 phong-toi:text-phong-chu-phu">{tenTep(tep)}</p>
        </div>
        <button type="button" onClick={() => setTepIdDaChon(null)}
          className="rounded-md border border-slate-300 phong-toi:border-phong-vien px-3 py-2 text-sm font-semibold text-slate-700 phong-toi:text-phong-chu-phu">
          Đóng xem trước
        </button>
      </header>
      <NoiDungXemTruoc key={tep.taiLieuId} tep={tep} cuocHopId={cuocHopId} token={token} layTep={taiLieuVanBan.layTep} />
    </section>
  )

  return (
    <div className="space-y-3 border-t border-slate-200 phong-toi:border-phong-vien pt-4">
      <h4 className="font-semibold">{taiLieuVanBan.ten}</h4>

      {choPhepTaiLen && (
        <form onSubmit={xuLyTaiLen} className="flex flex-wrap items-end gap-3">
          <label className="grid gap-1 text-sm font-medium">
            Chọn tệp đính kèm
            <input
              name="file"
              type="file"
              required
              disabled={dangTaiLen}
              className="max-w-full rounded-lg border border-slate-300 p-2 phong-toi:truong-phong-hop"
            />
          </label>

          <button
            type="submit"
            disabled={dangTaiLen}
            className="rounded-lg bg-emerald-800 phong-toi:bg-phong-nut px-4 py-2 text-sm font-semibold text-white disabled:opacity-60"
          >
            {dangTaiLen ? 'Đang tải lên…' : 'Tải lên'}
          </button>
        </form>
      )}

      {loiThaoTac && <p role="alert" className="text-sm text-red-800 phong-toi:text-phong-do">{loiThaoTac}</p>}

      <KhongGianVanBan banXem={banXem}>
        {!coDanhSachCoSan && loiDanhSach ? (
          <p role="alert" className="text-sm text-red-800 phong-toi:text-phong-do">{loiDanhSach}</p>
        ) : danhSach === null ? (
          <p role="status" className="text-sm text-slate-600 phong-toi:text-phong-chu-phu">Đang tải tài liệu…</p>
        ) : danhSach.length === 0 ? (
          <p className="text-sm text-slate-600 phong-toi:text-phong-chu-phu">Chưa có tệp đính kèm.</p>
        ) : (
          <ul className="divide-y divide-slate-200 phong-toi:divide-phong-vien text-sm">
            {danhSach.map((taiLieu) => (
              <li key={taiLieu.taiLieuId} className="flex flex-wrap items-center justify-between gap-2 py-2">
                <button type="button" aria-pressed={taiLieu.taiLieuId === tepIdDaChon}
                  onClick={() => setTepIdDaChon(taiLieu.taiLieuId)}
                  className="rounded-md px-2 py-1 text-left font-medium text-emerald-800 phong-toi:text-phong-xanh hover:bg-emerald-50 phong-toi:hover:bg-phong-mat-noi aria-pressed:bg-emerald-100">
                  <span className="sr-only">Xem trước </span>
                  <span className="break-all">{tenTep(taiLieu)}</span>
                </button>

                <button
                  type="button"
                  onClick={() => xuLyTaiXuong(taiLieu)}
                  className="rounded-lg border border-slate-300 phong-toi:border-phong-vien px-3 py-1.5 font-semibold text-emerald-800 phong-toi:text-phong-xanh"
                >
                  Tải xuống
                </button>
              </li>
            ))}
          </ul>
        )}
      </KhongGianVanBan>
    </div>
  )
}