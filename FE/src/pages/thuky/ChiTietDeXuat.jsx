import { useEffect, useState } from 'react'
import { useDongBoDuLieu } from '../../services/useDongBoDuLieu.js'
import ArrowLeft from 'lucide-react/dist/esm/icons/arrow-left.mjs'
import ArrowRight from 'lucide-react/dist/esm/icons/arrow-right.mjs'
import CalendarDays from 'lucide-react/dist/esm/icons/calendar-days.mjs'
import {
  batDauCuocHop,
  ketThucCuocHop,
  layChiTietDeXuat,
  layHoSoThuKyLanhDao,
  layLuaChonKeHoach,
  layTrangThaiHienThiCuocHop,
  tiepNhanDeXuat,
  xuLyYeuCauKeHoach,
} from '../../api/cuocHopApi.js'
import TaiLieuKeHoachThuKy from './TaiLieuKeHoachThuKy.jsx'
import FormKeHoachThuKy from './FormKeHoachThuKy.jsx'
import DiemDanhThuKy from './DiemDanhThuKy.jsx'
import BienBanThuKy from './BienBanThuKy.jsx'
import KetLuanThuKy from './KetLuanThuKy.jsx'
import ThanhVienCuocHopThuKy from './ThanhVienCuocHopThuKy.jsx'
import DanhSachBieuQuyetChuTri from '../pheduyet/DanhSachBieuQuyetChuTri.jsx'
import TaoBieuQuyet from '../pheduyet/TaoBieuQuyet.jsx'
import NhanTrangThai from './NhanTrangThaiDeXuat.jsx'
import NoiDungPhanHoi from './NoiDungPhanHoi.jsx'
import ThongTinCuocHop from './ThongTinCuocHop.jsx'
import TaiLieuTongQuanCuocHop from './TaiLieuTongQuanCuocHop.jsx'
import LichCuaHoSo from './LichCuaHoSo.jsx'
import VanBanCuaHoSo from './VanBanCuaHoSo.jsx'
import HoSoDaTrinh from './HoSoDaTrinh.jsx'
import ThaoTacKeHoach from './ThaoTacKeHoach.jsx'
import ChuyenKeHoachCapTren from './ChuyenKeHoachCapTren.jsx'
import DieuHanhCuocHop from './DieuHanhCuocHop.jsx'
import TienDoCuocHop from './TienDoCuocHop.jsx'
import LichSuKeHoach from './LichSuKeHoach.jsx'
import TiepNhanDeXuatAction from './thaoTacKeHoach/TiepNhanDeXuatAction.jsx'
const TABS = [
  { id: 'thong-tin', ten: 'Đề xuất' },
  { id: 'ke-hoach', ten: 'Lịch và kế hoạch' },
  { id: 'lich-su', ten: 'Lịch sử và phản hồi' },
  { id: 'diem-danh', ten: 'Tham dự' },
  { id: 'bieu-quyet', ten: 'Biểu quyết' },
  { id: 'bien-ban', ten: 'Biên bản' },
  { id: 'ket-luan', ten: 'Kết luận' },
]

export default function ChiTietDeXuat({ cuocHopId, token, nguoiDung, onQuayLai, onDaTiepNhan, onLapLich, onMoYeuCauDuyet, tabBanDau, chiXem = false }) {
  const phienDongBo = useDongBoDuLieu()
  const [chiTiet, setChiTiet] = useState(null)
  const [loi, setLoi] = useState('')
  const [dangTiepNhan, setDangTiepNhan] = useState(false)
  const [loiTiepNhan, setLoiTiepNhan] = useState('')
  const [thongBaoTiepNhan, setThongBaoTiepNhan] = useState('')
  const [dangBatDau, setDangBatDau] = useState(false)
  const [loiBatDau, setLoiBatDau] = useState('')
  const [coLichXacNhan, setCoLichXacNhan] = useState(false)
  const [dangKetThuc, setDangKetThuc] = useState(false)
  const [loiKetThuc, setLoiKetThuc] = useState('')
  const [tabHienTai, setTabHienTai] = useState(tabBanDau)
  const [luaChonKeHoach, setLuaChonKeHoach] = useState(null)
  const [loiLuaChon, setLoiLuaChon] = useState('')
  const [moFormCapNhatKeHoach, setMoFormCapNhatKeHoach] = useState(false)
  const [yeuCauThuKy, setYeuCauThuKy] = useState(null)
  const [loiHoSo, setLoiHoSo] = useState('')
  const [thongBaoChuyenCap, setThongBaoChuyenCap] = useState('')
  const buocXuLy = chiTiet?.buocXuLyHienTai
  const [lyDoRaSoat, setLyDoRaSoat] = useState('')

  const buocThuKyLanhDao = ['CHO_THU_KY_LANH_DAO', 'THU_KY_LANH_DAO_XU_LY'].includes(buocXuLy)

  useEffect(() => {
    const controller = new AbortController()

    async function taiChiTiet() {
      try {
        const duLieu = await layChiTietDeXuat(cuocHopId, token, controller.signal)
        if (!controller.signal.aborted) {
          setChiTiet(duLieu)
          setLoi('')
        }
      } catch (loiTaiChiTiet) {
        if (controller.signal.aborted) return

        const thongBao = {
          403: 'Bạn không có quyền xem đề xuất này.',
          404: 'Không tìm thấy đề xuất cuộc họp.',
        }

        if ([401, 403, 404].includes(loiTaiChiTiet.status)) setChiTiet(null)
        setLoi(thongBao[loiTaiChiTiet.status] || 'Không tải được chi tiết đề xuất.')
      }
    }

    taiChiTiet()
    return () => controller.abort()
  }, [cuocHopId, token, phienDongBo])

  useEffect(() => {
    if (chiXem || !buocThuKyLanhDao || nguoiDung?.maVaiTro !== 'THU_KY') return

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
  }, [chiXem, buocThuKyLanhDao, cuocHopId, nguoiDung?.maVaiTro, token, phienDongBo])

  async function xuLyHoSoThuKyLanhDao(thaoTac) {
    if (dangTiepNhan || !yeuCauThuKy || !buocThuKyLanhDao) return

    const ghiChu = lyDoRaSoat.trim()
    if (['tra-lai', 'tu-choi-tiep-nhan'].includes(thaoTac) && !ghiChu) {
      setLoiTiepNhan('Nhập lý do để thư ký lập hồ sơ nhận phản hồi.')
      return
    }
    setDangTiepNhan(true)
    setLoiTiepNhan('')
    setThongBaoTiepNhan('')

    try {
      const ketQua = await xuLyYeuCauKeHoach(
        cuocHopId, yeuCauThuKy.yeuCauPheDuyetId, thaoTac, ghiChu || null, token,
      )
      const chiTietMoi = await layChiTietDeXuat(cuocHopId, token)
      setChiTiet(chiTietMoi)
      onDaTiepNhan(cuocHopId, chiTietMoi)
      setThongBaoTiepNhan(ketQua.message)
    } catch (loiXuLy) {
      setLoiTiepNhan(loiXuLy.message)
    } finally {
      setDangTiepNhan(false)
    }
  }

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
      onDaTiepNhan(cuocHopId, thayDoi)
      setThongBaoTiepNhan(ketQua.message || 'Tiếp nhận đề xuất thành công.')
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
      setTabHienTai(!chiXem && chiTiet?.duocToChuc ? 'diem-danh' : 'thong-tin')
      const moi = await layChiTietDeXuat(cuocHopId, token).catch(() => null)
      if (moi) setChiTiet(moi)
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
      setTabHienTai('bien-ban')
      const moi = await layChiTietDeXuat(cuocHopId, token).catch(() => null)
      if (moi) setChiTiet(moi)
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
  const dangMoCapNhatKeHoach = moFormCapNhatKeHoach && laThuKy && dangToChuc
  const hienFormKeHoach = dangLapKeHoach || dangMoCapNhatKeHoach
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
  }, [chiXem, cuocHopId, token, hienFormKeHoach, tabHienTai, coKhachNgoai, phienDongBo])
  const dangDienRa = chiTiet?.trangThai === 'DANG_DIEN_RA'
  const daKetThuc = chiTiet?.trangThai === 'DA_KET_THUC'
  const duocSoanVanBan = daKetThuc && laThuKy
  const coBanGiao = chiTiet?.yeuCauBanGiaoId != null
  const laChuTri = Number(nguoiDung?.nguoiDungId) === Number(chiTiet?.nguoiChuTriId)
  const hienThanhVien = Boolean(
    chiTiet && (chiXem ? laChuTri || nguoiDung?.nguoiDungId === chiTiet.nguoiDeXuatId : laThuKy || coBanGiao)
    && (dangToChuc || dangDienRa || daKetThuc),
  )
  const choTiepNhan = chiTiet?.trangThai === 'CHO_TIEP_NHAN'
  const cacTab = TABS.filter(({ id }) => {
    if (id === 'ke-hoach') return true
    if (id === 'lich-su') {
      return !chiXem || Number(nguoiDung?.nguoiDungId) === Number(chiTiet?.nguoiDeXuatId)
    }
    if (id === 'diem-danh') return (laChuTri || (!chiXem && (laThuKy || coBanGiao))) && (dangDienRa || daKetThuc)
    if (id === 'bieu-quyet') return laChuTri && (dangDienRa || daKetThuc)
    if (id === 'bien-ban' || id === 'ket-luan') return daKetThuc && (laThuKy || coBanGiao || laChuTri)
    return true
  })

  const coTheMoYeuCauDuyet = Boolean(
    onMoYeuCauDuyet && chiTiet?.nguoiDangXuLyId != null
    && Number(chiTiet.nguoiDangXuLyId) === Number(nguoiDung?.nguoiDungId)
    && ['CHO_TRUONG_PHONG', 'CHO_LANH_DAO'].includes(chiTiet.buocXuLyHienTai)
    && layTrangThaiHienThiCuocHop(chiTiet) === 'CHO_DUYET',
  )

  const giaiDoanDieuHanh = dangDienRa ? 'dang-hop' : daKetThuc ? 'ket-thuc' : dangToChuc ? 'chuan-bi' : null
  const tabThaoTac = dangDienRa ? ['thong-tin', 'diem-danh', 'bieu-quyet']
    : daKetThuc ? ['bien-ban', 'ket-luan'] : []
  const thaoTacCuocHop = cacTab.filter(({ id }) => tabThaoTac.includes(id)).map(({ id, ten }) => ({
    ten: id === 'thong-tin' ? 'Xem nội dung họp' : ten,
    onClick: () => setTabHienTai(id),
  }))
  if (dangToChuc && !chiXem && laThuKy && onLapLich) {
    thaoTacCuocHop.push({ ten: 'Mở lịch họp', onClick: () => onLapLich(cuocHopId) })
  }

  const choChuyenCap = chiTiet?.buocXuLyHienTai === 'CHO_GUI_CAP_TREN'
  const coTheChuyenCap = !chiXem && chiTiet?.yeuCauChoGuiCapTrenId != null

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
        <ArrowLeft size={16} aria-hidden="true" /> Quay lại danh sách
      </button>

      {loi && <p role="alert" className="rounded-xl border border-red-200 bg-red-50 p-4 text-sm text-red-800">{loi}</p>}
      {chiTiet === null ? (
        !loi && <p role="status" className="rounded-2xl border border-slate-200 bg-white p-8 text-center text-sm text-slate-600">Đang tải chi tiết cuộc họp…</p>
      ) : (
        <>
          <div className={'grid items-start gap-5' + (hienThanhVien && !['ke-hoach', 'bien-ban', 'ket-luan'].includes(tabHienThi)
            ? ' xl:grid-cols-[minmax(0,1fr)_300px]' : '')}>
            <section className="min-w-0 overflow-hidden rounded-2xl border border-slate-200/80 bg-white shadow-sm shadow-slate-900/[0.02]">
              <div className="border-b border-slate-200 bg-[#fbfdff] p-5 sm:p-7">
                <div className="flex flex-wrap items-start justify-between gap-4">
                  <div className="min-w-0">
                    <p className="flex items-center gap-2 text-sm font-semibold text-blue-800">
                      <CalendarDays size={17} aria-hidden="true" /> Hồ sơ cuộc họp
                    </p>
                    <h1 className="mt-2 break-words text-2xl font-bold tracking-tight text-[#102a43] md:text-3xl">{chiTiet.tieuDe}</h1>
                    <p className="mt-3 inline-block break-all rounded-md border border-slate-200 bg-white px-2.5 py-1 font-mono text-xs text-slate-600">
                      {chiTiet.maCuocHop || `Mã cuộc họp #${cuocHopId}`}
                    </p>
                    {(chiTiet.tenNguoiChuTri || chiTiet.tenThuKyCuocHop) && (
                      <dl className="mt-4 flex flex-wrap gap-x-6 gap-y-2 text-sm">
                        {chiTiet.tenNguoiChuTri && (
                          <div>
                            <dt className="text-xs text-slate-500">Chủ trì</dt>
                            <dd className="font-medium">{chiTiet.tenNguoiChuTri}</dd>
                          </div>
                        )}
                        {chiTiet.tenThuKyCuocHop && (
                          <div>
                            <dt className="text-xs text-slate-500">Thư ký cuộc họp</dt>
                            <dd className="font-medium">{chiTiet.tenThuKyCuocHop}</dd>
                          </div>
                        )}
                      </dl>
                    )}
                  </div>
                  <div className="flex flex-wrap items-center gap-2">
                    <NhanTrangThai trangThai={layTrangThaiHienThiCuocHop(chiTiet)} />
                    {coTheMoYeuCauDuyet && (
                      <button
                        type="button"
                        onClick={() => onMoYeuCauDuyet(cuocHopId)}
                        className="inline-flex items-center gap-2 rounded-lg bg-blue-700 px-3.5 py-2.5 text-sm font-semibold text-white hover:bg-blue-800 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue-700"
                      >
                        Mở yêu cầu duyệt <ArrowRight size={16} aria-hidden="true" />
                      </button>
                    )}
                  </div>
                </div>
              </div>

              {giaiDoanDieuHanh && (
                <div className="px-5 pt-4 sm:px-7">
                  <DieuHanhCuocHop
                    giaiDoan={giaiDoanDieuHanh} chiTiet={chiTiet}
                    coLichXacNhan={chiTiet.coLichDaXacNhan ?? coLichXacNhan}
                    coQuyenDieuHanh={laChuTri || (!chiXem && laThuKy)}
                    dangBatDau={dangBatDau} dangKetThuc={dangKetThuc}
                    loiBatDau={loiBatDau} loiKetThuc={loiKetThuc}
                    onBatDau={xuLyBatDau} onKetThuc={xuLyKetThuc}
                    thaoTac={thaoTacCuocHop}
                  />
                </div>
              )}

              {choChuyenCap && (
                <div className="mx-5 mt-4 flex flex-wrap items-center justify-between gap-3 rounded-xl border border-blue-200 bg-blue-50 px-4 py-3 sm:mx-7">
                  <p className="text-sm font-medium text-blue-950">
                    {coTheChuyenCap ? 'Trưởng phòng đã duyệt cấp 1. Hồ sơ đang chờ bạn chuyển lên cấp trên.'
                      : `Hồ sơ đang chờ ${chiTiet.tenNguoiDangXuLy || 'người đã trình cấp 1'} chuyển cấp.`}
                  </p>
                  {coTheChuyenCap && (
                    <button type="button" className="rounded-lg bg-blue-700 px-3 py-2 text-sm font-semibold text-white hover:bg-blue-800"
                      onClick={() => setTabHienTai('ke-hoach')}>Mở phần trình cấp 2</button>
                  )}
                </div>
              )}

              <div className="px-5 pt-4 sm:px-7">
                <TienDoCuocHop chiTiet={chiTiet} />
              </div>

              {chiTiet.phanHoiKeHoach && (
                <div className="px-5 pt-3"><NoiDungPhanHoi phanHoi={chiTiet.phanHoiKeHoach} /></div>
              )}

              <nav
                role="tablist"
                aria-label="Chi tiết cuộc họp"
                className="flex gap-1 overflow-x-auto border-b border-slate-200 bg-white px-4 pt-3 sm:px-6"
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

                    {choTiepNhan && !chiXem && chiTiet.duocLapKeHoach && (
                      <TiepNhanDeXuatAction onClick={xuLyTiepNhan} disabled={dangTiepNhan} />
                    )}

                    <TaiLieuTongQuanCuocHop key={'de-xuat/' + cuocHopId} cuocHopId={cuocHopId} token={token} loai="de-xuat"
                      choPhepTaiLen={chiTiet.trangThai === 'NHAP' && nguoiDung.nguoiDungId === chiTiet.nguoiDeXuatId} />
                    {chiTiet.daHoanTatHoSo && <p className="text-sm font-semibold text-emerald-800">Đã hoàn tất hồ sơ.</p>}

                    {loiTiepNhan && <p role="alert" className="text-sm text-red-800">{loiTiepNhan}</p>}
                    {thongBaoTiepNhan && <p role="status" className="rounded-lg bg-emerald-50 p-3 text-sm text-emerald-800">{thongBaoTiepNhan}</p>}
                  </>
                )}

                {tabHienThi === 'ke-hoach' && (
                  <div className="space-y-6">
                    {coTheChuyenCap && (
                      <ChuyenKeHoachCapTren
                        key={chiTiet.yeuCauChoGuiCapTrenId}
                        cuocHopId={cuocHopId}
                        yeuCauId={chiTiet.yeuCauChoGuiCapTrenId}
                        token={token}
                        onDaGui={async (ketQua) => {
                          setThongBaoChuyenCap(ketQua?.message || 'Đã chuyển hồ sơ lên cấp trên.')
                          const chiTietMoi = await layChiTietDeXuat(cuocHopId, token)
                          setChiTiet(chiTietMoi)
                          onDaTiepNhan?.(cuocHopId, chiTietMoi)
                        }}
                      />
                    )}

                    {!chiXem && laThuKy && dangToChuc && !moFormCapNhatKeHoach && (
                      <div className="rounded-xl border border-amber-200 bg-amber-50 p-4">
                        <p className="text-sm font-semibold text-amber-950">Cần đổi lịch hoặc nội dung kế hoạch?</p>
                        <ol className="mt-2 list-inside list-decimal space-y-1 text-sm text-amber-900">
                          <li>Nếu đổi lịch, tạo lịch mới ở trạng thái Đề xuất trên Calendar trước.</li>
                          <li>Cập nhật và lưu kế hoạch, chọn lịch đề xuất rồi trình duyệt lại từ đầu.</li>
                        </ol>
                        <p className="mt-2 text-xs text-amber-900">
                          Lịch đã xác nhận hiện tại vẫn được giữ nguyên trong lúc chờ duyệt bản cập nhật.
                        </p>
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
                        Chỉnh sửa bên dưới rồi bấm Lưu kế hoạch. Sau khi lưu, chọn lịch Đề xuất và trình lại; bản đã duyệt trước đó vẫn được lưu trong lịch sử.
                      </p>
                    )}
                    {moFormCapNhatKeHoach && dangLapKeHoach && (
                      <p role="status" className="rounded-lg border border-emerald-200 bg-emerald-50 p-3 text-sm text-emerald-900">
                        Bản cập nhật đã được lưu. Chọn lịch đang Đề xuất, kiểm tra tài liệu rồi trình duyệt lại từ cấp đầu tiên.
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
                    />

                    {hienFormKeHoach ? (
                      <>
                        {loiLuaChon && <p role="alert" className="text-sm text-red-800">{loiLuaChon}</p>}
                        <FormKeHoachThuKy
                          luaChonChuTri={luaChonKeHoach?.nguoiChuTri}
                          luaChonThuKy={luaChonKeHoach?.thuKyCuocHop}
                          chiTiet={chiTiet}
                          token={token}
                          laBanCapNhat={moFormCapNhatKeHoach}
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
                            onDaTiepNhan(cuocHopId, thayDoi)
                          }}
                        />
                        {onLapLich && (dangLapKeHoach || !dangMoCapNhatKeHoach) && (
                          <button
                            type="button"
                            className="rounded-lg border px-3 py-2 text-sm font-semibold"
                            onClick={() => onLapLich(cuocHopId)}
                          >
                            Chọn lịch trên Calendar
                          </button>
                        )}
                        <TaiLieuKeHoachThuKy
                          luaChonKeHoach={luaChonKeHoach}
                          nguoiChuTriId={chiTiet.nguoiChuTriId}
                          cuocHopId={cuocHopId}
                          token={token}
                          loaiHinh={chiTiet.loaiHinh}
                          onLapLich={onLapLich}
                          vaiTroNguoiDeXuat={chiTiet.vaiTroNguoiDeXuat}
                          choPhepThaoTac={dangLapKeHoach}
                          onDaTrinh={(chiTietMoi) => {
                            setChiTiet(chiTietMoi)
                            setMoFormCapNhatKeHoach(false)
                            onDaTiepNhan(cuocHopId, chiTietMoi)
                          }}
                        />
                      </>
                    ) : (
                      <HoSoDaTrinh
                        key={`${cuocHopId}/${buocXuLy}/${chiTiet.yeuCauBanGiaoId ?? ''}`}
                        cuocHopId={cuocHopId}
                        token={token}
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
                    {loiTiepNhan && <p role="alert" className="text-sm text-red-800">{loiTiepNhan}</p>}
                    {thongBaoTiepNhan && <p role="status" className="rounded-lg bg-emerald-50 p-3 text-sm text-emerald-800">{thongBaoTiepNhan}</p>}
                    {thongBaoChuyenCap && <p role="status" className="rounded-lg bg-emerald-50 p-3 text-sm text-emerald-800">{thongBaoChuyenCap}</p>}
                  </div>
                )}

                {tabHienThi === 'lich-su' && (
                  <>
                    {chiTiet.phanHoiKeHoach && <NoiDungPhanHoi phanHoi={chiTiet.phanHoiKeHoach} />}
                    <LichSuKeHoach cuocHopId={cuocHopId} token={token} />
                  </>
                )}

                {tabHienThi === 'diem-danh' && (dangDienRa || daKetThuc) && (
                  <DiemDanhThuKy cuocHopId={cuocHopId} token={token} readOnly={daKetThuc || chiXem || !laThuKy} />
                )}

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

                {!duocSoanVanBan && ['bien-ban', 'ket-luan'].includes(tabHienThi) && (
                  <VanBanCuaHoSo key={tabHienThi} loai={tabHienThi} cuocHopId={cuocHopId} token={token}
                    tenChuTri={chiTiet.tenNguoiChuTri} choPhepXuLy={laChuTri && daKetThuc} onPhatHanh={taiLaiSauPhatHanh} />
                )}

                {duocSoanVanBan && tabHienThi === 'bien-ban' && (
                  <BienBanThuKy
                    key={`bien-ban-${cuocHopId}`}
                    cuocHopId={cuocHopId}
                    tieuDeCuocHop={chiTiet.tieuDe}
                    tenChuTri={chiTiet.tenNguoiChuTri}
                    token={token}
                  />
                )}

                {duocSoanVanBan && tabHienThi === 'ket-luan' && (
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

            {hienThanhVien && (
              <aside className="min-w-0 space-y-4">
                <div className="rounded-2xl border border-slate-200/80 bg-white p-5 shadow-sm shadow-slate-900/[0.02]">
                  <div className="mb-4">
                    <p className="text-xs font-bold uppercase tracking-[0.14em] text-emerald-800">Thành viên</p>
                    <h2 className="mt-1 text-lg font-bold text-slate-900">{!chiXem && laThuKy && dangToChuc ? 'Mời tham dự' : 'Thành viên cuộc họp'}</h2>
                  </div>
                  <ThanhVienCuocHopThuKy
                    cuocHopId={cuocHopId}
                    token={token}
                    choPhepMoi={!chiXem && laThuKy && dangToChuc}
                    onTrangThaiLich={setCoLichXacNhan}
                    onLapLich={onLapLich}
                    lichHopIdKeHoachDaDuyet={chiTiet.lichHopIdKeHoachDaDuyet}
                  />
                </div>
              </aside>
            )}
          </div>
        </>
      )}
    </div>
  )
}



