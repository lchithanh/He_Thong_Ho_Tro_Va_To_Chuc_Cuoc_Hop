import { useEffect, useMemo, useRef, useState } from 'react'
import { NoiDungXemTruoc } from './XemTruocTepKeHoach.jsx'
import { layTepGiayMoi } from './taoGiayMoi.js'
import { dinhDangNgayGio } from '../../utils/thoiGianCuocHop.js'

const LOP_NHAP = 'mt-1 block w-full rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm font-normal'
export default function TepThuMoiFields({ layThongTin, thongTinCuocHop, disabled }) {
  const nhomTruong = useRef(null)
  const phien = useRef(0)
  const [cheDo, setCheDo] = useState('KHONG_KEM')
  const [trangThai, setTrangThai] = useState(null)
  const dangTao = trangThai?.dangTao
  const tepXem = trangThai?.tepXem
  const layTep = useMemo(() => async () => tepXem.tep, [tepXem])

  useEffect(() => {
    const form = nhomTruong.current?.form
    function boBanXem() {
      phien.current += 1
      setTrangThai(null)
    }
    function datLai() {
      setCheDo('KHONG_KEM')
      boBanXem()
    }
    form?.addEventListener('change', boBanXem)
    form?.addEventListener('input', boBanXem)
    form?.addEventListener('reset', datLai)
    return () => {
      phien.current += 1
      form?.removeEventListener('change', boBanXem)
      form?.removeEventListener('input', boBanXem)
      form?.removeEventListener('reset', datLai)
    }
  }, [])

  async function xemTruoc(event) {
    const duLieu = new FormData(event.currentTarget.form)
    const phienHienTai = ++phien.current
    setTrangThai({ dangTao: true })
    try {
      const tepXem = await layTepGiayMoi(duLieu, layThongTin(duLieu))
      if (phien.current === phienHienTai) setTrangThai({ tepXem })
    } catch (loi) {
      if (phien.current === phienHienTai) setTrangThai({ loi: loi.message })
    }
  }

  return (
    <fieldset ref={nhomTruong} disabled={disabled}
      className="space-y-3 rounded-xl border border-slate-200 bg-slate-50 p-3">
      <label className="block text-sm font-semibold">
        Giấy mời đính kèm
        <select name="tepGiayMoi" className={LOP_NHAP} value={cheDo} onChange={(event) => setCheDo(event.target.value)}>
          <option value="KHONG_KEM">Không đính kèm</option>
          <option value="CO_SAN">Chọn PDF hoặc Word có sẵn</option>
          <option value="PDF">Tạo giấy mời PDF</option>
          <option value="WORD">Tạo giấy mời Word</option>
        </select>
      </label>
      {cheDo === 'CO_SAN' && (
        <label className="block text-sm font-semibold">
          Tệp PDF hoặc Word (.docx), tối đa 2 MB
          <input name="tepGiayMoiCoSan" className={LOP_NHAP} type="file" accept=".pdf,.docx" required />
        </label>
      )}
      {thongTinCuocHop && (
        <dl className="grid gap-2 rounded-lg bg-white p-3 text-sm text-slate-900">
          <div><dt className="font-semibold">Tiêu đề cuộc họp đã duyệt</dt><dd className="break-words">{thongTinCuocHop.tieuDe}</dd></div>
          <div><dt className="font-semibold">Nội dung họp</dt><dd className="whitespace-pre-wrap break-words">{thongTinCuocHop.noiDung || 'Chưa ghi nội dung'}</dd></div>
          <div><dt className="font-semibold">Thời gian</dt><dd>{dinhDangNgayGio(thongTinCuocHop.thoiGianBatDau)} – {dinhDangNgayGio(thongTinCuocHop.thoiGianKetThuc)}</dd></div>
          <div><dt className="font-semibold">Địa điểm/Hình thức</dt><dd className="break-words">{thongTinCuocHop.diaDiem}</dd></div>
        </dl>
      )}
      {['PDF', 'WORD'].includes(cheDo) && (
        <p className="text-xs text-slate-600">Giấy mời điền theo thông tin đã duyệt và thông tin người nhận. Tệp tạo từ mẫu là bản chưa ký.</p>
      )}
      {cheDo !== 'KHONG_KEM' && (
        <button type="button" disabled={dangTao} onClick={xemTruoc}
          className="rounded-lg border border-blue-200 bg-white px-3 py-2 text-sm font-semibold text-blue-900 disabled:opacity-50">
          {dangTao ? 'Đang tạo bản xem trước…' : 'Xem trước giấy mời'}
        </button>
      )}
      {trangThai?.loi && <p role="alert" className="text-sm text-red-800">{trangThai.loi}</p>}
      {tepXem && (
        <section className="min-w-0 rounded-xl border border-slate-200 bg-white">
          <NoiDungXemTruoc tep={{ tenFileGoc: tepXem.tenTep, dinhDangFile: tepXem.dinhDang }}
            layTep={layTep} />
        </section>
      )}
    </fieldset>
  )
}
