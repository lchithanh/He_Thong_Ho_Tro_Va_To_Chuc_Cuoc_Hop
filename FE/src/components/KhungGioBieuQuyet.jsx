import { dinhDangNgayGio, soPhutGiuaHaiMoc } from '../utils/thoiGianCuocHop.js'

export default function KhungGioBieuQuyet({ muc }) {
  const soPhut = muc.thoiLuongPhut ?? soPhutGiuaHaiMoc(muc.thoiGianBatDau, muc.thoiGianKetThuc)
  const chuaMo = muc.trangThai === 'NHAP' && muc.thoiLuongPhut != null
  return (
    <dl className="mt-2 flex flex-wrap gap-x-5 gap-y-1 text-sm text-slate-600 phong-toi:text-phong-chu-phu">
      {!chuaMo && <><div><dt className="inline font-medium">Bắt đầu · </dt><dd className="inline">{dinhDangNgayGio(muc.thoiGianBatDau)}</dd></div>
      <div><dt className="inline font-medium">Kết thúc · </dt><dd className="inline">{dinhDangNgayGio(muc.thoiGianKetThuc)}</dd></div></>}
      {soPhut != null && <div><dt className="inline font-medium">Thời lượng · </dt><dd className="inline">{soPhut} phút{chuaMo && ' kể từ khi mở'}</dd></div>}
    </dl>
  )
}
