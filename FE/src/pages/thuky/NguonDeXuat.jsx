const TEN_VAI_TRO = {
  LANH_DAO: 'Lãnh đạo',
  TRUONG_PHONG: 'Trưởng phòng',
  THU_KY: 'Thư ký',
}

export default function NguonDeXuat({ hoSo }) {
  const vaiTro = TEN_VAI_TRO[hoSo.vaiTroNguoiDeXuat]
  const nguoiDeXuat = [vaiTro, hoSo.tenNguoiDeXuat].filter(Boolean).join(' · ')

  return (
    <div className="space-y-1 text-sm">
      <p>
        <span className="font-semibold">Đề xuất bởi </span>
        {nguoiDeXuat || 'Chưa có thông tin'}
      </p>

      {hoSo.tenNguoiTrinh && (
        <p className="text-slate-600">
          <span className="font-semibold">Trình kế hoạch bởi </span>
          {hoSo.tenNguoiTrinh}
        </p>
      )}
    </div>
  )
}