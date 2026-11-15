import { useEffect, useRef, useState } from 'react'
import { LOP_KHOI, LOP_TRUONG, LOP_NUT_CHINH } from '../../components/lopBieuMau.js'
import { goiApiQuanTri } from '../../api/quanTriApi.js'

const TINH_TRANG_THIET_BI = {
  TOT: 'Tốt',
  HU_HONG: 'Hư hỏng',
  BAO_TRI: 'Bảo trì',
  NGUNG_SU_DUNG: 'Ngừng sử dụng',
}

export default function DanhMucQuanTri({ token, loai }) {
  const laPhongBan = loai === 'departments'
  const danhTu = laPhongBan ? 'phòng ban' : 'thiết bị'
  const [danhSach, setDanhSach] = useState(null)
  const [danhSachLienQuan, setDanhSachLienQuan] = useState([])
  const [loiTai, setLoiTai] = useState('')
  const [loi, setLoi] = useState('')
  const [thongBao, setThongBao] = useState('')
  const [mucChon, setMucChon] = useState(null)
  const [dangLuu, setDangLuu] = useState(false)
  const [tuKhoa, setTuKhoa] = useState('')
  const [tinhTrang, setTinhTrang] = useState('')
  const [lanTai, setLanTai] = useState(0)
  const vungBieuMau = useRef(null)

  useEffect(() => {
    const controller = new AbortController()
    const tuyChon = { signal: controller.signal }
    Promise.all([
      goiApiQuanTri(loai, token, tuyChon),
      goiApiQuanTri(laPhongBan ? 'users' : 'meeting-rooms', token, tuyChon),
    ]).then(([muc, lienQuan]) => {
      if (controller.signal.aborted) return
      setDanhSach(muc)
      setDanhSachLienQuan(lienQuan)
      setLoiTai('')
    }).catch((error) => {
      if (!controller.signal.aborted) setLoiTai(error.message)
    })
    return () => controller.abort()
  }, [loai, laPhongBan, lanTai, token])

  useEffect(() => {
    vungBieuMau.current?.focus()
  }, [mucChon])

  const id = laPhongBan ? mucChon?.phongBanId : mucChon?.thietBiId
  const idCua = (muc) => laPhongBan ? muc.phongBanId : muc.thietBiId
  const tenCua = (muc) => laPhongBan ? muc.tenPhongBan : muc.tenThietBi
  const truongPhong = danhSachLienQuan.filter((nguoi) =>
    nguoi.phongBanId === id && nguoi.maVaiTro === 'CAN_BO' && nguoi.trangThai === 'HOAT_DONG',
  )
  const dangLoc = tuKhoa.trim().toLocaleLowerCase('vi')
  const ketQua = danhSach?.filter((muc) =>
    (!tinhTrang || muc.tinhTrang === tinhTrang)
    && [tenCua(muc), muc.maThietBi, muc.tenPhongHop, muc.tenTruongPhong]
      .some((giaTri) => giaTri?.toLocaleLowerCase('vi').includes(dangLoc)),
  )

  function moForm(muc) {
    setMucChon(muc)
    setLoi('')
    setThongBao('')
  }

  async function luu(event) {
    event.preventDefault()
    if (dangLuu) return
    const form = new FormData(event.currentTarget)
    const duLieu = laPhongBan
      ? { tenPhongBan: form.get('tenPhongBan').trim(), moTa: form.get('moTa').trim() }
      : {
        tenThietBi: form.get('tenThietBi').trim(),
        maThietBi: form.get('maThietBi').trim(),
        soLuong: Number(form.get('soLuong')),
        tinhTrang: form.get('tinhTrang'),
        phongHopId: Number(form.get('phongHopId')) || null,
        ghiChu: form.get('ghiChu').trim(),
      }
    await capNhatDanhSach(id ? `${loai}/${id}` : loai, id ? 'PUT' : 'POST', duLieu)
  }

  async function phanCongTruongPhong(event) {
    event.preventDefault()
    if (dangLuu || !id) return
    const truongPhongId = Number(new FormData(event.currentTarget).get('truongPhongId'))
    if (!truongPhong.some((nguoi) => nguoi.nguoiDungId === truongPhongId)) return
    await capNhatDanhSach(`${loai}/${id}/leader`, 'PATCH', { truongPhongId })
  }

  async function capNhatDanhSach(url, method, duLieu) {
    setDangLuu(true)
    setLoi('')
    setThongBao('')
    try {
      const daLuu = await goiApiQuanTri(url, token, { method, duLieu })
      const nguoi = truongPhong.find((muc) => muc.nguoiDungId === daLuu.truongPhongId)
      const muc = laPhongBan && nguoi ? { ...daLuu, tenTruongPhong: nguoi.hoTen } : daLuu
      setDanhSach((hienTai) => id
        ? hienTai.map((item) => idCua(item) === id ? muc : item)
        : [...hienTai, muc])
      setMucChon(muc)
      setThongBao(method === 'PATCH' ? 'Đã phân công trưởng phòng.' : `Đã lưu ${danhTu}.`)
      if (method === 'PATCH') setLanTai((lan) => lan + 1)
    } catch (error) {
      setLoi(error.message)
    } finally {
      setDangLuu(false)
    }
  }

  return (
    <div className="space-y-5">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <h1 className="text-2xl font-bold">Quản lý {danhTu}</h1>
        <button type="button" className={LOP_NUT_CHINH} disabled={!danhSach || dangLuu} onClick={() => moForm({})}>Thêm {danhTu}</button>
      </div>
      {loiTai && (
        <div className="flex flex-wrap items-center gap-3 rounded-lg bg-red-50 p-3 text-sm text-red-800">
          <p role="alert">{loiTai}</p>
          <button type="button" className="font-semibold underline" onClick={() => setLanTai((lan) => lan + 1)}>Tải lại</button>
        </div>
      )}
      {!danhSach && !loiTai && <p role="status" className="text-sm text-slate-600">Đang tải {danhTu}…</p>}
      {danhSach && (
        <div className={LOP_KHOI}>
          <div className="grid gap-3 sm:grid-cols-2">
            <label className="text-sm font-semibold">Tìm {danhTu}
              <input type="search" className={LOP_TRUONG} value={tuKhoa} onChange={(event) => setTuKhoa(event.target.value)} />
            </label>
            {!laPhongBan && (
              <label className="text-sm font-semibold">Tình trạng
                <select className={LOP_TRUONG} value={tinhTrang} onChange={(event) => setTinhTrang(event.target.value)}>
                  <option value="">Tất cả tình trạng</option>
                  {Object.entries(TINH_TRANG_THIET_BI).map(([ma, ten]) => <option key={ma} value={ma}>{ten}</option>)}
                </select>
              </label>
            )}
          </div>
          {ketQua.length === 0 && <p className="text-sm text-slate-600">{danhSach.length ? 'Không tìm thấy kết quả.' : `Chưa có ${danhTu}.`}</p>}
          <ul className="divide-y divide-slate-100">
            {ketQua.map((muc) => (
              <li key={idCua(muc)} className="flex flex-wrap items-center justify-between gap-3 py-3">
                <div className="min-w-0">
                  <p className="break-words font-semibold">{tenCua(muc)}</p>
                  <p className="text-sm text-slate-600">
                    {laPhongBan
                      ? `Trưởng phòng · ${muc.tenTruongPhong || (muc.truongPhongId ? `#${muc.truongPhongId}` : 'Chưa phân công')}`
                      : [muc.maThietBi, muc.tenPhongHop || 'Chưa gắn phòng họp', `SL ${muc.soLuong}`, TINH_TRANG_THIET_BI[muc.tinhTrang] || muc.tinhTrang].filter(Boolean).join(' · ')}
                  </p>
                </div>
                <button type="button" className="rounded-lg border border-slate-200 px-3 py-2 text-sm font-semibold hover:bg-slate-50 disabled:opacity-50" disabled={dangLuu} onClick={() => moForm(muc)}>Chỉnh sửa</button>
              </li>
            ))}
          </ul>
        </div>
      )}

      {mucChon && (
        <section ref={vungBieuMau} tabIndex={-1} className={`${LOP_KHOI} scroll-mt-24`} aria-label={`Biểu mẫu ${danhTu}`}>
          <div className="flex items-center justify-between gap-3">
            <h2 className="text-lg font-bold">{id ? 'Cập nhật' : 'Thêm'} {danhTu}</h2>
            <button type="button" className="text-sm font-semibold text-slate-600 disabled:opacity-50" disabled={dangLuu} onClick={() => moForm(null)}>Đóng</button>
          </div>
          <form key={id ?? 'moi'} onSubmit={luu} className="space-y-4">
            <fieldset disabled={dangLuu} className="grid gap-4 sm:grid-cols-2">
              <label className="text-sm font-semibold">Tên {danhTu}
                <input className={LOP_TRUONG} name={laPhongBan ? 'tenPhongBan' : 'tenThietBi'} defaultValue={tenCua(mucChon) ?? ''} maxLength={150} required />
              </label>
              {laPhongBan ? (
                <label className="text-sm font-semibold sm:col-span-2">Mô tả
                  <textarea className={LOP_TRUONG} name="moTa" defaultValue={mucChon.moTa ?? ''} rows={3} />
                </label>
              ) : (
                <>
                  <label className="text-sm font-semibold">Mã thiết bị
                    <input className={LOP_TRUONG} name="maThietBi" defaultValue={mucChon.maThietBi ?? ''} maxLength={50} />
                  </label>
                  <label className="text-sm font-semibold">Số lượng
                    <input className={LOP_TRUONG} name="soLuong" type="number" min="1" step="1" defaultValue={mucChon.soLuong ?? 1} required />
                  </label>
                  <label className="text-sm font-semibold">Tình trạng
                    <select className={LOP_TRUONG} name="tinhTrang" defaultValue={mucChon.tinhTrang ?? 'TOT'} required>
                      {Object.entries(TINH_TRANG_THIET_BI).map(([ma, ten]) => <option key={ma} value={ma}>{ten}</option>)}
                    </select>
                  </label>
                  <label className="text-sm font-semibold">Phòng họp
                    <select className={LOP_TRUONG} name="phongHopId" defaultValue={mucChon.phongHopId ?? ''}>
                      <option value="">Chưa gắn phòng họp</option>
                      {danhSachLienQuan.map((muc) => <option key={muc.phongHopId} value={muc.phongHopId}>{muc.tenPhongHop}</option>)}
                    </select>
                  </label>
                  <label className="text-sm font-semibold sm:col-span-2">Ghi chú
                    <textarea className={LOP_TRUONG} name="ghiChu" defaultValue={mucChon.ghiChu ?? ''} rows={3} />
                  </label>
                </>
              )}
            </fieldset>
            <button type="submit" className={LOP_NUT_CHINH} disabled={dangLuu}>{dangLuu ? 'Đang lưu…' : 'Lưu thay đổi'}</button>
          </form>

          {laPhongBan && id && (
            <form key={`truong-phong-${id}-${mucChon.truongPhongId ?? ''}`} onSubmit={phanCongTruongPhong} className="flex flex-wrap items-end gap-3 border-t border-slate-100 pt-4">
              <label className="min-w-48 flex-1 text-sm font-semibold">Trưởng phòng
                <select className={LOP_TRUONG} name="truongPhongId" defaultValue={mucChon.truongPhongId ?? ''} disabled={dangLuu || !truongPhong.length} required>
                  <option value="">{truongPhong.length ? 'Chọn cán bộ trong phòng ban' : 'Chưa có cán bộ đủ điều kiện'}</option>
                  {truongPhong.map((nguoi) => <option key={nguoi.nguoiDungId} value={nguoi.nguoiDungId}>{nguoi.hoTen}</option>)}
                </select>
              </label>
              <button type="submit" className={LOP_NUT_CHINH} disabled={dangLuu || !truongPhong.length}>Phân công trưởng phòng</button>
            </form>
          )}
          {loi && <p role="alert" className="text-sm text-red-800">{loi}</p>}
          {thongBao && <p role="status" className="text-sm text-emerald-800">{thongBao}</p>}
        </section>
      )}
    </div>
  )
}
