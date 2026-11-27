import { useEffect, useRef, useState } from 'react'
import { useDongBoDuLieu } from '../../services/useDongBoDuLieu.js'
import {
  batDauCuocHop,
  ketThucCuocHop,
  layChiTietDeXuat,
  layKeHoachChoToiDuyet,
  layHoSoThuKyLanhDao,
  layLuaChonKeHoach,
  tiepNhanDeXuat,
} from '../../api/cuocHopApi.js'
import TaiLieuKeHoachThuKy from './TaiLieuKeHoachThuKy.jsx'
import FormKeHoachThuKy from './FormKeHoachThuKy.jsx'
import DiemDanhThuKy from './DiemDanhThuKy.jsx'
import BienBanThuKy from './BienBanThuKy.jsx'
import KetLuanThuKy from './KetLuanThuKy.jsx'
import ThanhVienCuocHopThuKy from './ThanhVienCuocHopThuKy.jsx'
import KhachNgoaiCuocHop from './KhachNgoaiCuocHop.jsx'
import DanhSachBieuQuyetChuTri from '../pheduyet/DanhSachBieuQuyetChuTri.jsx'
import TaoBieuQuyet from '../pheduyet/TaoBieuQuyet.jsx'
import NhanTrangThai from './NhanTrangThaiDeXuat.jsx'
import { layNhanTrangThaiHoSo } from './hienThiDeXuat.js'
import NoiDungPhanHoi from './NoiDungPhanHoi.jsx'
import ThongTinCuocHop from './ThongTinCuocHop.jsx'
import TaiLieuTongQuanCuocHop from './TaiLieuTongQuanCuocHop.jsx'
import LichCuaHoSo from './LichCuaHoSo.jsx'
import VanBanCuaHoSo from './VanBanCuaHoSo.jsx'
import HoSoDaTrinh from './HoSoDaTrinh.jsx'
import ThaoTacKeHoach from './ThaoTacKeHoach.jsx'
import ChuyenKeHoachCapTren from './ChuyenKeHoachCapTren.jsx'
import TienDoCuocHop from './TienDoCuocHop.jsx'
import BauChonCuocHop from '../thamdu/BauChonCuocHop.jsx'
import NghiQuyetCuocHop from '../thamdu/NghiQuyetCuocHop.jsx'
import LichSuKeHoach from './LichSuKeHoach.jsx'
import CalendarDays from 'lucide-react/dist/esm/icons/calendar-days.mjs'
import { TieuDeHoSo } from '../../components/GiaoDienPhongHop.jsx'
import { LOP_NUT_CHINH, LOP_NUT_PHU } from '../../components/giaoDienPhongHop.js'
import { FormDeXuatLich } from './LichHopThuKy.jsx'
import TiepNhanDeXuatAction from './thaoTacKeHoach/TiepNhanDeXuatAction.jsx'
const TABS = [
  { id: 'thong-tin', ten: 'Đề xuất' },
  { id: 'ke-hoach', ten: 'Lịch và kế hoạch' },
  { id: 'loi-moi', ten: 'Lời mời và thành viên' },
  { id: 'lich-su', ten: 'Lịch sử và phản hồi' },
  { id: 'diem-danh', ten: 'Điểm danh' },
  { id: 'bieu-quyet', ten: 'Biểu quyết' },
  { id: 'bau-chon', ten: 'Bầu chọn' },
  { id: 'nghi-quyet', ten: 'Nghị quyết' },
  { id: 'bien-ban', ten: 'Biên bản' },
  { id: 'ket-luan', ten: 'Kết luận' },
]

export default function ChiTietDeXuat({ cuocHopId, token, nguoiDung, onQuayLai, onDaTiepNhan, onLapLich, onMoPhongHop, tabBanDau, chiXem = false }) {
  const phienDongBo = useDongBoDuLieu()
  const [chiTiet, setChiTiet] = useState(null)
  const [loi, setLoi] = useState('')
  const [dangTiepNhan, setDangTiepNhan] = useState(false)
  const [loiTiepNhan, setLoiTiepNhan] = useState('')
  const [choDoiLich, setChoDoiLich] = useState(null)
  const [moLichTaiHoSo, setMoLichTaiHoSo] = useState(false)
  const [lichMoiTao, setLichMoiTao] = useState(null)
  const tieuDeFormLich = useRef(null)
  const [thongBaoTiepNhan, setThongBaoTiepNhan] = useState('')
  const [dangBatDau, setDangBatDau] = useState(false)
  const [loiBatDau, setLoiBatDau] = useState('')
  const [dangKetThuc, setDangKetThuc] = useState(false)
  const [loiKetThuc, setLoiKetThuc] = useState('')
  const [tabHienTai, setTabHienTai] = useState(tabBanDau)
  const [luaChonKeHoach, setLuaChonKeHoach] = useState(null)
  const [loiLuaChon, setLoiLuaChon] = useState('')
  const [moFormCapNhatKeHoach, setMoFormCapNhatKeHoach] = useState(false)
  const [yeuCauThuKy, setYeuCauThuKy] = useState(null)
  const [loiHoSo, setLoiHoSo] = useState('')
  const [yeuCauDuyet, setYeuCauDuyet] = useState(null)
  const [loiTaiQuyenDuyet, setLoiTaiQuyenDuyet] = useState('')
  const [thongBaoChuyenCap, setThongBaoChuyenCap] = useState('')
  const buocXuLy = chiTiet?.buocXuLyHienTai

  const laNguoiRaSoat = chiTiet?.nguoiDangXuLyId === nguoiDung?.nguoiDungId
  const buocThuKyLanhDao = ['CHO_THU_KY_LANH_DAO', 'THU_KY_LANH_DAO_XU_LY'].includes(buocXuLy)

  useEffect(() => {
    const controller = new AbortController()

    async function taiChiTiet() {
      try {
        const duLieu = await layChiTietDeXuat(cuocHopId, token, controller.signal)
        if (controller.signal.aborted) return
        setChiTiet(duLieu)
        setLoi('')
      } catch (loiTaiChiTiet) {
        if (controller.signal.aborted) return

        const thongBao = {
          403: 'Bạn không có quyền xem đề xuất này.',
          404: 'Không tìm thấy đề xuất cuộc họp.',
        }

        if (loiTaiChiTiet.status === 403 || loiTaiChiTiet.status === 404) setChiTiet(null)
        setLoi(thongBao[loiTaiChiTiet.status] || 'Không tải được chi tiết đề xuất.')
      }
    }

    taiChiTiet()
    return () => controller.abort()
  }, [cuocHopId, token, phienDongBo])

  useEffect(() => {
    if (chiXem || !buocThuKyLanhDao || !laNguoiRaSoat) return

    const controller = new AbortController()
    layHoSoThuKyLanhDao(token, controller.signal)
      .then((hoSo) => {
        if (!controller.signal.aborted) {
          setYeuCauThuKy(hoSo.find((muc) => muc.cuocHopId === cuocHopId) ?? null)
          setLoiHoSo('')
        }
      })
      .catch((loiTai) => {
        if (!controller.signal.aborted) setLoiHoSo(loiTai.message)
      })

    return () => controller.abort()
  }, [chiXem, buocThuKyLanhDao, cuocHopId, laNguoiRaSoat, token, phienDongBo])

  useEffect(() => {
    if (!['CHO_TRUONG_PHONG', 'CHO_TRUONG_NHOM', 'CHO_LANH_DAO'].includes(buocXuLy)) return

    const controller = new AbortController()
    const cap = {
      CHO_TRUONG_PHONG: 'TRUONG_PHONG',
      CHO_TRUONG_NHOM: 'TRUONG_NHOM',
      CHO_LANH_DAO: 'LANH_DAO',
    }[buocXuLy]
    layKeHoachChoToiDuyet(token, controller.signal)
      .then((danhSach) => {
        if (controller.signal.aborted) return
        setYeuCauDuyet(danhSach.find((muc) => (
          muc.cuocHopId === cuocHopId && muc.capPheDuyet === cap && muc.trangThai === 'CHO_DUYET'
        )) ?? null)
        setLoiTaiQuyenDuyet('')
      })
      .catch((error) => {
        if (!controller.signal.aborted) setLoiTaiQuyenDuyet(error.message)
      })
    return () => controller.abort()
  }, [buocXuLy, cuocHopId, token, phienDongBo])

  async function xuLyTiepNhan() {
    if (dangTiepNhan || chiTiet?.trangThai !== 'CHO_TIEP_NHAN') return

    setDangTiepNhan(true)
    setLoiTiepNhan('')

    try {
      const ketQua = await tiepNhanDeXuat(cuocHopId, token)
      const thayDoi = {
        trangThai: ketQua.trangThai,
        buocXuLyHienTai: ketQua.buocXuLyHienTai,
      }

      setChiTiet((hienTai) => ({ ...hienTai, ...thayDoi }))
      onDaTiepNhan?.(cuocHopId, thayDoi)
      setThongBaoTiepNhan(ketQua.message || 'Tiếp nhận đề xuất thành công.')
      setTabHienTai('ke-hoach')
    } catch (loiKhiTiepNhan) {
      const thongBao = {
        401: 'Phiên đăng nhập không còn hợp lệ.',
        403: 'Tài khoản này không có quyền tiếp nhận đề xuất.',
        409: 'Trạng thái đề xuất đã thay đổi. Hãy tải lại trang.',
      }

      setLoiTiepNhan(
        thongBao[loiKhiTiepNhan.status] || 'Không tiếp nhận được đề xuất.',
      )
    } finally {
      setDangTiepNhan(false)
    }
  }

  function taiLaiSauPhatHanh() {
    layChiTietDeXuat(cuocHopId, token).then((muc) => {
      setChiTiet(muc)
      onDaTiepNhan?.(cuocHopId, muc)
    }).catch((error) => setLoi(error.message))
  }

  async function xuLyBatDau() {
    if (dangBatDau) return
    setDangBatDau(true)
    setLoiBatDau('')
    try {
      const ketQua = await batDauCuocHop(cuocHopId, token)
      setChiTiet((hienTai) => ({ ...hienTai, ...ketQua }))
      onDaTiepNhan?.(cuocHopId, ketQua)
      if (onMoPhongHop) onMoPhongHop(cuocHopId)
      else setTabHienTai('diem-danh')
    } catch (loi) {
      setLoiBatDau(loi.message)
    } finally {
      setDangBatDau(false)
    }
  }

  async function xuLyKetThuc() {
    if (dangKetThuc || chiTiet?.trangThai !== 'DANG_DIEN_RA') return

    setDangKetThuc(true)
    setLoiKetThuc('')

    try {
      const ketQua = await ketThucCuocHop(cuocHopId, token)
      const thayDoi = {
        trangThai: ketQua.trangThai,
        buocXuLyHienTai: ketQua.buocXuLyHienTai,
      }

      setChiTiet((hienTai) => ({ ...hienTai, ...thayDoi }))
      onDaTiepNhan?.(cuocHopId, thayDoi)
    } catch (loi) {
      setLoiKetThuc(loi.message)
    } finally {
      setDangKetThuc(false)
    }
  }

  const dangLapKeHoach = chiTiet?.duocLapKeHoach && chiTiet.trangThai === 'DANG_CHUAN_BI'
    && chiTiet.buocXuLyHienTai === 'LAP_KE_HOACH'
  const dangToChuc = chiTiet?.trangThai === 'DA_DUYET'
    && chiTiet.buocXuLyHienTai === 'THU_KY_TO_CHUC'
  const laThuKy = chiTiet?.duocToChuc === true
  const coLichXacNhan = chiTiet?.coLichDaXacNhan === true
  const dangMoCapNhatKeHoach = moFormCapNhatKeHoach && laThuKy && dangToChuc
  const hienFormKeHoach = !chiXem && (dangLapKeHoach || dangMoCapNhatKeHoach)
  const coKhachNgoai = chiTiet?.coKhachMoiBenNgoai
  useEffect(() => {
    if (chiXem || !hienFormKeHoach || tabHienTai !== 'ke-hoach') return
    const controller = new AbortController()
    layLuaChonKeHoach(cuocHopId, token, controller.signal)
      .then((duLieu) => {
        if (controller.signal.aborted) return
        setLuaChonKeHoach(duLieu)
        setLoiLuaChon('')
      })
      .catch((loi) => {
        if (controller.signal.aborted) return
        setLuaChonKeHoach(null)
        setLoiLuaChon(loi.message)
      })
    return () => controller.abort()
  }, [chiXem, cuocHopId, token, hienFormKeHoach, tabHienTai, coKhachNgoai])
  const dangDienRa = chiTiet?.trangThai === 'DANG_DIEN_RA'
  const daKetThuc = chiTiet?.trangThai === 'DA_KET_THUC'
  const coBanGiao = chiTiet?.yeuCauBanGiaoId != null
  const laChuTri = Number(nguoiDung?.nguoiDungId) === Number(chiTiet?.nguoiChuTriId)
  const hienThanhVien = Boolean(
    chiTiet && (chiXem ? laChuTri || nguoiDung?.nguoiDungId === chiTiet.nguoiDeXuatId : laThuKy || coBanGiao)
    && (dangToChuc || dangDienRa || daKetThuc),
  )
  const choTiepNhan = chiTiet?.trangThai === 'CHO_TIEP_NHAN'
  const coTheTiepNhan = choTiepNhan && !chiXem && chiTiet.duocLapKeHoach
  function moFormLich() {
    if (moLichTaiHoSo) {
      tieuDeFormLich.current?.focus()
      return
    }
    setMoLichTaiHoSo(true)
  }
  useEffect(() => {
    if (moLichTaiHoSo && tabHienTai === 'ke-hoach') tieuDeFormLich.current?.focus()
  }, [moLichTaiHoSo, tabHienTai])

  const cacTab = TABS.filter(({ id }) => {
    if (id === 'loi-moi') return hienThanhVien
    if (id === 'ke-hoach') return true
    if (id === 'lich-su') {
      return !chiXem || Number(nguoiDung?.nguoiDungId) === Number(chiTiet?.nguoiDeXuatId)
    }
    if (id === 'diem-danh') return !chiXem && (laThuKy || coBanGiao) && (dangDienRa || daKetThuc)
    if (id === 'bieu-quyet' || id === 'bau-chon') return laChuTri && (dangDienRa || daKetThuc)
    if (id === 'nghi-quyet') return (laChuTri || laThuKy) && (dangToChuc || dangDienRa || daKetThuc)
    if (id === 'bien-ban' || id === 'ket-luan') return daKetThuc && (laThuKy || coBanGiao || laChuTri)
    return true
  })

  const tabHienThi = cacTab.some((tab) => tab.id === tabHienTai)
    ? tabHienTai
    : 'thong-tin'

  return (
    <div className="space-y-5">
      <button
        type="button"
        onClick={onQuayLai}
        disabled={dangTiepNhan}
        className="inline-flex items-center gap-2 rounded-lg px-2 py-1 text-sm font-semibold text-emerald-800 transition hover:bg-emerald-50 disabled:opacity-60"
      >
        <span aria-hidden="true">←</span> Quay lại danh sách
      </button>

      {loi && <p role="alert" className="rounded-xl border border-red-200 bg-red-50 p-4 text-sm text-red-800">{loi}</p>}
      {chiTiet === null ? !loi && (
        <p role="status" className="rounded-2xl border border-slate-200 bg-white p-8 text-center text-sm text-slate-600">Đang tải chi tiết cuộc họp…</p>
      ) : (
        <>
            <section className="min-w-0 overflow-hidden rounded-2xl border border-slate-200/80 bg-white shadow-sm shadow-slate-900/[0.02]">
              <div className="border-b border-[#334e68] bg-[#102a43] p-5 text-white sm:p-7">
                <div className="flex flex-wrap items-start justify-between gap-4">
                  <div className="min-w-0">
                    <p className="text-xs font-bold uppercase tracking-[0.12em] text-[#bcccdc]">Hồ sơ cuộc họp</p>
                    <h1 className="mt-2 break-words text-2xl font-bold tracking-tight md:text-3xl">{chiTiet.tieuDe}</h1>
                    {chiTiet.maCuocHop && <p className="mt-3 inline-block break-all rounded-md border border-[#486581] bg-[#243b53] px-2.5 py-1 font-mono text-xs text-[#d9e2ec]">
                      {chiTiet.maCuocHop}
                    </p>}
                    {(chiTiet.tenNguoiChuTri || chiTiet.tenThuKyCuocHop) && (
                      <dl className="mt-4 flex flex-wrap gap-x-6 gap-y-2 text-sm">
                        {chiTiet.tenNguoiChuTri && (
                          <div>
                            <dt className="text-xs text-[#bcccdc]">Chủ trì</dt>
                            <dd className="font-medium">{chiTiet.tenNguoiChuTri}</dd>
                          </div>
                        )}
                        {chiTiet.tenThuKyCuocHop && (
                          <div>
                            <dt className="text-xs text-[#bcccdc]">Thư ký cuộc họp</dt>
                            <dd className="font-medium">{chiTiet.tenThuKyCuocHop}</dd>
                          </div>
                        )}
                      </dl>
                    )}
                  </div>
                  <NhanTrangThai trangThai={layNhanTrangThaiHoSo(chiTiet)} />
                </div>
              </div>

              <div className="px-5 pt-4 sm:px-7">
                <TienDoCuocHop chiTiet={chiTiet} />
              </div>

              {yeuCauDuyet?.cuocHopId === chiTiet.cuocHopId
                && ['CHO_TRUONG_PHONG', 'CHO_TRUONG_NHOM', 'CHO_LANH_DAO'].includes(buocXuLy) && tabHienThi !== 'ke-hoach' && (
                <div className="px-5 pt-4 sm:px-7">
                  <section className="rounded-xl border border-blue-100 bg-blue-50 p-4">
                    <TieuDeHoSo Icon={CalendarDays} cap="h2"
                      phu="Xem lịch, nội dung và tài liệu của lần trình đang chờ bạn."
                      thaoTac={<button type="button" className={LOP_NUT_CHINH} onClick={() => setTabHienTai('ke-hoach')}>
                        Mở hồ sơ để duyệt
                      </button>}>
                      Kế hoạch và lịch chờ bạn duyệt
                    </TieuDeHoSo>
                  </section>
                </div>
              )}

              {coTheTiepNhan && (
                <div className="px-5 pt-4 sm:px-7">
                  <TiepNhanDeXuatAction onClick={xuLyTiepNhan} disabled={dangTiepNhan} />
                  {loiTiepNhan && <p role="alert" className="mt-2 text-sm text-rose-800">{loiTiepNhan}</p>}
                </div>
              )}

              {dangDienRa && (laChuTri || laThuKy) && (
                <div className="px-5 pt-4 sm:px-7">
                  <a href={`#cuoc-hop/${cuocHopId}`}
                    onClick={onMoPhongHop ? (event) => {
                      if (event.ctrlKey || event.metaKey || event.shiftKey || event.altKey) return
                      event.preventDefault()
                      onMoPhongHop(cuocHopId)
                    } : undefined}
                    className="inline-flex rounded-lg bg-emerald-800 px-4 py-2.5 text-sm font-semibold text-white hover:bg-emerald-900">
                    Mở phòng họp đang diễn ra
                  </a>
                </div>
              )}

              {(chiTiet.yeuCauChoGuiCapTrenId != null || thongBaoChuyenCap) && (
                <div className="space-y-3 px-5 pt-4 sm:px-7">
                  {thongBaoChuyenCap && (
                    <p role="status" className="rounded-lg bg-emerald-50 p-3 text-sm text-emerald-800">
                      {thongBaoChuyenCap}
                    </p>
                  )}
                  {chiTiet.yeuCauChoGuiCapTrenId != null && (
                    <ChuyenKeHoachCapTren
                      key={chiTiet.yeuCauChoGuiCapTrenId}
                      cuocHopId={cuocHopId}
                      yeuCauId={chiTiet.yeuCauChoGuiCapTrenId}
                      token={token}
                      onDaGui={async (ketQua) => {
                        const chiTietMoi = await layChiTietDeXuat(cuocHopId, token)
                        setChiTiet(chiTietMoi)
                        onDaTiepNhan?.(cuocHopId, chiTietMoi)
                        setThongBaoChuyenCap(ketQua?.message || 'Đã chuyển hồ sơ lên cấp trên.')
                      }}
                    />
                  )}
                </div>
              )}

              {chiTiet.phanHoiKeHoach && (
                <div className="px-5 pt-3"><NoiDungPhanHoi phanHoi={chiTiet.phanHoiKeHoach} /></div>
              )}

              {dangToChuc && hienThanhVien && laThuKy && !chiXem && tabHienThi !== 'loi-moi' && (
                <div className="px-5 pt-4 sm:px-7">
                  <button type="button" onClick={() => setTabHienTai('loi-moi')}
                    className="rounded-lg border border-blue-200 bg-blue-50 px-4 py-2 text-sm font-semibold text-blue-950 hover:bg-blue-100">
                    Quản lý lời mời và thành viên
                  </button>
                </div>
              )}

              <nav
                role="tablist"
                aria-label="Chi tiết cuộc họp"
                className="flex gap-1 overflow-x-auto border-b border-slate-100 px-4 pt-3 sm:px-6"
              >
                {cacTab.map((tab) => (
                  <button
                    key={tab.id}
                    type="button"
                    role="tab"
                    id={`tab-${tab.id}`}
                    aria-selected={tabHienThi === tab.id}
                    aria-controls={`panel-${tab.id}`}
                    onClick={() => setTabHienTai(tab.id)}
                    className={`shrink-0 border-b-2 px-3 py-3 text-sm font-semibold transition ${
                      tabHienThi === tab.id
                        ? 'border-emerald-700 text-emerald-800'
                        : 'border-transparent text-slate-500 hover:text-slate-800'
                    }`}
                  >
                    {tab.ten}
                  </button>
                ))}
              </nav>

              <div
                role="tabpanel"
                id={`panel-${tabHienThi}`}
                aria-labelledby={`tab-${tabHienThi}`}
                className="space-y-5 p-5 sm:p-6"
              >
                {tabHienThi === 'thong-tin' && (
                  <>
                    <ThongTinCuocHop chiTiet={chiTiet} />
                    {chiTiet.trangThai !== 'NHAP' && (
                      <LichCuaHoSo cuocHopId={cuocHopId} token={token} loaiHinh={chiTiet.loaiHinh}
                        lichHopIdKeHoachDaDuyet={chiTiet.lichHopIdKeHoachDaDuyet} />
                    )}

                    <TaiLieuTongQuanCuocHop key={'de-xuat/' + cuocHopId} cuocHopId={cuocHopId} token={token} loai="de-xuat"
                      choPhepTaiLen={chiTiet.trangThai === 'NHAP' && nguoiDung.nguoiDungId === chiTiet.nguoiDeXuatId} />
                    {chiTiet.daHoanTatHoSo && <p className="text-sm font-semibold text-emerald-800">Đã hoàn tất hồ sơ.</p>}

                    {dangToChuc && ((!chiXem && laThuKy) || laChuTri) && (
                      <section className="space-y-3 rounded-xl border border-emerald-200 bg-emerald-50 p-4">
                        <h2 className="font-bold text-emerald-950">Bắt đầu và mở phòng họp</h2>
                        <p className="text-sm text-emerald-900">
                          {coLichXacNhan
                            ? 'Chủ trì và thành viên có lời mời hợp lệ sẽ thấy thông báo và nút Tham gia.'
                            : 'Cần xác nhận lịch trước khi mở phòng họp.'}
                        </p>
                        <div className="flex flex-wrap items-center gap-3">
                          {!chiXem && laThuKy && onLapLich && <button type="button" onClick={() => onLapLich(cuocHopId)}
                            className="rounded-lg border border-slate-200 px-3 py-2 text-sm font-semibold">Mở Calendar</button>}
                          {coLichXacNhan && <button type="button" disabled={dangBatDau} onClick={xuLyBatDau}
                            className="rounded-lg bg-emerald-800 px-4 py-2 text-sm font-semibold text-white disabled:opacity-60">
                            {dangBatDau ? 'Đang bắt đầu…' : 'Bắt đầu cuộc họp'}
                          </button>}
                          {loiBatDau && <p role="alert" className="text-sm text-red-800">{loiBatDau}</p>}
                        </div>
                      </section>
                    )}

                    {!onMoPhongHop && !chiXem && laThuKy && dangDienRa && (
                      <div className="rounded-xl border border-rose-200 bg-rose-50 p-4">
                        <p className="text-sm font-semibold text-rose-950">Cuộc họp đang diễn ra</p>
                        <p className="mt-1 text-sm text-rose-800">Kết thúc cuộc họp sau khi hoàn tất nội dung và biểu quyết.</p>
                        {loiKetThuc && <p role="alert" className="mt-3 text-sm text-red-800">{loiKetThuc}</p>}
                        <button
                          type="button"
                          onClick={xuLyKetThuc}
                          disabled={dangKetThuc}
                          className="mt-4 rounded-lg bg-rose-700 px-4 py-2.5 text-sm font-semibold text-white transition hover:bg-rose-800 disabled:opacity-60"
                        >
                          {dangKetThuc ? 'Đang kết thúc…' : 'Kết thúc cuộc họp'}
                        </button>
                      </div>
                    )}

                    {!coTheTiepNhan && loiTiepNhan && <p role="alert" className="text-sm text-red-800">{loiTiepNhan}</p>}
                    {thongBaoTiepNhan && <p role="status" className="rounded-lg bg-emerald-50 p-3 text-sm text-emerald-800">{thongBaoTiepNhan}</p>}
                  </>
                )}

                {tabHienThi === 'ke-hoach' && (
                  <div className="space-y-6">
                    {thongBaoTiepNhan && <p role="status" className="rounded-lg bg-emerald-50 p-3 text-sm text-emerald-800">{thongBaoTiepNhan}</p>}
                    {dangLapKeHoach && !chiXem && choDoiLich === false && (
                      <section className="space-y-4 rounded-2xl border border-slate-200 bg-slate-50 p-4">
                        <TieuDeHoSo Icon={CalendarDays} thaoTac={<>
                          {onLapLich && <button type="button" className={LOP_NUT_PHU} onClick={() => onLapLich(cuocHopId)}>Mở lịch tổng thể</button>}
                          <button type="button" className={LOP_NUT_CHINH} aria-expanded={moLichTaiHoSo}
                            onClick={() => moLichTaiHoSo ? setMoLichTaiHoSo(false) : moFormLich()}>
                            {moLichTaiHoSo ? 'Đóng form lịch' : 'Tạo lịch đề xuất'}
                          </button>
                        </>}>Lập lịch cho cuộc họp</TieuDeHoSo>
                        {moLichTaiHoSo && (
                          <div className="space-y-2">
                            <h4 ref={tieuDeFormLich} tabIndex={-1} className="scroll-mt-24 text-sm font-semibold">Giờ họp và phòng họp</h4>
                            <FormDeXuatLich key={chiTiet.loaiHinh} cuocHopId={cuocHopId} loaiHinh={chiTiet.loaiHinh} token={token}
                              thongTinDeXuat={chiTiet.thongTinDeXuat} thoiLuongDuKienPhut={chiTiet.thoiLuongDuKienPhut}
                              onDaTao={(lich) => setLichMoiTao(lich)} />
                          </div>
                        )}
                      </section>
                    )}

                    {!chiXem && laThuKy && dangToChuc && !moFormCapNhatKeHoach && choDoiLich === false && (
                      <div className="rounded-xl border border-amber-200 bg-amber-50 p-4">
                        <p className="text-sm font-semibold text-amber-950">Chỉnh sửa nội dung kế hoạch</p>
                        <p className="mt-2 text-sm text-amber-900">Nếu chỉ đổi giờ họp, dùng mục Dời lịch bên dưới để gửi giờ mới cho người được mời.</p>
                        <div className="mt-3 flex flex-wrap gap-2">
                          {onLapLich && (
                            <button
                              type="button"
                              onClick={() => onLapLich(cuocHopId)}
                              className="rounded-lg border border-amber-300 bg-white px-3 py-2 text-sm font-semibold text-amber-950 transition hover:bg-amber-100"
                            >
                              Mở Calendar
                            </button>
                          )}
                          <button
                            type="button"
                            onClick={() => setMoFormCapNhatKeHoach(true)}
                            className="rounded-lg bg-emerald-800 px-4 py-2 text-sm font-semibold text-white transition hover:bg-emerald-900"
                          >
                            Cập nhật kế hoạch
                          </button>
                        </div>
                      </div>
                    )}
                    {dangMoCapNhatKeHoach && (
                      <p role="status" className="rounded-lg border border-blue-200 bg-blue-50 p-3 text-sm text-blue-900">
                        Chỉnh sửa bên dưới rồi bấm Lưu kế hoạch. Sau khi lưu, chọn lịch dùng cho lần trình lại; bản đã duyệt trước đó vẫn được lưu trong lịch sử.
                      </p>
                    )}
                    {moFormCapNhatKeHoach && dangLapKeHoach && (
                      <p role="status" className="rounded-lg border border-emerald-200 bg-emerald-50 p-3 text-sm text-emerald-900">
                        Bản cập nhật đã được lưu. Chọn lịch dùng cho lần trình, kiểm tra tài liệu rồi gửi duyệt.
                      </p>
                    )}
                    {dangMoCapNhatKeHoach && chiTiet.trangThai === 'DA_DUYET' && (
                      <button
                        type="button"
                        onClick={() => setMoFormCapNhatKeHoach(false)}
                        className="rounded-lg border border-slate-200 px-3 py-2 text-sm font-semibold text-slate-700"
                      >
                        Hủy cập nhật
                      </button>
                    )}
                    <LichCuaHoSo
                      key={cuocHopId + '/' + chiTiet.trangThai}
                      cuocHopId={cuocHopId}
                      loaiHinh={chiTiet.loaiHinh}
                      token={token}
                      lichHopIdKeHoachDaDuyet={chiTiet.lichHopIdKeHoachDaDuyet}
                      onTrangThaiDoiLich={setChoDoiLich}
                      coQuyenTrinhLai={!chiXem && laThuKy && dangToChuc}
                      onDaTrinhLai={(ketQua) => {
                        setChiTiet((hienTai) => ({
                          ...hienTai,
                          trangThai: ketQua.trangThai,
                          buocXuLyHienTai: ketQua.buocXuLyHienTai,
                          duocToChuc: false,
                        }))
                        setThongBaoTiepNhan(ketQua.message)
                        layChiTietDeXuat(cuocHopId, token).then((chiTietMoi) => {
                          setChiTiet(chiTietMoi)
                          onDaTiepNhan?.(cuocHopId, chiTietMoi)
                        }).catch(() => setThongBaoTiepNhan(
                          'Đã trình lịch thay thế. Tải lại hồ sơ để xem người đang xử lý.',
                        ))
                      }}
                    />

                    {hienFormKeHoach ? (
                      <>
                        {loiLuaChon && <p role="alert" className="text-sm text-red-800">{loiLuaChon}</p>}
                        <FormKeHoachThuKy
                          luaChonChuTri={luaChonKeHoach?.nguoiChuTri}
                          luaChonThuKy={luaChonKeHoach?.thuKyCuocHop}
                          thuKyNhomCoDinh={luaChonKeHoach?.thuKyNhomCoDinh}
                          chiTiet={chiTiet}
                          token={token}
                          laBanCapNhat={moFormCapNhatKeHoach}
                          lyDoKhoa={choDoiLich === true ? 'Hãy xử lý lịch đang dời ở mục Lịch họp phía trên trước khi lưu kế hoạch.'
                            : choDoiLich === null ? 'Cần tải thông tin lịch trước khi lưu kế hoạch.' : ''}
                          onDaLuu={(duLieu, ketQua) => {
                            if (duLieu.coKhachMoiBenNgoai !== chiTiet.coKhachMoiBenNgoai) {
                              setLuaChonKeHoach(null)
                            }
                            const thayDoi = {
                              ...duLieu,
                              trangThai: ketQua.trangThai,
                              buocXuLyHienTai: ketQua.buocXuLyHienTai,
                              duocLapKeHoach: true,
                              duocToChuc: false,
                            }
                            setChiTiet((hienTai) => ({ ...hienTai, ...thayDoi }))
                            onDaTiepNhan?.(cuocHopId, thayDoi)
                          }}
                        />
                        <TaiLieuKeHoachThuKy
                          luaChonKeHoach={luaChonKeHoach}
                          lichHopIdKeHoachDaDuyet={chiTiet.lichHopIdKeHoachDaDuyet}
                          nguoiChuTriId={chiTiet.nguoiChuTriId}
                          cuocHopId={cuocHopId}
                          token={token}
                          loaiHinh={chiTiet.loaiHinh}
                          onLapLich={dangLapKeHoach ? moFormLich : onLapLich}
                          lichMoiTaoId={lichMoiTao?.lichHopId}
                          vaiTroNguoiDeXuat={chiTiet.vaiTroNguoiDeXuat}
                          choPhepThaoTac={dangLapKeHoach && choDoiLich === false}
                          choDoiLich={choDoiLich === true}
                          onDaTrinh={(chiTietMoi) => {
                            setChiTiet(chiTietMoi)
                            setMoFormCapNhatKeHoach(false)
                            onDaTiepNhan?.(cuocHopId, chiTietMoi)
                          }}
                        />
                      </>
                    ) : (
                      <HoSoDaTrinh
                        key={`${cuocHopId}/${buocXuLy}/${chiTiet.yeuCauBanGiaoId ?? ''}`}
                        cuocHopId={cuocHopId}
                        token={token}
                        buocRaSoat={buocThuKyLanhDao ? buocXuLy : null}
                        tenNguoiRaSoat={buocThuKyLanhDao ? chiTiet.tenNguoiDangXuLy : null}
                        choChuyenCap={buocXuLy === 'CHO_GUI_CAP_TREN'}
                        yeuCauDuyet={yeuCauDuyet}
                        onDaXuLy={async (_, thongBao) => {
                          setThongBaoTiepNhan(thongBao)
                          const chiTietMoi = await layChiTietDeXuat(cuocHopId, token)
                          setYeuCauDuyet(null)
                          setChiTiet(chiTietMoi)
                          onDaTiepNhan?.(cuocHopId, chiTietMoi)
                        }}
                      />
                    )}

                    {buocThuKyLanhDao && loiHoSo && (
                      <p role="alert" className="text-sm text-red-800">{loiHoSo}</p>
                    )}
                    {!chiXem && buocThuKyLanhDao && yeuCauThuKy && (
                      <div className="space-y-3">
                        <ThaoTacKeHoach
                          key={`${yeuCauThuKy.yeuCauPheDuyetId}/${buocXuLy}`}
                          cuocHopId={cuocHopId}
                          yeuCauPheDuyetId={yeuCauThuKy.yeuCauPheDuyetId}
                          token={token}
                          cheDo={buocXuLy === 'CHO_THU_KY_LANH_DAO' ? 'choTiepNhan' : 'dangRaSoat'}
                          onDangXuLy={setDangTiepNhan}
                          onDaXuLy={async (_, thongBao) => {
                            const chiTietMoi = await layChiTietDeXuat(cuocHopId, token)
                            setChiTiet(chiTietMoi)
                            onDaTiepNhan?.(cuocHopId, chiTietMoi)
                            setThongBaoTiepNhan(thongBao)
                          }}
                        />
                      </div>
                    )}
                    {loiTaiQuyenDuyet && ['CHO_TRUONG_PHONG', 'CHO_TRUONG_NHOM', 'CHO_LANH_DAO'].includes(buocXuLy) && (
                      <p role="alert" className="text-sm text-red-800">{loiTaiQuyenDuyet}</p>
                    )}
                    {!coTheTiepNhan && loiTiepNhan && <p role="alert" className="text-sm text-red-800">{loiTiepNhan}</p>}
                  </div>
                )}

                {tabHienThi === 'lich-su' && (
                  <>
                    {chiTiet.phanHoiKeHoach && <NoiDungPhanHoi phanHoi={chiTiet.phanHoiKeHoach} />}
                    <LichSuKeHoach cuocHopId={cuocHopId} token={token} />
                  </>
                )}

                {tabHienThi === 'loi-moi' && (
                  <div className="grid min-w-0 items-start gap-4 xl:grid-cols-[minmax(0,2fr)_minmax(0,1fr)]">
                    <div className="rounded-2xl border border-slate-200/80 bg-white p-5 shadow-sm shadow-slate-900/[0.02]">
                      <div className="mb-4">
                        <p className="text-xs font-bold uppercase tracking-[0.14em] text-emerald-800">Thành viên</p>
                        <h2 className="mt-1 text-lg font-bold text-slate-900">{!chiXem && laThuKy && dangToChuc ? 'Mời tham dự' : 'Thành viên cuộc họp'}</h2>
                      </div>
                      <ThanhVienCuocHopThuKy
                        cuocHopId={cuocHopId}
                        thongTinCuocHop={chiTiet.thongTinThuMoi}
                        token={token}
                        choPhepMoi={!chiXem && laThuKy && dangToChuc}
                        onLapLich={onLapLich}
                        lichHopIdKeHoachDaDuyet={chiTiet.lichHopIdKeHoachDaDuyet}
                      />
                    </div>
                    {(coKhachNgoai || chiTiet.phamViCuocHop === 'BEN_NGOAI') && (laThuKy || laChuTri) && (
                      <KhachNgoaiCuocHop
                        key={cuocHopId}
                        thongTinCuocHop={chiTiet.thongTinThuMoi}
                        cuocHopId={cuocHopId}
                        token={token}
                        laThuKy={!chiXem && laThuKy}
                        choPhepMoi={!chiXem && laThuKy && dangToChuc && Boolean(chiTiet.thongTinThuMoi)}
                        trucTuyen={chiTiet.loaiHinh === 'TRUC_TUYEN'}
                      />
                    )}
                  </div>
                )}

                {tabHienThi === 'diem-danh' && (dangDienRa || daKetThuc) && (
                  <DiemDanhThuKy cuocHopId={cuocHopId} token={token} readOnly={daKetThuc || !laThuKy} />
                )}

                {tabHienThi === 'bau-chon' && <BauChonCuocHop cuocHopId={cuocHopId} token={token} chiXem={daKetThuc} />}
                {tabHienThi === 'nghi-quyet' && <NghiQuyetCuocHop cuocHopId={cuocHopId} token={token} duocQuanLy={!daKetThuc && (laChuTri || laThuKy)} chuTri={laChuTri && !daKetThuc ? { ...nguoiDung, token } : undefined} />}
                {tabHienThi === 'bieu-quyet' && laChuTri && (
                  daKetThuc ? (
                    <DanhSachBieuQuyetChuTri
                      cuocHopId={cuocHopId}
                      token={token}
                      dangDienRa={false}
                      lanTao={0}
                    />
                  ) : (
                    <TaoBieuQuyet
                      key={cuocHopId}
                      nguoiDung={{ ...nguoiDung, token }}
                      cuocHopId={cuocHopId}
                    />
                  )
                )}

                {(chiXem || !laThuKy) && ['bien-ban', 'ket-luan'].includes(tabHienThi) && (
                  <VanBanCuaHoSo key={tabHienThi} loai={tabHienThi} cuocHopId={cuocHopId} token={token}
                    tenChuTri={chiTiet.tenNguoiChuTri} choPhepXuLy={laChuTri && daKetThuc} onPhatHanh={taiLaiSauPhatHanh} />
                )}

                {!chiXem && laThuKy && tabHienThi === 'bien-ban' && daKetThuc && (
                  <BienBanThuKy
                    key={`bien-ban-${cuocHopId}`}
                    cuocHopId={cuocHopId}
                    tieuDeCuocHop={chiTiet.tieuDe}
                    tenChuTri={chiTiet.tenNguoiChuTri}
                    token={token}
                  />
                )}

                {!chiXem && laThuKy && tabHienThi === 'ket-luan' && daKetThuc && (
                  <KetLuanThuKy
                    key={`ket-luan-${cuocHopId}`}
                    cuocHopId={cuocHopId}
                    tieuDeCuocHop={chiTiet.tieuDe}
                    tenChuTri={chiTiet.tenNguoiChuTri}
                    token={token}
                  />
                )}
              </div>
            </section>

        </>
      )}
    </div>
  )
}



