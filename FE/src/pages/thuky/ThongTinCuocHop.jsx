import { layTrangThaiHienThiCuocHop } from '../../api/cuocHopApi.js'
import { TEN_PHAM_VI } from './hienThiDeXuat.js'
import NguonDeXuat from './NguonDeXuat.jsx' 
const TEN_BUOC = {
  LAP_KE_HOACH: 'Thư ký lập kế hoạch',
  CHO_TRUONG_PHONG: 'Chờ trưởng phòng duyệt',
  CHO_GUI_CAP_TREN: 'Thư ký ban đầu chọn lãnh đạo và chuyển hồ sơ',
  CHO_THU_KY_LANH_DAO: 'Chờ thư ký lãnh đạo tiếp nhận lần trình',
  THU_KY_LANH_DAO_XU_LY: 'Thư ký lãnh đạo đang rà soát',
  CHO_LANH_DAO: 'Chờ lãnh đạo duyệt',
  THU_KY_TO_CHUC: 'Thư ký tổ chức cuộc họp',
  SAU_CUOC_HOP: 'Xử lý sau cuộc họp',
}

const THONG_BAO = {
  NHAP: 'Đề xuất đang được lưu dưới dạng bản nháp.',
  CHO_TIEP_NHAN: 'Đề xuất đang chờ thư ký tiếp nhận.',
  DANG_CHUAN_BI: 'Thư ký đang chuẩn bị kế hoạch cuộc họp.',
  CHO_GUI_CAP_TREN: 'Trưởng phòng đã đồng ý. Kế hoạch chưa được duyệt cuối; hồ sơ đang chờ thư ký ban đầu chuyển đến thư ký lãnh đạo.',
  CHO_DUYET: 'Hồ sơ đang trong quy trình phê duyệt.',
  TRA_LAI: 'Kế hoạch đã được trả lại để chỉnh sửa.',
  TU_CHOI: 'Hồ sơ đã bị từ chối.',
  DA_DUYET: 'Kế hoạch đã được duyệt. Xem lịch đi kèm trong mục Lịch họp.',
  DANG_DIEN_RA: 'Cuộc họp đang diễn ra.',
  DA_KET_THUC: 'Cuộc họp đã kết thúc.',
  DA_HUY: 'Cuộc họp đã được hủy.',
}

const HINH_THUC = { TRUC_TIEP: 'Trực tiếp', TRUC_TUYEN: 'Trực tuyến' }

function nguoi(ten, id) {
  return ten || (id != null ? 'Mã người dùng #' + id : 'Chưa có thông tin phân công')
}

export default function ThongTinCuocHop({ chiTiet }) {
  const trangThai = layTrangThaiHienThiCuocHop(chiTiet)
  const buoc = chiTiet.buocXuLyHienTai
  const thongTin = [
    ['Phạm vi', TEN_PHAM_VI[chiTiet.phamViCuocHop] || chiTiet.phamViCuocHop],
    ['Hình thức', HINH_THUC[chiTiet.loaiHinh] || chiTiet.loaiHinh],
    ['Người đề xuất', nguoi(chiTiet.tenNguoiDeXuat, chiTiet.nguoiDeXuatId)],
    ['Ngày tạo', chiTiet.ngayTao ? new Date(chiTiet.ngayTao).toLocaleString('vi-VN') : null],
    ['Chủ trì', nguoi(chiTiet.tenNguoiChuTri || chiTiet.tenChuTri, chiTiet.nguoiChuTriId)],
    ['Thư ký cuộc họp', nguoi(chiTiet.tenThuKyCuocHop, chiTiet.thuKyCuocHopId)],
    ['Phòng ban chủ trì', chiTiet.tenPhongBanChuTri || (chiTiet.phongBanChuTriId != null ? 'Mã phòng ban #' + chiTiet.phongBanChuTriId : null)],
    ['Khách mời bên ngoài', chiTiet.coKhachMoiBenNgoai == null ? null : chiTiet.coKhachMoiBenNgoai ? 'Có' : 'Không'],
  ]

  return (
    <>
      {(THONG_BAO[trangThai] || TEN_BUOC[buoc]) && (
        <div className="rounded-xl border border-slate-200 bg-slate-50 p-4 text-sm">
          {THONG_BAO[trangThai] && <p className="font-semibold text-slate-900">{THONG_BAO[trangThai]}</p>}
          {TEN_BUOC[buoc] && <p className="mt-1 text-slate-600">Bước hiện tại · {TEN_BUOC[buoc]}</p>}
        </div>
      )}
      <section className="space-y-3" aria-label="Thông tin chung">
        <h2 className="font-semibold text-slate-900">Thông tin chung</h2>
        <dl className="grid gap-x-4 gap-y-3 rounded-xl border border-slate-200 p-4 text-sm sm:grid-cols-2">
          {thongTin.map(([nhan, giaTri]) => (
            <div key={nhan} className="min-w-0">
              <dt className="text-slate-500">{nhan}</dt>
              <dd className="mt-0.5 break-words font-medium text-slate-900">{giaTri ?? 'Chưa có thông tin'}</dd>
            </div>
          ))}
        </dl>
      </section>
      <section className="space-y-3" aria-label="Nội dung cuộc họp">
        <h2 className="font-semibold text-slate-900">Nội dung cuộc họp</h2>
        <dl className="grid gap-3 sm:grid-cols-2">
          {[
            ['Mục tiêu', chiTiet.mucTieu],
            ['Nội dung dự kiến', chiTiet.noiDungDuKien],
          ].map(([nhan, giaTri]) => (
            <div key={nhan} className="min-w-0 rounded-xl bg-slate-50 p-4 text-sm">
              <dt className="font-semibold text-slate-700">{nhan}</dt>
              <dd className="mt-1 whitespace-pre-wrap break-words leading-6 text-slate-800">{giaTri || 'Chưa nhập'}</dd>
            </div>
          ))}
        </dl>
      </section>
    </>
  )
}



