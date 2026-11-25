import { useEffect, useRef, useState } from 'react'
import { useDongBoDuLieu } from '../../services/useDongBoDuLieu.js'
import FileText from 'lucide-react/dist/esm/icons/file-text.mjs'
import ArrowLeft from 'lucide-react/dist/esm/icons/arrow-left.mjs'
import CalendarDays from 'lucide-react/dist/esm/icons/calendar-days.mjs'
import ListChecks from 'lucide-react/dist/esm/icons/list-checks.mjs'
import Users from 'lucide-react/dist/esm/icons/users.mjs'
import Send from 'lucide-react/dist/esm/icons/send.mjs'
import Paperclip from 'lucide-react/dist/esm/icons/paperclip.mjs'
import { TieuDeHoSo } from '../../components/GiaoDienPhongHop.jsx'
import { LOP_NUT_CHINH, LOP_NUT_PHU } from '../../components/GiaoDienPhongHop.js'
import DanhSachDeXuatCuaToi from './DanhSachDeXuatCuaToi.jsx'
import { TEN_PHAM_VI } from '../thuky/hienThiDeXuat.js'
import { LOP_KHOI, LOP_TRUONG } from '../../components/lopBieuMau.js'
import {
  guiDeXuatCuocHop,
  layDanhSachDeXuat,
  layPhongBanChuTriCoTheChon,
  layThuKyCoTheDeXuat,
  taoDeXuatCuocHop,
} from '../../api/cuocHopApi.js'

const DINH_DANG_TAI_LIEU = /\.(pdf|docx?|xlsx?)$/i
const DUNG_LUONG_TOI_DA_TEP = 10 * 1024 * 1024

const LOP_NHOM = 'space-y-4 rounded-2xl border border-slate-200 bg-white p-4 sm:p-5'

export default function DeXuatCuaToi({ token, nguoiDung, onXemCuocHop }) {
  const phienDongBo = useDongBoDuLieu()
  const [danhSach, setDanhSach] = useState(null)
  const [lanTai, setLanTai] = useState(0)
  const [moForm, setMoForm] = useState(false)
  const [lanTao, setLanTao] = useState(0)
  const tieuDeForm = useRef(null)
  const nutTao = useRef(null)
  const daMoForm = useRef(false)
  const [loiTai, setLoiTai] = useState('')
  const [loi, setLoi] = useState('')
  const [thongBao, setThongBao] = useState('')
  const [dangGui, setDangGui] = useState(false)
  const [tepDeXuat, setTepDeXuat] = useState([])
  const [loaiHinh, setLoaiHinh] = useState('TRUC_TIEP')
  const [soKhungGio, setSoKhungGio] = useState(1)
  const [phamVi, setPhamVi] = useState('NOI_BO')
  const [luaChonThuKy, setLuaChonThuKy] = useState(null)
  const [loiTaiThuKy, setLoiTaiThuKy] = useState('')
  const [danhSachPhongBan, setDanhSachPhongBan] = useState(null)
  const [loiTaiPhongBan, setLoiTaiPhongBan] = useState('')
  const [lanTaiPhongBan, setLanTaiPhongBan] = useState(0)
  const lanhDao = nguoiDung.maVaiTro === 'LANH_DAO'
  const coThuKy = Boolean(nguoiDung.tenThuKyMacDinh)
  const noiBo = phamVi === 'NOI_BO'
  const chuaCoPhongBan = noiBo && (lanhDao ? !danhSachPhongBan?.length : !nguoiDung.phongBanId)
  const phongBanMacDinh = danhSachPhongBan?.find((phong) => Number(phong.phongBanId) === Number(nguoiDung.phongBanId))

  useEffect(() => {
    const controller = new AbortController()
    layDanhSachDeXuat(token, controller.signal)
      .then((duLieu) => {
        if (controller.signal.aborted) return
        setDanhSach(duLieu.filter((muc) => muc.nguoiDeXuatId === nguoiDung.nguoiDungId))
        setLoiTai('')
      })
      .catch((error) => { if (!controller.signal.aborted) setLoiTai(error.message) })
    return () => controller.abort()
  }, [token, nguoiDung.nguoiDungId, lanTai, phienDongBo])

  useEffect(() => {
    const controller = new AbortController()
    layThuKyCoTheDeXuat(token, controller.signal)
      .then((duLieu) => {
        if (!controller.signal.aborted) {
          setLuaChonThuKy(duLieu)
          setLoiTaiThuKy('')
        }
      })
      .catch((error) => { if (!controller.signal.aborted) setLoiTaiThuKy(error.message) })
    return () => controller.abort()
  }, [token, phienDongBo])

  useEffect(() => {
    if (!lanhDao || !noiBo) return
    const controller = new AbortController()
    layPhongBanChuTriCoTheChon(token, controller.signal)
      .then((duLieu) => {
        if (controller.signal.aborted) return
        setDanhSachPhongBan(duLieu)
        setLoiTaiPhongBan('')
      })
      .catch((error) => {
        if (controller.signal.aborted) return
        setDanhSachPhongBan(null)
        setLoiTaiPhongBan(error.message)
      })
    return () => controller.abort()
  }, [token, lanhDao, noiBo, lanTaiPhongBan])

  useEffect(() => {
    if (moForm) {
      daMoForm.current = true
      tieuDeForm.current?.focus()
    } else if (daMoForm.current) {
      nutTao.current?.focus()
    }
  }, [moForm])

  function chuyenManHinh(hienForm) {
    setLoi('')
    setThongBao('')
    setMoForm(hienForm)
    window.scrollTo({ top: 0, behavior: 'instant' })
  }

  async function xuLyDeXuat(thaoTac) {
    if (dangGui) return
    setDangGui(true)
    setLoi('')
    setThongBao('')
    try {
      const ketQua = await thaoTac()
      setThongBao(ketQua.message)
      setLanTai((lan) => lan + 1)
    } catch (error) {
      setLoi(error.message)
    } finally {
      setDangGui(false)
    }
  }

  function chonTepDeXuat(event) {
    const input = event.currentTarget
    const tepMoi = Array.from(input.files)
    input.value = ''
    setLoi('')
    if (tepMoi.some((tep) => !DINH_DANG_TAI_LIEU.test(tep.name)
      || tep.size === 0 || tep.size > DUNG_LUONG_TOI_DA_TEP)) {
      setLoi('Chọn tệp PDF, Word hoặc Excel có nội dung, tối đa 10 MB mỗi tệp.')
      return
    }
    setTepDeXuat((hienTai) => hienTai.concat(tepMoi.filter((tep) => !hienTai.some((muc) =>
      muc.name === tep.name && muc.size === tep.size && muc.lastModified === tep.lastModified))))
  }

  function taoDeXuat(event) {
    event.preventDefault()
    const form = event.currentTarget
    const duLieu = new FormData(form)
    const guiNgay = event.nativeEvent.submitter?.value === 'gui'
    if (chuaCoPhongBan || (guiNgay && !coThuKy)) return
    const doc = (ten) => duLieu.get(ten)?.trim() || null
    xuLyDeXuat(async () => {
      const ketQua = await taoDeXuatCuocHop({
        tieuDe: duLieu.get('tieuDe').trim(),
        mucTieu: duLieu.get('mucTieu').trim(),
        noiDungDuKien: duLieu.get('noiDungDuKien').trim(),
        thoiLuongDuKienPhut: Number(duLieu.get('thoiLuongDuKienPhut')) || null,
        loaiHinh,
        thongTinDeXuat: {
          ketQuaKyVong: doc('ketQuaKyVong'),
          khungGioDeXuat: [...new Set(duLieu.getAll('khungGioDeXuat').filter(Boolean))],
          diaDiemDeXuat: doc('diaDiemDeXuat'),
          nenTangTrucTuyen: doc('nenTangTrucTuyen'),
          lienKetTrucTuyen: doc('lienKetTrucTuyen'),
          huongDanTrucTuyen: doc('huongDanTrucTuyen'),
          thanhPhanBatBuoc: doc('thanhPhanBatBuoc'),
          thanhPhanTuyChon: doc('thanhPhanTuyChon'),
          yeuCauChuanBi: doc('yeuCauChuanBi'),
          lienKetTaiLieu: doc('lienKetTaiLieu'),
        },
        phamViCuocHop: phamVi,
        phongBanChuTriId: noiBo
          ? Number(lanhDao ? duLieu.get('phongBanChuTriId') : nguoiDung.phongBanId) || null
          : null,
        coKhachMoiBenNgoai: phamVi === 'BEN_NGOAI' || duLieu.has('coKhachMoiBenNgoai'),
        thuKyCuocHopId: Number(duLieu.get('thuKyCuocHopId')) || null,
        guiNgay,
      }, token, tepDeXuat)
      form.reset()
      setTepDeXuat([])
      setSoKhungGio(1)
      setLoaiHinh('TRUC_TIEP')
      setPhamVi('NOI_BO')
      setLanTao((lan) => lan + 1)
      chuyenManHinh(false)
      return ketQua
    })
  }

  return (
    <div className="mx-auto max-w-6xl">
      <div hidden={moForm}>
        <DanhSachDeXuatCuaToi key={lanTao} danhSach={danhSach} loiTai={loiTai} loi={loi}
          thongBao={thongBao} dangGui={dangGui} coThuKy={coThuKy} nutTao={nutTao}
          onTao={() => chuyenManHinh(true)} onTaiLai={() => setLanTai((lan) => lan + 1)}
          onXemCuocHop={onXemCuocHop}
          onGui={(id) => xuLyDeXuat(() => guiDeXuatCuocHop(id, token))} />
      </div>
      <form className="space-y-4" hidden={!moForm} onSubmit={taoDeXuat}>
        <div className={LOP_KHOI}>
          <button type="button" className={LOP_NUT_PHU} disabled={dangGui} onClick={() => chuyenManHinh(false)}>
            <ArrowLeft size={16} aria-hidden="true" /> Về danh sách đề xuất
          </button>
          <TieuDeHoSo Icon={FileText} cap="h1">
            <span ref={tieuDeForm} tabIndex={-1} className="text-lg">Tạo đề xuất cuộc họp</span>
          </TieuDeHoSo>
        </div>
        <section className={LOP_NHOM} aria-label="Thông tin đề xuất">
          <TieuDeHoSo Icon={FileText} cap="h2">Thông tin đề xuất</TieuDeHoSo>
          <dl className="grid gap-3 rounded-xl border border-slate-200 bg-slate-50 p-3 text-sm sm:grid-cols-2">
            <div>
              <dt className="text-xs text-slate-600">Người đề xuất</dt>
              <dd className="mt-1 wrap-anywhere font-semibold">{nguoiDung.hoTen || 'Chưa có họ tên'}</dd>
              {nguoiDung.tenPhongBan && <dd className="text-xs text-slate-600">{nguoiDung.tenPhongBan}</dd>}
            </div>
            <div>
              <dt className="text-xs text-slate-600">Thư ký nhận đề xuất</dt>
              <dd className="mt-1 wrap-anywhere font-semibold">{nguoiDung.tenThuKyMacDinh || 'Chưa được phân công'}</dd>
            </div>
          </dl>
          <label className="block text-sm font-semibold">Tiêu đề
            <input className={LOP_TRUONG} name="tieuDe" required maxLength={255} />
          </label>
          <div className="grid gap-3 sm:grid-cols-2">
            <label className="text-sm font-semibold">Phạm vi
              <select className={LOP_TRUONG} value={phamVi} onChange={(event) => setPhamVi(event.target.value)}>
                {Object.entries(TEN_PHAM_VI).map(([ma, ten]) => <option key={ma} value={ma}>{ten}</option>)}
              </select>
            </label>
            <label className="text-sm font-semibold">Hình thức
              <select className={LOP_TRUONG} name="loaiHinh" value={loaiHinh} onChange={(event) => setLoaiHinh(event.target.value)}>
                <option value="TRUC_TIEP">Trực tiếp</option>
                <option value="TRUC_TUYEN">Trực tuyến</option>
              </select>
            </label>
          </div>
          {noiBo && (lanhDao ? (
            <div className="space-y-2">
              <label className="block text-sm font-semibold">Phòng ban chủ trì
                {danhSachPhongBan?.length ? (
                  <select name="phongBanChuTriId" className={LOP_TRUONG} required
                    defaultValue={phongBanMacDinh?.phongBanId ?? ''}>
                    <option value="" disabled>Chọn phòng ban theo tên</option>
                    {danhSachPhongBan.map((phong) => (
                      <option key={phong.phongBanId} value={phong.phongBanId}>{phong.tenPhongBan}</option>
                    ))}
                  </select>
                ) : (
                  <select className={LOP_TRUONG} disabled>
                    <option>{loiTaiPhongBan ? 'Không tải được danh sách' : danhSachPhongBan
                      ? 'Chưa có phòng ban đang hoạt động' : 'Đang tải phòng ban…'}</option>
                  </select>
                )}
              </label>
              {loiTaiPhongBan && <div className="flex flex-wrap items-center gap-2">
                <p role="alert" className="text-sm text-red-800">{loiTaiPhongBan}</p>
                <button type="button" className="rounded-lg border border-slate-300 px-3 py-1.5 text-sm font-semibold"
                  onClick={() => {
                    setLoiTaiPhongBan('')
                    setLanTaiPhongBan((lan) => lan + 1)
                  }}>Tải lại phòng ban</button>
              </div>}
            </div>
          ) : (
            <div className="rounded-lg border border-slate-200 bg-slate-50 px-3 py-2 text-sm">
              <p className="text-xs text-slate-600">Phòng ban chủ trì</p>
              <p className="font-semibold">{nguoiDung.tenPhongBan || 'Chưa có thông tin phòng ban'}</p>
            </div>
          ))}
          <div className="grid gap-3 sm:grid-cols-2">
            <label className="block text-sm font-medium">Mục đích
              <textarea className={LOP_TRUONG} name="mucTieu" rows={2} placeholder="Vì sao cần tổ chức cuộc họp?" />
            </label>
            <label className="block text-sm font-medium">Kết quả kỳ vọng
              <textarea className={LOP_TRUONG} name="ketQuaKyVong" rows={2} maxLength={4000}
                placeholder="Nội dung cần thống nhất hoặc quyết định" />
            </label>
          </div>
        </section>
        <section className={LOP_NHOM} aria-label="Thời gian và địa điểm dự kiến">
          <TieuDeHoSo Icon={CalendarDays} cap="h2">Thời gian và địa điểm dự kiến</TieuDeHoSo>
          <div className="grid gap-3 sm:grid-cols-2">
            {Array.from({ length: soKhungGio }, (_, i) => (
              <label key={i} className="block min-w-0 text-sm font-medium">
                {i === 0 ? 'Ngày giờ bắt đầu ưu tiên' : `Ngày giờ bắt đầu phương án ${i + 1}`}
                <input className={LOP_TRUONG} name="khungGioDeXuat" type="datetime-local" />
              </label>
            ))}
            <label className="block min-w-0 text-sm font-medium">Thời lượng dự kiến (phút)
              <input className={LOP_TRUONG} name="thoiLuongDuKienPhut" type="number" min="1" step="1"
                max="2147483647" placeholder="Ví dụ 60" />
            </label>
          </div>
          <div className="flex flex-wrap gap-2">
            {soKhungGio < 3 && <button type="button"
              className="rounded-lg border border-slate-300 px-3 py-1.5 text-sm font-semibold"
              onClick={() => setSoKhungGio((so) => so + 1)}>Thêm khung giờ</button>}
            {soKhungGio > 1 && <button type="button"
              className="rounded-lg border border-slate-300 px-3 py-1.5 text-sm font-semibold"
              onClick={() => setSoKhungGio((so) => so - 1)}>Bỏ khung giờ cuối</button>}
          </div>
          {loaiHinh === 'TRUC_TIEP' ? (
            <label className="block text-sm font-medium">Địa điểm đề xuất
              <input className={LOP_TRUONG} name="diaDiemDeXuat" maxLength={255} placeholder="Tên phòng, tầng, địa chỉ" />
            </label>
          ) : (
            <div className="grid gap-3 sm:grid-cols-2">
              <label className="block text-sm font-medium">Nền tảng trực tuyến
                <input className={LOP_TRUONG} name="nenTangTrucTuyen" maxLength={100} placeholder="Google Meet, Zoom, Microsoft Teams…" />
              </label>
              <label className="block text-sm font-medium">Liên kết tham gia dự kiến
                <input className={LOP_TRUONG} name="lienKetTrucTuyen" type="url" maxLength={2048} placeholder="https://" />
              </label>
              <label className="block text-sm font-medium sm:col-span-2">Hướng dẫn tham gia (nếu có)
                <input className={LOP_TRUONG} name="huongDanTrucTuyen" maxLength={500} placeholder="Mã cuộc họp hoặc mật mã" />
              </label>
            </div>
          )}
        </section>
        <section className={LOP_NHOM} aria-label="Thành phần dự kiến">
          <TieuDeHoSo Icon={Users} cap="h2">Thành phần dự kiến</TieuDeHoSo>
          <div className="grid gap-3 sm:grid-cols-2">
            <label className="block text-sm font-medium">Bắt buộc tham dự
              <textarea className={LOP_TRUONG} name="thanhPhanBatBuoc" rows={2} maxLength={4000} placeholder="Tên người hoặc bộ phận, mỗi mục một dòng" />
            </label>
            <label className="block text-sm font-medium">Tham dự để nắm thông tin
              <textarea className={LOP_TRUONG} name="thanhPhanTuyChon" rows={2} maxLength={4000} placeholder="Tên người hoặc bộ phận, mỗi mục một dòng" />
            </label>
          </div>
          <label className="flex items-center gap-2 text-sm">
            {phamVi === 'BEN_NGOAI'
              ? <input type="checkbox" checked disabled readOnly />
              : <input name="coKhachMoiBenNgoai" type="checkbox" />}
            Có khách mời bên ngoài
          </label>
          <label className="block text-sm font-semibold">Thư ký cuộc họp đề nghị
            <select className={LOP_TRUONG} name="thuKyCuocHopId">
              <option value="">Dùng thư ký mặc định</option>
              {luaChonThuKy?.map((nguoi) => (
                <option key={nguoi.nguoiDungId} value={nguoi.nguoiDungId}>
                  {[nguoi.hoTen, nguoi.chucVu, nguoi.tenPhongBan].filter(Boolean).join(' · ')}
                </option>
              ))}
            </select>
          </label>
          {loiTaiThuKy ? <p role="alert" className="text-sm text-red-800">{loiTaiThuKy}</p>
            : luaChonThuKy === null && <p role="status" className="text-sm text-slate-600">Đang tải danh sách thư ký…</p>}
        </section>
        <section className={LOP_NHOM} aria-label="Chương trình và tài liệu">
          <TieuDeHoSo Icon={ListChecks} cap="h2">Chương trình và tài liệu</TieuDeHoSo>
          <label className="block text-sm font-semibold">Chương trình dự kiến
            <textarea className={LOP_TRUONG} name="noiDungDuKien" rows={4}
              placeholder="Mỗi mục một dòng, có thể ghi kèm thời lượng và người trình bày" />
          </label>
          <fieldset disabled={dangGui} className="space-y-2 rounded-xl border border-slate-200 p-3">
            <legend className="px-1 text-sm font-semibold">
              <span className="inline-flex items-center gap-2"><Paperclip size={16} aria-hidden="true" /> Tài liệu và chuẩn bị trước</span>
            </legend>
            <p className="text-xs text-slate-600">Đính kèm tờ trình, báo cáo hoặc tài liệu làm căn cứ. PDF, Word, Excel · tối đa 10 MB mỗi tệp.</p>
            <label className="block text-sm font-medium">Yêu cầu chuẩn bị trước
              <textarea className={LOP_TRUONG} name="yeuCauChuanBi" rows={2} maxLength={4000} placeholder="Ai cần đọc hoặc chuẩn bị nội dung gì?" />
            </label>
            <label className="block text-sm font-medium">Liên kết tài liệu
              <textarea className={LOP_TRUONG} name="lienKetTaiLieu" rows={2} maxLength={4000} placeholder="Mỗi liên kết một dòng" />
            </label>
            <label className="block text-sm font-semibold">Chọn tệp
              <input type="file" multiple accept=".pdf,.doc,.docx,.xls,.xlsx" onChange={chonTepDeXuat}
                className={LOP_TRUONG + ' file:mr-3 file:rounded file:border-0 file:bg-blue-50 file:px-2 file:py-1 file:text-blue-900'} />
            </label>
            {tepDeXuat.length > 0 && <ul className="space-y-2">
              {tepDeXuat.map((tep, i) => (
                <li key={`${tep.name}-${tep.lastModified}-${i}`}
                  className="flex flex-wrap items-center justify-between gap-2 rounded-lg bg-slate-50 px-3 py-2">
                  <span className="min-w-0 wrap-anywhere text-sm">{tep.name}</span>
                  <button type="button" aria-label={`Bỏ tệp ${tep.name}`} className="text-sm font-semibold text-rose-800 hover:underline"
                    onClick={() => setTepDeXuat((ds) => ds.filter((_, viTri) => viTri !== i))}>Bỏ tệp</button>
                </li>
              ))}
            </ul>}
          </fieldset>
        </section>
        <div className="sticky bottom-2 z-10 space-y-2 rounded-2xl border border-slate-200 bg-white p-3 shadow-sm sm:p-4">
          {loi && <p role="alert" className="rounded-lg bg-rose-50 px-3 py-2 text-sm text-rose-900">{loi}</p>}
          {!coThuKy && <p className="text-sm text-slate-600">Bạn có thể lưu nháp và gửi sau khi có thư ký nhận đề xuất.</p>}
          <div className="flex flex-wrap justify-end gap-2">
            <button type="submit" value="nhap" disabled={dangGui || chuaCoPhongBan}
              className={LOP_NUT_PHU}>Lưu nháp</button>
            <button type="submit" value="gui" disabled={dangGui || !coThuKy || chuaCoPhongBan}
              className={LOP_NUT_CHINH}><Send size={16} aria-hidden="true" /> Gửi thư ký</button>
          </div>
          {dangGui && <p role="status" className="text-right text-sm text-slate-600">Đang lưu đề xuất…</p>}
        </div>
      </form>
    </div>
  )
}
