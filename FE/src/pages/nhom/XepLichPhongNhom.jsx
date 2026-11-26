import { useEffect, useState } from 'react'
import { capNhatLichHop, layPhongHopDeXuat } from '../../api/cuocHopApi.js'

const LOP_TRUONG = 'mt-1 block w-full rounded-lg border border-slate-300 p-2'

export default function XepLichPhongNhom({ hoSo, token, onDaLuu }) {
  const [mo, setMo] = useState(false)
  const [phong, setPhong] = useState(null)
  const [loi, setLoi] = useState('')
  const [dangLuu, setDangLuu] = useState(false)
  const [thongBao, setThongBao] = useState('')
  const cuocHopId = hoSo.cuocHopId

  useEffect(() => {
    if (!mo) return
    const controller = new AbortController()
    layPhongHopDeXuat(cuocHopId, token, null, controller.signal)
      .then((danhSach) => {
        if (!controller.signal.aborted) setPhong(danhSach.filter((p) => p.trangThai === 'SAN_SANG'))
      })
      .catch((error) => {
        if (!controller.signal.aborted) setLoi(error.message)
      })
    return () => controller.abort()
  }, [cuocHopId, token, mo])

  async function luu(event) {
    event.preventDefault()
    if (dangLuu) return
    const form = new FormData(event.currentTarget)
    setDangLuu(true)
    setLoi('')
    try {
      await capNhatLichHop(hoSo.lichHopId, {
        phongHopId: Number(form.get('phongHopId')),
        thoiGianBatDau: form.get('batDau'),
        thoiGianKetThuc: form.get('ketThuc'),
        lyDoThayDoi: form.get('lyDo').trim(),
      }, token)
      setMo(false)
      setThongBao('Đã cập nhật lịch phòng trong hồ sơ đang rà soát.')
      onDaLuu()
    } catch (error) {
      setLoi(error.message)
    } finally {
      setDangLuu(false)
    }
  }

  return <section className="space-y-3 rounded-xl border border-slate-200 bg-white p-3 sm:p-4">
    <button type="button" className="font-semibold text-emerald-800 underline" aria-expanded={mo}
      onClick={() => { setMo(!mo); setLoi(''); setThongBao('') }} disabled={dangLuu}>Xếp lịch phòng họp</button>
    {thongBao && <p role="status" className="text-sm text-emerald-800">{thongBao}</p>}
    {loi && <p role="alert" className="text-sm text-red-800">{loi}</p>}
    {mo && phong === null && !loi && <p role="status" className="text-sm">Đang tải phòng họp…</p>}
    {mo && phong && <form onSubmit={luu} className="grid gap-3 text-sm sm:grid-cols-2">
      <label>Phòng họp
        <select name="phongHopId" className={LOP_TRUONG} required
          defaultValue={phong.find((p) => p.tenPhongHop === hoSo.tenPhongHop && p.viTri === hoSo.viTriPhongHop)?.phongHopId ?? ''}>
          <option value="">Chọn phòng</option>
          {phong.map((p) => <option key={p.phongHopId} value={p.phongHopId}>{p.tenPhongHop} · {p.sucChua} chỗ</option>)}
        </select>
      </label>
      <label>Lý do xếp lại phòng hoặc giờ họp
        <input name="lyDo" className={LOP_TRUONG} maxLength={500} required />
      </label>
      <label>Bắt đầu
        <input name="batDau" type="datetime-local" className={LOP_TRUONG}
          defaultValue={hoSo.thoiGianBatDau?.slice(0, 16)} required />
      </label>
      <label>Kết thúc
        <input name="ketThuc" type="datetime-local" className={LOP_TRUONG}
          defaultValue={hoSo.thoiGianKetThuc?.slice(0, 16)} required />
      </label>
      <button type="submit" disabled={dangLuu || !phong.length}
        className="w-fit rounded-lg bg-emerald-800 px-4 py-2 font-semibold text-white disabled:opacity-60">
        {dangLuu ? 'Đang lưu…' : 'Lưu lịch phòng'}
      </button>
    </form>}
  </section>
}
