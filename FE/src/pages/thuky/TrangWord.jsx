import { useEffect, useState } from 'react'
import { layTepKeHoach } from '../../api/cuocHopApi.js'

const KHUNG_WORD = `<!doctype html>
<html lang="vi">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <meta http-equiv="Content-Security-Policy"
    content="default-src 'none'; style-src 'unsafe-inline'; img-src data:; font-src data:; base-uri 'none'; form-action 'none'">
  <style>
    html { overflow-y: scroll; overflow-x: auto; scrollbar-gutter: stable; }
    body { min-height: 100vh; margin: 0; background: #f1f5f9; }
    #noi-dung-word .docx-wrapper { padding: 16px; }
  </style>
</head>
<body><div id="noi-dung-word"></div></body>
</html>`

export default function TrangWord({ taiLieuId, cuocHopId, yeuCauId, token, tenTep, layTep = layTepKeHoach }) {
  const [khung, setKhung] = useState(null)
  const [loi, setLoi] = useState('')
  const [soTrang, setSoTrang] = useState(null)
  const daTai = soTrang !== null
  const [lanThu, setLanThu] = useState(0)

  useEffect(() => {
    if (!khung) return
    const controller = new AbortController()
    let theoDoiKichThuoc

    async function hienThiWord() {
      const [{ renderAsync }, tepWord] = await Promise.all([
        import('docx-preview'),
        layTep({ cuocHopId, yeuCauId }, taiLieuId, token, controller.signal),
      ])
      if (controller.signal.aborted) return
      const noiDung = khung.getElementById('noi-dung-word')
      const banVe = khung.createElement('div')
      await renderAsync(tepWord, banVe, undefined, {
        renderAltChunks: false,
        useBase64URL: true,
        ignoreLastRenderedPageBreak: false,
        breakPages: true,
      })
      if (controller.signal.aborted) return

      // Giữ bố cục của tài liệu và vô hiệu điều hướng từ các liên kết nhúng.
      banVe.querySelectorAll('a[href]').forEach((lienKet) => lienKet.removeAttribute('href'))
      noiDung.replaceChildren(banVe)
      const trang = banVe.querySelectorAll('section.docx')
      function vuaKhung() {
        const rongTrang = Math.max(0, ...Array.from(trang, (muc) => muc.offsetWidth))
        const rongKhung = khung.documentElement.clientWidth
        if (rongTrang > 0 && rongKhung > 32) {
          banVe.style.zoom = String(Math.min(1, rongKhung / (rongTrang + 64)))
        }
      }
      theoDoiKichThuoc = new ResizeObserver(vuaKhung)
      theoDoiKichThuoc.observe(khung.documentElement)
      vuaKhung()
      setSoTrang(trang.length)
    }

    hienThiWord().catch((error) => {
      if (!controller.signal.aborted) {
        setLoi(error.status ? error.message : 'Không đọc được bản xem trước Word. Bạn có thể tải tệp gốc.')
      }
    })
    return () => {
      controller.abort()
      theoDoiKichThuoc?.disconnect()
    }
  }, [khung, taiLieuId, cuocHopId, yeuCauId, token, lanThu, layTep])

  return (
    <>
      {loi ? (
        <div className="space-y-2 p-4 text-sm">
          <p role="alert" className="text-red-800">{loi}</p>
          <button type="button" className="font-semibold text-blue-900 underline" onClick={() => {
            setLoi('')
            setLanThu((lan) => lan + 1)
          }}>Thử lại</button>
        </div>
      ) : !daTai && <p role="status" className="p-4 text-sm">Đang chuẩn bị bản xem trước…</p>}
      {daTai && !loi && <p className="border-b border-slate-200 bg-white px-4 py-2 text-sm text-slate-700">
        {soTrang > 0 ? soTrang + ' trang · ' : ''}Cuộn trong khung để đọc toàn bộ tài liệu.
      </p>}
      <iframe title={`Xem trước ${tenTep}`} sandbox="allow-same-origin" srcDoc={KHUNG_WORD}
        onLoad={(event) => {
          const taiLieuKhung = event.currentTarget.contentDocument
          if (taiLieuKhung?.getElementById('noi-dung-word')) setKhung(taiLieuKhung)
        }}
        hidden={!daTai || Boolean(loi)} tabIndex={0} className="h-[min(82vh,64rem)] min-h-96 w-full border-0" />
    </>
  )
}
