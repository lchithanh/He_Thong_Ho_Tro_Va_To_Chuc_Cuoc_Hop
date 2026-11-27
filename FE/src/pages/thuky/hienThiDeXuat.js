import { layTrangThaiHienThiCuocHop } from '../../api/cuocHopApi.js'

export const TEN_PHAM_VI = {
  NOI_BO: 'Nội bộ',
  LIEN_PHONG_BAN: 'Liên phòng ban',
  TOAN_CO_QUAN: 'Toàn cơ quan',
  BEN_NGOAI: 'Bên ngoài',
}

export const TEN_TRANG_THAI = {
  NHAP: 'Bản nháp',
  CHO_TIEP_NHAN: 'Chờ tiếp nhận',
  DANG_CHUAN_BI: 'Đang chuẩn bị',
  DA_DUYET: 'Đã duyệt',
  CHO_DUYET: 'Chờ duyệt',
  CHO_TRUONG_PHONG: 'Chờ Trưởng phòng duyệt',
  CHO_TRUONG_NHOM: 'Chờ Tổ trưởng duyệt',
  CHO_LANH_DAO: 'Chờ lãnh đạo duyệt',
  CHO_THU_KY_LANH_DAO: 'Chờ tiếp nhận rà soát',
  THU_KY_LANH_DAO_XU_LY: 'Đang rà soát',
  CHO_GUI_CAP_TREN: 'Chờ chuyển cấp',
  TRA_LAI: 'Đã trả lại',
  TU_CHOI: 'Đã từ chối',
  DANG_DIEN_RA: 'Đang diễn ra',
  DA_KET_THUC: 'Đã kết thúc',
  DA_HUY: 'Đã hủy',
}

export function canChinhSuaKeHoach(hoSo) {
  return hoSo.duocLapKeHoach && hoSo.phanHoiKeHoach?.trangThai === 'TRA_LAI'
}

const BUOC_PHE_DUYET = new Set([
  'CHO_TRUONG_PHONG', 'CHO_TRUONG_NHOM', 'CHO_LANH_DAO',
  'CHO_THU_KY_LANH_DAO', 'THU_KY_LANH_DAO_XU_LY',
])

export function dangPheDuyetKeHoach(hoSo) {
  return layTrangThaiHienThiCuocHop(hoSo) === 'CHO_DUYET' && BUOC_PHE_DUYET.has(hoSo.buocXuLyHienTai)
}

export function layNhanTrangThaiHoSo(hoSo) {
  return dangPheDuyetKeHoach(hoSo) ? hoSo.buocXuLyHienTai : layTrangThaiHienThiCuocHop(hoSo)
}

export function khopTrangThaiHoSo(hoSo, trangThai) {
  return !trangThai || layTrangThaiHienThiCuocHop(hoSo) === trangThai || layNhanTrangThaiHoSo(hoSo) === trangThai
}

export function canChuanBiCuocHopDuocGiao(hoSo) {
  return hoSo.yeuCauBanGiaoId != null && (
    (hoSo.duocToChuc && hoSo.buocXuLyHienTai === 'THU_KY_TO_CHUC') || hoSo.duocLapKeHoach
  )
}
