import { useEffect, useState } from 'react'
import { docPhanHoiJson, goiApi } from '../api/apiClient.js'

export default function TepHoSo({ duongDan, token, maMoi, ten, xemPdf = true }) {
  const [mo, setMo] = useState(false)
  const [tep, setTep] = useState(null)
  const [loi, setLoi] = useState('')

  useEffect(() => {
    if (!mo) return
    const controller = new AbortController()
    let url
    goiApi(duongDan, { token, headers: maMoi ? { 'X-Ma-Loi-Moi': maMoi } : undefined, signal: controller.signal })
      .then(async (response) => {
        if (!response.ok) await docPhanHoiJson(response, 'Không mở được tệp.')
        const blob = await response.blob()
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
  }, [mo, duongDan, token, maMoi])

  return (
    <div className="space-y-2">
      <button type="button" className="font-semibold text-emerald-800 underline"
        aria-expanded={mo} onClick={() => { setTep(null); setLoi(''); setMo(!mo) }}>
        {mo ? 'Đóng tệp' : xemPdf ? 'Xem trước' : 'Mở tùy chọn tải'}
      </button>
      {mo && loi && <p role="alert">{loi}</p>}
      {mo && !tep && !loi && <p role="status">Đang tải tệp…</p>}
      {mo && tep && <>
        {xemPdf && tep.pdf && <iframe title={ten} src={tep.url} className="h-96 w-full rounded border" />}
        <a href={tep.url} download={ten} className="block font-semibold text-emerald-800 underline">Tải tệp</a>
      </>}
    </div>
  )
}
