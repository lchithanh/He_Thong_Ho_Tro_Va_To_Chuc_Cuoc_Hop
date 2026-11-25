import { useDongBoDuLieu } from '../../services/useDongBoDuLieu.js'
import { useEffect, useState } from 'react'
import { layGhiChuCuocHop, luuGhiChuCuocHop, xoaGhiChuCuocHop } from '../../api/ghiChuCuocHopApi.js'

const LOP_TRUONG = 'mt-1 w-full rounded-lg border border-slate-300 px-3 py-2 phong-toi:truong-phong-hop'
const LOP_NUT = 'rounded-lg border border-slate-300 phong-toi:border-phong-vien px-3 py-2 text-sm font-semibold disabled:opacity-60'

export default function GhiChuCuocHop({ cuocHopId, token, kichHoat }) {
  const phienDongBo = useDongBoDuLieu()
  const [danhSach, setDanhSach] = useState(null)
  const [dangSua, setDangSua] = useState(null)
  const [dangXuLy, setDangXuLy] = useState(false)
  const [loiTai, setLoiTai] = useState('')
  const [loiLuu, setLoiLuu] = useState('')
  const [thongBao, setThongBao] = useState('')
  const [lanTai, setLanTai] = useState(0)
  const [lanDatLai, setLanDatLai] = useState(0)

  useEffect(() => {
    if (!kichHoat || dangXuLy) return undefined
    const controller = new AbortController()
    layGhiChuCuocHop(cuocHopId, token, controller.signal)
      .then((duLieu) => {
        if (controller.signal.aborted) return
        setDanhSach(duLieu)
        setLoiTai('')
      })
      .catch((loi) => {
        if (!controller.signal.aborted) setLoiTai(loi.message)
      })
    return () => controller.abort()
  }, [cuocHopId, token, kichHoat, lanTai, dangXuLy, phienDongBo])

  function chonGhiChu(muc) {
    setDangSua(muc)
    setLanDatLai((lan) => lan + 1)
    setLoiLuu('')
    setThongBao('')
  }

  async function luu(event) {
    event.preventDefault()
    const form = new FormData(event.currentTarget)
    const duLieu = Object.fromEntries(form)
    if (!duLieu.chuDe.trim() || (!duLieu.yKienChinh.trim() && !duLieu.noiDungChot.trim())) {
      setLoiLuu('Nhập chủ đề và ít nhất một nội dung ghi chú.')
      return
    }
    setDangXuLy(true)
    setLoiLuu('')
    setThongBao('')
    try {
      setDanhSach(await luuGhiChuCuocHop(cuocHopId, dangSua?.ghiChuId,
        { ...duLieu, phienBan: dangSua?.phienBan }, token))
      chonGhiChu(null)
      setThongBao('Đã lưu ghi chú.')
    } catch (loi) {
      setLoiLuu(loi.message)
    } finally {
      setDangXuLy(false)
    }
  }

  async function xoa(muc) {
    if (!window.confirm(`Xóa ghi chú “${muc.chuDe}”?`)) return
    setDangXuLy(true)
    setLoiLuu('')
    setThongBao('')
    try {
      setDanhSach(await xoaGhiChuCuocHop(cuocHopId, muc, token))
      if (dangSua?.ghiChuId === muc.ghiChuId) chonGhiChu(null)
      setThongBao('Đã xóa ghi chú.')
    } catch (loi) {
      setLoiLuu(loi.message)
    } finally {
      setDangXuLy(false)
    }
  }

  return (
    <div className="grid gap-5 xl:grid-cols-2">
      <section className="space-y-3">
        <div className="flex flex-wrap items-center justify-between gap-2">
          <h3 className="font-semibold">Ghi chú đã lưu</h3>
          <button type="button" disabled={dangXuLy} className={LOP_NUT}
            onClick={() => setLanTai((lan) => lan + 1)}>Tải lại</button>
        </div>
        {loiTai && <p role="alert" className="text-sm text-rose-800 phong-toi:text-phong-do">{loiTai}</p>}
        {!loiTai && danhSach === null && <p role="status" className="text-sm">Đang tải ghi chú…</p>}
        {danhSach?.length === 0 && <p className="text-sm text-slate-600 phong-toi:text-phong-chu-phu">Chưa có ghi chú nào.</p>}
        {danhSach?.map((muc) => (
          <article key={muc.ghiChuId} className="space-y-2 rounded-xl border border-slate-200 phong-toi:border-phong-vien p-4">
            <h4 className="wrap-anywhere font-semibold">{muc.chuDe}</h4>
            {muc.nguoiPhatBieu && <p className="wrap-anywhere text-sm">Người phát biểu · {muc.nguoiPhatBieu}</p>}
            {muc.yKienChinh && <p className="whitespace-pre-wrap wrap-anywhere text-sm">{muc.yKienChinh}</p>}
            {muc.noiDungChot && <p className="whitespace-pre-wrap wrap-anywhere text-sm"><strong>Chủ trì chốt</strong> · {muc.noiDungChot}</p>}
            <div className="flex gap-2">
              <button type="button" className={LOP_NUT} disabled={dangXuLy} onClick={() => chonGhiChu(muc)}>Sửa</button>
              <button type="button" className={LOP_NUT + ' text-rose-800 phong-toi:text-phong-do'} disabled={dangXuLy}
                onClick={() => xoa(muc)}>Xóa</button>
            </div>
          </article>
        ))}
      </section>

      <form key={lanDatLai} onSubmit={luu} className="space-y-3">
        <h3 className="font-semibold">{dangSua ? 'Sửa ghi chú' : 'Thêm ghi chú'}</h3>
        <fieldset disabled={dangXuLy || danhSach === null} className="min-w-0 space-y-3">
          <label className="block text-sm font-medium">
            Chủ đề
            <input name="chuDe" defaultValue={dangSua?.chuDe ?? ''} maxLength={255} required className={LOP_TRUONG} />
          </label>
          <label className="block text-sm font-medium">
            Người phát biểu (nếu có)
            <input name="nguoiPhatBieu" defaultValue={dangSua?.nguoiPhatBieu ?? ''} maxLength={190} className={LOP_TRUONG} />
          </label>
          <label className="block text-sm font-medium">
            Ý kiến chính
            <textarea name="yKienChinh" defaultValue={dangSua?.yKienChinh ?? ''} maxLength={4000} rows={4} className={LOP_TRUONG} />
          </label>
          <label className="block text-sm font-medium">
            Nội dung chủ trì chốt
            <textarea name="noiDungChot" defaultValue={dangSua?.noiDungChot ?? ''} maxLength={4000} rows={3} className={LOP_TRUONG} />
          </label>
          <div className="flex gap-2">
            <button type="submit" className="rounded-lg bg-blue-800 phong-toi:bg-phong-nut px-4 py-2 text-sm font-semibold text-white disabled:opacity-60">
              {dangXuLy ? 'Đang xử lý…' : 'Lưu ghi chú'}
            </button>
            {dangSua && <button type="button" className={LOP_NUT} onClick={() => chonGhiChu(null)}>Hủy chỉnh sửa</button>}
          </div>
        </fieldset>
        {loiLuu && <p role="alert" className="text-sm text-rose-800 phong-toi:text-phong-do">{loiLuu}</p>}
        {thongBao && <p role="status" className="text-sm text-emerald-800 phong-toi:text-phong-luc">{thongBao}</p>}
      </form>
    </div>
  )
}
