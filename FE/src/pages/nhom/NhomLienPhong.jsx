import { useEffect, useState } from 'react'
import { goiNhomLienPhong } from '../../api/nhomLienPhongApi.js'
import { useDongBoDuLieu } from '../../services/useDongBoDuLieu.js'
import FileText from 'lucide-react/dist/esm/icons/file-text.mjs'
import { HopThoaiHoSo } from '../../components/GiaoDienPhongHop.jsx'

const TRANG_THAI = {
  CHO_CU_NHAN_SU: 'Chờ các phòng cử người',
  CHO_KICH_HOAT: 'Chờ kích hoạt',
  HOAT_DONG: 'Đang hoạt động',
  DA_DONG: 'Đã đóng',
}
const TEN_CAP_DUYET = { TRUONG_NHOM: 'Tổ trưởng', LANH_DAO: 'Chủ tịch hoặc Phó Chủ tịch phụ trách', VAN_PHONG: 'Văn phòng (luồng cũ)' }
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
  const canChuTich = mucDichHop !== 'HOP_THUONG'
  const canLanhDao = canChuTich || coKhachNgoai || moiThem.length > 0
  const lanhDao = chiTiet.lanhDaoDuyet.filter((nguoi) => !canChuTich || nguoi.chuTich)
  const tenToTruong = chiTiet.thanhVien.find((nguoi) => nguoi.nguoiDungId === chiTiet.nhom.truongNhomId)?.hoTen
  const tenThuKy = chiTiet.thanhVien.find((nguoi) => nguoi.nguoiDungId === chiTiet.nhom.thuKyNhomId)?.hoTen
  const thieuLanhDao = canLanhDao && !lanhDao.length
  const nhanSuHienThi = chiTiet.nguoiMoiThem.filter((nguoi) => moiThem.includes(nguoi.nguoiDungId)
    || nguoi.hoTen.toLocaleLowerCase('vi').includes(tuKhoa.trim().toLocaleLowerCase('vi')))
  return <Phieu tieuDe="Gửi đề xuất họp từ nhóm" nut={thieuLanhDao ? null : 'Gửi đề xuất'} dangGui={dangGui}
    onGui={(f) => !thieuLanhDao && gui('/de-xuat', 'POST', {
      tieuDe: f.get('tieuDe'), mucTieu: f.get('mucTieu'), noiDung: f.get('noiDung'), loaiHinh: f.get('loaiHinh'),
      mucDichHop, coKhachNgoai, nguoiMoiThemIds: moiThem,
      emailKhachNgoai: (f.get('emailKhachNgoai') || '').split(/[,\s]+/).filter(Boolean),
      lanhDaoDuyetId: Number(f.get('lanhDaoDuyetId')) || null,
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
    {canLanhDao && <Truong ten={canChuTich ? 'Chủ tịch duyệt' : 'Chủ tịch hoặc Phó Chủ tịch phụ trách duyệt'}>
      <select key={String(canChuTich)} name="lanhDaoDuyetId" className={TRUONG} required
        defaultValue={lanhDao.length === 1 ? lanhDao[0].nguoiDungId : ''}>
        <option value="">Chọn người duyệt</option>
        {lanhDao.map((nguoi) => <option key={nguoi.nguoiDungId} value={nguoi.nguoiDungId}>{nguoi.hoTen} · {nguoi.chucVu}</option>)}
      </select>
    </Truong>}
    <p className="text-sm text-slate-700">Thư ký lập kế hoạch và tổ chức · {tenThuKy || 'Thư ký nhóm'}</p>
    <p className="text-sm text-slate-700">{canLanhDao
      ? 'Văn phòng sẽ rà soát hồ sơ và trình người duyệt đã chọn.'
      : `Tổ trưởng ${tenToTruong || ''} duyệt. Văn phòng kiểm tra lịch phòng nếu dùng phòng họp chung.`}</p>
    {thieuLanhDao && <p role="alert" className="text-sm text-amber-800">Chưa có tài khoản lãnh đạo đúng chức vụ để nhận hồ sơ này.</p>}
  </Phieu>
}

function DoiNguoi({ chiTiet, quyen, dangGui, gui }) {
  const phongBan = chiTiet.phongBan.filter((p) => chiTiet.duocGui || p.phongBanId === quyen.phongTruongPhong)
  const [phongId, setPhongId] = useState(phongBan.find((p) => p.phongBanId === quyen.phongTruongPhong)?.phongBanId ?? phongBan[0]?.phongBanId)
  if (!phongBan.length) return null
  const tvCu = chiTiet.thanhVien.filter((v) => v.phongBanId === phongId)
  const tvMoi = chiTiet.nguonNhanSu.filter((v) => v.phongBanId === phongId &&
    !chiTiet.thanhVien.some((tv) => tv.nguoiDungId === v.nguoiDungId))
  return <Phieu tieuDe="Đề xuất thêm hoặc đổi người" nut="Gửi đề xuất"
    dangGui={dangGui} onGui={(f) => gui('/doi-nguoi', 'POST', {
      phongBanId: phongId, nguoiCuId: Number(f.get('cu')) || null, nguoiMoiId: Number(f.get('moi')) || null, lyDo: f.get('lyDo'),
    })}>
    <Truong ten="Phòng ban"><select value={phongId} onChange={(e) => setPhongId(Number(e.target.value))} className={TRUONG}>
      {phongBan.map((p) => <option key={p.phongBanId} value={p.phongBanId}>{p.tenPhongBan}</option>)}
    </select></Truong>
    <div className="grid gap-3 sm:grid-cols-2">
      <ChonNguoi key={`cu-${phongId}`} ten="Người rời nhóm" name="cu" danhSach={tvCu} batBuoc={false} />
      <ChonNguoi key={`moi-${phongId}`} ten="Người bổ sung" name="moi" danhSach={tvMoi} batBuoc={false} />
    </div>
    <Truong ten="Lý do"><input name="lyDo" maxLength={500} required className={TRUONG} /></Truong>
  </Phieu>
}

function ChiTietNhom({ chiTiet, quyen, dangGui, gui, laLanhDao }) {
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

    {nhom.trangThai === 'HOAT_DONG' && <DoiNguoi chiTiet={chiTiet} quyen={quyen} dangGui={dangGui} gui={gui} />}
    {doiNguoi.length > 0 && <details className={KHUNG}>
      <summary className="cursor-pointer font-semibold">Yêu cầu đổi nhân sự ({doiNguoi.length})</summary>
      {doiNguoi.map((muc) => <article key={muc.doiNguoiId} className="space-y-2 border-t border-slate-100 pt-3 text-sm">
        <p className="font-medium">{[ten(muc.nguoiCuId), ten(muc.nguoiMoiId)].filter(Boolean).join(' → ')} · {(muc.trangThai === 'CHO_XU_LY'
          ? muc.truongPhongXacNhanId ? 'Chờ Tổ trưởng xử lý' : 'Chờ Trưởng phòng xác nhận'
          : TEN_DOI_NGUOI[muc.trangThai])}</p>
        <p>{muc.lyDo}</p>
        {muc.duocXuLy && nhom.trangThai === 'HOAT_DONG' && <Phieu tieuDe={muc.truongPhongXacNhanId ? 'Xử lý yêu cầu' : 'Xác nhận thay đổi nhân sự'} dangGui={dangGui}
          onGui={(f) => gui(`/doi-nguoi/${muc.doiNguoiId}`, 'PUT', { dongY: f.get('quyetDinh') === 'dong-y', yKien: f.get('yKien') })}>
          <Truong ten="Ý kiến xử lý"><input name="yKien" maxLength={500} placeholder="Nhập lý do nếu từ chối" className={TRUONG} /></Truong>
          <div className="flex gap-2">
            <button className={XAC_NHAN} type="submit" name="quyetDinh" value="dong-y">Đồng ý</button>
            <button className={NUT} type="submit" name="quyetDinh" value="tu-choi">Từ chối</button>
          </div>
        </Phieu>}
      </article>)}
    </details>}

    {!laLanhDao && chiTiet.duocGui && <DeXuatNhom chiTiet={chiTiet} dangGui={dangGui} gui={gui} />}
    {deXuat.length > 0 && <details className={KHUNG}>
      <summary className="cursor-pointer font-semibold">Đề xuất họp đã gửi ({deXuat.length})</summary>
      <ul className="space-y-2 text-sm">
        {deXuat.map((muc) => <li key={muc.cuocHopId}>
          <strong>{muc.tieuDe}</strong> · {TEN_CAP_DUYET[muc.capDuyet]}{muc.tenNguoiDuyet && ` · ${muc.tenNguoiDuyet}`}
        </li>)}
      </ul>
    </details>}

    {chiTiet.duocDong && <Phieu tieuDe="Đóng nhóm" nut="Đóng nhóm" dangGui={dangGui}
        onGui={(f) => gui('/dong', 'POST', { lyDo: f.get('lyDo') })}>
        <Truong ten="Lý do đóng"><input name="lyDo" maxLength={500} required className={TRUONG} /></Truong>
      </Phieu>}
  </div>
}

export default function NhomLienPhong({ token, nguoiDung, onMoMuc }) {
  const phienDongBo = useDongBoDuLieu()
  const laLanhDao = nguoiDung.maVaiTro === 'LANH_DAO'
  const [trang, setTrang] = useState(null)
  const [chiTiet, setChiTiet] = useState(null)
  const [nhomId, setNhomId] = useState(null)
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
      setThongBao(ketQua?.cuocHopId ? 'Đã gửi đề xuất tới thư ký nhóm.' : 'Đã cập nhật nhóm.')
      setLanTai((lan) => lan + 1)
      return true
    } finally {
      setDangGui(false)
    }
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
        gui={gui} laLanhDao={laLanhDao} />}
    </div>}
  </div>
}
