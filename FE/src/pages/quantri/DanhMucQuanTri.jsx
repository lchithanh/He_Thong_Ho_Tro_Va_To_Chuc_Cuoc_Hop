import { useEffect, useId, useState } from 'react'
import Pencil from 'lucide-react/dist/esm/icons/pencil.mjs'
import Plus from 'lucide-react/dist/esm/icons/plus.mjs'
import { goiApiQuanTri } from '../../api/quanTriApi.js'
import { khopTuKhoa } from '../../components/giaoDienPhongHop.js'
import { useDongBoDuLieu } from '../../services/useDongBoDuLieu.js'
import { TINH_TRANG_THIET_BI, TRANG_THAI_PHONG, TRANG_THAI_TAI_KHOAN } from './danhMucQuanTri.js'
import { NganChinhSua, PhanHoi, TieuDeTrang, TimKiem, TrangThai } from './uiQuanTri.jsx'

const TRANG_THAI_PHONG_CO_THE_LUU = ['SAN_SANG', 'BAO_TRI', 'NGUNG_SU_DUNG']
const TEN_VAI_TRO = { ADMIN: 'Quản trị viên', LANH_DAO: 'Lãnh đạo', THU_KY: 'Thư ký', CAN_BO: 'Cán bộ' }

const CAU_HINH = {
  users: {
    ten: 'Tài khoản', khoa: 'nguoiDungId', tenChinh: 'hoTen', truongPhu: 'tenDangNhap',
    moTa: 'Quản lý hồ sơ người dùng, phòng ban và vai trò.',
    cot: [['Phòng ban', 'tenPhongBan'], ['Vai trò', 'tenVaiTro']], trangThai: TRANG_THAI_TAI_KHOAN,
    danhMucLienQuan: 'departments',
  },
  departments: {
    ten: 'Phòng ban', khoa: 'phongBanId', tenChinh: 'tenPhongBan', truongPhu: 'moTa',
    moTa: 'Cập nhật thông tin phòng ban và phân công trưởng phòng.',
    cot: [['Trưởng phòng', 'tenTruongPhong']], trangThai: TRANG_THAI_TAI_KHOAN,
    danhMucLienQuan: 'users',
  },
  'meeting-rooms': {
    ten: 'Phòng họp', khoa: 'phongHopId', tenChinh: 'tenPhongHop', truongPhu: 'moTa',
    moTa: 'Quản lý địa điểm, sức chứa và trạng thái phục vụ.',
    cot: [['Vị trí', 'viTri'], ['Sức chứa', 'sucChua']], trangThai: TRANG_THAI_PHONG,
  },
  equipment: {
    ten: 'Thiết bị', khoa: 'thietBiId', tenChinh: 'tenThietBi', truongPhu: 'maThietBi',
    moTa: 'Quản lý thiết bị, số lượng và phòng được trang bị.',
    cot: [['Phòng họp', 'tenPhongHop'], ['Số lượng', 'soLuong']], trangThai: TINH_TRANG_THIET_BI,
    danhMucLienQuan: 'meeting-rooms',
  },
}

function text(form, key) {
  return String(form.get(key) ?? '').trim()
}

function so(form, key) {
  return Number(form.get(key)) || null
}

export default function DanhMucQuanTri({ token, loai, nguoiDungId, phongHopLoc, onXemThietBi }) {
  const phienDongBo = useDongBoDuLieu()
  const { ten, khoa, tenChinh, truongPhu, moTa, cot, trangThai, danhMucLienQuan } = CAU_HINH[loai]
  const danhTu = ten.toLocaleLowerCase('vi')
  const [danhSach, setDanhSach] = useState(null)
  const [lienQuan, setLienQuan] = useState(null)
  const [vaiTro, setVaiTro] = useState(null)
  const [loiTai, setLoiTai] = useState('')
  const [loi, setLoi] = useState('')
  const [thongBao, setThongBao] = useState('')
  const [mucChon, setMucChon] = useState(null)
  const [dangLuu, setDangLuu] = useState(false)
  const [tuKhoa, setTuKhoa] = useState('')
  const [loc, setLoc] = useState('')
  const [locPhongHop, setLocPhongHop] = useState(() => String(phongHopLoc ?? ''))
  const [lanTai, setLanTai] = useState(0)

  useEffect(() => {
    const controller = new AbortController()
    const tuyChon = { signal: controller.signal }
    Promise.allSettled([
      goiApiQuanTri(loai, token, tuyChon),
      danhMucLienQuan ? goiApiQuanTri(danhMucLienQuan, token, tuyChon) : [],
      loai === 'users' ? goiApiQuanTri('users/roles', token, tuyChon) : [],
    ]).then(([chinh, phu, quyen]) => {
      if (controller.signal.aborted) return
      if (chinh.status === 'fulfilled') setDanhSach(chinh.value)
      if (phu.status === 'fulfilled') setLienQuan(phu.value)
      if (quyen.status === 'fulfilled') setVaiTro(quyen.value)
      setLoiTai(chinh.status === 'rejected' ? chinh.reason.message
        : phu.status === 'rejected' ? 'Chưa tải được danh mục liên quan.'
          : quyen.status === 'rejected' ? 'Chưa tải được danh sách vai trò.' : '')
    })
    return () => controller.abort()
  }, [loai, danhMucLienQuan, lanTai, token, phienDongBo])

  const khoaTrangThai = loai === 'equipment' ? 'tinhTrang' : 'trangThai'
  const ketQua = danhSach?.filter((muc) =>
    (!loc || muc[khoaTrangThai] === loc)
    && (loai !== 'equipment' || !locPhongHop || String(muc.phongHopId ?? '') === locPhongHop)
    && [muc[tenChinh], muc[truongPhu], muc.email, ...cot.map(([, key]) => muc[key])]
      .some((giaTri) => khopTuKhoa(String(giaTri ?? ''), tuKhoa)),
  )
  const id = mucChon?.[khoa]
  const laTaiKhoanHienTai = id != null && Number(nguoiDungId) === id
  const trangThaiTaiKhoanMoi = mucChon?.trangThai === 'HOAT_DONG' ? 'BI_KHOA' : 'HOAT_DONG'

  function moForm(muc) {
    setMucChon(muc)
    setLoi('')
    setThongBao('')
  }

  async function luu(url, method, duLieu, nhan) {
    if (dangLuu) return
    setDangLuu(true)
    setLoi('')
    setThongBao('')
    try {
      const muc = await goiApiQuanTri(url, token, { method, duLieu })
      setDanhSach((cu) => cu.some((item) => item[khoa] === muc[khoa])
        ? cu.map((item) => item[khoa] === muc[khoa] ? muc : item) : [...cu, muc])
      setMucChon(null)
      setThongBao(nhan)
      setLanTai((lan) => lan + 1)
    } catch (error) {
      setLoi(error.message)
    } finally {
      setDangLuu(false)
    }
  }

  function luuThongTin(event) {
    event.preventDefault()
    const form = new FormData(event.currentTarget)
    let duLieu
    switch (loai) {
      case 'users':
        duLieu = {
          hoTen: text(form, 'hoTen'), email: text(form, 'email'),
          maNhanVien: text(form, 'maNhanVien') || null, soDienThoai: text(form, 'soDienThoai'),
          chucVu: text(form, 'chucVu'), phongBanId: so(form, 'phongBanId'),
        }
        if (!id) Object.assign(duLieu, {
          tenDangNhap: text(form, 'tenDangNhap'), matKhau: form.get('matKhau'), vaiTroId: so(form, 'vaiTroId'),
        })
        break
      case 'meeting-rooms':
        duLieu = { tenPhongHop: text(form, 'tenPhongHop'), viTri: text(form, 'viTri'),
          sucChua: so(form, 'sucChua'), moTa: text(form, 'moTa') }
        break
      case 'equipment':
        duLieu = { tenThietBi: text(form, 'tenThietBi'), maThietBi: text(form, 'maThietBi'),
          soLuong: so(form, 'soLuong'), tinhTrang: text(form, 'tinhTrang'), phongHopId: so(form, 'phongHopId'),
          ghiChu: text(form, 'ghiChu') }
        break
      default:
        duLieu = { tenPhongBan: text(form, 'tenPhongBan'), moTa: text(form, 'moTa') }
    }
    luu(id ? `${loai}/${id}` : loai, id ? 'PUT' : 'POST', duLieu, `Đã lưu ${danhTu}.`)
  }

  function luuThaoTac(event, duongDan, tenTruong, laSo, thanhCong) {
    event.preventDefault()
    const form = new FormData(event.currentTarget, event.nativeEvent.submitter)
    luu(`${loai}/${id}/${duongDan}`, 'PATCH', { [tenTruong]: laSo ? so(form, tenTruong) : text(form, tenTruong) }, thanhCong)
  }

  const phongBan = loai === 'users' ? (lienQuan ?? []).filter((phong) =>
    phong.trangThai === 'HOAT_DONG' || phong.phongBanId === mucChon?.phongBanId) : []
  const truongPhong = loai === 'departments' && id ? (lienQuan ?? []).filter((nguoi) =>
    nguoi.phongBanId === id && nguoi.maVaiTro === 'CAN_BO' && nguoi.trangThai === 'HOAT_DONG') : []
  const chuaCoLuaChon = loai === 'users' && !id && !vaiTro?.length

  return (
    <div className="adm-page">
      <TieuDeTrang ten={ten} moTa={moTa}>
        <button type="button" className="adm-btn adm-primary" disabled={!danhSach} onClick={() => moForm({})}>
          <Plus size={18} aria-hidden="true" /> Thêm {danhTu}
        </button>
      </TieuDeTrang>
      <PhanHoi loi={loiTai} thongBao={thongBao} onTaiLai={() => setLanTai((lan) => lan + 1)} />
      {danhSach ? (
        <section className="adm-surface" aria-label={`Danh sách ${danhTu}`}>
          <div className="adm-toolbar">
            <TimKiem tuKhoa={tuKhoa} onDoi={setTuKhoa} nhan={`Tìm ${danhTu}`} />
            <select className="adm-input adm-filter" aria-label="Lọc theo trạng thái" value={loc} onChange={(event) => setLoc(event.target.value)}>
              <option value="">Tất cả trạng thái</option>
              {Object.entries(trangThai).map(([ma, [nhan]]) => <option key={ma} value={ma}>{nhan}</option>)}
            </select>
            {loai === 'equipment' && lienQuan && (
              <select className="adm-input adm-filter" aria-label="Lọc thiết bị theo phòng họp" value={locPhongHop}
                onChange={(event) => setLocPhongHop(event.target.value)}>
                <option value="">Tất cả phòng họp</option>
                {lienQuan.map((phong) => <option key={phong.phongHopId} value={phong.phongHopId}>{phong.tenPhongHop}</option>)}
              </select>
            )}
            <p className="adm-result" aria-live="polite">{ketQua.length} / {danhSach.length} {danhTu}</p>
          </div>
          {ketQua.length ? (
            <div className="adm-table-scroll" tabIndex={0} role="region" aria-label={`Bảng ${danhTu}`}>
              <table className="adm-table">
                <thead><tr><th scope="col">{ten}</th>
                  {cot.map(([nhan, key]) => <th key={key} scope="col">{nhan}</th>)}
                  <th scope="col">Trạng thái</th><th scope="col" className="adm-actions">Thao tác</th>
                </tr></thead>
                <tbody>{ketQua.map((muc) => {
                  const [nhan, mau] = trangThai[muc[khoaTrangThai]] ?? [muc[khoaTrangThai] || 'Chưa cập nhật']
                  return (
                    <tr key={muc[khoa]}>
                      <th scope="row"><strong>{muc[tenChinh]}</strong><small>{muc.email || muc[truongPhu]}</small></th>
                      {cot.map(([, key]) => <td key={key}>{muc[key] ?? (key === 'tenTruongPhong' && muc.truongPhongId ? `#${muc.truongPhongId}` : '—')}</td>)}
                      <td><TrangThai ten={nhan} mau={mau} /></td>
                      <td className="adm-actions">
                        <div className="adm-form-actions">
                          {loai === 'meeting-rooms' && <button type="button" className="adm-btn adm-edit"
                            aria-label={`Thiết bị trong ${muc.tenPhongHop}`} onClick={() => onXemThietBi(muc.phongHopId)}>
                            Thiết bị
                          </button>}
                          <button type="button" className="adm-btn adm-edit"
                            aria-label={`Chỉnh sửa ${muc[tenChinh]}`} onClick={() => moForm(muc)}>
                            <Pencil size={15} aria-hidden="true" /> Sửa
                          </button>
                        </div>
                      </td>
                    </tr>
                  )
                })}</tbody>
              </table>
            </div>
          ) : <p className="adm-empty">{danhSach.length ? 'Không có kết quả phù hợp.' : `Chưa có ${danhTu}.`}</p>}
        </section>
      ) : !loiTai && <p role="status" className="adm-empty adm-surface">Đang tải {danhTu}…</p>}

      {mucChon && (
        <NganChinhSua tieuDe={id ? `Chỉnh sửa ${danhTu}` : `Thêm ${danhTu}`}
          moTa={id ? mucChon[tenChinh] : undefined} dangLuu={dangLuu} onDong={() => moForm(null)}>
          <PhanHoi loi={loi} />
          <form onSubmit={luuThongTin} className="adm-form">
            <fieldset disabled={dangLuu} className="adm-form-grid">
              {loai === 'users' ? (
                <>
                  <Truong ten="hoTen" nhan="Họ và tên" muc={mucChon} required maxLength={150} />
                  <Truong ten="email" nhan="Email" muc={mucChon} type="email" required maxLength={190} />
                  <Truong ten="maNhanVien" nhan="Mã nhân viên" muc={mucChon} maxLength={30} />
                  <Truong ten="soDienThoai" nhan="Số điện thoại" muc={mucChon} type="tel" maxLength={25} />
                  <Truong ten="chucVu" nhan="Chức vụ" muc={mucChon} maxLength={120} />
                  <LuaChon ten="phongBanId" nhan="Phòng ban" giaTri={mucChon.phongBanId}
                    tuyChon={phongBan.map((muc) => [muc.phongBanId, muc.tenPhongBan])}
                    disabled={!lienQuan} trong="Chưa gán phòng ban" />
                  {!id && <>
                    <Truong ten="tenDangNhap" nhan="Tên đăng nhập" muc={mucChon} required maxLength={80} />
                    <Truong ten="matKhau" nhan="Mật khẩu ban đầu" muc={mucChon} type="password" autoComplete="new-password" required />
                    <LuaChon ten="vaiTroId" nhan="Vai trò" tuyChon={(vaiTro ?? []).map((muc) => [muc.vaiTroId, muc.tenVaiTro || TEN_VAI_TRO[muc.maVaiTro]])}
                      trong={vaiTro?.length ? 'Chọn vai trò' : 'Chưa có vai trò khả dụng'} required />
                  </>}
                </>
              ) : (
                <>
                  <Truong ten={tenChinh} nhan={`Tên ${danhTu}`} muc={mucChon} required maxLength={loai === 'meeting-rooms' ? 120 : 150} rong />
                  {loai === 'meeting-rooms' && <>
                    <Truong ten="viTri" nhan="Vị trí" muc={mucChon} maxLength={255} />
                    <Truong ten="sucChua" nhan="Sức chứa (người)" muc={mucChon} type="number" min="1" required />
                  </>}
                  {loai === 'equipment' && <>
                    <Truong ten="maThietBi" nhan="Mã thiết bị" muc={mucChon} maxLength={50} />
                    <Truong ten="soLuong" nhan="Số lượng" muc={mucChon} type="number" min="1" macDinh={1} required />
                    <LuaChon ten="tinhTrang" nhan="Tình trạng" giaTri={mucChon.tinhTrang ?? 'TOT'}
                      tuyChon={Object.entries(TINH_TRANG_THIET_BI).map(([ma, [nhan]]) => [ma, nhan])} required />
                    <LuaChon ten="phongHopId" nhan="Phòng họp" giaTri={mucChon.phongHopId} trong="Chưa gắn phòng họp" disabled={!lienQuan}
                      tuyChon={(lienQuan ?? []).map((muc) => [muc.phongHopId, muc.tenPhongHop])} />
                  </>}
                  <Truong ten={loai === 'equipment' ? 'ghiChu' : 'moTa'} nhan={loai === 'equipment' ? 'Ghi chú' : 'Mô tả'} muc={mucChon} nhieuDong rong />
                </>
              )}
            </fieldset>
            <div className="adm-form-actions">
              <button type="button" className="adm-btn adm-secondary" onClick={() => moForm(null)} disabled={dangLuu}>Đóng</button>
              <button type="submit" className="adm-btn adm-primary"
                disabled={dangLuu || chuaCoLuaChon || ((loai === 'users' || loai === 'equipment') && !lienQuan)}>
                {dangLuu ? 'Đang lưu…' : id ? 'Lưu thông tin' : `Thêm ${danhTu}`}
              </button>
            </div>
          </form>

          {id && loai === 'departments' && (
            <form className="adm-subform" onSubmit={(event) => luuThaoTac(event, 'leader', 'truongPhongId', true, 'Đã phân công trưởng phòng.')}>
              <LuaChon ten="truongPhongId" nhan="Trưởng phòng" giaTri={mucChon.truongPhongId}
                trong={truongPhong.length ? 'Chọn cán bộ trong phòng ban' : 'Chưa có cán bộ đủ điều kiện'}
                tuyChon={truongPhong.map((muc) => [muc.nguoiDungId, muc.hoTen])} disabled={dangLuu || !truongPhong.length} required />
              <div className="adm-form-actions">
                <button type="submit" className="adm-btn adm-secondary" disabled={dangLuu || !truongPhong.length}>Phân công</button>
                {mucChon.truongPhongId && <button type="button" className="adm-btn adm-secondary adm-danger-text" disabled={dangLuu}
                  onClick={() => luu(`departments/${id}/leader`, 'PATCH', { truongPhongId: null }, 'Đã bỏ phân công trưởng phòng.')}>
                  Bỏ phân công
                </button>}
              </div>
            </form>
          )}
          {id && loai === 'meeting-rooms' && (
            <form className="adm-subform" onSubmit={(event) => luuThaoTac(event, 'status', 'trangThai', false, 'Đã cập nhật trạng thái phòng.')}>
              <LuaChon ten="trangThai" nhan="Trạng thái phòng" giaTri={mucChon.trangThai}
                trong={TRANG_THAI_PHONG_CO_THE_LUU.includes(mucChon.trangThai) ? undefined : 'Chọn trạng thái mới'}
                tuyChon={TRANG_THAI_PHONG_CO_THE_LUU.map((ma) => [ma, TRANG_THAI_PHONG[ma][0]])} disabled={dangLuu} required />
              <button type="submit" className="adm-btn adm-secondary" disabled={dangLuu}>Lưu trạng thái</button>
            </form>
          )}
          {id && loai === 'users' && !laTaiKhoanHienTai && (
            <form className="adm-subform" onSubmit={(event) => luuThaoTac(event, 'vai-tro', 'vaiTroId', true, 'Đã cập nhật vai trò.')}>
              <LuaChon ten="vaiTroId" nhan="Vai trò" giaTri={mucChon.vaiTroId}
                trong={vaiTro?.length ? 'Chọn vai trò' : 'Chưa có vai trò khả dụng'}
                tuyChon={(vaiTro ?? []).map((muc) => [muc.vaiTroId, muc.tenVaiTro || TEN_VAI_TRO[muc.maVaiTro]])}
                disabled={dangLuu || !vaiTro?.length} required />
              <button type="submit" className="adm-btn adm-secondary" disabled={dangLuu || !vaiTro?.length}>Lưu vai trò</button>
            </form>
          )}
          {id && loai === 'users' && !laTaiKhoanHienTai && (
            <form className="adm-subform" onSubmit={(event) => luuThaoTac(event, 'status', 'trangThai', false, 'Đã cập nhật trạng thái tài khoản.')}>
              <span>Trạng thái tài khoản <TrangThai ten={TRANG_THAI_TAI_KHOAN[mucChon.trangThai]?.[0] ?? mucChon.trangThai}
                mau={TRANG_THAI_TAI_KHOAN[mucChon.trangThai]?.[1]} /></span>
              <button name="trangThai" value={trangThaiTaiKhoanMoi} type="submit"
                className={`adm-btn adm-secondary ${trangThaiTaiKhoanMoi === 'BI_KHOA' ? 'adm-danger-text' : ''}`} disabled={dangLuu}>
                {trangThaiTaiKhoanMoi === 'BI_KHOA' ? 'Khóa tài khoản' : 'Kích hoạt tài khoản'}
              </button>
            </form>
          )}
        </NganChinhSua>
      )}
    </div>
  )
}

function Truong({ ten, nhan, muc, rong, nhieuDong, macDinh = '', ...props }) {
  const giaTri = muc[ten] ?? macDinh
  return <label className={`adm-field ${rong ? 'adm-field-wide' : ''}`}>
    {nhan}
    {nhieuDong ? <textarea className="adm-input" name={ten} defaultValue={giaTri} rows={3} {...props} />
      : <input className="adm-input" name={ten} defaultValue={giaTri} {...props} />}
  </label>
}

function LuaChon({ ten, nhan, giaTri, tuyChon, trong, ...props }) {
  const id = useId()
  return <label className="adm-field" htmlFor={id}>
    {nhan}
    <select id={id} className="adm-input" name={ten} defaultValue={giaTri ?? ''} {...props}>
      {trong && <option value="">{trong}</option>}
      {tuyChon.map(([ma, nhanLuaChon]) => <option key={ma} value={ma}>{nhanLuaChon}</option>)}
    </select>
  </label>
}
