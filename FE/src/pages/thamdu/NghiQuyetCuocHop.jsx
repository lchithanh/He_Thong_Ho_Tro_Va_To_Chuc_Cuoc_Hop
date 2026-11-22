import { useEffect, useState } from 'react'
import TepHoSo from '../../components/TepHoSo.jsx'
import { goiNghiQuyet } from '../../api/nghiepVuCuocHopApi.js'
import { useDongBoDuLieu } from '../../services/useDongBoDuLieu.js'

const TRANG_THAI = {
  NHAP: 'Bản nháp', DANG_BIEU_QUYET: 'Đã lập phiên biểu quyết',
  THONG_QUA: 'Thông qua', KHONG_THONG_QUA: 'Không thông qua',
  CHUA_DAT_DA_SO: 'Chưa đạt đa số', YEU_CAU_CHINH_SUA: 'Yêu cầu chỉnh sửa',
}
const TRUONG = 'mt-1 w-full rounded-lg border border-slate-300 px-3 py-2'
const NUT = 'rounded-lg border border-slate-300 px-3 py-2 font-semibold disabled:opacity-60'

function CayNghiQuyet({ danhSach, chaId = null, children }) {
  return (
    <ul className="space-y-2">
      {danhSach.filter((muc) => muc.nghiQuyetChaId === chaId || (chaId === null
        && !danhSach.some((cha) => cha.nghiQuyetId === muc.nghiQuyetChaId))).map((muc) => (
        <li key={muc.nghiQuyetId} className="space-y-2 rounded-lg border border-slate-200 p-3">
          <div className="flex flex-wrap justify-between gap-2">
            <h3 className="font-semibold">{muc.tieuDe}</h3>
            <span>{TRANG_THAI[muc.trangThai]}</span>
          </div>
          <p className="whitespace-pre-wrap break-words">{muc.noiDung}</p>
          {muc.lyDo && <p className="text-amber-950">Lý do chỉnh sửa · {muc.lyDo}</p>}
          {children(muc)}
          <CayNghiQuyet danhSach={danhSach} chaId={muc.nghiQuyetId}>{children}</CayNghiQuyet>
        </li>
      ))}
    </ul>
  )
}

export default function NghiQuyetCuocHop({ cuocHopId, token, duocQuanLy = false }) {
  const phien = useDongBoDuLieu()
  const [danhSach, setDanhSach] = useState(null)
  const [lanTai, setLanTai] = useState(0)
  const [mucSua, setMucSua] = useState(null)
  const [dangGui, setDangGui] = useState(false)
  const [loi, setLoi] = useState('')

  useEffect(() => {
    const controller = new AbortController()
    goiNghiQuyet(cuocHopId, token, '', { signal: controller.signal }).then(setDanhSach)
      .catch((error) => { if (!controller.signal.aborted) setLoi(error.message) })
    return () => controller.abort()
  }, [cuocHopId, token, phien, lanTai])

  async function gui(duongDan, duLieu, method = 'POST') {
    setDangGui(true)
    setLoi('')
    try {
      await goiNghiQuyet(cuocHopId, token, duongDan, duLieu instanceof FormData ? { method, body: duLieu } : { method, duLieu })
      setLanTai((lan) => lan + 1)
      setMucSua(null)
    } catch (error) {
      setLoi(error.message)
    } finally {
      setDangGui(false)
    }
  }

  function luu(event) {
    event.preventDefault()
    const duLieu = new FormData(event.currentTarget)
    if (mucSua.yeuCauSua) {
      gui(`/${mucSua.nghiQuyetId}/yeu-cau-sua`, { lyDo: duLieu.get('lyDo') })
      return
    }
    if (mucSua.nghiQuyetChaId != null) duLieu.set('nghiQuyetChaId', String(mucSua.nghiQuyetChaId))
    gui(mucSua.nghiQuyetId ? `/${mucSua.nghiQuyetId}` : '', duLieu, mucSua.nghiQuyetId ? 'PUT' : 'POST')
  }

  return (
    <section className="space-y-3 text-sm">
      <h2 className="text-lg font-bold">Nghị quyết</h2>
      {loi && <p role="alert" className="text-red-800">{loi}</p>}
      {!danhSach && !loi && <p role="status">Đang tải nghị quyết…</p>}
      {danhSach?.length === 0 && <p>Chưa có nghị quyết.</p>}
      {duocQuanLy && <button type="button" className={NUT} onClick={() => setMucSua({})}>Thêm nghị quyết cấp gốc</button>}
      {mucSua && (
        <form key={JSON.stringify(mucSua)} className="space-y-2 rounded-lg border border-slate-200 p-3" onSubmit={luu}>
          {mucSua.yeuCauSua ? (
            <label className="block">Lý do chỉnh sửa<textarea className={TRUONG} name="lyDo" required maxLength={500} /></label>
          ) : (
            <>
              <label className="block">Tiêu đề<input className={TRUONG} name="tieuDe" required maxLength={255} defaultValue={mucSua.tieuDe} /></label>
              <label className="block">Tệp nghị quyết (PDF)<input className={TRUONG} name="tep" type="file" required accept=".pdf,application/pdf" /></label>
            </>
          )}
          <div className="flex gap-2">
            <button className={NUT} disabled={dangGui}>{mucSua.yeuCauSua ? 'Gửi yêu cầu chỉnh sửa' : 'Lưu nghị quyết'}</button>
            <button className={NUT} disabled={dangGui} type="button" onClick={() => setMucSua(null)}>Hủy</button>
          </div>
        </form>
      )}
      {danhSach && <CayNghiQuyet danhSach={danhSach}>{(muc) => (
        <>
        {muc.taiLieuId && <TepHoSo key={muc.taiLieuId} token={token} ten={muc.tieuDe + '.pdf'}
          duongDan={`/api/cuoc-hop/${cuocHopId}/nghi-quyet/${muc.nghiQuyetId}/tep`} />}
        {muc.ngayCongBo && <p>Đã công bố</p>}
        {duocQuanLy && <div className="flex flex-wrap gap-2">
          {muc.trangThai === 'NHAP' && muc.taiLieuId && !muc.ngayCongBo
            && <button className={NUT} type="button" disabled={dangGui}
              onClick={() => gui(`/${muc.nghiQuyetId}/cong-bo`)}>Công bố cho thành viên</button>}
          {muc.cap < 4 && <button type="button" className={NUT}
            onClick={() => setMucSua({ nghiQuyetChaId: muc.nghiQuyetId })}>Thêm nghị quyết cấp dưới</button>}
          {['NHAP', 'YEU_CAU_CHINH_SUA'].includes(muc.trangThai) && <button className={NUT} type="button"
            onClick={() => setMucSua(muc)}>Sửa nội dung</button>}
          {['KHONG_THONG_QUA', 'CHUA_DAT_DA_SO'].includes(muc.trangThai) && <button className={NUT} type="button"
            onClick={() => setMucSua({ ...muc, yeuCauSua: true })}>Yêu cầu chỉnh sửa</button>}
        </div>}
        </>
      )}</CayNghiQuyet>}
      {duocQuanLy && <p>Sau khi công bố, chủ trì chọn nghị quyết trong mục Biểu quyết để mở phiên bỏ phiếu.</p>}
    </section>
  )
}
