import { useEffect, useState } from 'react'
import { goiNhomLienPhong } from '../../api/nhomLienPhongApi.js'
import BauLaiChucDanhNhom from './BauLaiChucDanhNhom.jsx'
import { useDongBoDuLieu } from '../../services/useDongBoDuLieu.js'
import FileText from 'lucide-react/dist/esm/icons/file-text.mjs'
import ArrowLeft from 'lucide-react/dist/esm/icons/arrow-left.mjs'
import ChevronRight from 'lucide-react/dist/esm/icons/chevron-right.mjs'
import Users from 'lucide-react/dist/esm/icons/users.mjs'
import X from 'lucide-react/dist/esm/icons/x.mjs'
import { HopThoaiHoSo } from '../../components/GiaoDienPhongHop.jsx'
import { LOP_CHU_PHU, LOP_NUT_CHINH, LOP_NUT_PHU, LOP_THE } from '../../components/giaoDienPhongHop.js'
import NhanTrangThai from '../thuky/NhanTrangThaiDeXuat.jsx'
import ChiTietDeXuat from '../thuky/ChiTietDeXuat.jsx'
import { dangPheDuyetKeHoach, layNhanTrangThaiHoSo } from '../thuky/hienThiDeXuat.js'

const TRANG_THAI = {
  CHO_CU_NHAN_SU: 'Chờ các phòng cử người',
  CHO_KICH_HOAT: 'Chờ kích hoạt',
  HOAT_DONG: 'Đang hoạt động',
  DA_DONG: 'Đã đóng',
}
const TEN_CAP_DUYET = { TRUONG_NHOM: 'Tổ trưởng', LANH_DAO: 'Chủ tịch hoặc Phó Chủ tịch phụ trách' }
const TEN_DOI_NGUOI = { CHO_XU_LY: 'Chờ xử lý', DA_CAP_NHAT: 'Đã cập nhật', TU_CHOI: 'Từ chối', DA_HUY: 'Đã hủy' }
const KHUNG = 'space-y-3 rounded-xl border border-slate-200 bg-white p-4'
const TRUONG = 'block w-full rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm'
const NUT = 'rounded-lg border border-slate-300 px-3 py-2 text-sm font-semibold hover:bg-slate-50'
const XAC_NHAN = 'rounded-lg bg-emerald-800 px-4 py-2 text-sm font-semibold text-white disabled:opacity-60'

function Truong({ ten, children }) {
  return <label className="block space-y-1 text-sm font-medium"><span>{ten}</span>{children}</label>
}

function ChonNguoi({ ten, name, danhSach, batBuoc = true, macDinh = '' }) {
  return <Truong ten={ten}>
    <select name={name} required={batBuoc} className={TRUONG} defaultValue={macDinh}>
      <option value="">{batBuoc ? 'Chọn người' : 'Không chọn'}</option>
      {danhSach.map((muc) => <option value={muc.nguoiDungId} key={muc.nguoiDungId}>{muc.hoTen}</option>)}
    </select>
  </Truong>
}

function Phieu({ tieuDe, nut, dangGui, onGui, moNgay = false, onDong, children }) {
  const [dangMo, setDangMo] = useState(moNgay)
  const [loi, setLoi] = useState('')
  function dong() {
    setDangMo(false)
    onDong?.()
  }
  return <>
    {!moNgay && <button className={NUT} type="button" onClick={() => setDangMo(true)}>{tieuDe}</button>}
    {dangMo && <HopThoaiHoSo tieuDe={tieuDe} Icon={FileText} dangLuu={dangGui} onDong={dong}>
      <form onSubmit={async (event) => {
        event.preventDefault()
        const f = new FormData(event.currentTarget, event.nativeEvent.submitter)
        setLoi('')
        try {
          if (await onGui(f)) dong()
        } catch (error) {
          setLoi(error.message)
        }
      }}>
        <fieldset className="min-w-0 space-y-3" disabled={dangGui}>
          {loi && <p role="alert" className="text-sm text-rose-800">{loi}</p>}
          {children}
          {nut && <button type="submit" className={XAC_NHAN}>{nut}</button>}
        </fieldset>
      </form>
    </HopThoaiHoSo>}
  </>
}

function TaoNhom({ duLieu, dangGui, gui, onDong }) {
  const { quyen, phongBanTao } = duLieu
  const [phongChuTri, setPhongChuTri] = useState(quyen.lanhDao ? '' : String(quyen.phongTruongPhong))
  return <Phieu tieuDe={quyen.lanhDao ? 'Chỉ đạo lập nhóm' : 'Tạo yêu cầu lập nhóm'} nut="Gửi yêu cầu" moNgay onDong={onDong}
    dangGui={dangGui} onGui={(form) => gui('', 'POST', {
      tenNhom: form.get('tenNhom'), mucDich: form.get('mucDich'), capNhom: form.get('capNhom'),
      phongBanChuTriId: Number(phongChuTri), phongBanIds: [Number(phongChuTri), ...form.getAll('phong').map(Number)],
    })}>
    <Truong ten="Tên nhóm"><input name="tenNhom" maxLength={150} required className={TRUONG} /></Truong>
    <Truong ten="Mục đích"><textarea name="mucDich" rows={2} maxLength={1000} required className={TRUONG} /></Truong>
    <div className="grid gap-3 sm:grid-cols-2">
      <Truong ten="Phòng chủ trì">
        <select value={phongChuTri} onChange={(e) => setPhongChuTri(e.target.value)} required className={TRUONG}
          disabled={!quyen.lanhDao}>
          <option value="">Chọn phòng</option>
          {phongBanTao.map((p) => <option key={p.phongBanId} value={p.phongBanId}>{p.tenPhongBan}</option>)}
        </select>
      </Truong>
      <Truong ten="Cấp nhóm"><select name="capNhom" className={TRUONG}>
        <option value="THUONG">Thường</option><option value="CAP_CAO">Cấp cao</option>
      </select></Truong>
    </div>
    <fieldset className="grid gap-1 sm:grid-cols-2">
      <legend className="mb-1 text-sm font-medium">Phòng cùng tham gia</legend>
      {phongBanTao.filter((p) => String(p.phongBanId) !== phongChuTri).map((p) => (
        <label key={p.phongBanId} className="flex items-center gap-2 text-sm">
          <input type="checkbox" name="phong" value={p.phongBanId} />{p.tenPhongBan}
        </label>
      ))}
    </fieldset>
  </Phieu>
}

function DeXuatNhom({ chiTiet, dangGui, gui }) {
  const [mucDichHop, setMucDichHop] = useState('HOP_THUONG')
  const [coKhachNgoai, setCoKhachNgoai] = useState(false)
  const [moiThem, setMoiThem] = useState([])
  const [tuKhoa, setTuKhoa] = useState('')
  const canLanhDao = mucDichHop !== 'HOP_THUONG' || coKhachNgoai
  const [chuTichLapNhom] = chiTiet.lanhDaoDuyet
  const tenToTruong = chiTiet.thanhVien.find((nguoi) => nguoi.nguoiDungId === chiTiet.nhom.truongNhomId)?.hoTen
  const tenThuKy = chiTiet.thanhVien.find((nguoi) => nguoi.nguoiDungId === chiTiet.nhom.thuKyNhomId)?.hoTen
  const thieuLanhDao = canLanhDao && !chuTichLapNhom
  const nhanSuHienThi = chiTiet.nguoiMoiThem.filter((nguoi) => moiThem.includes(nguoi.nguoiDungId)
    || nguoi.hoTen.toLocaleLowerCase('vi').includes(tuKhoa.trim().toLocaleLowerCase('vi')))
  return <Phieu tieuDe="Gửi đề xuất họp từ nhóm" nut={thieuLanhDao ? null : 'Gửi đề xuất'} dangGui={dangGui}
    onGui={(f) => !thieuLanhDao && gui('/de-xuat', 'POST', {
      tieuDe: f.get('tieuDe'), mucTieu: f.get('mucTieu'), noiDung: f.get('noiDung'), loaiHinh: f.get('loaiHinh'),
      mucDichHop, coKhachNgoai, nguoiMoiThemIds: moiThem,
      emailKhachNgoai: (f.get('emailKhachNgoai') || '').split(/[,\s]+/).filter(Boolean),
      lanhDaoDuyetId: canLanhDao ? chuTichLapNhom.nguoiDungId : null,
    })}>
    <Truong ten="Tiêu đề"><input name="tieuDe" required maxLength={255} className={TRUONG} /></Truong>
    <Truong ten="Mục tiêu"><input name="mucTieu" required maxLength={3000} className={TRUONG} /></Truong>
    <Truong ten="Nội dung dự kiến"><textarea name="noiDung" rows={3} required maxLength={10000} className={TRUONG} /></Truong>
    <div className="grid gap-3 sm:grid-cols-2">
      <Truong ten="Mục đích họp"><select value={mucDichHop} onChange={(e) => setMucDichHop(e.target.value)} className={TRUONG}>
        <option value="HOP_THUONG">Bàn chuyên môn, triển khai hoặc rà soát tiến độ</option>
        <option value="CHU_TICH_CHU_TRI">Mời Chủ tịch chủ trì</option>
        <option value="XIN_CHU_TICH_KET_LUAN">Xin Chủ tịch tháo gỡ vướng mắc hoặc kết luận</option>
      </select></Truong>
      <Truong ten="Hình thức"><select name="loaiHinh" className={TRUONG}>
        <option value="TRUC_TIEP">Trực tiếp</option><option value="TRUC_TUYEN">Trực tuyến</option>
      </select></Truong>
    </div>
    <details className="rounded-lg border border-slate-200 p-3">
      <summary className="cursor-pointer text-sm font-medium">Mời thêm cán bộ dự buổi họp ({moiThem.length})</summary>
      <div className="mt-2">
        <Truong ten="Tìm theo họ tên"><input type="search" className={TRUONG} value={tuKhoa}
          onChange={(e) => setTuKhoa(e.target.value)} /></Truong>
      </div>
      <ul className="mt-2 max-h-44 space-y-1 overflow-auto">{nhanSuHienThi.map((nguoi) =>
        <li key={nguoi.nguoiDungId}><label className="flex gap-2 text-sm">
          <input type="checkbox" checked={moiThem.includes(nguoi.nguoiDungId)}
            onChange={(e) => setMoiThem(e.target.checked
              ? [...moiThem, nguoi.nguoiDungId] : moiThem.filter((id) => id !== nguoi.nguoiDungId))} />{nguoi.hoTen}
        </label></li>)}</ul>
      {!nhanSuHienThi.length && <p className="text-sm text-slate-600">Không có cán bộ phù hợp.</p>}
    </details>
    <label className="flex gap-2 text-sm"><input type="checkbox" checked={coKhachNgoai}
      onChange={(e) => setCoKhachNgoai(e.target.checked)} /> Có khách mời ngoài cơ quan</label>
    {coKhachNgoai && <Truong ten="Email khách ngoài"><textarea name="emailKhachNgoai" required rows={2}
      className={TRUONG} placeholder="Mỗi dòng một email" /></Truong>}
    {canLanhDao && chuTichLapNhom && <Truong ten="Chủ tịch duyệt cuối">
      <span className="text-sm text-slate-700">{chuTichLapNhom.hoTen} · {chuTichLapNhom.chucVu}</span>
    </Truong>}
    <p className="text-sm text-slate-700">Thư ký lập kế hoạch và tổ chức · {tenThuKy || 'Thư ký nhóm'}</p>
    <p className="text-sm text-slate-700">{canLanhDao
      ? 'Thư ký nhóm trình Tổ trưởng trước, sau đó chuyển hồ sơ đến Chủ tịch đã lập nhóm.'
      : `Tổ trưởng ${tenToTruong || ''} duyệt kế hoạch và lịch.`}</p>
    {thieuLanhDao && <p role="alert" className="text-sm text-amber-800">Người lập nhóm phải là Chủ tịch có vai trò Lãnh đạo và đang hoạt động để nhận hồ sơ này.</p>}
  </Phieu>
}

const TEN_THAO_TAC_THANH_VIEN = {
  them: 'Bổ sung thành viên',
  thay: 'Thay thành viên',
  rut: 'Rút khỏi nhóm',
}

function QuanLyThanhVien({ chiTiet, quyen, nguoiDungId, dangGui, gui }) {
  const { nhom, phongBan, thanhVien, nguonNhanSu, duocGui } = chiTiet
  const [mo, setMo] = useState(false)
  const [phongLoc, setPhongLoc] = useState('')
  const [thaoTac, setThaoTac] = useState(null)
  const [loi, setLoi] = useState('')
  const [thongBao, setThongBao] = useState('')
  const duocRut = nhom.trangThai === 'HOAT_DONG' && nguoiDungId != null && nguoiDungId === nhom.truongNhomId
  const dangRut = thaoTac?.loai === 'rut'
  const suaDuocPhong = (phongId) => nhom.trangThai === 'HOAT_DONG' &&
    (duocGui || phongId === quyen.phongTruongPhong)
  const phongSuaDuoc = phongBan.filter((phong) => suaDuocPhong(phong.phongBanId))
  const phongChon = thaoTac?.nguoi?.phongBanId ?? thaoTac?.phongId
  const nguoiThay = nguonNhanSu.filter((nguoi) => nguoi.phongBanId === phongChon &&
    !thanhVien.some((tv) => tv.nguoiDungId === nguoi.nguoiDungId))
  const phongHienThi = phongBan.filter((phong) => !phongLoc || String(phong.phongBanId) === phongLoc)

  function chonThaoTac(loai, nguoi) {
    setLoi('')
    setThongBao('')
    setThaoTac({ loai, nguoi, phongId: phongSuaDuoc.find((p) => String(p.phongBanId) === phongLoc)?.phongBanId ??
      phongSuaDuoc[0]?.phongBanId })
  }

  async function guiYeuCau(event) {
    event.preventDefault()
    if (dangGui) return
    const form = new FormData(event.currentTarget)
    setLoi('')
    try {
      await gui('/doi-nguoi', 'POST', {
        phongBanId: phongChon,
        nguoiCuId: thaoTac.nguoi?.nguoiDungId ?? null,
        nguoiMoiId: dangRut ? null : Number(form.get('nguoiMoi')),
        lyDo: form.get('lyDo'),
      })
      setThaoTac(null)
      setThongBao(dangRut ? `Đã rút ${thaoTac.nguoi.hoTen} khỏi nhóm.`
        : 'Đã gửi đề xuất. Theo dõi và xử lý tại mục Yêu cầu đổi nhân sự.')
    } catch (error) {
      setLoi(error.message)
    }
  }

  function dong() {
    setMo(false)
    setThaoTac(null)
    setLoi('')
    setThongBao('')
  }

  return <section className={LOP_THE + ' flex flex-wrap items-center justify-between gap-3'}>
    <div className="flex items-center gap-3">
      <span className="grid size-10 shrink-0 place-items-center rounded-xl bg-blue-100 text-blue-900 phong-toi:bg-phong-nhan-xanh phong-toi:text-phong-xanh">
        <Users size={20} aria-hidden="true" />
      </span>
      <div>
        <h3 className="font-semibold">Thành viên nhóm</h3>
        <p className={LOP_CHU_PHU}>{thanhVien.length} thành viên · {phongBan.length} phòng ban</p>
      </div>
    </div>
    <button className={LOP_NUT_PHU} type="button" onClick={() => setMo(true)}>
      {(duocRut || phongSuaDuoc.length > 0) ? 'Quản lý thành viên' : 'Xem thành viên'} <ChevronRight aria-hidden="true" size={16} />
    </button>
    {mo && <HopThoaiHoSo tieuDe={thaoTac ? TEN_THAO_TAC_THANH_VIEN[thaoTac.loai] : 'Thành viên nhóm'}
      Icon={Users} dangLuu={dangGui} onDong={dong}>
      <div className="space-y-4 text-sm">
        {thaoTac ? <>
          <button className={LOP_NUT_PHU} type="button" disabled={dangGui} onClick={() => setThaoTac(null)}>
            <ArrowLeft aria-hidden="true" size={16} /> Về danh sách
          </button>
          <form onSubmit={guiYeuCau}>
            <fieldset className="min-w-0 space-y-4" disabled={dangGui}>
              {thaoTac.nguoi ? <div className={LOP_THE}>
                <p className="font-semibold">{thaoTac.nguoi.hoTen}</p>
                <p className={LOP_CHU_PHU}>{phongBan.find((p) => p.phongBanId === phongChon)?.tenPhongBan}</p>
              </div> : <Truong ten="Phòng ban">
                <select className={TRUONG} value={phongChon} onChange={(event) => {
                  setThaoTac({ ...thaoTac, phongId: Number(event.target.value) })
                  setLoi('')
                }}>
                  {phongSuaDuoc.map((phong) => <option key={phong.phongBanId} value={phong.phongBanId}>{phong.tenPhongBan}</option>)}
                </select>
              </Truong>}
              {!dangRut && <ChonNguoi key={phongChon} ten="Thành viên mới" name="nguoiMoi" danhSach={nguoiThay} />}
              {!dangRut && !nguoiThay.length && <p className={LOP_CHU_PHU}>Phòng này chưa có nhân sự có thể bổ sung.</p>}
              <Truong ten="Lý do"><textarea name="lyDo" rows={3} required maxLength={500} className={TRUONG} /></Truong>
              <p className={LOP_CHU_PHU}>{dangRut ? 'Thành viên sẽ rời nhóm ngay khi bạn lưu.'
                : 'Danh sách thành viên được cập nhật sau khi Trưởng phòng liên quan xác nhận và Tổ trưởng đồng ý.'}</p>
              {loi && <p role="alert" className="text-rose-800 phong-toi:text-phong-do">{loi}</p>}
              <button className={LOP_NUT_CHINH} disabled={dangRut ? !duocRut : !nguoiThay.length} type="submit">
                {dangRut ? 'Rút khỏi nhóm' : 'Gửi đề xuất'}
              </button>
            </fieldset>
          </form>
        </> : <>
          {thongBao && <p role="status" className="rounded-lg bg-emerald-50 p-3 text-emerald-900 phong-toi:bg-phong-nhan-xanh phong-toi:text-phong-xanh">{thongBao}</p>}
          <div className="flex flex-wrap items-end justify-between gap-3">
            <Truong ten="Lọc theo phòng ban">
              <select className={TRUONG} value={phongLoc} onChange={(event) => setPhongLoc(event.target.value)}>
                <option value="">Tất cả phòng ban</option>
                {phongBan.map((phong) => <option key={phong.phongBanId} value={phong.phongBanId}>{phong.tenPhongBan}</option>)}
              </select>
            </Truong>
            {phongSuaDuoc.length > 0 && <button className={LOP_NUT_CHINH} type="button" onClick={() => chonThaoTac('them')}>
              Bổ sung thành viên
            </button>}
          </div>
          {phongHienThi.map((phong) => {
            const danhSach = thanhVien.filter((tv) => tv.phongBanId === phong.phongBanId)
            return <section key={phong.phongBanId} className={LOP_THE + ' space-y-2'}>
              <h4 className="font-semibold">{phong.tenPhongBan} <span className={LOP_CHU_PHU}>· {danhSach.length} người</span></h4>
              {danhSach.length === 0 && <p className={LOP_CHU_PHU}>Chưa cử thành viên.</p>}
              <ul className="divide-y divide-slate-200 phong-toi:divide-phong-vien">
                {danhSach.map((nguoi) => {
                  const chucDanh = nguoi.nguoiDungId === nhom.truongNhomId ? 'Tổ trưởng'
                    : nguoi.nguoiDungId === nhom.thuKyNhomId ? 'Thư ký nhóm' : null
                  const coYeuCau = chiTiet.doiNguoi.some((yc) =>
                    yc.nguoiCuId === nguoi.nguoiDungId && yc.trangThai === 'CHO_XU_LY')
                  return <li key={nguoi.nguoiDungId} className="flex flex-wrap items-center justify-between gap-2 py-2">
                    <div className="min-w-0">
                      <p className="break-words font-medium">{nguoi.hoTen}</p>
                      {chucDanh && <p className={LOP_CHU_PHU}>{chucDanh}</p>}
                      {coYeuCau && <p className={LOP_CHU_PHU}>Có yêu cầu chờ xử lý</p>}
                    </div>
                    {!chucDanh && <div className="flex flex-wrap gap-2">
                      {!coYeuCau && suaDuocPhong(phong.phongBanId) && (
                        <button className={LOP_NUT_PHU} type="button" onClick={() => chonThaoTac('thay', nguoi)}
                          aria-label={`Thay ${nguoi.hoTen}`}>Thay người</button>
                      )}
                      {duocRut && (danhSach.length > 1 ? (
                        <button className={LOP_NUT_PHU + ' text-rose-800 phong-toi:text-phong-do'}
                          type="button" onClick={() => chonThaoTac('rut', nguoi)}
                          aria-label={`Rút ${nguoi.hoTen} khỏi nhóm`}>Rút khỏi nhóm</button>
                      ) : <span className={LOP_CHU_PHU + ' self-center'}>Thành viên cuối của phòng</span>)}
                    </div>}
                  </li>
                })}
              </ul>
            </section>
          })}
          {phongSuaDuoc.length > 0 && <p className={LOP_CHU_PHU}>Để thay người đang giữ chức danh, mở hồ sơ Bầu lại chức danh.</p>}
          <button className={LOP_NUT_PHU} type="button" onClick={dong}>Đóng</button>
        </>}
      </div>
    </HopThoaiHoSo>}
  </section>
}

function DanhSachNhomDialog({ tieuDe, Icon, danhSach, dangGui, khoa, tenMuc, nhanTrangThai, moTa, children }) {
  const [muc, setMuc] = useState(null)
  const daChon = danhSach.find((banGhi) => khoa(banGhi) === muc)
  return <section className={LOP_THE + ' flex flex-wrap items-center justify-between gap-3'}>
    <div className="flex items-center gap-3">
      <Icon aria-hidden="true" size={20} className="text-blue-800 phong-toi:text-phong-xanh" />
      <div>
        <h3 className="font-semibold">{tieuDe}</h3>
        <p className={LOP_CHU_PHU}>{danhSach.length} hồ sơ</p>
      </div>
    </div>
    <button type="button" className={LOP_NUT_PHU} onClick={() => setMuc('danh-sach')}>
      Mở danh sách <ChevronRight aria-hidden="true" size={16} />
    </button>
    {muc != null && <HopThoaiHoSo tieuDe={tieuDe} Icon={Icon} dangLuu={dangGui} onDong={() => setMuc(null)}>
      <div className="space-y-4 text-sm">
        <div className="flex flex-wrap items-center justify-between gap-2">
          {daChon ? <button type="button" className={LOP_NUT_PHU} disabled={dangGui} onClick={() => setMuc('danh-sach')}>
            <ArrowLeft aria-hidden="true" size={16} /> Về danh sách
          </button> : <span />}
          <button type="button" className={LOP_NUT_PHU} disabled={dangGui} onClick={() => setMuc(null)}>
            <X aria-hidden="true" size={16} /> Đóng
          </button>
        </div>
        {daChon ? (
          <div className="space-y-4">
            <div className="flex flex-wrap items-center justify-between gap-2">
              <h4 className="break-words font-semibold">{tenMuc(daChon)}</h4>
              {nhanTrangThai(daChon)}
            </div>
            {children(daChon)}
          </div>
        ) : danhSach.length ? (
          <ul className="space-y-2">
            {danhSach.map((banGhi) => (
              <li key={khoa(banGhi)}>
                <button type="button" className={LOP_NUT_PHU + ' w-full justify-between text-left'}
                  onClick={() => setMuc(khoa(banGhi))}>
                  <span className="min-w-0">
                    <span className="block break-words">{tenMuc(banGhi)}</span>
                    <span className={LOP_CHU_PHU + ' block font-normal'}>{moTa(banGhi)}</span>
                  </span>
                  <span className="flex shrink-0 items-center gap-1.5">{nhanTrangThai(banGhi)}
                    <ChevronRight aria-hidden="true" size={16} />
                  </span>
                </button>
              </li>
            ))}
          </ul>
        ) : <p className={LOP_CHU_PHU}>Chưa có hồ sơ.</p>}
      </div>
    </HopThoaiHoSo>}
  </section>
}

function TrangThaiDoiNguoi({ muc }) {
  const dangCho = muc.trangThai === 'CHO_XU_LY'
  const nhan = dangCho
    ? (muc.nguoiMoiId == null || muc.truongPhongXacNhanId) ? 'Chờ Tổ trưởng xử lý' : 'Chờ Trưởng phòng xác nhận'
    : TEN_DOI_NGUOI[muc.trangThai] || muc.trangThai
  const mau = dangCho ? 'bg-amber-50 text-amber-900 phong-toi:bg-phong-nhan-vang phong-toi:text-phong-vang'
    : muc.trangThai === 'DA_CAP_NHAT' ? 'bg-emerald-50 text-emerald-900 phong-toi:bg-phong-nhan-xanh phong-toi:text-phong-xanh'
      : 'bg-slate-100 text-slate-700 phong-toi:bg-phong-mat-noi phong-toi:text-phong-chu-phu'
  return <span className={mau + ' rounded-full px-2.5 py-1 text-xs font-semibold'}>{nhan}</span>
}

function ChiTietDoiNhanSu({ muc, ten, tenPhong, duocXuLy, dangGui, gui }) {
  const [loi, setLoi] = useState('')
  const [thongBao, setThongBao] = useState('')
  async function xuLy(event) {
    event.preventDefault()
    if (dangGui) return
    const phieu = new FormData(event.currentTarget, event.nativeEvent.submitter)
    const dongY = phieu.get('quyetDinh') === 'dong-y'
    const yKien = phieu.get('yKien').trim()
    setLoi('')
    setThongBao('')
    if (!dongY && !yKien) {
      setLoi('Nhập lý do từ chối trước khi gửi.')
      return
    }
    try {
      await gui(`/doi-nguoi/${muc.doiNguoiId}`, 'PUT', { dongY, yKien })
      setThongBao(dongY ? 'Đã ghi nhận xác nhận của bạn.' : 'Đã từ chối yêu cầu.')
    } catch (error) {
      setLoi(error.message)
    }
  }

  return <div className="space-y-4">
    <dl className={LOP_THE + ' grid gap-3 sm:grid-cols-2'}>
      <div><dt className={LOP_CHU_PHU}>Phòng ban</dt><dd>{tenPhong}</dd></div>
      <div><dt className={LOP_CHU_PHU}>Người gửi yêu cầu</dt><dd>{muc.tenNguoiYeuCau || ten(muc.nguoiYeuCauId)}</dd></div>
      {muc.nguoiCuId != null && <div><dt className={LOP_CHU_PHU}>Người rời nhóm</dt><dd>{muc.tenNguoiCu || ten(muc.nguoiCuId)}</dd></div>}
      {muc.nguoiMoiId != null && <div><dt className={LOP_CHU_PHU}>Người bổ sung</dt><dd>{muc.tenNguoiMoi || ten(muc.nguoiMoiId)}</dd></div>}
      <div className="sm:col-span-2"><dt className={LOP_CHU_PHU}>Lý do</dt><dd className="whitespace-pre-wrap break-words">{muc.lyDo}</dd></div>
    </dl>
    {loi && <p role="alert" className="text-rose-800 phong-toi:text-phong-do">{loi}</p>}
    {thongBao && <p role="status" className={LOP_CHU_PHU}>{thongBao}</p>}
    {duocXuLy && !thongBao && <form onSubmit={xuLy} className="space-y-3">
      <Truong ten="Ý kiến xử lý">
        <textarea name="yKien" maxLength={500} rows={2} placeholder="Nhập lý do nếu từ chối" className={TRUONG} />
      </Truong>
      <div className="flex flex-wrap gap-2">
        <button className={LOP_NUT_CHINH} disabled={dangGui} type="submit" name="quyetDinh" value="dong-y">
          {muc.nguoiMoiId == null ? 'Rút khỏi nhóm' : muc.truongPhongXacNhanId ? 'Đồng ý cập nhật' : 'Xác nhận thay đổi'}
        </button>
        <button className={LOP_NUT_PHU} disabled={dangGui} type="submit" name="quyetDinh" value="tu-choi">Từ chối</button>
      </div>
    </form>}
  </div>
}

function ChiTietNhom({ chiTiet, quyen, dangGui, gui, laLanhDao, token, nguoiDungId, onMoHoSo }) {
  const { nhom, phongBan, thanhVien, nguonNhanSu, doiNguoi, deXuat } = chiTiet
  const conCuNguoi = ['CHO_CU_NHAN_SU', 'CHO_KICH_HOAT'].includes(nhom.trangThai)
  const phongCuaToi = phongBan.find((p) => p.phongBanId === quyen.phongTruongPhong)
  const dsCu = thanhVien.filter((muc) => muc.phongBanId === phongCuaToi?.phongBanId)
  const ten = (nguoiId) => [...thanhVien, ...nguonNhanSu].find((muc) => muc.nguoiDungId === nguoiId)?.hoTen
    ?? (nguoiId == null ? '' : `Nhân sự #${nguoiId}`)
  return <div className="space-y-4">
    <section className={KHUNG}>
      <h2 className="text-lg font-bold">{nhom.tenNhom}</h2>
      <p className="text-sm">{nhom.mucDich}</p>
      <p className="text-sm font-semibold">{TRANG_THAI[nhom.trangThai]}</p>
      <ul className="grid gap-2 sm:grid-cols-2">
        {phongBan.map((p) => <li key={p.phongBanId} className="rounded-lg bg-slate-50 p-3 text-sm">
          <strong>{p.tenPhongBan}</strong>{p.phongBanId === nhom.phongBanChuTriId && ' · Chủ trì'}
          <ul className="mt-1 space-y-1">
            {thanhVien.filter((v) => v.phongBanId === p.phongBanId).map((v) => <li key={v.nguoiDungId}>
              {v.hoTen}{v.nguoiDungId === nhom.truongNhomId && ' · Tổ trưởng'}
              {v.nguoiDungId === nhom.thuKyNhomId && ' · Thư ký nhóm'}
            </li>)}
            {!p.ngayCu && <li>Chưa cử người</li>}
          </ul>
        </li>)}
      </ul>
    </section>

    <QuanLyThanhVien chiTiet={chiTiet} quyen={quyen} nguoiDungId={nguoiDungId} dangGui={dangGui} gui={gui} />

    {conCuNguoi && phongCuaToi && <Phieu key={dsCu.map((muc) => muc.nguoiDungId).join(',')} tieuDe="Cử nhân sự của phòng"
      nut="Lưu danh sách cử" dangGui={dangGui}
      onGui={(f) => gui(`/phong-ban/${phongCuaToi.phongBanId}/cu-nguoi`, 'PUT', { nguoiDungIds: f.getAll('nhanSu').map(Number) })}>
      <div className="grid gap-1 sm:grid-cols-2">
        {nguonNhanSu.filter((v) => v.phongBanId === phongCuaToi.phongBanId).map((v) => <label key={v.nguoiDungId} className="flex gap-2 text-sm">
          <input type="checkbox" name="nhanSu" value={v.nguoiDungId} defaultChecked={dsCu.some((muc) => muc.nguoiDungId === v.nguoiDungId)} />{v.hoTen}
        </label>)}
      </div>
    </Phieu>}

    {chiTiet.duocKichHoat && <Phieu tieuDe="Kích hoạt nhóm" nut="Kích hoạt" dangGui={dangGui}
      onGui={(f) => gui('/kich-hoat', 'POST', { truongNhomId: Number(f.get('truong')), thuKyNhomId: Number(f.get('thuKy')) })}>
      <ChonNguoi ten="Tổ trưởng (chọn trưởng phòng đã được cử)" name="truong" danhSach={thanhVien} />
      <ChonNguoi ten="Thư ký nhóm" name="thuKy" danhSach={thanhVien} />
    </Phieu>}

    {['HOAT_DONG', 'DA_DONG'].includes(nhom.trangThai) && (
      <BauLaiChucDanhNhom cuocHopTrongNhom={deXuat} nhomId={nhom.nhomId} nguoiDungId={nguoiDungId} token={token} />
    )}

    {doiNguoi.length > 0 && <DanhSachNhomDialog tieuDe="Yêu cầu đổi nhân sự" Icon={Users}
      danhSach={doiNguoi} dangGui={dangGui} khoa={(muc) => muc.doiNguoiId}
      tenMuc={(muc) => {
        const cu = muc.tenNguoiCu || ten(muc.nguoiCuId)
        const moi = muc.tenNguoiMoi || ten(muc.nguoiMoiId)
        if (muc.nguoiCuId == null) return `Bổ sung ${moi}`
        if (muc.nguoiMoiId == null) return `Rút ${cu} khỏi nhóm`
        return `${cu} → ${moi}`
      }}
      nhanTrangThai={(muc) => <TrangThaiDoiNguoi muc={muc} />}
      moTa={(muc) => phongBan.find((p) => p.phongBanId === muc.phongBanId)?.tenPhongBan}>
      {(muc) => <ChiTietDoiNhanSu key={muc.doiNguoiId} muc={muc} ten={ten}
        tenPhong={phongBan.find((p) => p.phongBanId === muc.phongBanId)?.tenPhongBan}
        duocXuLy={muc.duocXuLy && nhom.trangThai === 'HOAT_DONG'} dangGui={dangGui} gui={gui} />}
    </DanhSachNhomDialog>}

    {!laLanhDao && chiTiet.duocGui && <DeXuatNhom chiTiet={chiTiet} dangGui={dangGui} gui={gui} />}
    {deXuat.length > 0 && <DanhSachNhomDialog tieuDe="Đề xuất họp đã gửi" Icon={FileText}
      danhSach={deXuat} khoa={(muc) => muc.cuocHopId} tenMuc={(muc) => muc.tieuDe}
      nhanTrangThai={(muc) => <NhanTrangThai trangThai={layNhanTrangThaiHoSo(muc)} />}
      moTa={(muc) => muc.tenNguoiDuyet || TEN_CAP_DUYET[muc.capDuyet]}>
      {(muc) => <div className="space-y-3">
        <dl className={LOP_THE + ' grid gap-3 sm:grid-cols-2'}>
          <div><dt className={LOP_CHU_PHU}>Người gửi đề xuất</dt><dd>{ten(muc.nguoiGuiId)}</dd></div>
          <div><dt className={LOP_CHU_PHU}>Cấp duyệt</dt><dd>{TEN_CAP_DUYET[muc.capDuyet] || 'Chưa xác định'}</dd></div>
          <div><dt className={LOP_CHU_PHU}>Người được chỉ định duyệt</dt><dd>{muc.tenNguoiDuyet || 'Chưa phân công'}</dd></div>
        </dl>
        {chiTiet.hoSoCoQuyenXem?.includes(muc.cuocHopId) && (
          <button type="button" className={LOP_NUT_CHINH} onClick={() => onMoHoSo(muc)}>Mở hồ sơ cuộc họp</button>
        )}
      </div>}
    </DanhSachNhomDialog>}

    {chiTiet.duocDong && <Phieu tieuDe="Đóng nhóm" nut="Đóng nhóm" dangGui={dangGui}
        onGui={(f) => gui('/dong', 'POST', { lyDo: f.get('lyDo') })}>
        <Truong ten="Lý do đóng"><input name="lyDo" maxLength={500} required className={TRUONG} /></Truong>
      </Phieu>}
  </div>
}

export default function NhomLienPhong({ token, nguoiDung, onMoMuc, onLapLich, onMoPhongHop }) {
  const phienDongBo = useDongBoDuLieu()
  const laLanhDao = nguoiDung.maVaiTro === 'LANH_DAO'
  const [trang, setTrang] = useState(null)
  const [chiTiet, setChiTiet] = useState(null)
  const [nhomId, setNhomId] = useState(null)
  const [hoSoDangXem, setHoSoDangXem] = useState(null)
  const [muc, setMuc] = useState('nhom')
  const [lanTai, setLanTai] = useState(0)
  const [loi, setLoi] = useState('')
  const [thongBao, setThongBao] = useState('')
  const [dangGui, setDangGui] = useState(false)

  useEffect(() => {
    const controller = new AbortController()
    const tuyChon = { signal: controller.signal }
    Promise.all([
      goiNhomLienPhong('', token, tuyChon),
      nhomId == null ? null : goiNhomLienPhong(`/${nhomId}`, token, tuyChon),
    ]).then(([duLieu, chiTietMoi]) => {
      if (controller.signal.aborted) return
      setTrang(duLieu)
      setChiTiet(chiTietMoi)
      setLoi('')
    }).catch((error) => {
      if (!controller.signal.aborted) {
        if (error.status === 403 || error.status === 404) setChiTiet(null)
        setLoi(error.message)
      }
    })
    return () => controller.abort()
  }, [nhomId, token, lanTai, phienDongBo])

  function chonNhom(id) {
    setChiTiet(null)
    setNhomId(id)
    setMuc('nhom')
    setLoi('')
    setThongBao('')
  }

  async function gui(duongDan, method, duLieu) {
    setDangGui(true)
    setLoi('')
    setThongBao('')
    try {
      const goc = muc === 'tao' ? '' : `/${nhomId}`
      const ketQua = await goiNhomLienPhong(goc + duongDan, token, { method, duLieu })
      if (ketQua?.nhomId) chonNhom(ketQua.nhomId)
      setThongBao(ketQua?.cuocHopId ? 'Đã gửi đề xuất tới thư ký nhóm.'
        : method === 'POST' && duongDan === '/doi-nguoi' && duLieu.nguoiMoiId != null
          ? 'Đã gửi yêu cầu thay đổi nhân sự.' : 'Đã cập nhật nhóm.')
      setLanTai((lan) => lan + 1)
      return true
    } finally {
      setDangGui(false)
    }
  }

  if (hoSoDangXem) {
    return <ChiTietDeXuat cuocHopId={hoSoDangXem.cuocHopId} token={token} nguoiDung={nguoiDung}
      tabBanDau={dangPheDuyetKeHoach(hoSoDangXem) ? 'ke-hoach' : 'thong-tin'}
      onMoPhongHop={onMoPhongHop} onLapLich={onLapLich} onQuayLai={() => {
        setHoSoDangXem(null)
        setLanTai((lan) => lan + 1)
      }} />
  }

  return <div className="space-y-4">
    <header className="flex flex-wrap items-center justify-between gap-3">
      <h1 className="text-xl font-bold">Nhóm liên phòng</h1>
      <div className="flex flex-wrap gap-2">
        {muc !== 'nhom' && <button className={NUT} onClick={() => chonNhom(null)}>Danh sách nhóm</button>}
        {trang?.quyen && (trang.quyen.lanhDao || trang.quyen.phongTruongPhong != null) &&
          <button className={NUT} onClick={() => { chonNhom(null); setMuc('tao') }}>Lập nhóm</button>}
        {laLanhDao && <button className={NUT} onClick={() => onMoMuc('de-xuat-cua-toi')}>
          Đề xuất họp
        </button>}
        <button className={NUT} onClick={() => setLanTai((lan) => lan + 1)}>Làm mới</button>
      </div>
    </header>
    {loi && <p role="alert" className="rounded-lg bg-rose-50 p-3 text-sm text-rose-800">{loi}</p>}
    {thongBao && <p role="status" className="rounded-lg bg-emerald-50 p-3 text-sm text-emerald-900">{thongBao}</p>}
    {!trang && !loi && <p role="status">Đang tải nhóm…</p>}
    {muc === 'tao' && trang && <TaoNhom duLieu={trang} gui={gui} dangGui={dangGui} onDong={() => setMuc('nhom')} />}
    {muc === 'nhom' && trang && <div className="grid items-start gap-4 lg:grid-cols-[minmax(14rem,1fr)_minmax(0,3fr)]">
      <section className={KHUNG}>
        <h2 className="font-semibold">Danh sách nhóm</h2>
        {!trang.danhSach.length && <p className="text-sm text-slate-600">Bạn chưa có nhóm liên phòng.</p>}
        <ul className="space-y-2">{trang.danhSach.map((nhom) => <li key={nhom.nhomId}>
          <button type="button" className={NUT + ' w-full text-left'} aria-pressed={nhomId === nhom.nhomId}
            onClick={() => chonNhom(nhom.nhomId)}>
            {nhom.tenNhom}
            <span className="mt-1 block text-xs font-normal">{TRANG_THAI[nhom.trangThai]}</span>
          </button>
        </li>)}</ul>
      </section>
      {nhomId && !chiTiet && !loi && <p role="status">Đang tải chi tiết nhóm…</p>}
      {chiTiet && <ChiTietNhom key={nhomId} chiTiet={chiTiet} quyen={trang.quyen} dangGui={dangGui}
        gui={gui} laLanhDao={laLanhDao} token={token} nguoiDungId={nguoiDung.nguoiDungId} onMoHoSo={setHoSoDangXem} />}
    </div>}
  </div>
}



