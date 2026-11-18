import { useEffect, useRef, useState } from 'react'
import { useDongBoDuLieu } from '../../services/useDongBoDuLieu.js'
import {
  layChiTietLichCalendar,
  layDanhSachDeXuat,
  layDanhSachLichHop,
  layTaiLieuLanTrinh,
  taiXuongTaiLieuLanTrinh,
  layPhongHopDeXuat,
  taoLichHop,
  xacNhanLichHop,
  xuLyLichKhongDuyet,
} from '../../api/cuocHopApi.js'

import LichCuocHop from '../../lichhop/LichCuocHop.jsx'
import { maNgay } from '../../lichhop/lichHopHienThi.js'
import ThanhVienCuocHopThuKy from './ThanhVienCuocHopThuKy.jsx'

const LOP_TRUONG = 'mt-1 w-full rounded-lg border border-slate-300 px-3 py-2'

const TRANG_THAI_PHONG = {
  SAN_SANG: 'Sẵn sàng',
  BAO_TRI: 'Bảo trì',
  NGUNG_SU_DUNG: 'Ngừng sử dụng',
  BAN: 'Bận',
}

function FormDeXuatLich({ cuocHopId, loaiHinh, token, ngayChon, khungGio, onDaTao }) {
  const phienDongBo = useDongBoDuLieu()
  const [loiLuu, setLoiLuu] = useState('')
  const [thongBao, setThongBao] = useState('')
  const [dangLuu, setDangLuu] = useState(false)

  const formLich = useRef(null)
  const hopTrucTiep = loaiHinh === 'TRUC_TIEP'
  const [gioBatDau, setGioBatDau] = useState(khungGio?.batDau ?? '')
  const [gioKetThuc, setGioKetThuc] = useState(khungGio?.ketThuc ?? '')
  const [danhSachPhong, setDanhSachPhong] = useState(null)
  const [loiPhong, setLoiPhong] = useState(null)
  const [phongHopId, setPhongHopId] = useState('')
  const coKhoangGio = Boolean(gioBatDau && gioKetThuc && gioKetThuc > gioBatDau)
  const mocBatDau = coKhoangGio ? gioBatDau : ''
  const mocKetThuc = coKhoangGio ? gioKetThuc : ''
  const khoaGio = mocBatDau + '/' + mocKetThuc
  const phongHienTai = danhSachPhong?.khoaGio === khoaGio ? danhSachPhong.duLieu : null
  const phongDangChon = phongHienTai?.find((phong) => String(phong.phongHopId) === phongHopId)
  const phongHopLe = phongDangChon?.trangThai === 'SAN_SANG' && phongDangChon.trungLich === false
  const loiPhongHienTai = loiPhong?.khoaGio === khoaGio ? loiPhong.message : ''

  useEffect(() => {
    if (!hopTrucTiep) return
    const controller = new AbortController()
    const khoangGio = mocBatDau ? { batDau: mocBatDau, ketThuc: mocKetThuc } : null
    layPhongHopDeXuat(cuocHopId, token, khoangGio, controller.signal)
      .then((duLieu) => {
        if (controller.signal.aborted) return
        setDanhSachPhong({ khoaGio, duLieu })
        setLoiPhong(null)
      })
      .catch((loi) => {
        if (!controller.signal.aborted) setLoiPhong({ khoaGio, message: loi.message })
      })
    return () => controller.abort()
  }, [cuocHopId, token, hopTrucTiep, mocBatDau, mocKetThuc, khoaGio, phienDongBo])

  function dienNgayVaoForm() {
    const batDau = formLich.current.elements.namedItem('thoiGianBatDau')
    const ketThuc = formLich.current.elements.namedItem('thoiGianKetThuc')
    const ngay = maNgay(ngayChon)
    setGioBatDau(ngay + 'T' + (batDau.value.slice(11) || '08:00'))
    setGioKetThuc(ngay + 'T' + (ketThuc.value.slice(11) || '09:00'))
    batDau.focus()
  }

  async function xuLyTaoLich(event) {
    event.preventDefault()
    if (dangLuu) return

    const form = event.currentTarget
    const duLieuForm = new FormData(form)
    const thoiGianBatDau = duLieuForm.get('thoiGianBatDau')
    const thoiGianKetThuc = duLieuForm.get('thoiGianKetThuc')

    setLoiLuu('')
    setThongBao('')

    if (thoiGianKetThuc <= thoiGianBatDau) {
      setLoiLuu('Giờ kết thúc phải sau giờ bắt đầu.')
      return
    }

    if (hopTrucTiep && (!phongHopLe || loiPhongHienTai)) {
      setLoiLuu('Chọn phòng sẵn sàng và còn trống trong khung giờ họp.')
      return
    }

    setDangLuu(true)

    try {
      const lichMoi = await taoLichHop({
        cuocHopId,
        phongHopId: hopTrucTiep ? Number(duLieuForm.get('phongHopId')) : null,
        thoiGianBatDau,
        thoiGianKetThuc,
      }, token)

      onDaTao(lichMoi)
      form.reset()
      setGioBatDau('')
      setGioKetThuc('')
      setPhongHopId('')
      setThongBao('Đã tạo lịch đề xuất.')
    } catch (loi) {
      setLoiLuu(loi.message)
    } finally {
      setDangLuu(false)
    }
  }

  return (
      <form ref={formLich} onSubmit={xuLyTaoLich} className="space-y-4 rounded-xl border border-slate-200 bg-white p-4">
        <button type="button" onClick={dienNgayVaoForm} disabled={dangLuu}
          className="text-sm font-semibold text-emerald-800">Dùng ngày đang chọn trên lịch</button>
        <div className="grid gap-3 sm:grid-cols-2">
          <label className="block text-sm font-medium">
            Bắt đầu
            <input name="thoiGianBatDau" type="datetime-local" value={gioBatDau} onChange={(event) => setGioBatDau(event.target.value)} required className={LOP_TRUONG} />
          </label>

          <label className="block text-sm font-medium">
            Kết thúc
            <input name="thoiGianKetThuc" type="datetime-local" value={gioKetThuc} onChange={(event) => setGioKetThuc(event.target.value)} required className={LOP_TRUONG} />
          </label>
        </div>

        {hopTrucTiep && (
          <div className="space-y-3">
            <label className="block text-sm font-medium">
              Phòng họp
              <select name="phongHopId" value={phongHopId} required className={LOP_TRUONG}
                onChange={(event) => setPhongHopId(event.target.value)}
                disabled={!phongHienTai?.length || dangLuu || Boolean(loiPhongHienTai)}>
                <option value="">Chọn phòng họp</option>
                {phongHienTai?.map((phong) => (
                  <option key={phong.phongHopId} value={phong.phongHopId}>
                    {phong.tenPhongHop} · {TRANG_THAI_PHONG[phong.trangThai] ?? phong.trangThai}
                    {phong.trungLich === true ? ' · Trùng lịch' : phong.trungLich === false ? ' · Còn trống' : ''}
                  </option>
                ))}
              </select>
            </label>
            {loiPhongHienTai ? <p role="alert" className="text-sm text-red-800">{loiPhongHienTai}</p>
              : !phongHienTai ? <p role="status" className="text-sm text-slate-600">Đang tải thông tin phòng…</p>
                : phongHienTai.length === 0 && <p className="text-sm text-slate-600">Chưa có phòng họp.</p>}
            {phongDangChon && (
              <dl className="grid gap-3 rounded-xl border border-slate-200 bg-slate-50 p-3 text-sm sm:grid-cols-2">
                {[
                  ['Tên phòng', phongDangChon.tenPhongHop],
                  ['Vị trí', phongDangChon.viTri],
                  ['Sức chứa', phongDangChon.sucChua == null ? null : `${phongDangChon.sucChua} người`],
                  ['Trạng thái phòng', TRANG_THAI_PHONG[phongDangChon.trangThai] ?? phongDangChon.trangThai],
                  ['Khung giờ đã chọn', phongDangChon.trungLich == null ? 'Chọn giờ bắt đầu và kết thúc để kiểm tra'
                    : phongDangChon.trungLich ? 'Trùng lịch đã xác nhận' : 'Còn trống'],
                  ['Mô tả', phongDangChon.moTa],
                ].map(([nhan, giaTri]) => (
                  <div key={nhan}>
                    <dt className="font-semibold">{nhan}</dt>
                    <dd className="mt-0.5 break-words text-slate-700">{giaTri || 'Chưa có thông tin'}</dd>
                  </div>
                ))}
              </dl>
            )}
          </div>
        )}

        {loiLuu && <p role="alert" className="text-sm text-red-800">{loiLuu}</p>}
        {thongBao && <p role="status" className="text-sm text-emerald-800">{thongBao}</p>}

        <button
          type="submit"
          disabled={dangLuu || (hopTrucTiep && (!phongHopLe || Boolean(loiPhongHienTai)))}
          className="rounded-lg bg-emerald-800 px-4 py-2 text-sm font-semibold text-white disabled:opacity-60"
        >
          {dangLuu ? 'Đang lưu…' : 'Đề xuất lịch họp'}
        </button>
      </form>
  )
}

export function LichHopCaNhan({ token, nguoiDung, cuocHopLapLich }) {
  const phienDongBo = useDongBoDuLieu()
  const [danhSach, setDanhSach] = useState(null)
  const [loiTai, setLoiTai] = useState('')
  const [ngayChon, setNgayChon] = useState(() => new Date())

  useEffect(() => {
    const controller = new AbortController()
    layDanhSachLichHop(token, controller.signal)
      .then((lich) => { if (!controller.signal.aborted) setDanhSach(lich) })
      .catch((loi) => { if (!controller.signal.aborted) setLoiTai(loi.message) })
    return () => controller.abort()
  }, [token, phienDongBo])

  const [khungGio, setKhungGio] = useState(null)
  const [moForm, setMoForm] = useState(Boolean(cuocHopLapLich))
  const [deXuat, setDeXuat] = useState(null)
  const [loiDeXuat, setLoiDeXuat] = useState('')
  const [cuocHopId, setCuocHopId] = useState(cuocHopLapLich ?? '')
  const laThuKy = ['THU_KY', 'CAN_BO'].includes(nguoiDung?.maVaiTro)
  const vungDeXuat = useRef(null)

  useEffect(() => {
    if (moForm) vungDeXuat.current?.focus()
  }, [moForm, khungGio])

  useEffect(() => {
    if (!laThuKy || !moForm) return
    const controller = new AbortController()
    layDanhSachDeXuat(token, controller.signal)
      .then((duLieu) => {
        if (!controller.signal.aborted) setDeXuat(duLieu.filter((muc) =>
          (muc.duocLapKeHoach && muc.buocXuLyHienTai === 'LAP_KE_HOACH')
          || (muc.duocToChuc && muc.buocXuLyHienTai === 'THU_KY_TO_CHUC'),
        ))
      })
      .catch((loi) => { if (!controller.signal.aborted) setLoiDeXuat(loi.message) })
    return () => controller.abort()
  }, [token, laThuKy, moForm, phienDongBo])

  const cuocHop = deXuat?.find((muc) => String(muc.cuocHopId) === String(cuocHopId))

  function moDeXuat(ngay, gio) {
    const batDau = new Date(ngay.getFullYear(), ngay.getMonth(), ngay.getDate(), gio)
    const ketThuc = new Date(ngay.getFullYear(), ngay.getMonth(), ngay.getDate(), gio + 1)
    const dinhDang = (moc) => maNgay(moc) + 'T' + String(moc.getHours()).padStart(2, '0') + ':00'
    setKhungGio({ batDau: dinhDang(batDau), ketThuc: dinhDang(ketThuc) })
    setMoForm(true)
  }

  function capNhatLich(lichMoi) {
    setDanhSach((hienTai) => [
      ...(hienTai ?? []).filter((lich) => lich.lichHopId !== lichMoi.lichHopId), lichMoi,
    ])
  }

  return (
    <div className="space-y-5">
      <LichCuocHop
        danhSach={danhSach}
        loiTai={loiTai}
        ngayChon={ngayChon}
        onChonNgay={setNgayChon}
        onMoDeXuat={laThuKy ? moDeXuat : undefined}
        renderChiTiet={(lich) => <ChiTietLich key={lich.lichHopId} lich={lich} token={token} onDaXuLy={(lichMoi) => {
          capNhatLich(lichMoi)
          if (lichMoi.trangThai === 'DA_XAC_NHAN' && lichMoi.lichThayTheId != null) {
            layDanhSachLichHop(token).then(setDanhSach).catch((loi) => setLoiTai(loi.message))
          }
        }} />}
      />
      {laThuKy && moForm && (
        <section className="space-y-4 rounded-xl border border-slate-200 bg-white p-4">
          <div className="flex justify-between gap-2">
            <h3 ref={vungDeXuat} tabIndex={-1} className="scroll-mt-24 font-semibold">Đề xuất lịch họp</h3>
            <button type="button" onClick={() => { setMoForm(false); setKhungGio(null) }} className="text-sm font-semibold">Đóng</button>
          </div>
          {loiDeXuat && <p role="alert" className="text-sm text-red-800">{loiDeXuat}</p>}
          {!deXuat && !loiDeXuat && <p role="status" className="text-sm">Đang tải cuộc họp…</p>}
          {deXuat?.length === 0 && <p className="text-sm">Chưa có cuộc họp ở bước tổ chức.</p>}
          {!!deXuat?.length && (
            <label className="block text-sm font-semibold">Cuộc họp
              <select className={LOP_TRUONG} value={cuocHop ? cuocHopId : ''} onChange={(event) => setCuocHopId(event.target.value)}>
                <option value="">Chọn cuộc họp</option>
                {deXuat.map((muc) => <option key={muc.cuocHopId} value={muc.cuocHopId}>{muc.tieuDe}</option>)}
              </select>
            </label>
          )}
          {cuocHop && <FormDeXuatLich key={cuocHop.cuocHopId + '/' + (khungGio?.batDau ?? '')} {...cuocHop} token={token} ngayChon={ngayChon} khungGio={khungGio}
            onDaTao={(lich) => { capNhatLich(lich); setNgayChon(new Date(lich.thoiGianBatDau)) }} />}
        </section>
      )}
    </div>
  )
}

function ChiTietLich({ lich, token, onDaXuLy }) {
  const phienDongBo = useDongBoDuLieu()
  const lichHopId = lich.lichHopId
  const [chiTiet, setChiTiet] = useState(null)
  const [tep, setTep] = useState(null)
  const [loiTep, setLoiTep] = useState('')
  const [loi, setLoi] = useState('')
  const [dangLuu, setDangLuu] = useState(false)
  const [lyDo, setLyDo] = useState('')
  const [thongBao, setThongBao] = useState('')
  const yeuCauId = chiTiet?.yeuCauPheDuyetId

  useEffect(() => {
    const controller = new AbortController()
    layChiTietLichCalendar(lichHopId, token, controller.signal)
      .then((duLieu) => { if (!controller.signal.aborted) setChiTiet(duLieu) })
      .catch((loi) => { if (!controller.signal.aborted) setLoi(loi.message) })
    return () => controller.abort()
  }, [lichHopId, token, phienDongBo])

  useEffect(() => {
    if (yeuCauId == null) return
    const controller = new AbortController()
    layTaiLieuLanTrinh(yeuCauId, token, controller.signal)
      .then((duLieu) => { if (!controller.signal.aborted) setTep(duLieu) })
      .catch((loi) => { if (!controller.signal.aborted) setLoiTep(loi.message) })
    return () => controller.abort()
  }, [yeuCauId, token, phienDongBo])

  async function xuLy(thaoTac) {
    if (dangLuu) return
    setLoi('')
    if (thaoTac !== 'xac-nhan' && !lyDo.trim()) {
      setLoi('Nhập lý do trước khi từ chối hoặc trả lại lịch.')
      return
    }
    setDangLuu(true)
    try {
      const ketQua = thaoTac === 'xac-nhan'
        ? await xacNhanLichHop(lichHopId, token)
        : await xuLyLichKhongDuyet(lichHopId, thaoTac, lyDo.trim(), token)
      onDaXuLy(ketQua)
      setThongBao({ 'xac-nhan': 'Đã xác nhận lịch.', 'tu-choi': 'Đã từ chối lịch.', 'tra-lai': 'Đã trả lại để thư ký đề xuất lịch mới.' }[thaoTac])
    } catch (loi) {
      setLoi(loi.message)
    } finally {
      setDangLuu(false)
    }
  }

  return (
    <section className="space-y-4 rounded-xl border border-slate-200 p-4 text-sm">
      <h4 className="font-bold">Chi tiết cuộc họp</h4>
      {loi && <p role="alert" className="text-red-800">{loi}</p>}
      {thongBao && <p role="status" className="text-emerald-800">{thongBao}</p>}
      {!chiTiet && !loi && <p role="status">Đang tải chi tiết…</p>}
      {chiTiet && (
        <>
          <dl className="grid gap-3 sm:grid-cols-2">
            {[
              ['Phòng họp', chiTiet.phong?.tenPhongHop || 'Trực tuyến'],
              ['Vị trí', chiTiet.phong?.viTri],
              ['Sức chứa', chiTiet.phong?.sucChua],
              ['Trạng thái phòng', TRANG_THAI_PHONG[chiTiet.phong?.trangThai] ?? chiTiet.phong?.trangThai],
              ['Mô tả phòng', chiTiet.phong?.moTa],
              ['Mục tiêu', chiTiet.mucTieu],
              ['Nội dung kế hoạch', chiTiet.noiDungDuKien],
              ['Ghi chú trình', chiTiet.ghiChuTrinh],
              ['Lý do xử lý lịch', lich.lyDoThayDoi],
            ].filter(([, giaTri]) => giaTri != null && giaTri !== '').map(([nhan, giaTri]) => (
              <div key={nhan}><dt className="font-semibold">{nhan}</dt><dd className="whitespace-pre-wrap break-words">{giaTri}</dd></div>
            ))}
          </dl>
          {yeuCauId != null && (
            <div className="space-y-2 border-t border-slate-100 pt-3">
              <p className="font-semibold">Tài liệu đã trình</p>
              {loiTep ? <p role="alert" className="text-red-800">{loiTep}</p> : tep === null ? <p role="status">Đang tải tài liệu…</p> : !tep.length && <p>Không có tệp gửi kèm.</p>}
              <ul className="space-y-2">{tep?.map((muc) => (
                <li key={muc.taiLieuId}>
                  <button type="button" className="break-all text-left font-semibold text-emerald-800 underline"
                    onClick={() => taiXuongTaiLieuLanTrinh(yeuCauId, muc, token).catch((loi) => setLoiTep(loi.message))}>
                    Tải {muc.tenFileGoc || muc.tenTaiLieu}
                  </button>
                </li>
              ))}</ul>
            </div>
          )}
          {chiTiet.duocXacNhan && lich.trangThai === 'DE_XUAT' && (
            <div className="space-y-3 border-t border-slate-100 pt-3">
              <label className="block font-semibold">Lý do từ chối hoặc trả lại
                <textarea className={LOP_TRUONG} rows={2} value={lyDo} onChange={(event) => setLyDo(event.target.value)} />
              </label>
              <div className="flex flex-wrap gap-2">
                {[['xac-nhan', 'Xác nhận lịch'], ['tu-choi', 'Từ chối lịch'], ['tra-lai', 'Trả lại để chỉnh lịch']].map(([ma, ten]) => (
                  <button key={ma} type="button" disabled={dangLuu} onClick={() => xuLy(ma)}
                    className={`rounded-lg border px-3 py-2 font-semibold disabled:opacity-60 ${ma === 'xac-nhan' ? 'bg-emerald-800 text-white' : 'border-slate-200'}`}>{ten}</button>
                ))}
              </div>
            </div>
          )}
          {chiTiet.duocXemThanhVien && <ThanhVienCuocHopThuKy cuocHopId={lich.cuocHopId} token={token} choPhepMoi={false} />}
        </>
      )}
    </section>
  )
}
