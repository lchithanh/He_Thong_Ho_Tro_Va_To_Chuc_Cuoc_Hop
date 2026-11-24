import { useEffect, useState } from 'react'
import TrangExcel from './TrangExcel.jsx'
import TrangWord from './TrangWord.jsx'
import KhongGianVanBan from './KhongGianVanBan.jsx'
import { layTepKeHoach, taiXuongTepKeHoach } from '../../api/cuocHopApi.js'

const DINH_DANG_XEM = new Set(['pdf', 'docx', 'xlsx'])

function tenTep(muc) {
  return muc.tenFileGoc || muc.tenTaiLieu || `Tài liệu #${muc.taiLieuId}`
}

export default function XemTruocTepKeHoach({ danhSach, cuocHopId, yeuCauId, token, loiDanhSach, children }) {
  const [tepIdDaChon, setTepIdDaChon] = useState(null)
  const [loiTaiXuong, setLoiTaiXuong] = useState('')
  const tep = danhSach?.find((muc) => muc.taiLieuId === tepIdDaChon)
    ?? danhSach?.find((muc) => DINH_DANG_XEM.has(muc.dinhDangFile))
    ?? danhSach?.[0]

  async function taiXuong() {
    setLoiTaiXuong('')
    try {
      await taiXuongTepKeHoach({ cuocHopId, yeuCauId }, tep, token)
    } catch (error) {
      setLoiTaiXuong(error.message)
    }
  }

  const banXem = (
    <section aria-label="Xem trước tệp kế hoạch"
      className="min-w-0 overflow-hidden rounded-xl border border-slate-200 bg-slate-100">
      <header className="flex flex-wrap items-center justify-between gap-3 border-b border-slate-200 bg-white px-4 py-3">
        <div className="min-w-0">
          <h4 className="text-sm font-bold text-slate-900">Xem trước tài liệu</h4>
          {tep && <p className="mt-1 break-all text-xs text-slate-600">{tenTep(tep)}</p>}
        </div>
        {tep && (
          <button type="button" onClick={taiXuong}
            className="rounded-md border border-blue-200 px-3 py-2 text-sm font-semibold text-blue-900">
            Tải tệp gốc
          </button>
        )}
      </header>
      {loiTaiXuong && <p role="alert" className="p-4 text-sm text-red-800">{loiTaiXuong}</p>}
      {tep ? (
        <NoiDungXemTruoc key={tep.taiLieuId} tep={tep} cuocHopId={cuocHopId}
          yeuCauId={yeuCauId} token={token} />
      ) : (
        <p className="p-4 text-sm text-slate-600">Chọn một tệp trong danh sách để xem nội dung.</p>
      )}
    </section>
  )

  return (
    <KhongGianVanBan banXem={banXem}>
      <section className="overflow-hidden rounded-xl border border-slate-200 bg-white">
        <h4 className="border-b border-slate-200 px-4 py-3 text-sm font-bold text-slate-900">
          {yeuCauId == null ? 'Tài liệu kế hoạch' : 'Tài liệu của lần trình'}
        </h4>
        {loiDanhSach ? <p role="alert" className="p-4 text-sm text-red-800">{loiDanhSach}</p>
          : danhSach == null ? <p role="status" className="p-4 text-sm">Đang tải danh sách tài liệu…</p>
            : danhSach.length === 0 ? <p className="p-4 text-sm text-slate-600">Chưa có tệp kế hoạch.</p>
              : (
                <ul className="max-h-72 divide-y divide-slate-100 overflow-y-auto">
                  {danhSach.map((muc) => (
                    <li key={muc.taiLieuId}>
                      <button type="button" aria-pressed={muc.taiLieuId === tep?.taiLieuId}
                        onClick={() => {
                          setTepIdDaChon(muc.taiLieuId)
                          setLoiTaiXuong('')
                        }}
                        className="flex w-full items-center gap-3 border-l-4 border-transparent px-3 py-2.5 text-left text-sm hover:bg-slate-50 focus-visible:outline-2 focus-visible:-outline-offset-2 focus-visible:outline-blue-700 aria-pressed:border-blue-700 aria-pressed:bg-blue-50 aria-pressed:text-blue-950">
                        <span className="w-12 shrink-0 rounded bg-slate-100 py-1 text-center text-xs font-semibold uppercase text-slate-700">
                          {muc.dinhDangFile || 'Tệp'}
                        </span>
                        <span className="min-w-0 break-words font-medium">{tenTep(muc)}</span>
                      </button>
                    </li>
                  ))}
                </ul>
              )}
      </section>
      {children}
    </KhongGianVanBan>
  )
}

export function NoiDungXemTruoc({ tep, cuocHopId, yeuCauId, token, layTep = layTepKeHoach }) {
  const thongTin = { taiLieuId: tep.taiLieuId, cuocHopId, yeuCauId, token, tenTep: tenTep(tep), layTep }
  const dinhDang = (tep.dinhDangFile || tenTep(tep).split('.').pop()).toLowerCase()
  if (dinhDang === 'docx') return <TrangWord {...thongTin} />
  if (dinhDang === 'pdf') return <TrangPdf {...thongTin} />
  if (dinhDang === 'xlsx') return <TrangExcel {...thongTin} />
  return (
    <p className="p-4 text-sm text-slate-600 phong-toi:text-phong-chu-phu">
      {dinhDang === 'doc'
        ? 'Hãy tải tệp gốc để mở. Để xem trong hồ sơ, lưu tệp Word dạng .docx hoặc PDF rồi tải lên.'
        : dinhDang === 'xls'
          ? 'Hãy tải tệp gốc để mở. Để xem trong hồ sơ, lưu tệp Excel dạng .xlsx rồi tải lên.'
          : 'Hãy tải tệp gốc để mở định dạng này.'}
    </p>
  )
}

function TrangPdf({ taiLieuId, cuocHopId, yeuCauId, token, tenTep, layTep }) {
  const [ketQua, setKetQua] = useState(null)
  const [lanThu, setLanThu] = useState(0)

  useEffect(() => {
    const controller = new AbortController()
    let url
    layTep({ cuocHopId, yeuCauId }, taiLieuId, token, controller.signal)
      .then(async (blob) => {
        const dauTep = await blob.slice(0, 1024).text()
        if (controller.signal.aborted) return
        if (!dauTep.includes('%PDF-')) throw new Error('Máy chủ chưa trả về tệp PDF hợp lệ.')
        const tepPdf = blob.type === 'application/pdf' ? blob : blob.slice(0, blob.size, 'application/pdf')
        url = URL.createObjectURL(tepPdf)
        setKetQua({ url })
      })
      .catch((error) => {
        if (!controller.signal.aborted) setKetQua({ loi: error.message })
      })
    return () => {
      controller.abort()
      if (url) URL.revokeObjectURL(url)
    }
  }, [taiLieuId, cuocHopId, yeuCauId, token, lanThu, layTep])

  if (!ketQua) return <p role="status" className="p-4 text-sm">Đang chuẩn bị bản xem trước…</p>
  if (ketQua.loi) return (
    <div className="space-y-2 p-4 text-sm">
      <p role="alert" className="text-red-800 phong-toi:text-phong-do">{ketQua.loi}</p>
      <button type="button" className="font-semibold text-blue-900 phong-toi:text-phong-xanh underline" onClick={() => {
        setKetQua(null)
        setLanThu((lan) => lan + 1)
      }}>Thử lại</button>
    </div>
  )

  return (
    <>
      <div className="border-b border-slate-200 phong-toi:border-phong-vien px-4 py-2 text-right text-sm">
        <a href={ketQua.url} target="_blank" rel="noopener noreferrer"
          className="font-semibold text-blue-900 phong-toi:text-phong-xanh underline">Mở bản PDF trong tab mới</a>
      </div>
      <iframe title={`Xem trước ${tenTep}`} src={`${ketQua.url}#view=FitH`}
        className="block h-[min(78vh,56rem)] min-h-96 w-full border-0" />
    </>
  )
}
