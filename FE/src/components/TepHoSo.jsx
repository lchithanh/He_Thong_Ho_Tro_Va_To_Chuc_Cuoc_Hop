import { useCallback, useEffect, useState } from 'react'
import Eye from 'lucide-react/dist/esm/icons/eye.mjs'
import Download from 'lucide-react/dist/esm/icons/download.mjs'
import { docPhanHoiJson, goiApi } from '../api/apiClient.js'

async function docTepHoSo(duongDan, token, maMoi, signal) {
  const response = await goiApi(duongDan, {
    token, headers: maMoi ? { 'X-Ma-Loi-Moi': maMoi } : undefined, signal,
  })
  if (!response.ok) await docPhanHoiJson(response, 'Không mở được tệp.')
  return response.blob()
}

export default function TepHoSo({ duongDan, token, maMoi, ten, xemPdf = true, xemRong = false, taiTrucTiep = false, xemNoiDung, tomTat }) {
  const [mo, setMo] = useState(false)
  const [tep, setTep] = useState(null)
  const [loi, setLoi] = useState('')
  const [dangTai, setDangTai] = useState(false)
  const [loiTai, setLoiTai] = useState('')
  const coBanXemTruoc = xemPdf || Boolean(xemNoiDung)
  const coBanXemTruocRieng = Boolean(xemNoiDung)
  const layTepXemTruoc = useCallback(
    (_hoSo, _taiLieuId, _token, signal) => docTepHoSo(duongDan, token, maMoi, signal),
    [duongDan, token, maMoi],
  )

  useEffect(() => {
    if (!mo || coBanXemTruocRieng) return
    const controller = new AbortController()
    let url
    docTepHoSo(duongDan, token, maMoi, controller.signal)
      .then((blob) => {
        if (!controller.signal.aborted) {
          url = URL.createObjectURL(blob)
          setTep({ url, pdf: blob.type === 'application/pdf' })
        }
      })
      .catch((error) => { if (!controller.signal.aborted) setLoi(error.message) })
    return () => {
      controller.abort()
      if (url) URL.revokeObjectURL(url)
    }
  }, [mo, coBanXemTruocRieng, duongDan, token, maMoi])

  async function taiTep() {
    if (dangTai) return
    setDangTai(true)
    setLoiTai('')
    try {
      const blob = await docTepHoSo(duongDan, token, maMoi)
      const url = URL.createObjectURL(blob)
      const lienKet = document.createElement('a')
      lienKet.href = url
      lienKet.download = ten
      document.body.appendChild(lienKet)
      lienKet.click()
      lienKet.remove()
      window.setTimeout(() => URL.revokeObjectURL(url), 1000)
    } catch (error) {
      setLoiTai(error.message)
    } finally {
      setDangTai(false)
    }
  }

  const lopNut = tomTat
    ? 'inline-flex items-center gap-1.5 rounded-lg border border-slate-300 px-3 py-1.5 text-xs font-semibold text-blue-900 hover:bg-blue-50 disabled:opacity-60 phong-toi:border-phong-vien phong-toi:bg-phong-mat-noi phong-toi:text-phong-xanh phong-toi:hover:bg-phong-nhan-xanh'
    : 'font-semibold text-emerald-800 underline disabled:opacity-60 phong-toi:text-phong-xanh'

  return (
    <div className="min-w-0 space-y-2">
      <div className="flex flex-wrap items-start justify-between gap-3">
        {tomTat}
        <div className="flex shrink-0 flex-wrap gap-2">
          {(!taiTrucTiep || coBanXemTruoc) && (
            <button type="button" className={lopNut}
              aria-expanded={mo} onClick={() => { setTep(null); setLoi(''); setMo(!mo) }}>
              {tomTat && <Eye size={14} aria-hidden="true" />}
              {mo ? 'Đóng tệp' : coBanXemTruoc ? 'Xem trước' : 'Mở tùy chọn tải'}
            </button>
          )}
          {taiTrucTiep && (
            <button type="button" disabled={dangTai} onClick={taiTep}
              className={lopNut}>
              {tomTat && <Download size={14} aria-hidden="true" />}
              {dangTai ? 'Đang tải tệp…' : 'Tải xuống'}
            </button>
          )}
        </div>
      </div>
      {loiTai && <p role="alert">{loiTai}</p>}
      {mo && loi && <p role="alert">{loi}</p>}
      {mo && coBanXemTruocRieng && xemNoiDung(layTepXemTruoc)}
      {mo && !coBanXemTruocRieng && !tep && !loi && <p role="status">Đang tải tệp…</p>}
      {mo && tep && <>
        {xemPdf && tep.pdf && <iframe title={ten} src={tep.url} className={`h-96 w-full rounded border phong-toi:border-phong-vien ${xemRong ? 'phong-toi:h-[clamp(22rem,65vh,54rem)]' : ''}`} />}
        {!taiTrucTiep && <a href={tep.url} download={ten} className="block font-semibold text-emerald-800 phong-toi:text-phong-xanh underline">Tải tệp</a>}
      </>}
    </div>
  )
}
