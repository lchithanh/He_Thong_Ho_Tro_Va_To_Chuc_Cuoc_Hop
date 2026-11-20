import { useEffect, useState } from 'react'
import {
  batDauCuocHop,
  ketThucCuocHop,
  layChiTietDeXuat,
  layKeHoachChoToiDuyet,
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
import KhachNgoaiCuocHop from './KhachNgoaiCuocHop.jsx'
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
import TienDoCuocHop from './TienDoCuocHop.jsx'
import LichSuKeHoach from './LichSuKeHoach.jsx'
import TiepNhanDeXuatAction from './thaoTacKeHoach/TiepNhanDeXuatAction.jsx'
const TABS = [
  { id: 'thong-tin', ten: 'Đề xuất' },
  { id: 'ke-hoach', ten: 'Lịch và kế hoạch' },
  { id: 'lich-su', ten: 'Lịch sử và phản hồi' },
  { id: 'diem-danh', ten: 'Điểm danh' },
  { id: 'bieu-quyet', ten: 'Biểu quyết' },
  { id: 'bien-ban', ten: 'Biên bản' },
  { id: 'ket-luan', ten: 'Kết luận' },
]

export default function ChiTietDeXuat({ cuocHopId, token, nguoiDung, onQuayLai, onDaTiepNhan, onLapLich, tabBanDau, chiXem = false }) {
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
  const [yeuCauDuyet, setYeuCauDuyet] = useState(null)
  const [loiTaiQuyenDuyet, setLoiTaiQuyenDuyet] = useState('')
  const [thongBaoChuyenCap, setThongBaoChuyenCap] = useState('')
  const buocXuLy = chiTiet?.buocXuLyHienTai
  const [lyDoRaSoat, setLyDoRaSoat] = useState('')

  const buocThuKyLanhDao = ['CHO_THU_KY_LANH_DAO', 'THU_KY_LANH_DAO_XU_LY'].includes(buocXuLy)

  useEffect(() => {
    const controller = new AbortController()

    async function taiChiTiet() {
      try {
        const duLieu = await layChiTietDeXuat(cuocHopId, token, controller.signal)
        if (!controller.signal.aborted) setChiTiet(duLieu)
      } catch (loiTaiChiTiet) {
        if (controller.signal.aborted) return

        const thongBao = {
          403: 'Bạn không có quyền xem đề xuất này.',
          404: 'Không tìm thấy đề xuất cuộc họp.',
        }

        setLoi(thongBao[loiTaiChiTiet.status] || 'Không tải được chi tiết đề xuất.')
      }
    }

    taiChiTiet()
    return () => controller.abort()
  }, [cuocHopId, token])

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
  }, [chiXem, buocThuKyLanhDao, cuocHopId, nguoiDung?.maVaiTro, token])

  useEffect(() => {
    if (!['CHO_TRUONG_PHONG', 'CHO_LANH_DAO'].includes(buocXuLy)) return

    const controller = new AbortController()
    const cap = buocXuLy === 'CHO_TRUONG_PHONG' ? 'TRUONG_PHONG' : 'LANH_DAO'
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
  }, [buocXuLy, cuocHopId, token])

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
      onDaTiepNhan?.(cuocHopId, chiTietMoi)
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
      onDaTiepNhan?.(cuocHopId, thayDoi)
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
      setTabHienTai('diem-danh')
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
  const cacTab = TABS.filter(({ id }) => {
    if (id === 'ke-hoach') return true
    if (id === 'lich-su') {
      return !chiXem || Number(nguoiDung?.nguoiDungId) === Number(chiTiet?.nguoiDeXuatId)
    }
    if (id === 'diem-danh') return !chiXem && (laThuKy || coBanGiao) && (dangDienRa || daKetThuc)
    if (id === 'bieu-quyet') return laChuTri && (dangDienRa || daKetThuc)
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

      {loi ? (
        <p role="alert" className="rounded-xl border border-red-200 bg-red-50 p-4 text-sm text-red-800">{loi}</p>
      ) : chiTiet === null ? (
        <p role="status" className="rounded-2xl border border-slate-200 bg-white p-8 text-center text-sm text-slate-600">Đang tải chi tiết cuộc họp…</p>
      ) : (
        <>
          <div className={'grid items-start gap-5' + (hienThanhVien && !['ke-hoach', 'bien-ban', 'ket-luan'].includes(tabHienThi)
            ? ' xl:grid-cols-[minmax(0,1fr)_300px]' : '')}>
            <section className="min-w-0 overflow-hidden rounded-2xl border border-slate-200/80 bg-white shadow-sm shadow-slate-900/[0.02]">
              <div className="border-b border-[#334e68] bg-[#102a43] p-5 text-white sm:p-7">
                <div className="flex flex-wrap items-start justify-between gap-4">
                  <div className="min-w-0">
                    <p className="text-xs font-bold uppercase tracking-[0.12em] text-[#bcccdc]">Hồ sơ cuộc họp</p>
                    <h1 className="mt-2 break-words text-2xl font-bold tracking-tight md:text-3xl">{chiTiet.tieuDe}</h1>
                    <p className="mt-3 inline-block break-all rounded-md border border-[#486581] bg-[#243b53] px-2.5 py-1 font-mono text-xs text-[#d9e2ec]">
                      {chiTiet.maCuocHop || `Mã cuộc họp #${cuocHopId}`}
                    </p>
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
                  <NhanTrangThai trangThai={layTrangThaiHienThiCuocHop(chiTiet)} />
                </div>
              </div>

              <div className="px-5 pt-4 sm:px-7">
                <TienDoCuocHop chiTiet={chiTiet} />
              </div>

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

                    {choTiepNhan && !chiXem && chiTiet.duocLapKeHoach && (
                      <TiepNhanDeXuatAction onClick={xuLyTiepNhan} disabled={dangTiepNhan} />
                    )}

                    <TaiLieuTongQuanCuocHop key={'de-xuat/' + cuocHopId} cuocHopId={cuocHopId} token={token} loai="de-xuat"
                      choPhepTaiLen={chiTiet.trangThai === 'NHAP' && nguoiDung.nguoiDungId === chiTiet.nguoiDeXuatId} />
                    {chiTiet.daHoanTatHoSo && <p className="text-sm font-semibold text-emerald-800">Đã hoàn tất hồ sơ.</p>}

                    {!chiXem && laThuKy && dangToChuc && (
                      <div className="flex flex-wrap items-center gap-3">
                        {onLapLich && <button type="button" onClick={() => onLapLich(cuocHopId)}
                          className="rounded-lg border border-slate-200 px-3 py-2 text-sm font-semibold">Mở Calendar</button>}
                        {coLichXacNhan && <button type="button" disabled={dangBatDau} onClick={xuLyBatDau}
                          className="rounded-lg bg-emerald-800 px-4 py-2 text-sm font-semibold text-white disabled:opacity-60">
                          {dangBatDau ? 'Đang bắt đầu…' : 'Bắt đầu cuộc họp'}
                        </button>}
                        {loiBatDau && <p role="alert" className="text-sm text-red-800">{loiBatDau}</p>}
                      </div>
                    )}

                    {!chiXem && laThuKy && dangDienRa && (
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

                    {loiTiepNhan && <p role="alert" className="text-sm text-red-800">{loiTiepNhan}</p>}
                    {thongBaoTiepNhan && <p role="status" className="rounded-lg bg-emerald-50 p-3 text-sm text-emerald-800">{thongBaoTiepNhan}</p>}
                  </>
                )}

                {tabHienThi === 'ke-hoach' && (
                  <div className="space-y-6">
                    {!chiXem && laThuKy && dangToChuc && !moFormCapNhatKeHoach && (
                      <div className="rounded-xl border border-amber-200 bg-amber-50 p-4">
                        <p className="text-sm font-semibold text-amber-950">Cần đổi lịch hoặc nội dung kế hoạch?</p>
                        <ol className="mt-2 list-inside list-decimal space-y-1 text-sm text-amber-900">
                          <li>Nếu đổi lịch, tạo lịch mới ở trạng thái Đề xuất trên Calendar trước.</li>
                          <li>Lưu kế hoạch. Khi trình lại, chọn giữ lịch đã xác nhận hoặc chọn lịch đề xuất mới.</li>
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
                            onDaTiepNhan?.(cuocHopId, thayDoi)
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
                          lichHopIdKeHoachDaDuyet={chiTiet.lichHopIdKeHoachDaDuyet}
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
                            onDaTiepNhan?.(cuocHopId, chiTietMoi)
                          }}
                        />
                      </>
                    ) : (
                      <HoSoDaTrinh
                        key={`${cuocHopId}/${buocXuLy}/${chiTiet.yeuCauBanGiaoId ?? ''}`}
                        cuocHopId={cuocHopId}
                        token={token}
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
                    {loiTaiQuyenDuyet && ['CHO_TRUONG_PHONG', 'CHO_LANH_DAO'].includes(buocXuLy) && (
                      <p role="alert" className="text-sm text-red-800">{loiTaiQuyenDuyet}</p>
                    )}
                    {loiTiepNhan && <p role="alert" className="text-sm text-red-800">{loiTiepNhan}</p>}
                    {thongBaoTiepNhan && <p role="status" className="rounded-lg bg-emerald-50 p-3 text-sm text-emerald-800">{thongBaoTiepNhan}</p>}
                  </div>
                )}

                {tabHienThi === 'lich-su' && (
                  <>
                    {chiTiet.phanHoiKeHoach && <NoiDungPhanHoi phanHoi={chiTiet.phanHoiKeHoach} />}
                    <LichSuKeHoach cuocHopId={cuocHopId} token={token} />
                  </>
                )}

                {tabHienThi === 'diem-danh' && (dangDienRa || daKetThuc) && (
                  <DiemDanhThuKy cuocHopId={cuocHopId} token={token} readOnly={daKetThuc || !laThuKy} />
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
                {(coKhachNgoai || chiTiet.phamViCuocHop === 'BEN_NGOAI') && (laThuKy || laChuTri) && (
                  <KhachNgoaiCuocHop
                    key={cuocHopId}
                    cuocHopId={cuocHopId}
                    token={token}
                    laThuKy={!chiXem && laThuKy}
                    choPhepMoi={!chiXem && laThuKy && dangToChuc && coLichXacNhan}
                    trucTuyen={chiTiet.loaiHinh === 'TRUC_TUYEN'}
                  />
                )}
              </aside>
            )}
          </div>
        </>
      )}
    </div>
  )
}



